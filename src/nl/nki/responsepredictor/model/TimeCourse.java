package nl.nki.responsepredictor.model;

import java.util.Arrays;
import java.util.HashMap;

import nl.nki.responsepredictor.NotFoundException;

import org.apache.commons.lang.ArrayUtils;

/***
 * 
 * Contains the results from a timecourse experiment in a threedimensional
 * array.
 * 
 * Dimensions: 1: proteins 2: timepoints 3: treatments
 * 
 * * @author Cor Lieftink
 * 
 */

public class TimeCourse {
	double[][][] data;
	String[] protIds;
	String[] timeIds;
	String[] treatIds;

	public TimeCourse(double[][][] data, String[] protIds, String[] timeIds,
			String[] treatIds) {
		super();
		this.data = data;
		this.protIds = protIds;
		this.timeIds = timeIds;
		this.treatIds = treatIds;
	}

	public double[][][] getData() {
		return data;
	}

	public void setData(double[][][] data) {
		this.data = data;
	}

	public String[] getProtIds() {
		return protIds;
	}

	public void setProtIds(String[] protIds) {
		this.protIds = protIds;
	}

	public String[] getTimeIds() {
		return timeIds;
	}

	public void setTimeIds(String[] timeIds) {
		this.timeIds = timeIds;
	}

	public String[] getTreatIds() {
		return treatIds;
	}

	public void setTreatIds(String[] treatIds) {
		this.treatIds = treatIds;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + Arrays.hashCode(protIds);
		result = prime * result + Arrays.hashCode(timeIds);
		result = prime * result + Arrays.hashCode(treatIds);
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
		TimeCourse other = (TimeCourse) obj;
		if (!Arrays.deepEquals(data, other.data))
			return false;
		if (!Arrays.equals(protIds, other.protIds))
			return false;
		if (!Arrays.equals(timeIds, other.timeIds))
			return false;
		if (!Arrays.equals(treatIds, other.treatIds))
			return false;
		return true;
	}

	/**
	 * Goal : define a new TimeCourse dataset based on a original dataset, a
	 * subset of ids, and a HashMap mapping the .. ids in the original dataset
	 * to a desired id. The newly defined TimeCourse has the prot ids as defined
	 * in parameter.
	 * 
	 * @param ids
	 *            : ids to be used in created TimeCourse, for example
	 *            representing ids of prior model.
	 * @return
	 * @throws NotFoundException
	 */

	public TimeCourse subDataset(RpNode[] nodes, String dataset) throws NotFoundException {

		String[] newIds = new String[nodes.length];
		HashMap<String, String> newOrigMap = new HashMap<String, String>();

		//for (int i = 0; i < nodes.length; i++) {
		int newIdx=-1;
		for (RpNode node : nodes) {
			newIds[++newIdx] = node.getId();
			newOrigMap.put(node.getId(), node.getIdMaps().get(dataset));
		}
		String[] timeIds = getTimeIds();
		String[] treatIds = getTreatIds();
		double[][][] newData = new double[newIds.length][timeIds.length][treatIds.length];

		for (int i = 0; i < newIds.length; i++)
			for (int j = 0; j < timeIds.length; j++)
				for (int k = 0; k < treatIds.length; k++) {
					// get idx id of protein in original, using the newId to
					// oldId map.
					if (!newOrigMap.containsKey(newIds[i]))
						throw new NotFoundException("For protein: " + newIds[i]
								+ " there is no mapping to id in dataset!");
					else {
						String mappedId = newOrigMap.get(newIds[i]);
						int p = ArrayUtils
								.indexOf(getProtIds(), mappedId);
						if (p == -1)
							throw new NotFoundException("For mappedId : "
									+ mappedId + "no match found in dataset!");
						newData[i][j][k] = getData()[p][j][k];
					}
				}

		return (new TimeCourse(newData, newIds, timeIds, treatIds));
	}



}
