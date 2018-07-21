import java.awt.image.BufferedImage;
import java.util.Random;
import javax.sound.sampled.Clip;
import java.awt.Graphics2D;

/*	en Drawable och DynamicImage klass som representerar ett fallande objekt med faketmotorer.
 *	Denna klass hanterar allt som rör det fallande objektet: Ljud, Animation och fysik, samt håller koll
 * 	på katt räkningen och håller i ett LearningMatrix objekt (hjärnan) och ett RocketThrust objekt (raketmotorerna).
 * 
 * 	Denna klass gör kanske lite för mycket själv. från början trodde jag inte att den skulle bli så stor.
 * 	Men allt eftersom programmet tog form så växte antalet variabler och metoder som denna klass håller.
 * 	Den är allt annat än generell just nu.
 *  	
 */

public final class LandingObject extends DynamicImage {
	
	private float fuel, altitude, velocity;			// den aktuella bränslenivån, höjden och hastigheten
	private float initFuel, initAltitude, initVelocity;		// den initiella bränslenivån, höjden och hastigheten
	private float altScale, thrustAcceleration, gravity, fuelConsumption;	// höjd till bild skala, accelerationer och bränsle förbruktning
	private int ground;								// vart marken är på bilden (pixlar y-led)
	private RocketThrust flames;					// raket motorerna
	private LearningMatrix ai;						// hjärnan
	private Timer timer;							// en timer för fysik
	private boolean isFalling;						// true om objektet är fallande
	private boolean isDone;							// true om objektet är klart för en ny omgång
	private float timeFactor;						// tidsfaktor. Denna används för att få allt att gå snabbare
	private int totalCats, totalWins, totalCrashes, totalFuelEmpty;		// håller räkningen
	private Random rand;							// slumpar fram ingångsvärden för ett fall					 
	private boolean lastResult;						// senaste resultat (om landning lyckades eller ej)
	private int thrusterSeparation;					// distans mellan de två utblåsen till raketmotorerna
	private Playable screamingCat;					// skrikande katt ljud
	private Playable catMeow;						// jamande katt ljud
	private Playable groundHit;						// pladask ljud 
	
	public LandingObject() {
		super(3);			// gör utrymme för 3 bilder i DynamicImage
		try {	
			// hämtar värden från ResourceManager
			fuel = ResourceManager.getConfigFloatValue("fuel");
			altitude = ResourceManager.getConfigFloatValue("altitude");
			velocity = ResourceManager.getConfigFloatValue("initialVelocity");
			
			// sätter de initiella värdena till samma som nyss hämtades
			initFuel = fuel;
			initAltitude = altitude;
			initVelocity = velocity;
			
			// initierar bilderna
			BufferedImage[] img = new BufferedImage[3];
			img[0] = ResourceManager.getImage("fallingCat");
			img[1] = ResourceManager.getImage("thrustingCat");
			img[2] = ResourceManager.getImage("deadCat");
			setOfsetX(2, -10);		// förskjuter en av bilderna lite åt vänster
			super.setImages(img);
			
			// hämtar fler resurser från Resource Manager
			altScale = ResourceManager.getConfigFloatValue("altitudeScale");
			thrustAcceleration = ResourceManager.getConfigFloatValue("thrustAcceleration");
			gravity = ResourceManager.getConfigFloatValue("gravity");
			fuelConsumption = ResourceManager.getConfigFloatValue("fuelConsumption");
			ground = ResourceManager.getConfigIntValue("ground");
			
			timeFactor = 5;		// initiell tidsfaktor är 5 (5 gånger verklig tid). 
			
			flames = new RocketThrust();
			timer = new Timer();
			rand = new Random(System.currentTimeMillis());
			ai = new LearningMatrix(altitude, fuel, velocity, gravity, 8 );		// initierar ai med ingångsvärdena och dimension på 8*8*8 celler
			
			thrusterSeparation = 40;
			
			// hämtar ljud från ResourceManager och ser till att de går att spela
			screamingCat = new Playable(ResourceManager.getAudio("angryCat"));
			screamingCat.setPlayActive(true);
			catMeow = new Playable(ResourceManager.getAudio("catMeow"));
			catMeow.setPlayActive(true);
			groundHit = new Playable(ResourceManager.getAudio("groundSmack"));
			groundHit.setPlayActive(true);
			
		} catch(Exception e) {
			System.out.println("Exception caught in LandingObject.LandingObject(): " + e.toString());
		}
	}
	
	// ritar ai aktiviteten, flamman och det fallande objektet
	protected void paint(Graphics2D g, int x, int y) {
		ai.draw(g);
		flames.draw(g);
		flames.move(thrusterSeparation, 0);
		flames.draw(g);
		super.paint(g, x, y);
	}
	
	// updaterar fysik och annan logik
	protected void logic() {
		if(isFalling) {
			updatePhysics();
			// om katten upplever en skräckupplevelse så ska screamingCat spelas
			if(velocity < -15 && altitude <= 150 && fuel <= 0 && !screamingCat.isPlaying()) screamingCat.play();
			// om katten nått marken så ska catDone() utföras
			if(altitude <= 0) {
				catDone();
			}
			// Jag har här begränsat objektet till att bara kunna ta 2 beslut i sekunden då det ser lite mer realistiskt ut
			else if(flames.timeSinceLastThrust() >= 500/timeFactor && fuel > 0) {
				// tar ett nytt beslut 
				setThruster(ai.getDecision(altitude, fuel, velocity));
			}

			// sätter bildens nya position
			setPosition(x, ground-(int)(altitude*altScale));
			flames.setPosition(x+10, y+50);
		}
		else {
			// när objektet nått marken så ska programmet vänta en stund innan det fortsätter
			if(timer.peakDeltaTime() >= 5000/timeFactor) {		
				isDone = true;
			}
		}
	}
	
