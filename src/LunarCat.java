import java.lang.Exception;
import javax.swing.JFrame;


/* LunarCat är huvud klassen för programmet och innehåller metoden main().
 * LunarCat ärver från Jframe och är ansvarig för programmets fönstret.
 * LunarCat styr också programmets FPS med ett Timer objekt
 */

public class LunarCat extends JFrame {
	
	Timer fpsTimer; // används för att styra programmets fps
	World world; // Rymmer nästan hela programmets logik och är en JPanel
	
	// både fpsTimer och world initieras av parametrar i konstruktorn.
	public LunarCat(World worldToUse, int fps) {
		// Den vanliga JFrame och JPanel initieringen. world är ett JPanel objekt och LunarCat en JFrame klass
		super("LunarCat");
		world = worldToUse;
		super.getContentPane().add(world);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE); 
		pack();
		setVisible(true);
		
		// Definierar fpsTimer och initierar dess fps
		fpsTimer = new Timer();
		fpsTimer.setFps(fps);
	}
	

//Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..
	public static void main(String args[]) {
		try {
			LunarCat lunarcat = new LunarCat(new CatWorld(), ResourceManager.getConfigIntValue("fps"));
			lunarcat.mainLoop(); 		
			lunarcat.exit(); 	
			
		} catch(Exception e) {
			System.out.println("Exception caught in LunarCat.main(): " + e.toString());
		}
	}
//Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..Main..

	
	// metoden som innehåller programmets huvudloop och fps kontroll
	private void mainLoop() throws InterruptedException {
		initialize();				// ska göra allt som behövs göras inna loopen körs igång. gör inget för tillfälligt.
		while(!world.hasQuit()) {	// loopen körs tills worlds status är satt till quit
			update();				// Uppdaterar worlds logik. nottera att denna metod begränsas inte av fps
			if(fpsTimer.isRenderTime()) { // om fpsTimer anser att det är dags att rendera en ny ruta så görs detta
				render();
			}
			Thread.sleep(5);	// för att processorn inte ska brinna upp. 5 ms är godtyckligt och lungnar min dators fläkt.
		}						// Programmets fps börjar försämras om jag går över 10 ms. man skulle igentligen kunna
	}							// ha ett system som räknar ut hur mycket sömn programmet har råd med. men detta får duga.
	
	// Denna metod ska bara existera.
	private void initialize() {}
	
	// uppdaterar worlds logik
	private void update() {
		world.update();
	}
	
	// rensar och ritar om allt i worlds paintComponent metod på rutan.
	private void render() {
		world.repaint();
	}
	
	private void exit() {
		dispose();
	}

}
