import java.awt.image.BufferedImage;
import java.util.Random;
import javax.sound.sampled.Clip;
import java.awt.Graphics2D;

/*	en Drawable och DynamicImage klass som representerar ett fallande objekt med faketmotorer.
 *	Denna klass hanterar allt som r�r det fallande objektet: Ljud, Animation och fysik, samt h�ller koll
 * 	p� katt r�kningen och h�ller i ett LearningMatrix objekt (hj�rnan) och ett RocketThrust objekt (raketmotorerna).
 * 
 * 	Denna klass g�r kanske lite f�r mycket sj�lv. fr�n b�rjan trodde jag inte att den skulle bli s� stor.
 * 	Men allt eftersom programmet tog form s� v�xte antalet variabler och metoder som denna klass h�ller.
 * 	Den �r allt annat �n generell just nu.
 *  	
 */

public final class LandingObject extends DynamicImage {
	
	private float fuel, altitude, velocity;			// den aktuella br�nsleniv�n, h�jden och hastigheten
	private float initFuel, initAltitude, initVelocity;		// den initiella br�nsleniv�n, h�jden och hastigheten
	private float altScale, thrustAcceleration, gravity, fuelConsumption;	// h�jd till bild skala, accelerationer och br�nsle f�rbruktning
	private int ground;								// vart marken �r p� bilden (pixlar y-led)
	private RocketThrust flames;					// raket motorerna
	private LearningMatrix ai;						// hj�rnan
	private Timer timer;							// en timer f�r fysik
	private boolean isFalling;						// true om objektet �r fallande
	private boolean isDone;							// true om objektet �r klart f�r en ny omg�ng
	private float timeFactor;						// tidsfaktor. Denna anv�nds f�r att f� allt att g� snabbare
	private int totalCats, totalWins, totalCrashes, totalFuelEmpty;		// h�ller r�kningen
	private Random rand;							// slumpar fram ing�ngsv�rden f�r ett fall					 
	private boolean lastResult;						// senaste resultat (om landning lyckades eller ej)
	private int thrusterSeparation;					// distans mellan de tv� utbl�sen till raketmotorerna
	private Playable screamingCat;					// skrikande katt ljud
	private Playable catMeow;						// jamande katt ljud
	private Playable groundHit;						// pladask ljud 
	
	public LandingObject() {
		super(3);			// g�r utrymme f�r 3 bilder i DynamicImage
		try {	
			// h�mtar v�rden fr�n ResourceManager
			fuel = ResourceManager.getConfigFloatValue("fuel");
			altitude = ResourceManager.getConfigFloatValue("altitude");
			velocity = ResourceManager.getConfigFloatValue("initialVelocity");
			
			// s�tter de initiella v�rdena till samma som nyss h�mtades
			initFuel = fuel;
			initAltitude = altitude;
			initVelocity = velocity;
			
			// initierar bilderna
			BufferedImage[] img = new BufferedImage[3];
			img[0] = ResourceManager.getImage("fallingCat");
			img[1] = ResourceManager.getImage("thrustingCat");
			img[2] = ResourceManager.getImage("deadCat");
			setOfsetX(2, -10);		// f�rskjuter en av bilderna lite �t v�nster
			super.setImages(img);
			
			// h�mtar fler resurser fr�n Resource Manager
			altScale = ResourceManager.getConfigFloatValue("altitudeScale");
			thrustAcceleration = ResourceManager.getConfigFloatValue("thrustAcceleration");
			gravity = ResourceManager.getConfigFloatValue("gravity");
			fuelConsumption = ResourceManager.getConfigFloatValue("fuelConsumption");
			ground = ResourceManager.getConfigIntValue("ground");
			
			timeFactor = 5;		// initiell tidsfaktor �r 5 (5 g�nger verklig tid). 
			
			flames = new RocketThrust();
			timer = new Timer();
			rand = new Random(System.currentTimeMillis());
			ai = new LearningMatrix(altitude, fuel, velocity, gravity, 8 );		// initierar ai med ing�ngsv�rdena och dimension p� 8*8*8 celler
			
			thrusterSeparation = 40;
			
			// h�mtar ljud fr�n ResourceManager och ser till att de g�r att spela
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
			// om katten upplever en skr�ckupplevelse s� ska screamingCat spelas
			if(velocity < -15 && altitude <= 150 && fuel <= 0 && !screamingCat.isPlaying()) screamingCat.play();
			// om katten n�tt marken s� ska catDone() utf�ras
			if(altitude <= 0) {
				catDone();
			}
			// Jag har h�r begr�nsat objektet till att bara kunna ta 2 beslut i sekunden d� det ser lite mer realistiskt ut
			else if(flames.timeSinceLastThrust() >= 500/timeFactor && fuel > 0) {
				// tar ett nytt beslut 
				setThruster(ai.getDecision(altitude, fuel, velocity));
			}

			// s�tter bildens nya position
			setPosition(x, ground-(int)(altitude*altScale));
			flames.setPosition(x+10, y+50);
		}
		else {
			// n�r objektet n�tt marken s� ska programmet v�nta en stund innan det forts�tter
			if(timer.peakDeltaTime() >= 5000/timeFactor) {		
				isDone = true;
			}
		}
	}
	
