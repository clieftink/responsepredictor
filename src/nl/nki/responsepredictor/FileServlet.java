package nl.nki.responsepredictor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileServlet extends HttpServlet {   
   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
	    InputStream in = request.getInputStream();
	    BufferedReader r = new BufferedReader(new InputStreamReader(in));
	    StringBuffer buf = new StringBuffer();
	    String line;
	    while ((line = r.readLine())!=null) {
		buf.append(line);
	    }
	    String s = buf.toString();

    }
}
