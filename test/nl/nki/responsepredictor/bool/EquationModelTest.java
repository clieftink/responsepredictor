package nl.nki.responsepredictor.bool;

import static org.junit.Assert.assertTrue;

import nl.nki.responsepredictor.exception.ItemExistsException;

import org.junit.Test;

public class EquationModelTest {

	@Test
	public void addEdgeNoInputYet() throws InvalidEquationException, ItemExistsException {
		
		//Check 1: in case target does not have any input nodes yet
		String[] eqs = { "A=", "B=", "C=A"};
		EquationModel e = new EquationModel(eqs);
		e.addEdge("A", "B", -1);

		int[][] inputs = e.getInputs();
		//int[][] expinputs  = {{},{0}};
		assertTrue("inputs[1].length =" + inputs[1].length ,inputs[1].length > 0 );
		assertTrue("inputs[1][0]=" + inputs[1][0], inputs[1][0]==0);
		
		String[] internalEqs= e.getInternalEqs();
		assertTrue("internalEqs[1]=" + internalEqs[1], internalEqs[1].equals("!0"));
		
	}

	@Test
	public void addEdgeAlreadyInput() throws InvalidEquationException, ItemExistsException {
		String[] eqs = { "A=", "B=", "C=A"};
		EquationModel e = new EquationModel(eqs);

		//check 2: in case target already has one input node
		e.addEdge("B", "C", 1);
		int[][] inputs = e.getInputs();
		assertTrue("inputs[2][1]=" + inputs[2][1], inputs[2][1]==1);
		String[] internalEqs= e.getInternalEqs();
		assertTrue("internalEqs[2]=" + internalEqs[2], internalEqs[2].equals("0|1"));
		
	}
	
	@Test
	public void addEdgeAlreadyInputAnd() throws InvalidEquationException, ItemExistsException {
		String[] eqs = { "A=", "B=", "C=","D=A&B"};
		EquationModel e = new EquationModel(eqs);

		//check 2: in case target already has one input node
		e.addEdge("C", "D", 1);
		int[][] inputs = e.getInputs();
		assertTrue("inputs[3][2]=" + inputs[3][2], inputs[3][2]==2);
		String[] internalEqs= e.getInternalEqs();
		assertTrue("internalEqs[3]=" + internalEqs[3], internalEqs[3].equals("0&1|2"));
		
	}
	
	
	
	@Test (expected=ItemExistsException.class)
	public void addEdgeAlreadyExists() throws InvalidEquationException, ItemExistsException {
		String[] eqs = { "A=", "B=A"};
		EquationModel e = new EquationModel(eqs);
		e.addEdge("A", "B", 1);
		
		e.getVariables();
		int[][] inputs = e.getInputs();
		
		//int[][] expinputs  = {{},{0}};
		assertTrue("inputs[1].length =" + inputs[1].length ,inputs[1].length > 0 );
		assertTrue("inputs[1][0]=" + inputs[1][0], inputs[1][0]==0);

		
	}

}
