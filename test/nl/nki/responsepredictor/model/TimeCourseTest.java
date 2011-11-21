package nl.nki.responsepredictor.model;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

public class TimeCourseTest {
	@Test
	public void subDataSet() throws Exception {

		// Example data:
		// 4 proteins
		// 3 timepoints
		// 2 treats
		double[][][] data = { { { 111, 112 }, { 121, 122 }, { 131, 132 } },
				{ { 211, 212 }, { 221, 222 }, { 231, 232 } },
				{ { 311, 312 }, { 321, 222 }, { 331, 332 } },
				{ { 411, 412 }, { 421, 222 }, { 431, 432 } } };
		String[] protIds = { "AKT.pS473", "AKT.pT308", "AMPK.PT172", "cJUN.pS73" };
		String[] timeIds = { "0", "1", "2" };
		String[] treatIds = { "EGF0", "EGF1" };
		double weight = 0;

		//Original timecourse
		TimeCourse origTc = new TimeCourse(data, protIds, timeIds, treatIds);
		
		//prior modes
		RpNode[] nodes = new RpNode[2];
		HashMap<String,String> idMaps0 = new HashMap<String,String>();
		idMaps0.put("hill_2011","AKT.pT308");
		nodes[0] = new RpNode("-2", "bb", "b", 1,weight,0,"",idMaps0); 
		
		HashMap<String,String> idMaps1 = new HashMap<String,String>();
		idMaps1.put("hill_2011","AMPK.PT172");
		nodes[1] = new RpNode("-3", "cc", "c", 1,weight,0,"",idMaps1); 
		
		TimeCourse newTc = origTc.subDataset(nodes,"hill_2011");
		
		double[][][] expData = {{{ 211, 212 }, { 221, 222 }, { 231, 232 } } , { { 311, 312 }, { 321, 222 }, { 331, 332 }}};
		String[] expProtIds = {"-2","-3"}; 
		String[] expTimeIds = { "0", "1", "2" };
		String[] expTreatIds = { "EGF0", "EGF1" };
		TimeCourse expTc = new TimeCourse(expData, expProtIds, expTimeIds, expTreatIds);

		assertTrue(newTc.equals(expTc));
	}
}
