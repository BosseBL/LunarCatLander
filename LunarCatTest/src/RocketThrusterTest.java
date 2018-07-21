/*	en Test klass som ska testa ifall RocketThruster h�ller sin animerade flamma innanf�r dess satta dimension.
 * 	Detta dest �r definierat i metoden boundryTest() som genererar en upps�ttning linjer ifr�n RocketThrust
 * 	och testar ifall de h�ller sig innanf�r gr�nserna.
 * 
 * 	Testet skriver ut resultaten med System.out.println(). just nu s� skriver den ingenting d� ingen linje hamnar utanf�r
 * 
 * 	Jag har modifierat klassen RocketThrust genom att kommentera bort on�digt ineh�ll f�r att slippa inf�ra
 * 	klasser som �r on�diga f�r testet
 * 
 */


public class RocketThrusterTest extends Test {
	RocketThrust rt;
	
	public RocketThrusterTest() {
		rt = new RocketThrust();
		rt.setUpdateActive(true);
	}
	
	// genererar en flamma (slumpm�ssiga linjer) och testar om de h�ller sig innanf�r gr�nsen definierad av
	// positionen och dimensionen
	private void boundryTest() {
		rt.setDimension((int)(200*Math.random()), (int)(200*Math.random()));	// slumpar fram ny dimension
		
		// s�tter position f�r enklare test s� att den �vre och v�nstra gr�nsen �r 0 och h�gra och undre �r width resp. height
		// testet �r till f�r att positionen inte ska bli f�rskjuten -1 till v�nster om width �r udda
		if(rt.getWidth()%2 == 0) rt.setPosition(rt.getWidth()/2, 0);	
		else rt.setPosition(rt.getWidth()/2+1, 0);
		
		rt.update();				// genererar nya linjer
		Line[] lineList = rt.getLineList();	
		Point p = new Point();
		
		// utf�r test f�r varje genererad linje. 
		for(Line l : lineList) {
			// enbart den andra punkten testas d� den f�rsta alltid kommer att vara samma (width/2, 0)
			p = l.getSecondPoint();
			if(p.x < 0)					System.out.println("line outside boundry: p2.x < 0, p2.x = " + p.x + ", widht = " + rt.getWidth());
			if(p.x > rt.getWidth())		System.out.println("line outside boundry: p2.x > width, p2.x = " + p.x + ", widht = " + rt.getWidth()); 
			if(p.y < 0) 				System.out.println("line outside boundry: p2.y < 0, p2.y = " + p.y + ", height = " + rt.getHeight());
			if(p.y > rt.getHeight())	System.out.println("line outside boundry: p2.y > height, p2.y = " + p.y + ", height = " + rt.getHeight());
		}
	}

	
	@Override public void test() {
		boundryTest();
	}

}
