import java.awt.Graphics;
import javax.swing.JPanel;

/*	en abstrakt klass som representerar en v�rld. CatWorld �rver fr�n denna �verklass. Jag inspererades av
 * 	spelutveckling n�r jag gjorden denna klass. Den l�gger grunden f�r tillst�nds hantering, uppdatering och 
 * 	rendering. den �r dessutom en JPanel. Det enda som saknas �r en �verklass f�r alla v�rldens objekt s� att
 * 	f�rflyttning av objekt fr�n en v�rld till en annan blir m�jlig.
 */

public abstract class World extends JPanel{
	
	private boolean quit;					// en flagga som visar om det �r dags att avsluta eller inte
	
	protected StateManager stateManager;	// en tillst�nds hanterare
	
	protected World() {
		stateManager = new StateManager();
		quit = false;
	}
	
	// uppdaterar v�rldens logik
	protected void update() {
		// Detta ser till s� att initiate() alltid kallas p� d� tillst�ndet har �ndrats
		if(stateManager.updateState()) initiate();
	}
	
	// renderar
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
	
	public boolean hasQuit() {return quit;}	
	
	protected void quit() {quit = true;}
	
	// initierar ett nytt tillst�nd
	protected abstract void initiate();
}

