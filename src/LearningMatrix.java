import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;
import java.awt.Graphics2D;
import java.lang.Math;

/*	en Drawable klass som representerar hjärn aktiviteten hos LandingObjekt. Denna klass blev en drawable
 * 	när jag insåg att jag ville kunna rita hjärnaktiviteten.
 * 
 * 	Den håller i en 3D matris med inlärningen och en massa andra variabler och har den viktiga funktionen att
 * 	ta beslut basserat på bränslenivå, fallhastighet och höjd
 */

public final class LearningMatrix extends Drawable{
	
	// en innre klass som används för att lagra positionen till en specifik cell i 3D matrisen samt ett beslut
	private class Index {
		public int height, fuel, velocity;		// index för 3D matrisen
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
	
	// en till innre klass som representerar en cell i 3D matrisen. Den håller enbart 2 räknare som utgör cellens historik
	private class Feedback {
		public int on, off;
		public Feedback() {
			on = 5;
			off = 5;
		}
	}
	
	// de maximala värdena. Dessa används för att ta fram ett index i 3D matrisen tillsammans med de aktuella värdena
	// för höjd, bränsle och hastighet
	private float maxHeight, maxFuel, maxVelocity;	
	// 3D matrisen som lagrar all historik om beslut. Cellerna består av Feedback objekt
	private Feedback[][][] feedbackMatrix;	
	// Den nuvarande cellen basserat på aktuella värden
	private Index currentCell;
	// lista med information om vilka celler som har blivit besökta under en omgång och vilket beslut som togs
	private ArrayList<Index> visitedCells;
	// 3D matrisens dimension
	private int matrixSize;
	// används för att slumpa fram beslut
	private Random rand;
	
	public LearningMatrix() {}
	
	// initierar alla värden basserat på ingångsvärden, gravitation och matris storlek
	public LearningMatrix(float startHeight, float startFuel, float startVel, float gravity, int matrixArrayLength) {
		if(startVel <= 0) maxHeight = startHeight;					// om hastigheten aldrig är positiv så blir maxhöjden starthöjden
		else maxHeight = (float)Math.pow(startVel, 2)/(2*gravity) + startHeight;	// annars så räknas maxhöjden fram med lite fysik
		maxFuel = startFuel;									// max bränslenivå är alltid samma som start bränslenivå
		maxVelocity = (float)Math.sqrt(2*maxHeight*(-gravity));		// maxhastighet räknas fram teoretiskt
		
		// skapar en ny 3D matris med den valda storleken
		matrixSize = matrixArrayLength;
		feedbackMatrix = new Feedback[matrixArrayLength][matrixArrayLength][matrixArrayLength];
		
		// initierar matrisens celler
		for(int i = 0; i < matrixArrayLength; i++) {
			for(int j = 0; j < matrixArrayLength; j++) {
				for(int k = 0; k < matrixArrayLength; k++) {
					feedbackMatrix[i][j][k] = new Feedback();
				}}}
		
		// initierar den nuvarande cellen, besökta celler listan och rand
		currentCell = new Index();
		visitedCells = new ArrayList<Index>();
		rand = new Random();
	}
	
	// rapporterar resultatet av ett fall och uppdaterar 3D matrisen basserat på detta
	public void repportResult(boolean result) {
		Feedback feedback = new Feedback();
		Index i = new Index();
		if(result == true) {		// om det var en lyckad landning så ska alla besökta celler uppmuntras
			for(int j = 0; j < visitedCells.size(); j++) {
				i = visitedCells.get(j);
				feedback = feedbackMatrix[i.height][i.fuel][i.velocity];
				for(int k = 0; k < 2 ; k++) {
					if(feedback.on < 9 && i.action ) feedbackMatrix[i.height][i.fuel][i.velocity].on += 1;
					else if(feedback.off < 9 && !i.action) feedbackMatrix[i.height][i.fuel][i.velocity].off += 1;
				}
			}
		}
		else {					// om det var en misslyckad landning så ska alla besökta celler straffas
			for(int j = 0; j < visitedCells.size(); j++) {
				i = visitedCells.get(j);
				feedback = feedbackMatrix[i.height][i.fuel][i.velocity];
				if(feedback.on > 0 && i.action ) feedbackMatrix[i.height][i.fuel][i.velocity].on -= 1;
				else if(feedback.off > 0 && !i.action) feedbackMatrix[i.height][i.fuel][i.velocity].off -= 1;
			}
		}
		visitedCells.clear();		// rensar listan med besökta celler för en ny omgång
	}
	
	
	// tar beslut basserat på höjd, bränsle och hastighet och lagrar detta i besökta celler samt returnerar beslutet
	public boolean getDecision(float height, float fuel, float velocity) {
		// tar fram vilket index värdena tillhör. Detta görs med häxkonst och omkastning till int
		int heightCell = (int)((matrixSize-1)*height/(maxHeight));
		int fuelCell = (int)((matrixSize)*fuel/(maxFuel));
		int velocityCell = (int)((matrixSize-1)*(velocity+maxVelocity)/(maxVelocity+10));
		
		// om värdena ligger utanför 3D matrisens omfång så används det yttersta indexet för motsvarande värde
		if(heightCell >= matrixSize) heightCell = matrixSize-1;
		if(heightCell < 0) heightCell = 0;
		if(fuelCell < 0) fuelCell = 0;
		if(fuelCell >= matrixSize) fuelCell = matrixSize-1;
		if(velocityCell >= matrixSize) velocityCell = matrixSize-1;
		if(velocityCell < 0) velocityCell = 0;
		
		// om den aktuella cellen är samma cell som förra gången metoden kallades på så ges samma beslut som sist
		if(currentCell.height==heightCell&&currentCell.fuel==fuelCell&&currentCell.velocity==velocityCell ) {
			return currentCell.action;
		}
		else {		// annars så tas ett nytt beslut och detta beslut stoppas i besökta celler
			Feedback feedback = feedbackMatrix[heightCell][fuelCell][velocityCell]; // hämtar feedback från denna cell
			boolean decision;
			// tar ett beslut basserat på feedback från cellen
			if(feedback.on > feedback.off) decision = true;			
			else if(feedback.on < feedback.off) decision = false;
			// om ett beslut inte kan tas så slumpas ett beslut fram
			else decision = rand.nextBoolean();
			// uppdaterar den nuvarande cellen
			currentCell.setIndex(heightCell, fuelCell, velocityCell, decision);
			// lägger till den nuvarande cellen i besökta celler listan
			visitedCells.add(new Index(heightCell, fuelCell, velocityCell, decision));
			return decision;	// returnerar beslutet
		}
		
	}

	// ritar hjärnaktiviteten
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