	// när katten nått marken utförst detta
	private void catDone() {
		// om hastigheten var mindre än -7 eller om bränslet var slut så har den förlorat
		if(velocity < -7 || fuel <= 0) {
			// om hastigheten var mindre än -7 så dog objektet. bilden byts ut och ljud spelas
			if(velocity < -7) {
				setCurrentImage(2);
				groundHit.play();
			}
			// om bränslet var slut eller ej så ska följande räknare höjas.
			if(fuel <= 0) totalFuelEmpty++;
			else totalCrashes++;
			ai.repportResult(false);	// raporterar resultatet
			lastResult = false;
		}
		// om allt gick väl så ska motsatta saker ske
		else {
			setCurrentImage(0);
			totalWins++;
			ai.repportResult(true);
			lastResult = true;
			if(Math.random() > 0.7) catMeow.play();		// vill inte att detta ljus spelas varje gång. så slumpar detta.
		}
	
		totalCats++;	// räknar ännu en katt
		velocity = 0;	// nollställer hastigheten.
		flames.stop();	// slutar spela ljud
		screamingCat.stop();
		isFalling = false;	// ändrar statusen till icke fallande
		timer.update();		// updaterar timer
	}
	
	
	// Denna metod sköter uppdaterar all fysik i fallandet
	private void updatePhysics() {
		float dtime = (float)timer.getDeltaTime()*timeFactor/1000;	// klockar tiden sen senaste updateringen
		if(flames.isActive()) {									// om raketmotorerna är på så ser fysiken lite annorlunda ut
			fuel += fuelConsumption*dtime;						// bränsle ska räknas bort basserat på förbrukning och tid
			float acceleration = gravity+thrustAcceleration;	// den totala accelerationen räknas ut
			float newVelocity = velocity+dtime*acceleration;	// hastighet ska läggas på
			altitude += (newVelocity+velocity)*dtime/2;		// för att öka nogrannheten så tas ett medelvärde på förra och nya hastigheten när höjden räknas ut
			velocity = newVelocity;					// och så blir den nya hastigheten den aktuella
		}
		else {												// om raketmotorerna inte är på så är det vanligt fallande
			float newVelocity = velocity+gravity*dtime;		// ny hastighet räknas ut
			altitude += (newVelocity+velocity)*dtime/2;		// höjd räknas ut med medelvärde här också
			velocity = newVelocity;						// uppdaterar hastigheten
		}
		if(fuel <= 0) setThruster(false);		// om bränslet är slut så ska motorerna slås av
	}
	
	// initierar allt för ett nytt fall
	public void throwNewCat() {
		isFalling = true;		// objektet är återigen fallande
		isDone = false;		// och inte färdig
		timer.update();		// uppdaterar tiden för att nollställa klockan
		// slumpar fram ingångsvärden
		fuel = initFuel - 50*rand.nextFloat();
		altitude = initAltitude - 100*rand.nextFloat();
		velocity = initVelocity + 10*rand.nextFloat();
		// justering av bild
		setRenderActive(true);
		setUpdateActive(true);
		setCurrentImage(0);
		// stoppar allt ljud så att de är tillbakaspolade inför nästa spelning
		catMeow.stop();
		groundHit.stop();
	}
	
	// get metoder
	public float getAltitude() { return altitude;}
	public float getFuel() {return fuel;}
	public float getVelocity() {return velocity;}
	
	// sätter på och stänger av raketmotorerna
	private void setThruster(boolean b) {
		if(fuel > 0 && b) {
				flames.start();
				setCurrentImage(1);
			}
		else {
			flames.stop();
			setCurrentImage(0);
		}
	}
	
	// ändrar tidsfaktorn. Om tidsfaktorn är för hög så stängs allt ljud av då det låter frukansvärt i hög hastighet
	public void setTimeFactor(float f) {
		if(f > 20) {
			flames.setPlayable(false);
			screamingCat.setPlayActive(false);
			catMeow.setPlayActive(false);
			groundHit.setPlayActive(false);
		}
		else {
			flames.setPlayable(true);
			screamingCat.setPlayActive(true);
			catMeow.setPlayActive(true);
			groundHit.setPlayActive(true);
		}
		
		timeFactor = f;
	} 
	
	public LearningMatrix getBrain() {return ai;}		// behövs för att CatWorld ska kunna sätta renderings position
	
	public boolean isDone() {return isDone;}
	
	public boolean getLastResutl() {return lastResult;}
	
	// används när katt bilder byts mot landar bilder. då ändras raketmotorernas position
	public void setThrusterSeparation(int i) {thrusterSeparation = i;}	
	// används för att byta ljudet när katterna byts mot landare
	public void setGroundHitSound(Clip c) {
		groundHit.setClip(c);
	}
	
	// get metoder för alla räknare
	public int getTotalCats() {return totalCats;}
	public int getTotalWins() {return totalWins;}
	public int getTotalCrashes() {return totalCrashes;}
	public int getTotalFuelEmpty() {return totalFuelEmpty;}
}


