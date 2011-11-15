package nl.nki.responsepredictor.bool;

/**
 * Created by Jan Krumsiek
 * Released under GPL.
 */

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

/**
 * Provides high-level analysis functions for Boolean models, beyond single
 * update steps. This includes Boolean simulations, steady state and
 * state-transition graph calculation for sync and async updating policies.
 */
public class BooleanSimulation {

	private BooleanModel model;
	private boolean[] fixedVars;

	/**
	 * Creates a new simulation object based on a given BooleanModel
	 */
	public BooleanSimulation(BooleanModel model) {
		this.model = model;
		// initialize
		fixedVars = new boolean[model.numberOfVariables()];
	}

	/**
	 * Update policy constants. Different variants of asynchronous updating are
	 * only relevant for Boolean simulations, not not steady state and STG
	 * calculations.
	 */
	public enum UpdatePolicy {
		SYNC, /** synchronous update */
		ASYNC_ORDERED, /** asynchronous update, with a given order */
		ASYNC_RANDOM
		/** asynchronous update, choose random variable in each step */
	}

	/**
	 * @param init
	 *            initial Boolean state as byte[] array
	 * @param timesteps
	 *            number of time steps to be simulated
	 * @param updatePolicy
	 *            update policy
	 * @return simulation 2D byte[][] array, first dimension = time, second =
	 *         variables
	 */
	public BooleanState[] performSimulation(BooleanState init, int timesteps,
			UpdatePolicy updatePolicy) {
		return performSimulation(init, timesteps, updatePolicy, null);
	}

	/**
	 * @param init
	 *            initial Boolean state as byte[] array
	 * @param timesteps
	 *            number of time steps to be simulated
	 * @param updatePolicy
	 *            update policy
	 * @param order
	 *            use given variable order for ASYNC_ORDERED mode
	 * @return simulation 2D byte[][] array, first dimension = time, second =
	 *         variables
	 * @throws InvalidParameterException
	 *             if order!=null and mode!=ASYNC_ORDERED
	 */
	public BooleanState[] performSimulation(BooleanState init, int timesteps,
			UpdatePolicy mode, int[] order) {

		int n = model.numberOfVariables();

		// prepare results array
		byte[][] res = new byte[timesteps][n];

		// ensure parameters
		if (mode != UpdatePolicy.ASYNC_ORDERED && order != null) {
			throw new InvalidParameterException(
					"order attribute only works in combination with mode==ASYNC_ORDERED");
		}
		// ordering stuff, only actually used if
		// mode==SimulationMode.ASYNC_ORDERED
		int curorder = -1;
		if (mode == UpdatePolicy.ASYNC_ORDERED) {
			if (order == null) {
				// generate default order
				order = new int[n];
				for (int i = 0; i < n; i++)
					order[i] = i;
			}
		}

		// iterate over timesteps
		byte[] curstate = init.b;
		res[0] = init.b.clone();
		for (int t = 1; t < timesteps; t++) {
			if (mode == UpdatePolicy.SYNC) {
				// do the synchronous update, that is use all update rules
				for (int i = 0; i < n; i++) {
					if (!fixedVars[i]) // fixed ones are not updated
						res[t][i] = model.singleUpdate(i, curstate);
					else
						res[t][i] = res[t - 1][i];
				}
			} else {
				// asynchronous update, we need to be careful with fired
				// variables here
				int next = -1;
				next = 0;
				if (mode == UpdatePolicy.ASYNC_RANDOM) {
					do {
						next = (int) (Math.random() * n);
					} while (fixedVars[next]);
				} else {
					// mode==SimulationMode.ASYNC_ORDERED
					do {
						curorder = (curorder + 1) % n;
						next = order[curorder];
					} while (fixedVars[next]);
				}

				// perform single update
				res[t] = curstate.clone();
				res[t][next] = model.singleUpdate(next, curstate);
			}
			// update state
			curstate = res[t];
		}

		return BooleanState.createFromArray(res);

	}

	/**
	 * Calculates steady states and, optionally, the state-transition graph
	 * using sync or async updating.
	 * 
	 * This function avoids the power2 internal encoding of states and should
	 * thus work for models of arbitrary size.
	 * 
	 * @param sync
	 *            true=synchronous, false=asynchronous
	 * @param init
	 *            initial state, in this version, CANNOT be null
	 * @param calcSTgraph
	 *            whether or not to actually store the state transition graph
	 * @return resulting StateSpace object
	 */
	public StateSpace calcStateSpace(boolean sync,
			BooleanState init, boolean calcSTgraph) {

		// prepare
		Queue<BooleanState> toprocess = new LinkedList<BooleanState>();
		Set<BooleanState> processed = new TreeSet<BooleanState>();
		Set<BooleanState> ss = new HashSet<BooleanState>(); // steady states
		int n = model.numberOfVariables();
		//
		ArrayList<BooleanEdge> edges = null;
		if (calcSTgraph)
			edges = new ArrayList<BooleanEdge>();
		
		if (init==null)
			throw new NullPointerException("initial value must not be null");

		// start from initial state!
		toprocess.add(init);
		processed.add(init);

		while (!toprocess.isEmpty()) {
			BooleanState o = toprocess.poll();
			// get follow-up state(s)
			if (sync) {
				// synchronous, update all
				byte[] fa = o.b.clone();
				for (int i = 0; i < n; i++) {
					if (!fixedVars[i]) // fixed ones are not updated
						fa[i] = model.singleUpdate(i, o.b);
				}
				BooleanState f = new BooleanState(fa);

				if (o.equals(f))
					// it's a steady state
					ss.add(o);
				else {
					// store edge
					if (calcSTgraph)
						edges.add(new BooleanEdge(o, f));
					;
					// check if visited already, otherwise add to queue
					// don't need to do this if we started from an initial state
					// => everything is in the queue anyway
					if (init != null && !processed.contains(f)) {
						processed.add(f);
						toprocess.add(f);
					}
				}
			} else {
				// asynchronous
		
				byte[] tfa = o.b.clone();
				for (int i = 0; i < n; i++) {
					// do not update fixed ones
					if (!fixedVars[i]) {
						byte[] fa = o.b.clone();
						fa[i] = model.singleUpdate(i, o.b);
						tfa[i] = fa[i];

						// queuing
						BooleanState f = new BooleanState(fa);
						if (!Arrays.equals(o.b, fa)) {
							// store edge
							if (calcSTgraph)
								edges.add(new BooleanEdge(o, f));
							// check if visited already, otherwise add to queue
							// don't need to do this if we started from an
							// initial state => everything is in the queue
							// anyway
							if (!processed.contains(f)) {
								processed.add(f);
								toprocess.add(f);
							}
						}
					}
				}
				// steady state?
				BooleanState tf = new BooleanState(tfa);
				if (o.equals(tf))
					ss.add(tf);

			}
		}

		return new StateSpace(ss, edges, n, sync);

	}

	/**
	 * Defines a species to be fixed, that is it will never change its current
	 * state, independent of the actual Boolean function in the model
	 */
	public void setFixedVariable(int num, boolean fixed) {
		// verify the species is valid
		if (num < 0 || num >= model.numberOfVariables())
			throw new IllegalArgumentException("invalid variable number: "
					+ num);
		fixedVars[num] = fixed;
	}

}
