package enigma.engine.baseconversion;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import enigma.engine.DrawableCharBuffer;
import enigma.engine.DrawableString;
import enigma.engine.Entity;
import enigma.engine.TextureLookup;
import enigma.engine.Timer;

public class BinaryAdder extends Entity {
	private DrawableCharBuffer additionDS;
	private DrawableCharBuffer numberDS;
	private DrawableCharBuffer spacingProvider;
	private DrawableCharBuffer bottomAnswerDS;
	// private DrawableString answerDS;
	private DrawableString remainderDS;
	private DrawableString multSymbolDS;
	private DrawableCharBuffer userTypedDCB;

	private String solution = "";

	private ArrayList<DrawableCharBuffer> numberCarries = new ArrayList<DrawableCharBuffer>();

	private boolean colorCode = true;

	private char addSymbol = '+';

	private int topNumberIdx = 0;
	private int resultIdx = 0;

	private float additionalScaleFactor = 1f;

	private DrawableString cursorDS;
	private boolean drawCursor = true;

	private Timer timer;
	private String cursorTimerKey = "C";
	private long cursorDelay = 400;

	private int number;
	private int multiplicon;
	private float x;
	private float y;
	private boolean allowIO = true;

	private State state;
	boolean userInputActive = true;

	// processing numerator fields
	private String numberStr;
	private int positionIdx;

	// compose the division bar
	private Vector2 btmPointRight = new Vector2();
	private Vector2 bottomLeftPoint = new Vector2();

	private ArrayList<ArrayList<DrawableString>> carries;

	private ArrayList<DrawableString> aboveAdditionNumbers;
	private ArrayList<DrawableString> columnResults;
	private ArrayList<DrawableString> subNumbersExtensions;
	private ArrayList<Float> subOffsets;
	private ArrayList<Vector2> horrizontal1Points;
	private ArrayList<Vector2> horrizontal2Points;

	// LOGIC
	private float extraFactor = 1.2f;
	private DrawableString sizeSourceDS;
	private float spaceOffset;

	private float scaleX;
	private float scaleY;

	public BinaryAdder(int number, int addition, float x, float y, boolean start) {
		this(Integer.toBinaryString(number), Integer.toBinaryString(addition), x, y, start);
	}

	public BinaryAdder(String number, String addition, float x, float y, boolean start) {
		this.additionDS = new DrawableCharBuffer(filterZeros("" + addition));
		this.numberDS = new DrawableCharBuffer("" + number);
		// this.spacingProvider = new DrawableCharBuffer("" + number);
		this.spacingProvider = new DrawableCharBuffer("");
		this.bottomAnswerDS = new DrawableCharBuffer("");

		this.number = Integer.parseInt(number, 2);
		this.multiplicon = Integer.parseInt(addition, 2);

		this.multSymbolDS = new DrawableString("" + addSymbol);
		this.multSymbolDS.setRightAlign();

		this.numberStr = this.numberDS.getText();

		this.cursorDS = new DrawableString("|");
		this.sizeSourceDS = new DrawableString("3");

		this.x = x;
		this.y = y;
		this.state = State.START;

		aboveAdditionNumbers = new ArrayList<DrawableString>();
		columnResults = new ArrayList<DrawableString>();

		horrizontal1Points = new ArrayList<Vector2>();
		horrizontal2Points = new ArrayList<Vector2>();

		this.timer = new Timer();
		timer.setTimer(cursorTimerKey, cursorDelay);

		userTypedDCB = new DrawableCharBuffer("");
		numberCarries.add(new DrawableCharBuffer(""));

		topNumberIdx = numberDS.size() - 1;
		resultIdx = additionDS.size() - 1;

		carries = new ArrayList<ArrayList<DrawableString>>();
		for (int i = 0; i < numberDS.length(); ++i) {
			addNewCarryColumn();
		}

		// ELEMENT TO DELETE
		// this.answerDS = new DrawableString("");
		// this.answerDS.setRightAlign();
		// this.remainderDS = new DrawableString("");
		// subLastAnswer = new ArrayList<Integer>();
		// subAnswerLength = new ArrayList<Integer>();
		// subOffsets = new ArrayList<Float>();
		// subNumbersExtensions = new ArrayList<DrawableString>();
		// this.remainderDS.setScale(additionalScaleFactor, additionalScaleFactor);
		// this.remainderDS.makeRed();
		// STOP DELETING

		positionElements();

		calculateSolution();

		if (start) {
			nextStep();
		}
	}

