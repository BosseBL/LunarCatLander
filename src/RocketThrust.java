import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

import javax.sound.sampled.Clip;

// En Drawable klass som representerar flamman under raketmotorerna. 
// Denna klass �stadkommer en ruskigt ful animerad flamma som genereras av massor av slumpm�ssiga linjer i r�tt och gult.

public class RocketThrust extends Drawable {

	private int maxLines;		// maximalt antal ritade linjer
	private Line[] lineList;	// lista inneh�llandes linjerna som ska ritas
	private Playable thrusterSound;		// ljudet som raketmotorn ska ge ifr�n sig
	private boolean active;		// om raketmotorn �r p� eller av
	private Timer timer;		// en timer som r�knar tiden sen motorn st�ngdes av eller sattes p�
	
	public RocketThrust() {
		// s�tter width och height till 30 resp 100. Dessa anv�nds f�r att begr�nsa flammans spridning.
		super(0, 0, 60, 100);
		try {
		maxLines = 30;			
		thrusterSound = new Playable(ResourceManager.getAudio("thruster"));		// h�mtar ljudet fr�n ResourceManager
		thrusterSound.setPlayActive(true);
		active = false;
		timer = new Timer();
		timer.update();
		} catch(Exception e) {
			System.out.println("Exception caught in RocketThrust.RocketThrust(): " + e.toString());
		}
	}

	public void setMaxLines(int i) {maxLines = i;}	

	// s�tter p� raket motorn
	public void start() {
		if(!active) {
			super.setRenderActive(true);
			super.setUpdateActive(true);
			thrusterSound.play();
			active = true;
			timer.update();
		}
	}
	
	// st�nger av raketmotorn
	public void stop() {
		if(active) {
			super.setRenderActive(false);
			super.setUpdateActive(false);
			thrusterSound.stop();
			active = false;
			timer.update();
		}
	}
	
	public boolean isActive() {
		return active;
	}
	// till�ter att st�nga av det spelbara ljudet
	public void setPlayable(boolean b) {
		thrusterSound.setPlayActive(b);
	}
	// returnerar tiden sen raketmotorernas status sist �ndrades (on/off)
	public long timeSinceLastThrust() {
		return timer.peakDeltaTime();
	}
	
	// returnerar listan med genererade linjer
	public Line[] getLineList() {return lineList;}

	// ritar alla linjer
	@Override protected void paint(Graphics2D g, int x, int y) {
		logic();
		for(Line line : lineList) {
			Tools.drawLine(line, g);
		}		
	}

	// genererar nya linjer
	@Override protected void logic() {
		Random rand = new Random();
		int lines = (int)(maxLines*rand.nextFloat());
		lineList = new Line[lines];
		for(int i = 0; i < lines; i++) {
			lineList[i] = new Line();
			int y2 = (int)(height*rand.nextDouble());
			int x2 = (int)(width*Math.sin(2*Math.PI*rand.nextDouble())/2);
			lineList[i].setLine(x, y, x+x2, y+y2);
			if(rand.nextBoolean()) lineList[i].setColor(Color.RED);
			else lineList[i].setColor(Color.YELLOW);
		}		
	}
}
