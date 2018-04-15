package enigma.engine.basicmath;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import enigma.engine.DrawableCharBuffer;
import enigma.engine.LERPEntity;

public class DrawableExponent extends LERPEntity {
	private DrawableCharBuffer number;
	private DrawableExponent exponent;

	float exponentScale = 0.5f;

	/**
	 * Recursively add exponents, pass null for exponent of nothing.
	 * 
	 * @param number
	 * @param exponent
	 */
	public DrawableExponent(DrawableCharBuffer number, DrawableExponent exponent, float currentNumberScale) {
		this.number = number;
		this.exponent = exponent;

		applyExponentSizeRecursively(currentNumberScale);
	}

	private void applyExponentSizeRecursively(float currentScaleFactor) {
		this.number.setScale(currentScaleFactor, currentScaleFactor);
		if (this.exponent != null) {
			this.exponent.applyExponentSizeRecursively(currentScaleFactor * exponentScale);
		}
	}

	@Override
	public void setPosition(float newX, float newY) {
		x = newX;
		y = newY;
		if (number.writeFromLeft()) {
			number.setXY(newX, newY);

			if (exponent != null) {
			float offset = number.width();
				exponent.setPosition(newX + offset, newY + number.height() * 0.5f);
			}
		} else if (number.writeFromRight()) {
			number.setXY(newX, newY);

			if (exponent != null) {
				float offset = number.width();
				exponent.setPosition(newX - offset, newY + number.height() * 0.5f);
			}
		} else {
			// write from center
			number.setXY(newX, newY);
			if (exponent != null) {
				float offset = number.width();
				exponent.setPosition(newX + offset * 0.5f, newY + number.height() * 0.5f);
			}
		}
	}

	@Override
	public void logicAfterLERP() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(SpriteBatch batch) {
		number.draw(batch);
		if (exponent != null) {
			exponent.draw(batch);
		}
	}

	@Override
	public void dispose() {
	}

	public void overrideExponentText(String newText) {
		if(exponent != null) {
			exponent.setText(newText);
		}
	}

	private void setText(String newText) {
		number.setText(newText);
	}

}
