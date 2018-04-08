package enigma.engine.baseconversion;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import enigma.engine.CourseModule;
import enigma.engine.DrawableCharBuffer;
import enigma.engine.DrawableString;
import enigma.engine.Game;
import enigma.engine.TextureLookup;

public class IEEEFloat16Converter extends CourseModule {
	private enum State {
		WHOLE_NUM, FRACT_NUM, INTERPOLATE_NUM, SCIENTIFIC, SIGN_BIT, EXPONENT_ADD, MANTISSA, DONE 
	}

	private State state = State.WHOLE_NUM;
	private DrawableString instructions;
	private DrawableString number;
	// private LongDivisionEntity longDiv;
	// private BinaryFractMultiply multUnit;
	// private BinaryAdder adderUnit;
	private ArrayList<Sprite> sNotationLoops;
	private Random rng;
	private WholeNumberBinaryConverter wholeConv;
	private FractionalNumberBinaryConverter fracConv;
	private DrawableCharBuffer result;
	private ScientificExponentFinder sciFinder;
	private DrawableCharBuffer exponentCounter;
	private DrawableCharBuffer twosComplementExponentCounter;
	private DrawableCharBuffer bitsIEEE;
	private DrawableString signBit;
	private BinaryAdder exponentAddr;
	private DrawableCharBuffer exponentText;
	private DrawableCharBuffer mantissa;
	private int mantissaSpotsUsed;
	private DrawableCharBuffer mantissaPadding;

	/**
	 * Constructor
	 * 
	 * @param camera
	 *            the Orthographic camera. This is used to convert points
	 */
	public IEEEFloat16Converter(OrthographicCamera camera) {
		super(camera);

		instructions = new DrawableString("IEEE 16bit Conversions");
		instructions.setXY(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.90f);

		number = new DrawableString("1234");
		number.setXY(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.80f);

		sNotationLoops = new ArrayList<Sprite>();
		sNotationLoops.add(new Sprite(TextureLookup.sNotationLoop));
		sNotationLoops.get(0).setPosition(300, 300);

		if (Game.DEBUG) {
			rng = new Random(12);
		} else {
			rng = new Random();
		}

		generateNumber();
		prepareSubComponents();
	}

	private void generateNumber() {
		// int wholeNumber = rng.nextInt(500) + 1;
		//int wholeNumber = rng.nextInt(10) + 1;
		int wholeNumber = 0;

		int decimalNumerator = rng.nextInt(500) + 1;
		if (decimalNumerator % 2 != 00) decimalNumerator++;

		int decimalDenominator = rng.nextInt(500) + 600;
		if (decimalDenominator % 2 != 0) decimalDenominator++;

		double fractional = decimalNumerator / (double) decimalDenominator;

		String fractionalStr = "" + fractional;
		int trimLength = 4 + rng.nextInt(3);
		if (fractionalStr.length() > trimLength) {
			fractionalStr = fractionalStr.substring(0, trimLength);
		}
		if (fractionalStr.charAt(0) == '0') {
			fractionalStr = fractionalStr.substring(1);
		}

		String numberStr = "" + wholeNumber;
		numberStr += fractionalStr;

		number.setText(numberStr);

	}

	private void prepareSubComponents() {
		String fractionalNumber = number.getText();
		String[] numSplit = fractionalNumber.split("\\.");
		String wholeNum = numSplit[0];
		String fracNum = "0." + numSplit[1];

		float centerOff = 0.45f;
		wholeConv = new WholeNumberBinaryConverter(Integer.parseInt(wholeNum));
		wholeConv.setPosition(Gdx.graphics.getWidth() * centerOff, Gdx.graphics.getHeight() * 0.5f);

		fracConv = new FractionalNumberBinaryConverter(Float.parseFloat(fracNum));
		fracConv.setPosition(Gdx.graphics.getWidth() * (1f - centerOff), Gdx.graphics.getHeight() * 0.5f);
		fracConv.setLimitMultiplications(7);
	}

