package nl.nki.responsepredictor.check;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashMap;

import nl.nki.responsepredictor.Matrix;
import nl.nki.responsepredictor.Network;
import nl.nki.responsepredictor.bool.BooleanModel;
import nl.nki.responsepredictor.bool.BooleanSimulation;
import nl.nki.responsepredictor.bool.BooleanState;
import nl.nki.responsepredictor.bool.EquationModel;
import nl.nki.responsepredictor.bool.StateSpace;
import nl.nki.responsepredictor.model.RpEdge;
import nl.nki.responsepredictor.model.RpNode;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

public class ModelCheckerTest {

	Network abacNetwork = null;

	@Before
	public void init() {
		// String[] eqs = { "A=", "B=A", "C=!A" };
		// EquationModel m = new EquationModel(eqs);
		RpNode[] nodes = new RpNode[3];
		nodes[0] = new RpNode("-1", "A", "A", 1);
		nodes[1] = new RpNode("-2", "B", "B", 1);
		nodes[2] = new RpNode("-3", "C", "C", 1);

		RpEdge[] edges = new RpEdge[2];
		edges[0] = new RpEdge("-1", "-2", 1);
		edges[1] = new RpEdge("-1", "-3", -1);

		abacNetwork = new Network(nodes, edges);

	}

	@Test
	public void runCheckBool() throws Exception {

		String json = "[{\"id\":1,\"name\":\"ref\",\"start\":{\"A\":\"0\",\"B\":\"0\",\"C\":\"0\"},"
				+ "\"fixed\":[],\"end\":{\"A\":0,\"B\":\"0\",\"C\":\"1\"}},"
				+ "{\"id\":2,\"name\":\"exp\",\"start\":{\"A\":\"0.7\",\"B\":\"0\",\"C\":\"0\"},"
				+ "\"fixed\":[],\"end\":{\"A\":0.8,\"B\":\"0\",\"C\":null}}]";

		Gson gson = new Gson();
		Observation[] obs = gson.fromJson(json, Observation[].class);

		// Run modelchecker
		ModelChecker modelChecker = new ModelChecker();

		CheckResult chr = modelChecker.runCheckBool(abacNetwork, obs);
		Matrix res = chr.getMatrix();
		double[][] data = res.getData();
		// TODO fixed exp values for nodes with fixed values is null
		String[] expRowIds = { "ref", "exp" };
		String[] expColIds = { "A", "B", "C" };

		double[][] expRes = { { 1.0, 1.0, 1.0 }, { 1.0, 0.0, -1 } };

		// TODO check in case an observation does not provide a steady state
		// and therefore result[1]=null

		for (int i = 0; i < 2; i++) {
			assertTrue("i=" + i + ",expRowIds[i]=" + expRowIds[i]
					+ ", res.getRowIds()[i]=" + res.getRowIds()[i],
					expRowIds[i].equals(res.getRowIds()[i]));

			for (int j = 0; j < 3; j++) {
				if (i == 0) {
					assertTrue("j=" + j + ",expColIds[j]=" + expColIds[j]
							+ ", res.getColIds()[j]=" + res.getColIds()[j],
							expColIds[j].equals(res.getColIds()[j]));
				}

				// Double.NaN values cannot be compared with ==
				assertTrue("i=" + i + " ,j=" + j + " ,expRes[i][j]: "
						+ expRes[i][j] + ", data[i][j]:" + data[i][j],
						data[i][j] == expRes[i][j]);
			}
		}
	}

	/**
	 * Returns 9 in case node not in steady state
	 * 
	 * @throws Exception
	 */


 	@Test
	public void runCheckBoolNoSteady() throws Exception {
		String[] eqs = { "A=!B", "B=A" };
		EquationModel m = new EquationModel(eqs);

		RpNode[] nodes = new RpNode[2];
		nodes[0] = new RpNode("-1", "A", "A", 1);
		nodes[1] = new RpNode("-2", "B", "B", 1);

		RpEdge[] edges = new RpEdge[2];
		edges[0] = new RpEdge("-2", "-1", -1);
		edges[1] = new RpEdge("-1", "-2", 1);

		Network network = new Network(nodes, edges);

		String json = "[{\"id\":1,\"name\":\"ref\",\"start\":{\"A\":\"1\",\"B\":\"0\"},"
				+ "\"fixed\":[],\"end\":{\"A\":0,\"B\":\"1\"}}]";

		Gson gson = new Gson();
		Observation[] obs = gson.fromJson(json, Observation[].class);

		// Run modelchecker
		ModelChecker modelChecker = new ModelChecker();
		CheckResult chr = modelChecker.runCheckBool(network, obs);
		double[][] res = chr.getMatrix().getData();

		// TODO fixed exp values for nodes with fixed values is null
		String[] keys = { "A", "B" };
		double[][] expRes = { { 9.0, 9.0 } };

		// TODO check in case an observation does not provide a steady state
		// and therefore result[1]=null

		for (int j = 0; j < 2; j++)
			assertTrue("j=" + j + ",expRes[0][j]: " + expRes[0][j]
					+ ", res[0][j]:" + res[0][j], res[0][j] == expRes[0][j]);

	}

