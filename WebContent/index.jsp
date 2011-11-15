<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>Responsepredictor 1.2.0</title>

<link rel="stylesheet" href="css/responsepredictor.css" type="text/css"
	media="screen" />
<!--  menu -->
<link rel="stylesheet" type="text/css" href="css/ddsmoothmenu.css" />
<link rel="stylesheet" type="text/css" href="css/ddsmoothmenu-v.css" />

<!-- Flash embedding utility (needed to embed Cytoscape Web) -->
<script type="text/javascript" src="js/AC_OETags.js"></script>
<script type="text/javascript" src="js/json2.js"></script>

<!--  Keep jquery before the loading the scrollbar scripts, otherwise conflict with  scrollbar scripts-->
<script type="text/javascript" src="js/jquery-1.4.4.js"></script>


<script type="text/javascript" src="js/cytoscapeweb.js"></script>
<script type="text/javascript" src="js/cytoscapeweb-file.js"></script>
<script type="text/javascript" src="js/responsepredictor.js"></script>


<!-- scrollbar  -->
<script type="text/javascript" src="js/scrollbar/prototype.js"></script>
<script type="text/javascript" src="js/scrollbar/slider.js"></script>
<script type="text/javascript" src="js/scrollbar/scroller.js"></script>
<script type="text/javascript" src="js/scrollbar/scriptaculous.js"></script>

<!--menu -->
<script type="text/javascript" src="js/ddsmoothmenu.js"></script>
<script type="text/javascript"></script>

<!--color range -->
<script type="text/javascript" src="js/jquery.colors.bundle.js"></script>



</head>

<body onload=initRpInterface();>

<div id="mainMenu">
<ul>
	<!--  First part of the menu black to avoid overlay of submenus and input div -->
	<li><a id="mainMenuLi0A" style="width: 250px; height: 16px;"
		href="#"></a></li>
	<!--
	<li onclick="getDummySimResult();"><a href="#">get dummy sim result</a></li>		
	-->
	<li><a href="#">File</a>
	<ul id="fileMenu" onclick="resetMenuAndSim();">
		<li onclick="emptyNetwork();"><a href="#">New network</a></li>
		<li><a href="#">Network import <span id="networkImport"></span>
		</a></li><!--
		<li><a href="#">Network Export</a>
		<ul>
			--><li><a href="#">Network export <span id="networkExportXgmml"></span> </a></li><!--
			<li><a href="#">sif <span id="networkExportSif"></span> </a></li>
		</ul>
		</li>
	--></ul>
	</li>
	<li><a href="#">Build</a>
	<ul>
		<li><a href="#">Node</a>
		<ul>
			<li onclick="createForm('addEditNode');"><a href="#">Add</a></li>
			<li onclick="createForm('removeNode');"><a href="#">Remove</a></li>
		</ul>
		</li>
		<li><a href="#">Edge</a>
		<ul>
			<li onclick="createForm('addEditEdge');"><a href="#">Add</a></li>
			<li onclick="createForm('removeEdge');"><a href="#">Remove</a></li>
		</ul>
		</li>
	</ul>
	</li><!--
	<li><a href="#">Improve</a>
	<ul>
		<li><a onclick="resetMenuAndSim();" href="#">Timecourse
		import <span id="dataImport"></span> </a></li>

		<li><a onclick="getCnoResult();" href="#">CNO</a></li>

		<li><a onclick="createForm('dbi');" href="#">Dynamic Bayesian
		Inference</a></li>
	</ul>
	</li>
	--><li><a href="#">Simulate</a>
	<ul>
		<li onclick="simFormSetNetwork();"><a href="#">Run
		simulation</a></li>
		<li onclick="setState(0);"><a href="#">Set state to 0</a></li>
	</ul>
	</li>
	<li><a href="#">Check</a>
	<ul>
		<li onclick="openCloseObsPane();"><a href="#">Open/close
		observation pane</a></li>
		<li><a href="#">Observation(s)</a>
		<ul>
			<li onclick="createForm('addEditObs');"><a href="#">Add one</a></li>
			<li><a onclick="resetMenuAndSim();" href="#">Import <span
				id="obsImport"></span> </a></li>
			<li><a onclick="resetMenuAndSim();" href="#">Export <span
				id="obsExport"></span> </a></li>
			<li><a onclick="clearObs();" href="#">Remove all observations</a></li>
		</ul>
		</li>
		<li onclick="getCheckResult();"><a href="#">Run check</a></li>
	</ul>
	</li>
</ul>

</div>
<!--  end menu -->

<div id="menuBar"><!-- <div id="divUploadFileName"></div>  -->
<div id="divInputContainer" class="makeScroll">
<div id="divInput"></div>
<div id="divUploadTarget"><iframe id="upload_target"
	, name="upload_target" , src=""></iframe></div>
</div>
<div id="divSimControlPanel"><img src="images/backward_end.png"
	onclick="simControl(0)" /> <img src="images/backward_one.png"
	onclick="simControl(1)" /> <img src="images/stop.png"
	onclick="simControl(2)" /> <img src="images/play.png"
	onclick="simControl(3)" /> <img src="images/forward_one.png"
	onclick="simControl(4)" /> <img src="images/forward_end.png"
	onclick="simControl(5)" /></div>
</div>
<!-- end menubar -->
<div id="cytoscapeweb"></div>
<div id="obs">Observations</div>
<div id="wait"><img src="images/please_wait.gif"></img></div>
</body>

</html>
