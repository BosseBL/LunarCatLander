import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

/*	En Drawable klass som representerar en graf. Klassen ritar en ruta
 * 	och plottar en graf basserat p� en lista med v�rden. 
 */

public final class Grapher extends Drawable{

	private ArrayList<Integer> data;	// lista med v�rdena som ska plottas
	private int scaleY, scaleX;			// x och y skala. �r dessa satt till 1 s� blir en enhet en pixel
	private Text label;				// en beteckning som ritas utanf�r grafrutan. Samma koncept som i klassen Displayer
	
	public Grapher() {
		super();
		setScale(1, 1);
		label = new Text("", Color.WHITE, Font.ITALIC, 14);
		label.setRenderActive(true);
		data = new ArrayList<Integer>();
		data.add(0);
	}
	
	
	public void setScale(int scaleX, int scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	public void setData(ArrayList<Integer> val) {data = val;}
	
	public ArrayList<Integer> getData() {return data;}
	
	// metod som l�gger till en serie v�rde p� slutet av listan data
	public void addData(int[] list) {
		for(int i:list) {
			data.add(i);
		}
	}
	
	// metod som l�gger till ett v�rde p� slutet av listan data
	public void addData(int i) {
		if(data.size() >= width/scaleX && data.size() != 0) data.remove(0);
		data.add(i);
	}
	
	// rensar och l�gger till nya v�rden i listan data
	public void setData(int[] list) {
		data.clear();
		addData(list);
	}
	
	// justerar skala f�r att graf rutan ska kunna rymma en viss v�rdem�ngd och m�ngd v�rden
	public void fitFor(int maxValue, int numberOfValues) {
		scaleX = width/numberOfValues;
		scaleY = height/maxValue;
	}
	
	public void setLabel(String str) {label.setText(str);}

	
	@Override protected void paint(Graphics2D g, int x, int y) {
		
		Tools.drawDisplayFrame(x, y, width, height, g);

		label.setPosition(x, y-5);
		label.draw(g);
		
		//skalar om ifall grafen �r utanf�r
		for(int i = 0; i<data.size(); i++) if(scaleY*data.get(i) > height) scaleY = height/data.get(i);
		
		// s�tter start och slut v�rde f�r x basserat p� grafens bredd och antal v�rden
		int startX, endX;
		if(data.size()*scaleX > width) {
			endX = data.size(); 
			startX = data.size() - width/scaleX;
		}
		else {
			startX = 0;
			endX = data.size();
		}
		
		g.setColor(Color.WHITE);
		//streckar ut var 10:e enhet p� y-axeln
		for(int i = 10; i < height/scaleY; i += 10) {
			g.fillRect(x-1, y+height-i*scaleY-1, 3, 2);
		}
		
		g.setColor(Color.GREEN);
		
		// skalar om y och x efter angiven skala
		int[] yAxel = new int[data.size()];
		for(int i = 0; i < data.size() ; i++) yAxel[i] = data.get(i)*scaleY;
		int[] xAxel = new int[data.size()];
		for(int i = 0 ; i < xAxel.length; i++) xAxel[i] = i*scaleX;
		
		// plottar grafen
		for(int i = 1; i < endX-startX; i++) {
			g.drawLine(x+xAxel[i-1], (y+height)-yAxel[startX + i-1], x+xAxel[i], (y+height)-yAxel[startX + i]);
		}			
	}

	@Override protected void logic() {}
	
}

