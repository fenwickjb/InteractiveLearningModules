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

public class BinaryFractMultiply extends Entity {
	public enum State {
		START, MULT_ELEMENT
	}
	
	private DrawableString multipliconDS;
	private DrawableString numberDS;
	//private DrawableString answerDS;
	private DrawableString multSymbolDS;
	
	private ArrayList<DrawableCharBuffer> answerRows = new ArrayList<DrawableCharBuffer>();
	
	private char multSymbol = 'x';
	//private char multSymbol = '*';
	

	private DrawableString cursorDS;
	private boolean drawCursor = true;

	private Timer timer;
	private String cursorTimerKey = "C";
	private long cursorDelay = 400;

	private float number;
	private float multiplicon;
	private float x;
	private float y;
	private boolean allowIO = true;

	private State state;
	boolean userInputActive = true;


	// compose the division bar
	private Vector2 btmPointRight = new Vector2();
	private Vector2 bottomLeftPoint = new Vector2();

	private ArrayList<DrawableString> aboveAdditionNumbers;
	private ArrayList<DrawableString> columnResults;
	private ArrayList<Float> subOffsets;
	private ArrayList<Vector2> horrizontal1Points;
	private ArrayList<Vector2> horrizontal2Points;

	private DrawableString userTypedDS;
	private DrawableString sizeSourceDS;
	private float spaceOffset;

	private boolean done = false;
	private boolean active = true;
	
	

	public BinaryFractMultiply(float number, float multiplicon, float x, float y, boolean start) {
		this.multipliconDS = new DrawableString(filterZeros("" + multiplicon));
		this.numberDS = new DrawableString("" + number);
		
		this.number = number;
		this.multiplicon = multiplicon;
		
		this.multSymbolDS = new DrawableString("" + multSymbol);
		this.multSymbolDS.setRightAlign();
		
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
	}

	private void positionCursor() {
		switch (state) {
		case MULT_ELEMENT: {
			DrawableCharBuffer answerDS = answerRows.get(answerRows.size() - 1);
			
			float x = answerDS.getX();
			float y = answerDS.getY();
			cursorDS.setXY(x + spaceOffset, y);
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

	private float getHeightTolerenace() {
		return 0.2f * sizeSourceDS.height();
	}

	@Override
	public void draw(SpriteBatch batch) {
		// Sprite and String drawings.
		numberDS.draw(batch);
		multipliconDS.draw(batch);
		multSymbolDS.draw(batch);
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



	@Override
	public void logic() {

	}

	@Override
	public void dispose() {

	}

	public void IO() {
		if (allowIO && !done && active) {

			pollTypedNumbers();
			if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) || Gdx.input.isKeyJustPressed(Input.Keys.DEL)
					|| Gdx.input.isKeyJustPressed(Input.Keys.D)) {
				if(userTypedDS.length() != 0) {
					String text = userTypedDS.getText();
					text = text.substring(0, text.length() - 1);
					userTypedDS.setText(text);
				}
				positionCursor();
			}

			if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
				nextStep();
			}
		}
	}

	private void pollTypedNumbers() {


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
		if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)){
			userTypedDS.append(".");
		}

		if (capturedNumber) {
			// just overwrite what ever they typed last time.
			addNumberToUserTyped(number);
			positionUserTyped();
			positionCursor();
		}
	}

	private void addNumberToUserTyped(int number) {
		
		userTypedDS.append("" + number);
	}



	private void nextStep() {
		if(state == State.START) {
			state = State.MULT_ELEMENT;
			positionElements();
			return;
		}
		
		if(!done) {
			float answer = number * multiplicon;
			if(checkIfUserTyped(answer)) {
				done = true;
				userTypedDS.setText("" + answer);
			} else {
				userTypedDS.setText("");
				done  = false;
			}
		}
	}

	
	
	protected String getUserText() {
		return userTypedDS.getText();
	}

	private boolean checkIfUserTyped(float targeNumber) {
		String userText = userTypedDS.getText();

		// user didn't type anything, show them an answer.
		if (userText == "") return true;

		// this should take care of the minus sign; the user cannot type minus.
		float userNumber = Math.abs(Float.parseFloat(userText));

		return Math.abs(userNumber -targeNumber) < 0.00001f;
	}

	private boolean shouldDrawCursor() {
		if(done || !active)
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
		//this.remainderDS.setScale(scaleX * additionalScaleFactor, scaleY * additionalScaleFactor);
		this.sizeSourceDS.setScale(scaleX, scaleY);
		calculateSpaceOffsets();
		for (int i = 0; i < aboveAdditionNumbers.size(); ++i) {
			aboveAdditionNumbers.get(i).setScale(scaleX, scaleY);
			columnResults.get(i).setScale(scaleX, scaleY);
		}
//		for (int i = 0; i < subNumbersExtensions.size(); ++i) {
//			subNumbersExtensions.get(i).setScale(scaleX, scaleY);
//		}
		positionElements();
		positionCursor();
		positionUserTyped();
		positionSubtractionResults();
		//positionNumberExtensions();
	}

	private void calculateSpaceOffsets() {	
		spaceOffset = sizeSourceDS.width() * 0.2f;
	}

	public void setActive(boolean activate) {
		this.active = activate;
	}
}
