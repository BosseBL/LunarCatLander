/*	En klass som tillsammans med en tillst�nds uppr�knare fungerar som en enkel tllst�nds hanterare.
 * 	uppr�knings typen m�ste implementera State f�r att kunna anv�ndas med StateManager. Se CatWorld f�r att 
 * 	se hur det g�r till
 * 
 */

public class StateManager {
	
	private State currentState;
	private State nextState;
	
	public StateManager() {
		currentState = null;
		nextState = null;
	}
	
	public boolean updateState() { 
		if(nextState != currentState) {
			currentState = nextState; 
			return true;
		}
		else {
			return false;
		}
	}
	public State getState() { return currentState; }
	public void setNextState(State state) { nextState = state;}
}
