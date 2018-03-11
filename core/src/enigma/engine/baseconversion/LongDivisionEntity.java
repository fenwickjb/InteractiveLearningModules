package enigma.engine.baseconversion;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import enigma.engine.DrawableString;
import enigma.engine.Entity;
import enigma.engine.TextureLookup;
import enigma.engine.Timer;

public class LongDivisionEntity extends Entity {
	private DrawableString denominatorDS;
	private DrawableString numeratorDS;
	private DrawableString answerDS;
	
	private float scaleX;
	private float scaleY;
	
	private DrawableString cursorDS;
	private boolean drawCursor = true;
	
	private Timer timer;
	private String cursorTimerKey = "C";
	private long cursorDelay = 400;
	
	private int numerator;
	private int denominator;
	private float x;
	private float y;
	private boolean allowIO = true;
	
	private State state;
	boolean userInputActive = true;
	
	// processing numerator fields
	private StringBuilder miniNumerator = new StringBuilder();
	private String numeratorStr;
	private int positionIdx;
	
	private int base;
	
	// compose the division bar
	private Vector2 btmPoint = new Vector2();
	private Vector2 topLeftPoint = new Vector2();
	private Vector2 topRightPoint = new Vector2();
	private int lastDivisionResult;
	
	private ArrayList<DrawableString> subNumbers;
	private ArrayList<DrawableString> subResults;
	private ArrayList<Integer> subLastAnswer;
	private ArrayList<Integer> subAnswerLength;
	private ArrayList<Float> subOffsets;
	private ArrayList<Vector2> horrizontal1Points;
	private ArrayList<Vector2> horrizontal2Points;

	
	// LOGIC
	private int lastPositionIdx = -1;
	
	private DrawableString userTypedDS;
	private float extraFactor = 1.2f;

	private StringBuilder valueToSubtractFrom = new StringBuilder();
	
	public LongDivisionEntity(int numerator, int denominator, float x, float y) {
		this.denominatorDS = new DrawableString("" + denominator);
		this.numeratorDS = new DrawableString("" + numerator);
		this.numerator = numerator;
		this.numeratorStr = this.numeratorDS.getText();
		this.denominator = denominator;
		this.answerDS = new DrawableString("");
		this.answerDS.setLeftAlign();
		this.cursorDS = new DrawableString("|");
		
		this.x = x;
		this.y = y;
		this.state = State.PICK_TOP_NUM;
		
		subNumbers = new ArrayList<DrawableString>();
		subResults = new ArrayList<DrawableString>();
		subLastAnswer = new ArrayList<Integer>();
		subAnswerLength = new ArrayList<Integer>();
		subOffsets = new ArrayList<Float>();
		horrizontal1Points = new ArrayList<Vector2>();
		horrizontal2Points = new ArrayList<Vector2>();
		
		this.timer = new Timer();
		timer.setTimer(cursorTimerKey, cursorDelay);

		userTypedDS = new DrawableString("");
		
		positionElements();
	}
	
	
	private void positionElements() {
		numeratorDS.setXY(x, y);
		
		float halfNumerWidth = numeratorDS.width() / 2;
		float halfDenomWidth = denominatorDS.width() / 2;
		float toleranceWidth = getWidthTolerance();
		float toleranceHeight = getHeightTolerenace();
		
		denominatorDS.setXY(x - (halfDenomWidth + halfNumerWidth + toleranceWidth), y);
		
		btmPoint.x = denominatorDS.getX() + halfDenomWidth + 0.5f * toleranceWidth;
		btmPoint.y = denominatorDS.getY() - 0.5f * denominatorDS.height() - toleranceHeight;
		
		topLeftPoint.x = btmPoint.x;
		topLeftPoint.y = btmPoint.y + denominatorDS.height(); 
		topLeftPoint.y += 2 * toleranceHeight; //move bar slightly above the numbers.
		
		topRightPoint.y = topLeftPoint.y;
		topRightPoint.x = topLeftPoint.x + 0.5f * toleranceWidth + numeratorDS.width();
		
		answerDS.setXY(x - numeratorDS.width() / 2 , y + numeratorDS.height() + 2 * toleranceHeight);
		
		positionCursor();
		positionUserTyped();
		positionSubtractionResults();
		positionHorrizontalBars();
	}

