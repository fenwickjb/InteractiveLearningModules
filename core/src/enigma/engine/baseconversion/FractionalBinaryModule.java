package enigma.engine.baseconversion;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import enigma.engine.CourseModule;
import enigma.engine.DrawableString;
import enigma.engine.Game;
import enigma.engine.TextureLookup;


public class FractionalBinaryModule extends CourseModule {
	private enum State{
		WHOLE_NUM,
		FRACT_NUM
	}
	private State state = State.WHOLE_NUM;
	private DrawableString instructions;
	private DrawableString number;
	//private LongDivisionEntity longDiv;
	//private BinaryFractMultiply multUnit;
	//private BinaryAdder adderUnit;
	private ArrayList<Sprite> sNotationLoops;
	private Random rng;
	private WholeNumberBinaryConverter wholeConv;
	private FractionalNumberBinaryConverter fracConv;
	
	/**
	 * Constructor
	 * 
	 * @param camera
	 *            the Orthographic camera. This is used to convert points
	 */
	public FractionalBinaryModule(OrthographicCamera camera) {
		super(camera);
		
		instructions = new DrawableString("Fractional Binary Conversions");
		instructions.setXY(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() * 0.90f);
		
		number = new DrawableString("1234");
		number.setXY(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.80f);

		sNotationLoops = new ArrayList<Sprite>();
		sNotationLoops.add(new Sprite(TextureLookup.sNotationLoop));
		sNotationLoops.get(0).setPosition(300, 300);
		
		if(Game.DEBUG) {
			rng = new Random(12);
		} else {
			rng = new Random();
		}
		
		generateNumber();
		prepareSubComponents();
	}
	
	private void generateNumber() {
		int wholeNumber = rng.nextInt(500) + 1;
		
		int decimalNumerator = rng.nextInt(500) + 1;
		if(decimalNumerator % 2 != 00) decimalNumerator++;
		
		int decimalDenominator = rng.nextInt(500) + 600;
		if(decimalDenominator % 2 != 0) decimalDenominator++;
		
		double fractional = decimalNumerator / (double) decimalDenominator;
		
		String fractionalStr = "" + fractional;
		int trimLength = 4 + rng.nextInt(3);
		if(fractionalStr.length() > trimLength) {
			fractionalStr = fractionalStr.substring(0, trimLength);
		}
		if(fractionalStr.charAt(0) == '0') {
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
		if(Gdx.input.isKeyJustPressed(Input.Keys.N)) {
			generateNumber();
			prepareSubComponents();
		}
		wholeConv.IO();
		fracConv.IO();
	}

	@Override
	public void logic() {
		super.logic();
		instructions.animateLogic();
		wholeConv.logic();
		fracConv.logic();
		
		transitionalLogic();
		
	}

	private void transitionalLogic() {
		if(state == State.WHOLE_NUM) {
			if(wholeConv.isDone()) {
				state = State.FRACT_NUM;
				//createFractional();
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
	}
	
}
