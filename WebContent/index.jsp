
<HTML>
<HEAD>
<title>Responsepredictor 1.2.1</title>

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



<SCRIPT LANGUAGE=javascript SRC="js/indexA.js"></SCRIPT>
</HEAD>

<body style="margin: 0; overflow: hidden"
	onload="OnLoadIndex();initRpInterface();" onresize="OnResizeIndex()"
	onmouseup="OnMouseUpBar()" onmousemove="return OnMouseMoveBar(event);">

	<div id="mainMenu">
		<ul>
			<!--  First part of the menu black to avoid overlay of submenus and input div -->
			<li><a id="mainMenuLi0A" style="width: 250px; height: 16px;"
				href="#"></a>
			</li>

<!-- 			<li onclick="showValuesObs('start');"><a href="#">show startvalues</a>
			</li> -->

			<li><a href="#">Network</a>
				<ul id="fileMenu" onclick="resetMenuAndSim();">
					<li onclick="emptyNetwork();"><a href="#">New network</a>
					</li>
					<li><a href="#">import <span id="networkImport"></span> </a>
					</li>
					<!--
		<li><a href="#">Network Export</a>
		<ul>
			-->
					<li><a href="#">export <span id="networkExportXgmml"></span>
					</a>
					</li>
					<!--
			<li><a href="#">sif <span id="networkExportSif"></span> </a></li>
		</ul>
		</li>
	-->
				</ul>
			</li>
			<li><a href="#">Node</a>
				<ul>
					<li onclick="createForm('addEditNode');"><a href="#">Add</a>
					</li>
					<li onclick="createForm('removeNode');"><a href="#">Remove</a>
					</li>
				</ul>
			</li>
			<li><a href="#">Edge</a>
				<ul>
					<li onclick="createForm('addEditEdge');"><a href="#">Add</a>
					</li>
					<li onclick="createForm('removeEdge');"><a href="#">Remove</a>
					</li>
				</ul>
			</li>
			<!--
	<li><a href="#">Improve</a>
	<ul>
		<li><a onclick="resetMenuAndSim();" href="#">Timecourse
		import <span id="dataImport"></span> </a></li>

		<li><a onclick="getCnoResult();" href="#">CNO</a></li>

		<li><a onclick="createForm('dbi');" href="#">Dynamic Bayesian
		Inference</a></li>
	</ul>
	</li>
	-->
			<li><a href="#">Simulate</a>
				<ul>
					<li onclick="simFormSetNetwork();"><a href="#">Run
							simulation</a>
					</li>
					<li onclick="setState(0);"><a href="#">Set state to 0</a>
					</li>
				</ul>
			</li>
			<li><a href="#">Observations</a>
				<ul id="observationsMenu">
					<!-- 		<li onclick="openCloseObsPane();"><a href="#">Open/close
		observation pane</a></li> -->
					<li onclick="createForm('addEditObs');"><a href="#">Add</a>
					</li>
					<li><a onclick="resetMenuAndSim();" href="#">Import <span
							id="obsImport"></span> </a>
					</li>
					<li><a onclick="resetMenuAndSim();" href="#">Export <span
							id="obsExport"></span> </a>
					</li>
					<li><a onclick="clearObs();" href="#">Remove all</a>
					</li>
				</ul>
			</li>
			<li><a href="#">Check</a>
				<ul>
					<li onclick="getCheckResult();"><a href="#">Run check</a>
					</li>
				</ul>
			</li>
		</ul>

	</div>
	<!--  end menu -->

	<div id="menuBar">
		<!-- <div id="divUploadFileName"></div>  -->
		<div id="divInputContainer" class="makeScroll">
			<div id="divInput"></div>
			<div id="divUploadTarget">
				<iframe id="upload_target" , name="upload_target" , src=""></iframe>
			</div>
		</div>
		<div id="divSimControlPanel">
			<img src="images/backward_end.png" onclick="simControl(0)" /> <img
				src="images/backward_one.png" onclick="simControl(1)" /> <img
				src="images/stop.png" onclick="simControl(2)" /> <img
				src="images/play.png" onclick="simControl(3)" /> <img
				src="images/forward_one.png" onclick="simControl(4)" /> <img
				src="images/forward_end.png" onclick="simControl(5)" />
		</div>
	</div>

	<!-- Header -->
	<div id="divHeader" style="width: 100%; height: 4%"></div>

	<!-- Vertical Bar -->
	<div id="divVertBar" onmousedown="return OnMouseDownBar(true, event);"
		style="cursor: col-resize; font-size: 3pt; position: absolute; width: 5px; background-color: black">ddd</div>

	<!--
<div id="divHorzBar" onmousedown="return OnMouseDownBar(false, event);" style="cursor:row-resize;font-size:3pt;position:absolute;width:100%;height:4px;background-color:blue"></div>
 -->


	<table cellpadding=0 cellspacing=0 style="border: 3px solid blue">
		<tr>
			<td style="border: 2px solid white"><div id="cytoscapeweb">This
					is the cytoscape panel</div>
			</td>

			<!-- divPhantomBar -->
			<div id="divPhantomBar"
				style="display: none; font-size: 3pt; position: absolute; background-color: #369"></div>

			<td style="border: 2px solid white"><div id="obs">
					Observation panel<br> <br> <br>
				</div>
			</td>
		</tr>
	</table>
</BODY>
</HTML>




