/*	en Test klass som ska testa ifall RocketThruster håller sin animerade flamma innanför dess satta dimension.
 * 	Detta dest är definierat i metoden boundryTest() som genererar en uppsättning linjer ifrån RocketThrust
 * 	och testar ifall de håller sig innanför gränserna.
 * 
 * 	Testet skriver ut resultaten med System.out.println(). just nu så skriver den ingenting då ingen linje hamnar utanför
 * 
 * 	Jag har modifierat klassen RocketThrust genom att kommentera bort onödigt inehåll för att slippa införa
 * 	klasser som är onödiga för testet
 * 
 */


public class RocketThrusterTest extends Test {
	RocketThrust rt;
	
	public RocketThrusterTest() {
		rt = new RocketThrust();
		rt.setUpdateActive(true);
	}
	
	// genererar en flamma (slumpmässiga linjer) och testar om de håller sig innanför gränsen definierad av
	// positionen och dimensionen
	private void boundryTest() {
		rt.setDimension((int)(200*Math.random()), (int)(200*Math.random()));	// slumpar fram ny dimension
		
		// sätter position för enklare test så att den övre och vänstra gränsen är 0 och högra och undre är width resp. height
		// testet är till för att positionen inte ska bli förskjuten -1 till vänster om width är udda
		if(rt.getWidth()%2 == 0) rt.setPosition(rt.getWidth()/2, 0);	
		else rt.setPosition(rt.getWidth()/2+1, 0);
		
		rt.update();				// genererar nya linjer
		Line[] lineList = rt.getLineList();	
		Point p = new Point();
		
		// utför test för varje genererad linje. 
		for(Line l : lineList) {
			// enbart den andra punkten testas då den första alltid kommer att vara samma (width/2, 0)
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
