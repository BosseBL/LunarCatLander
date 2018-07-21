import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

/*
 * 	en Drawable klass som hanterar ritbar text. Den håller i textens typ, färg och texten som ska ritas.
 * 	Anledningen till att jag skapade klassen var för att jag ville att ritningen av texten skulle
 * 	kunna läsa radbyten.
 * 
 */

public class Text extends Drawable{

	protected String textToDraw;
	protected Color color;
	protected Font font;
	
	public Text() {
		textToDraw = "";
		color = Color.green;;
		font = new Font(null, Font.BOLD, 20);
	}
	public Text(String str, Color c, Font f) {
		textToDraw = str;
		color = c;
		font = f;
	}
	public Text(String str, Color c, int fontStyle, int size) {
		textToDraw = str;
		color = c;
		font = new Font(null, fontStyle, size);
	}
	

	public void setText(String str) {textToDraw = str;}
	public void setColor(Color c) {color = c;}
	public void setFont(Font f) {font = f;}
	
	public Font getFont() {return font;}
	public Color getColor() {return color;}
	
	
	// ritar texten inklusive radbyten
	@Override protected void paint(Graphics2D g, int x, int y) {
		try {
			Scanner s = new Scanner(new ByteArrayInputStream(textToDraw.getBytes("UTF-8") ) );
			g.setColor(color);
			g.setFont(font);
			int i = 0;
			while(s.hasNext()) {
				g.drawString(s.nextLine(), x, y+i*font.getSize());
				i++;
			}
		} catch (UnsupportedEncodingException e) {
			System.out.println("Exception caught in Text.paint(): " + e.toString());
		}		
	}
	
	@Override protected void logic() {}
}
