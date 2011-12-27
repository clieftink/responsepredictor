package nl.nki.responsepredictor.xgmml;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import nl.nki.responsepredictor.Network;
import nl.nki.responsepredictor.RpTestData;
import nl.nki.responsepredictor.model.RpEdge;
import nl.nki.responsepredictor.model.RpNode;

import org.junit.Test;
import org.xml.sax.SAXException;

public class XgmmlDomParserTest {

	@Test
	public void network() throws ParserConfigurationException, SAXException, IOException {
		//1. define input
		String file = RpTestData.getNetworkAsString(4);
		
		//2. run method
		XgmmlDomParser parser = new XgmmlDomParser();
		Network network = parser.network(file, true);
		
		//3. evaluate
		
		//expected values for the three nodes
		String[] id= {"-1","-2","-3"};
		String[] canonicalName = {"AA","BB","CC"};
		 String[] label = {"A","B","C"};
		 int type =1;
		 double weight = 0;
		 double state = 0;
		 String[] notes ={"notesA","notesB","notesC"};
		 String[] hillValues = {"AKT.pT308","AMPK.PT172","cJUN.pS73"};
		 
		 RpNode[] expNodes = new RpNode[3];
		 for (int i=0; i < 3; i++){
			 HashMap<String, String> idMaps = new HashMap<String, String>();
			 idMaps.put("hill_2011",hillValues[i]);
//			 if (i==0)
//				 idMaps.put(DataSet.BENDER_2010,"Akt");		 				 
			 
			 RpNode node = new RpNode(id[i], canonicalName[i], label[i],  type, weight,
					 state,  notes[i], idMaps);			 
			 expNodes[i]=node;
		 }
		 
		 //expected values for two edges
		 String[] ids = {"1","2"};
		 String[] labels ={"A (1) B","A (-1) C"};
		 String[] targets={"-2","-3"}; 
		 int[] interaction={1,-1};
		 //TODO parse notes and refs
		 HashMap<String,String> refs = null;
		 String edgeNotes=null;
		 
		 RpEdge[] expEdges = new RpEdge[2];
		 for (int i=0; i < 2; i++){		 
			 expEdges[i] = new RpEdge(ids[i],labels[i],"-1",targets[i],interaction[i], 0, refs,edgeNotes);
		 }
		Network expNetwork = new Network(expNodes,expEdges);
		
		assertTrue(expNetwork.equals(network));
		
	}
	
	@Test
	public void testNetworkSif() throws ParserConfigurationException, SAXException, IOException {
		XgmmlDomParser parser = new XgmmlDomParser();
		Network network = parser.network("/home/cor/ws_csbc/csbc/network/csbc_prior.xgmml", false);
		String res = network.toSif();
		int x = 0;
		String res2 = network.listNodes();
		
	}
}
