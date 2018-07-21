import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/*	En Drawable klass som representerar en ruta med text inuti och en tillhörande beteckning. 
 * 	Den används för att skapa bl.a. mätarna.
 */

public class Displayer extends Drawable {
	
	private Text textToDraw;	// texten inuti rutan
	private Point textOfset;	// textens placering i rutan
	private Text label;			// betecknings text utanför rutan
	
	
	public Displayer() {
		super();
		textOfset = new Point(3, 14);
		label = new Text("", Color.WHITE, Font.ITALIC, 14);
		textToDraw = new Text();
		textToDraw.setPosition(x+textOfset.x, y+textOfset.y);
		textToDraw.setRenderActive(true);
		label.setRenderActive(true);
	}
	
	public void setText(String text) {textToDraw.setText(text);}
	public void setTextFont(Font f) {textToDraw.setFont(f);}
	public void setLabel(String str) {label.setText(str); }
	public void setTextColor(Color c) {textToDraw.setColor(c); }
	
	// ser till att texten hamnar mitt i rutan (enbart y-led)
	public void centerText() {
		textOfset.setX(3);
		textOfset.setY((height+textToDraw.getFont().getSize())/2);
		textToDraw.setPosition(x+textOfset.getX(), y+textOfset.getY());
	}
	
	public void setOfset(int x, int y) {
		textOfset.setPoint(x, y);
		textToDraw.setPosition(this.x+x, this.y+y);
	}
	
	public void setPosition(int x, int y) {
		super.setPosition(x, y);
		textToDraw.setPosition(x+textOfset.getX(), y+textOfset.getY());
	}


	@Override protected void paint(Graphics2D g, int x, int y) {
		Tools.drawDisplayFrame(x, y, width, height, g);		// ritar en svart ruta med grå ram
		
		// placerar ut beteckningen
		label.setPosition(x, y-5);
		label.draw(g);
		
		// ritar innehållet i rutan
		textToDraw.draw(g);
	}

	@Override protected void logic() {}
}
