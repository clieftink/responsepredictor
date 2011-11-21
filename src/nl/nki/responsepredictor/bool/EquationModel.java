package nl.nki.responsepredictor.bool;

/**
 * Created by Jan Krumsiek
 * Released under GPL.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

import nl.nki.responsepredictor.exception.ItemExistsException;

import org.apache.commons.lang.ArrayUtils;

/**
 * Implementation of a BooleanModel with internal equation representation. Does
 * not suffer from exponentially growing truth tables, but will be slower than a
 * TruthTableModel.
 * 
 * This class uses a simple tokenization- and stack based equation parsing
 * approach.
 */

public class EquationModel extends BooleanModel {

	// internal fields
	private Map<String, Integer> varMap = new HashMap<String, Integer>();
	private String[] internalEqs;
	private String[] variables;
	private int[][] inputs; // which factor is an input to which factor?
							// dim 1 : the target factors
							// dim 2 : the source factors.

	// constants
	private final String ops = "|&!+/*"; // not all of them are valid, some
											// will throw errors
	private int[] opPriorities;

	private void initOpPriorities() {
		opPriorities = new int[256];
		opPriorities['|'] = 1;
		opPriorities['&'] = 2;
		opPriorities['!'] = 3;
	}

	/**
	 * Generates a new EquationModel from a given set of equations.
	 * 
	 * @param eqs
	 * @throws InvalidEquationException
	 *             if the equation is invalid
	 */
	public EquationModel(String[] eqs) throws InvalidEquationException {

		initOpPriorities();

		ArrayList<String> internal = new ArrayList<String>();
		List<Integer> assignedTo = new ArrayList<Integer>();
		List<List<Integer>> ainputs = new ArrayList<List<Integer>>();

		// iterate over equations
		for (int i = 0; i < eqs.length; i++) {
			// find the '='
			int ieq = eqs[i].indexOf('=');
			if (ieq == -1)
				throw new InvalidEquationException(
						"Missing assignment symbol = in " + eqs[i]);
			// map assigned variable
			int iassign = mapVariable(eqs[i].substring(0, ieq));
			assignedTo.add(iassign);
			// ensure arraylists are large enough
			for (int j = internal.size(); j <= iassign; j++) {
				internal.add(null);
				ainputs.add(null);
			}

			if (eqs[i].substring(ieq + 1).length() > 0) {

				// tokenize equation, build up internal one
				String delims = "()" + ops;
				StringTokenizer tokenizer = new StringTokenizer(
						eqs[i].substring(ieq + 1), delims, true); // do not
																	// return
																	// delimiters
																	// as tokens
				StringBuilder internalEq = new StringBuilder();
				ArrayList<Integer> curinputs = new ArrayList<Integer>();
				while (tokenizer.hasMoreTokens()) {
					String tok = tokenizer.nextToken();
					if (!delims.contains(tok) && tok.trim().length() > 0) {
						// this is a variable, map
						int mapped = mapVariable(tok.trim());
						internalEq.append(mapped);
						curinputs.add(mapped);
					} else
						// just append
						internalEq.append(tok);
				}
				// add
				internal.set(iassign, internalEq.toString());
				ainputs.set(iassign, curinputs);
			} else {
				// add
				internal.set(iassign, null);
				ainputs.set(iassign, null);
			}

		}

		// ensure there is one equation for each player
		for (int i = internal.size(); i < varMap.size(); i++) {
			internal.add(null);
			ainputs.add(null);
		}

		// convert to array
		internalEqs = internal.toArray(new String[0]);

		// we run each equation once with a zero vector
		// the trick is that the parser throws a RuntimeException (because it
		// crashes) which we catch here
		// these errors will not happen later on again
		byte[] state = new byte[varMap.size()];
		for (int i = 0; i < internalEqs.length; i++) {
			try {
				singleUpdate(i, state);
			} catch (RuntimeException e) {
				// e.printStackTrace();
				throw new InvalidEquationException("Invalid equation: "
						+ eqs[assignedTo.get(i)]);
			}
		}

		// assemble variable list
		variables = new String[varMap.size()];
		for (String v : varMap.keySet())
			variables[varMap.get(v)] = v;

		// convert inputs to array of arrays
		inputs = new int[ainputs.size()][];
		for (int i = 0; i < ainputs.size(); i++) {
			if (ainputs.get(i) == null)
				inputs[i] = new int[0];
			else {
				inputs[i] = new int[ainputs.get(i).size()];
				for (int j = 0; j < ainputs.get(i).size(); j++)
					inputs[i][j] = ainputs.get(i).get(j);
			}
		}

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
		return inputs;
	}

	public String[] getInternalEqs() {
		return internalEqs;
	}