	private void calculateSolution() {
		try {
			int result = number + multiplicon;
			solution = Integer.toBinaryString(result);
		} catch (Exception e) {
			// Gdx.app.log(tag, message);
		}

	}

	private void addNewCarryColumn() {
		carries.add(new ArrayList<DrawableString>());
	}

	private String filterZeros(String number) {
		String[] split = number.split("\\.");
		if (split.length == 1) {
			return number;
		} else if (split.length == 2) {
			int decimal = Integer.parseInt(split[1]);
			if (decimal == 0) {
				return split[0];
			} else {
				return number;
			}
		} else {
			return number;
		}
	}

	private void positionElements() {
		reapplyScale();
		calculateSpaceOffsets();
		numberDS.setXY(x, y);
		// spacingProvider.setLeftAlign();
		spacingProvider.setRightAlign();
		spacingProvider.setXY(x + numberDS.width() / 2, y);

		bottomAnswerDS.setRightAlign();

		float halfNumerWidth = numberDS.width() / 2;
		float toleranceHeight = getHeightTolerenace();

		additionDS.setRightAlign();
		additionDS.setXY(x + halfNumerWidth, y - (numberDS.height() + toleranceHeight));

		float symbolOffset = 4 * spaceOffset;
		multSymbolDS.setXY(additionDS.getX() - additionDS.width() - symbolOffset, additionDS.getY());

		btmPointRight.x = additionDS.getX();
		btmPointRight.y = additionDS.getY() - 0.5f * additionDS.height() - toleranceHeight;

		float bottomWidth = additionDS.width() + multSymbolDS.width() + symbolOffset;
		bottomLeftPoint.x = btmPointRight.x - Math.max(numberDS.width(), bottomWidth);
		bottomLeftPoint.y = btmPointRight.y;

		// answerDS.setXY(x - numberDS.width() / 2, y - 2*numberDS.height() - 2 *
		// toleranceHeight);
		float firstRowY = y - 2 * numberDS.height() - 2 * toleranceHeight;
		float rowX = additionDS.getX() - spaceOffset;
		for (int i = 0; i < numberCarries.size(); ++i) {
			DrawableCharBuffer answer = numberCarries.get(i);

			answer.setXY(rowX, firstRowY);
		}

		positionAnswer();
		calculateSpaceOffsets();
		positionUserTyped();
		positionSubtractionResults();
		positionHorrizontalBars();
		// positionNumberExtensions();
		positionCarries();
	}

	private void positionCarries() {
		while (spacingProvider.size() < carries.size()) {
			spacingProvider.preappend("0"); // this is a very costly operation.
		}

		for (int index = 0; index < carries.size(); ++index) {
			int spacingSize = spacingProvider.size();
			if (spacingSize == 0) return;

			DrawableString spacer = spacingProvider.getCharObjectAt((spacingSize - 1) - index);
			float x = spacer.getX();
			float y = spacer.getY();

			ArrayList<DrawableString> carryColumn = carries.get(index);
			for (int row = 0; row < carryColumn.size(); ++row) {
				DrawableString ds = carryColumn.get(row);
				ds.setXY(x, y + (row + 1) * (ds.height() + spaceOffset));
			}
		}
	}

