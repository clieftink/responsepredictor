package nl.nki.responsepredictor.action;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import nl.nki.responsepredictor.EquationModelFactory;
import nl.nki.responsepredictor.Matrix;
import nl.nki.responsepredictor.Network;
import nl.nki.responsepredictor.RpHelper;
import nl.nki.responsepredictor.bool.EquationModel;
import nl.nki.responsepredictor.check.CheckResult;
import nl.nki.responsepredictor.check.ModelChecker;
import nl.nki.responsepredictor.check.Observation;
import nl.nki.responsepredictor.model.RpNode;
import nl.nki.responsepredictor.xgmml.XgmmlDomParser;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.google.gson.Gson;

public class CheckAction implements ServletRequestAware {
    private HttpServletRequest request;

    public void setServletRequest(HttpServletRequest httpServletRequest) {
	this.request = httpServletRequest;
    }

    public String execute() throws ServletException, IOException, Exception {
	String model = request.getParameter("network");

	// Get the observations from the input.
	String strObs = request.getParameter("obs");
	Gson gson = new Gson();
	Observation[] obs = gson.fromJson(strObs, Observation[].class);

	//TODO implement fixed values
	String fixedValues = request.getParameter("fixedValues");
	
	// Run modelchecker
	XgmmlDomParser parser = new XgmmlDomParser();
	Network network = parser.network(model, true);
	
	ModelChecker modelChecker = new ModelChecker();
	
	CheckResult chr = modelChecker.runCheckBool(network, obs);
	 Matrix res = chr.getMatrix();
	 
	 //replace ids by labels
	 
	RpHelper rp = new RpHelper();
	JSONObject json  = rp.createJSONObject("CHECK", res);
	
	json.put("score", Double.toString(chr.getScore()));
	
	request.setAttribute("checkResult", json);

	return "success";

    }
}
