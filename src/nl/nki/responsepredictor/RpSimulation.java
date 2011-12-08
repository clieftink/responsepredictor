package nl.nki.responsepredictor;

import java.util.LinkedHashMap;

import net.sf.json.JSONObject;
import nl.nki.responsepredictor.bool.BooleanSimulation;
import nl.nki.responsepredictor.bool.BooleanSimulation.UpdatePolicy;
import nl.nki.responsepredictor.bool.BooleanState;
import nl.nki.responsepredictor.bool.EquationModel;
import nl.nki.responsepredictor.bool.StateSpace;
import nl.nki.responsepredictor.model.RpEdge;
import nl.nki.responsepredictor.model.RpNode;

import org.apache.commons.lang.ArrayUtils;

public class RpSimulation {

	public Matrix runNetwork(String simType, Network network,
			LinkedHashMap<String, Double> startValues, String[] fixed,
			int timeTo) throws Exception {

		EquationModel boolModel = EquationModelFactory.fromNetwork(network);
		return (this.runNetwork(simType, boolModel, startValues, fixed, timeTo));

	}

	/**
	 * 
	 * 
	 * @param simType
	 * @param network
	 * @param startValues: Linked Hashmap of node-id and startvalue.
	 * @param fixedValues
	 * @param timeTo
	 * @return A simulationResult object. In the object variable result, in the
	 *         rows are the iterations, and in the columns the variables. In
	 *         case no data, the result field will be null.
	 * @throws Exception
	 */

	public Matrix runNetwork(String simType, EquationModel model,
			LinkedHashMap<String, Double> startValues, String[] fixed,
			int timeTo) throws Exception {

		// TODO set in model fixed values as input node
		// TODO net.sf.json.Abstract.JSON has a dependency on
		// org.apache.commons.logging.LogFactory

		// Apply fixed values
		BooleanSimulation sim = new BooleanSimulation(model);
		String[] specIdStr = model.getVariables();

		// Set all fixedValues via setInputSpecies
		if (fixed != null) {
			for (int i = 0; i < fixed.length; i++) {
				String key = fixed[i];
				int keyIdx = ArrayUtils.indexOf(specIdStr, key);
				if (keyIdx > -1)
					sim.setFixedVariable(keyIdx, true);
			}
		}

		// ?TODO create an array with default value Double.NaN;
		RpHelper rph = new RpHelper();
		double[] initValues = rph
				.createSpecIdValueArray(specIdStr, startValues);
		
		//round values for simulation
		initValues= rph.round(initValues, 0);
		

		// Convert initValues to byte array
		byte[] init = new byte[initValues.length];
		for (int i = 0; i < initValues.length; i++)
			// TODO check if NaN produces an casting error
			init[i] = (byte) initValues[i];

		BooleanState initState = new BooleanState(init);

		byte[][] resBytes = null;
		double[][] res = null;
		if (Terms.CalcType.valueOf(simType).equals(Terms.CalcType.STEADYSTATE)) {
			StateSpace sp = sim.calcStateSpace(true, initState, true);
			int n = sp.numVariables();

			// in case of synchronous there is max 1 steady state

			byte[] pss = sp.partialSteadyState();
			if ( pss != null){
				resBytes = new byte[1][n];
				resBytes[0] = pss;
			}else {
				// in case of synchronous there is max 1 steady state
				Object[] ssArray = sp.ss().toArray();
				if (ssArray.length > 0) {
					BooleanState state = (BooleanState) ssArray[0];
					resBytes = new byte[1][n];
					resBytes[0] = state.b;
				}
			}

			// convert bytes to double (for now)
			if (resBytes != null) {
				res = new double[resBytes.length][resBytes[0].length];
				for (int i = 0; i < resBytes.length; i++) {
					for (int j = 0; j < resBytes[0].length; j++) {
						res[i][j] = resBytes[i][j];
					}
				}
			}

		} else if (Terms.CalcType.valueOf(simType).equals(
				Terms.CalcType.TIMECOURSE)) {
			// run sync simulation from certain initial state
			BooleanState[] resState = sim.performSimulation(initState, timeTo,
					UpdatePolicy.SYNC);
			// convert bytes to double (for now)
			if (resState != null) {
				res = new double[resState.length][resState[0].b.length];
				for (int i = 0; i < resState.length; i++) {
					for (int j = 0; j < resState[0].b.length; j++) {
						res[i][j] = resState[i].b[j];
					}
				}
			}
		}

		Matrix simResult = new Matrix(null, specIdStr, res);
		return simResult;

	}

	/**
	 * Run method with json input and output
	 * 
	 */

	// TODO make from simType an Enumeration.
	
	/** 
	 * Calculate values of response variables and these to
	 * .. the simulation result
	 */

	public Matrix addResponse(Matrix simResult, Network network) {
		Matrix res = null;

		// check if there are Response nodes (type 2)
		RpNode[] nodes = network.getNodes(2);

		// If so retrieve regression function: weight as property of inc. edges
		if (nodes.length == 0)
			res = simResult;
		else {

			RpEdge[] incEdges = network.getIncEdges(nodes[0].getId());

			double value = 0; // TODO set to intercept

			// get the simulation value for the start node in the incEdges
			// 0.6
			for (int i = 0; i < incEdges.length; i++)
				value += incEdges[i].getWeight()
						* simResult.getValue(incEdges[i].getSource());

			// double value =

			// intercept = weight value in Response node itself

			// add the values for each of the responses to the simResult

			// values for 1 (f.e. steady state) or more (timecourse with nr
			// iterations > 1

			String[] rowIds = null;

			String[] colIds = (String[]) ArrayUtils.add(simResult.getColIds(),
					nodes[0].getId());

			double[][] data = new double[simResult.getData().length][simResult
					.getData()[0].length + 1];

			// copy data
			for (int i = 0; i < simResult.getData().length; i++)
				for (int j = 0; j < simResult.getData()[0].length; j++)
					data[i][j] = simResult.getData()[i][j];

			// add data
			data[0][simResult.getData()[0].length] = value;

			res = new Matrix(rowIds, colIds, data);
		}
		return res;

	}

	public JSONObject runJSON(String simType, String networkString,
			LinkedHashMap<String, Double> startValues, String fixedString,
			int timeTo) throws Exception {

		Network network = NetworkFactory.fromXgmmlAsString(networkString);

		RpHelper rp = new RpHelper();
		String[] fixed = rp.jsonArrayToStringArray(fixedString);

		Matrix simResult = this.runNetwork(simType, network, startValues,
				fixed, timeTo);

		simResult = addResponse(simResult, network);

		JSONObject res = rp.createJSONObject(simType, simResult);

		return res;
	}

}
