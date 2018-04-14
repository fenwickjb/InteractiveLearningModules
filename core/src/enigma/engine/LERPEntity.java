package enigma.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public abstract class LERPEntity extends Entity{
	//interpolationg fields
	private static Vector2 utilVec = new Vector2();
	protected boolean interpolateToPoint = false;
	protected boolean interpolateToScale = false;
	protected Vector2 interpolatePoint = new Vector2();
	protected Vector2 targetScale = new Vector2(1, 1);
	protected float interpolateSpeed = Tools.convertSpeedTo60FPSValue(10f);
	protected float interpolateScaleSpeed = Tools.convertSpeedTo60FPSValue(0.25f);
	protected float scaleX = 1;
	protected float scaleY = 1;
	protected float x = 0;
	protected float y = 0;
	
	private void handleInterpolation() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		if(interpolateToScale) {
			//this of this as a 1D vector
			//assuming a uniform scale
			float scaleDirection;
			float scaleDelta;
			
			boolean shouldScaleX = Math.abs(targetScale.x - this.scaleX) > 0.001f;
			boolean shouldScaleY = Math.abs(targetScale.y - this.scaleY) > 0.001f;
			
			if (shouldScaleX) {
				scaleDirection = targetScale.y - this.scaleX;
				scaleDelta = MathUtils.clamp(scaleDirection * interpolateScaleSpeed * deltaTime, -1, 1);
				this.scaleX += scaleDelta;
			}
			if(shouldScaleY) {
				scaleDirection = targetScale.y - this.scaleY;
				scaleDelta = MathUtils.clamp(scaleDirection * interpolateScaleSpeed * deltaTime, -1, 1);
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

			Vector2 direction = LERPEntity.utilVec.set(interpolatePoint.x - this.x, interpolatePoint.y - this.y);
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
	
	public void setInterpolateToPoint(float x, float y) {
		this.interpolatePoint.set(x, y);
		this.interpolateToPoint= true;
	}
	
	public void setInterpolateToScale(float scaleX, float scaleY) {
		this.targetScale.set(scaleX, scaleY);
		this.interpolateToScale = true;
	}
	
	public void setInterpolateSpeedFactor(float factor) {
		interpolateSpeed = Tools.convertSpeedTo60FPSValue(factor);
	}
	
	public void logic() {
		if(isInterpolating()) {
			handleInterpolation();
		}
		logicAfterLERP();
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public abstract void setPosition(float newX, float newY);
	
	public boolean isInterpolating() {
		return interpolateToScale || interpolateToPoint;
	}
	
	/** this method is called within the logic method */
	public abstract void logicAfterLERP();
	
}
