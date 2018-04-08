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
		for(DrawableString charDS : buffer) {
			charDS.logic();
		}
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

	public void setRed(int idx) {
		buffer.get(idx).makeRed();
	}
	public void setBlue(int idx) {
		buffer.get(idx).makeBlue();
	}
	public void setNormalColor(int idx) {
		buffer.get(idx).unhighlight();
	}

	public int size() {
		return buffer.size();
	}
	
	public int length() {
		return size();
	}

	public char getCharAt(int idx) {
		return buffer.get(idx).getText().charAt(0);
	}

	public void setText(String string) {
		//this method is going to be a bit resource wasteful
		buffer.clear();
		for(int i = 0; i < string.length(); ++ i) {
			DrawableString newText = new DrawableString("" + string.charAt(i));
			newText.setScale(scaleX, scaleY);
			buffer.add(newText);
		}
		positionElements();
		
		//this method will need clearing of remainder buffer
		// int i;
		// for(i = 0; i < string.length(); ++i) {
		// if(i < buffer.size()) {
		// buffer.get(i).setText("" + string.charAt(i));
		// } else {
		// buffer.add(new DrawableString("" + string.charAt(i)));
		// }
		// }
	}

	public void append(String string) {
		for(int i = 0; i < string.length(); ++ i) {
			DrawableString charVal = new DrawableString("" + string.charAt(i));
			charVal.setScale(scaleX, scaleY);
			buffer.add(charVal);
		}
		positionElements();
	}
	
	public void preappend(String string) {
		int oldSize = buffer.size();
		for(int i = 0; i < string.length(); ++i){
			//create space for shifting
			buffer.add(null);
		}
		for(int i = buffer.size() - 1; i > oldSize - 1; --i) {
			//shift elements over
			buffer.set(i, buffer.get(i - oldSize));
		}
		
		for(int i = 0; i < string.length(); ++ i) {
			DrawableString charVal = new DrawableString("" + string.charAt(i));
			charVal.setScale(scaleX, scaleY);
			buffer.set(i, charVal);
		}
		positionElements();
	}
	
	public void preappend(DrawableString string) {
		DrawableString previous = string;
		
		buffer.add(null);
		for(int i = 0; i < buffer.size(); ++i) {
			DrawableString newPrevious = buffer.get(i);
			buffer.set(i, previous);
			previous = newPrevious;
		}
		positionElements();
	}

	public DrawableString getCharObjectAt(int i) {
		return buffer.get(i);
	}

	public boolean isInterpolating() {
		for(int i = 0; i < buffer.size(); ++i) {
			DrawableString ch = buffer.get(i);
			if(ch.isInterpolating() || ch.isAnimating()) {
				return true;
			}
		}
		return false;
	}

	public void setCharAt(int index, DrawableString value) {
		buffer.set(index, value);
	}

	public void setCharAt(int index, char ch) {
		buffer.get(index).setText("" + ch);
	}
}
