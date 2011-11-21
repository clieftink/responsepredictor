<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="net.sf.json.JSONArray"%>
<%
    JSONArray res = (JSONArray) request
            .getAttribute("namesFiles");
    out.print(res);
    out.flush();
%>