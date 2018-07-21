import java.lang.Exception;
import javax.swing.JFrame;


/* LunarCat �r huvud klassen f�r programmet och inneh�ller metoden main().
 * LunarCat �rver fr�n Jframe och �r ansvarig f�r programmets f�nstret.
 * LunarCat styr ocks� programmets FPS med ett Timer objekt
 */

public class LunarCat extends JFrame {
	
	Timer fpsTimer; // anv�nds f�r att styra programmets fps
	World world; // Rymmer n�stan hela programmets logik och �r en JPanel
	
	// b�de fpsTimer och world initieras av parametrar i konstruktorn.
	public LunarCat(World worldToUse, int fps) {
		// Den vanliga JFrame och JPanel initieringen. world �r ett JPanel objekt och LunarCat en JFrame klass
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

	
	// metoden som inneh�ller programmets huvudloop och fps kontroll
	private void mainLoop() throws InterruptedException {
		initialize();				// ska g�ra allt som beh�vs g�ras inna loopen k�rs ig�ng. g�r inget f�r tillf�lligt.
		while(!world.hasQuit()) {	// loopen k�rs tills worlds status �r satt till quit
			update();				// Uppdaterar worlds logik. nottera att denna metod begr�nsas inte av fps
			if(fpsTimer.isRenderTime()) { // om fpsTimer anser att det �r dags att rendera en ny ruta s� g�rs detta
				render();
			}
			Thread.sleep(5);	// f�r att processorn inte ska brinna upp. 5 ms �r godtyckligt och lungnar min dators fl�kt.
		}						// Programmets fps b�rjar f�rs�mras om jag g�r �ver 10 ms. man skulle igentligen kunna
	}							// ha ett system som r�knar ut hur mycket s�mn programmet har r�d med. men detta f�r duga.
	
	// Denna metod ska bara existera.
	private void initialize() {}
	
	// uppdaterar worlds logik
	private void update() {
		world.update();
	}
	
	// rensar och ritar om allt i worlds paintComponent metod p� rutan.
	private void render() {
		world.repaint();
	}
	
	private void exit() {
		dispose();
	}

}
