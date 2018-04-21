package enigma.engine.utilities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import enigma.engine.DrawableString;
import enigma.engine.Entity;
import enigma.engine.TextureLookup;
import enigma.engine.Touchable;

public class KeybindDisplay extends Entity implements Touchable{
	
	protected LERPSprite background;
	protected ArrayList<Tuple2<TextButton, DrawableString>> keybinds;
	protected float bgCacheX;
	protected float bgCacheY;
	
	
	private boolean hidden;
	private final float hiddenX;
	private final float hiddenY;
	private final float displayX;
	private final float displayY;
	
	public KeybindDisplay(
			ArrayList<Tuple2<String, String>> keyActionPairs, 
			float width, float height,
			float centerX, float centerY,
			boolean startHidden
			) {
		background = new LERPSprite(TextureLookup.buttonBlack);
		background.setSize(width, height);
		background.setPosition(centerX - width /2, centerY - height/2);
		background.setInterpolateSpeedFactor(30f);
		bgCacheX = background.getX();
		bgCacheY= background.getY();
		
		// generate keybinds
		keybinds = new ArrayList<Tuple2<TextButton,DrawableString>>();
		for(Tuple2<String, String> pair : keyActionPairs) {
			TextButton button = new TextButton(pair.first);
			DrawableString action = new DrawableString(pair.second, true);
			keybinds.add(new Tuple2<TextButton, DrawableString>(button, action));
		}
		
		hidden = startHidden;
		if(hidden) {
			hiddenX = background.getX();
			hiddenY = background.getY();
			displayX = background.getX();
			displayY = background.getY() - Gdx.graphics.getHeight();
		} else {
			hiddenX = background.getX();
			hiddenY = background.getY() + Gdx.graphics.getHeight();
			displayX = background.getX();
			displayY = background.getY();			
		}
		
		recenter();
	}

	private void recenter() {
		//+number reserves extra space
		int simulatedSize = keybinds.size() + 1;
		float elementHeightAllowance = background.getHeight() / (simulatedSize);
		
		float heightDisplacement = 1.25f * keybinds.get(0).first.getHeight();
		float quarterDisplacment = background.getWidth() * 0.25f;
		float eigthDisplacement = quarterDisplacment * 0.5f;
		
		float centerX = background.getX() + 2 * quarterDisplacment;
		float bottomY = background.getY();// + background.getHeight() * 0.5f;
		
		for(int i = 0; i < keybinds.size(); ++i) {
			Tuple2<TextButton, DrawableString> pair = keybinds.get(i);
			float height = ((simulatedSize - i) * elementHeightAllowance - heightDisplacement);
			
			pair.first.setCenterPosition(centerX - quarterDisplacment, bottomY + height);
			pair.second.setXY(centerX + eigthDisplacement , bottomY + height);
		}
	}
	
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button, OrthographicCamera camera) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button, OrthographicCamera camera) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer, OrthographicCamera camera) {
		return false;
	}

	@Override
	public void draw(SpriteBatch batch) {
		background.draw(batch);
		for(int i = 0; i < keybinds.size(); ++i) {
			Tuple2<TextButton, DrawableString> pair = keybinds.get(i);
			pair.first.draw(batch);
			pair.second.draw(batch);
		}
	}

	@Override
	public void logic() {
		background.logic();
		
		io_private();
		
		//if background moved (ie it lerped), then update accordingly
		//float comparisons are risky, this is really checking if there is any different bit pattern, 
		//they should stay the same exact bit pattern if nothing changes.  
		if(background.getY() != bgCacheY || background.getX() != bgCacheX) {
			recenter();
		}
		
		
		long currentTime = System.currentTimeMillis();
		for(int i = 0; i < keybinds.size(); ++i) {
			Tuple2<TextButton, DrawableString> pair = keybinds.get(i);
			pair.first.logic(currentTime);
			//pair.second.logic(), //drawable string only has animateLogic at this time.
		}
	}

	
	private void io_private() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			if(hidden) {
				background.setInterpolatePoint(displayX, displayY);
			} else {
				background.setInterpolatePoint(hiddenX, hiddenY);
			}
			
			hidden = !hidden;
		}
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	/** Throwing this incase later I think it should have a reposition method; I'll see this and refrain from writing the method.*/
	public void setPosition(float x, float y) {
		throw new RuntimeException("This menu is not repositionable, set the start position within the constructor. This is because the menu self hides/unhides.");
	}

	@Override
	public void dispose() {
	}
}
