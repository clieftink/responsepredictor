package nl.nki.responsepredictor.model;

import static org.junit.Assert.assertTrue;
import net.sf.json.JSONObject;

import org.junit.Test;

public class MutationsTest {
	
	@Test
	public void convertMutationsToJson() {
		RpNode[] addNodes = new RpNode[2];
		addNodes[0] = new RpNode("-4","DD", "D", 1);
		addNodes[1] = new RpNode("-5","EE","E", 1);

		RpNode[] updateNodes = new RpNode[0];

		String[] removeNodes = { "-1", "-2" };

		// assume for the moment the following result
		RpEdge[] addEdges = new RpEdge[2];
		addEdges[0] = new RpEdge("e3", "CD", "-3", "-4", 1);

		addEdges[1] = new RpEdge("e4", "DE", "-4", "-5", -1);

		RpEdge[] updateEdges = new RpEdge[1];
		updateEdges[0]= new RpEdge("e2", "CD", "-3", "-4", -1);
		
		String[] removeEdges = { "e1" };

		Mutations mut = new Mutations(addNodes, updateNodes, removeNodes,
				addEdges, updateEdges, removeEdges);

		JSONObject resJson = mut.convertToJson();

		// Expected result
		// Convert cno result in JSON object
		String expRes = "{\"addNodes\":[{\"id\":\"-4\",\"label\":\"D\",\"Type\":1,\"canonicalName\":\"DD\",\"weight\":0,\"state\":0,\"notes\":\"\"},"
				+ "{\"id\":\"-5\",\"label\":\"E\",\"Type\":1,\"canonicalName\":\"EE\",\"weight\":0,\"state\":0,\"notes\":\"\"}],"
				+ "\"updateNodes\":[],\"removeNodes\":[\"-1\",\"-2\"],"
				+ "\"addEdges\":[{\"id\":\"e3\",\"label\":\"CD\",\"directed\":true,\"source\":\"-3\",\"target\":\"-4\","
				+ "\"interaction\":1,\"weight\":0}"
				+ ",{\"id\":\"e4\",\"label\":\"DE\",\"directed\":true,\"source\":\"-4\",\"target\":\"-5\","
				+ "\"interaction\":-1,\"weight\":0}],"
				+ "\"updateEdges\":[{\"id\":\"e2\",\"label\":\"CD\",\"directed\":true,\"source\":\"-3\",\"target\":\"-4\","
				+ "\"interaction\":-1,\"weight\":0}],\"removeEdges\":[\"e1\"]}";

		assertTrue(resJson.toString().equals(expRes));
	}

}
