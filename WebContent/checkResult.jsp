<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="net.sf.json.JSONObject"%>
<%
    JSONObject simResult = (JSONObject) request
            .getAttribute("checkResult");
    out.print(simResult);
    out.flush();
%>