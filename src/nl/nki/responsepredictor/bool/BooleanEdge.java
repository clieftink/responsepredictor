package nl.nki.responsepredictor.bool;

/**
 * An ordered pair of BooleanStates
 */
public class BooleanEdge {

	private BooleanState pfrom;
	private BooleanState pto;

	public BooleanEdge(BooleanState from, BooleanState to) {
		this.pfrom=from;
		this.pto=to;
	}
	
	public BooleanState from() {
		return pfrom;
	}
	
	public BooleanState to() {
		return pto;
	}
	
	
}
