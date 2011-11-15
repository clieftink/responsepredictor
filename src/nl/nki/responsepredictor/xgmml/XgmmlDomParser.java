package nl.nki.responsepredictor.xgmml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.nki.responsepredictor.Network;
import nl.nki.responsepredictor.model.RpEdge;
import nl.nki.responsepredictor.model.RpNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XgmmlDomParser {

	/**
	 * @param e
	 *            : an xgmml entry node or edge
	 * @param attName
	 * @return empty String in attribute not found
	 */
	public String getValueAtt(Element e, String attName) {
		NodeList listA = e.getElementsByTagName("att");
		String result = "";
		boolean notFound = true;
		int j = -1;
		while (j + 1 < listA.getLength() && notFound) {
			j++;
			Element a = (Element) listA.item(j);
			if (a.getAttribute("name").toLowerCase()
					.equals(attName.toLowerCase())) {
				result = a.getAttribute("value");
				notFound = false;
			}
		}
		return result;
	}

	public RpNode[] networkNodes(Element r) {
		// create conversion table from id (key) to label (value)
		NodeList nodeList = r.getElementsByTagName("node");
		RpNode[] nodes = new RpNode[nodeList.getLength()];
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			NamedNodeMap atts = node.getAttributes(); // attributes within the
														// node tag
			String id = atts.getNamedItem("id").getNodeValue();
			String label = atts.getNamedItem("label").getNodeValue();

			// put all the attributes in the corresponding member of RpNode
			// object.
			int type = 0;
			double weight = 0;
			double state = 0;
			String canonicalName = null;
			String notes = null;
			NodeList idMapsList = null;
			HashMap<String, String> idMaps = null;

			// Other attributes, defined outside the node tage
			// f.e. "<att name=\"state\" type=\"real\" value=\"0\"/>"
			NodeList otherAtts = node.getChildNodes();
			for (int j = 0; j < otherAtts.getLength(); j++) {
				Node otherAtt = otherAtts.item(j);
				String nodeName = otherAtt.getNodeName();
				
				if (otherAtt.getNodeName().equals("att")) {
					
					// attributes within the att  tag
					NamedNodeMap otherAttAtt = otherAtt.getAttributes(); 
					String otherAttAttName = otherAttAtt.getNamedItem("name")
							.getNodeValue();
					String otherAttValue = null;
					if (otherAttAttName.equals("Type")
							|| otherAttAttName.equals("weight")
							|| otherAttAttName.equals("state")							
							|| otherAttAttName.equals("canonicalName")
							|| otherAttAttName.equals("notes"))
						otherAttValue = otherAttAtt.getNamedItem("value")
								.getNodeValue();
					if (otherAttAttName.equals("Type"))
						type = Integer.valueOf(otherAttValue).intValue();
					else if (otherAttAttName.equals("weight")) {
						Scanner scanner = new Scanner(otherAttValue);
						if (scanner.hasNextDouble())
							state = scanner.nextDouble();
					} else if (otherAttAttName.equals("state")) {
						Scanner scanner = new Scanner(otherAttValue);
						if (scanner.hasNextDouble())
							state = scanner.nextDouble();
					} else if (otherAttAttName.equals("canonicalName"))
						canonicalName = otherAttValue;
					else if (otherAttAttName.equals("notes"))
						notes = otherAttValue;
					else if (otherAttAttName.equals("idMaps")) {
						idMapsList = otherAtt.getChildNodes();
						idMaps = new HashMap<String, String>();
						for (int k = 0; k < idMapsList.getLength(); k++) {
							// [name="hill_2011", type="string", value="AKT.pT308"]
							Node idMapListItem = idMapsList.item(k);
							//\n are also considered items [#text: \n ],so filter for that with selection for
							//att
							if (idMapListItem.getNodeName().equals("att")) {
								NamedNodeMap idMapListItemAtts = idMapListItem
										.getAttributes();
								//TODO translate name into a Dataset
								String idMapListItemDataset = idMapListItemAtts
										.getNamedItem("name").getNodeValue();
								
								Node itemValue = idMapListItemAtts.getNamedItem("value");
								String idMapListItemValue = "";
								if (itemValue != null)
									idMapListItemValue = itemValue.getNodeValue();
								
								// TODO make flexibel for other datasets
								idMaps.put("hill_2011", idMapListItemValue);
							}
						}
					}
				}
			}
			nodes[i] = new RpNode(id, canonicalName, label, type, weight, state, notes,
					idMaps);
		}
		return nodes;
	}

	public RpEdge[] edges(Element r) {
		// create conversion table from id (key) to label (value)

		NodeList list = r.getElementsByTagName("edge");
		RpEdge[] edges = new RpEdge[list.getLength()];
		for (int i = 0; i < list.getLength(); i++) {
			Element e = (Element) list.item(i);

			String id = e.getAttribute("id");
			String label = e.getAttribute("label");
			String source = e.getAttribute("source");
			String target = e.getAttribute("target");

			double state = 0;
			/*
			 * In case in the upload file some but not all edges have the state
			 * attribute, .. apparently cytoscapeweb gives it the attribute and
			 * gives it an empty value
			 */
			int interaction = 0; // neutral value
			if (!this.getValueAtt(e, "interaction").equals(""))
				interaction = Integer.valueOf(
						this.getValueAtt(e, "interaction")).intValue();
			
			
			double weight = 0; // neutral value
			if (!this.getValueAtt(e, "weight").equals(""))
				weight = Double.valueOf(
						this.getValueAtt(e, "weight")).doubleValue();

			String canonicalName = null;
			if (!this.getValueAtt(e, "canonicalName").equals(""))
				canonicalName = this.getValueAtt(e, "canonicalName");

			// TODO parse notes and refs
			HashMap<String, String> refs = null;
			String notes = null;

			edges[i] = new RpEdge(id, label, source, target, interaction, weight,refs,
					notes);
		}
		return edges;
	}

	/**
	 * @param file
	 *            : xgmml file, can be a filename or the content in String
	 *            format. Information about .. this is given via the second
	 *            parameter asString.
	 * @param asString
	 *            : false, the parameter 'file' contains the filename, true: the
	 *            content.
	 * @return: information in a Network object
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public Network network(String file, boolean asString)
			throws ParserConfigurationException, SAXException, IOException {
		Network network = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document d = null;
		if (asString) {
			InputStream is = new ByteArrayInputStream(file.getBytes("UTF-8"));
			InputSource isrc = new org.xml.sax.InputSource(is);
			d = db.parse(isrc);

		} else {
			d = db.parse(file);
		}

		Element r = (Element) d.getDocumentElement();
		RpNode[] expNodes = networkNodes(r);
		RpEdge[] expEdges = edges(r);

		network = new Network(expNodes, expEdges);

		return network;

	}

}
