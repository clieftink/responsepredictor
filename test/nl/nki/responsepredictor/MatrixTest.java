package nl.nki.responsepredictor;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class MatrixTest {
	Matrix m;
	

	
	
	@Before
	public void init() {
		String[] rowIds = { "ref", "exp" };
		String[] colIds = { "B", "A" };
		double[][] data = { { 1, 2 }, { 3, 4 } };

		m = new Matrix(rowIds, colIds, data);

		m.sortColIdsAlphabet();
		
	}


	@Test
	public void sortRowIdsAlphabet() {

		String[] rowIds = { "ref", "exp" };
		String[] colIds = { "B", "A" };
		double[][] data = { { 1, 2 }, { 3, 4 } };

		Matrix m = new Matrix(rowIds, colIds, data);

		m.sortColIdsAlphabet();

		String[] expRowIds = { "ref", "exp" };
		String[] expColIds = { "A", "B" };
		double[][] expData = { { 2, 1 }, { 4, 3 } };

		for (int i = 0; i < 2; i++) {
			assertTrue("i=" + i + ",expRowIds[i]=" + expRowIds[i]
					+ ", res.getRowIds()[i]=" + m.getRowIds()[i],
					expRowIds[i].equals(m.getRowIds()[i]));

			for (int j = 0; j < 2; j++) {
				if (i == 0) {
					assertTrue("i=" + i + ",expColIds[i]=" + expColIds[i]
							+ ", res.getColIds()[i]=" + m.getColIds()[i],
							expColIds[j].equals(m.getColIds()[j]));
				}

				// Double.NaN values cannot be compared with ==
				assertTrue("i=" + i + " ,j=" + j + " ,expData[i][j]: "
						+ expData[i][j] + ", m.getData()[i][j]:"
						+ m.getData()[i][j], m.getData()[i][j] == expData[i][j]);
			}
		}
	}
	
	@Test
	public void idxColId() {
		assertTrue(m.getIdxColId("B")==1);
	}
	
	@Test 
	public void  getValue() {
		assertTrue(m.getValue("B")==1);
		
	}

}
