package enigma.engine.baseconversion;

import enigma.engine.DrawableCharBuffer;
import enigma.engine.DrawableString;

public class TwosComplement{
	enum State {INVERT_BITS, ADD_ONE, DONE}
	private State state = State.INVERT_BITS; 
	
	private DrawableCharBuffer source;
	private DrawableCharBuffer converted;
	private int number;

	private float x;

	private float y;

	private BinaryAdder addition;

	public TwosComplement(DrawableCharBuffer original, int number) {
		this.source = original;
		this.number = number;
	}
	
	public void nextConvertStep() {
		if(source.getCharAt(0) == '-') {
			if(converted == null) {
				createCopy();
				return;
			} 
			if(state == State.INVERT_BITS) {
				for(int i = 0; i < converted.length(); ++i) {
					DrawableString ch = converted.getCharObjectAt(i);
					if(ch.getText().equals("1")) {
						ch.setText("0");
					} else if (ch.getText().equals("0")) {
						ch.setText("1");
					}
				}
				state = State.ADD_ONE;
			} else if (state == State.ADD_ONE) {
				if(addition == null) {
					addition = new BinaryAdder(number, 1, this.x, this.y, true);
				} else if (!addition.isDone()){
					addition.IO();
				} else {
					state = State.DONE;
				}
			}
			
		} else {
			//there's nothing to be done, the value is converted.
		}
	}

	private void createCopy() {
		String srcConv = "";
		for(int i = 0; i < source.length(); ++i) {
			char val = source.getCharAt(i);
			char copy;
			if(val != '-') {
				copy = val;
			} else {
				copy = ' ';
			}
			srcConv += copy;
		}
		converted = new DrawableCharBuffer(srcConv);
	}
	
	public boolean isDone() {
		return state == State.DONE;
	}
	
}
