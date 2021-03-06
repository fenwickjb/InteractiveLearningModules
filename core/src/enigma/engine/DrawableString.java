package enigma.engine;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class DrawableString {
	private String text;
	private BitmapFont bmFont;
	private GlyphLayout gl;

	private static Random rng = new Random();

	/** variable used to determine when the last character was animated */
	private long timeLastAnimated = 0;
	private boolean currentlyAnimating = false;
	private int animCharacterIterator = 0;
	private StringBuffer buildingString;
	private long delayAmountInMiliSec = 25;
	private boolean addVariationToDelay = true;
	private long variation = 0;

	/** x is centered at the middle of the string */
	private float x;

	/** y is centered at the middle of the string */
	private float y;
	
	private boolean bShouldShow = true;
	private float scaleX = 1;
	private float scaleY = 1;
	
	private Align horrizontalAlign = Align.CENTER;
	private Align verticalAlign = Align.CENTER;
	
	//Interpolation Fields
	private static Vector2 utilVec = new Vector2();
	protected boolean interpolateToPoint = false;
	protected Vector2 interpolatePoint = new Vector2();
	protected float interpolateSpeed = Tools.convertSpeedTo60FPSValue(10f);

	public DrawableString(String text) {
		// bmFont = new BitmapFont(Gdx.files.internal("prada.fnt"));
		bmFont = TextureLookup.whiteBMFont; 

		gl = new GlyphLayout();
		gl.setText(bmFont, text);
		this.setText(text);
	}
	
	public DrawableString(String text, boolean unchangingFont) {
		// bmFont = new BitmapFont(Gdx.files.internal("prada.fnt"));
		if(unchangingFont) {
			bmFont = TextureLookup.ignoreBmInversionWhite;
		} else {
			bmFont = TextureLookup.whiteBMFont; 
		}

		gl = new GlyphLayout();
		gl.setText(bmFont, text);
		this.setText(text);
	}

	public void draw(SpriteBatch batch) {
		if(bShouldShow)
		{
			bmFont.getData().setScale(this.scaleX, this.scaleY);
			
			String text;
			float halfWidth = gl.width / 2;
			float halfHeight = gl.height / 2;
			
			if (!currentlyAnimating) {
				//bmFont.draw(batch, getText(), x - gl.width / 2, y + gl.height / 2);
				text = getText();
			} else {
				//bmFont.draw(batch, buildingString.toString(), x - gl.width / 2, y + gl.height / 2);
				text=buildingString.toString();
			}
			
			//account for scaling
			halfWidth *= scaleX;
			halfHeight *= scaleY;
			
			float xComponent = 0, yComponent = 0;
			
			if(horrizontalAlign == Align.CENTER) {
				xComponent = x - halfWidth;
			} else if (horrizontalAlign == Align.LEFT) {
				xComponent = x;
			} else if (horrizontalAlign == Align.RIGHT) {
				xComponent = x - 2 * halfWidth;
			}
			
			if(verticalAlign == Align.CENTER) {
				yComponent = y + halfHeight;
			}
			
			
			//render
			bmFont.draw(batch, text, xComponent, yComponent);
			
			//restore non-scaled data.
			bmFont.getData().setScale(1, 1);
		}

		// draw accounting for scale (untested)
		// bmFont.draw(batch, getText(), x - (gl.width * bmFont.getScaleX()) / 2, y + (gl.height *
		// bmFont.getScaleY()) / 2);

	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		gl.setText(bmFont, text);
		currentlyAnimating = false;
	}

	public void setXY(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setXY(Vector3 location) {
		this.x = location.x;
		this.y = location.y;
	}

	public void translateX(float amount) {
		this.x += amount;
	}

	public void translateY(float amount) {
		this.y += amount;
	}

	public void dispose() {

		// bmFont.dispose();
	}

	public void appendToRight(DrawableString toAppend) {
		this.setText(text + toAppend.getText());
	}

	public float width() {
		return gl.width * bmFont.getScaleX() * scaleX;
	}

	public float height() {
		return gl.height * bmFont.getScaleY() * scaleY;
	}

	public float getY() {
		return y;
	}

	public float getX() {
		return x;
	}

	public Vector3 getPoint() {
		return new Vector3(x, y, 0);
	}

	public boolean otherObjectIsWithinRange(DrawableString other, int multipleOfTextHeight) {
		float threshold = multipleOfTextHeight * this.height();

		return Tools.rectangleCollisionForCenteredPoint(other.getPoint(), other.width(), other.height(), this.getPoint(), this.width(), this.height(), threshold);
	}

	public boolean touched(Vector3 touchPnt) {
		return Tools.pointWithinBounds(touchPnt.x, touchPnt.y, x - gl.width, x + gl.width, y - gl.height, y + gl.height);
	}

	public void highlight() {
		// bmFont.setColor(Color.YELLOW);
		bmFont = TextureLookup.yellowBMFont;
	}

	public void unhighlight() {
		// bmFont.setColor(Color.WHITE);
		bmFont = TextureLookup.whiteBMFont;
	}
	
	public void makeRed(){
		bmFont = TextureLookup.redBMFont;
	}
	
	public void makeBlue(){
		bmFont = TextureLookup.blueBMFont;
	}
	
	public void makeGreen(){
		bmFont = TextureLookup.greenBMFont;
	}
	
	public void makePink(){
		bmFont = TextureLookup.pinkBMFont;
	}
	
	public void makeYellow(){
		bmFont = TextureLookup.yellowBMFont;
	}
	public void makeOrange(){
		bmFont = TextureLookup.orangeBMFont;
	}
	
	public void makePurple(){
		bmFont = TextureLookup.purpleBMFont;
	}
	
	public void makeNormal(){
		this.unhighlight();
	}

	public void startAnimation() {
		if (!currentlyAnimating) {
			if (buildingString == null) {
				buildingString = new StringBuffer();
			}

			currentlyAnimating = true;
			buildingString.setLength(text.length());
			for (int i = 0; i < text.length(); ++i) {
				buildingString.setCharAt(i, ' ');
			}
			timeLastAnimated = System.currentTimeMillis();
			animCharacterIterator = 0;

		}
	}

	public void animateLogic() {
		// test if the class is in an animating state
		if (currentlyAnimating && animCharacterIterator < buildingString.length()) {
			long currTime = System.currentTimeMillis();

			// test if there has been enough time to delay the animation
			if (currTime - delayAmountInMiliSec + variation > timeLastAnimated) {
				timeLastAnimated = currTime;
				buildingString.setCharAt(animCharacterIterator, text.charAt(animCharacterIterator));
				animCharacterIterator++;

				if (addVariationToDelay) {
					int halfDelay = (int) (delayAmountInMiliSec * 0.5);

					// add a maximum variation of 50% the total variation
					variation = rng.nextInt(halfDelay + 1);

					// shift so negative numbers are positive
					variation = variation - (halfDelay);
				}
			}

			// test if iterator has reached last character
			if (animCharacterIterator == text.length() - 1) {
				currentlyAnimating = false;
			}
		}
	}

	public boolean isAnimating() {
		return currentlyAnimating;
	}

	public void setAnimationDelayVariation(boolean value) {
		this.addVariationToDelay = value;
		if (!value) {
			variation = 0;
		}
	}

	public void setVisible(boolean inShouldShow)
	{
		bShouldShow  = inShouldShow;
	}
	
	public void setScale(float x, float y) {
		this.scaleX = x != 0 ? x : 1;
		this.scaleY = y != 0 ? y : 1;;
	}

	public void setLeftAlign() {
		horrizontalAlign = Align.LEFT;
	}
	
	public void setRightAlign() {
		horrizontalAlign = Align.RIGHT;
	}
	
	public void setCenterAlign() {
		horrizontalAlign = Align.CENTER;
		
	}

	public int length() {
		return text.length();
	}

	public void append(String toAppend) {
		setText(this.getText() + toAppend);
	}
	
	public enum Align{
		LEFT,
		RIGHT,
		CENTER
	}

	public void interpolateTo(float x, float y) {
		//setXY(x, y);
		interpolateToPoint = true;
		this.interpolatePoint.set(x, y);
	}
	
	public void logic() {
		if (interpolateToPoint) {
			float deltaSpeed = interpolateSpeed * Gdx.graphics.getDeltaTime();

			float distance = Vector2.dst(interpolatePoint.x, interpolatePoint.y, getX(), getY());
			if (distance < 0.001f) {
				interpolateToPoint = false;
				return;
			}

			Vector2 direction = DrawableString.utilVec.set(interpolatePoint.x - getX(), interpolatePoint.y - getY());
			direction = direction.nor();
			direction.scl(deltaSpeed);

			if (direction.len() > distance) {
				direction.nor();
				direction.scl(distance);
			}

			setXY(x + direction.x, y + direction.y);
		}
	}

	
	public boolean isInterpolating() {
		return interpolateToPoint;
	}
	
	public void setInterpolateSpeedFactor(float factor) {
		interpolateSpeed = Tools.convertSpeedTo60FPSValue(factor);
	}

	public void matchColor(DrawableString colorSource) {
		bmFont = colorSource.bmFont;
	}

	public void scaleToScreen(boolean scaleToOneFirst) {
		if(scaleToOneFirst) {
			this.setScale(1, 1);
		}
		
		float width = Gdx.graphics.getWidth();
		
		
		//adding constant float to exaggerate true size of text,
		//this should give a little bit extra space on the sides
		float textSize = this.width() + 10f; 
		
		//do nothing if text size
		if(textSize < width) {
			return;
		}
		
		float ratio = width / textSize;
		this.setScale(ratio, ratio);
	}

}
