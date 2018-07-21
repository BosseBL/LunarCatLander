/*	En klass som tillsammans med en tillstŒnds upprŠknare fungerar som en enkel tllstŒnds hanterare.
 * 	upprŠknings typen mŒste implementera State fšr att kunna anvŠndas med StateManager. Se CatWorld fšr att 
 * 	se hur det gŒr till
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
