import java.awt.Color;
import java.lang.Math;

/* En hjäpklass som håller två punkter och en färg. Denna klass representerar en linje i ett 2D plan
 */

public class Line {
	
	private Point p1, p2;
	private Color color;
	
	public Line(int x1, int y1, int x2, int y2) {
		p1 = new Point();
		p2 = new Point();
		setLine(x1, y1, x2, y2);
	} 
	public Line(Point a, Point b) {
		setLine(a, b);
	}
	public Line() {
		p1 = new Point();
		p2 = new Point();
	}
	
	public void setLine(int x1, int y1, int x2, int y2) {
		p1.setPoint(x1, y1);
		p2.setPoint(x2, y2);
	}
	public void setLine(Point a, Point b) {
		p1 = a;
		p2 = b;
	}
	
	public void setColor(Color c) {color = c; }
	public Color getColor() {return color; }
	
	public Point getFirstPoint() {return p1;}
	public Point getSecondPoint() {return p2;}
	
	// pythagoras sats
	public double getLength() {
		return Math.sqrt(Math.pow(p2.getX()-p1.getX(), 2) + Math.pow(p2.getY()-p1.getY(), 2));
	}
	
}
