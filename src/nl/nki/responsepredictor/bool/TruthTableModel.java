package nl.nki.responsepredictor.bool;


/**
 * Created by Jan Krumsiek
 * Released under GPL.
 */


// TODO: sanity or error checking for the number of inputs => important not to run into errors with pow2 array

/**
 * Implementation of BooleanModel with internal truth table representation. Fast
 * due to quick array lookups, but has exponential space requirements in the
 * number of input nodes for each variable.
 * 
 * These models are not very convenient to be defined manually. One should
 * create an EquationModel and convert it to a TruthTableModel.
 */
public class TruthTableModel extends BooleanModel {

	// internal variables
	byte[][] truth; // list of truth tables, each truth table itself is a linearized hypercube with edge length 2
	private String[] variables;
	int[][] inputs=null;

	/**
	 * Generates a new TruthTableModel from a given variable list, truth table
	 * list, and input factor list.
	 */
	public TruthTableModel(String[] variables, byte[][] truth, int[][] inputs) {
		this.variables=variables;
		this.truth=truth;
		this.inputs=inputs;
	}


	/**
	 * 
	 */
	@Override
	public String[] getVariables() {
		return variables;
	}

	/**
	 * 
	 */
	@Override
	public int[][] getInputs() {
		// TODO Auto-generated method stub
		return inputs;
	}
	

	
	/**
	 * 
	 */
	@Override
	public byte singleUpdate(int factor, byte[] state) {

		// TODO: error checking

		// no inputs => keep state
		if (inputs[factor].length==0)
			return state[factor];
		else {

			// translate to linear index
			int r=0;
			for (int i=0; i<this.inputs[factor].length; i++) {
				r+=state[this.inputs[factor][i]]*BooleanGlobals.pow2[i];
			}

			// look it up in the right truth table, return
			return truth[factor][r];
		}
		
	}
	
}