	private void positionAnswer() {
		float x = additionDS.getX();
		float y = additionDS.getY();

		bottomAnswerDS.setXY(x, y - (additionDS.height() + 2 * spaceOffset));
	}

	private void reapplyScale() {
		userTypedDCB.setScale(scaleX, scaleY);

	}

	private void positionCursor() {
		switch (state) {
		case ADD_ELEMENT_BOTTOM: {
			// DrawableCharBuffer answerDS = numberCarries.get(numberCarries.size() - 1);

			float x = userTypedDCB.getX();
			float y = userTypedDCB.getY();
			// float ansWidth = userTypedDS.width();
			// float userWidth = userTypedDS.width();
			// cursorDS.setXY(x + extraFactor * (ansWidth + userWidth), y);
			cursorDS.setXY(x + spaceOffset, y);
			break;
		}
		default:
			break;
		}
	}

	private void positionUserTyped() {
		switch (state) {
		case ADD_ELEMENT_BOTTOM: {
			// DrawableCharBuffer answerDS = numberCarries.get(numberCarries.size() - 1);
			DrawableString spotDS = null;
			float x;
			float y;
			if (positionIdx < numberDS.length()) {
				spotDS = numberDS.getCharObjectAt((numberDS.length() - 1) - positionIdx);
				x = spotDS.getX();
				y = spotDS.getY();
			} else if (positionIdx < carries.size()) {
				// there must be an extra carry if this branch is hit, it must be 1; not null.
				spotDS = carries.get(positionIdx).get(0);
				x = spotDS.getX();
				y = numberDS.getCharObjectAt(0).getY();

			} else {
				return;
			}
			float width = spotDS.width();
			userTypedDCB.setRightAlign();
			// userTypedDS.setXY(x + (extraFactor * width), y);
			userTypedDCB.setXY(x + width + spaceOffset, y - 4 * (spotDS.height() + 2 * spaceOffset));
			break;
		}
		default:
			break;

		}
		positionCursor();
	}

	public static final float VERTICAL_SPACING_FACTOR = 1.2f;

	private void positionSubtractionResults() {
		for (int i = 0; i < aboveAdditionNumbers.size(); ++i) {
			DrawableString sub = aboveAdditionNumbers.get(i);
			DrawableString result = columnResults.get(i);
			float xPosition = subOffsets.get(i);

			float numPosY = numberDS.getY();
			float numHght = numberDS.height() * VERTICAL_SPACING_FACTOR;

			float subY = numPosY - (2 * i + 1) * numHght;
			float retY = numPosY - (2 * i + 2) * numHght;
			sub.setRightAlign();
			sub.setXY(xPosition, subY);

			result.setRightAlign();
			result.setXY(xPosition, retY);

		}

	}

	private void positionNumberExtensions() {
		// for (int i = 0; i < subNumbersExtensions.size(); ++i) {
		// DrawableString adjacentResult = columnResults.get(i);
		// DrawableString extension = subNumbersExtensions.get(i);
		//
		// float x = adjacentResult.getX();
		// float y = adjacentResult.getY();
		// extension.setLeftAlign();
		// extension.setXY(x, y);
		// }
	}

	private float getHeightTolerenace() {
		return 0.2f * sizeSourceDS.height();
	}

