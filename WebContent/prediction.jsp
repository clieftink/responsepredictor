<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="/struts-tags" prefix="s" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Index pagina</title>
    </head>
    <body>
        <h2>Predictive modelling for molecular biologists.</h2>
        <h3>Select models and calculate prediction.</h3>
        <img alt="example calculation input" src="calc_input.jpg"><BR><BR>
        <br>
        <s:form action="predict" method="post" enctype="multipart/form-data">
         <s:submit value="Predict" align="center" />
        </s:form>
        <a href="responsepredictor_userguide.pdf">Download responsepredictor userguide (pdf)</a>
    </body>
</html>
