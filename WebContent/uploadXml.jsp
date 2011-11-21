<%@page contentType="text/xml; charset=UTF-8"%>
<%
    String result = (String) request
            .getAttribute("upload");
    out.print(result);
    out.flush();
%>