	@Override
	public void draw(SpriteBatch batch) {
		// Sprite and String drawings.
		numberDS.draw(batch);
		additionDS.draw(batch);
		// answerDS.draw(batch);
		multSymbolDS.draw(batch);
		// if (drawRemainder) {
		// remainderDS.draw(batch);
		// }
		// drawSubtractionElements(batch);
		// drawNumberExtensions(batch);
		userTypedDCB.draw(batch);
		bottomAnswerDS.draw(batch);

		for (int i = 0; i < carries.size(); ++i) {
			ArrayList<DrawableString> column = carries.get(i);
			for (int j = 0; j < column.size(); ++j) {
				column.get(j).draw(batch);
			}
		}

		if (shouldDrawCursor()) {
			cursorDS.draw(batch);
		}

		batch.end();
		// Shape Drawing
		ShapeRenderer sr = TextureLookup.shapeRenderer;
		Color originalColor = sr.getColor();
		float r = originalColor.r;
		float g = originalColor.g;
		float b = originalColor.b;
		float a = originalColor.a;
		sr.setColor(TextureLookup.foregroundColor);

		sr.begin(ShapeType.Line);
		// draw the lines that make up the divisor bar.
		sr.line(btmPointRight, bottomLeftPoint);

		for (int i = 0; i < horrizontal1Points.size(); ++i) {
			Vector2 pnt1 = horrizontal1Points.get(i);
			Vector2 pnt2 = horrizontal2Points.get(i);
			sr.line(pnt1, pnt2);
		}

		sr.end();
		sr.setColor(r, g, b, a);

		batch.begin(); // restore the sprite batch rendering.
	}

	@Override
	public void logic() {
		for (int i = 0; i < carries.size(); ++i) {
			ArrayList<DrawableString> column = carries.get(i);
			for (int j = 0; j < column.size(); ++j) {
				column.get(j).logic();
			}
		}
		bottomAnswerDS.logic();
	}

	@Override
	public void dispose() {

	}

	public void IO() {
		if (allowIO) {

			pollTypedNumbers();
			if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) || Gdx.input.isKeyJustPressed(Input.Keys.DEL)
					|| Gdx.input.isKeyJustPressed(Input.Keys.D)) {
				clearUserTyped();
				positionCursor();
			}

