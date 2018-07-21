import java.awt.Graphics2D;

/* 	En abstrakt �verklass som representerar n�got som g�r att rita p� en 2D yta.
 * 	�r t�nkt att �rvas av alla objekt som ska synas p� sk�rmen i detta program.
 * 	Den inneh�ller variabler f�r position, dimension, offset. det finns ocks� 
 * 	en mekanism f�r att "st�nga av" ett draw objekt f�r tillf�lligt.
 */

public abstract class Drawable {
		protected int x, y, width, height;
		protected int drawOfsetX, drawOfsetY;	//vart paint() ska rita i f�rh�llande till positionen x och y
		
		// dessa variabler best�mmer om metoderna paint() och logic() ska anropas
		// n�r draw() resp. update() anropas.
		private boolean renderActive, updateActive;
		
		public Drawable() {
			setAll(0, 0, 0, 0, 0, 0, false, false);
		}
		
		public Drawable(int x, int y, int width, int height) {
			setAll(x, y, width, height, 0, 0, false, false);
		}
		
		
		// privat metod som enbart anv�nds i konstruktorn. sparade ett par rader kod med denna.
		private void setAll(int x, int y, int width, int height, int ofsetX, int ofsetY, boolean renderActive, boolean updateActive) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.drawOfsetX = ofsetX;
			this.drawOfsetY = ofsetY;
			this.renderActive = renderActive;
			this.updateActive = updateActive;
		}
		
		
		public void setPosition(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		// flyttar positionen deltaX och deltaY pixlar
		public void move(int deltaX, int deltaY) {
			x += deltaX;
			y += deltaY;
		}
		
		// returnerar positionen
		public int getX() {return x;}
		public int getY() {return y;}
		
		public void setDimension(int width, int height) {
			this.width = width;
			this.height = height;
		}
		
		public final int getWidth() {return width;}
		public final int getHeight() {return height;}
		
		public void setDrawOfset(int x, int y) {
			drawOfsetX = x;
			drawOfsetY = y;
		}
		
		public final boolean isRenderActive() {return renderActive;}
		public final boolean isUpdateActive() {return updateActive;}
		
		public final void setRenderActive(boolean b) {renderActive = b;}
		public final void setUpdateActive(boolean b) {updateActive = b;}
		
		
		/*	I den abstrakta metoden paint() f�r anv�ndaren av klassen definiera hur klassen ska 
		 * 	rita. Anropet till paint() sker dock genom den allm�na metoden draw() som i sin tur 
		 * 	kallar p� paint() om renderActive �r satt till true. Samma f�rh�llande har ocks�
		 * 	metoderna logic(), update() och variabeln updateActive.	
		 * 	Metoden draw() ser ocks� till att renderingen automatiskt sker med den offset som objektet har.
		 */
		
		public final void draw(Graphics2D g) {
			if(renderActive) {
				paint(g, x+drawOfsetX, y+drawOfsetY);
			}
		}
		public final void update() {
			if(updateActive) {
				logic();
			}
		}

		protected abstract void paint(Graphics2D g, int x, int y);
		protected abstract void logic();
}
