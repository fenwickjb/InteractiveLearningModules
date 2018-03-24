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

@SuppressWarnings("unused")
public class MultiplicationEntity extends Entity {
	private DrawableCharBuffer multipliconDS;
	private DrawableCharBuffer numberDS;
	//private DrawableString answerDS;
	private DrawableString remainderDS;
	private DrawableString multSymbolDS;
	
	private ArrayList<DrawableCharBuffer> answerRows = new ArrayList<DrawableCharBuffer>();
	private boolean colorCode = true;
	
	private char multSymbol = 'x';
	//private char multSymbol = '*';
	
	private int topNumberIdx = 0;
	private int multiconIdx = 0;

	private float additionalScaleFactor = 1f;

	private DrawableString cursorDS;
	private boolean drawCursor = true;

	private Timer timer;
	private String cursorTimerKey = "C";
	private long cursorDelay = 400;

	private float x;
	private float y;
	private boolean allowIO = true;

	private State state;
	boolean userInputActive = true;

	// processing numerator fields
	private StringBuilder miniNumerator = new StringBuilder();
	private String numberStr;
	private int positionIdx;

	// compose the division bar
	private Vector2 btmPointRight = new Vector2();
	private Vector2 bottomLeftPoint = new Vector2();

	private ArrayList<DrawableString> aboveAdditionNumbers;
	private ArrayList<DrawableString> columnResults;
	private ArrayList<DrawableString> subNumbersExtensions;
	private ArrayList<Integer> subLastAnswer;
	private ArrayList<Float> subOffsets;
	private ArrayList<Vector2> horrizontal1Points;
	private ArrayList<Vector2> horrizontal2Points;

	// LOGIC
	private int lastPositionIdx = -1;

	private DrawableString userTypedDS;
	private float extraFactor = 1.2f;
	private DrawableString sizeSourceDS;
	private float spaceOffset;

	private StringBuilder valueToSubtractFrom = new StringBuilder();
	
	private boolean drawRemainder = true;
	
	

	public MultiplicationEntity(float number, float multiplicon, float x, float y, boolean start) {
		this.multipliconDS = new DrawableCharBuffer(filterZeros("" + multiplicon));
		this.numberDS = new DrawableCharBuffer("" + number);
		
		this.multSymbolDS = new DrawableString("" + multSymbol);
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

		userTypedDS = new DrawableString("");
		answerRows.add(new DrawableCharBuffer(""));
		
		topNumberIdx = numberDS.size() - 1;
		multiconIdx = multipliconDS.size() - 1;
		
		
		//ELEMENT TO DELETE
		//this.answerDS = new DrawableString("");
		//this.answerDS.setRightAlign();
		this.remainderDS = new DrawableString("");
		subLastAnswer = new ArrayList<Integer>();
		//subAnswerLength = new ArrayList<Integer>();
		subOffsets = new ArrayList<Float>();
		subNumbersExtensions = new ArrayList<DrawableString>();
		this.remainderDS.setScale(additionalScaleFactor, additionalScaleFactor);
		this.remainderDS.makeRed();
		// STOP DELETING
		
		positionElements();
		
		if(start) {
			nextStep();
		}
	}

	private String filterZeros(String number) {
		String[] split = number.split("\\.");
		if(split.length == 1) {
			return number;
		} else if (split.length == 2) {
			int decimal = Integer.parseInt(split[1]);
			if(decimal == 0) {
				return split[0];
			} else {
				return number;
			}
		} else
		{
			return number;
		}
	}

	private void positionElements() {
		calculateSpaceOffsets();
		numberDS.setXY(x, y);

		float halfNumerWidth = numberDS.width() / 2;
		float toleranceHeight = getHeightTolerenace();

		multipliconDS.setRightAlign();
		multipliconDS.setXY(x + halfNumerWidth  , y - (numberDS.height() + toleranceHeight));
		
		float symbolOffset = 4 * spaceOffset;
		multSymbolDS.setXY(multipliconDS.getX() - multipliconDS.width() -  symbolOffset, multipliconDS.getY());

		btmPointRight.x = multipliconDS.getX();
		btmPointRight.y = multipliconDS.getY() - 0.5f * multipliconDS.height() - toleranceHeight;

		float bottomWidth = multipliconDS.width() + multSymbolDS.width() + symbolOffset;
		bottomLeftPoint.x = btmPointRight.x - Math.max(numberDS.width(), bottomWidth);
		bottomLeftPoint.y = btmPointRight.y;

		//answerDS.setXY(x - numberDS.width() / 2, y - 2*numberDS.height() - 2 * toleranceHeight);
		float firstRowY = y - 2*numberDS.height() - 2 * toleranceHeight;
		float rowX = multipliconDS.getX() - spaceOffset;
		for(int i = 0; i < answerRows.size(); ++i) {
			DrawableCharBuffer answer = answerRows.get(i); 
			
			answer.setXY(rowX, firstRowY);
		}

		calculateSpaceOffsets();
		positionCursor();
		positionUserTyped();
		positionSubtractionResults();
		positionHorrizontalBars();
		positionNumberExtensions();
	}

