package nl.nki.responsepredictor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import nl.nki.responsepredictor.bool.EquationModel;
import nl.nki.responsepredictor.bool.InvalidEquationException;
import nl.nki.responsepredictor.check.CheckResult;
import nl.nki.responsepredictor.check.ModelChecker;
import nl.nki.responsepredictor.check.Observation;
import nl.nki.responsepredictor.xgmml.XgmmlDomParser;

import org.xml.sax.SAXException;

import com.google.gson.Gson;


public class Responsepredictor {

	private Network network;
	private Observation[] observations;
	private ModelChecker checker;

	
	public Network getNetwork() {
		return network;
	}

	public void setNetwork(Network network) {
		this.network = network;
	}


	public Observation[] getObservations() {
		return observations;
	}

	public void setObservations(Observation[] observations) {
		this.observations = observations;
	}
	
	public Responsepredictor(Network network, Observation[] observations) {
		super();
		this.network = network;
		this.observations = observations;
	}
	

	public Responsepredictor(String networkFile) throws SAXException, IOException, InvalidEquationException, ParserConfigurationException  {
		super();
		
		
/*		EquationModel equationModel = EquationModelFactory.fromNetwork(networkFile);
		this.network = network;*/
	}
	
	public Responsepredictor(String networkFile, String observationsFile) throws SAXException, IOException, InvalidEquationException, ParserConfigurationException {

		//call to other constructor to handle the first parameter
		this(networkFile);
	
		Gson gson = new Gson();
		FileReader reader = new FileReader(observationsFile); 
		Observation[] obs  = gson.fromJson(reader, Observation[].class);
		this.observations = obs;
		
		ModelChecker checker = new ModelChecker();
		this.checker = checker;

	}
	
	/***
	 * 
	 * @return score of the checking the network against the observations.
	 * @throws Exception
	 */
	
	public double score() throws Exception {
		CheckResult checkResult = this.checker.runCheckBool(this.network, this.observations);
		double score = this.checker.score(checkResult);
		return score;
	}


	public String aboutRp() {
		return "Responsepredictor is a tool for finding possible improvements to a network.";
	}
	
	/**
	 * Prints modelchecking score to standard out
	 * 
	 * @param args
	 *            : [0] path for network in xgmml format [1] path for
	 * @throws Exception
	 */
/*	public static void main(String[] args) throws Exception {

		if (args.length < 2) {
			System.out
					.println("Not enough parameters. Usage: java -jar responsepredictor.jar [path xgmml network file] [path json observation file]");
		} else {
			// read network file
			XgmmlDomParser parser = new XgmmlDomParser();
			Network network = parser.network(args[0], false);

			// f.e. { "A=", "B=A", "C=!A" };
			String[] eqs = network.equations();
			EquationModel m = new EquationModel(eqs);

			String json = "[{\"id\":1,\"name\":\"ref\",\"start\":{\"A\":\"0\",\"B\":\"0\",\"C\":\"0\"},"
					+ "\"fixed\":[],\"end\":{\"A\":\"0\",\"B\":\"0\",\"C\":\"1\"}},"
					+ "{\"id\":2,\"name\":\"exp\",\"start\":{\"A\":\"1\",\"B\":\"0\",\"C\":\"0\"},"
					+ "\"fixed\":[],\"end\":{\"A\":1.0,\"B\":\"0\",\"C\":null}}]";
			
		    Scanner scanner = new Scanner(new File(args[1])).useDelimiter("\\Z");
		    String contents = scanner.next();
		    String json = contents.toString();

			Gson gson = new Gson();
			Observation[] obs = gson.fromJson(json, Observation[].class);

			// Run modelchecker
			ModelChecker modelChecker = new ModelChecker();

			CheckResult chr = modelChecker.runCheckBool(m, obs);
			Matrix res = chr.getMatrix();

			// { { 1.0, 1.0, 1.0 }, { 1.0, 0.0, -1 } };
			double[][] data = res.getData();

			// read the network file

			// read the observation file

			// run the modelchecking code
			// exp score 4/5= 0.8
			double score = modelChecker.score(data);

			System.out.println(score);
		}
	}*/
}
