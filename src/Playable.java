import javax.sound.sampled.Clip;

/*	En klass som representerar ett spelbart ljud.
 */


public class Playable {
	Clip audio;				// ljud klippet
	boolean playActive;		// en mute variabel. man kan se till s� att ljudet inte �r spelbart med denna
	
	// namnet p� denna variabel st�mmer inte helt d� den inte s�tts till false n�r ljudet spelat klart.
	// tanken �r att den t.ex. ska anv�ndas f�r att flera delar av programmet inte ska f�rs�ka spela ljudet samtidigt.
	boolean isPlaying;		
	
	
	
	public Playable(Clip clip) {
		audio = clip;
	}
	
	public void setPlayActive(boolean b) { playActive = b; }
	public void setClip(Clip c) {audio = c;} 
	public boolean isPlayActive() {return playActive; }
	public boolean isPlaying() {return isPlaying;}
	
	public void play() {
		if(playActive) {
			isPlaying = true;
			audio.start();
		}
	}
	public void stop() {
		isPlaying = false;
		audio.stop();
		audio.setFramePosition(0);	// ser till s� att klippet spelas fr�n b�rjan n�sta g�ng play() anv�nds
	}
	public void pause() {
		isPlaying = false;
		audio.stop();
	}
}
