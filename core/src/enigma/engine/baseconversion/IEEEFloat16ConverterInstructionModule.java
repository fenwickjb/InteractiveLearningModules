package enigma.engine.baseconversion;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import enigma.engine.CourseModule;
import enigma.engine.DrawableCharBuffer;

public class IEEEFloat16ConverterInstructionModule extends CourseModule {
	private static final String READINSTR = "READINSTR";
	private static final String ALLOW_DRAW = "DRAW!";
	private static final String EVALUATE_TWO_INSTR = "TWO_INSTRUCTIONS!";
	private static final String COMPLETE_DIVISION = "DIVISION";
	private static final String COMPLETE_FRACTION = "FRACTION";
	private static final String COMPLETE_SCIENTIFIC = "SCIENTIFIC";
	private static final String MOVE_MANTISSA = "MANTISSA";
	private static final String MOVE_EXPONENT = "MOVE_EXPONENT";
	private static final String ADD_EXPONENT = "ADD_EXPONENT";
	private static final String RESTORE_EXPONENT = "RESTORE_EXPONENT";
	private static final String SIGN_BIT = "SIGN_BIT";
	private static final String SHOW_BITS = "SHOWBITS";

	enum State {
		READ_INSTS, DIVISIONS, MULTIPLICATIONS, SCIENTIFIC, SHOW_BITS, MOVE_MANTISSA, MOVE_EXPONENT, EXPONENT, RESTORE_EXPONENT, SIGN, DONE
	}

	protected State state = State.READ_INSTS;

	protected ArrayList<String> instructions = new ArrayList<String>();
	protected HashSet<String> commands = new HashSet<String>();
	protected IEEEFloat16Converter ieee;
	private boolean allowLogic = true;
	private boolean allowIO = true;
	private boolean allowDraw = true;

