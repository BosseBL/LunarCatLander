import java.awt.Graphics;
import javax.swing.JPanel;

/*	en abstrakt klass som representerar en vŠrld. CatWorld Šrver frŒn denna šverklass. Jag inspererades av
 * 	spelutveckling nŠr jag gjorden denna klass. Den lŠgger grunden fšr tillstŒnds hantering, uppdatering och 
 * 	rendering. den Šr dessutom en JPanel. Det enda som saknas Šr en šverklass fšr alla vŠrldens objekt sŒ att
 * 	fšrflyttning av objekt frŒn en vŠrld till en annan blir mšjlig.
 */

public abstract class World extends JPanel{
	
	private boolean quit;					// en flagga som visar om det Šr dags att avsluta eller inte
	
	protected StateManager stateManager;	// en tillstŒnds hanterare
	
	protected World() {
		stateManager = new StateManager();
		quit = false;
	}
	
	// uppdaterar vŠrldens logik
	protected void update() {
		// Detta ser till sŒ att initiate() alltid kallas pŒ dŒ tillstŒndet har Šndrats
		if(stateManager.updateState()) initiate();
	}
	
	// renderar
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
	
	public boolean hasQuit() {return quit;}	
	
	protected void quit() {quit = true;}
	
	// initierar ett nytt tillstŒnd
	protected abstract void initiate();
}

