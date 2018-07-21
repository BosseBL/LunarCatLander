import java.util.Random;

/*	Test klass som ska testa metoden LandingObject.updatePhysics() och se hur mycket den simulerade
 * 	fysiken skiljer sig fr�n den teoretiska. Klassen ska ocks� testa ifall metodens tv� block (if else) 
 * 	genererar samma fysik d� thrustAcceleration �r satt till 0.
 * 
 * 	Testet begr�nsar sig till att utf�ra endast test med thrustAcceleration = 0.
 * 
 * 	Testet skriver ut resultaten med System.out.println()
 * 
 * 	Jag valde att kopiera metoden och skapa de variabler som fattades ist�llet f�r att f�ra in hela klassen 
 * 	LandingObject, d� detta hade kr�vt extremt mycket modifiering av klassen eller inf�randet av n�stan alla klasser
 * 	i programmet LunarCat.
 */


public class PhysicsTest extends Test{
	
	// samtliga variabler som updatePhysics anv�nder
	float fuel, altitude, velocity, fuelConsumption, gravity, thrustAcceleration, timeFactor;
	Timer timer;
	boolean thrusterActive;
	
	// anv�nds f�r att spara de initiella v�rdena
	float altitude2, gravity2, timeFactor2;
	
	
	public PhysicsTest() {
		// fuel �r satt till 1 och fuelConsumption till 0 f�r att updatePhysics() inte ska
		// �ndra p� thrusterActive
		fuelConsumption = 0;
		fuel = 1;				
		timer = new Timer();
		timeFactor = 20;
	}
	
	
	// metoden som ska testas. Denna metod sk�ter fysiken i LandingObject
	
	private void updatePhysics() {
		float dtime = (float)timer.getDeltaTime()*timeFactor/1000;
		if(thrusterActive) {
			fuel += fuelConsumption*dtime;
			float acceleration = gravity+thrustAcceleration;
			float newVelocity = velocity+dtime*acceleration;
			altitude += (newVelocity+velocity)*dtime/2;
			velocity = newVelocity;
		}
		else {
			float newVelocity = velocity+gravity*dtime;
			altitude += (newVelocity+velocity)*dtime/2;
			velocity = newVelocity;
		}
		if(fuel <= 0) thrusterActive = false;
	}
	
	// initierar alla v�rden
	public void setNewValues() {
		// ser till s� att accelerationen aldrig �r st�rre �n -0.5 och mindre �n -10
		do {
			gravity = (float) (-10*Math.random());
		}
		while(gravity > -1);
		
		thrustAcceleration = 0;
		
		altitude = (float) (50 + 150*Math.random());	// slumpar fram en start h�jd mellan 50 och 200
		velocity = 0;									// start hastighet �r alltid 0 f�r enkelhetens skull
		
		altitude2 = altitude;
		timeFactor2 = timeFactor;
		gravity2 = gravity;
	}
	
	// startar test
	private void throwNew() {
		timer.update();									// startar tiden.
	}
	// startar test med specifika ing�ngsv�rden
	private void throwNew(float altitude, float gravity, float timeFactor) {
		this.gravity = gravity;
		this.altitude = altitude;
		this.timeFactor = timeFactor;
		velocity = 0;
		thrusterActive = false;
		
		timer.update();
	}
	
	// utf�r en fall simulering och returnerar den totala tiden det tagit
	public long simulate() throws InterruptedException {
		long totalTime = 0;
		while(altitude > 0) {
			totalTime += timer.peakDeltaTime()*timeFactor;
			updatePhysics();
			Thread.sleep(7);
		}
		return totalTime;
	}
	
	// utf�r ett konsekvent test
	private void consistentTest() throws InterruptedException {
		setNewValues();
		thrusterActive = true;
		throwNew();
		System.out.println(toString());
		System.out.println("\nThruster active block: time = " + simulate() + ", velocity = " + velocity);
		
		thrusterActive = false;
		throwNew(altitude2, gravity2, timeFactor2);
		System.out.println("Thruster not active block: time = " + simulate() + ", velocity = " + velocity);
	}
	
	// utf�r ett teoretiskt test
	private void theoreticalTest() throws InterruptedException {
		thrusterActive = false;
		throwNew(altitude2, gravity2, timeFactor2);
		System.out.println(toString());
		long totalTime = simulate();
		float theoreticalTotalTime = (float)(1000*(Math.sqrt(-(2*altitude2)/gravity2)));
		float theoreticalVelocity = (float) -Math.sqrt(-gravity2*altitude2*2);
		System.out.println("\ntheoretical time = " + theoreticalTotalTime + ", simulated time = " + totalTime);
		System.out.println("theoretical velocity = " + theoreticalVelocity + ", simulated velocity = " + velocity);
	}
	
	// anv�nds f�r att returnera str�ng med startv�rden
	public String toString() {
		return "altitude = " + altitude + ", gravity = " + gravity + ", timeFactor = " + timeFactor;
	} 

	// utf�r b�de teoretiskt och konsekvent test
	@Override public void test() {
		try {
			System.out.println("\nConsistent test");
			consistentTest();
			System.out.println("\ntheoretical test");
			theoreticalTest();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void setTimeFactor(float f) {timeFactor = f; }
}
