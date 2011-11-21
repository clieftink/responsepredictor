<%@page contentType="text/html; charset=UTF-8"%>
<%
    String network = (String) request
            .getAttribute("network");
    out.print(network);
    out.flush();
%>