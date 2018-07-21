import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/*	en Drawable Klass som representerar en bild
 * 	Klassen är tänkt att användas som en överklass 
 */

public class StaticImage extends Drawable {
	
	BufferedImage image;
	
	public StaticImage() {
		super();
		image = null;
	}
	
	public void setImage(BufferedImage img) {image = img;}					// använder refferensen till en annan bild
	public void setCopyOfImage(BufferedImage img) {image = copyImage(img);}		// använder en kopia av en annan bild
	
	public BufferedImage getCopyOfImage() {return copyImage(image);}	// returnerar en koppia av bilden
	public BufferedImage getImage() {return image;}						// returnerar en refferens till bilden

	// kopierar en bild
	private BufferedImage copyImage(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		WritableRaster raster = bi.copyData(null);
		boolean isAlphaPremultiplied = bi.isAlphaPremultiplied();
		return new BufferedImage(cm, raster,isAlphaPremultiplied, null);
	}
	
	// ritar bilden
	@Override protected void paint(Graphics2D g, int x, int y) {
		g.drawImage(image, x, y, null);		
	}

	@Override protected void logic() {}
}
