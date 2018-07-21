/*	en hjŠlpklass fšr att hŒlla tvŒ heltal som representerar en punkt i ett 2D plan
 */

public class Point {
	public int x, y;
	
	
	public Point(int x, int y) {
		setPoint(x, y);
	}
	public Point() {
		setPoint(0,0);
	}
	
	
	public void setPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setX(int x) {this.x = x;}
	public void setY(int y) {this.y = y;}
	
	public void move(int deltaX, int deltaY) {
		x += deltaX;
		y += deltaY;
	}
	
	public int getX() {return x;}
	public int getY() {return y;}
}
