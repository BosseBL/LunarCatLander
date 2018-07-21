import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.lang.Exception;

/* En resurs hanterare som hanterar programmets externa filer och gör dess material tillgängligt för alla 
 * alla klasser i programmet. Den hanterar bild, ljud, text och config resurser. Den laddar alla resurser genom
 * att läsa av LoadList filerna vid programmets start.
 * Klassen är public och static, vilket inebär att metoderna inte kräver någon instans, och resurserna är tillgänliga
 * för alla klasser. 
 */

public class ResourceManager {
	// alla resurser förvaras i hashmaps och kan kommas åt genom resursens nyckel
	private static HashMap<String, BufferedImage> images = null;
	private static HashMap<String, String> config = null;
	private static HashMap<String, Clip> audios = null;
	private static HashMap<String, String> texts = null;
	
	static {	// initieringen sker i ett static block
		images = new HashMap<String, BufferedImage>();
		config = new HashMap<String, String>();
		audios = new HashMap<String, Clip>();
		texts = new HashMap<String, String>();
		
		try {
			Scanner file = new Scanner(new File("src/resources/imageLoadList.txt"));
			loadImages(file);
		
			file = new Scanner(new File("src/resources/config.txt"));
			loadConfig(file);
		
			file = new Scanner(new File("src/resources/audioLoadList.txt"));
			loadAudios(file);
			
			file = new Scanner(new File("src/resources/textLoadList.txt"));
			loadTexts(file);
			
		} catch (Exception e) {
			System.out.println("exception caught while loading resources: " + e.toString());
			System.exit(1);
		}
	}
	
	// följande metoder är bara till för att ladda hashmapparna med resurser vid starten av programmet
	private static void loadImages(Scanner file) throws IOException {
		while(file.hasNext()) {
				String key = file.next();
				BufferedImage image = ImageIO.read(new File(file.next()));
				images.put(key, image);
		}
	}
	private static void loadConfig(Scanner file) {
		while(file.hasNext()) {
				String key = file.next();
				String value = file.next();
				config.put(key, value);
		}
	}
	private static void loadAudios(Scanner file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		while(file.hasNext()) {
				String key = file.next();
				AudioInputStream is = AudioSystem.getAudioInputStream(new File(file.next()));
				Clip audio = AudioSystem.getClip();
				audio.open(is);
				audios.put(key, audio);
		}
	}
	private static void loadTexts(Scanner file) throws FileNotFoundException  {
		while(file.hasNext()) {
			String key = file.next();
			Scanner s = new Scanner(new File(file.next()));
			String str = "";
			while(s.hasNext()) {
				str += s.nextLine() + "\n";
			}
			texts.put(key, str);
		}
	}
	
	
	// alla get metoder.
	
	public static BufferedImage getImage(String key) throws Exception {
		if(images.containsKey(key)) return images.get(key);
		else throw new Exception("The requested image resource " + key + " is not loaded into the ResourceManager");
	}
	public static Clip getAudio(String key) throws Exception {
		if(audios.containsKey(key)) return audios.get(key);
		else throw new Exception("The requested audio resource " + key + " is not loaded into the ResourceManager");
	}
	public static String getText(String key) throws Exception {
		if(texts.containsKey(key)) return texts.get(key);
		else throw new Exception("The requested text resource " + key + " is not loaded into the ResourceManager");
	}
	
	// Det blev många metoder för att komma åt
	// materialet i config filen då den inehåller olika datatyper, och jag valde
	// att låta ResourceManager hantera konverteringen från string till vald datatyp
	public static String getConfigStrValue(String key) throws Exception {
		if(config.containsKey(key)) return config.get(key);
		else throw new Exception("the requested config value" + key + " is not loaded into the ResourceManager");
	}
	public static boolean getConfigBoolValue(String key) throws Exception {
		String value = getConfigStrValue(key);
		return Boolean.valueOf(value);
	}
	public static int getConfigIntValue(String key) throws Exception {
		String value = getConfigStrValue(key);
		return Integer.valueOf(value);
	}
	public static float getConfigFloatValue(String key) throws Exception {
		String value = getConfigStrValue(key);
		return Float.valueOf(value);
	}
	public static double getConfigDoubleValue(String key) throws Exception {
		String value = getConfigStrValue(key);
		return Double.valueOf(value);
	}
	
}
