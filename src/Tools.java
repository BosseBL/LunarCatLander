import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;


/* En statisk offentlig klass med hj�lpfunktioner som. Detta �r ett s�tt att g�mma undan f�rskr�ckligt jobbig kod att
 * titta p�. funktions orienterad programmering som g�r programmet mer l�ttl�st.
 * 
 */

public class Tools {
	
	// ritar ritar en svart, fylld rektangel med en gr� ram runt
	public static void drawDisplayFrame(int x, int y, int width, int height, Graphics2D g) {
		Color originalColor = g.getColor();
		g.setColor(Color.BLACK);
		g.fillRect(x, y, width, height);
		g.setColor(Color.GRAY);
		g.fillRect(x-3, y, 3, height);
		g.fillRect(x+width, y, 3, height);
		g.fillRect(x-3, y-3, width+6, 3);
		g.fillRect(x-3, y+height, width+6, 3);
		g.setColor(originalColor);
	}
	
	public static void drawLine(Line l, Graphics g) {
		Color originalColor = g.getColor();
		g.setColor(l.getColor());
		int x1 = l.getFirstPoint().getX();
		int y1 = l.getFirstPoint().getY();
		int x2 = l.getSecondPoint().getX();
		int y2 = l.getSecondPoint().getY();
		g.drawLine(x1, y1, x2, y2);
		g.setColor(originalColor);
	}
}
