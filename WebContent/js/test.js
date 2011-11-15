var TEST = {
	vis : null
};

/*var loadOptions = {
		swfPath : "swf/Importer",
		flashInstallerPath : "swf/playerProductInstall",
		data : function(data) {
			var drawOptions = { // draw options apparantly contains copies, no
				// references
				network : data.string,
				edgeLabelsVisible : false,
				layout : "Preset",
				// visualStyle : RP.visualStyle
			};

			RP.vis.draw(drawOptions);
			// will done after draw is complete
			// set RP.vis.ready code
		}
	};


	var networkImport = new org.cytoscapeweb.demo.Importer("networkImport",
			loadOptions);*/
	
	
	var options = {
			swfPath : "swf/CytoscapeWeb",
			flashInstallerPath : "swf/playerProductInstall"
		};

		// populate Cytoscape Web container div
		TEST.vis = new org.cytoscapeweb.Visualization("cytoscapeweb", options);