	private void positionCursor() {
		switch (state) {
		case MULT_ELEMENT: {
			DrawableCharBuffer answerDS = answerRows.get(answerRows.size() - 1);
			
			float x = answerDS.getX();
			float y = answerDS.getY();
			//float ansWidth = answerDS.width();
			//float userWidth = userTypedDS.width();
			//cursorDS.setXY(x + extraFactor * (ansWidth + userWidth), y);
			cursorDS.setXY(x + spaceOffset, y);
			break;
		}
		case WRITE_CARRY: {
			DrawableString lastSubNumber = aboveAdditionNumbers.get(aboveAdditionNumbers.size() - 1);
			float x = lastSubNumber.getX();
			float y = lastSubNumber.getY();
			cursorDS.setXY(x, y);
			break;
		}
		case SUM_ROWS: {
			DrawableString lastResultNumber = columnResults.get(columnResults.size() - 1);
			float x = lastResultNumber.getX();
			float y = lastResultNumber.getY();
			cursorDS.setXY(x, y);
			break;
		}
		default:
			break;
		}
	}

	private void positionUserTyped() {
		switch (state) {
		case MULT_ELEMENT: {
			DrawableCharBuffer answerDS = answerRows.get(answerRows.size() - 1);
			float x = answerDS.getX();
			float y = answerDS.getY();
			float width = answerDS.width();
			userTypedDS.setRightAlign();
			//userTypedDS.setXY(x + (extraFactor * width), y);
			userTypedDS.setXY(x + width + spaceOffset, y);
			break;
		}
		case WRITE_CARRY:
		case SUM_ROWS: {
			//DrawableCharBuffer answerDS = answerRows.get(answerRows.size() - 1);
			//float width = answerDS.width();
			float x = cursorDS.getX();
			float y = cursorDS.getY();

			userTypedDS.setRightAlign();
			userTypedDS.setXY(x, y);
			break;
		}
		default:
			break;

		}
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
		for (int i = 0; i < subNumbersExtensions.size(); ++i) {
			DrawableString adjacentResult = columnResults.get(i);
			DrawableString extension = subNumbersExtensions.get(i);

			float x = adjacentResult.getX();
			float y = adjacentResult.getY();
			extension.setLeftAlign();
			extension.setXY(x, y);
		}
	}
	

	private float getHeightTolerenace() {
		return 0.2f * sizeSourceDS.height();
	}

//	private float getWidthTolerance() {
//		return 0.5f * (0.5f * multipliconDS.width() + 0.5f * numberDS.width());
//	}

	@Override
	public void draw(SpriteBatch batch) {
		// Sprite and String drawings.
		numberDS.draw(batch);
		multipliconDS.draw(batch);
		//answerDS.draw(batch);
		multSymbolDS.draw(batch);
		if(drawRemainder) {
			remainderDS.draw(batch);
		}
		drawSubtractionElements(batch);
		drawNumberExtensions(batch);
		userTypedDS.draw(batch);

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

	private void drawSubtractionElements(SpriteBatch batch) {
		for (int i = 0; i < aboveAdditionNumbers.size(); ++i) {
			aboveAdditionNumbers.get(i).draw(batch);
			columnResults.get(i).draw(batch);
		}
	}

	private void drawNumberExtensions(SpriteBatch batch) {
		for (int i = 0; i < subNumbersExtensions.size(); ++i) {
			subNumbersExtensions.get(i).draw(batch);
		}
	}

	@Override
	public void logic() {

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
		case MULT_ELEMENT:
			// only allow a single number to be typed.
			userTypedDS.setText("" + number);
			break;
		case WRITE_CARRY:
			userTypedDS.append("" + number);
			break;
		case SUM_ROWS:
			userTypedDS.append("" + number);
			break;
		default:
			break;
		}
	}

