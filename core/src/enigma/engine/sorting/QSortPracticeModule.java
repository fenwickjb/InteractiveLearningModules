package enigma.engine.sorting;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import enigma.engine.CourseModule;
import enigma.engine.Draggable;
import enigma.engine.TextureLookup;

//enum SortType{SELECTION, INSERTION, QUICK}

public class QSortPracticeModule extends CourseModule {
	/** a vector to hold converted touch coordinates into game world coordinates */
	//private Vector3 convertedTouchVect = new Vector3(0, 0, 0);
	private boolean devMode = false;
	protected ShapeRenderer sr;
	protected Draggable dragTarget = null;

	private SortableArray array;

	private float elementWidth = 30;
	private int numElements;
	private Random rng;
	
	private SortType sortType = SortType.QUICK;
	private TutorialManager tutorialManager;

	/**
	 * Constructor
	 * 
	 * @param camera
	 *            the Orthographic camera. This is used to convert points
	 */
	public QSortPracticeModule(OrthographicCamera camera) {
		super(camera);
		sr = TextureLookup.shapeRenderer;
		rng = new Random();
//		rng = new Random(33); //set seed for all generation by uncommenting this line. 

		// randomly chose between 7-9 elements
		numElements = rng.nextInt(3) + 7;

		createNewArray();
		//startTutorialManager();
	}

	private void createNewArray() {
		//array = new InsertionSortableArray(Gdx.graphics.getWidth() / 2, 200, elementWidth, numElements, 10, rng.nextInt());
		//array = new SelectionSortableArray(Gdx.graphics.getWidth() / 2, 200, elementWidth, numElements, 10, rng.nextInt());
		
		if(sortType == SortType.QUICK) {
			array = new QuickSortableArray(Gdx.graphics.getWidth() / 2, 200, elementWidth, numElements, 10, rng.nextInt());
		} else if(sortType == SortType.INSERTION) {
			array = new InsertionSortableArray(Gdx.graphics.getWidth() / 2, 200, elementWidth, numElements, 10, rng.nextInt());
		} else if(sortType == SortType.SELECTION) {
			array = new SelectionSortableArray(Gdx.graphics.getWidth() / 2, 200, elementWidth, numElements, 10, rng.nextInt());
		}
		array.centerOnPoint(Gdx.graphics.getWidth() * .5f, Gdx.graphics.getHeight() * .2f);
		
		
		//clear any pending tutorial manager.
		tutorialManager = null;
	}

	@Override
	public void logic() {
		super.logic();
		if(tutorialManager != null && tutorialManager.active()) {
			tutorialManager.logic();
			array.logic();
		} else {
			array.IO();
			array.logic();
		}
	}

	@Override
	public void IO() {
		// do sub module IO
		super.IO();

		if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT) && Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
		}

		if (devMode && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
				sortType = SortType.SELECTION;
				createNewArray();
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
				sortType = SortType.INSERTION;
				createNewArray();
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
				sortType = SortType.QUICK;
				createNewArray();
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
				startTutorialManager();
			}
			if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
				int maxElementValue = 10;
				int max = maxElementValue;
				int[] sourceArray = {
						(int) (max * .5), 
						(int) (max * .3), 
						(int) (max * .6), 
						(int) (max * .7),
						(int) (max * 1),
						(int) (max * .2),
						(int) (max * .9)
						};
				
				array = new QuickSortableArray(Gdx.graphics.getWidth() / 2, 200, elementWidth, maxElementValue, sourceArray);
				array.centerOnPoint(Gdx.graphics.getWidth() * .5f, Gdx.graphics.getHeight() * .2f);
			}
			
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
			numElements = rng.nextInt(3) + 7;
			createNewArray();
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
			numElements = rng.nextInt(3) + 12;
			createNewArray();
		}

	}

	private void startTutorialManager() {
		tutorialManager = array.getTutorialManager(Gdx.graphics.getWidth() / 2, 200, elementWidth, numElements, 10);
		array = tutorialManager.getArray();
	}

	@Override
	public void draw(SpriteBatch batch) {
		// draw any sub modules (super call)
		super.draw(batch);
		
		boolean batchWasDrawing = false;
		if (batch.isDrawing()) {
			array.drawPreSprites(batch);
			
			
			// shape rendering cannot begin while batch isDrawing.
			batch.end();
			// flag that batch should start back after drawing of columns.
			batchWasDrawing = true;
		}
		if (sr.isDrawing()) throw new RuntimeException("The shape rendering should not be pre-drawing");

		sr.setColor(TextureLookup.foregroundColor);
		sr.begin(ShapeType.Filled);
		array.draw(batch);
		sr.end();

		if (batchWasDrawing) {
			// restore batch drawing.
			batch.begin();
			array.drawPostSprites(batch);
		}
	}

	@Override
	public void dispose() {
		// dispose any sub-modules
		super.dispose();
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(tutorialManager != null && tutorialManager.active()){
			return tutorialManager.touchDown(screenX, screenY, pointer, button, camera);
		} else {
			return array.touchDown(screenX, screenY, pointer, button, camera);
		}
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(tutorialManager != null && tutorialManager.active()){
			return tutorialManager.touchUp(screenX, screenY, pointer, button, camera);
		} else {
			return array.touchUp(screenX, screenY, pointer, button, camera);
		}	
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(tutorialManager != null && tutorialManager.active()){	
			return tutorialManager.touchDragged(screenX, screenY, pointer, camera);
		} else {
			return array.touchDragged(screenX, screenY, pointer, camera);
		}
	}
}
