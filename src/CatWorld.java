import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;


/*	CatWorld är en underklass till World och JPanel. CatWorld håller i nästan alla objekt som detta 
 * 	program består av. Klassen är ansvarig för att uppdatera alla objekt, rendera alla objekt, 
 * 	hantera inmatning från användaren och hantera programmets tillstånd. Detta görs genom 3 metoder. 
 * 	Klassen har bara 3 metoder:	paintComponent() som renderar, update() som updaterar 
 * 	och initiate() som initierar det nuvarande tillståndet.
 */
public final class CatWorld extends World{
	
	/*	Skapar en enum med programmets olika tillstånd i någorlunda kronologisk ordning. 
	 * 	Dessa används sedan i (StateManager)stateManager som ärvts av World.
	 */
	private enum CatState implements State {
		INTRO, CATDROPINIT, CATDROP, CSPEED, WAIT2FINISH, LANDERINIT, LANDER, OUTRO, EXIT
	}
	
	/*	Skapar en inre klass för att hantera inmatning. 
	 *  Jag har strävat efter att se till så att inmatningen enbart resulterar i att tillståndet ändras.
	 * 	Sen är det upp till programmets tillstånd hantering att bestämma vad som ska ske.
	 */
	private class InputHandler extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_C && stateManager.getState() == CatState.CATDROP) stateManager.setNextState(CatState.CSPEED);
		}
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_SPACE && stateManager.getState() == CatState.INTRO) stateManager.setNextState(CatState.CATDROPINIT);
			if(e.getKeyCode() == KeyEvent.VK_SPACE && stateManager.getState() == CatState.CATDROP) stateManager.setNextState(CatState.WAIT2FINISH);
			if(e.getKeyCode() == KeyEvent.VK_SPACE && stateManager.getState() == CatState.OUTRO) stateManager.setNextState(CatState.EXIT);
			if(e.getKeyCode() == KeyEvent.VK_SPACE && stateManager.getState() == CatState.LANDERINIT) stateManager.setNextState(CatState.LANDER);
			if(e.getKeyCode() == KeyEvent.VK_C && stateManager.getState() == CatState.CSPEED) stateManager.setNextState(CatState.CATDROP);
		}
	}
	
	
	// ---------------Deklatation---------------------------
	
	/*	Dessa är alla objekt som ärvt från Drawable och som kan rita på skärmen.
	 * 	De utgör allt som kommer att synas i program fönstret.
	 */
	private StaticImage background;	// representerar bakgrunds bilden
	private Displayer catCounter ,catSuccessCounter, catCrashCounter, fuelEmptyCounter; // representerar mätare
	private Displayer altimeter, fuelmeter, velometer;	// representerar änu flera mätare
	private Displayer textScreen;	// representerar introduktions och avslutnings rutan med text
	private Text instructions;	// representerar text med instruktioner för användaren
	private Text sign1, sign2, sign3;	// representerar '=' och '+' tecknen mellan några av mätarna
	private Grapher diagram;	// representerar en graf som plottar medelvärdet av antalet lyckade landningar
	 
	private LandingObject cat;	// representerar katterna och landaren
	
	private Timer fpsMeter; // Då jag vill kunna rita den verkliga FPS:en på skärmen så behöver jag en Timer
	private Text fpsText; // representerar fps mätaren
	
	
	//---------------Konstruktor-----------------------------
	
	public CatWorld() {
		super();	// initierar (StateManager)stateManager och (boolean)quit
		try {
			fpsMeter = new Timer();
			
			cat = new LandingObject();
			
			super.setFocusable(true);	// ser till så att programfönstret kan få fokus och lyssna på inmatning
			super.addKeyListener(new InputHandler());	// lägger till inmatningshanteraren
			
			// sätter nästa tillstånd till INTRO (nuvarande är fortfarande null)
			super.stateManager.setNextState(CatState.INTRO);
			
			// hämtar programfönstrets dimension från config.txt filen 		
			// och sätter programfönstrettill denna storlek
			int frameWidth = ResourceManager.getConfigIntValue("frameWidth");
			int frameHeight = ResourceManager.getConfigIntValue("frameHeight");
			super.setPreferredSize(new Dimension(frameWidth, frameHeight));
			
			// skapar bakgrunds bilden
			background = new StaticImage();
			background.setImage(ResourceManager.getImage("background"));
			
			
			// hämtar dimensionen för alla mätare i programmet.
			int displayerWidth = ResourceManager.getConfigIntValue("displayerWidth");
			int displayerHeight = ResourceManager.getConfigIntValue("displayerHeight");
			
			// skapar och initierar alla mätare och displayers
			
			catCounter = new Displayer();
			int catCounterX = ResourceManager.getConfigIntValue("catCounterX");
			int catCounterY = ResourceManager.getConfigIntValue("catCounterY");
			catCounter.setPosition(catCounterX, catCounterY);
			catCounter.setDimension(displayerWidth, displayerHeight);
			catCounter.centerText();
			catCounter.setLabel("Total cats");
			
			altimeter = new Displayer();
			int altimeterX = ResourceManager.getConfigIntValue("altimeterX");
			int altimeterY = ResourceManager.getConfigIntValue("altimeterY");
			altimeter.setPosition(altimeterX, altimeterY);
			altimeter.setDimension(displayerWidth, displayerHeight);
			altimeter.centerText();
			altimeter.setLabel("Altitude");
			
			velometer = new Displayer();
			int velometerX = ResourceManager.getConfigIntValue("velometerX");
			int velometerY = ResourceManager.getConfigIntValue("velometerY");
			velometer.setPosition(velometerX, velometerY);
			velometer.setDimension(displayerWidth, displayerHeight);
			velometer.centerText();
			velometer.setLabel("Velocity");
			
			fuelmeter = new Displayer();
			int fuelmeterX = ResourceManager.getConfigIntValue("fuelmeterX");
			int fuelmeterY = ResourceManager.getConfigIntValue("fuelmeterY");
			fuelmeter.setPosition(fuelmeterX, fuelmeterY);
			fuelmeter.setDimension(displayerWidth, displayerHeight);
			fuelmeter.centerText();
			fuelmeter.setLabel("Fuel");
			
			catSuccessCounter = new Displayer();
			int catSuccessX = ResourceManager.getConfigIntValue("catSuccessX");
			int catSuccessY = ResourceManager.getConfigIntValue("catSuccessY");
			catSuccessCounter.setPosition(catSuccessX, catSuccessY);
			catSuccessCounter.setDimension(displayerWidth, displayerHeight);
			catSuccessCounter.centerText();
			catSuccessCounter.setLabel("Win");
			
			catCrashCounter = new Displayer();
			int catCrashX = ResourceManager.getConfigIntValue("catCrashX");
			int catCrashY = ResourceManager.getConfigIntValue("catCrashY");
			catCrashCounter.setPosition(catCrashX, catCrashY);
			catCrashCounter.setDimension(displayerWidth, displayerHeight);
			catCrashCounter.centerText();
			catCrashCounter.setLabel("Crash");
			
			fuelEmptyCounter = new Displayer();
			int fuelEmptyX = ResourceManager.getConfigIntValue("fuelEmptyX");
			int fuelEmptyY = ResourceManager.getConfigIntValue("fuelEmptyY");
			fuelEmptyCounter.setPosition(fuelEmptyX, fuelEmptyY);
			fuelEmptyCounter.setDimension(displayerWidth, displayerHeight);
			fuelEmptyCounter.centerText();
			fuelEmptyCounter.setLabel("Empty tank");
			
			textScreen = new Displayer();
			int textScreenWidth = ResourceManager.getConfigIntValue("frameWidth") - 100;
			int textScreenHeight = ResourceManager.getConfigIntValue("frameHeight") - 100;
			textScreen.setPosition(50, 50);
			textScreen.setDimension(textScreenWidth, textScreenHeight);
			textScreen.setOfset(10, 30);
			textScreen.setTextFont(new Font(null, Font.ITALIC, 20));
			textScreen.setTextColor(Color.WHITE);
			
			
			// skapar och initierar alla objekt som ritar självständig text
			
			instructions = new Text("", Color.WHITE, Font.ITALIC, 16);
			int instructionsX = ResourceManager.getConfigIntValue("instructionsX");
			int instructionsY = ResourceManager.getConfigIntValue("instructionsY");
			instructions.setPosition(instructionsX, instructionsY);
			
			sign1 = new Text("=", Color.WHITE, Font.BOLD, 20);
			sign1.setPosition(116, 90);
			sign2 = new Text("+", Color.WHITE, Font.BOLD, 20);
			sign2.setPosition(244, 90);
			sign3 = new Text("+", Color.WHITE, Font.BOLD, 20);
			sign3.setPosition(374, 90);
			
			fpsText = new Text("", Color.WHITE, Font.ITALIC, 15);
			fpsText.setPosition(5, 15);
	
			// skapar och initierar grafritaren
			diagram = new Grapher();
			int diagramX = ResourceManager.getConfigIntValue("diagramX");
			int diagramY = ResourceManager.getConfigIntValue("diagramY");
			int diagramWidth = ResourceManager.getConfigIntValue("diagramWidth");
			int diagramHeight = ResourceManager.getConfigIntValue("diagramHeight");
			diagram.setPosition(diagramX, diagramY);
			diagram.setDimension(diagramWidth, diagramHeight);
			diagram.fitFor(20, 100);
			diagram.setLabel("Success rate");
		} catch(Exception e) {
			System.out.println("Exception caught in CatWorld.CatWorld(): " + e.toString());
			System.exit(1);
		}
			
			
	}
	
	
	//-----------------Metoder------------------------------

	
	/*	metoden update() sköter updateringen av instansens logik. Tanken är att denna metod ska  
	 * 	användas oberoende av fps och göra saker som inte får påverkas av skiftande fps så som 
	 * 	t.ex. den simulerade dynamiken som (LandingObject)cat utför.
	 */
	@Override public void update() {
		super.update();	// ser till så att nästa tillstånd initieras om det är ett nytt tillstånd
		State state = stateManager.getState();
		
		cat.update();	
		
		if(cat.isDone()) {
			// lägger till nästa värde som ska plottas i grafen
			diagram.addData((int)(100*((float)cat.getTotalWins()/(float)cat.getTotalCats()))); 
			
			// gör tillstånds beroende uppdateringar.
			switch((CatState)state) {
				case WAIT2FINISH:
					cat.setRenderActive(false);
					stateManager.setNextState(CatState.LANDERINIT);
					break;
				case LANDER:
					stateManager.setNextState(CatState.OUTRO);
					break;
				case CATDROP: case CSPEED:
					cat.throwNewCat();
					break;
				default:
					break;
			}
		}
	}
	
	/*	Denna metod utför vissa uppdateringar som enbart är nödvändiga innan rendering, samt kallar på alla
	 * 	Drawable objektens draw() metod. observera att vilka objekt som kommer synas beror på
	 * om de är renderActive eller inte. oavsätt om alla objektens draw metod anropas så kommer inte alla att renderas.
	 */

	@Override public void paintComponent(Graphics g) {
		
		// typomvandlar (Graphics)g till (Graphics2D)g för att kunna använda antialiasing
		// och för att alla Drawable objekt kräver det.
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		super.paintComponent(g2);	// rensar rutan
		
		// uppdaterar all text på de olika mätarna
		altimeter.setText(String.valueOf((int)cat.getAltitude()));
		fuelmeter.setText(String.valueOf((int)cat.getFuel()));
		velometer.setText(String.valueOf((int)cat.getVelocity()));
		catSuccessCounter.setText(String.valueOf(cat.getTotalWins()));
		catCrashCounter.setText(String.valueOf(cat.getTotalCrashes()));
		fuelEmptyCounter.setText(String.valueOf(cat.getTotalFuelEmpty()));
		catCounter.setText(String.valueOf(cat.getTotalCats()));
		
		fpsMeter.update();
		fpsText.setText("fps: " + String.valueOf(fpsMeter.getRealFps()));
		
		// ritar bakgrunds bilden
		background.draw(g2);
				
		// anropar alla objektens draw metoder.
		diagram.draw(g2);
		catCounter.draw(g2);
		altimeter.draw(g2);
		fuelmeter.draw(g2);
		velometer.draw(g2);
		catSuccessCounter.draw(g2);
		catCrashCounter.draw(g2);
		fuelEmptyCounter.draw(g2);
		instructions.draw(g2);
		sign1.draw(g2);
		sign2.draw(g2);
		sign3.draw(g2);
		fpsText.draw(g2);
		// räknar ut den riktiga fps:en och skriver ut den på skärmen
		
		cat.draw(g2);
		
		textScreen.draw(g2);
	}
	
	
	// varje gång programmets tillstånd ändras så kommer denna metod att anropas. Denna metod ser till att
	// alla nödvändiga ändringar sker inför det nya tillståndet.
	@Override public void initiate() {
		try { 
			State state = stateManager.getState();
			switch((CatState)state) {
				case INTRO: 
					background.setRenderActive(true);
					
					altimeter.setRenderActive(true);
					velometer.setRenderActive(true);
					fuelmeter.setRenderActive(true);
					
					catCounter.setRenderActive(true);
					catSuccessCounter.setRenderActive(true);
					catCrashCounter.setRenderActive(true);
					fuelEmptyCounter.setRenderActive(true);
					
					textScreen.setText(ResourceManager.getText("introText"));	// hämtar intro text
					textScreen.setRenderActive(true);
					
					instructions.setText(ResourceManager.getText("instructions1"));	// hämtar instruktions text
					instructions.setRenderActive(true);
	
					sign1.setRenderActive(true);
					sign2.setRenderActive(true);
					sign3.setRenderActive(true);
					
					fpsText.setRenderActive(true);
					
					diagram.setRenderActive(true);
					
					cat.setPosition(650, 0);				// sätter startposition för katten
					cat.getBrain().setPosition(780, 30);	// sätter position för "Brain activity"
					cat.getBrain().setRenderActive(true);	// och gör denna renderbar.
					
					break;
				case CATDROPINIT:
					textScreen.setRenderActive(false);
					cat.throwNewCat();
					stateManager.setNextState(CatState.CATDROP);
					break;
				case CATDROP:
					timeWarp(false);
					break;
				case CSPEED:
					timeWarp(true);
					break;
				case WAIT2FINISH:
					diagram.setRenderActive(false);
					catCounter.setRenderActive(false);
					altimeter.setRenderActive(false);
					fuelmeter.setRenderActive(false);
					velometer.setRenderActive(false);
					catSuccessCounter.setRenderActive(false);
					catCrashCounter.setRenderActive(false);
					fuelEmptyCounter.setRenderActive(false);
					instructions.setRenderActive(false);
					sign1.setRenderActive(false);
					sign2.setRenderActive(false);
					sign3.setRenderActive(false);
					cat.getBrain().setRenderActive(false);
					break;
				case LANDERINIT:
					// byter ut katt bilderna mot månlandar bilder och gör nödvändiga justeringar i orientering
					BufferedImage lander = ResourceManager.getImage("lander");
					BufferedImage explosion = ResourceManager.getImage("explosion");
					BufferedImage[] landerImages = {lander, lander, explosion};
					cat.setImages(landerImages);
					cat.setOfset(0, -50, -20);
					cat.setOfset(1, -50, -20);
					cat.setOfset(2, -92, -115);
					cat.setThrusterSeparation(20);
					instructions.setText(ResourceManager.getText("instructions2"));		// byter instruktions texten
					instructions.setRenderActive(true);
					cat.setGroundHitSound(ResourceManager.getAudio("explosion"));	// byter ut crash ljudet
					break;
				case LANDER:
					cat.setRenderActive(true);
					instructions.setRenderActive(false);
					cat.throwNewCat();
					break;
				case OUTRO:
					// beroende på om det slutar lycklig eller inte så ska olika texter visas på slutet
					if(cat.getLastResutl())textScreen.setText(ResourceManager.getText("outroTextWin"));
					else textScreen.setText(ResourceManager.getText("outroTextFail"));
					textScreen.setRenderActive(true);
					break;
				case EXIT:
					super.quit();	// sätter quit flaggan till true
					break;
				default:
					break;
			}
		} catch(Exception e) {
			System.out.println("Exception caught in CatWorld.initiate(): " + e.toString());
			System.exit(1);
		}
	}
	
	
	// ändrar tidens hastighet för (LandingObject)cat genom att sätta objektets tidsfaktorn till 50 eller 5.
	private void timeWarp(boolean b) {
		if(b) cat.setTimeFactor(50);
		else cat.setTimeFactor(5);
	}
	
}

