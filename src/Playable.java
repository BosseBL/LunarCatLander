import javax.sound.sampled.Clip;

/*	En klass som representerar ett spelbart ljud.
 */


public class Playable {
	Clip audio;				// ljud klippet
	boolean playActive;		// en mute variabel. man kan se till så att ljudet inte är spelbart med denna
	
	// namnet på denna variabel stämmer inte helt då den inte sätts till false när ljudet spelat klart.
	// tanken är att den t.ex. ska användas för att flera delar av programmet inte ska försöka spela ljudet samtidigt.
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
		audio.setFramePosition(0);	// ser till så att klippet spelas från början nästa gång play() används
	}
	public void pause() {
		isPlaying = false;
		audio.stop();
	}
}
