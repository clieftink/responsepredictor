package nl.nki.responsepredictor;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import nl.nki.responsepredictor.check.Observation;
import nl.nki.responsepredictor.model.RpNode;
import nl.nki.responsepredictor.model.TimeCourse;

import org.apache.commons.lang.ArrayUtils;

import com.sun.org.apache.xml.internal.security.utils.JavaUtils;

import au.com.bytecode.opencsv.CSVReader;

public class RpHelper {

	public double[] initValuesNaN(int n) {
		double[] initValues = new double[n];
		for (int i = 0; i < n; i++) {
			// null values are converted to 0 somewhere in the process
			initValues[i] = Double.NaN;
		}
		return initValues;
	}

	/**
	 * 
	 * @param specIdStr
	 * @param values
	 * @return in case of species in values, but not in specIdStr, just let them
	 *         out, as in the case of checking observations against a network
	 *         missing some of the start species.
	 * @throws Exception
	 */
	public double[] createSpecIdValueArray(String[] specIdStr,
			LinkedHashMap<String, Double> values) throws Exception {

		double[] initValues = this.initValuesNaN(specIdStr.length);

		Iterator<String> iterator = values.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			int i = ArrayUtils.indexOf(specIdStr, key);
			if (i != -1) {
				Double val = values.get(key);
				if (val == null) {
					val = 0.0;
				}
				initValues[i] = val;
			}
		}
		return initValues;
	}

	public JSONObject createJSONObject(String calcType, Matrix mat) {

		String[] rowIds = mat.getRowIds();
		String[] colIds = mat.getColIds();
		double[][] data = mat.getData();

		JSONObject resultAll = new JSONObject();

		// Steady state matrix has the species in the row, where simulation has
		// it in the columns
		// Steady state: columns: steady state(s), row: species
		//
		// Bij simulation:columns:species, row: iterations
		if (Terms.CalcType.valueOf(calcType).equals(Terms.CalcType.STEADYSTATE)
				|| Terms.CalcType.valueOf(calcType).equals(
						Terms.CalcType.TIMECOURSE)) {
			// iterate over all endstates (ic.1) cq timepoints
			if (data != null)
				for (int i = 0; i < data.length; i++) {
					JSONObject resPoint = new JSONObject();

					// iterate over all species
					for (int j = 0; j < colIds.length; j++) {
						resPoint.put(colIds[j], data[i][j]);
					}
					resultAll.put(Integer.toString(i), resPoint);
				}
		} else if (Terms.CalcType.valueOf(calcType)
				.equals(Terms.CalcType.CHECK)) {

			// rows, ic. observations
			JSONArray rowIdsJson = new JSONArray();
			for (int i = 0; i < rowIds.length; i++) {
				rowIdsJson.add(rowIds[i]);
			}
			resultAll.put("rowIds", rowIdsJson);

			// columns, ic. species
			JSONArray colIdsJson = new JSONArray();
			for (int i = 0; i < colIds.length; i++) {
				colIdsJson.add(colIds[i]);
			}
			resultAll.put("colIds", colIdsJson);

			// dataMatrix
			JSONArray matrixJson = new JSONArray();

			// iterate over the rows, ic. obs.
			for (int i = 0; i < data.length; i++) {
				JSONArray row = new JSONArray();

				// iterate over the columns, ic. species
				for (int j = 0; j < data[0].length; j++) {
					row.add(data[i][j]);
				}
				matrixJson.add(row);
			}
			;
			resultAll.put("matrix", matrixJson);

		}

		return resultAll;
	}

	public LinkedHashMap<String, Double> jsonToHashmap(String jsonString) {

		JSONObject obj = JSONObject.fromObject(jsonString);
		Iterator<String> pIterator = obj.keySet().iterator();
		LinkedHashMap<String, Double> startValues = new LinkedHashMap<String, Double>();
		while (pIterator.hasNext()) {
			String key = (String) pIterator.next();
			Double value = null;
			if (!obj.get(key).equals(JSONNull.getInstance())) { // start value
				// is defined
				String strValue = (String) obj.get(key);
				if (!strValue.equals("null")) {
					value = new Double(strValue);
				}
			}
			startValues.put(key, value);
		}

		return startValues;
	}

	public Observation[] parseJsonObsObject(JSONArray obsJson) {

		Observation[] obs = new Observation[2];
		/*
		 * // start with checking of the reference condition can be reproduced
		 * 
		 * String[] species = { "A", "B", "C" }; double[][] obsStartValues = { {
		 * 0.0, 0.0, 0.0 }, { 1.0, 0.0, 0.0 } };
		 * 
		 * //TODO set the first value to null, indicating no need to check
		 * double[][] obsEndValues = { { 0.0, 0.1, 0.0 }, { 1.0, 0.0, 0.0 } };
		 * 
		 * for (int i = 0; i < 2; i++) { LinkedHashMap<String, Double>
		 * startValues = new LinkedHashMap<String, Double>();
		 * startValues.put(species[0], new Double(obsStartValues[i][0]));
		 * startValues.put(species[1], new Double(obsStartValues[i][1]));
		 * startValues.put(species[2], new Double(obsStartValues[i][2]));
		 * 
		 * LinkedHashMap<String, Double> endValues = new LinkedHashMap<String,
		 * Double>(); endValues.put(species[0], new Double(obsEndValues[i][0]));
		 * endValues.put(species[1], new Double(obsEndValues[i][1]));
		 * endValues.put(species[2], new Double(obsEndValues[i][2]));
		 * 
		 * obs[i] = new Observation(startValues, endValues); }
		 */
		// reproducing this value
		return obs;
	}

	public String[] jsonArrayToStringArray(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		String[] result = new String[jsonArray.size()];
		if (jsonArray != null)
			for (int i = 0; i < jsonArray.size(); i++)
				result[i] = jsonArray.get(i).toString();

		return result;
	}

	public double round(double d, int decimalPlace) {
		// see the Javadoc about why we use a String in the constructor
		// http://java.sun.com/j2se/1.5.0/docs/api/java/math/BigDecimal.html#BigDecimal(double)

		BigDecimal bd = new BigDecimal(Double.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
	
	public double[] round(double d[], int decimalPlace) {
		// see the Javadoc about why we use a String in the constructor
		// http://java.sun.com/j2se/1.5.0/docs/api/java/math/BigDecimal.html#BigDecimal(double)
		double[] res = new double[d.length];
		for (int i=0; i < d.length; i++) {
			if (new Double(d[i]).equals(Double.NaN))
				res[i]=Double.NaN ;
			else {
				BigDecimal bd = new BigDecimal(Double.toString(d[i]));
				bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
				res[i]= bd.doubleValue();
				
			}
		}
		return res;
	}
	

	private CSVReader newCsvReader(String file, boolean inputAsString)
			throws FileNotFoundException {
		Reader reader = null;
		if (inputAsString)
			reader = new StringReader(file);
		else
			reader = new FileReader(file);
		return (new CSVReader(reader, '\t'));
	}

	/**
	 * Parsed an simple timecourse format, with fixed columns for .. sampleId,
	 * Treatment, TimeCourse, and one or more proteins
	 * 
	 * @param file
	 * @param inputAsString
	 *            : false, than file is the name of the file. True: file
	 *            contains the content of the file
	 * @return the content of the file as a TimeCourse object
	 */

	public TimeCourse tsvToTimeCourse(String file, boolean inputAsString)
			throws IOException {

		String[] protIds = null;
		CSVReader cReader = newCsvReader(file, inputAsString);

		String[] nextLine = null;
		LinkedHashSet<String> timeSet = new LinkedHashSet<String>();
		LinkedHashSet<String> treatSet = new LinkedHashSet<String>();

		// First loop through the file to fill protIds, treatIds and timeIds
		int rowId = -1; // Start with 0 to prevent confusion with java starting
						// with 0
		while ((nextLine = cReader.readNext()) != null) {
			rowId++;
			if (rowId == 0)
				protIds = (String[]) ArrayUtils.subarray(nextLine, 3,
						nextLine.length);
			else {
				treatSet.add(nextLine[1]);
				timeSet.add(nextLine[2]);
			}
		}
		cReader.close();

		String[] timeIds = timeSet.toArray(new String[timeSet.size()]);
		String[] treatIds = treatSet.toArray(new String[treatSet.size()]);

		double[][][] data = new double[protIds.length][timeIds.length][treatIds.length];

		// Loop again through the file
		cReader = newCsvReader(file, inputAsString);

		rowId = -1; // Start with 0 to prevent confusion with java starting with
					// 0
		while ((nextLine = cReader.readNext()) != null) {
			rowId++;
			if (rowId > 0) {
				// data dim 1: protein, dim2: time, dim3: treatment
				for (int i = 3; i < nextLine.length; i++) {
					int protIdx = i - 3;
					int timeIdx = ArrayUtils.indexOf(timeIds, nextLine[2]);
					int treatIdx = ArrayUtils.indexOf(treatIds, nextLine[1]);
					data[protIdx][timeIdx][treatIdx] = Double
							.valueOf(nextLine[i]);
				}
			}
		}
		cReader.close();

		return (new TimeCourse(data, protIds, timeIds, treatIds));
	}

	// convert file content to string
	public String fileToString(String fileName) throws IOException {
		// Open the file that is the first
		// command line parameter
		FileInputStream fStream = new FileInputStream(fileName);
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fStream);
		return (inputStreamToString(in));
	}

	public String inputStreamToString(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String res = bufferedReaderToString(br);
		in.close();
		return (res);

	}

	public String fileReaderToString(FileReader fr) throws IOException {
		BufferedReader br = new BufferedReader(fr);
		String res = bufferedReaderToString(br);
		fr.close();
		return (res);
	}

	// convert inputStream to String
	public String bufferedReaderToString(BufferedReader br) throws IOException {

		StringBuffer res = new StringBuffer();

		String strLine;

		// Read File Line By Line
		while ((strLine = br.readLine()) != null) {
			res.append(strLine.trim());
			res.append("\n");
		}
		// Close the input stream

		return (res.toString());
	}

	public String storedFileToString(HttpServletRequest request, String file)
			throws IOException {
		ServletContext ctx = request.getSession().getServletContext();
		InputStream in = ctx.getResourceAsStream(file);
		if (in == null)
			throw new FileNotFoundException(file);
		String res = inputStreamToString(in);
		return (res);
	}

	/*
	 * public String convertStreamToString(InputStream is) throws IOException {
	 * 
	 * To convert the InputStream to String we use the Reader.read(char[]
	 * buffer) method. We iterate until the Reader return -1 which means there's
	 * no more data to read. We use the StringWriter class to produce the
	 * string.
	 * 
	 * if (is != null) { Writer writer = new StringWriter();
	 * 
	 * char[] buffer = new char[1024]; try { Reader reader = new BufferedReader(
	 * new InputStreamReader(is, "UTF-8")); int n; while ((n =
	 * reader.read(buffer)) != -1) { writer.write(buffer, 0, n); } } finally {
	 * is.close(); } return writer.toString(); } else { return ""; } }
	 */

	public String fileAsStringFromFullPath(String fullPath) throws IOException {
		FileReader fr = new FileReader(fullPath);
		return (fileReaderToString(fr));
	}

	public JSONArray dirFileNamesAsJson(String dir) {
		File[] files = new File(dir).listFiles();

		JSONArray namesJson = new JSONArray();
		if (files != null) {
			// create String[]
			String[] names = new String[files.length];
			for (int i = 0; i < files.length; i++)
				names[i] = files[i].getName();

			java.util.Arrays.sort(names);

			// convert to JSon array
			
			for (int i = 0; i < names.length; i++)
				namesJson.add(names[i]);

		}
		return (namesJson);
	}
}
