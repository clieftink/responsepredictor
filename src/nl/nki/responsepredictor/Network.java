package nl.nki.responsepredictor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import nl.nki.responsepredictor.model.AdjacencyMatrix;
import nl.nki.responsepredictor.model.RpEdge;
import nl.nki.responsepredictor.model.RpNode;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.ArrayUtils;

public class Network {
	RpNode[] nodes;
	RpEdge[] edges;

	public Network(RpNode[] nodes, RpEdge[] edges) {
		super();
		this.nodes = nodes;
		this.edges = edges;
	}

	public RpNode[] getNodes() {
		return nodes;
	}

	public void setNodes(RpNode[] nodes) {
		this.nodes = nodes;
	}

	public RpEdge[] getEdges() {
		return edges;
	}

	public void setEdges(RpEdge[] edges) {
		this.edges = edges;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(edges);
		result = prime * result + Arrays.hashCode(nodes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Network other = (Network) obj;
		if (!Arrays.deepEquals(edges, other.edges))
			return false;
		if (!Arrays.deepEquals(nodes, other.nodes))
			return false;
		return true;
	}

	/**
	 * 
	 * @return a map of Nodes with value on the given parameter field as key
	 * 
	 */
	public LinkedHashMap<String, RpNode> getKeyNodeMap(String keyField) {
		LinkedHashMap<String, RpNode> nodesMap = new LinkedHashMap<String, RpNode>();
		for (int i = 0; i < nodes.length; i++) {
			String key = null;
			if (keyField.equals("id"))
				key = nodes[i].getId();
			else if (keyField.equals("label"))
				key = nodes[i].getLabel();

			nodesMap.put(key, nodes[i]);
		}
		return nodesMap;
	}

	/**
	 * 
	 * @return LinkedHashMap for players with one or more incoming edges. Player
	 *         id as key, and the object is a ArrayList<Edge>
	 */

	public LinkedHashMap<String, ArrayList<RpEdge>> incomingEdges() {
		LinkedHashMap<String, ArrayList<RpEdge>> res = new LinkedHashMap<String, ArrayList<RpEdge>>();

		for (int i = 0; i < edges.length; i++) {
			RpEdge edge = edges[i];

			if (res.get(edge.getTarget()) == null) {
				res.put(edge.getTarget(), new ArrayList<RpEdge>());
			}
			res.get(edge.getTarget()).add(edge);
		}

		return res;
	}

	public String equation(RpNode node) {

		if (node.getType() == 1) {

		} else if (node.getType() == -2) {

		}
		String res = null;

		// the same code, just a

		// "C=A$!B"

		return res;

	}

	/***
	 * 
	 * @return a String of equations appropriate for SimBool package .. based on
	 *         id as node identifier
	 */

	public String[] equations() {

		LinkedHashMap<String, ArrayList<RpEdge>> incEdges = incomingEdges();
		LinkedHashMap<String, RpNode> nodesMap = this.getKeyNodeMap("id");

		ArrayList<String> res = new ArrayList<String>();
		// Iterate through all nodes of the network
		for (int i = 0; i < nodes.length; i++) {

			// select for players (= not and-gates)
			if (nodes[i].getType() == 1) {
				StringBuffer sb = new StringBuffer();
				sb.append(nodes[i].getId());
				sb.append("=");

				// loop through incoming edges for a player
				// Incoming edges for player
				ArrayList<RpEdge> ip = incEdges.get(nodes[i].getId());
				if (ip == null) {
					sb.append("");
				} else { // node has 1 or more incoming edges
					// go through all the edges
					for (int j = 0; j < ip.size(); j++) {

						// single incoming edge for player
						RpEdge ipe = ip.get(j);
						RpNode srcNode = nodesMap.get(ipe.getSource());
						if (srcNode.getType() == 1) {
							if (j > 0) {
								sb.append("|");
							}
							;
							if (ipe.getInteraction() == -1)
								sb.append("!");
							sb.append(srcNode.getId());
						} else if (nodesMap.get(ipe.getSource()).getType() == -2) {
							// take all the parents and put them in an or
							// relation for the target node of the and-gate
							ArrayList<RpEdge> andGateIncEdges = incEdges
									.get(ipe.getSource());
							for (int k = 0; k < andGateIncEdges.size(); k++) {
								RpNode incNode = nodesMap.get(andGateIncEdges
										.get(k).getSource());
								// TODO base running code on the absolute values
								// of the nodes in stead of the label
								if (k > 0) {
									sb.append('&');
								}
								if (andGateIncEdges.get(k).getInteraction() == -1)
									sb.append("!");
								sb.append(incNode.getId());
							}
						}
					}
				}
				res.add(sb.toString());
			}
		}

		// = { "A=", "B=", "C=A$!B" };
		return res.toArray(new String[0]);

	}

	public int getMaxValueEdgeId() {
		int maxValueEdgeId = 1;

		for (int i = 0; i < edges.length; i++) {
			RpEdge edge = edges[i];
			// in case start with e, remove this
			String id = edge.getId();
			if (id.substring(0, 1).equals("e"))
				id = id.substring(1);

			if (maxValueEdgeId < Integer.valueOf(id))
				maxValueEdgeId = Integer.valueOf(id);
		}

		return maxValueEdgeId;
	}

	/**
	 * @return index of and edge in the prior network. -1 is returned if .. edge
	 *         was not present
	 */
	public int indexEdge(String srcNodeId, String targetNodeId) {
		int idx = -1;

		if (edges.length > 0) {
			boolean cont = true;
			while (cont) {
				++idx;

				if (edges[idx].getSource().equals(srcNodeId)
						&& edges[idx].getTarget().equals(targetNodeId))
					cont = false;
				else if (cont && (idx + 1 >= edges.length)) {
					cont = false;
					idx = -1;
				}
			}
		}

		return idx;
	}

	public HashMap<String, String> nodeLabels() {

		HashMap<String, String> res = new HashMap<String, String>();
		for (int i = 0; i < nodes.length; i++) {
			res.put(nodes[i].getId(), nodes[i].getLabel());
		}
		return res;

	}

	/**
	 * 
	 * @param property
	 * @return MultiKey (source & target) map to specific property, fe. id or
	 *         interaction
	 */

	public MultiKeyMap srcTargetEdge() {
		MultiKeyMap srcTargetEdge = new MultiKeyMap();
		for (int i = 0; i < edges.length; i++) {
			RpEdge edge = edges[i];
			// in case start with e, remove this
			srcTargetEdge.put(edge.getSource(), edge.getTarget(), edge);
		}
		return srcTargetEdge;
	}

	/**
	 * @ *
	 * 
	 * @return: AdjacencyMatrix with the nodes in a predefined order
	 * @throws IOException
	 */
	public AdjacencyMatrix toAdjacencyMatrix(String dataSet) {

		// build ids
		// Collect ids as Integer in order to sort them.
		// In case of need of altid, for example to match with existing
		// datasets, fill in accordingly

		// the String[] of ids to be used in the DBI run, both for the prior
		// double[][] and the timecourse double[][][] will be the node ids from
		// the
		// prior model sorted. The ids are numbers although put in a String in
		// compliance
		// with the way it is stored in Cytoscape Web.
		// so to have a correct ordering convert to Integer, sortd descending to
		// get order -1,-2,-3 etc.
		ArrayList<String> ids = new ArrayList<String>();

		ArrayList<String> altIds = null;
		if (dataSet != null) {
			altIds = new ArrayList<String>();
		}

		for (int i = 0; i < nodes.length; i++) {
			RpNode node = nodes[i];
			ids.add(node.getId());
			if (dataSet != null && !dataSet.equals("upload")) {
				// TODO
			}
		}

		// Convert ArrayList to Integer[]
		String[] idsArray = new String[ids.size()];
		ids.toArray(idsArray);

		String[] altIdsArray = null;
		if (dataSet != null) {
			altIdsArray = new String[ids.size()];
			altIds.toArray(altIdsArray);
		}
		// //TODO ids & altids sorteren
		// Arrays.sort(idsArray, Collections.reverseOrder());

		// Initialize data and place a 1 for each edge in the correct cell
		double[][] data = new double[idsArray.length][idsArray.length];
		for (int i = 0; i < edges.length; i++) {
			RpEdge edge = edges[i];
			int rowIdx = ArrayUtils.indexOf(idsArray, edge.getSource());
			int colIdx = ArrayUtils.indexOf(idsArray, edge.getTarget());
			data[rowIdx][colIdx] = 1.0;
		}

		return (new AdjacencyMatrix(idsArray, altIdsArray, data));
	}

	/**
	 * 
	 * @param type
	 * @return Array of nodes of Type 2
	 */

	public RpNode[] getNodes(int type) {
		ArrayList<RpNode> nodesOfType = new ArrayList<RpNode>();

		for (int i = 0; i < this.nodes.length; i++)
			if (this.nodes[i].getType() == type)
				nodesOfType.add(this.nodes[i]);

		RpNode[] nodesOfTypeArray = new RpNode[nodesOfType.size()];
		nodesOfType.toArray(nodesOfTypeArray);

		return (nodesOfTypeArray);
	}

	/**
	 * 
	 * @return array of incoming edges for a given nodeId
	 */

	public RpEdge[] getIncEdges(String nodeId) {
		ArrayList<RpEdge> incEdges = new ArrayList<RpEdge>();

		for (int i = 0; i < this.edges.length; i++)
			if (this.edges[i].getTarget().equals(nodeId))
				incEdges.add(this.edges[i]);

		RpEdge[] incEdgesArray = new RpEdge[incEdges.size()];
		incEdges.toArray(incEdgesArray);

		return (incEdgesArray);

	}

	public String toSif() {

		LinkedHashMap<String, RpNode> nodesMap = this.getKeyNodeMap("id");

		StringBuffer res = new StringBuffer();
		for (int i = 0; i < this.edges.length; i++) {
			RpEdge edge = this.edges[i];
			RpNode startNode = nodesMap.get(edge.getSource());
			RpNode targetNode = nodesMap.get(edge.getTarget());
			res.append(startNode.getLabel() + "\t" + edge.getInteraction()
					+ "\t" + targetNode.getLabel() + "\n");
		}

		return (res.toString());
	}

	public String listNodes() {

		

		String[] nodeLabels = new String[this.nodes.length];
		
		for (int i = 0; i < this.nodes.length; i++) 
			nodeLabels[i]= this.nodes[i].getLabel();
		
		//sort alphabetically
		Arrays.sort(nodeLabels);
		
		StringBuffer res = new StringBuffer();
		for (int i = 0; i < nodeLabels.length; i++) {
			if (i > 0)
				res.append("\n");
			res.append(nodeLabels[i]);
		}
		
		int k = 0;
		
		return(res.toString());
		
	}
}
