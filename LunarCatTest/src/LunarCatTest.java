/*	Huvud test programmet
 * 
 */


public class LunarCatTest {

	Test test;		// deklarerar ett nytt test objekt som kan kastas om till ett specifikt test objekt
	
	public LunarCatTest(Test t) {
		test = t;
	}
	
	public static void main(String[] args) {
		// skapar instansierar de b�da test klasserna
		PhysicsTest pt = new PhysicsTest();
		pt.setTimeFactor(5);	// s�tter den tidsfaktorn som PhysicsTest ska anv�nda.
		
		RocketThrusterTest rtt = new RocketThrusterTest();
		
		// initierar LunarCatTest med ett test objekt och k�r sedan dess test
		LunarCatTest lct = new LunarCatTest(pt);	// byt pt mot rtt i konstruktorn f�r att byta test
		lct.runTest();
	}
	
	// denna metod k�r testen
	public void runTest() {
		test.runTest(3);
		System.out.println("\ndone!");
	}

}