	private void positionCursor() {
		switch (state) {
		case PICK_TOP_NUM: {
			float x = answerDS.getX();
			float y = answerDS.getY();
			float width = answerDS.width();
			float userWidth = userTypedDS.width();
			cursorDS.setXY(x + extraFactor * (width + userWidth), y);
			break;
		}
		case PICK_SUB_NUM: {
			DrawableString lastSubNumber = subNumbers.get(subNumbers.size() - 1);
			float x = lastSubNumber.getX();
			float y = lastSubNumber.getY();
			cursorDS.setXY(x, y);
			break;
		}
		case WRITE_SUB_RESULT: {
			DrawableString lastResultNumber = subResults.get(subResults.size() - 1);
			float x = lastResultNumber.getX();
			float y = lastResultNumber.getY();
			cursorDS.setXY(x, y);
			break;
		}
		}
	}

	private void positionUserTyped() {
		switch(state) {
		case PICK_TOP_NUM:{
			float x = answerDS.getX();
			float y = answerDS.getY();
			float width = answerDS.width();
			userTypedDS.setLeftAlign();
			userTypedDS.setXY(x + (extraFactor * width), y);
			break;
		}
		case PICK_SUB_NUM:
		case WRITE_SUB_RESULT:{
			float x = cursorDS.getX();
			float y = cursorDS.getY();
			float width = answerDS.width();
			
			userTypedDS.setRightAlign();
			userTypedDS.setXY(x, y);
			break;
		}
			
		}		
	}
	
	public static final float VERTICAL_SPACING_FACTOR = 1.2f; 
	private void positionSubtractionResults() {
		for(int i = 0; i < subNumbers.size(); ++i) {
			DrawableString sub = subNumbers.get(i);
			DrawableString result = subResults.get(i);
			float xPosition = subOffsets.get(i);
			
			float numPosY = numeratorDS.getY();
			float numHght = numeratorDS.height() * VERTICAL_SPACING_FACTOR;
			
			float subY = numPosY - (2*i + 1) * numHght;
			float retY = numPosY - (2*i + 2) * numHght;
			sub.setRightAlign();
			sub.setXY(xPosition, subY);
			
			result.setRightAlign();
			result.setXY(xPosition, retY);
			
		}
		
	}


	private float getHeightTolerenace() {
		return 0.2f * numeratorDS.height();
	}

	private float getWidthTolerance() {
		return 0.5f * (0.5f * denominatorDS.width()+ 0.5f * numeratorDS.width());
	}

	@Override
	public void draw(SpriteBatch batch) {
		// Sprite and String drawings.
		numeratorDS.draw(batch);
		denominatorDS.draw(batch);
		answerDS.draw(batch);
		drawSubtractionElements(batch);
		userTypedDS.draw(batch);
		
		if(shouldDrawCursor()) {
			cursorDS.draw(batch);
		}
		
		batch.end();
		//Shape Drawing
		ShapeRenderer sr = TextureLookup.shapeRenderer;
		Color originalColor = sr.getColor();
		float r = originalColor.r;
		float g = originalColor.g;
		float b = originalColor.b;
		float a = originalColor.a;
		sr.setColor(TextureLookup.foregroundColor);
		
		sr.begin(ShapeType.Line);
		//draw the lines that make up the divisor bar. 
		sr.line(btmPoint, topLeftPoint);
		sr.line(topLeftPoint, topRightPoint);
		
		for(int i = 0; i < horrizontal1Points.size(); ++i) {
			Vector2 pnt1 = horrizontal1Points.get(i);
			Vector2 pnt2 = horrizontal2Points.get(i);
			sr.line(pnt1, pnt2);
		}
		
		sr.end();
		sr.setColor(r, g, b, a);
		
		
		batch.begin(); //restore the sprite batch rendering.
	}

	private void drawSubtractionElements(SpriteBatch batch) {
		for(int i = 0; i < subNumbers.size(); ++i) {
			subNumbers.get(i).draw(batch);
			subResults.get(i).draw(batch);
		}
	}


	@Override
	public void logic() {
		
	}

	@Override
	public void dispose() {
		
	}
	
