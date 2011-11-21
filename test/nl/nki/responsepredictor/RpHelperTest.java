package nl.nki.responsepredictor;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import net.sf.json.JSONArray;
import nl.nki.responsepredictor.model.TimeCourse;

import org.junit.Before;
import org.junit.Test;

public class RpHelperTest {
	RpHelper helper;

	@Before
	public void init() {
		helper = new RpHelper();
	}

	@Test
	public void createSpecIdValueArray() throws Exception {

		String[] specIdStr = { "A", "B" };
		LinkedHashMap<String, Double> values = new LinkedHashMap<String, Double>();
		values.put("A", 1.0);
		values.put("D", 1.0);

		double[] res = helper.createSpecIdValueArray(specIdStr, values);

		Double[] exp = { 1.0, Double.NaN };

		for (int i = 0; i < 2; i++)
			assertTrue("i=" + i + ",exp[i]: " + exp[i] + " ,res[i]:" + res[i],
					exp[i].equals(res[i]));
	}

	@Test
	public void jsonToHashmap() {
		String jsonString = "{\"A\":\"0\",\"B\":\"1\",\"C\":\"0\"}";

		RpHelper rp = new RpHelper();
		LinkedHashMap<String, Double> result = rp.jsonToHashmap(jsonString);

		Iterator<String> iterator = result.keySet().iterator();

		String[] expKeys = { "A", "B", "C" };
		Double[] expVals = { 0.0, 1.0, 0.0 };

		int i = -1;
		while (iterator.hasNext()) {
			i++;
			String key = (String) iterator.next();
			Double val = result.get(key);
			assertTrue("key + i=" + i, key.equals(expKeys[i]));
			assertTrue("value, i=" + 1,
					expVals[i].doubleValue() == val.doubleValue());
		}
	}

	/*
	 * @Test public void parseJsonObsObject() { //\ String json=
	 * "[{\"id\":1,\"name\":\"ref\",\"start\":{\"A\":\"0\",\"B\":\"0\",\"C\":\"0\"},"
	 * +
	 * "\"end\":{\"A\":0,\"B\":\"0\",\"C\":\"1\"}},{\"id\":2,\"name\":\"exp\",\"start\":{\"A\":\"1\",\"B\":\"0\",\"C\":\"0\"},"
	 * + "\"end\":{\"A\":1.0,\"B\":\"0\",\"C\":\"0\"}}]";
	 * 
	 * 
	 * Gson gson = new Gson(); Observation[] obs = gson.fromJson(json,
	 * Observation[].class);
	 * 
	 * int i = 0;
	 * 
	 * }
	 */

	@Test
	public void jsonArrayToStringArray() {

		String jsonString = "[\"A\",\"B\"]";
		String[] res = helper.jsonArrayToStringArray(jsonString);
		String[] expRes = { "A", "B" };
		for (int i = 0; i < 2; i++)
			assertTrue("i=" + i + ",expRes[i]: " + expRes[i] + " ,res[i]:"
					+ res[i], expRes[i].equals(res[i]));

	}
	
	@Test 
	public void round() {
		double x = 0.014145397382225321;
		double y = helper.round(x,10);
		double eY  = 0.0141453974;
		assertTrue(Double.doubleToLongBits (y)== Double.doubleToLongBits (eY)); 
	}
	
	@Test 
	public void roundArray() {
		double[] x = {0.4,0.5,Double.NaN};
	
		double[] y = helper.round(x,0);
		double eY1  = 0;
		double eY2 = 1;
		
		assertTrue(Double.doubleToLongBits (y[0])== Double.doubleToLongBits (eY1)); 
		assertTrue(Double.doubleToLongBits (y[1])== Double.doubleToLongBits (eY2)); 
	}

	
	@Test
	public void inputStreamToString() throws IOException {
		File file = new File("data/timeCourse543.tsv");
		FileReader fr = new FileReader(file);
		String res = helper.fileReaderToString(fr);
		String expResult = "\"sampleId\"\t\"trEgf\"\t\"time\"\t\"AktpS\"\t\"AktpT\"\t\"AMPK\"\t\"cJUNp\"\t\"EGFRp\"\n" +
				"1\t0\t5\t-0.6247379664789564\t-2.1925720471590946\t-4.916212052960316\t-3.289501555530109\t-6.246397915861723\n" +
				"2\t0\t15\t-0.4903055861141785\t-2.057080357603576\t-5.463532555138658\t-3.425046084694389\t-5.810893966823505\n" +
				"3\t0\t30\t-0.390184292240646\t-2.0237868980839724\t-4.9180550644901295\t-3.2263920594554127\t-5.202297862615986\n" +
				"4\t0\t60\t-0.3675917411605786\t-2.1180771309048465\t-4.952429265307635\t-3.486779159198035\t-4.632180468134154\n" +
				"5\t5\t5\t0.10927748640198313\t-1.2457845790262851\t-5.029240451645601\t-3.352584547500474\t-4.798908911315786\n" +
				"6\t5\t15\t0.2424444671533241\t-1.1385776668555867\t-5.230813184832257\t-3.1084745676219474\t-5.071279753609457\n" +
				"7\t5\t30\t0.38480888549647535\t-1.1691743753723534\t-5.203448496227632\t-3.0212224861426904\t-5.062674129488343\n" +
				"8\t5\t60\t0.28158504655675315\t-1.322813016096119\t-5.479087247813367\t-2.9166654018535407\t-4.554526629301036\n" +
				"9\t10\t5\t0.29379283142530394\t-1.1222478795528095\t-4.7717233597644295\t-3.3120557427924164\t-3.919299196149976\n" +
				"10\t10\t15\t0.4650445845255362\t-1.022140536658830\t-5.266205386311658\t-3.07052987421603\t-4.283090213395247\n" +
				"11\t10\t30\t0.6466333987151461\t-1.0447429349723303\t-4.980114915482538\t-3.0064867562506015\t-4.065751643771884\n" +
				"12\t10\t60\t0.5654875642189102\t-1.1084269896594223\t-5.273011998641125\t-2.6517053597893243\t-3.7504457424925275\n";
		
		assertTrue(expResult.equals(res));
	
	}

	@Test
	public void dirFileNamesAsJson() {
		JSONArray res = helper.dirFileNamesAsJson("WebContent/networks");
		
		int  q=0;
		
	}
	
}
