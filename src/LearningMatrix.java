import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;
import java.awt.Graphics2D;
import java.lang.Math;

/*	en Drawable klass som representerar hj�rn aktiviteten hos LandingObjekt. Denna klass blev en drawable
 * 	n�r jag ins�g att jag ville kunna rita hj�rnaktiviteten.
 * 
 * 	Den h�ller i en 3D matris med inl�rningen och en massa andra variabler och har den viktiga funktionen att
 * 	ta beslut basserat p� br�nsleniv�, fallhastighet och h�jd
 */

public final class LearningMatrix extends Drawable{
	
	// en innre klass som anv�nds f�r att lagra positionen till en specifik cell i 3D matrisen samt ett beslut
	private class Index {
		public int height, fuel, velocity;		// index f�r 3D matrisen
		public boolean action;					// ett beslut
		
		public Index() {
			height = 0;
			fuel = 0;
			velocity = 0;
			action = false;
		}
		
		public Index(int h, int f, int v, boolean a) {
			height = h;
			fuel = f;
			velocity = v;
			action = a;
		}

		public void setIndex(int height, int fuel, int velocity, boolean action) {
			this.height = height;
			this.fuel = fuel;
			this.velocity = velocity;
			this.action = action;
		}
			 
	}
	
	// en till innre klass som representerar en cell i 3D matrisen. Den h�ller enbart 2 r�knare som utg�r cellens historik
	private class Feedback {
		public int on, off;
		public Feedback() {
			on = 5;
			off = 5;
		}
	}
	
	// de maximala v�rdena. Dessa anv�nds f�r att ta fram ett index i 3D matrisen tillsammans med de aktuella v�rdena
	// f�r h�jd, br�nsle och hastighet
	private float maxHeight, maxFuel, maxVelocity;	
	// 3D matrisen som lagrar all historik om beslut. Cellerna best�r av Feedback objekt
	private Feedback[][][] feedbackMatrix;	
	// Den nuvarande cellen basserat p� aktuella v�rden
	private Index currentCell;
	// lista med information om vilka celler som har blivit bes�kta under en omg�ng och vilket beslut som togs
	private ArrayList<Index> visitedCells;
	// 3D matrisens dimension
	private int matrixSize;
	// anv�nds f�r att slumpa fram beslut
	private Random rand;
	
	public LearningMatrix() {}
	
	// initierar alla v�rden basserat p� ing�ngsv�rden, gravitation och matris storlek
	public LearningMatrix(float startHeight, float startFuel, float startVel, float gravity, int matrixArrayLength) {
		if(startVel <= 0) maxHeight = startHeight;					// om hastigheten aldrig �r positiv s� blir maxh�jden starth�jden
		else maxHeight = (float)Math.pow(startVel, 2)/(2*gravity) + startHeight;	// annars s� r�knas maxh�jden fram med lite fysik
		maxFuel = startFuel;									// max br�nsleniv� �r alltid samma som start br�nsleniv�
		maxVelocity = (float)Math.sqrt(2*maxHeight*(-gravity));		// maxhastighet r�knas fram teoretiskt
		
		// skapar en ny 3D matris med den valda storleken
		matrixSize = matrixArrayLength;
		feedbackMatrix = new Feedback[matrixArrayLength][matrixArrayLength][matrixArrayLength];
		
		// initierar matrisens celler
		for(int i = 0; i < matrixArrayLength; i++) {
			for(int j = 0; j < matrixArrayLength; j++) {
				for(int k = 0; k < matrixArrayLength; k++) {
					feedbackMatrix[i][j][k] = new Feedback();
				}}}
		
		// initierar den nuvarande cellen, bes�kta celler listan och rand
		currentCell = new Index();
		visitedCells = new ArrayList<Index>();
		rand = new Random();
	}
	