	public void IO() {
		if(allowIO) {
			
			pollTypedNumbers();
			if(Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) 
					|| Gdx.input.isKeyJustPressed(Input.Keys.DEL)
					|| Gdx.input.isKeyJustPressed(Input.Keys.D)) {
				clearUserTyped();
				positionCursor();
			}
			
			if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
				nextStep();
			}
		}
	}

	private void pollTypedNumbers() {
		
		if(!allowNumberTyping()) return;
		
		boolean capturedNumber = false;
		int number = -1;
		
		for(int i = 0; i < 10 && !capturedNumber; ++i) {
			if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_0 + i)) {
				capturedNumber = true;
				number = i;
			}
		}
		for(int i = 0; i < 10 && !capturedNumber; ++i) {
			if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0 + i)) {
				capturedNumber = true;
				number = i;
			}
		}
		
		if(capturedNumber) {
			//just overwrite what ever they typed last time.
			addNumberToUserTyped(number);
			positionUserTyped();
			positionCursor();
		}
	}

	private void addNumberToUserTyped(int number) {
		switch (state) {
		case PICK_TOP_NUM:
			//only allow a single number to be typed.
			userTypedDS.setText("" + number);
			break;
		case PICK_SUB_NUM:
			userTypedDS.append("" + number);
			break;
		case WRITE_SUB_RESULT:
			userTypedDS.append("" + number);
			break;
		}
	}


	private boolean allowNumberTyping() {
		switch(state) {
		case PICK_TOP_NUM:
			return positionIdx < numeratorStr.length();
		case PICK_SUB_NUM:
			return true;
		case WRITE_SUB_RESULT:
			return true;
		}
		return false;
	}


	private void nextStep() {
		
		switch(state) {
		case PICK_TOP_NUM:
			handlePickTopNum();
			break;
		case PICK_SUB_NUM:
			handlePickSubNum();
			break;
		case WRITE_SUB_RESULT:
			handleWriteSubResult();
			break;
		}		
	}

	private void handlePickTopNum() {
		//check if this is the first step.
		if(numeratorStr == null) {
			numeratorStr = "" + numeratorDS.getText();
			positionIdx = 0;
			lastPositionIdx = -1;
		}	
		
		//check if user is done.
		if(positionIdx >= numeratorStr.length()) {
			return;
		}
		
		if(positionIdx != lastPositionIdx) {
			char newDigit = numeratorStr.charAt(positionIdx);
			miniNumerator.append(newDigit);
			lastPositionIdx = positionIdx;
		}
		
		int stepNumerator = Integer.parseInt(miniNumerator.toString());
		
		lastDivisionResult = stepNumerator / denominator;
		if (lastDivisionResult > 0) {
			if(checkIfUserTyped(lastDivisionResult)) {			
				//	int subtractValue = denominator * division;
				//valueToSubtractFrom.append(miniNumerator.toString());
				valueToSubtractFrom.setLength(0);
				valueToSubtractFrom.append(miniNumerator.toString());
			 	miniNumerator.setLength(0);
			 	positionIdx++;
			 	appendTextToAnswer(lastDivisionResult + "");
			 	clearUserTyped();
			 	transitionTo(State.PICK_SUB_NUM, lastDivisionResult);
			} else {
				clearUserTyped();
			}
		}
		else {
			if(checkIfUserTyped(0))
			{
				appendTextToAnswer("" + 0);
				positionIdx++;
				clearUserTyped();
			} else {
				clearUserTyped();
			}
		}
	}

	private void handlePickSubNum() {
		String userInput = userTypedDS.getText();
		userInput = userInput.substring(1, userInput.length());
		boolean moveToNextStep = false;
		
		int multNum = subLastAnswer.get(subLastAnswer.size() - 1);
		int correctSubValue = multNum * denominator;
		if( userInput.length() > 0) {
			//user typed something, check validity.
			if(checkIfUserTyped(correctSubValue)) {
				moveToNextStep = true;
			} else {
				clearUserTyped();
			}
		} else {
			//user did not provide answer, show solution.
			moveToNextStep = true;
		}
		
		if(moveToNextStep) {
			DrawableString subNum = subNumbers.get(subNumbers.size() - 1);
			subNum.setText("-" + correctSubValue);
			clearUserTyped();
			transitionTo(State.WRITE_SUB_RESULT, -1);
		}
		
	}
	
	private void handleWriteSubResult() {
		String userInput = userTypedDS.getText();
		boolean moveToNextStep = false;
		
		int subtractionNumber = Math.abs(Integer.parseInt(subNumbers.get(subNumbers.size() - 1).getText()));
		
		int fromNum = Integer.parseInt(valueToSubtractFrom.toString());
		int resultValue = fromNum - subtractionNumber;
		
		if(userInput.length() > 0) {
			//user typed something, check if it is correct.
			moveToNextStep = checkIfUserTyped(resultValue);
			
		} else {
			//user didn't type something, show them the answer.
			moveToNextStep = true;
		}
		
		if (moveToNextStep) {
			valueToSubtractFrom.setLength(0);
			valueToSubtractFrom.append("" + resultValue);
			
			DrawableString retNum = subResults.get(subResults.size() - 1);
			retNum.setText("" + resultValue);
			clearUserTyped();
			transitionTo(State.PICK_TOP_NUM, -1);
		}
		
	}

	private void transitionTo(State newState, int passedValueIfNecessary) {
		state = newState;
		switch(state) {
		case PICK_TOP_NUM:
			preparePickTopNum();
			break;
		case PICK_SUB_NUM:
			preparePickSubNum(passedValueIfNecessary);
			break;
		case WRITE_SUB_RESULT:
			prepareWriteSubResult();
			break;
		}
	}

	private void preparePickTopNum() {
		clearUserTyped();
		positionCursor();
		positionUserTyped();
		miniNumerator.setLength(0);
		miniNumerator.append(valueToSubtractFrom.toString());
	}


	private void preparePickSubNum(int resultOfLastPickTop) {
		subNumbers.add(new DrawableString(""));
		subResults.add(new DrawableString(""));
		subLastAnswer.add(resultOfLastPickTop);
		subAnswerLength.add(answerDS.length());
		float offset = calculateSubElementXOffset();
		subOffsets.add(offset);
		userTypedDS.setText("-");
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
		for(int i = 0; i < horrizontal1Points.size(); ++i)
		{
			DrawableString subtractSpot = subNumbers.get(i);
			float x = subtractSpot.getX();
			float y = subtractSpot.getY() - (extraFactor * subtractSpot.height()) * 0.5f;
			horrizontal1Points.get(i).set(x, y);
			horrizontal2Points.get(i).set(x - subtractSpot.width(), y);
		}
	}
	
	private float calculateSubElementXOffset() {
		float offset = numeratorDS.getX() - (numeratorDS.width()*0.5f);
		offset += answerDS.width();
		return offset;
	}
	
	private void correctSubElementsOffsetsForScale() {
		String answer = answerDS.getText();
		
		for(int i = 0; i < subNumbers.size(); ++i) {
			int answerChars = subAnswerLength.get(i);
			answerDS.setText(answer.substring(0, answerChars));
			float offset = calculateSubElementXOffset();
			subOffsets.set(i, offset);
		}
		
		//restore answer.
		answerDS.setText(answer);
	}
	
	private void appendTextToAnswer(String text) {
		String answerText = answerDS.getText();
		answerText += text;
		answerDS.setText(answerText);
		positionCursor();
	}
	
	private String getUserText() {
		return userTypedDS.getText();
	}


	private void clearUserTyped() {
		if (state == State.PICK_SUB_NUM) {
			userTypedDS.setText("-");
		}else {
			userTypedDS.setText("");
		}
		positionCursor();
	}

	private boolean checkIfUserTyped(int targeNumber) {
		String userText = userTypedDS.getText();
		
		// user didn't type anything, show them an answer.
		if(userText == "") return true;
		
		//this should take care of the minus sign; the user cannot type minus.
		int userNumber = Math.abs(Integer.parseInt(userText));
		
		return userNumber == targeNumber;
	}
	

	private boolean shouldDrawCursor() {
		if(timer.timerUp(cursorTimerKey)) {
			drawCursor = !drawCursor;
			timer.setTimer(cursorTimerKey, cursorDelay);
		}
		
		return drawCursor && userInputActive;
	}

	public void scale(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.denominatorDS.setScale(scaleX,scaleY);
		this.numeratorDS.setScale(scaleX, scaleY);
		this.answerDS.setScale(scaleX, scaleY);
		this.cursorDS.setScale(scaleX, scaleY);
		this.userTypedDS.setScale(scaleX, scaleY);
		for(int i = 0; i < subNumbers.size(); ++i) {
			subNumbers.get(i).setScale(scaleX, scaleY);
			subResults.get(i).setScale(scaleX, scaleY);
		}
		correctSubElementsOffsetsForScale();
		positionElements();
		positionCursor();
		positionUserTyped();
	}
	
	public enum State {
		PICK_TOP_NUM,
		PICK_SUB_NUM,
		WRITE_SUB_RESULT
	}
}