	@Override
	public void IO() {
		super.IO();

		if (Game.DEBUG) {
			if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) {
				instructions.setScale(2, 2);
			} else if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
				instructions.setScale(0.5f, 0.5f);
			}
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
			generateNumber();
			prepareSubComponents();
		}

		wholeConv.IO();
		fracConv.IO();
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			stateNextEnter();
		}
		if(exponentAddr != null) {
			exponentAddr.IO();
		}

	}

	private void stateNextEnter() {
 		if (state == State.SCIENTIFIC) {
			if (sciFinder == null) {
				sciFinder = new ScientificExponentFinder(Float.parseFloat(number.getText()), result);
				exponentCounter = new DrawableCharBuffer("exponent:0");
				//exponentCounter.setRightAlign();
				exponentCounter.setScale(0.75f, 0.75f);
				float x = result.getX();// + 1.5f* result.width();
				float y=  result.getY() -  result.height();
				exponentCounter.setXY(x, y);
				
				twosComplementExponentCounter = new DrawableCharBuffer("");
				twosComplementExponentCounter.setXY(x, y - result.height());

			} else {
				if (!result.isInterpolating()) {
					if (!sciFinder.isDone()) {
						sciFinder.next();
						int expon = sciFinder.getExponent();
						boolean negExponent = !sciFinder.exponentIsPositive();
						String newString = "exponent:" + (negExponent ? "-" : "") +Integer.toBinaryString(expon);
						exponentCounter.setText(newString);
					} else {
						state = State.SIGN_BIT;
					}
				}
			}
		} else if (state == State.SIGN_BIT) {
			if (bitsIEEE == null) {
				createFPBits();
			} else {
				if(number.getText().charAt(0) == '-')
				{
					signBit = new DrawableString("1");
				} else {
					signBit = new DrawableString("0");
				}
				DrawableString signSpot = bitsIEEE.getCharObjectAt(0);
				signBit.setXY(signSpot.getX(), signSpot.getY());
				state = State.EXPONENT_ADD;
			}
		} else if (state == State.EXPONENT_ADD) {
			if(exponentAddr == null) {
				float x = Gdx.graphics.getWidth() * 0.7f;
				float y = Gdx.graphics.getHeight() * 0.3f;
				
				String rawText = exponentCounter.getText();
				rawText = rawText.substring("exponent:".length(), rawText.length());
				int exponent = Integer.parseInt(rawText,2);// * (negative ? -1 : 1);
				
				exponent = 1;
				exponentAddr = new BinaryAdder(15, exponent, x, y, true);
				exponentAddr.scale(0.7f, 0.7f);
			}
			else if (!exponentAddr.isDone()) {
				//do nothing until exponeAddr is done.
			} else {
				//exponentAddr is done
				String correctExponent = exponentAddr.getAnswerString();
				exponentText = new DrawableCharBuffer(correctExponent);
				DrawableCharBuffer answerTextObject = exponentAddr.getAnswerObject();
				exponentText.setXY(answerTextObject.getX(), answerTextObject.getY());
				
				int bitIndex = 1;
				for(int i = 0; i < 5; ++i) {
					DrawableString spot = bitsIEEE.getCharObjectAt(bitIndex);
					DrawableString result = exponentText.getCharObjectAt(i);
					result.interpolateTo(spot.getX(), spot.getY());
					bitIndex++;
				}
				state = State.MANTISSA;
			}
		} else if (state == State.MANTISSA) {
			if (mantissa == null) {
				mantissa = new DrawableCharBuffer(result.getText());
				mantissa.setXY(result.getX(), result.getY());
				int mantissaStart = -1;
				for (int i = 0; i < mantissa.length(); ++i) {
					// clear everything before the . (including the . too)
					if (mantissa.getCharAt(i) != '.') {
						mantissa.setCharAt(i, ' ');
					} else {
						mantissa.setCharAt(i, ' ');
						mantissaStart = i + 1;
						break;
					}
				}
				mantissaSpotsUsed = 0;
				int len = mantissa.length();
				for (int i = 0; i < 10 && i < len - mantissaStart; ++i) {
					DrawableString mDigit = mantissa.getCharObjectAt(i + mantissaStart);
					DrawableString spot = bitsIEEE.getCharObjectAt(6 + i);
					mDigit.interpolateTo(spot.getX(), spot.getY());
					mantissaSpotsUsed++;
				}
			} else {
				if(mantissaSpotsUsed != 10) {
					String padding = "";
					for(int i = 0; i < (10 - mantissaSpotsUsed); ++i) {
						padding += "0";
					}
					mantissaPadding = new DrawableCharBuffer(padding);
					//mantissaPadding.setRightAlign();
					
					int startIdx = mantissaSpotsUsed + 6;
					for(int i = 0; i <mantissaPadding.size();++i ) {
						DrawableString spot = bitsIEEE.getCharObjectAt(startIdx + i);
						mantissaPadding.getCharObjectAt(i).setXY(spot.getX(), spot.getY());
					}
				}
				state = State.DONE;
			}
		}
	}

	private void createFPBits() {
		bitsIEEE = new DrawableCharBuffer("________________");
		int index = 1;
		
		for(int i = 0; i < 5; ++i) {
			bitsIEEE.setBlue(index);
			index++;
		}
		for(int i = 0; i < 10; ++i) {
			bitsIEEE.setRed(index);
			index++;
		}
		bitsIEEE.setXY(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.7f);
	}

	private void prepareBinaryResult() {
		String remainders = wholeConv.getReversedRemainderString();
		String fractionalDigits = fracConv.getDigitsString();

		String fractionalBinary = remainders + "." + fractionalDigits;

		result = new DrawableCharBuffer(fractionalBinary);
		result.setXY(Gdx.graphics.getWidth() * 0.50f, Gdx.graphics.getHeight() * 0.3f);
		int index = 0;
		for (int i = 0; i < remainders.length(); ++i) {
			result.setRed(index);
			DrawableString number = result.getCharObjectAt(index);
			float cX = number.getX();
			float cY = number.getY();

			int reversedIndex = (remainders.length() - 1) - i;
			float sX = wholeConv.getRemainderX(reversedIndex);
			float sY = wholeConv.getRemainderY(reversedIndex);

			number.setXY(sX, sY);
			number.interpolateTo(cX, cY);

			++index;
		}
		++index; // should leave period white
		for (int i = 0; i < fractionalDigits.length(); ++i) {
			result.setBlue(index);
			DrawableString fracNum = result.getCharObjectAt(index);
			float cX = fracNum.getX();
			float cY = fracNum.getY();
			float sX = fracConv.getWholeResultLocX(i);
			float sY = fracConv.getWholeResultLocY(i);

			fracNum.setXY(sX, sY);
			fracNum.interpolateTo(cX, cY);
			++index;
		}

		for (int i = 0; i < result.length(); ++i) {
			result.getCharObjectAt(i).setInterpolateSpeedFactor(4f);
		}
	}

	@Override
	public void logic() {
		super.logic();
		instructions.animateLogic();

		wholeConv.logic();
		fracConv.logic();
		if (result != null) {
			result.logic();
		}
		transitionalLogic();
		if(exponentAddr != null) {
			exponentAddr.logic();
		}
		if (exponentText != null) {
			exponentText.logic();
		}
		if(mantissa != null) {
			mantissa.logic();
		}
		if(mantissaPadding != null) {
			mantissaPadding.logic();
		}
	}

	private void transitionalLogic() {
		if (state == State.WHOLE_NUM) {
			if (wholeConv.isDone()) {
				state = State.FRACT_NUM;
				// createFractional();
			}
		} else if (state == State.FRACT_NUM) {
			if (fracConv.isDone()) {
				if (result == null) {
					prepareBinaryResult();
				}
				state = State.SCIENTIFIC;
			}
		} else if (state == State.SCIENTIFIC) {
			if(sciFinder != null && sciFinder.isDone()) {
				
			}
		}
	}

	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		instructions.draw(batch);
		number.draw(batch);
		wholeConv.draw(batch);
		fracConv.draw(batch);
		if (result != null) {
			result.draw(batch);
		}
		if(exponentCounter != null) {
			exponentCounter.draw(batch);
		}
		if(twosComplementExponentCounter != null) {
			twosComplementExponentCounter.draw(batch);
		}
		if(bitsIEEE != null) {
			bitsIEEE.draw(batch);
		}
		if(signBit != null) {
			signBit.draw(batch);
		}
		if(exponentAddr != null) {
			exponentAddr.draw(batch);
		}
		if(exponentText != null) {
			exponentText.draw(batch);
		}
		if(mantissa != null) {
			mantissa.draw(batch);
		}
		if(mantissaPadding != null) {
			mantissaPadding.draw(batch);
		}
	}

}
