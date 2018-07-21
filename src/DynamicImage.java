import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/*	En Drawable klass som representerar en serie med bilder som går att rita.
 * 	Ett användningsområde för denna klass skulle kunna vara i tex animation.
 * 	Klassen är tänkt att användas som en överklass
 */


public class DynamicImage extends Drawable {
	protected BufferedImage[] image;	// lista med alla bilder
	protected int currentImage;			// den nuvarande bilden som ritas när draw() anropas
	protected int[] ofsetX;				// listor med bildernas individuella offset
	protected int[] ofsetY;
	
	public DynamicImage(int numberOfPictures) {
		super();
		image = new BufferedImage[numberOfPictures];
		ofsetX = new int[numberOfPictures];
		ofsetY = new int[numberOfPictures];
		currentImage = 0;
	}
	
	public void setImages(BufferedImage[] img) {image = img;}
	public void setImage(BufferedImage img, int i) {image[i] = img;}
	public void setOfsetX(int picture, int ofset) {ofsetX[picture] = ofset; }
	public void setOfsetY(int picture, int ofset) {ofsetY[picture] = ofset; }
	public void setOfset(int picture, int ofsetX, int ofsetY) {
		this.ofsetX[picture] = ofsetX;
		this.ofsetY[picture] = ofsetY;
	}
	
	public BufferedImage getImage(int i) {return image[i];}
	public BufferedImage[] getImages() {return image;}
	
	public void setCurrentImage(int i) {currentImage = i;}
	
	
	@Override protected void paint(Graphics2D g, int x, int y) {
		g.drawImage(image[currentImage], x+ofsetX[currentImage], y+ofsetY[currentImage], null);		
	}

	
	@Override protected void logic() {}


}
