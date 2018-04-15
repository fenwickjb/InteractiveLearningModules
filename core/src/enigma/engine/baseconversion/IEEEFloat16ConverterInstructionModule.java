package enigma.engine.baseconversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import enigma.engine.CourseModule;

public class IEEEFloat16ConverterInstructionModule extends CourseModule {
	private static final String DRAW = "DRAW!";
	private static final String EVALUATE_TWO_INSTR = "TWO_INSTRUCTIONS!";
	
	enum State { READ_INSTS }
	protected State state = State.READ_INSTS;
	
	protected ArrayList<String> instructions = new ArrayList<String>();
	protected HashSet<String> commands = new HashSet<String>();
	protected IEEEFloat16Converter ieee;
	private boolean allowLogic = true;
	private boolean allowIO = true;
	private boolean allowDraw = true;

	private int index = -1;
	/**
	 * Constructor
	 * 
	 * @param camera
	 *            the Orthographic camera. This is used to convert points
	 */
	public IEEEFloat16ConverterInstructionModule(OrthographicCamera camera) {
		super(camera);
		ieee = new IEEEFloat16Converter(camera);
		generateInstructions();
		generateCommands();
		allowLogic = false;
		allowDraw = false;
		ieee.getInstruction().setText("Welcome! Press Enter to start.");
		ieee.getInstruction().startAnimation();
	}

	private void generateCommands() {
		commands.add(DRAW);
		commands.add(EVALUATE_TWO_INSTR);
		
	}

	private void generateInstructions() {
		instructions.add("This module teaches how IEEE floats are stored in binary.");
		instructions.add("We're going to be working with a 16 bit number...");
		instructions.add("... but the process is the same for 32bit and 64bits.");
		instructions.add(EVALUATE_TWO_INSTR);
		instructions.add(DRAW);
		instructions.add("Let's figure out how to store the number below.");
		instructions.add("The first step is to convert the number to plain old binary.");
		instructions.add("The easiest way to do this, is to separate our number into two parts.");
		instructions.add("The first part will be our whole number portion; ie to the left of the decimal.");
		instructions.add("The second part will be our fractional portion; ie to the right of the decimal.");
		instructions.add("We can convert the whole number part by repeatedly dividing by two!");
		instructions.add("Everytime we get a remainder, the remainder becomes a digit!");
		instructions.add("We then continue by dividing our result by two.");
		instructions.add("Eventually we get zero as a result, and this stops our divisions");
		instructions.add("Note: This produces the binary number in reverse...");
		instructions.add("...so let's write our division from backards (right to left)...");
		instructions.add("... this will make it easier to read once we're done.");
		
		//instructions.add("To convert the fractional po
	}

	@Override
	public void IO() {
		super.IO();
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			switch(state) {
			case READ_INSTS:{
				processNextInstructin();
				break;
			}
			}
			
			
		}
		
		if (allowIO) {
			ieee.IO();
		}
	}

	private void processNextInstructin() {
		//return if complete.
		if(index + 1 == instructions.size()) {
			return;
		}
		
		index = index + 1;
		String inst = instructions.get(index);
		if(!commands.contains(inst)) {
			//this is an instruction, not a command.
			ieee.getInstruction().setText(inst);
			ieee.getInstruction().startAnimation();
		} else {
			runCommand(inst);
		}
				
	}

	private void runCommand(String command) {
		//this would be faster with simulated function-pointers using single-method interfaces, but 
		//since this is is such a simple case, we're going to just do a chain of else if, doing otherwise
		//really is overengineering since this is such a rare case.
		if(DRAW.equals(command)) {
			allowDraw = true;
		}
		if(EVALUATE_TWO_INSTR.equals(command)) {
			processNextInstructin();
			processNextInstructin();
		}
		
	}

	@Override
	public void logic() {
		super.logic();

		ieee.getInstruction().animateLogic();
		ieee.getInstruction().logic();
		
		if(allowLogic) {
			ieee.logic();
		}
	}

	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		ieee.getInstruction().draw(batch);
		
		if(allowDraw) {
			ieee.draw(batch);
		}
	}

}
