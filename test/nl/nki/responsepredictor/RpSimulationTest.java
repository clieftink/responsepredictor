package nl.nki.responsepredictor;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.json.JSONObject;
import nl.nki.responsepredictor.bool.BooleanModel;
import nl.nki.responsepredictor.bool.EquationModel;
import nl.nki.responsepredictor.bool.InvalidEquationException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class RpSimulationTest {

	RpSimulation rpSim = new RpSimulation();
	String simType = "TIMECOURSE";
	String fixedString = "[]";
	int timeTo = 2;
	
	String network1String;
	String network2String;
	Network network1;
	Network network2;

	LinkedHashMap<String, Double> startValues = new LinkedHashMap<String, Double>();

	@Before
	public void init() throws SAXException, IOException, InvalidEquationException, ParserConfigurationException {
		startValues.put("-1", 1.0);
		startValues.put("-2", 1.0);
		startValues.put("-3", 0.0);
		network1String = RpTestData.getNetworkAsString(1);
		network1 = NetworkFactory.fromXgmmlAsString(network1String);
		
		network2String = RpTestData.getNetworkAsString(2);
		network2 = NetworkFactory.fromXgmmlAsString(network2String);

	}

	@Test
	public void testRunBoolAndGate() throws Exception {
		RpHelper rp = new RpHelper();
		String[] fixed = rp.jsonArrayToStringArray(fixedString);

		Matrix simResult = rpSim.runNetwork(simType, network1, startValues, fixed,
				timeTo);

		String[] specIdStr = simResult.getColIds();
		double[][] result = simResult.getData();
		double[][] expResult = { { 1.0, 1.0, 0.0 }, { 1.0, 1.0, 0.0 } };
		String[] expSpecIdStr = { "-1", "-2", "-3" };

		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 3; j++) {
				assertTrue("i=" + i + ", j=" + j,
						result[i][j] == expResult[i][j]);
			}

		for (int i = 0; i < 3; i++)
			assertTrue("i=" + i + "specIdStr[i]=" + specIdStr[i]
					+ ", expSpecIdStr[i]=" + expSpecIdStr[i],
					specIdStr[i].equals(expSpecIdStr[i]));
	}

	@Test
	public void testRunJSONBoolAndGate() throws Exception {

		JSONObject simResultJSON = rpSim.runJSON(simType, network1String,
				startValues, fixedString, timeTo);
		String simResult = simResultJSON.toString();
		String expResult = "{\"0\":{\"-1\":1,\"-2\":1,\"-3\":0},\"1\":{\"-1\":1,\"-2\":1,\"-3\":0}}";

		assertTrue("Result: " + simResult + ", expResult: " + expResult,
				simResult.equals(expResult));

	}

	@Test
	public void testRunJSONSteadyState() throws Exception {

		String simType = "STEADYSTATE";
		// Also test that it can handle fixed and/or startValues that are not
		// part of the network, as this can be the case of checking
		// observations.
		LinkedHashMap<String, Double> startValues = new LinkedHashMap<String, Double>();
		startValues.put("-1", 1.0); // A
		startValues.put("-2", 0.0); // B
		startValues.put("-3", 0.0); // R
		JSONObject simResultJSON = rpSim.runJSON(simType, network2String,
				startValues, fixedString, 0);
		String simResult = simResultJSON.toString();

		//
		String expResult = "{\"0\":{\"-1\":1,\"-2\":1,\"-3\":0.3}}";

		assertTrue("Result: " + simResult + ", expResult: " + expResult,
				simResult.equals(expResult));

	}

	@Test
	public void testRunJSONSteadyStateFixed() throws Exception {

		String simType = "STEADYSTATE";
		String fixed = "[\"-2\"]";
		LinkedHashMap<String, Double> startValues = new LinkedHashMap<String, Double>();
		startValues.put("-1", 1.0);
		startValues.put("-2", 0.0);
		startValues.put("-3", 0.0);

		JSONObject simResultJSON = rpSim.runJSON(simType, network2String,
				startValues, fixed, 0);
		String simResult = simResultJSON.toString();

		String expResult = "{\"0\":{\"-1\":1,\"-2\":0,\"-3\":0}}";

		assertTrue("Result: " + simResult + ", expResult: " + expResult,
				simResult.equals(expResult));

	}

	@Test
	public void testRunPartialSteadyState() throws Exception {
		String simType = "STEADYSTATE";

		//define model
		String[] eqs = new String[] {
				"-1=",  // no inputs, always keeps the initial state
				"-2=-1",
				"-3=-2&!-4",
				"-4=-3"
		};
		EquationModel model = new EquationModel(eqs);  // throws InvalidEquationException

		String[] fixed = new String[1];
		fixed[0]="-1";
		
		LinkedHashMap<String, Double> startValues = new LinkedHashMap<String, Double>();
		startValues.put("-1", 1.0);
		startValues.put("-2", 0.0);
		startValues.put("-3", 0.0);
		startValues.put("-4", 0.0);

		int timeTo = 50;

//		public Matrix run(String simType, EquationModel model,
//				LinkedHashMap<String, Double> startValues, String[] fixed,
//				int timeTo)
		Matrix res = rpSim.runNetwork(simType, model, startValues, fixed, timeTo);

	    String[] rowIds = null;
	    String[] colIds = {"-1","-2","-3","-4"};
	    double[][] data = new double[1][4];
	    data[0][0] = 1;
	    data[0][1] = 1;
	    data[0][2] = -1;
	    data[0][3] = -1;
		Matrix resExp = new Matrix(rowIds,colIds,data);

		assertTrue(res.equals(resExp));

	}
	
	@Test
	public void testAddResponse() {
		
		String[] rowIds = null;
	    String[] colIds = {"-1","-2"};
		double[][] data = new double[1][2];
	    data[0][0] = 1;
	    data[0][1] = 1;
	    Matrix simResult = new Matrix(rowIds,colIds,data);
	    
		Matrix res = rpSim.addResponse(simResult, network2);

	    String[] expColIds = {"-1","-2","-3"};
	    data = new double[1][3];
	    data[0][0] = 1;
	    data[0][1] = 1;
	    data[0][2] = 0.3;
		Matrix resExp = new Matrix(null,expColIds,data);

		assertTrue(res.equals(resExp));
		
		
	}

}
