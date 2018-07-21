import java.util.ArrayList;

/* En m�ngsidig klocka som kan anv�ndas till lite allt m�jligt. Man m�ste dock vara f�rsiktig s� att man inte
 * anv�nder en och samma instans till flera �ndam�l samtidigt d� alla metoder anv�nder och modifierar i princip samma
 * medlemmar.
 */

//!!!!!!!!!!! om fps-bara fps kontroll etc

public class Timer {
	/* deltaTick representerar en tidsskillnad(ms)
	 * lastTick representerar en tidigare sparad tid(ms)
	 * mSecPerFrame representerar 1/fps vilket sparas om instansen ska anv�ndas f�r fps kontroll eller liknande
	 */
	long deltaTick, lastTick, mSecPerFrame;
	ArrayList<Integer> realFpsArray;
	int lastRealFps;
	
	// n�r klassen intansieras s� sparar objektet den nuvarande tiden i lastTick
	public Timer() {
		lastTick = System.currentTimeMillis();
		mSecPerFrame = 0;
		deltaTick = 0;
		realFpsArray = new ArrayList<Integer>();
		lastRealFps = 0;
	}
	
	// s�tter deltaTick till tiden som g�tt sedan den senaste uppdateringen och p�b�rjar en ny tidtagning
	public void update() {
		long currentTick = getTick();
		deltaTick = currentTick - lastTick;
		lastTick = currentTick;
	}
	
	// g�r samma som update, men returnerar ocks� deltaTick
	public long getDeltaTime() {
		update();
		return deltaTick;
	}
	
	// om man vill se hur mycket tid som g�tt sedan den senaste uppdateringen utan att utf�ra en uppdatering s�
	// kan denna metod anv�ndas som returnerar den nuvarande tidsskillnaden
	public long peakDeltaTime() {
		return getTick()-lastTick;
	}
	
	// s�tter en fps f�r fps kontroll
	public void setFps(int fps) {
		mSecPerFrame = (long)1000/fps;
	}
	
	// returnerar den absoluta tiden. dvs g�r samma sak som System.currentTimeMillis()
	public long getTick() {
		return System.currentTimeMillis();
	}
	
	// man anv�nder fps kontroll f�r att se till s� att systemet h�ller sig till en viss fps. 
	// Men det finns fall d�r fps kontroll inte garanterar en viss fps. Detta sker n�r processorn f�r alldeles f�r mycket
	// att g�ra. D�rf�r kan det vara bra att ha en metod som returnerar den verkliga fps:en s� att man kan se n�r systemet
	// b�rjar n� sin gr�ns. Detta g�r denna metod genom att ta medelv�rdet av var 10:e framr�knat fps.
	public int getRealFps() {
		if(realFpsArray.size() < 10) realFpsArray.add(1000/(int)deltaTick);
		else {
			lastRealFps = 0;
			for(Integer fps : realFpsArray) {
				lastRealFps += fps;
			}
			lastRealFps = lastRealFps/10;
			realFpsArray.clear();
		}
		return lastRealFps;
	}
	
	// metoden f�r fps kontroll. denna metod returnerar true om tillr�ckligt l�ng tid har g�tt sedan
	// f�rra uppdateringen. Samtidigt som den returnerar true s� nollst�ller den klockan inf�r n�sta uppdatering
	public boolean isRenderTime() { //!!!!!!!!!!
		if(getTick() - lastTick < mSecPerFrame) return false;
		else {
			update();
			return true;
		}
	}
	
}