	private boolean allowNumberTyping() {
		switch (state) {
		case MULT_ELEMENT:
			return positionIdx < numberStr.length();
		case WRITE_CARRY:
			return true;
		case SUM_ROWS:
			return true;
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
		case MULT_ELEMENT:
			handlePickTopNum();
			break;
		case WRITE_CARRY:
			handlePickSubNum();
			break;
		case SUM_ROWS:
			handleWriteSubResult();
			break;
		default:
			break;
		}
	}

	private void handleStart() {
		if(colorCode){
			numberDS.setRed(topNumberIdx);
			multipliconDS.setBlue(multiconIdx);
		}
		
		transitionTo(State.MULT_ELEMENT, -1);
	}

	private void handlePickTopNum() {
		// check if this is the first step.
		if (numberStr == null) {
			numberStr = "" + numberDS.getText();
			positionIdx = 0;
			lastPositionIdx = -1;
		}

		// check if user is done.
		if (positionIdx >= numberStr.length()) {
			return;
		}

		if (positionIdx != lastPositionIdx) {
			updatePositionDigit();
		}

		//int stepNumerator = Integer.parseInt(miniNumerator.toString());

//		lastDivisionResult = stepNumerator / multiplicon;
//		if (lastDivisionResult > 0 || (positionIdx >= numberStr.length() - 1)) {
//			if (checkIfUserTyped(lastDivisionResult)) {
//				valueToSubtractFrom.setLength(0);
//				valueToSubtractFrom.append(miniNumerator.toString());
//				miniNumerator.setLength(0);
//				positionIdx++;
//				appendTextToDS(lastDivisionResult + "", answerDS);
//				clearUserTyped();
//				transitionTo(State.PICK_SUB_NUM, lastDivisionResult);
//			} else {
//				clearUserTyped();
//			}
//		} else {
//			if (checkIfUserTyped(0)) {
//				appendTextToDS("" + 0, answerDS);
//				positionIdx++;
//				clearUserTyped();
//			} else {
//				clearUserTyped();
//			}
//		}
	}

	private void updatePositionDigit() {
		char newDigit = numberStr.charAt(positionIdx);
		miniNumerator.append(newDigit);
		if (subNumbersExtensions.size() > 0)
			appendTextToDS(newDigit + "", subNumbersExtensions.get(subNumbersExtensions.size() - 1));
		lastPositionIdx = positionIdx;
	}

	
	private void handlePickSubNum() {
		String userInput = userTypedDS.getText();
		userInput = userInput.substring(1, userInput.length());
		boolean moveToNextStep = false;

		int multNum = subLastAnswer.get(subLastAnswer.size() - 1);
//		int correctSubValue = multNum * multiplicon;
//		if (userInput.length() > 0) {
//			// user typed something, check validity.
//			if (checkIfUserTyped(correctSubValue)) {
//				moveToNextStep = true;
//			} else {
//				clearUserTyped();
//			}
//		} else {
//			// user did not provide answer, show solution.
//			moveToNextStep = true;
//		}
//
//		if (moveToNextStep) {
//			DrawableString subNum = aboveAdditionNumbers.get(aboveAdditionNumbers.size() - 1);
//			subNum.setText("-" + correctSubValue);
//			clearUserTyped();
//			transitionTo(State.WRITE_SUB_RESULT, -1);
//		}

	}

	private void handleWriteSubResult() {
		String userInput = userTypedDS.getText();
		boolean moveToNextStep = false;

		int subtractionNumber = Math.abs(Integer.parseInt(aboveAdditionNumbers.get(aboveAdditionNumbers.size() - 1).getText()));

		int fromNum = Integer.parseInt(valueToSubtractFrom.toString());
		int resultValue = fromNum - subtractionNumber;

		if (userInput.length() > 0) {
			// user typed something, check if it is correct.
			moveToNextStep = checkIfUserTyped(resultValue);

		} else {
			// user didn't type something, show them the answer.
			moveToNextStep = true;
		}

		if (moveToNextStep) {
			valueToSubtractFrom.setLength(0);
			valueToSubtractFrom.append("" + resultValue);

			DrawableString retNum = columnResults.get(columnResults.size() - 1);
			retNum.setText("" + resultValue);
			clearUserTyped();
			transitionTo(State.MULT_ELEMENT, -1);
		} else {
			//let user change what they typed instead of clobbering it.
			//clearUserTyped(); //uncomment if you want this to clobber their input.
		}

	}

