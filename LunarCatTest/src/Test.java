
// abstrakt testklass. metoden test ska ineh�lla ett test som anv�ndaren implementerar i en underklass
// testet k�rs sedan genom att anropa metoden runTest(int times) som k�r metoden test() ett visst antal g�nger
// enligt parametern times.

public abstract class Test {
	
	public void runTest(int times) {
		if(times > 0) {
			for(int i = 0; i < times; i++) {
				test();
			}
		}
	}
	
	public abstract void test();
}
