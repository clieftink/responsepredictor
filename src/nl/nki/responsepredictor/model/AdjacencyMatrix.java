package nl.nki.responsepredictor.model;

import java.util.Arrays;

public class AdjacencyMatrix {
	String[] ids; //the same for columns and rows
	String[] altIds; //used to map with stored datasets
	double[][] data; //Matlab wrappers apparently don't accept int.
	
	
	
	public AdjacencyMatrix(String[] ids, String[] altIds, double[][] data) {
		super();
		this.ids = ids;
		this.altIds = altIds;
		this.data = data;
	}
	public String[] getIds() {
		return ids;
	}
	public void setIds(String[] ids) {
		this.ids = ids;
	}
	public String[] getAltIds() {
		return altIds;
	}
	public void setAltIds(String[] altIds) {
		this.altIds = altIds;
	}
	public double[][] getData() {
		return data;
	}
	public void setData(double[][] data) {
		this.data = data;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(altIds);
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + Arrays.hashCode(ids);
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
		AdjacencyMatrix other = (AdjacencyMatrix) obj;
		if (!Arrays.equals(altIds, other.altIds))
			return false;
		if (!Arrays.deepEquals(data, other.data))
			return false;
		if (!Arrays.equals(ids, other.ids))
			return false;
		return true;
	}
	
	
	

	
		
}
