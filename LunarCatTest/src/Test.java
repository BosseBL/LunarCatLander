
// abstrakt testklass. metoden test ska inehŒlla ett test som anvŠndaren implementerar i en underklass
// testet kšrs sedan genom att anropa metoden runTest(int times) som kšr metoden test() ett visst antal gŒnger
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
