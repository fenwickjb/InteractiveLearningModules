package enigma.engine.baseconversion;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import enigma.engine.CourseModule;
import enigma.engine.DrawableString;


public class FractionalBinaryModule extends CourseModule {
	
	private DrawableString instructions;
	private DrawableString number;
	private LongDivisionEntity longDiv;
	private BinaryFractMultiply multUnit;
	private BinaryAdder adderUnit;
	
	/**
	 * Constructor
	 * 
	 * @param camera
	 *            the Orthographic camera. This is used to convert points
	 */
	public FractionalBinaryModule(OrthographicCamera camera) {
		super(camera);
		
		instructions = new DrawableString("Hello World");
		instructions.setXY(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() * 0.90f);
		
		number = new DrawableString("1234");
		number.setXY(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.80f);
		number.setScale(0.8f, 0.8f);
		
		longDiv = new LongDivisionEntity(739, 3, Gdx.graphics.getWidth()*0.25f, Gdx.graphics.getHeight()/2);
		longDiv.setActive(false);
		
		multUnit = new BinaryFractMultiply(0.37f, 2, Gdx.graphics.getWidth()*0.75f, Gdx.graphics.getHeight()/2, false);
		multUnit.setActive(false);
		
		adderUnit = new BinaryAdder(13, 13, Gdx.graphics.getWidth()*0.5f, Gdx.graphics.getHeight()/2, false);
	}

	@Override
	public void IO() {
		super.IO();
		if(Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) {
			instructions.setScale(2, 2);
			longDiv.scale(1.5f, 1.5f);
			multUnit.scale(1.5f, 1.5f);
			adderUnit.scale(1.5f, 1.5f);
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)){
			instructions.setScale(0.5f, 0.5f);
			longDiv.scale(0.5f, 0.5f);
			multUnit.scale(0.5f, 0.5f);
			adderUnit.scale(0.5f, 0.5f);
		}
		longDiv.IO();
		multUnit.IO();
		adderUnit.IO();
	}

	@Override
	public void logic() {
		super.logic();
		instructions.animateLogic();
		longDiv.logic();
		multUnit.logic();
		adderUnit.logic();
	}

	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		instructions.draw(batch);
		number.draw(batch);
		longDiv.draw(batch);
		multUnit.draw(batch);
		adderUnit.draw(batch);
	}
	
}
