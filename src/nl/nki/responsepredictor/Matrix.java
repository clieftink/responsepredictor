package nl.nki.responsepredictor;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

public class Matrix {

    String[] rowIds;
    String[] colIds;
    double[][] data;

    public String[] getRowIds() {
	return rowIds;
    }

    public void setRowIds(String[] rowIds) {
	this.rowIds = rowIds;
    }

    public String[] getColIds() {
	return colIds;
    }

    public void setColIds(String[] colIds) {
	this.colIds = colIds;
    }

    public double[][] getData() {
	return data;
    }

    public void setData(double[][] data) {
	this.data = data;
    }

    public Matrix(String[] rowIds, String[] colIds, double[][] data) {
	super();
	this.rowIds = rowIds;
	this.colIds = colIds;
	this.data = data;
    }

    public void sortColIdsAlphabet() {

	String[] colIdsSorted = colIds.clone();
	double[][] dataSorted = new double[rowIds.length][colIds.length];
	java.util.Arrays.sort(colIdsSorted, String.CASE_INSENSITIVE_ORDER);
	for (int i = 0; i < rowIds.length; i++)
	    for (int j = 0; j < colIdsSorted.length; j++) {
		String key = colIdsSorted[j];
		int k = ArrayUtils.indexOf(this.colIds, key); 
		dataSorted[i][j] = data[i][k];
	    }
	this.colIds = colIdsSorted;
	this.data = dataSorted;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(colIds);
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + Arrays.hashCode(rowIds);
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
		Matrix other = (Matrix) obj;
		if (!Arrays.equals(colIds, other.colIds))
			return false;
		if (!Arrays.deepEquals(data, other.data))
			return false;
		if (!Arrays.equals(rowIds, other.rowIds))
			return false;
		return true;
	}
	
	/**
	 * 
	 * 
	 * @return column index of the given colId
	 */
	
	public int getIdxColId(String colId) {
		int idx = -1;
		if (colIds.length > 0) {
			boolean cont = true;
			while (cont) {
				++idx;
				if (this.colIds[idx].equals(colId))
					cont = false;
				else 
					if (cont && (idx + 1 >= colIds.length)) {
						cont = false;
						idx = -1;
					}
			}
		}
		return idx;
	}
		
		
	
	/**
	 * Get value for specific colId and row 0
	 * 
	 */
	public double getValue(String colId) {
		return (data[0][getIdxColId(colId)]);
	}
    
}
