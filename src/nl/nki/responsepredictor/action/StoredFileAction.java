package nl.nki.responsepredictor.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import nl.nki.responsepredictor.RpHelper;

import org.apache.struts2.interceptor.ServletRequestAware;

public class StoredFileAction implements ServletRequestAware {
	private HttpServletRequest request;

	public void setServletRequest(HttpServletRequest httpServletRequest) {
		this.request = httpServletRequest;
	}

	public String execute() throws IOException {
		//currently not used as there is just one stored network
		String storedFileId = request.getParameter("storedFileId");	
		String fileType = request.getParameter("fileType");	
		RpHelper helper = new RpHelper();
		
		String file ="";
		if (fileType.equals("observations"))
			file = "/observations/" + storedFileId + ".json"; //+ "timeCourse543.tsv";

		else 
			file = "/networks/" + storedFileId + ".xgmml"; //+ "timeCourse543.tsv";
		
		file = helper.storedFileToString(request, file);
		
		request.setAttribute("file", file);
        return "success"; //final SUCCESS is not seen => compiler error 
        
        
	}

}