	// rapporterar resultatet av ett fall och uppdaterar 3D matrisen basserat p� detta
	public void repportResult(boolean result) {
		Feedback feedback = new Feedback();
		Index i = new Index();
		if(result == true) {		// om det var en lyckad landning s� ska alla bes�kta celler uppmuntras
			for(int j = 0; j < visitedCells.size(); j++) {
				i = visitedCells.get(j);
				feedback = feedbackMatrix[i.height][i.fuel][i.velocity];
				for(int k = 0; k < 2 ; k++) {
					if(feedback.on < 9 && i.action ) feedbackMatrix[i.height][i.fuel][i.velocity].on += 1;
					else if(feedback.off < 9 && !i.action) feedbackMatrix[i.height][i.fuel][i.velocity].off += 1;
				}
			}
		}
		else {					// om det var en misslyckad landning s� ska alla bes�kta celler straffas
			for(int j = 0; j < visitedCells.size(); j++) {
				i = visitedCells.get(j);
				feedback = feedbackMatrix[i.height][i.fuel][i.velocity];
				if(feedback.on > 0 && i.action ) feedbackMatrix[i.height][i.fuel][i.velocity].on -= 1;
				else if(feedback.off > 0 && !i.action) feedbackMatrix[i.height][i.fuel][i.velocity].off -= 1;
			}
		}
		visitedCells.clear();		// rensar listan med bes�kta celler f�r en ny omg�ng
	}
	
	
	// tar beslut basserat p� h�jd, br�nsle och hastighet och lagrar detta i bes�kta celler samt returnerar beslutet
	public boolean getDecision(float height, float fuel, float velocity) {
		// tar fram vilket index v�rdena tillh�r. Detta g�rs med h�xkonst och omkastning till int
		int heightCell = (int)((matrixSize-1)*height/(maxHeight));
		int fuelCell = (int)((matrixSize)*fuel/(maxFuel));
		int velocityCell = (int)((matrixSize-1)*(velocity+maxVelocity)/(maxVelocity+10));
		
		// om v�rdena ligger utanf�r 3D matrisens omf�ng s� anv�nds det yttersta indexet f�r motsvarande v�rde
		if(heightCell >= matrixSize) heightCell = matrixSize-1;
		if(heightCell < 0) heightCell = 0;
		if(fuelCell < 0) fuelCell = 0;
		if(fuelCell >= matrixSize) fuelCell = matrixSize-1;
		if(velocityCell >= matrixSize) velocityCell = matrixSize-1;
		if(velocityCell < 0) velocityCell = 0;
		
		// om den aktuella cellen �r samma cell som f�rra g�ngen metoden kallades p� s� ges samma beslut som sist
		if(currentCell.height==heightCell&&currentCell.fuel==fuelCell&&currentCell.velocity==velocityCell ) {
			return currentCell.action;
		}
		else {		// annars s� tas ett nytt beslut och detta beslut stoppas i bes�kta celler
			Feedback feedback = feedbackMatrix[heightCell][fuelCell][velocityCell]; // h�mtar feedback fr�n denna cell
			boolean decision;
			// tar ett beslut basserat p� feedback fr�n cellen
			if(feedback.on > feedback.off) decision = true;			
			else if(feedback.on < feedback.off) decision = false;
			// om ett beslut inte kan tas s� slumpas ett beslut fram
			else decision = rand.nextBoolean();
			// uppdaterar den nuvarande cellen
			currentCell.setIndex(heightCell, fuelCell, velocityCell, decision);
			// l�gger till den nuvarande cellen i bes�kta celler listan
			visitedCells.add(new Index(heightCell, fuelCell, velocityCell, decision));
			return decision;	// returnerar beslutet
		}
		
	}

	// ritar hj�rnaktiviteten
	@Override protected void paint(Graphics2D g, int x, int y) {
		Index i;
		int dy = 1;
		int dx = 0;
		String str;
		
		g.setColor(Color.WHITE);
		g.drawString("Brain activity", super.x, super.y);
		
		for(int j = 0; j < visitedCells.size(); j++) {
			i = visitedCells.get(j);
			if(i.action == true) {
				g.setColor(Color.GREEN);
				str = String.valueOf(feedbackMatrix[i.height][i.fuel][i.velocity].on);
			}
			else {
				g.setColor(Color.RED);
				str = String.valueOf(feedbackMatrix[i.height][i.fuel][i.velocity].off);
			}
			if(feedbackMatrix[i.height][i.fuel][i.velocity].on ==
					feedbackMatrix[i.height][i.fuel][i.velocity].off) {
				str = "X";
			}
			g.drawString(str, super.x+10*dx, super.y+16*dy);
			dx++;
			if(dx >= 10) {
				dx = 0;
				dy++;
			}
		}	
	}

	@Override protected void logic() {}
	
}
