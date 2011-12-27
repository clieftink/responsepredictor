<%@page contentType="text/html; charset=UTF-8"%>
<%
    String file = (String) request
            .getAttribute("file");
    out.print(file);
    out.flush();
%>