package enigma.engine.baseconversion;

import enigma.engine.DrawableCharBuffer;
import enigma.engine.DrawableString;

public class ScientificExponentFinder {

	private DrawableCharBuffer buffer;
	//private float value;
	private int radexPosition = -1;
	private boolean done = false;
	private int exponents = 0;
	private boolean exponentIsPositive = true;

	public ScientificExponentFinder(float value, DrawableCharBuffer resultBuffer) {
		//this.value = value;
		this.buffer = resultBuffer;
		for(int i = 0; i < buffer.size(); ++i) {
			if(buffer.getCharAt(i) == '.') {
				radexPosition = i;
				break;
			}
			if(i == buffer.size()-1) throw new RuntimeException("failed to find radex in ScientificExponentFinder");
		}
		
		if(value >= 1) {
			exponentIsPositive = true;
		} else {
			exponentIsPositive = false;
		}
		
	}
	
	public void next() {
		if(radexPosition == -1 || done) {
			return;
		}
		
		if(exponentIsPositive) {
			//value is not purely fractional, this means the notation moves to the left and have positive exponent
			if(radexPosition != 1) { //radex should stop at the position before last (assuming no leading zeros)
				swap(radexPosition, radexPosition -1);
				radexPosition--;
				exponents++;
				
				if(radexPosition == 1)
					done = true;
			}else {
				//check that number isn't completely fractional, if it is not, then the 
				//scientific notation is correct.
				String text = buffer.getText();
				if(text.charAt(0) != '0') {
					//this means that that value is not purely fractional, and no movement is needed in the scientific notation
					done = true;
				}
			}
		} else {
			//value is purely fractional, move to the right, and have negative exponent
			if(radexPosition != buffer.length() - 2) {

				DrawableString afterRadex = buffer.getCharObjectAt(radexPosition + 1);
				swap(radexPosition, radexPosition + 1);
				radexPosition++;
				exponents++;
				if(afterRadex.getText().charAt(0) == '1') {
					done = true;
				}
			}
		}
	}
	
	private void swap(int firstIndex, int secondIndex) {
		DrawableString first= buffer.getCharObjectAt(firstIndex);
		DrawableString second= buffer.getCharObjectAt(secondIndex);
		buffer.setCharAt(firstIndex, second);
		buffer.setCharAt(secondIndex, first);
		
		float fX = first.getX();
		float fY = first.getY();
		float sX = second.getX();
		float sY = second.getY();
		
		second.interpolateTo(fX, fY);
		first.interpolateTo(sX, sY);
	}

	public int getExponent() {
		return exponents;
	}
	
	public boolean exponentIsPositive() {
		return exponentIsPositive;
	}
	
	public boolean isDone() {
		return done;
	}

}
