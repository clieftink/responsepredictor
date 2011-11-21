package nl.nki.responsepredictor.bool;

/**
 * Created by Jan Krumsiek
 * Released under GPL.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Represents a generated Boolean state spaces, that is 
 * (1) a state-transition graph represented as a list of edges
 * (2) the computed steady states
 * 
 * Avoids power2 representations, all states are arrays of bytes
 */
public final class StateSpace {

	private Set<BooleanState> ss;
	private List<BooleanEdge> edges;
	private int numVariables;
	private boolean sync;
	private List<Node> graph;
	private List<Integer> initial;
	
	private Map<BooleanState,Integer> stateIndex;
	private ArrayList<BooleanState> stateList;


	/**
	 * Returns the list of steady states, as decimal representation. Can be
	 * converted to byte[] using {@link BooleanGlobals#dec2bin(long, int)}
	 */
	public Set<BooleanState> ss() {
		return ss;
	}

	/**
	 * Returns the list of edges in the state-transition graph, or null if no
	 * STG was calculated. Each edge is represented by a 2-element array 
	 * ("from" state and "to" state)
	 */
	public List<BooleanEdge> edges() {
		return edges;
	}

	/**
	 * Returns then number of variables of the underlying model. Required for
	 * binary to decimal conversions.
	 */
	public int numVariables() {
		return numVariables;
	}


	/**
	 * Generates a new state space.
	 * @param ss
	 * @param edges
	 * @param numVariables
	 */
	public StateSpace(Set<BooleanState> ss, List<BooleanEdge> edges, int numVariables, boolean sync) {
		this.ss=ss;
		this.edges=edges;
		this.numVariables=numVariables;
		this.sync=sync;

		// index all the states, if ST graph given
		if (edges!=null) {
			stateIndex=new HashMap<BooleanState, Integer>();
			for (BooleanEdge edge : edges) {
				if (stateIndex.get(edge.from())==null) 
					stateIndex.put(edge.from(), stateIndex.size());
				if (stateIndex.get(edge.to())==null) 
					stateIndex.put(edge.to(), stateIndex.size());
			}	
			// also need the mapping back from numbers to states
			stateList = new ArrayList<BooleanState>();
			for (int i=0; i<stateIndex.size(); i++)
				stateList.add(null);
			for (BooleanState key : stateIndex.keySet()) {
				int i = stateIndex.get(key);
				stateList.set(i, key);
			}
				
			
			// now turn the edges into an actual graph
			graph = new ArrayList<Node>();
			// init nodes
			for (int i=0; i<stateIndex.size(); i++)
				graph.add(new Node(stateIndex.get(stateList.get(i))));
			// create edges
			for (BooleanEdge edge : edges) {
				// node indices
				int i1=stateIndex.get(edge.from());
				int i2=stateIndex.get(edge.to());
				// edge
				graph.get(i1).addSuccessor(graph.get(i2));
				graph.get(i2).addPredecessor(graph.get(i1));
			}
			// find the initial states, the one with no predecessory
			initial = new ArrayList<Integer>();
			for (int i=0; i<stateIndex.size(); i++) {
				if (graph.get(i).predecessors.size()==0)
					initial.add(i);
			}
		}
	
	}


	/**
	 * Distance from initial value to the steady state (number of update steps).
	 * Currently only works for synchronous updating, since there we only have a
	 * single path.
	 */
	public int initToSSDistance() {
		if (!sync)
			throw new IllegalArgumentException("only implemented for synchronous updating");
		return edges.size();
	}
	
	/*
	 * Currently only works for synchronous updating, since there we only have a
	 * single path.
	 */
	public byte[] partialSteadyState() {
		if (!sync)
			throw new IllegalArgumentException("only implemented for synchronous updating");
		// if this SP graph has a steady state, it cannot have a circle or a partial steady state
		if (ss.size()>0)
			return null;
		
		// start from initial state and go forwards until we found the circle
		Node cur=graph.get(initial.get(0));
		List<Node> path = new ArrayList<Node>();
		path.add(cur);
		while (!path.contains(cur.successors.get(0))) {
			cur=cur.successors.get(0);
			path.add(cur);
		}
		// the cycle starts with the successor of cur
		int cycleFrom = path.indexOf(cur.successors.get(0));
		
		// now we go through that cycle again and check if any of the factors stays constant
		byte[] res = new byte[numVariables];
		res=stateList.get(path.get(cycleFrom).index).b.clone();
		for (int i=cycleFrom+1; i<path.size(); i++) {
			byte[] curS = stateList.get(path.get(i).index).b;
			for (int j=0; j<curS.length; j++) {
				if (res[j]!=curS[j])
					res[j]=-1;
			}
		}
		
		// first, we need to find the circle
		return res;
	}

	class Node {
		List<Node> successors;
		List<Node> predecessors;
		private int index;
		public Node(int index) {
			this.index=index;
			successors=new ArrayList<Node>();
			predecessors=new ArrayList<Node>();
		}
		public void addSuccessor(Node s) {
			successors.add(s);
		}
		public void addPredecessor(Node s) {
			predecessors.add(s);
		}
		@Override
		public String toString() {
			return index+"";
		}
	}


}
