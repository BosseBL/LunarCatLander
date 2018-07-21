import java.util.ArrayList;

/* En mångsidig klocka som kan användas till lite allt möjligt. Man måste dock vara försiktig så att man inte
 * använder en och samma instans till flera ändamål samtidigt då alla metoder använder och modifierar i princip samma
 * medlemmar.
 */

//!!!!!!!!!!! om fps-bara fps kontroll etc

public class Timer {
	/* deltaTick representerar en tidsskillnad(ms)
	 * lastTick representerar en tidigare sparad tid(ms)
	 * mSecPerFrame representerar 1/fps vilket sparas om instansen ska användas för fps kontroll eller liknande
	 */
	long deltaTick, lastTick, mSecPerFrame;
	ArrayList<Integer> realFpsArray;
	int lastRealFps;
	
	// när klassen intansieras så sparar objektet den nuvarande tiden i lastTick
	public Timer() {
		lastTick = System.currentTimeMillis();
		mSecPerFrame = 0;
		deltaTick = 0;
		realFpsArray = new ArrayList<Integer>();
		lastRealFps = 0;
	}
	
	// sätter deltaTick till tiden som gått sedan den senaste uppdateringen och påbörjar en ny tidtagning
	public void update() {
		long currentTick = getTick();
		deltaTick = currentTick - lastTick;
		lastTick = currentTick;
	}
	
	// gör samma som update, men returnerar också deltaTick
	public long getDeltaTime() {
		update();
		return deltaTick;
	}
	
	// om man vill se hur mycket tid som gått sedan den senaste uppdateringen utan att utföra en uppdatering så
	// kan denna metod användas som returnerar den nuvarande tidsskillnaden
	public long peakDeltaTime() {
		return getTick()-lastTick;
	}
	
	// sätter en fps för fps kontroll
	public void setFps(int fps) {
		mSecPerFrame = (long)1000/fps;
	}
	
	// returnerar den absoluta tiden. dvs gör samma sak som System.currentTimeMillis()
	public long getTick() {
		return System.currentTimeMillis();
	}
	
	// man använder fps kontroll för att se till så att systemet håller sig till en viss fps. 
	// Men det finns fall där fps kontroll inte garanterar en viss fps. Detta sker när processorn får alldeles för mycket
	// att göra. Därför kan det vara bra att ha en metod som returnerar den verkliga fps:en så att man kan se när systemet
	// börjar nå sin gräns. Detta gör denna metod genom att ta medelvärdet av var 10:e framräknat fps.
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
	
	// metoden för fps kontroll. denna metod returnerar true om tillräckligt lång tid har gått sedan
	// förra uppdateringen. Samtidigt som den returnerar true så nollställer den klockan inför nästa uppdatering
	public boolean isRenderTime() { //!!!!!!!!!!
		if(getTick() - lastTick < mSecPerFrame) return false;
		else {
			update();
			return true;
		}
	}
	
}
