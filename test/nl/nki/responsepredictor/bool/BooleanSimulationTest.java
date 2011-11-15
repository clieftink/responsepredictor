package nl.nki.responsepredictor.bool;

import static org.junit.Assert.*;

import java.util.Arrays;

import nl.nki.responsepredictor.bool.BooleanSimulation.UpdatePolicy;
import nl.nki.responsepredictor.exception.ItemExistsException;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

public class BooleanSimulationTest {

	private void compare(byte[][] expRes, BooleanState[] resState) {
		byte[][] res = null;
		if (resState != null) {
			res = new byte[resState.length][resState[0].b.length];
			for (int i = 0; i < resState.length; i++) {
				for (int j = 0; j < resState[0].b.length; j++) {
					res[i][j] = resState[i].b[j];
				}
			}
		}

		for (int i = 0; i < expRes.length; i++) {
			for (int j = 0; j < expRes[0].length; j++) {
				assertTrue("i=" + i + " ,j=" + j + " ,expRes[i][j]= "
						+ expRes[i][j] + " ,res[i][j]= " + res[i][j],
						expRes[i][j] == res[i][j]);
			}
		}
	}

	@Test
	public void performSimulation() throws InvalidEquationException, ItemExistsException {
		// braces around are a temporary workaround in because
		// !B!C produce wrong result

		// check for fully prepared equations and for eqs later on added.
		for (int i = 0; i < 2; i++) {
			String[] eqs = new String[3];
			eqs[0] = "-1=";
			eqs[1] = "-2=";
			EquationModel m = null;
			if (i == 0) {
				eqs[2] = "-3=-1|!-2";
				m = new EquationModel(eqs);
			} else {
				eqs[2] = "-3=-1";
				m = new EquationModel(eqs);
				m.addEdge("-2", "-3", -1);
			}

			BooleanSimulation boolSim = new BooleanSimulation(m);
			byte[] init = { 0, 0, 0 };
			BooleanState initState = new BooleanState(init);
			BooleanState[] resState = boolSim.performSimulation(initState, 2,
					UpdatePolicy.SYNC);

			// first dimension time, second dim. variables
			byte[][] expRes = { { 0, 0, 0 }, { 0, 0, 1 } };
			compare(expRes, resState);

		}
	}

	@Test
	public void performSimulation2() throws InvalidEquationException {
		String[] eqs = { "A=", "B=", "C=", "D=A|!B|!C" };
		BooleanModel m = new EquationModel(eqs);
		BooleanSimulation boolSim = new BooleanSimulation(m);
		byte[] init = { 0, 0, 0, 0 };
		BooleanState initState = new BooleanState(init);
		BooleanState[] resState = boolSim.performSimulation(initState, 2,
				UpdatePolicy.SYNC);

		// first dimension time, second dim. variables
		byte[][] expRes = { { 0, 0, 0, 0 }, { 0, 0, 0, 1 } };
		compare(expRes, resState);
	}

	@Test
	public void performSimulationFixed() throws InvalidEquationException {

		// use the pulsing feed-forward loop again
		String[] eqs = new String[] { "A=", // no inputs, always keeps the
				// initial state
				"B=A", "C=A&!B" };
		byte[] init = new byte[] { 1, 0, 0 };
		BooleanState initState = new BooleanState(init);

		// generate model
		BooleanModel m = new EquationModel(eqs); // throws
		// InvalidEquationException
		BooleanSimulation sim = new BooleanSimulation(m);

		/*
		 * // regular simulation byte[][] y1 = sim.performSimulation(init, 10,
		 * UpdatePolicy.SYNC); System.out.println("Regular simulation:");
		 * BooleanGlobals.printSimulation(y1);
		 */

		// fix B
		sim.setFixedVariable(1, true);
		BooleanState[] resState = sim.performSimulation(initState, 3,
				UpdatePolicy.SYNC);
		byte[][] expRes = { { 1, 0, 0 }, { 1, 0, 1 }, { 1, 0, 1 } };

		compare(expRes, resState);

	}

