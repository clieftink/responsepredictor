package nl.nki.responsepredictor.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import nl.nki.responsepredictor.RpHelper;

import org.apache.struts2.interceptor.ServletRequestAware;

public class StoredNetworkAction implements ServletRequestAware {
	private HttpServletRequest request;

	public void setServletRequest(HttpServletRequest httpServletRequest) {
		this.request = httpServletRequest;
	}

	public String execute() throws IOException {
		//currently not used as there is just one stored network
		String storedNetworkId = request.getParameter("storedNetworkId");		
		RpHelper helper = new RpHelper();
		String file = "/networks/" + storedNetworkId + ".xgmml"; //+ "timeCourse543.tsv";
		String network = helper.storedFileToString(request, file);
		
		request.setAttribute("network", network);
        return "success"; //final SUCCESS is not seen => compiler error 
        
        
	}

}
