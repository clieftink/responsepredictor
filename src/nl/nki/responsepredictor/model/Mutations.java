package nl.nki.responsepredictor.model;

import java.util.Arrays;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Mutations {
	RpNode[] addNodes;
	RpNode[] updateNodes;
	String[] removeNodes;

	RpEdge[] addEdges;
	RpEdge[] updateEdges;
	String[] removeEdges;

	public Mutations(RpNode[] addNodes, RpNode[] updateNodes,
			String[] removeNodes, RpEdge[] addEdges, RpEdge[] updateEdges,
			String[] removeEdges) {
		super();
		this.addNodes = addNodes;
		this.updateNodes = updateNodes;
		this.removeNodes = removeNodes;
		this.addEdges = addEdges;
		this.updateEdges = updateEdges;
		this.removeEdges = removeEdges;
	}

	public RpNode[] getAddNodes() {
		return addNodes;
	}

	public void setAddNodes(RpNode[] addNodes) {
		this.addNodes = addNodes;
	}

	public RpNode[] getUpdateNodes() {
		return updateNodes;
	}

	public void setUpdateNodes(RpNode[] updateNodes) {
		this.updateNodes = updateNodes;
	}

	public String[] getRemoveNodes() {
		return removeNodes;
	}

	public void setRemoveNodes(String[] removeNodes) {
		this.removeNodes = removeNodes;
	}

	public RpEdge[] getAddEdges() {
		return addEdges;
	}

	public void setAddEdges(RpEdge[] addEdges) {
		this.addEdges = addEdges;
	}

	public RpEdge[] getUpdateEdges() {
		return updateEdges;
	}

	public void setUpdateEdges(RpEdge[] updateEdges) {
		this.updateEdges = updateEdges;
	}

	public String[] getRemoveEdges() {
		return removeEdges;
	}

	public void setRemoveEdges(String[] removeEdges) {
		this.removeEdges = removeEdges;
	}
	
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(addEdges);
		result = prime * result + Arrays.hashCode(addNodes);
		result = prime * result + Arrays.hashCode(removeEdges);
		result = prime * result + Arrays.hashCode(removeNodes);
		result = prime * result + Arrays.hashCode(updateEdges);
		result = prime * result + Arrays.hashCode(updateNodes);
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
		Mutations other = (Mutations) obj;
		if (!Arrays.deepEquals(addEdges, other.addEdges))
			return false;
		if (!Arrays.deepEquals(addNodes, other.addNodes))
			return false;
		if (!Arrays.deepEquals(removeEdges, other.removeEdges))
			return false;
		if (!Arrays.equals(removeNodes, other.removeNodes))
			return false;
		if (!Arrays.deepEquals(updateEdges, other.updateEdges))
			return false;
		if (!Arrays.deepEquals(updateNodes, other.updateNodes))
			return false;
		return true;
	}

	public JSONArray convertNodesToJson(RpNode[] nodes) {
		JSONArray nodesJson = new JSONArray();
		if (nodes != null)
			for (int i = 0; i < nodes.length; i++) {
				RpNode node = nodes[i];
				JSONObject nodeJson = new JSONObject();
				nodeJson.put("id", node.getId());
				nodeJson.put("label", node.getLabel());
				nodeJson.put("Type", node.getType());
				nodeJson.put("canonicalName", node.getCanonicalName()); // redundant info
				nodeJson.put("weight", node.getWeight());
				nodeJson.put("state", node.getState());
				nodeJson.put("notes", node.getNotes());
				nodesJson.add(nodeJson);
			}
		return (nodesJson);
	}

	public JSONArray convertEdgesToJson(RpEdge[] edges) {
		JSONArray edgesJson = new JSONArray();
		if (edges != null) 
			for (int i = 0; i < edges.length; i++) {
				RpEdge edge = edges[i];
				JSONObject edgeJson = new JSONObject();
				edgeJson.put("id", edge.getId());
				edgeJson.put("label", edge.getLabel());
				edgeJson.put("directed", true);
				edgeJson.put("source", edge.getSource());
				edgeJson.put("target", edge.getTarget());
				edgeJson.put("interaction", edge.getInteraction());
				edgeJson.put("weight", edge.getWeight());
				edgeJson.put("refs", edge.getRefs());
				edgeJson.put("notes", edge.getNotes());
				edgesJson.add(edgeJson);
			}
		return(edgesJson);
	}

	public JSONObject convertToJson() {

		JSONObject resultAll = new JSONObject();

		// Nodes : 
		//add
		JSONArray addNodesJson = convertNodesToJson(this.addNodes);
		resultAll.put("addNodes", addNodesJson);

		//update
		JSONArray updateNodesJson = convertNodesToJson(this.updateNodes);
		resultAll.put("updateNodes", updateNodesJson);

		//remove
		JSONArray removeNodesJson = new JSONArray();
		if (this.removeNodes !=null)
			for (int i = 0; i < this.removeNodes.length; i++) 
				removeNodesJson.add(this.removeNodes[i]);
			
		resultAll.put("removeNodes", removeNodesJson);

		// Edges 
		//add
		JSONArray addEdgesJson = convertEdgesToJson(this.addEdges);
		resultAll.put("addEdges", addEdgesJson);
		
		//update
		JSONArray updateEdgesJson = convertEdgesToJson(this.updateEdges);
		resultAll.put("updateEdges", updateEdgesJson);		

		//remove
		JSONArray removeEdgesJson = new JSONArray();
		if (this.removeEdges != null)
			for (int i = 0; i < this.removeEdges.length; i++) 
				removeEdgesJson.add(this.removeEdges[i]);
		resultAll.put("removeEdges", removeEdgesJson);
		
		return (resultAll);

	}

}
