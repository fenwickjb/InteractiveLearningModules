package enigma.engine;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DrawableCharBuffer {
	public enum Align{
		LEFT,
		RIGHT,
		CENTER
	}
	
	private ArrayList<DrawableString> buffer = new ArrayList<DrawableString>();
	private Align alignment = Align.CENTER;
	private float scaleX;
	private float scaleY;
	private float spacingBetweenChars;
	private DrawableString exampleDS;
	private float x;
	private float y;
	
	public DrawableCharBuffer(String charString) {
		buffer.clear();
		for(int i = 0; i < charString.length(); ++ i) {
			buffer.add(new DrawableString("" + charString.charAt(i)));
		}
		exampleDS = new DrawableString("2");
		
		positionElements();
	}
	
	private void calculateSpacing() {
		spacingBetweenChars = exampleDS.width() * 0.005f;
	}

	public void positionElements() {
		float totalWidth = width();
		float startX;
		
		if(alignment == Align.CENTER) {
			startX = x - totalWidth / 2;
		} else if (alignment == Align.LEFT) {
			startX = x;
		} else{
			startX = x - totalWidth;
		}
		
		for(int i = 0; i < buffer.size(); ++ i) {
			buffer.get(i).setXY(startX, y);
			startX += exampleDS.width() + spacingBetweenChars;
		}
	}

	public void setScale(float x, float y) {
		this.scaleX = x != 0 ? x : 1;
		this.scaleY = y != 0 ? y : 1;;
		exampleDS.setScale(scaleX, scaleY);
		for(int i = 0; i < buffer.size(); ++ i) {
			buffer.get(i).setScale(scaleX, scaleY);
		}
		positionElements();
	}
	
	public void draw(SpriteBatch batch) {
		for(int i = 0; i < buffer.size(); ++ i) {
			buffer.get(i).draw(batch);
		}
	}
	
	public void setLeftAlign() {
		this.alignment = Align.LEFT;
		// for(int i = 0; i < buffer.size(); ++ i) {
		// buffer.get(i).setLeftAlign();;
		// }
		positionElements();
	}
	
	public void setRightAlign() {
		this.alignment = Align.RIGHT;
		// for(int i = 0; i < buffer.size(); ++ i) {
		// //buffer.get(i).setRightAlign();;
		// }
		positionElements();
	}
	
	public void setCenterAlign() {
		this.alignment = Align.CENTER;
		// for(int i = 0; i < buffer.size(); ++ i) {
		// //buffer.get(i).setCenterAlign();;
		// }
		positionElements();
	}
	
	public void setXY(float x, float y) {
		this.x = x;
		this.y = y;
		positionElements();
	}
	
	public float width() {
		calculateSpacing();
		return exampleDS.width() * buffer.size() + spacingBetweenChars * buffer.size() - 1;
	}
	
	public float height() {
		return exampleDS.height();
	}
	
	public void logic() {
		
	}
	
	public String getText() {
		String text = "";
		for(int i = 0; i < buffer.size(); ++ i) {
			text += buffer.get(i).getText();
		}
		return text;
	}
	
	public float getX() { return x; }
	public float getY() { return y; }
	
	public float getCharWidth() {
		return exampleDS.width();
	}
}