	private void transitionTo(State newState, int passedValueIfNecessary) {
		state = newState;
		switch (state) {
		case MULT_ELEMENT:
			preparePickTopNum();
			break;
		case WRITE_CARRY:
			preparePickSubNum(passedValueIfNecessary);
			break;
		case SUM_ROWS:
			prepareWriteSubResult();
			break;
		default:
			break;
		}
	}

	private void preparePickTopNum() {
//		miniNumerator.setLength(0);
//		miniNumerator.append(valueToSubtractFrom.toString());
//
//		String lastResult = columnResults.get(columnResults.size() - 1).getText();
//		DrawableString extension = new DrawableString(miniNumerator.toString().substring(lastResult.length()));
//		subNumbersExtensions.add(extension);
//
//		// correctly calculate the extension value
//		if (lastPositionIdx != positionIdx && positionIdx < numberStr.length()) updatePositionDigit();
//
//		// check if division is done, configure remainder.
//		if(positionIdx >= numberStr.length()) {
//			drawCursor = false;
//			remainder = Integer.parseInt(columnResults.get(columnResults.size() - 1).getText());
//			remainderDS.setText("R:" + remainder);
//			if(makeLastResultColored) {
//				columnResults.get(columnResults.size() - 1).makeRed();
//			}
//		}
		
		clearUserTyped();
		positionCursor();
		positionUserTyped();
		positionNumberExtensions();
	}

	private void preparePickSubNum(int resultOfLastPickTop) {
		
		
		positionSubtractionResults();
		positionCursor();
		positionUserTyped();
	}

	private void prepareWriteSubResult() {
		horrizontal1Points.add(new Vector2(0, 0));
		horrizontal2Points.add(new Vector2(0, 0));
		positionHorrizontalBars();

		clearUserTyped();
		positionCursor();
		positionUserTyped();

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



	private void appendTextToDS(String text, DrawableString targetDS) {
		String textToAmend = targetDS.getText();
		textToAmend += text;
		targetDS.setText(textToAmend);
		positionCursor();
	}

	protected String getUserText() {
		return userTypedDS.getText();
	}

	private void clearUserTyped() {
		if (state == State.WRITE_CARRY) {
			userTypedDS.setText("-");
		} else {
			userTypedDS.setText("");
		}
		positionCursor();
	}

	private boolean checkIfUserTyped(int targeNumber) {
		String userText = userTypedDS.getText();

		// user didn't type anything, show them an answer.
		if (userText == "") return true;

		// this should take care of the minus sign; the user cannot type minus.
		int userNumber = Math.abs(Integer.parseInt(userText));

		return userNumber == targeNumber;
	}

	private boolean shouldDrawCursor() {
		if(remainderDS.getText() != "" || state == State.START)
			return false;
		
		if (timer.timerUp(cursorTimerKey)) {
			drawCursor = !drawCursor;
			timer.setTimer(cursorTimerKey, cursorDelay);
		}

		return drawCursor && userInputActive;
	}

	public void scale(float scaleX, float scaleY) {
		this.multipliconDS.setScale(scaleX, scaleY);
		this.numberDS.setScale(scaleX, scaleY);
		//this.answerDS.setScale(scaleX, scaleY);
		this.multSymbolDS.setScale(scaleX, scaleY);
		this.cursorDS.setScale(scaleX, scaleY);
		this.userTypedDS.setScale(scaleX, scaleY);
		this.remainderDS.setScale(scaleX * additionalScaleFactor, scaleY * additionalScaleFactor);
		this.sizeSourceDS.setScale(scaleX, scaleY);
		calculateSpaceOffsets();
		for (int i = 0; i < aboveAdditionNumbers.size(); ++i) {
			aboveAdditionNumbers.get(i).setScale(scaleX, scaleY);
			columnResults.get(i).setScale(scaleX, scaleY);
		}
		for (int i = 0; i < subNumbersExtensions.size(); ++i) {
			subNumbersExtensions.get(i).setScale(scaleX, scaleY);
		}
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
		START, MULT_ELEMENT, WRITE_CARRY, SUM_ROWS
	}
}
