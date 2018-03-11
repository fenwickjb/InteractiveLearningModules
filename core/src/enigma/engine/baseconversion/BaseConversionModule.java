package enigma.engine.baseconversion;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import enigma.engine.CourseModule;
import enigma.engine.DrawableString;


public class BaseConversionModule extends CourseModule {
	
	private DrawableString instructions;
	private DrawableString number;
	private LongDivisionEntity longDiv;
	
	/**
	 * Constructor
	 * 
	 * @param camera
	 *            the Orthographic camera. This is used to convert points
	 */
	public BaseConversionModule(OrthographicCamera camera) {
		super(camera);
		
		instructions = new DrawableString("Hello World");
		instructions.setXY(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() * 0.90f);
		
		number = new DrawableString("1234");
		number.setXY(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.80f);
		number.setScale(0.8f, 0.8f);
		
		longDiv = new LongDivisionEntity(739, 3, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
	}

	@Override
	public void IO() {
		super.IO();
		if(Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) {
			instructions.setScale(2, 2);
			longDiv.scale(1.5f, 1.5f);
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)){
			instructions.setScale(0.5f, 0.5f);
			longDiv.scale(0.5f, 0.5f);
		}
		longDiv.IO();
	}

	@Override
	public void logic() {
		super.logic();
		instructions.animateLogic();
		longDiv.logic();
	}

	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		instructions.draw(batch);
		number.draw(batch);
		longDiv.draw(batch);
	}
	
}
