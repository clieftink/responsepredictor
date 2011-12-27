package nl.nki.responsepredictor.bool;

/**
 * Created by Jan Krumsiek
 * Released under GPL.
 */



/**
 * Contains some general functions we need for Boolean modeling.
 */

public class BooleanGlobals {

	/**
	 *  Power 2 array for rapid calculation
	 */  
	public static final int[] pow2 = { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096,
			8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576 };

	/**
	 * Converts state represented as byte array into decimal representation
	 * @param b input byte array
	 * @return decimal representation of the given Boolean state
	 */
	public static long bin2dec(byte[] b) {
		long r=0;
		for (int i=0; i<b.length; i++) {
			r+=BooleanGlobals.pow2[i]*b[i];
		}
		return r;
	}


	/**
	 * Converts decimal representation of a Boolean state into byte array.
	 * @param state input state
	 * @param n number of factors in the state (required for leading zeros)
	 * @return byte array representation of the given Boolean state 
	 */
	public static byte[] dec2bin(long state, int n) {
		byte[] res = new byte[n];
		for (int i=n-1; i>=0; i--) {
			if (state>=pow2[i]) {
				res[i]=1;
				state-=pow2[i];
			}
		}
		return res;
	}
	
	/**
	 * Print state-transition graph for the given state space object to stdout
	 * @param sp
	 */
	public static void printSTGraph(StateSpace sp) {
		
		for (BooleanEdge edge : sp.edges()) {
			System.out.print(byteArrToStr(edge.from().b));
			System.out.print(" => ");
			System.out.print(byteArrToStr(edge.to().b));
			System.out.println();
		}
		
	}
	
	/**
	 * Print steady states stored in the given state space object to stdout
	 * @param sp
	 */
	public static void printSteadyState(StateSpace sp) {
		
		for (BooleanState ss : sp.ss())
			System.out.println(byteArrToStr(ss.b));
		
	}
	
	/**
	 * Print results of Boolean simulation to stdout. Will be arranged as one row for each player and one column for each timepoints.
	 * @param sim byte[][] array as produced by {@link BooleanSimulation#performSimulation(byte[], int, jkbool.BooleanSimulation.SimulationMode)}
	 */
	public static void printSimulation(BooleanState[] sim) {
		// print it
		for (int i=0; i<sim[i].length(); i++) {
			for (int t=0; t<sim.length;t++) {
				System.out.print(sim[t].b[i]);
			}
			System.out.println();
		}
	}
	
	
	/**
	 * Simply joins a given byte array into one String.
	 */
	private static String byteArrToStr(byte[] arr) {
		char[] conv = new char[arr.length];
		for (int i=0; i<conv.length; i++)
			conv[i] = (arr[i]==1) ? '1' : '0';
		return new String(conv);
	}
	
}
