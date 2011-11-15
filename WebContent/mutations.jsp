<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="net.sf.json.JSONObject"%>
<%
    JSONObject simResult = (JSONObject) request
            .getAttribute("mutations");
    out.print(simResult);
    out.flush();
%>