	// n�r katten n�tt marken utf�rst detta
	private void catDone() {
		// om hastigheten var mindre �n -7 eller om br�nslet var slut s� har den f�rlorat
		if(velocity < -7 || fuel <= 0) {
			// om hastigheten var mindre �n -7 s� dog objektet. bilden byts ut och ljud spelas
			if(velocity < -7) {
				setCurrentImage(2);
				groundHit.play();
			}
			// om br�nslet var slut eller ej s� ska f�ljande r�knare h�jas.
			if(fuel <= 0) totalFuelEmpty++;
			else totalCrashes++;
			ai.repportResult(false);	// raporterar resultatet
			lastResult = false;
		}
		// om allt gick v�l s� ska motsatta saker ske
		else {
			setCurrentImage(0);
			totalWins++;
			ai.repportResult(true);
			lastResult = true;
			if(Math.random() > 0.7) catMeow.play();		// vill inte att detta ljus spelas varje g�ng. s� slumpar detta.
		}
	
		totalCats++;	// r�knar �nnu en katt
		velocity = 0;	// nollst�ller hastigheten.
		flames.stop();	// slutar spela ljud
		screamingCat.stop();
		isFalling = false;	// �ndrar statusen till icke fallande
		timer.update();		// updaterar timer
	}
	
	
	// Denna metod sk�ter uppdaterar all fysik i fallandet
	private void updatePhysics() {
		float dtime = (float)timer.getDeltaTime()*timeFactor/1000;	// klockar tiden sen senaste updateringen
		if(flames.isActive()) {									// om raketmotorerna �r p� s� ser fysiken lite annorlunda ut
			fuel += fuelConsumption*dtime;						// br�nsle ska r�knas bort basserat p� f�rbrukning och tid
			float acceleration = gravity+thrustAcceleration;	// den totala accelerationen r�knas ut
			float newVelocity = velocity+dtime*acceleration;	// hastighet ska l�ggas p�
			altitude += (newVelocity+velocity)*dtime/2;		// f�r att �ka nogrannheten s� tas ett medelv�rde p� f�rra och nya hastigheten n�r h�jden r�knas ut
			velocity = newVelocity;					// och s� blir den nya hastigheten den aktuella
		}
		else {												// om raketmotorerna inte �r p� s� �r det vanligt fallande
			float newVelocity = velocity+gravity*dtime;		// ny hastighet r�knas ut
			altitude += (newVelocity+velocity)*dtime/2;		// h�jd r�knas ut med medelv�rde h�r ocks�
			velocity = newVelocity;						// uppdaterar hastigheten
		}
		if(fuel <= 0) setThruster(false);		// om br�nslet �r slut s� ska motorerna sl�s av
	}
	
	// initierar allt f�r ett nytt fall
	public void throwNewCat() {
		isFalling = true;		// objektet �r �terigen fallande
		isDone = false;		// och inte f�rdig
		timer.update();		// uppdaterar tiden f�r att nollst�lla klockan
		// slumpar fram ing�ngsv�rden
		fuel = initFuel - 50*rand.nextFloat();
		altitude = initAltitude - 100*rand.nextFloat();
		velocity = initVelocity + 10*rand.nextFloat();
		// justering av bild
		setRenderActive(true);
		setUpdateActive(true);
		setCurrentImage(0);
		// stoppar allt ljud s� att de �r tillbakaspolade inf�r n�sta spelning
		catMeow.stop();
		groundHit.stop();
	}
	
	// get metoder
	public float getAltitude() { return altitude;}
	public float getFuel() {return fuel;}
	public float getVelocity() {return velocity;}
	
	// s�tter p� och st�nger av raketmotorerna
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
	
	// �ndrar tidsfaktorn. Om tidsfaktorn �r f�r h�g s� st�ngs allt ljud av d� det l�ter frukansv�rt i h�g hastighet
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
	
	public LearningMatrix getBrain() {return ai;}		// beh�vs f�r att CatWorld ska kunna s�tta renderings position
	
	public boolean isDone() {return isDone;}
	
	public boolean getLastResutl() {return lastResult;}
	
	// anv�nds n�r katt bilder byts mot landar bilder. d� �ndras raketmotorernas position
	public void setThrusterSeparation(int i) {thrusterSeparation = i;}	
	// anv�nds f�r att byta ljudet n�r katterna byts mot landare
	public void setGroundHitSound(Clip c) {
		groundHit.setClip(c);
	}
	
	// get metoder f�r alla r�knare
	public int getTotalCats() {return totalCats;}
	public int getTotalWins() {return totalWins;}
	public int getTotalCrashes() {return totalCrashes;}
	public int getTotalFuelEmpty() {return totalFuelEmpty;}
}


