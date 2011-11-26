package nl.nki.responsepredictor.check;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import nl.nki.responsepredictor.EquationModelFactory;
import nl.nki.responsepredictor.Matrix;
import nl.nki.responsepredictor.Network;
import nl.nki.responsepredictor.RpHelper;
import nl.nki.responsepredictor.RpSimulation;
import nl.nki.responsepredictor.bool.EquationModel;
import nl.nki.responsepredictor.model.RpNode;

import org.apache.commons.lang.ArrayUtils;

public class ModelChecker {

	public LinkedHashMap<String, Double> convertFromLabelToId(
			LinkedHashMap<String, Double> inputValues,
			LinkedHashMap<String, RpNode> labelNode) {

		LinkedHashMap<String, Double> outputValues = new LinkedHashMap<String, Double>();
		Set<String> st = inputValues.keySet();
		Iterator<String> itr = st.iterator();
		String id = null;
		while (itr.hasNext()) {
			String key = (String) itr.next();
			id = ((RpNode) labelNode.get(key)).getId();
			outputValues.put(id, inputValues.get(key));
		}

		return outputValues;
	}
	
	
	public String[] convertFromLabelToId(
			String[] inputValues,
			LinkedHashMap<String, RpNode> labelNode) {
		
		String[] outputValues = new String[inputValues.length];
		for (int i= 0; i < inputValues.length; i++) 
			outputValues[i] = ((RpNode) labelNode.get(inputValues[i])).getId();

		return outputValues;
	}

	/**
	 * 
	 * @param model
	 * @param obs
	 * 
	 * @return CheckResult object, with a matrix with in the row dimension the
	 *         observations in the order of the obs-object. In the column
	 *         dimension the variables are in alphabetical order.
	 * 
	 *         If observation does not have a value for a node, it will not be
	 *         checked. The resulting value will -1. If a variable does not have
	 *         a steady state because of oscillation, it gets the value 9
	 * 
	 *         start values can be between 0 and 1. Below 0.5 becomes 0, above 1
	 *         for simulation
	 * 
	 * 
	 * @throws Exception
	 */

	public CheckResult runCheckBool(Network network, Observation[] obs)
			throws Exception {

		EquationModel boolModel = EquationModelFactory.fromNetwork(network);

		RpHelper rph = new RpHelper();

		// create mapping ids and labels
		LinkedHashMap<String, RpNode> idNode = network.getKeyNodeMap("id");
		LinkedHashMap<String, RpNode> labelNode = network
				.getKeyNodeMap("label");

		double[][] data = null;

		// convert ids to labels
		String[] specIdStr = boolModel.getVariables();

		String[] obsNames = new String[obs.length];

		data = new double[obs.length][specIdStr.length];
		// set default values to -1, meaning not too be checked. NaN is not
		// accepted
		// by the library for making an json object.
		for (int i = 0; i < obs.length; i++)
			for (int j = 0; j < specIdStr.length; j++)
				data[i][j] = -1;

		for (int i = 0; i < obs.length; i++) {
			obsNames[i] = obs[i].getName();

			// convert from label to id
			LinkedHashMap<String, Double> startValues = convertFromLabelToId(
					obs[i].getStart(), labelNode);

			// TODO convert from label to id
			String[] fixed = convertFromLabelToId(obs[i].getFixed(),labelNode);
			
			RpSimulation rpSim = new RpSimulation();
			Matrix simResult = rpSim.runNetwork("STEADYSTATE", boolModel,
					startValues, fixed, 0);

			double[][] steadyStates = simResult.getData();

			// TODO convert from label to id
			LinkedHashMap<String, Double> obsEndValues = convertFromLabelToId(
					obs[i].getEnd(), labelNode);

			if (obsEndValues == null)
				throw new Exception("Observation " + i
						+ " has no end values defined!");

			Iterator<String> iterator = obsEndValues.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				// keys in obs are given in terms of label
				// convert to ids

				int j = ArrayUtils.indexOf(specIdStr, key);

				// Check if key is in specIdStr. TODO verify that this check
				// is necessary
				if (j > -1) {
					// !Null means that the node has too be checked
					if (obsEndValues.get(key) != null) {
						// No need to check if obsEndValues[i[j] isNaN, as
						// currently no startvalues can be NaN
						if (steadyStates == null || steadyStates[0][j] == -1.0)
							data[i][j] = 9; // indicates no steady state
						else {
							if (steadyStates[0][j] == rph.round(
									obsEndValues.get(key), 0))
								data[i][j] = 1.0;
							else
								data[i][j] = 0.0;
						}
					}
				}
			}
		}

		// convertIds to labels
		String[] labels = new String[specIdStr.length];
		for (int i = 0; i < specIdStr.length; i++)
			labels[i] = ((RpNode) idNode.get(specIdStr[i])).getLabel();

		Matrix res = new Matrix(obsNames, labels, data);
		res.sortColIdsAlphabet();

		double score = score(res.getData());

		CheckResult chr = new CheckResult(res, score);
		return chr;
	}

	/**
	 * @return Score for modelchecking result The score is the number of correct
	 *         checks divided by total checks. In case not steady (value 9) ,
	 *         this is excluded from calculation.
	 * @param data
	 *            . The following values are possible -1 : not checked 0 :
	 *            checked and incorrect, 9: checked and not steady state, 1 :
	 *            checked and correct
	 */

	public double score(double[][] data) {
		double score = 0;
		int nrChecked = 0;

		if (data != null && data.length > 0 && data[0].length > 0)
			for (int i = 0; i < data.length; i++)
				for (int j = 0; j < data[0].length; j++) {
					if (data[i][j] == 1)
						score += 1;

					if (data[i][j] == 1 || data[i][j] == 0)
						nrChecked += 1;
				}

		double res = score / nrChecked;
		RpHelper rph = new RpHelper();

		if (!new Double(res).equals(Double.NaN))
			res = rph.round(res, 2);

		return res;

	}

	public double score(CheckResult chr) {
		Matrix res = chr.getMatrix();
		double[][] data = res.getData();
		double score = this.score(data);
		return score;
	}
}
