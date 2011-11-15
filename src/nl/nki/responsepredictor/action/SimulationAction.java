package nl.nki.responsepredictor.action;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import nl.nki.responsepredictor.RpHelper;
import nl.nki.responsepredictor.RpSimulation;
import nl.nki.responsepredictor.Terms;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.mathworks.toolbox.javabuilder.MWException;

public class SimulationAction implements ServletRequestAware, ServletResponseAware {
    private static Logger log = Logger.getLogger(SimulationAction.class);
    HttpServletRequest request;
    HttpServletResponse response;

    @Override
    public void setServletRequest(HttpServletRequest httpServletRequest) {
	// TODO Auto-generated method stub
	this.request = httpServletRequest;
    }

    @Override
    public void setServletResponse(HttpServletResponse httpServletResponse) {
	// TODO Auto-generated method stub
	this.response = httpServletResponse;

    }



    public String execute() throws MWException, IOException {
	// Everything in try in order to make sure that error's are propagated
	// to the ajax call.

	try {
	   // @SuppressWarnings("unchecked")
	    String strStartValues = request.getParameter("startValues");
	    RpHelper rp = new RpHelper();
	    LinkedHashMap<String, Double> startValues = rp.jsonToHashmap(strStartValues);
	    String network = request.getParameter("network");
	    String fixedValues = request.getParameter("fixedValues");
	    String iterations = request.getParameter("iterations");
	    String simType = request.getParameter("simType");
	    JSONObject simResult = null;

	    // The Matlab options is just temporarily, until the "fixed" feature
	    // is ready in the bool package

		RpSimulation sim = new RpSimulation();
		if (Terms.CalcType.valueOf(simType).equals(
			Terms.CalcType.STEADYSTATE)) {
		    simResult = sim.runJSON(simType, network, startValues,
			    fixedValues, 0);
		} else if (Terms.CalcType.valueOf(simType).equals(
			Terms.CalcType.TIMECOURSE)) {
		    // TODO check if Odefy indeed sees 0 as first iteration
		    int timeTo = Integer.parseInt(iterations) + 1;
		    simResult = sim.runJSON(simType, network, startValues,
			    fixedValues, timeTo);
		}

	    request.setAttribute("simResult", simResult);
	} catch (Exception e) {
	    log.error("execute", e);
	    // request.setAttribute("error",e);

	    response.sendError(500, e.getMessage() + e.toString());
	}

	return "success";

    }
}
