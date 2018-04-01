package enigma.engine.baseconversion;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import enigma.engine.DrawableString;

public class WholeNumberBinaryConverter {
	private ArrayList<LongDivisionEntity> components;
	//private int number;
	private float x = 0;
	private float y = 0;
	private DrawableString exampleDS;
	private float scaleX = 1;
	private float scaleY = 1;
	private boolean complete = false;
	private float shrinkFactor = 0.45f;

	public WholeNumberBinaryConverter(int number) {
		components = new ArrayList<LongDivisionEntity>();
		//this.number = number;
		
		exampleDS = new DrawableString("0");
		components.add(new LongDivisionEntity(number, 2, x, y));
		
		positionElements();
	}

	private void positionElements() {
		scaleElements();
		
		float lastX = x; 
		float lastWidth = 0;
		float spacing = calculateSpacing();
		
		for(int i = 0; i < components.size(); ++i) {
			LongDivisionEntity lde = components.get(i);
			float newX = lastX - lastWidth - ((i == 0) ? 0 : spacing);
			lde.setPosition(newX, y);
			
			lastX = newX;
			lastWidth = lde.getWidth();
		}
	}
	
	private void scaleElements() {
		int size = components.size();
		for(int i = 0; i < size; ++i) {
			if(size - 1 != i) {
				components.get(i).scale(shrinkFactor* scaleX, shrinkFactor* scaleY);
			} else {
				//go ahead and scale element if they're done.
				LongDivisionEntity comp = components.get(i);
				int result = comp.result();
				if(result == 0 && comp.isDone()) {
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
		for(LongDivisionEntity entity : components) {
			entity.logic();
		}
		LongDivisionEntity last = components.get(components.size() - 1);
		if(last.isDone()) {
			int result = last.result();
			if(result == 0) {
				//binary conversion is done. 
			} else {
				//there are still diits to be processed.
				LongDivisionEntity newComp = new LongDivisionEntity(result, 2, x, y);
				components.add(newComp);
				positionElements();
			}
		}
		if(!complete && components.size() > 0) {
			if(last.isDone() && last.result() == 0) {
				positionElements();
				complete = true;
			}
		}
	}
	
	public void draw(SpriteBatch batch) {
		for(LongDivisionEntity entity : components) {
			entity.draw(batch);
		}		
	}

	public void IO() {
		for(LongDivisionEntity entity : components) {
			entity.IO();
		}
	}
	
	public boolean isDone() {
		return complete;
	}
}
