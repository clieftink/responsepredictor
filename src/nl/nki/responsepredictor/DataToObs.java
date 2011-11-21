package nl.nki.responsepredictor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import nl.nki.responsepredictor.check.Observation;
import au.com.bytecode.opencsv.CSVReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/***
 * Convert data in MIDAS format into Observations in json format
 * 
 * 
 * @author cor
 *
 */


public class DataToObs {
	
	
	public Observation[] readMidas(String file) throws IOException {
		
		//de ids have to come 
		
		//optioneel maken om eerst ook nog een mapping te maken 
		//vgl. straks MIDAS file in kunnen lezen
		//evt. ook een mapping file, zonder reducties
		//wat als stra
		
		
	    CSVReader reader = new CSVReader(new FileReader(file));
	    String [] nextLine;
	    int r=0;
	    while ((nextLine = reader.readNext()) != null) {
	    	++r;
	    	if (r==1) 
	    		//get the headers
	    		
	    		
	        // nextLine[] is an array of values from the line
	        System.out.println(nextLine[0] + nextLine[1] + "etc...");

	    
	    }

		
		Observation[] res = null;
		
		return res;
		
	}
	
	
	public String obsToJson(Observation[] observations) {
		
		Gson gson = new GsonBuilder().serializeNulls().create();
		String res = gson.toJson(observations);
		
		return res;
		
		
	}

	public String midasToJson(String file) throws IOException {
		
		Observation[] obs = readMidas(file);
		String res = obsToJson(obs);
		
		return res;
		
		
	}
	
}
