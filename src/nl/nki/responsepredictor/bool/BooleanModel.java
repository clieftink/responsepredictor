package nl.nki.responsepredictor.bool;

/**
 * Created by Jan Krumsiek
 * Released under GPL.
 */


/**
 * Abstract class for a Boolean model. Implements some basic getter
 * functionalities and the conversion to a TruthTableModel
 */

public abstract class BooleanModel {

	/**
	 * Central function of a BooleanModel. Given a variable to be updated and
	 * the current state, returns the follow-up value of the variable
	 */
	public abstract byte singleUpdate(int var, byte[] state);

	/**
	 * Returns the variables contained in this model, as a String array
	 */
	public abstract String[] getVariables();

	
	/**
	 * Returns the internal index of a given model variable.
	 * @param variable variable string to search
	 * @return index of variable, or -1 if the variable does not exist
	 */
	public int variableID(String variable) {
		int r=-1;
		String s[] = getVariables();
		for (int i=0; i<s.length && r==-1; i++) {
			if (s[i].equals(variable)) {
				r=i;
			}
		}
		return r;
	}
	
	/**
	 * Returns the number of variables contained in this model.
	 */
	public int numberOfVariables() {
		return getVariables().length;
	}

	/**
	 * Returns the input array, that is for each factor in the model the
	 * corresponding input species, in order.
	 */
	public abstract int[][] getInputs();	
	
	/**
	 * Converts any BooleanModel into a TruthTableModel. Internally, the
	 * algorithm goes through all possible input state combinations for all
	 * players, and fills up the truth table by calling the singleUpdate
	 * function.
	 * 
	 * @return generated TruthTableModel
	 */
	public TruthTableModel generateTruthTableModel() {
		
		// initialize
		int[][] inputs = this.getInputs();
		byte[][] truth = new byte[inputs.length][]; 
		// iterate over species
		for (int i=0; i<inputs.length; i++) {
			int n=inputs[i].length;
			if (n==0) {
				truth[i] = new byte[0];
			} else {
				truth[i] = new byte[BooleanGlobals.pow2[n]];

				// iterate over states
				for (int j=0; j<BooleanGlobals.pow2[n]; j++) {
					// get truth table value
					byte[] state = new byte[inputs.length];
					byte[] insstate = BooleanGlobals.dec2bin(j, n);
					for (int k=0; k<n; k++)
						state[inputs[i][k]]=insstate[k];
					
					truth[i][j] = this.singleUpdate(i, state);
				}
			}
		}
		
		return new TruthTableModel(this.getVariables(), truth, inputs);
	}


}
