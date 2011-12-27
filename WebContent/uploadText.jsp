<%@page contentType="text/html; charset=UTF-8"%>
<%
    String result = (String) request
            .getAttribute("upload");
    out.print(result);
    out.flush();
%>