	/**
	 * Returns the internal identifier for a given string variable,
	 * automatically grows
	 */
	private int mapVariable(String var) {
		// find it
		Integer index = varMap.get(var);
		// create, if not existing
		if (index == null) {
			index = varMap.size();
			varMap.put(var, index);
		}
		// return
		return index;

	}

	/**
	 * 
	 */
	@Override
	public byte singleUpdate(int factor, byte[] state) {

		// get the equation
		String eq = this.internalEqs[factor];

		if (eq == null)
			return state[factor]; // don't change anything

		Stack<Byte> valueStack = new Stack<Byte>();
		Stack<Character> opStack = new Stack<Character>();

		StringTokenizer tokenizer = new StringTokenizer(eq, "()" + ops, true);
		while (tokenizer.hasMoreTokens()) {
			String tok = tokenizer.nextToken();
			// check what this is
			if (tok.charAt(0) == '(')
				// push onto operator stack
				opStack.push('(');
			else if (tok.charAt(0) == ')') {
				// process up to the corresponding opening parenthesis
				Character o;
				while ((o = opStack.pop()) != '(') {
					applyOperator(o, valueStack);
				}

			} else if (ops.contains(tok)) {
				// this is an operator

				// work down queue if we have operators with higher priority
				// still in there
				int thisPrio = opPriorities[tok.charAt(0)];
				while (!opStack.isEmpty()
						&& opPriorities[opStack.peek()] >= thisPrio) {
					applyOperator(opStack.pop(), valueStack);
					// for debugging:
					// System.out.println("v:" + valueStack + ";  o: " +
					// opStack);
				}
				// push to the stack
				opStack.push(tok.charAt(0));
			} else if (containsOnlyNumbers(tok)) {
				// System.out.println("A NUMBER: " + tok);
				// look up its value in the current state, push onto to the
				// value stack
				valueStack.push(state[Integer.parseInt(tok)]);
			}

			// for debugging:
			// System.out.println("v:" + valueStack + ";  o: " + opStack);
		}

		// process whatever is left on the operator stack
		opStack.empty();
		while (!opStack.empty()) {
			applyOperator(opStack.pop(), valueStack);
			// for debugging:
			// System.out.println("v:" + valueStack + ";  o: " + opStack);
		}

		// only one value may be left on the value stack
		if (valueStack.size() != 1)
			throw new RuntimeException();

		// value stack contains the final result
		return valueStack.pop();

	}

	/**
	 * takes the current operator and the value stack, evaluates the operator,
	 * and pushes the value back to the stack
	 */
	private void applyOperator(char o, Stack<Byte> valueStack) {
		// apply operator
		byte res = -1;
		if (o == '&')
			// boolean and
			res = (byte) (valueStack.pop() & valueStack.pop());
		else if (o == '|')
			// boolean or
			res = (byte) (valueStack.pop() | valueStack.pop());
		else if (o == '!')
			// boolean not, unary
			res = (byte) (1 - valueStack.pop());
		else
			throw new RuntimeException();

		// push back
		valueStack.push(res);
	}

	/**
	 * determines whether a string contains only number 0-9
	 */
	private boolean containsOnlyNumbers(String str) {

		// It can't contain only numbers if it's null or empty...
		if (str == null || str.length() == 0)
			return false;

		for (int i = 0; i < str.length(); i++) {

			// If we find a non-digit character we return false.
			if (!Character.isDigit(str.charAt(i)))
				return false;
		}

		return true;
	}

	/**
	 * Add an Edge to the network. New Edges are always put in an OR
	 * relationship
	 * 
	 * 
	 * @param source
	 * @param target
	 * @param interaction
	 * @throws ItemExistsException
	 */

	public void addEdge(String source, String target, int interaction)
			throws ItemExistsException {

		// update inputs
		int srcIdx = mapVariable(source);
		int targetIdx = mapVariable(target);

		// check if this has to be in increasing order
		int[][] inputs = this.inputs;
		int[] inputArray = inputs[targetIdx];

		// TODO check if relation already exists. If so, throw checkException
		if (ArrayUtils.contains(inputArray, srcIdx))
			throw new ItemExistsException("source=" + source + " ,target="
					+ target);
		else {

			int[] inputArrayExtended = new int[inputArray.length + 1];
			inputArrayExtended[inputArray.length] = srcIdx;
			System.arraycopy(inputArray, 0, inputArrayExtended, 0,
					inputArray.length);
			inputs[targetIdx] = inputArrayExtended;
			this.inputs = inputs;

			// update internalEqs
			String[] eqs = this.internalEqs;
			String eq = eqs[targetIdx];

			if (eq == null)
				eq = "";

			// todo if | then new one also inf if, else &
			// for now just |
			if (eq.length() > 0) {
				eq += "|";
			}

			if (interaction == -1) {
				eq += "!";
			}
			eq += srcIdx;

			this.internalEqs[targetIdx] = eq;

		}

	}

}
