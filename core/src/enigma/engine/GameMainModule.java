package enigma.engine;

import com.badlogic.gdx.graphics.OrthographicCamera;

import enigma.engine.baseconversion.BaseConversionModule;
import enigma.engine.baseconversion.FractionalBinaryModule;
import enigma.engine.baseconversion.IEEEFloat16Converter;
import enigma.engine.basicmath.LongDivisionModule;
import enigma.engine.basicmath.MultiplicationModule;
import enigma.engine.fsm.MasterFSMModule;
import enigma.engine.regex.MasterRegularExpressionModule;
import enigma.engine.sorting.QSortInstructionModule;
import enigma.engine.sorting.QSortPracticeModule;
import enigma.engine.sorting.SelectSortInstructionModule;

//@formatter:off
enum module
{
	REGEX,
	FSM,
	FLOAT_POINT,
	QSORT_INSTRUCTION,
	QSORT_PRACTICE,
	INSERT_SORT_INSTRUCTION,
	INSERT_SORT_PRACTICE,
	SELECTION_SORT_INSTRUCTION,
	SELECTION_SORT_PRACTICE,
	BASE_CONVERSION,
	LONG_DIVISION_REMAINDER,
	MULTIPLICATION,
	BINARY_RADEX,
	BINARY	
}
//@formatter:on

public class GameMainModule extends CourseModule {

	public static final module compileTimeModuleSetting = module.FLOAT_POINT;
	
	public GameMainModule(OrthographicCamera camera) {
		super(camera);

		switch (GameMainModule.compileTimeModuleSetting) {
		case REGEX:
			subModules.add(new MasterRegularExpressionModule(camera)); //regular expression module
			break;
		case FSM:
			subModules.add(new MasterFSMModule(camera));
			break;
		case FLOAT_POINT:
			//subModules.add(new MasterFloatingPointModule(camera));
			subModules.add(new IEEEFloat16Converter(camera));
			break;
		case QSORT_INSTRUCTION:
			subModules.add(new QSortInstructionModule(camera));
			break;
		case QSORT_PRACTICE:
			subModules.add(new QSortPracticeModule(camera));
			break;
		case SELECTION_SORT_INSTRUCTION:
			subModules.add(new SelectSortInstructionModule(camera));
			break;
		case SELECTION_SORT_PRACTICE:

			break;
		case INSERT_SORT_INSTRUCTION:

			break;
		case BASE_CONVERSION:
			subModules.add(new BaseConversionModule(camera));
			break;
		case LONG_DIVISION_REMAINDER:
			subModules.add(new LongDivisionModule(camera));
			break;
		case MULTIPLICATION:
			subModules.add(new MultiplicationModule(camera));
			break;	
		case INSERT_SORT_PRACTICE:
			break;
		case BINARY:
			
			break;
		case BINARY_RADEX:
			subModules.add(new FractionalBinaryModule(camera));
			break;
		default:
			break;
		}
		

		// load the first module
		loadCurrentModule();
	}

	@Override
	public void logic() {
		super.logic();
	}
}


