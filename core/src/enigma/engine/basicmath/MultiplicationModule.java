package enigma.engine.basicmath;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import enigma.engine.CourseModule;
import enigma.engine.DrawableCharBuffer;
import enigma.engine.DrawableString;
import enigma.engine.baseconversion.MultiplicationEntity;

public class MultiplicationModule extends CourseModule {

	private DrawableString instructions;
	private DrawableString number;
	private MultiplicationEntity multEntity;
	private Random rng;
	private DrawableCharBuffer test;

	/**
	 * Constructor
	 * 
	 * @param camera
	 *            the Orthographic camera. This is used to convert points
	 */
	public MultiplicationModule(OrthographicCamera camera) {
		super(camera);

		instructions = new DrawableString("Multiplication");
		instructions.setXY(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.90f);

		number = new DrawableString("");
		number.setXY(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.80f);
		number.setScale(0.8f, 0.8f);

		multEntity = new MultiplicationEntity(1952.5f, 14, Gdx.graphics.getWidth() / 2, getHeightPositionForLDEntity());
		
		rng = new Random(11);
		
		test = new DrawableCharBuffer("hello 0123456789.01");
		test.setXY(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
		test.setRightAlign();
	}
	
	private float getHeightPositionForLDEntity() {
		return Gdx.graphics.getHeight() * 0.7f;
	}

	private void calculateNewRandomDivision() {
		int numerator = Math.abs(rng.nextInt(100000) + 1);
		int denominator = Math.abs(rng.nextInt(25) + 1);
		
		multEntity = new MultiplicationEntity(numerator, denominator, Gdx.graphics.getWidth() / 2, getHeightPositionForLDEntity());
	}

	@Override
	public void IO() {
		super.IO();
		if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) {
			instructions.setScale(2, 2);
			multEntity.scale(1.5f, 1.5f);
			test.setScale(2, 2);
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
			instructions.setScale(0.5f, 0.5f);
			multEntity.scale(0.5f, 0.5f);
			test.setScale(0.5f, 0.5f);
		}
		else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
			calculateNewRandomDivision();
		}
		multEntity.IO();
	}

	@Override
	public void logic() {
		super.logic();
		instructions.animateLogic();
		multEntity.logic();
		test.logic();
	}

	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		instructions.draw(batch);
		number.draw(batch);
		multEntity.draw(batch);
		//test.draw(batch);
	}

}
