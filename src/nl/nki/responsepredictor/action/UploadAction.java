package nl.nki.responsepredictor.action;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import nl.nki.responsepredictor.RpHelper;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;

public class UploadAction extends ActionSupport implements ServletRequestAware{
	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	//private fields has to start with the name of the input field, ic. "upload"
	private File upload = null;
    private String uploadContentType = null;
    private String uploadFileName = null;

    public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadContentType() {
		return uploadContentType;
	}

	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}

    public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public String upload() throws IOException{
		
		String fileType = request.getParameter("fileType");
		
		RpHelper helper = new RpHelper();
		String upload = helper.fileAsStringFromFullPath(this.upload.getPath());
		request.setAttribute("upload", upload);
		if (fileType.equals("xml"))
				return "successXml";
		else return "successText";
    }
	
	
	
	


}