	private int index = -1;
	private WholeNumberBinaryConverter wholeConv;
	private FractionalNumberBinaryConverter fracConv;
	private DrawableCharBuffer result;

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
		commands.add(READINSTR);
		commands.add(ALLOW_DRAW);
		commands.add(EVALUATE_TWO_INSTR);
		commands.add(COMPLETE_DIVISION);
		commands.add(COMPLETE_FRACTION);
		commands.add(COMPLETE_SCIENTIFIC);
		commands.add(MOVE_MANTISSA);
		commands.add(MOVE_EXPONENT);
		commands.add(ADD_EXPONENT);
		commands.add(RESTORE_EXPONENT);
		commands.add(SIGN_BIT);
		commands.add(SHOW_BITS);

	}

	private void generateInstructions() {
		instructions.add("This module teaches how IEEE floats are stored in binary.");
		instructions.add("We're going to be working with a 16 bit numbers...");
		instructions.add("... but the process is the same for 32bit and 64bit numbers.");
		instructions.add(EVALUATE_TWO_INSTR);
		instructions.add(ALLOW_DRAW);
		instructions.add("Let's figure out how to store the number below.");
		instructions.add("The first step is to convert the number to plain old binary.");
		instructions.add("The easiest way to do this, is to separate our number into two parts.");
		instructions.add("The first part is the whole number portion(left of the decimal point).");
		instructions.add("The second part is the fractional portion; (right of the decimal point).");

		instructions.add("We can convert the whole number part by repeatedly dividing by 2!");
		instructions.add("Everytime we get a remainder, the remainder becomes a digit!");
		instructions.add("We repeatedly divide our result by two and look at the remainders.");
		instructions.add("Eventually, we get zero as a result (and this stops our divisions)");
		instructions.add("An example will make this clearer, lets work it out.");
		instructions.add("When you see a flashing '|' cursor, type the answer.");
		instructions.add(COMPLETE_DIVISION);
		instructions.add("Fill in the answers, or press enter to see the correct value.");

		instructions.add("Note: each division builds the binary number in reverse...");
		instructions.add("...so, let's write our divisions backards (right to left)...");
		instructions.add("... this will make it easier to read once we're done.");
		instructions.add("Now, the red remainders represent the whole number portion in binary.");

		instructions.add("Next, we need to convert the numbers to the right of the decimal point");
		instructions.add("The fractional portion is converted by a series of multiplications");
		instructions.add("So, instead of dividing, we multiply our fractional portion by 2 ...");
		instructions.add("...the result's *whole* number portion tells us a digit");
		instructions.add("That is, if '0' is to the left of the decimal, then the next binary digit is 0.");
		instructions.add("If a '1' is to the left of the decimal, then the next binary digit is 1!");
		instructions.add("Just like in the previous dividing steps, we keep multiplying to get more digits!");
		instructions.add("One small note though...");
		instructions.add("if we get a 1, then we must subtact 1 before the next multiplication...");
		instructions.add("...don't worry, that will become clear with the example.");
		instructions.add("Now let's get started with the fractional part...");
		instructions.add("Keep multiplying the numbers until we get zero, or run out of space.");
		instructions.add(COMPLETE_FRACTION);

		instructions.add("We have successfully converted the number to binary! But...");
		instructions.add("...the IEEE float specification doesn't store this number directly...");
		instructions.add("... to store the number we need to first convert it to scientific notation.");

		instructions.add("Before I explain this, there is a slight change in language I need to explain.");
		instructions.add("When we're dealing with decimal numbers, we call the '.' a 'decimal point'.");
		instructions.add("When we're dealing with binary numbers, we could the '.' a 'binary point'.");
		instructions.add("In a more general sense, we could just call the '.' a 'radex point'");
		instructions.add("However, I'm going to keep refering to the '.' as a decimal point, regardless of our base.");

		instructions.add("Now then, to convert our number to scientific notation!");
		instructions.add("Just like in decimal scientific notation, we move the decimal point.");
		instructions.add("Normally, every time we move decimal point, we have to multiply by a power of 10.");
		instructions.add("But in binary, we multiply by a power of 2 -- instead of 10.");
		instructions.add(COMPLETE_SCIENTIFIC);
		instructions.add("Press enter move the decimal point, notice the change in power of 2.");

		instructions.add("Now we're almost done, but not quite...");
		instructions.add("...IEEE doesn't directly store the scientific notation...");
		instructions.add("...it stores other data too, such as positive/negative bit and the exponent of 2.");

		instructions.add(SHOW_BITS);
		instructions.add("Now, the IEEE floating point is broken up into 3 pieces.");
		instructions.add("it consists of,  1 sign bit, 5 exponent bits, and 10 Mantissa bits");
		instructions.add("The mantissa is where we store the scientific notation number.");
		instructions.add("The mantissa is the right hand 10 bits, colored purple.");
		instructions.add("Press enter to move our binary number to the final conversion.");
		instructions.add(MOVE_MANTISSA);
		instructions.add("If we have any extra bits, we pad them with zeros.");

		instructions.add("You may have noticed that we did not store the whole number before the decimal.");
		instructions.add("This is actually a space saving mechanism");
		instructions.add("Remember, in binary digits are either 0 and 1...");
		instructions.add("...if digit left of the decimal place were 0, it would be invalid scientific notation...");
		instructions.add("That is, we don't write '0.11 x 2^2', instead we write '1.1 x 2^1'");
		instructions.add("This is because in scientific notation we only have a single digit whole number portion.'");
		instructions.add("So, the leftmost digit must be 1, since there are no other characters in binary.");
		instructions.add("Thus, we can save one bit by not including the first 1 in our conversion!");

		instructions.add("Now, let's store the exponent of the 2");
		instructions.add("Because we can have positive or negative exponents... ");
		instructions.add("...we won't store the exponent directly.");
		instructions.add("Instead of directly using 2s complement, IEEE uses a 'bias' value.");
		instructions.add("This bias value is 01111 for 16 bits.");
		instructions.add("Now, 01111 is basically an unsigned value that represents a 0 exponent.");
		instructions.add("So, if 01111 is in our exponent bits, then we have a 0 exponent");
		instructions.add(MOVE_EXPONENT);
		instructions.add("The next step is to add our exponent to this 01111 bias.");
		instructions.add("As a result, positive exponents add and become larger than the 01111...");
		instructions.add("...and negative exponents add to 01111 and become smaller than the 01111!");
		instructions.add("This is how we can store negative and positive exponents!");
		instructions.add(ADD_EXPONENT);
		instructions.add("Now, add together our exponent with the bias value.");

		instructions.add(RESTORE_EXPONENT);
		instructions.add("The last thing we need to do is store the sign of the converted value.");
		instructions.add("We simply store a 0 for positive numbers, and a 1 for negative numbers.");
		instructions.add("Since our value is positive, the first bit becomes a 0.");
		instructions.add(SIGN_BIT);
		instructions.add("And that is it, we're done converting the number!");

		instructions.add("You can press spacebar at any time to see the available keybinds.");
		instructions.add("At anytime you get stuck, simply press enter to see the next step.");
		instructions.add("You may want to first have the computer walk you through a few.");
		instructions.add("But do try and solve some yourself. :)");
		instructions.add("Press n to randomly generate a new problem for practice.");

	}

	@Override
	public void IO() {
		super.IO();

		if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
			// if they press then, then just give them practice problems.
			state = State.DONE;
			allowDraw = true;
			allowIO = true;
			allowLogic = true;
			ieee.allowNegativeNumbers = true;
			ieee.makeSeedRandom();
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			switch (state) {
			case READ_INSTS: {
				processNextInstruction();
				break;
			}
			default:
				break;
			}
		}
		if (allowIO) {
			ieee.IO();
		}
	}

	private void processNextInstruction() {
		// return if complete.
		if (index + 1 == instructions.size()) {
			return;
		}

		index = index + 1;
		String inst = instructions.get(index);
		if (!commands.contains(inst)) {
			// this is an instruction, not a command.
			ieee.getInstruction().setText(inst);
			ieee.getInstruction().scaleToScreen(true);
			ieee.getInstruction().startAnimation();
		} else {
			runCommand(inst);
		}

	}

	private void runCommand(String command) {
		// this would be faster with simulated function-pointers using single-method
		// interfaces, but
		// since this is is such a simple case, we're going to just do a chain of else
		// if, doing otherwise
		// really is overengineering since this is such a rare case.
		if (ALLOW_DRAW.equals(command)) {
			allowDraw = true;
		} else if (READINSTR.equals(command)) {
			allowIO = false;
			allowLogic = false;
			state = State.READ_INSTS;
		} else if (EVALUATE_TWO_INSTR.equals(command)) {
			processNextInstruction();
			processNextInstruction();
		} else if (COMPLETE_DIVISION.equals(command)) {
			state = State.DIVISIONS;
			processNextInstruction();
			allowIO = true;
			allowLogic = true;
		} else if (COMPLETE_FRACTION.equals(command)) {
			state = State.MULTIPLICATIONS;
			processNextInstruction();
			allowIO = true;
			allowLogic = true;
		} else if (COMPLETE_SCIENTIFIC.equals(command)) {
			state = State.SCIENTIFIC;
			allowIO = true;
			allowLogic = true;
			processNextInstruction();
		} else if (SHOW_BITS.equals(command)) {
			ieee.allowNextStepEnter(true);
			processNextInstruction();
			state = State.SHOW_BITS;
		} else if (MOVE_MANTISSA.equals(command)) {
			state = State.MOVE_MANTISSA;
			ieee.allowNextStepEnter(true);
			processNextInstruction();
		} else if (MOVE_EXPONENT.equals(command)) {
			state = State.MOVE_EXPONENT;
			ieee.allowNextStepEnter(true);
			processNextInstruction();
		} else if (ADD_EXPONENT.equals(command)) {
			ieee.allowExponentCursor();
			allowIO = true;
			state = State.EXPONENT;
			ieee.allowNextStepEnter(true);
			processNextInstruction();
		} else if (RESTORE_EXPONENT.equals(command)) {
			state = State.RESTORE_EXPONENT;
			ieee.allowNextStepEnter(true);
			processNextInstruction();
		} else if (SIGN_BIT.equals(command)) {
			state = State.SIGN;
			ieee.allowNextStepEnter(true);
			processNextInstruction();
		}

	}

	@Override
	public void logic() {
		super.logic();

		ieee.getInstruction().animateLogic();
		ieee.getInstruction().logic();

		if (wholeConv != null) {
			wholeConv.logic();
		}
		if (fracConv != null) {
			fracConv.logic();
		}
		if (result != null) {
			result.logic();
		}

		pollStateChanges();
		if (allowLogic) {
			ieee.logic();
		} else {
			// hack to prevent menu from being shut down.
			ieee.getKeyBindDisplay().logic();
		}

	}

	private void pollStateChanges() {

		// this is definitely atrocious in regards to software engineering...
		// but I don't have much time left and need to get this complete.
		if (state == State.DIVISIONS) {
			if (ieee.isDoneWithDivision()) {
				state = State.READ_INSTS;
				allowIO = false;
				allowLogic = false;

				wholeConv = ieee.getWholeConv();
			}
		} else if (state == State.MULTIPLICATIONS) {
			if (ieee.isDoneWithMultiplications()) {
				state = State.READ_INSTS;
				allowIO = false;
				allowLogic = false;
				fracConv = ieee.getFracConv();

				result = ieee.getResult();
			}
		} else if (state == State.SCIENTIFIC) {
			if (ieee.isDoneWithScientificNotation()) {
				state = State.READ_INSTS;
				// allowIO = false;
				// allowLogic = false;
				ieee.allowNextStepEnter(false);
			}
		} else if (state == State.SHOW_BITS) {
			if (ieee.displayingBits()) {
				state = State.READ_INSTS;
				ieee.allowNextStepEnter(false);
			}
		} else if (state == State.MOVE_MANTISSA) {
			if (ieee.mantisaaDisplaying()) {
				state = State.READ_INSTS;
				ieee.allowNextStepEnter(false);
			}
		} else if (state == State.MOVE_EXPONENT) {
			if (ieee.exponentHasMoved()) {
				state = State.READ_INSTS;
				ieee.allowNextStepEnter(false);
				ieee.disableAdderCursor();
				allowIO = false;
			}
		} else if (state == State.EXPONENT) {
			if (ieee.exponentAddingDone()) {
				state = State.READ_INSTS;
				ieee.allowNextStepEnter(false);
				allowIO = true;
			}
		} else if (state == State.RESTORE_EXPONENT) {
			if (ieee.exponentMovedToBitsAndComplete()) {
				state = State.READ_INSTS;
				ieee.allowNextStepEnter(false);
			}
		} else if (state == State.SIGN) {
			if (ieee.signBitComplete()) {
				state = State.READ_INSTS;
				ieee.allowNextStepEnter(false);
			}
		}

	}

	@Override
	public void draw(SpriteBatch batch) {
		super.draw(batch);
		ieee.getInstruction().draw(batch);

		if (allowDraw) {
			ieee.draw(batch);
		} else {
			ieee.getKeyBindDisplay().draw(batch);
		}
	}

}
