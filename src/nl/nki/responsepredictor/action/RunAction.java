package nl.nki.responsepredictor.action;

import java.util.Hashtable;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

public class RunAction implements ServletRequestAware {
    private static Logger log = Logger.getLogger(RunAction.class);

    private HttpServletRequest request;

    // fileName="test/simboolnet/orton_2nodes.sif"
    // inputLevels=
    /*
     * Hashtable<String, double> inputLevels = new Hashtable<String, double>();
     * numbers.put("one", 1); numbers.put("two", 2); numbers.put("three", 3);
     */

    public Hashtable<String, Double> parseStartLevelsNodes(String inputLevels)
	    throws Exception {
	Hashtable<String, Double> startLevelsNodes = new Hashtable<String, Double>();
	Scanner scanner = new Scanner(inputLevels);

	try {
	    // first use a Scanner to get each line
	    while (scanner.hasNextLine()) {
		Scanner scanner2 = new Scanner(scanner.nextLine());
		scanner2.useDelimiter(":");
		if (scanner2.hasNext()) {
		    String name = scanner2.next();
		    String value = scanner2.next();
		    startLevelsNodes.put(name.trim(),
			    Double.valueOf(value.trim()));
		} else {
		    log.error("Empty or invalid line. Unable to process.");
		    throw new Exception(
			    "Empty or invalid line. Unable to process.");
		    // TODO present user with error message
		}
	    }
	} finally {
	    // ensure the underlying stream is always closed
	    // this only has any effect if the item passed to the Scanner
	    // constructor implements Closeable (which it does in this case).
	    scanner.close();
	}
	return (startLevelsNodes);
    }


    @Override
    public void setServletRequest(HttpServletRequest httpServletRequest) {
	// TODO Auto-generated method stub
	this.request = httpServletRequest;
    }
    
    public String execute() throws Exception {
	
	// Provide option to upload startLevelsNodes
	Hashtable<String, Double> startLevelsNodes = parseStartLevelsNodes(request
		.getParameter("inputLevels"));
	
	
	
	return "success";
    }

}