	@Test
	public void performSimulation150() throws InvalidEquationException {

		String[] eqs = new String[] { "A=", "B=A", "C=B", "D=C", "E=D", "F=E",
				"G=F", "H=G", "I=H", "J=I", "K=J", "L=K", "M=L", "N=M", "O=N",
				"P=O", "Q=P", "R=Q", "S=R", "T=S", "U=T", "V=U", "W=V", "X=W",
				"Y=X", "Z=Y", "AA=Z", "AB=AA", "AC=AB", "AD=AC", "AE=AD",
				"AF=AE", "AG=AF", "AH=AG", "AI=AH", "AJ=AI", "AK=AJ", "AL=AK",
				"AM=AL", "AN=AM", "AO=AN", "AP=AO", "AQ=AP", "AR=AQ", "AS=AR",
				"AT=AS", "AU=AT", "AV=AU", "AW=AV", "AX=AW", "AY=AX", "AZ=AY",
				"BA=AZ", "BB=BA", "BC=BB", "BD=BC", "BE=BD", "BF=BE", "BG=BF",
				"BH=BG", "BI=BH", "BJ=BI", "BK=BJ", "BL=BK", "BM=BL", "BN=BM",
				"BO=BN", "BP=BO", "BQ=BP", "BR=BQ", "BS=BR", "BT=BS", "BU=BT",
				"BV=BU", "BW=BV", "BX=BW", "BY=BX", "BZ=BY", "CA=BZ", "CB=CA",
				"CC=CB", "CD=CC", "CE=CD", "CF=CE", "CG=CF", "CH=CG", "CI=CH",
				"CJ=CI", "CK=CJ", "CL=CK", "CM=CL", "CN=CM", "CO=CN", "CP=CO",
				"CQ=CP", "CR=CQ", "CS=CR", "CT=CS", "CU=CT", "CV=CU", "CW=CV",
				"CX=CW", "CY=CX", "CZ=CY", "DA=CZ", "DB=DA", "DC=DB", "DD=DC",
				"DE=DD", "DF=DE", "DG=DF", "DH=DG", "DI=DH", "DJ=DI", "DK=DJ",
				"DL=DK", "DM=DL", "DN=DM", "DO=DN", "DP=DO", "DQ=DP", "DR=DQ",
				"DS=DR", "DT=DS", "DU=DT", "DV=DU", "DW=DV", "DX=DW", "DY=DX",
				"DZ=DY", "EA=DZ", "EB=EA", "EC=EB", "ED=EC", "EE=ED", "EF=EE",
				"EG=EF", "EH=EG", "EI=EH", "EJ=EI", "EK=EJ", "EL=EK", "EM=EL",
				"EN=EM", "EO=EN", "EP=EO", "EQ=EP", "ER=EQ", "ES=ER", "ET=ES" };
		BooleanModel m = new EquationModel(eqs);

		byte[] init = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0 };
		BooleanState initState = new BooleanState(init);
		byte[] expRes = { 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0 };

		// Test for EquationModel and TruthTableModel
		for (int i = 0; i < 2; i++) {
			if (i == 1)
				m = m.generateTruthTableModel();

			BooleanSimulation boolSim = new BooleanSimulation(m);
			BooleanState[] resState = boolSim.performSimulation(initState, 4,
					UpdatePolicy.SYNC);
			byte[] res = resState[3].b;

			for (int j = 0; j < 150; j++) {
				assertTrue("j=" + j + " ,expRes[j]= " + expRes[j] + " ,res[j] "
						+ res[j], expRes[j] == res[j]);
			}

		}
	}
	
	
	@Test
	public void partialSS() throws InvalidEquationException {
		
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
//		System.out.println("\nSync ST graph from initial:");
//		BooleanGlobals.printSTGraph(sp);
//		// steady state
//		System.out.println("Number of steady states: " + sp.ss().size());
//		
//		System.out.println("Partial steady state:");
//		System.out.println(Arrays.toString(sp.partialSteadyState()));
		byte[] res = sp.partialSteadyState();
		
		byte[] expRes = {1,1,-1,-1};
		assertTrue(Arrays.equals(res, expRes));
	}
}