	@Test
	public void runCheckBoolNoSteady2() throws Exception {
		
		RpNode[] nodes = new RpNode[5];
		nodes[0] = new RpNode("-1", "A", "A", 1);
		nodes[1] = new RpNode("-2", "B", "B", 1);
		nodes[2] = new RpNode("-3", "and1", "and1", -2);
		nodes[3] = new RpNode("-4", "C", "C", 1);
		nodes[4] = new RpNode("-5", "D", "D", 1);

		RpEdge[] edges = new RpEdge[5];
		edges[0] = new RpEdge("-1", "-2", 1);
		edges[1] = new RpEdge("-2", "-3", 1);
		edges[2] = new RpEdge("-5", "-3", -1);
		edges[3] = new RpEdge("-3", "-4", 1);
		edges[4] = new RpEdge("-4", "-5", 1);

		Network network = new Network(nodes, edges);

		LinkedHashMap<String, Double> start = new LinkedHashMap<String, Double>();
		start.put("A",1.0);
		start.put("B",0.0);
		start.put("C",0.0);
		start.put("D",0.0);
		
		String[] fixed = new String[1];
		fixed[0]="A";
		
		LinkedHashMap<String, Double> end = new LinkedHashMap<String, Double>();
		end.put("A",1.0);
		end.put("B",1.0);
		end.put("C",0.0);
		end.put("D",0.0);
		
		Observation[] obs = new Observation[1];
		obs[0] = new Observation("1", "obs1",start,fixed,end);
		
		// Run modelchecker
		ModelChecker modelChecker = new ModelChecker();
		CheckResult chr = modelChecker.runCheckBool(network, obs);
		double[][] res = chr.getMatrix().getData();

		// TODO fixed exp values for nodes with fixed values is null
		double[][] expRes = { { 1.0, 1.0, 9.0,9.0} };

		// TODO check in case an observation does not provide a steady state
		// and therefore result[1]=null

		for (int j = 0; j < expRes.length; j++)
			assertTrue("j=" + j + ",\"-2\",expRes[0][j]: " + expRes[0][j]
					+ ", res[0][j]:" + res[0][j], res[0][j] == expRes[0][j]);
	}

	/**
	 * Check when B is fixed to 0, the modelchecker proves right the observation
	 * with end-value 0 for B.
	 * 
	 * @throws Exception
	 */

	@Test
	public void runCheckBoolFixed() throws Exception {

		String json = "[{\"id\":1,\"name\":\"exp\",\"start\":{\"A\":\"1\",\"B\":\"0\",\"C\":\"0\"},"
				+ "\"fixed\":[\"B\"],\"end\":{\"A\":1.0,\"B\":\"0\",\"C\":0}}]";

		Gson gson = new Gson();
		Observation[] obs = gson.fromJson(json, Observation[].class);

		// Run modelchecker
		ModelChecker modelChecker = new ModelChecker();
		CheckResult chr = modelChecker.runCheckBool(abacNetwork, obs);
		Matrix res = chr.getMatrix();
		double[][] data = res.getData();

		// TODO fixed exp values for nodes with fixed values is null
		String expRowIds = "exp";
		String[] expColIds = { "A", "B", "C" };

		// Exp result: 1 = true , 0 = false
		double[][] expRes = { { 1.0, 1.0, 1.0 } };

		// TODO check in case an observation does not provide a steady state
		// and therefore result[1]=null

		assertTrue(
				"expRowIds: " + expRowIds + "res.getRowIds()[0])"
						+ res.getRowIds()[0],
				expRowIds.equals(res.getRowIds()[0]));

		for (int j = 0; j < 3; j++) {
			assertTrue("expColIds[j]=" + expColIds[j] + ", res.getColIds()[j]="
					+ res.getColIds()[j],
					expColIds[j].equals(res.getColIds()[j]));

			// Double.NaN values cannot be compared with ==
			assertTrue("j=" + j + " ,expRes[0][j]: " + expRes[0][j]
					+ ", res[0][j]:" + data[0][j], data[0][j] == expRes[0][j]);
		}
	}

	@Test
	public void score() throws Exception {
		double[][] data = { { 0.0, -1 }, { 1.0, 9 } };
		ModelChecker modelChecker = new ModelChecker();
		double res = modelChecker.score(data);
		double exp = 0.50;
		assertTrue("res: " + res + ",exp: " + exp, res == exp);
	}

}