			if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
				nextStep();
			}
		}
	}

	private void pollTypedNumbers() {

		if (!allowNumberTyping()) return;

		boolean capturedNumber = false;
		int number = -1;

		for (int i = 0; i < 10 && !capturedNumber; ++i) {
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0 + i)) {
				capturedNumber = true;
				number = i;
			}
		}
		for (int i = 0; i < 10 && !capturedNumber; ++i) {
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0 + i)) {
				capturedNumber = true;
				number = i;
			}
		}

		if (capturedNumber) {
			// just overwrite what ever they typed last time.
			addNumberToUserTyped(number);
			positionUserTyped();
			positionCursor();
		}
	}

	private void addNumberToUserTyped(int number) {
		switch (state) {
		case ADD_ELEMENT_BOTTOM:
			// only allow a single number to be typed.
			userTypedDCB.append("" + number);
			break;
		default:
			break;
		}
	}

	private boolean allowNumberTyping() {
		switch (state) {
		case ADD_ELEMENT_BOTTOM:
			return positionIdx < numberStr.length();
		default:
			break;
		}
		return false;
	}

	private void nextStep() {

		switch (state) {
		case START:
			handleStart();
			break;
		case ADD_ELEMENT_BOTTOM:
			handleTypeBottom();
			break;
		default:
			break;
		}
	}

	private void handleStart() {
		if (colorCode) {
			numberDS.setRed(topNumberIdx);
			additionDS.setBlue(resultIdx);
		}

		transitionTo(State.ADD_ELEMENT_BOTTOM, -1);
	}

	private void handleTypeBottom() {
		try {
			// check if user is done.
			// if (positionIdx >= solution.length()) {
			// transitionTo(State.DONE, -1);
			// drawCursor = false;
			// return;
			// }

			int top = 0;
			int bottom = 0;
			if (positionIdx < numberDS.length()) {
				top = Integer.parseInt("" + numberDS.getCharAt((numberDS.length() - 1) - positionIdx));
			}
			if (positionIdx < additionDS.length()) {
				bottom = Integer.parseInt("" + additionDS.getCharAt((additionDS.length() - 1) - positionIdx));
			}

			int sum = top + bottom;
			ArrayList<DrawableString> caryColumn = carries.get(positionIdx);
			for (int i = 0; i < caryColumn.size(); ++i) {
				sum += Integer.parseInt(caryColumn.get(i).getText());
			}

			boolean proceedToNextStep = false;
			String userTextAnswer = userTypedDCB.getText();
			int userAnswer = -1;
			if (userTextAnswer != "") {
				userAnswer = Integer.parseInt(userTypedDCB.getText(), 2);
			} else {
				// user didn't type an answer, they must want to see solution.
				// proceedToNextStep = true;
				userTypedDCB.setText(Integer.toBinaryString(sum));
				return;
			}

			if (userAnswer == sum) {
				proceedToNextStep = true;
			} else {
				// user typed wrong answer
			}

			if (proceedToNextStep) {
				if (userAnswer == sum) {
					// show green check only if user actually typed something.
					// TODO display a green check mark.
				}

				// interpolate to correct positions
				for (int i = 0; i < userTypedDCB.size(); ++i) {
					DrawableString charObjectAt = userTypedDCB.getCharObjectAt((userTypedDCB.size() - 1) - i);
					if (i == 0) {
						// least order digit goes to answer
						addAndInterpolateAnswer(charObjectAt);
					} else {
						// move this to carry if a non-zero
						String text = charObjectAt.getText();
						if (text.equals("1")) {
							addAndInterpolateCarry(charObjectAt, i + positionIdx);
						}
					}
				}
				userTypedDCB.setText("");
				if (positionIdx < numberDS.length()) {
					numberDS.setNormalColor(-positionIdx + (numberDS.length() - 1));
				}
				if (positionIdx < additionDS.length()) {
					additionDS.setNormalColor(-positionIdx + (additionDS.length() - 1));
				}

				positionIdx++;
				if (colorCode) {
					if (positionIdx < numberDS.length()) {
						numberDS.setRed(-positionIdx + (numberDS.length() - 1));
					}
					if (positionIdx < additionDS.length()) {
						additionDS.setBlue(-positionIdx + (additionDS.length() - 1));
					}
				}
				positionUserTyped();
				if (positionIdx >= solution.length()) {
					transitionTo(State.DONE, -1);
					return;
				}
			}
		} catch (Exception e) {
			// prevent crashes when user types non-integers.
			userTypedDCB.setText("");
		}
	}

	private void addAndInterpolateAnswer(DrawableString ds) {
		float oriX = ds.getX();
		float oriY = ds.getY();
		bottomAnswerDS.preappend(ds);

		float interpX = ds.getX();
		float interpY = ds.getY();

		ds.setXY(oriX, oriY);
		ds.interpolateTo(interpX, interpY);
	}

	private void addAndInterpolateCarry(DrawableString ds, int index) {
		while (index >= carries.size()) {
			addNewCarryColumn();
		}
		while (index >= spacingProvider.length()) {
			spacingProvider.preappend("0"); // this is a very costly operation.
		}
		DrawableString spacer = spacingProvider.getCharObjectAt((spacingProvider.size() - 1) - index);
		float x = spacer.getX();
		float y = spacer.getY();

		ArrayList<DrawableString> carryColumn = carries.get(index);
		carryColumn.add(ds);
		ds.interpolateTo(x, y + carryColumn.size() * (ds.height() + spaceOffset));
	}

	// private void updatePositionDigit() {
	// char newDigit = numberStr.charAt(positionIdx);
	// miniNumerator.append(newDigit);
	// if (subNumbersExtensions.size() > 0)
	// appendTextToDS(newDigit + "",
	// subNumbersExtensions.get(subNumbersExtensions.size() - 1));
	// lastPositionIdx = positionIdx;
	// }

	private void transitionTo(State newState, int passedValueIfNecessary) {
		state = newState;
		switch (state) {
		case ADD_ELEMENT_BOTTOM:
			preparePickTopNum();
			break;
		default:
			break;
		}
	}

	private void preparePickTopNum() {
		// miniNumerator.setLength(0);
		// miniNumerator.append(valueToSubtractFrom.toString());
		//
		// String lastResult = columnResults.get(columnResults.size() - 1).getText();
		// DrawableString extension = new
		// DrawableString(miniNumerator.toString().substring(lastResult.length()));
		// subNumbersExtensions.add(extension);
		//
		// // correctly calculate the extension value
		// if (lastPositionIdx != positionIdx && positionIdx < numberStr.length())
		// updatePositionDigit();
		//
		// // check if division is done, configure remainder.
		// if(positionIdx >= numberStr.length()) {
		// drawCursor = false;
		// remainder = Integer.parseInt(columnResults.get(columnResults.size() -
		// 1).getText());
		// remainderDS.setText("R:" + remainder);
		// if(makeLastResultColored) {
		// columnResults.get(columnResults.size() - 1).makeRed();
		// }
		// }

		clearUserTyped();
		positionCursor();
		positionUserTyped();
		// positionNumberExtensions();
	}

	private void positionHorrizontalBars() {
		for (int i = 0; i < horrizontal1Points.size(); ++i) {
			DrawableString subtractSpot = aboveAdditionNumbers.get(i);
			float x = subtractSpot.getX();
			float y = subtractSpot.getY() - (extraFactor * subtractSpot.height()) * 0.5f;
			horrizontal1Points.get(i).set(x, y);
			horrizontal2Points.get(i).set(x - subtractSpot.width(), y);
		}
	}

	protected String getUserText() {
		return userTypedDCB.getText();
	}

	private void clearUserTyped() {
		userTypedDCB.setText("");
		positionCursor();
	}

	private boolean shouldDrawCursor() {
		if (state == State.START || state == State.DONE) return false;

		if (timer.timerUp(cursorTimerKey)) {
			drawCursor = !drawCursor;
			timer.setTimer(cursorTimerKey, cursorDelay);
		}

		return drawCursor && userInputActive;
	}

	public void scale(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.additionDS.setScale(scaleX, scaleY);
		this.numberDS.setScale(scaleX, scaleY);
		this.spacingProvider.setScale(scaleX, scaleY);
		// this.answerDS.setScale(scaleX, scaleY);
		this.multSymbolDS.setScale(scaleX, scaleY);
		this.cursorDS.setScale(scaleX, scaleY);
		this.userTypedDCB.setScale(scaleX, scaleY);
		// this.remainderDS.setScale(scaleX * additionalScaleFactor, scaleY *
		// additionalScaleFactor);
		this.sizeSourceDS.setScale(scaleX, scaleY);
		this.bottomAnswerDS.setScale(scaleX, scaleY);
		this.userTypedDCB.setScale(scaleX, scaleY);
		for (int i = 0; i < carries.size(); ++i) {
			ArrayList<DrawableString> column = carries.get(i);
			for (int j = 0; j < column.size(); ++j) {
				column.get(j).setScale(scaleX, scaleY);
			}
		}

		calculateSpaceOffsets();
		for (int i = 0; i < aboveAdditionNumbers.size(); ++i) {
			aboveAdditionNumbers.get(i).setScale(scaleX, scaleY);
			columnResults.get(i).setScale(scaleX, scaleY);
		}
		// for (int i = 0; i < subNumbersExtensions.size(); ++i) {
		// subNumbersExtensions.get(i).setScale(scaleX, scaleY);
		// }
		positionElements();
		positionCursor();
		positionUserTyped();
		positionSubtractionResults();
		positionNumberExtensions();
	}

	private void calculateSpaceOffsets() {
		spaceOffset = sizeSourceDS.width() * 0.2f;
	}

	public enum State {
		START, ADD_ELEMENT_BOTTOM, DONE
	}

	public boolean isDone() {
		return state == State.DONE;
	}

	public DrawableCharBuffer getAnswerObject() {
		return bottomAnswerDS;
	}
	public String getAnswerString() {
		return bottomAnswerDS.getText();
	}
}
