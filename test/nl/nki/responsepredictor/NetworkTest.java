package nl.nki.responsepredictor;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.xml.parsers.ParserConfigurationException;

import nl.nki.responsepredictor.model.RpEdge;
import nl.nki.responsepredictor.model.RpNode;
import nl.nki.responsepredictor.xgmml.XgmmlDomParser;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class NetworkTest {

	String network = "<graph label=\"Cytoscape Web\" directed=\"1\" Graphic=\"1\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" "
		+ "xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "
		+ "xmlns:cy=\"http://www.cytoscape.org\" xmlns=\"http://www.cs.rpi.edu/XGMML\">"
		+ "<att name=\"documentVersion\" value=\"0.1\"/>"
		+ "<att type=\"string\" name=\"backgroundColor\" value=\"#ffffff\"/>"
		+ "<att type=\"real\" name=\"GRAPH_VIEW_ZOOM\" value=\"1\"/>"
		+ "<att type=\"real\" name=\"GRAPH_VIEW_CENTER_X\" value=\"285\"/>"
		+ "<att type=\"real\" name=\"GRAPH_VIEW_CENTER_Y\" value=\"342.5\"/>"
		+ "<node label=\"A\" name=\"\" id=\"-1\">"
		+ "<att name=\"state\" type=\"real\" value=\"NaN\"/>"
		+ "<att name=\"notes\" type=\"string\" value=\"\"/>"
		+ "<att name=\"canonicalName\" type=\"string\" value=\"A\"/>"
		+ "<att name=\"Type\" type=\"integer\" value=\"1\"/>"
		+ "<graphics x=\"284\" y=\"297.5\" w=\"40\" labelanchor=\"c\" fill=\"#ff99ff\" width=\"3\" outline=\"#ffffff\" h=\"40\" cy:nodeTransparency=\"0.8\" "
		+ "cy:nodeLabelFont=\"Arial-0-15\" type=\"ELLIPSE\"/>"
		+ "</node>"
		+ "<node label=\"B\" name=\"\" id=\"-2\">"
		+ "<att name=\"state\" type=\"real\" value=\"NaN\"/>"
		+ "<att name=\"notes\" type=\"string\" value=\"\"/>"
		+ "<att name=\"canonicalName\" type=\"string\" value=\"B\"/>"
		+ "<att name=\"Type\" type=\"integer\" value=\"1\"/>"
		+ "<graphics x=\"249.5\" y=\"387.5\" w=\"40\" labelanchor=\"c\" fill=\"#ff99ff\" width=\"3\" outline=\"#ffffff\" h=\"40\" cy:nodeTransparency=\"0.8\" "
		+ "cy:nodeLabelFont=\"Arial-0-15\" type=\"ELLIPSE\"/>"
		+ "</node>"
		+ "<edge label=\"A (1) B\" source=\"-1\" target=\"-2\" id=\"e1\" directed=\"true\">"
		+ "<att name=\"canonicalName\" type=\"string\" value=\"A (1) B\"/>"
		+ "<att name=\"state\" type=\"string\" value=\"NaN\"/>"
		+ "<att name=\"refs\" type=\"list\"/>"
		+ "<att name=\"notes\" type=\"string\" value=\"\"/>"
		+ "<att name=\"interaction\" type=\"integer\" value=\"1\"/>"
		+ "<graphics width=\"4\" fill=\"#0b94b1\" cy:sourceArrowColor=\"#000000\" cy:edgeLineType=\"SOLID\" cy:targetArrowColor=\"#000000\" "
		+ "cy:sourceArrow=\"0\" cy:targetArrow=\"6\"/>"
		+ "</edge>"
		+ "</graph>";
	
	String fileName1 = "data/nki/TNBC_jordi_v2_min.xgmml";
	Network network0;
	Network network1;
	Network network2;
	Network network3;
	Network network4;
	
	
	@Before
	public void init() throws SAXException, IOException, ParserConfigurationException {
		String file = RpTestData.getNetworkAsString(4);
		
		XgmmlDomParser parser = new XgmmlDomParser();
		
		//TODO DEBUG
		//network0 = parser.network(fileName1, false);

		// parser with inputAsString and network 1
		String content = RpTestData.getNetworkAsString(1);
		network1 = parser.network(content, true);

		// parser with inputAsString and network 2
		content = RpTestData.getNetworkAsString(2);
		network2 = parser.network(content, true);

		content = RpTestData.getNetworkAsString(3);
		network3 = parser.network(content, true);

		content = RpTestData.getNetworkAsString(4);
		network4 = parser.network(content, true);
		
	}

	
	@Test
	public void incomingEdges() {

		LinkedHashMap<String, ArrayList<RpEdge>> res = network1.incomingEdges();

		// Expected incEdges

		LinkedHashMap<String, ArrayList<RpEdge>> exp = new LinkedHashMap<String, ArrayList<RpEdge>>();

		// src,target, interaction
		RpEdge e1 = new RpEdge("1", "A (1) andAB", "-1", "-4", 1,0,null,null);
		RpEdge e2 = new RpEdge("2", "B (-1) andAB", "-2", "-4", -1,0,null,null);
		RpEdge e3 = new RpEdge("3", "andAB (1) C", "-4", "-3", 1,0,null,null);

		ArrayList<RpEdge> list1 = new ArrayList<RpEdge>();
		list1.add(e1);
		list1.add(e2);
		exp.put("-4", list1);// andAB

		ArrayList<RpEdge> list2 = new ArrayList<RpEdge>(); // for andAB two
															// incoming edges
		list2.add(e3);
		exp.put("-3", list2);

		Iterator<String> it = exp.keySet().iterator();
		while (it.hasNext()) {
			String nodeKey = (String) it.next();
			ArrayList<RpEdge> expList = exp.get(nodeKey);
			ArrayList<RpEdge> resList = res.get(nodeKey);
			
			for (int i = 0; i < expList.size(); i++) {
				assertTrue(
						"nodeKey: " + nodeKey + ", i: " + i + ", expList.get(i): "
								+ expList.get(i) + " ,resList.get(i): "
								+ resList.get(i),
						expList.get(i).equals(resList.get(i)));
			}
		}
	}

	@Test
	public void equations1() {
		String[] res = network1.equations();
		String[] exp = { "-1=", "-2=", "-3=-1&!-2" };
		for (int i = 0; i < 3; i++) {
			assertTrue("i=" + i + "res[i]: " + res[i] + " ,exp[i]" + exp[i],
					res[i].equals(exp[i]));
		}
	}

	@Test
	public void equations2() {
		String[] res = network2.equations();
		String[] exp = { "-1=", "-2=-1", "-3=-2" };
		for (int i = 0; i < 2; i++) {
			assertTrue("i=" + i + "res[i]: " + res[i] + " ,exp[i]" + exp[i],
					res[i].equals(exp[i]));
		}
	}

	@Test
	public void equations3() {
		String[] res = network3.equations();
		String[] exp = { "-1=", "-2=", "-3=-1|!-2" };
		for (int i = 0; i < 2; i++) {
			assertTrue("i=" + i + "res[i]: " + res[i] + " ,exp[i]" + exp[i],
					res[i].equals(exp[i]));
		}
	}

	@Test
	public void indexEdge() {
		int idx = network4.indexEdge("-1", "-3");
		assertTrue("idx: " + idx, idx == 1);
	}

	@Test
	public void getNodes() {
		RpNode[] nodes = network2.getNodes(2);
		
	}

}
