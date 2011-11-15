package nl.nki.responsepredictor;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedHashMap;

import nl.nki.responsepredictor.check.Observation;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DataToObsTest {

	@Test
	public void readMidas() throws IOException {
		DataToObs dto = new DataToObs();
		
		String id = "1";
		String name ="MCF7S.S+EGF";
		LinkedHashMap<String, Double> start =new LinkedHashMap<String, Double>();
		start.put("-1", 0.6);
		start.put("-2", 0.3);
		
		//the experimental condition must have the same name as the protein
		//Input conditions for now set with the hand
		//Mentioned in the experimental conditions, means it is fixed
		//First handle the experimental conditions
		//Bij simuleren moeten de 
		
		
		//Later on, stored 

		
		String[] fixed = {"-1"};
		LinkedHashMap<String, Double> end = new LinkedHashMap<String, Double>();
		end.put("-1", null);
		end.put("-2", 0.0);
		
		Observation obs = new Observation(id,name,start,fixed,end);
		Observation[] observations = new Observation[1];
		observations[0]= obs;
		
		Observation[] res = dto.readMidas("test/data/ex_midas.csv");

/*		String resExp = "[{\"id\":\"1\",\"name\":\"obsA\",\"start\":{\"-1\":1.0,\"-2\":0.0},\"fixed\":[\"-1\"],\"end\":{\"-1\":null,\"-2\":0.0}}]";

		assertTrue(resExp.equals(res));*/
	
	}
}
