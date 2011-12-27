package nl.nki.responsepredictor.action;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import nl.nki.responsepredictor.RpHelper;

import org.apache.struts2.interceptor.ServletRequestAware;

public class NamesFilesAction implements ServletRequestAware {
	private HttpServletRequest request;

	public void setServletRequest(HttpServletRequest httpServletRequest) {
		this.request = httpServletRequest;
	}

	public String execute() throws ServletException, IOException, Exception {
		String filesDir = request.getParameter("filesDir");
		
		ServletContext ctx = request.getSession().getServletContext();
		String path = ctx.getRealPath("/" +filesDir +"/");
		
		RpHelper helper = new RpHelper();
		
		//fileNamesAsJson
		JSONArray namesFiles=  helper.dirFileNamesAsJson(path);
		
		request.setAttribute("namesFiles", namesFiles);
		return "success";
	}
}
