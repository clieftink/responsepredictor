package nl.nki.responsepredictor.bool;

/**
 * Created by Jan Krumsiek
 * Released under GPL.
 */

import java.util.Arrays;

import nl.nki.responsepredictor.bool.BooleanSimulation.UpdatePolicy;


/**
 * Contains a few example calls on how to use the Boolean modeling classes.
 */
public class Examples {
	
	public static void main(String[] args) throws InvalidEquationException {

		// uncomment the example calls you wish to run
		
//		feedForwardLoop();
//		System.out.println("\n======\n");
//		speedTests();
//		System.out.println("\n======\n");
//		fixedVariable();
//		System.out.println("\n======\n");
		partialSS(); 
		
	}

	/**
	 * Runs a simple incoherent feed-forward loop model with synchronous update,
	 * creating a pulse for the third factor.
	 */
	static void feedForwardLoop() throws InvalidEquationException {
		
		// define the Boolean equations
		String[] eqs = new String[] {
				"A=",  // no inputs, always keeps the initial state
				"B=A",
				"C=A&!B"
		}; 
		
		
		// generate model
		BooleanModel m = new EquationModel(eqs);  // throws InvalidEquationException
		System.out.println("Generated Boolean model with factors: "
				+ Arrays.toString(m.getVariables()));		
		
		
		// run sync simulation from certain initial state
		BooleanSimulation sim = new BooleanSimulation(m);
		
		BooleanState init = new BooleanState(1,0,0);
		int timeto=10;
		BooleanState[] y = sim.performSimulation(init, timeto, UpdatePolicy.SYNC); 
		// show it
		System.out.println("\nSimulation (sync):");
		BooleanGlobals.printSimulation(y);
		
		
		// calculate steady states from given initial state (sync)
		StateSpace sp = sim.calcStateSpace(
				true,  // yes, synchronous 
				init,  
				false); // no, do not store state-transition graph
		// show it
		System.out.println("\nSteady states from initial:");
		BooleanGlobals.printSteadyState(sp);

		
		// calculate steady states and ST graph for the whole state space (no initial value), async
		sp = sim.calcStateSpace(
				false,  // no, not synchronous 
				init,   // use an initial state
				true);  // yes, store state-transition graph
		// show results
		System.out.println("\nAsync ST graph from initial:");
		BooleanGlobals.printSTGraph(sp);
		
	}
	

	/**
	 * Enumerates the entire state space on a larger model (10 factors), with
	 * the "original" EquationModel variant, and with the derived
	 * TruthTableModel
	 * @throws InvalidEquationException 
	 */
	static void speedTests() throws InvalidEquationException {

		// define equations => this model is from Klamt et al., 2006 (http://www.biomedcentral.com/1471-2105/7/56)
		String[] eqs = new String[] {
				"B=A&I1",
				"E=!I1&I2",
				"A=!D",
				"D=C",
				"C=B|E",
				"F=E|G",
				"G=F",
				"O2=G",
				"O1=C"
		};
		
		// generate model
		BooleanModel m = new EquationModel(eqs);  // throws InvalidEquationException
		System.out.println("Generated Boolean model with factors: "
				+ Arrays.toString(m.getVariables()));		
		// convert to TruthTableModel
		BooleanModel t = m.generateTruthTableModel();
		
		// initialize
		long e,s;
		BooleanSimulation sim;
		BooleanState init = new BooleanState(0,0,1,0,0,0,0,0,0,0,0);
		int nruns=3000;
		
		// calculate state spaces for EquationModel variant
		System.out.println("Running EquationModel tests...");
		sim = new BooleanSimulation(m);
		s=System.currentTimeMillis();
		for (int i=0; i<nruns; i++)
			sim.calcStateSpace(false, init, true);
		e=System.currentTimeMillis();
		System.out.println("Elapsed time " + (e-s) + " ms");
		System.out.println( ((float)nruns/(e-s)*1000) + " runs/s");
		

		// calculate state spaces for TruthTableModel variant
		System.out.println("Running TruthTableModel tests...");
		sim = new BooleanSimulation(t);
		s=System.currentTimeMillis();
		for (int i=0; i<nruns; i++)
			sim.calcStateSpace(false, init, true);
		e=System.currentTimeMillis();
		System.out.println("Elapsed time " + (e-s) + " ms");
		System.out.println( ((float)nruns/(e-s)*1000) + " runs/s");
		
	}

	/**
	 * Simulate with a fixed variable, that is one that does not change its
	 * value regardless of the defined Boolean function.
	 * @throws InvalidEquationException 
	 */
	static void fixedVariable() throws InvalidEquationException {

		// use the pulsing feed-forward loop again
		String[] eqs = new String[] {
				"A=",  // no inputs, always keeps the initial state
				"B=A",
				"C=A&!B"
		};
		BooleanState init = new BooleanState(1,0,0);
		
		// generate model
		BooleanModel m = new EquationModel(eqs);  // throws InvalidEquationException
		System.out.println("Generated Boolean model with factors: "
				+ Arrays.toString(m.getVariables())+"\n");		
		BooleanSimulation sim = new BooleanSimulation(m);

		// regular simulation
		BooleanState[] y1 = sim.performSimulation(init, 10, UpdatePolicy.SYNC);
		System.out.println("Regular simulation:");
		BooleanGlobals.printSimulation(y1);
		
		// fix B
		sim.setFixedVariable(1, true);
		BooleanState[] y2 = sim.performSimulation(init, 10, UpdatePolicy.SYNC);
		System.out.println("\nB fixed:");
		BooleanGlobals.printSimulation(y2);
	}
	
	
	
	static void partialSS() throws InvalidEquationException {
	
		// this model is going to oscillate, but A and B will stay constant
		String[] eqs = new String[] {
				"A=",  // no inputs, always keeps the initial state
				"B=A",
				"C=B&!D",
				"D=C"
		};
		
		// define model
		BooleanModel m = new EquationModel(eqs);  // throws InvalidEquationException
		System.out.println("Generated Boolean model with factors: "
				+ Arrays.toString(m.getVariables())+"\n");		
		BooleanSimulation sim = new BooleanSimulation(m);
		BooleanState init = new BooleanState(1,0,0,0);

		// calculate steady states and ST graph 
		StateSpace sp = sim.calcStateSpace(
				true,  // yes, synchronous 
				init,   // use an initial state
				true);  // yes, store state-transition graph
		// show results
		System.out.println("\nSync ST graph from initial:");
		BooleanGlobals.printSTGraph(sp);
		// steady state
		System.out.println("Number of steady states: " + sp.ss().size());
		
		System.out.println("Partial steady state:");
		System.out.println(Arrays.toString(sp.partialSteadyState()));
		
	}

}
