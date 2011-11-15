package nl.nki.responsepredictor.action;

import java.util.Hashtable;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

public class RunActionTest extends TestCase {
    private static Logger log = Logger.getLogger(RunActionTest.class);
    private Hashtable<String, Double> startLevelsNodes = new Hashtable<String, Double>();
    private Hashtable<String, Double> edgeWeights = new Hashtable<String, Double>();
    private int numberIterations;


    public void testParseStartLevelsNodes() throws Exception {
	
	//prepare input
	String inputLevels="egfr:0.01\nnf1:0.5";
	
	//run method
	RunAction predictionAction = new RunAction();
	Hashtable<String, Double>  startLevelsNodes= predictionAction.parseStartLevelsNodes(inputLevels);
	
	//Evaluate results
	assertTrue(startLevelsNodes.get("egfr").doubleValue()==0.01);
	assertTrue(startLevelsNodes.get("nf1").doubleValue()==0.5);
	
    }

}
