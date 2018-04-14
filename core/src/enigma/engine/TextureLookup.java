package enigma.engine;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TextureLookup {
	private static boolean blackColorTheme = true;
	private static ArrayList<Texture> allTextures = new ArrayList<Texture>();
	public static Texture kButton;
	public static Texture kButtonPressed;
	public static Texture buttonBlack;
	public static Texture buttonGrey;
	public static Texture lambdaTexture;
	public static Texture arrowUpLarge;
	public static Texture arrowUpSmall;
	public static Texture redX;
	public static Texture greenCheckMark;
	public static Texture sNotationLoop;
	
	public static Texture qsortSwapQmarkIcon;
	public static Texture qsortHidePivotIcon;
	public static Texture qsortRestorePivotIcon;

	public static BitmapFont whiteBMFont;
	public static BitmapFont yellowBMFont;
	public static BitmapFont orangeBMFont;
	public static BitmapFont redBMFont;
	public static BitmapFont blueBMFont;
	public static BitmapFont greenBMFont;
	public static BitmapFont pinkBMFont;
	public static BitmapFont purpleBMFont;
	public static BitmapFont ignoreBmInversionWhite;
	
	public static ShapeRenderer shapeRenderer;
	public static String fontName = "prada.fnt";

	// Warning: be careful when using these colors; properly copy them without changing reference
	public static Color foregroundColor = Color.WHITE;
	public static Color backgroundColor = Color.BLACK;
	private static Color redBlackBg = Color.RED;
	private static Color redWhiteBg = Color.SCARLET;
	private static Color blueBlackBg = Color.BLUE;
	private static Color blueWhiteBg = Color.NAVY;
	private static Color greenBlackBg = Color.GREEN;
	private static Color greenWhiteBg = Color.OLIVE;

	public static void initTextures() {
		// check if textures have already been initialized
		if (allTextures.size() > 0) {
			return;
		}
		
		createTextures();
		createBmFonts();

		// initialize the shape renderer
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setColor(Color.WHITE);
	}
	
	private static void createTextures() {
		kButton = loadTexture("KButton.png");
		kButtonPressed = loadTexture("KButtonPressed.png");
		lambdaTexture = loadTexture("Lambda.png");
		buttonBlack = loadTexture("ButtonBlack.png");
		buttonGrey = loadTexture("ButtonGrey.png");
		arrowUpLarge = loadTexture("UpArrowLargeSolid.png");
		arrowUpSmall = loadTexture("UpArrowSolid.png");
		redX = loadTexture("RedX.png");
		greenCheckMark = loadTexture("greenCheckMark.png");

		qsortHidePivotIcon = loadTexture("HidePivot.png");
		qsortRestorePivotIcon = loadTexture("RestorePivot.png");
		qsortSwapQmarkIcon = loadTexture("SwapQmark.png");
		
		sNotationLoop = loadTexture("SNotation.png");
	}
	
	private static Texture loadTexture(String name) {
		Texture texture = new Texture(Gdx.files.internal(name));
		allTextures.add(texture);
		return texture;
	}

	private static void createBmFonts(){
		// initialize the bit map font
		whiteBMFont = new BitmapFont(Gdx.files.internal(fontName));

		yellowBMFont = new BitmapFont(Gdx.files.internal(fontName));
		yellowBMFont.setColor(Color.YELLOW);
		
		redBMFont = new BitmapFont(Gdx.files.internal(fontName));
		redBMFont.setColor(Color.RED);
		
		blueBMFont = new BitmapFont(Gdx.files.internal(fontName));
		blueBMFont.setColor(Color.BLUE);
		
		greenBMFont = new BitmapFont(Gdx.files.internal(fontName));
		greenBMFont.setColor(Color.GREEN);
		
		pinkBMFont = new BitmapFont(Gdx.files.internal(fontName));
		pinkBMFont.setColor(Color.PINK);
		
		purpleBMFont = new BitmapFont(Gdx.files.internal(fontName));
		purpleBMFont.setColor(Color.PURPLE);
		
		orangeBMFont = new BitmapFont(Gdx.files.internal(fontName));
		orangeBMFont.setColor(Color.ORANGE);
		
		ignoreBmInversionWhite = new BitmapFont(Gdx.files.internal(fontName));
		ignoreBmInversionWhite.setColor(Color.WHITE);
	}

	public static void dispose() {
		for (Texture tex : allTextures) {
			tex.dispose();
		}

		if (whiteBMFont != null) whiteBMFont.dispose();
		if (yellowBMFont != null) whiteBMFont.dispose();
		if (redBMFont != null) redBMFont.dispose();
		if (blueBMFont != null) blueBMFont.dispose();
		if (shapeRenderer != null) shapeRenderer.dispose();
	}

	public static void swapColorScheme() {
		Color temp = backgroundColor;
		backgroundColor = foregroundColor;
		foregroundColor = temp;
		swapWhiteFont();
		
		//swap theme flag
		blackColorTheme = !blackColorTheme;
	}
	
	private static boolean whiteFont = true;
	private static void swapWhiteFont(){
		if(whiteFont){
			whiteFont = false;
			whiteBMFont.setColor(Color.BLACK);			
		}else {
			whiteFont = true;
			whiteBMFont.setColor(Color.WHITE);
		}
	}

	public static Color getRedColor() {
		if(blackColorTheme) {
			return redBlackBg;
		}else {
			return redWhiteBg;
		}
	}

	public static Color getBlueColor() {
		if(blackColorTheme) {
			return blueBlackBg;
		}else {
			return blueWhiteBg;
		}
	}
	
	public static Color getGreenColor() {
		if(blackColorTheme) {
			return greenBlackBg;
		}else {
			return greenWhiteBg;
		}
	}
	
}
