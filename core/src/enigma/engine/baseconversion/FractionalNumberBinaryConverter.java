package enigma.engine.baseconversion;


import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import enigma.engine.DrawableString;
import enigma.engine.Tools;
import enigma.engine.utilities.LERPSprite;

@SuppressWarnings("unused")
public class FractionalNumberBinaryConverter {
	private ArrayList<BinaryFractMultiply> components;
	private float number;
	private float x = 0;
	private float y = 0;
	private DrawableString exampleDS;
	private float scaleX = 1;
	private float scaleY = 1;
	private boolean complete = false;
	private float shrinkFactor = 0.45f;
	private int multLimit = Integer.MAX_VALUE;
	private boolean finalScaleResultScaled = false;
	
	//interpolationg fields
	private static Vector2 utilVec = new Vector2();
	private boolean interpolating = false;
	protected boolean interpolateToPoint = false;
	protected boolean interpolateToScale = false;
	protected Vector2 interpolatePoint = new Vector2();
	protected float targetScale = 1;
	protected float interpolateSpeed = Tools.convertSpeedTo60FPSValue(10f);
	protected float interpolateScaleSpeed = Tools.convertSpeedTo60FPSValue(10f);

	public FractionalNumberBinaryConverter(float number) {
		components = new ArrayList<BinaryFractMultiply>();
		this.number = number;
		
		exampleDS = new DrawableString("0");
		components.add(new BinaryFractMultiply(number, x, y, false));
		
		positionElements();
	}

	private void positionElements() {
		scaleElements();
		
		float lastX = x; 
		float lastWidth = 0;
		float spacing = calculateSpacing();
		
		for(int i = 0; i < components.size(); ++i) {
			BinaryFractMultiply mult = components.get(i);
			float newX = lastX + lastWidth + ((i == 0) ? 0 : spacing);
			if(i != components.size() -1 || i == 0 || (i == (multLimit - 1) && isDone())) {
				mult.setPosition(newX, y);
			} else {
				mult.setPosition(newX + 0.5f * mult.getWidth(), y);
			}
			
			lastX = newX;
			lastWidth = mult.getWidth();
		}
	}
	
	private void scaleElements() {
		int size = components.size();
		for(int i = 0; i < size; ++i) {
			if(size - 1 != i) {
				components.get(i).scale(shrinkFactor* scaleX, shrinkFactor* scaleY);
			} else {
				//go ahead and scale element if they're done.
				if (isDone() && components.get(i).isDone()) {
					components.get(i).scale(shrinkFactor* scaleX, shrinkFactor* scaleY);
				}
			}
		}				
	}

	private float calculateSpacing() {
		//height doesn't change with length, so I use it a lot of a consistent, 
		//but scaled offset source.
		return exampleDS.height() * 0.5f;
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
		positionElements();
		
	}
	
	public void logic() {
		for(BinaryFractMultiply entity : components) {
			entity.logic();
		}
		
		//when the user is done, make sure the last component is correctly positioned and colored. 
		if(isDone()) {
			if(!finalScaleResultScaled) {
				positionElements();
				components.get(components.size() - 1).colorWholeDigit();
				finalScaleResultScaled = true;
			}
			return;
		}
		
		BinaryFractMultiply last = components.get(components.size() - 1);
		if(last.isDone()) {
			last.colorWholeDigit();
			float result = last.result();
			if(result >= 1) {
				result -= 1;
				result = Float.parseFloat(Tools.trimFloat(result , ("" + last.result()).length()));
			}
			if(result == 0 || shouldStopCreatingNewMultiplications()) {
				//binary conversion is done. 
				positionElements();
			} else {
				//there are still digits to be processed.
				BinaryFractMultiply newComp = new BinaryFractMultiply(result, x, y, true);
				components.add(newComp);
				positionElements();
			}
		}
//		if(!complete && components.size() > 0) {
//			if(last.isDone() && last.result() == 0) {
//				positionElements();
//				complete = true;
//			}
//		}
		
		if(interpolateToScale || interpolateToPoint) {
			handleInterpolation();
		}
	}
	
	private void handleInterpolation() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		if(interpolateToScale) {
			//this of this as a 1D vector
			//assuming a uniform scale
			float scaleDirection;
			float scaleDelta;
			
			boolean shouldScaleX = Math.abs(targetScale - this.scaleX)< 0.001f;
			boolean shouldScaleY = Math.abs(targetScale - this.scaleY)< 0.001f;
			
			if (shouldScaleX) {
				scaleDirection = targetScale - this.scaleX;
				scaleDelta = scaleDirection * interpolateScaleSpeed * deltaTime;
				this.scaleX += scaleDelta;
			}
			if(shouldScaleY) {
				scaleDirection = targetScale - this.scaleY;
				scaleDelta = scaleDirection * interpolateScaleSpeed * deltaTime;
				this.scaleY += scaleDelta;
			}
			
			interpolateToScale = shouldScaleX || shouldScaleY;
		}
		
		float newX = this.x;
		float newY = this.y;
		if (interpolateToPoint) {
			float deltaSpeed = interpolateSpeed * deltaTime;

			float distance = Vector2.dst(interpolatePoint.x, interpolatePoint.y, this.x, this.y);
			if (distance < 0.001f) {
				interpolateToPoint = false;
				return;
			}

			Vector2 direction = FractionalNumberBinaryConverter.utilVec.set(interpolatePoint.x - this.x, interpolatePoint.y - this.y);
			direction = direction.nor();
			direction.scl(deltaSpeed);

			if (direction.len() > distance) {
				direction.nor();
				direction.scl(distance);
			}

			newX = direction.x + this.x;
			newY = direction.y + this.y;
		}
		
		//this will cause a re-scaling first as a side effect.
		setPosition(newX, newY);
	}
	
	public void setInterpolateSpeedFactor(float factor) {
		interpolateSpeed = Tools.convertSpeedTo60FPSValue(factor);
	}

	private boolean shouldStopCreatingNewMultiplications() {
		return components.size() >= multLimit;
	}

	public void draw(SpriteBatch batch) {
		for(BinaryFractMultiply entity : components) {
			entity.draw(batch);
		}		
	}

	public void IO() {
		for(BinaryFractMultiply entity : components) {
			entity.IO();
		}
	}
	
	public boolean isDone() {
		return complete 
				|| (components.size() == multLimit && components.get(components.size() -1).isDone());
	}

	public void setLimitMultiplications(int limitMultiplications) {
		this.multLimit = limitMultiplications;
	}

	public String getDigitsString() {
		if(!isDone())
			return null;
		
		String result = "";

		for(BinaryFractMultiply comp : components) {
			result += comp.getResultText().charAt(0);
		}
		
		return result;
	}

	public float getWholeResultLocX(int index) {
		BinaryFractMultiply comp= components.get(index);
		return comp.getWholeDS().getX();
	}

	public float getWholeResultLocY(int index) {
		BinaryFractMultiply comp= components.get(index);
		return comp.getWholeDS().getY();
	}
}
