<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
<%    
    Exception e = (Exception)  request.getAttribute("error");
	if (e == null) {
	    out.print("No error attribute in the request");
	}else {
	    out.print(e.getMessage());
	}
   %>
</html>