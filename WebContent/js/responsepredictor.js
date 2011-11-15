// Global variables in order to be able to run simulation
// All globals in one object to prevent interference of global variables in other scripts.
var RP = {
	prior : null,
	obs : new Array(),
	timeCourseData : null,
/*	nodeIdsHill : [ '', 'AKT.pS473', 'AKT.pT308', 'AMPK.PT172', 'cJUN.pS73',
			'EGFR.PY1173', 'GSK3.pS21', 'JNK.PT183', 'LKB1.pS428',
			'MAPK.pT202', 'MEK1.2.PS217', 'mTOR.pS2448', 'p38.PT180',
			'p70S6K.pT389', 'p90RSK.pT359', 'PDK1.pS241', 'PIfn3Kp110',
			'STAT3.pT727', 'STAT3.pY705', 'STAT5.pY964', 'TSC2.PT1462' ],*/
	timeOutHandler : null,
	simIt : -1,
	simPlayIsOn : 0,
	maxSimItNr : 0,
	states : null,
	vis : null,
	swfu : null,
	controlEnabled : false,
	networkChanged : false,
	obsChanged : false,
	tabLinks : new Array(),
	contentDivs : new Array(),
/*	nodeIdGroup : "hill_2011",*/
	nodeType : {
		CUE : 0,
		SIGNAL : 1,
		ANDGATE : -2,
		RESPONSE : 2
	},
	mutState : {
		UNCHANGED : 0,
		ADDED : 1,
		UPDATED : 2,
		DELETED : 3
	},
	implRevMut : {
		IMPL : 1,
		REV : 2
	},
	colorScheme : {
		INITIAL : 0,
		SIMSTATE : 1,
		MUTSTATE : 2
	},
	baseColor : '#3EA99F',
	datasets : null, // contains the names of the datasets. Works as cache.
	divModelCheckVisible : false
};

// extension

RP.visualStyle = {
	nodes : {
		shape : {
			discreteMapper : {
				attrName : "Type",
				entries : [ {
					attrValue : -2,
					value : "CIRCLE"
				}, {
					attrValue : 0,
					value : "DIAMOND"
				}, {
					attrValue : 1,
					value : "CIRCLE"
				}, {
					attrValue : 2,
					value : "ROUNDRECT"
				} ]
			}
		},
		borderWidth : 3,
		borderColor : "#ffffff",
		size : {
			discreteMapper : {
				attrName : "Type",
				entries : [ {
					attrValue : -2,
					value : 20
				}, {
					attrValue : 0,
					value : 40
				}, {
					attrValue : 1,
					value : 40
				}, {
					attrValue : 2,
					value : 40
				} ]
			}
		},
		color : RP.baseColor,
		labelFontSize : 15,
		labelFontWeight : "bold",
		labelHorizontalAnchor : "center"
	},
	edges : {
		width : 5/*
					 * { defaultValue : 5, continuousMapper : { attrName :
					 * "weight", minValue : 1, maxValue : 10, minAttrValue : 0,
					 * maxAttrValue : 1 } }
					 */,
		color : RP.baseColor,
		label : "",
		targetArrowShape : {
			discreteMapper : {
				attrName : "interaction",
				entries : [ {
					attrValue : -1,
					value : "T"
				}, {
					attrValue : 0,
					value : "NONE"
				}, {
					attrValue : 1,
					value : "ARROW"
				} ]
			}
		}
	}
};

function setBaseColor(hexCode) {
	RP.baseColor = hexCode;
	var visualStyle = RP.vis.visualStyle();
	visualStyle.nodes.color = RP.baseColor;
	visualStyle.edges.color = RP.baseColor;
	RP.vis.visualStyle(visualStyle);
}

function setColorScheme(colorScheme) {
	var visualStyle = RP.vis.visualStyle();

	if (colorScheme == RP.colorScheme.INITIAL) {
		visualStyle.nodes.color = RP.baseColor;
		visualStyle.edges.color = RP.baseColor;
	} else if (colorScheme == RP.colorScheme.SIMSTATE) {
		visualStyle.nodes.color = {
			continuousMapper : {
				attrName : "state",
				minValue : "#F85B5B", // red
				maxValue : "#00FF00", // green
				minAttrValue : 0,
				maxAttrValue : 1.0
			}
		};

	} else if (colorScheme == RP.colorScheme.MUTSTATE) {
		var color = {
			discreteMapper : {
				attrName : "mutState",
				entries : [ {
					attrValue : undefined,
					value : RP.baseColor
				}, {
					attrValue : null,
					value : RP.baseColor
				}, {
					attrValue : 0,
					value : RP.baseColor
				}, {
					attrValue : 1, // added
					value : "#990099"
				}, {
					attrValue : 2, // updated
					value : "FF6633"
				}, {
					attrValue : 3, // deleted
					value : "FFFF00"
				} ]
			}
		};
		visualStyle.nodes.color = color;
		visualStyle.edges.color = color;
	}
	RP.vis.visualStyle(visualStyle);
}

function setWait(x) {
	var div = document.getElementById("wait");
	div.style.visibility = x;
}

// remove multiple, leading or trailing spaces
function trim(s) {
	s = s.replace(/(^\s*)|(\s*$)/gi, "");
	s = s.replace(/[ ]{2,}/gi, " ");
	s = s.replace(/\n /, "\n");
	return s;
}

/**
 * Check if the uploaded network is complete. If the nodes does not have a state
 * field, add this field. This will only be the case if there is none node with
 * a state field. If at least one node has a state field, the import routine
 * apparently will give all other nodes also a state field
 */
function completeNetwork() {
	var nodes = RP.vis.nodes();
	var edges = RP.vis.edges();
	var notesField = {
		name : "notes",
		type : "string",
		defValue : ""
	};

	var refsField = {
		name : "refs",
		type : "list"
	};

	// check if the first node has a state variable
	if (nodes.length > 0) {
		var typeField = {
			name : "Type",
			type : "int",
			defValue : 1
		};

		var stateField = {
			name : "state",
			type : "number", // with defValue does not seem to work
			defValue : null
		};

		var idMapsField = {
			name : "idMaps",
			type : "list"
		};
		if (!("Type" in nodes[0].data))
			RP.vis.addDataField("Type", typeField);

		if (!("state" in nodes[0].data)) {
			RP.vis.addDataField("nodes", stateField);
			setState(null);
		}

		if (!("notes" in nodes[0].data))
			RP.vis.addDataField("nodes", notesField);

		if (!("idMaps" in nodes[0].data))
			RP.vis.addDataField("nodes", idMapsField);
	}

	var mutField = {
		name : "mutState",
		type : "int"
	};

	var interactionField = {
		name : "interaction",
		type : "string",
		defValue : "1"
	};

	if (edges.length > 0) {
		if (!("interaction" in edges[0].data))
			RP.vis.addDataField("edges", interactionField);

		if (!("mutState" in edges[0].data))
			RP.vis.addDataField("edges", mutField);

		if (!("notes" in edges[0].data))
			RP.vis.addDataField("edges", notesField);

		// Apparently a list field is not exported in case there are no entries
		if (!("refs" in edges[0].data))
			RP.vis = RP.vis.addDataField("edges", refsField);

	} else {
		// in case network imported without any edges, Cytoscape web will
		// create later the fields, however probably without the correct field
		// type
		RP.vis = RP.vis.addDataField("edges", interactionField);
		RP.vis = RP.vis.addDataField("edges", refsField);
		RP.vis = RP.vis.addDataField("edges", notesField);
		RP.vis.addDataField("edges", mutField);
	}
}

/**
 * Get an Array of Indices of the node given a specific field as input
 * 
 * @param field
 * @returns {Array}
 */

function getNodeIdArray(nodes, field) {
	// create index for node id or canonicalName
	var nodeIdArray = new Array(nodes.length);
	for (i = 0; i < nodes.length; i++) {
		if (field == "id")
			nodeIdArray[i] = nodes[i].data.id;
		else if (field == "label")
			nodeIdArray[i] = nodes[i].data.label;
		else if (field == "canonicalName")
			nodeIdArray[i] = nodes[i].data.canonicalName;
		else {
			alert('field is not appropriate to create index!');
		}
		;
	}
	return nodeIdArray;
}

/**
 * Set all the state values to null
 * 
 */

function setState(state) {
	var state = {
		state : state
	};
	var ids = Array();
	for ( var i = 0, len = RP.vis.nodes().length; i < len; i++)
		ids.push(RP.vis.nodes()[i].data.id);
	RP.vis.updateData("nodes", ids, state);
}

/**
 * Update the state of one or more nodes. NaN values are filtered out, as
 * Cytoscape Web produces error when trying to update for NaN. Initial loading
 * from xml however allows for NaN values.
 * 
 * @param nodes :
 *            collection of nodes in the network
 * @param states:
 *            key-value pairs for the nodes to be updated. The key contains the
 *            node id.
 * @returns : an array with changed nodes
 */
function updateState(states, field) {
	var nodes = RP.vis.nodes();
	nodeIdArray = getNodeIdArray(nodes, "id");

	updates = new Array();
	var i = 0;
	jQuery.each(states, function(key, value) {
		i++;
		var node_k = nodes[nodeIdArray.indexOf(key)];
		if (typeof (node_k) == 'undefined') {
			// TODO in stead of alert, to some error log
			alert('for key =' + key + ' no node found');
		} else {
			// null parsed by parseFloat to NaN
			// NaN not accepted as value by Cytoscape web
			if (value == null || isNaN(value)) {
				node_k.data.state = null;
			} else {
				node_k.data.state = parseFloat(value);
			}
			updates[i] = node_k;
		}
	});

	return updates;

}

function removeHTMLTags() {
	if (document.getElementById && document.getElementById("input-code")) {
		var strInputCode = document.getElementById("input-code").innerHTML;
		/*
		 * This line is optional, it replaces escaped brackets with real ones,
		 * i.e. < is replaced with < and > is replaced with >
		 */
		strInputCode = strInputCode.replace(/&(lt|gt);/g,
				function(strMatch, p1) {
					return (p1 == "lt") ? "<" : ">";
				});
		var strTagStrippedText = strInputCode.replace(/<\/?[^>]+(>|$)/g, "");
		alert("Output text:\n" + strTagStrippedText);
		// Use the alert below if you want to show the input and the output text
		// alert("Input code:\n" + strInputCode + "\n\nOutput text:\n" +
		// strTagStrippedText);
	}
}

var t;
var timer_is_on = 0;

function qShift() {
	q.shift()();
}

function timedCount() {
	t = setTimeout("qShift", 1000);
}

function doTimer(q) {

	if (!timer_is_on) {
		timer_is_on = 1;
		timedCount();
	}
}

function stopCount() {
	clearTimeout(t);
	timer_is_on = 0;
}

function setOpacity(obj, opacity) {
	opacity = (opacity == 100) ? 99.999 : opacity;

	// IE/Win
	obj.style.filter = "alpha(opacity:" + opacity + ")";

	// Safari<1.2, Konqueror
	obj.style.KHTMLOpacity = opacity / 100;

	// Older Mozilla and Firefox
	obj.style.MozOpacity = opacity / 100;

	// Safari 1.2, newer Firefox and Mozilla, CSS3
	obj.style.opacity = opacity / 100;
}

function emptyElement(el) {
	// remove existing el nodes, start with the last one as
	// nodes are instantly removed

	var length = el.children.length;
	for ( var i = length - 1; i >= 0; i--) {
		var child = el.children[i];
		// TODO check if the following if statement can be removed
		if (child.id != "upload_target")
			el.removeChild(el.children[i]);
	}

	// f.e. textNode
	var length = el.childNodes.length;
	for ( var i = length - 1; i >= 0; i--) {
		var node = el.childNodes[i];
		el.removeChild(el.childNodes[i]);
	}
}

function resetSim() {
	RP.timeOutHandler = null;
	// iteration index starting with 0
	RP.simIt = -1;
	RP.simPlayIsOn = 0;
	RP.maxSimItNr = 0;
	RP.controlEnabled = false;
	setOpacity(document.getElementById("divSimControlPanel"), 50);

}

function emptyDivInput() {
	emptyElement(document.getElementById("divInput"));
	Scroller.resetSlider("divInputContainer");
}

/**
 * Clean in case of switching forms.
 * 
 */

function resetMenuAndSim() {
	emptyDivInput();
	resetSim();
}

// Update one iteration
function simUpdateOne() {
	statesIt = RP.states[RP.simIt];
	var updates = updateState(statesIt, "canonicalName");
	RP.vis.updateData(updates);
}

function stop() {
	clearTimeout(RP.timeOutHandler);
	playisOn = 0;
}

function simPlayOn() {
	if (RP.simIt + 1 < RP.maxSimItNr) {
		RP.simIt = RP.simIt + 1;
		simUpdateOne();
		RP.timeOutHandler = setTimeout("simPlayOn()", 1000);
	} else {
		stop();
	}
}

function simControl(key) {
	if (RP.controlEnabled) {
		if (key == 3) { // play
			if (RP.simIt + 1 < RP.maxSimItNr) {
				/* if (!RP.simPlayIsOn) { */
				/**
				 * @param tag
				 * @param isSingle
				 * @param arrays
				 * @param field
				 * @returns {___anonymous9692_9693}
				 */
				RP.simPlayIsOn = 1;
				simPlayOn();
				/* } */
			} else {
				alert('Already at end of simulation!');
			}
		} else {
			stop();
			if (key != 2) {
				switch (key) {
				case 0:// backward end
					RP.simIt = 0;
					break;
				case 1: // backwardOne
					if (RP.simIt > 0) {
						RP.simIt = RP.simIt - 1;
					}
					;
					break;
				case 4: // forwardOne
					if (RP.simIt < RP.maxSimItNr) {
						RP.simIt = RP.simIt + 1;
					}
					;
					break;
				case 5: // forwardEnd
					RP.simIt = RP.maxSimItNr - 1;
				}
				simUpdateOne();
			}
		}
	}
}

/**
 * Returns the value of an item in een form, ic. frmInput Parameters tag: f.e.
 * 'input[type=radio]' or 'input[type=text]' isSingle : a single value vs an
 * array of values.
 * 
 */

function getValues(tag, isSingle, arrays, field, removePrefix) {
	if (isSingle) {
		var values;
	} else {
		var values = {};
	}

	var inputs = jQuery(tag);
	jQuery.each(inputs, function(i, item) {
		var value;
		
		if (typeof item['value'] == "undefined") 
			value = null;
		else {
			if (trim(item['value']) == "")
				value = null;
			else 
				value = item['value'];
		};
		
		if (typeof isSingle != "undefined" && isSingle) {
			values = value;
		} else {
			if (tag == 'input[type=radio]') {
				if (item['checked']) {
					values = value;
				}
			} else {
				if (typeof (arrays) != 'undefined' && arrays != null
						&& item[field] in arrays) {
					if (typeof (values[item[field]]) == 'undefined')
						values[item[field]] = new Array();
					values[item[field]].push(value);
				} else {
					var itemName = item[field];
					if (removePrefix)
						itemName = itemName.substring(1);
					values[itemName] = value;
				}
			}
		}
	});
	return values;

}

function copySortOnNames(nodes) {
	function compareNames(a, b) {
		var nameA = a.data.canonicalName.toLowerCase();
		var nameB = b.data.canonicalName.toLowerCase();
		if (nameA < nameB) {
			return -1;
		}
		;
		if (nameA > nameB) {
			return 1;
		}
		;
		return 0;
	}

	var nodesCopy = nodes.slice(0);

	nodesCopy.sort(compareNames);

	return nodesCopy;

}

function createTdSelect(prefix, id, name, defValue) {
	var td1 = document.createElement("td");
	var select = document.createElement("select");
	select.id = prefix + id;
	select.name = name;
	select.size = 1;
	for ( var i = -1; i < 2; i++) {
		var option = document.createElement("option");
		var value;
		if (i == -1) {
			value = null;
			valueText = "";
		} else {
			value = i;
			valueText = i + ""; // convert to string
			if (typeof (defValue) == 'undefined') {
				if (i == 0)
					option.selected = true;
			} else if (i == defValue)
				option.selected = true;
		}
		option.value = value;
		text = document.createTextNode(valueText);
		option.appendChild(text);
		select.appendChild(option);
	}

	td1.appendChild(select);
	return td1;
}

/**
 * *
 * 
 * @param id
 * @param name
 * @param inclEnd
 * @param obsRec :
 *            the values for start,fix and values for a protein within an
 *            observation
 * 
 * @param inputtext:
 *            true, than input as text field, otherwise select field
 * 
 * @returns
 */

function addRow(id, name, inclEnd, obsRec,inputText) {
	if (!document.getElementById)
		return; // Prevent older browsers from getting any further.

	if (document.createElement) { // W3C Dom method.
		var row = document.createElement("tr");
		var td1 = document.createElement("td");
		var text = document.createTextNode(name);
		td1.appendChild(text);
		row.appendChild(td1);

		
		if (typeof (obsRec) != 'undefined') {
			var obsRecStart = obsRec.start;
			var obsRecFixed = obsRec.fixed;
			var obsRecEnd = obsRec.end;
		}
		
		var td2;
		if (inputText) { 
			if (typeof (obsRecStart) == 'undefined')
				obsRecStart="";
			td2 = createInput("s"+id, name, 4, 10, "text", obsRecStart);
		}else 
			td2 = createTdSelect("s", id, name, obsRecStart);
		
		row.appendChild(td2);

		var td3 = document.createElement("td");
		var input = document.createElement("input");
		input.id = "f" + id;
		input.name = "fixed";
		// input.size = 1;
		input.type = "checkbox"; // Type of field - can be any valid input
		// type
		input.value = id;
		if (obsRecFixed)
			input.checked = true;
		td3.appendChild(input);
		row.appendChild(td3);

		if (inclEnd) {
			var td4;
			if (inputText) { 
				if (typeof (obsRecEnd) == 'undefined')
					obsRecEnd="";
				td4 = createInput("e"+id, name, 4, 10, "text", obsRecEnd);
			} else 					
				td4 = createTdSelect("e", id, name, obsRecEnd);
			row.appendChild(td4);
		}
		return (row);
	}
}

function getNetworkXgmml() {
	var x = RP.vis.xgmml();
	var q = 0;

}

function getFixedValues() {
	var fixed = [];
	jQuery('input[type=checkbox]:checked').each(function(index) {
		fixed.push(jQuery(this).val());
	});
	return fixed;
}

function updateNodesWithStartValues() {
	// update nodes with start values: states[0]
	RP.simIt = 0;
	RP.simPlayIsOn = 0;
	var statesLength = 0;
	for ( var props in RP.states) {
		statesLength++;
	}
	RP.maxSimItNr = statesLength;
	setColorScheme(RP.colorScheme.SIMSTATE);
	if (RP.maxSimItNr == 0) {
		alert("No states found!");
	} else {
		simUpdateOne();

		if (RP.maxSimItNr > 1) {
			RP.controlEnabled = true;
			setOpacity(document.getElementById("divSimControlPanel"), 100);
		}

	}
}

function getSimResult() {
	resetSim();

	var tmpStartValues = getValues('select[id^="s"]', false, null, 'id');
	// the ids are prefixed with , so remove the s
	var startValues = {};
	jQuery.each(tmpStartValues, function(key, value) {
		startValues[key.substring(1)] = value;
	});

	var fixedValues = getFixedValues();
	var iterations = getValues('input[type=text]', true, null, 'name');
	var simType = getValues('input[type=radio]', false, null, 'name');

	// check at least one value not null
	jQuery.ajax({
		beforeSend : function() {
			jQuery('body').css('cursor', 'wait');
		},
		complete : function() {
			jQuery('body').css('cursor', 'default');
		},

		type : 'post',
		url : "updates",
		dataType : "json", // , makes it of type object in stead of String
		data : {
			network : RP.vis.xgmml(),
			startValues : JSON.stringify(startValues),
			fixedValues : JSON.stringify(fixedValues),
			iterations : iterations,
			simType : simType
		},
		success : function(msg) {
			RP.states = msg;
			updateNodesWithStartValues();
		},
		error : function(xhr, ajaxOptions, thrownError) {
			xhrStatusText = xhr.statusText;
			xhrResponseText = xhr.responseText;
			window.open("error.html");
			// alert(xhr.statusText);
		}
	});

};

var Base64 = {

	// private property
	_keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

	// public method for encoding
	encode : function(input) {
		var output = "";
		var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
		var i = 0;

		input = Base64._utf8_encode(input);

		while (i < input.length) {

			chr1 = input.charCodeAt(i++);
			chr2 = input.charCodeAt(i++);
			chr3 = input.charCodeAt(i++);

			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;

			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}

			output = output + this._keyStr.charAt(enc1)
					+ this._keyStr.charAt(enc2) + this._keyStr.charAt(enc3)
					+ this._keyStr.charAt(enc4);

		}

		return output;
	},

	// public method for decoding
	decode : function(input) {
		var output = "";
		var chr1, chr2, chr3;
		var enc1, enc2, enc3, enc4;
		var i = 0;

		input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

		while (i < input.length) {

			enc1 = this._keyStr.indexOf(input.charAt(i++));
			enc2 = this._keyStr.indexOf(input.charAt(i++));
			enc3 = this._keyStr.indexOf(input.charAt(i++));
			enc4 = this._keyStr.indexOf(input.charAt(i++));

			chr1 = (enc1 << 2) | (enc2 >> 4);
			chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
			chr3 = ((enc3 & 3) << 6) | enc4;

			output = output + String.fromCharCode(chr1);

			if (enc3 != 64) {
				output = output + String.fromCharCode(chr2);
			}
			if (enc4 != 64) {
				output = output + String.fromCharCode(chr3);
			}

		}

		output = Base64._utf8_decode(output);

		return output;

	},

	// private method for UTF-8 encoding
	_utf8_encode : function(string) {
		string = string.replace(/\r\n/g, "\n");
		var utftext = "";

		for ( var n = 0; n < string.length; n++) {

			var c = string.charCodeAt(n);

			if (c < 128) {
				utftext += String.fromCharCode(c);
			} else if ((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			} else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}

		}

		return utftext;
	},

	// private method for UTF-8 decoding
	_utf8_decode : function(utftext) {
		var string = "";
		var i = 0;
		var c = c1 = c2 = 0;

		while (i < utftext.length) {

			c = utftext.charCodeAt(i);

			if (c < 128) {
				string += String.fromCharCode(c);
				i++;
			} else if ((c > 191) && (c < 224)) {
				c2 = utftext.charCodeAt(i + 1);
				string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
				i += 2;
			} else {
				c2 = utftext.charCodeAt(i + 1);
				c3 = utftext.charCodeAt(i + 2);
				string += String.fromCharCode(((c & 15) << 12)
						| ((c2 & 63) << 6) | (c3 & 63));
				i += 3;
			}

		}

		return string;
	}

};

function rowSelected(tab) {
	var cont = true;
	var sel = 1;

	// skip first row (is header) with id =0
	var i = 0;
	while (i < tab.childNodes.length && cont) {
		i++;
		if (tab.childNodes[i].className == "rowSelected") {
			sel = tab.childNodes[i];
			cont = false;
		}
	}
	return sel;
}

/**
 * @param trId :
 *            id of the row of the newly selected rows
 */

function markSelectRowDrawDetails(trIdNewSel) {
	// test if the observation on the row is not just deleted
	// the row function is also triggered after the delete
	// obsIndex = rowIndex -1
	if (trIdNewSel - 1 <= RP.obs.length - 1) {
		// demark current selected row
		var tab = document.getElementById("tabObsTable");
		var trOld = rowSelected(tab);
		trOld.className = null;
		// sets also automatically the background color right

		// mark new selected row
		if (trIdNewSel > 0) {
			var trNew = document.getElementById(trIdNewSel);
			trNew.className = "rowSelected";

			// draw details. The observation index has an -1 value compare to
			// the
			// rowid
			drawDetails(tab, trIdNewSel - 1);
		}
	}
}

/**
 * 
 * @param obsIndex
 * @returns
 */

function deleteObs(obsIndex) {
	if (confirm("Do you want to delete observation: " + RP.obs[obsIndex].name
			+ "?")) {

		RP.obs.splice(obsIndex, 1);

		// keep marked line on same position
		if (obsIndex >= RP.obs.length) {
			obsIndex = RP.obs.length - 1;
		}
		crObsView(obsIndex);
	}
	;
}

function createObsTable(obsIndex, obsNames, nodesNames, matrix, score) {
	if (typeof (obsIndex) == 'undefined')
		trId = 0;
	var tab = document.createElement("table");
	tab.id = "tabObsTable";

	// Create header for nodesNames
	var tr = document.createElement("tr");
	tr.id = 0;
	tr.className = "rowNotSelected";
	// start with empty fields above simulate, edit and trash and a field with
	// score above obs name
	for ( var i = 0; i < 4; i++) {
		var td = document.createElement("td");
		td.bgcolor = '#FFFFFF';
		td.style.height = '25px';
		var text = "";
		if (i < 3)
			text = document.createTextNode("");
		else
			text = document.createTextNode(score + "");
		td.appendChild(text);
		tr.appendChild(td);
	}

	if (typeof (nodesNames) != 'undefined') {
		for ( var i = 0; i < nodesNames.length; i++) {
			var th = document.createElement("th");
			th.bgcolor = '#FFFFFF';
			var text = document.createTextNode(nodesNames[i]);
			th.appendChild(text);
			tr.appendChild(th);
		}
	}
	tab.appendChild(tr);

	// iterate over observations
	for ( var i = 0; i < obsNames.length; i++) {
		// Start with row id
		var tr = document.createElement("tr");
		tr.id = i + 1; // 0 is header
		if (i == obsIndex) {
			tr.className = "rowSelected";
		}

		// Add clickable images for simulation, edit and delete
		for ( var j = 0; j < 3; j++) {
			var td0 = document.createElement("td");
			var img = document.createElement("img");

			var funct;
			// get the id of the row by going to parent td, and then to tr
			
			if (j == 0) {
				img.src = "images/simulation.png";
				img.onclick = function() {
					markSelectRowDrawDetails(this.parentNode.parentNode.id);
					simFormSetNetwork(RP.obs[this.parentNode.parentNode.id - 1]);
				};
			}
				
			if (j==1) {
				img.src = "images/edit.jpg"	;
					img.onclick = function() {
					markSelectRowDrawDetails(this.parentNode.parentNode.id);
					createForm('addEditObs', RP.obs[this.parentNode.parentNode.id - 1]);
				};
			}
				

			if (j==2) {
				img.src = "images/trash.png";
				img.onclick = function() {
					markSelectRowDrawDetails(this.parentNode.parentNode.id);
					deleteObs(this.parentNode.parentNode.id - 1);
				};
			};

			td0.appendChild(img);
			tr.appendChild(td0);
		}

		var th = document.createElement("th");
		var value = obsNames[i];
		var text = document.createTextNode(value);
		th.appendChild(text);
		th.style.width = "240px";
		th.style.textAlign = "left";
		tr.appendChild(th);

		tr.onclick = function() {
			markSelectRowDrawDetails(this.id);
		};
		if (typeof (matrix) != 'undefined') {
			// iterate over species
			// matrix contains model - data
			var matrix0 = matrix[0];
			for ( var j = 0; j < matrix[0].length; j++) {
				var td = document.createElement("td");
				var value = matrix[i][j];
				// TODO replace -1.0 by NA /null if not checked
				// in stead of -1
				// in case of value -1 (meaning not to checked,
				// no other background is set
				if (value == -1.0) {
					td.bgColor = '#ff99ff';
				} else if (value == 0.0) {
					td.bgColor = '#FF6666';
				} else if (value == 1.0) {
					td.bgColor = '#00FF00';
				} else if (value == 9.0) {
					td.bgColor = '#808080';
				} else if (isNaN(value)) {
					td.bgColor = '#ff99ff';
				}
				tr.appendChild(td);
			}
			;
		}
		tab.appendChild(tr);
	}
	;

	// add 3 rows for the details of the selected row
	var rowName = [ "start", "fix", "end" ];
	for ( var i = 0; i < 3; i++) {

		var tr = document.createElement("tr");
		tr.id = obsNames.length + i + i;
		tr.className = "rowSelected";

		// First fields where in the Oveview the simulation, edit and trash
		// images are
		for (var j=0 ; j < 3 ; j++){
			var td = document.createElement("td");
			tr.appendChild(td);
		}

		// Second field contains the name of the field
		var th = document.createElement("th");
		var textNode1 = document.createTextNode(rowName[i]);
		th.appendChild(textNode1);
		th.style.textAlign = "right";
		tr.appendChild(th);

		for ( var j = 0; j < nodesNames.length; j++) {
			var td = document.createElement("td");
			tr.appendChild(td);
		}

		tab.appendChild(tr);
	}

	// in case of deleted row on the obsIndex
	if (obsIndex > RP.obs.length)
		obsIndex = 0
		
	drawDetails(tab, obsIndex);

	return tab;

}

function tableResult(data) {
	if (typeof (data["colIds"]) == 'undefined'
			|| typeof (data["matrix"]) == 'undefined'
			|| data["rowIds"].length != data["matrix"].length
			|| data["colIds"].length != data["matrix"][0].length) {
		var msg = "";
		if (typeof (data["rowIds"]) == 'undefined') {
			msg += "No row ids. ";
		}
		;
		if (typeof (data["colIds"]) == 'undefined') {
			msg += "No column ids. ";
		}
		;
		if (typeof (data["matrix"]) == 'undefined') {
			msg += "No data. ";
		}
		;
		if (typeof (data["score"]) == 'undefined') {
			msg += "No score. ";
		}
		;

		if (data["rowIds"].length != data["matrix"].length) {
			msg += "Number of data['rowIds'] is not equal to number of rows in data['matrix']. ";
		}
		;
		if (data["colIds"].length != data["matrix"][0].length) {
			msg += "Number of data['colIds'] is not equal to number of cols in data['matrix']. ";
		}
		;
		alert(msg);
	} else {
		var table = createObsTable(0, data["rowIds"], data["colIds"],
				data["matrix"], data["score"]);
		return table;
	}

}

var checkResultWindow;

// param 1 : experimental data

function getResult(data, url, dataType, successFunction) {

	resetSim();

	jQuery.ajax({
		beforeSend : function() {
			jQuery('body').css('cursor', 'wait');
		},
		complete : function() {
			jQuery('body').css('cursor', 'default');
		},
		type : 'post',
		url : url,
		dataType : dataType,
		data : data,
		success : successFunction,
		error : function(xhr, ajaxOptions, thrownError) {
			xhrStatusText = xhr.statusText;
			xhrResponseText = xhr.responseText;
			window.open("error.html");
			// alert(xhr.statusText);
		}
	});
}

function getCheckResult() {

	// TODO if no observations defined, give alert
	if (RP.obs.length == 0)
		alert("No observations defined.")
	else {
		successFunction = function(data) {

			var table = tableResult(data);
			var divCheck = document.getElementById('obs');
			emptyElement(divCheck);
			divCheck.appendChild(table);
		};
		var data = {
			network : RP.vis.xgmml(),
			obs : JSON.stringify(RP.obs)
		};

		getResult(data, 'check', "json", successFunction);
	}
	;
};

function testMutations() {

	var data = {
		"removeEdges" : [ "2" ],
		// "removeNodes" : [ -1, -2 ],
		// "addNodes" : [ {
		// "id" : -4,
		// "label" : "D",
		// "Type" : 1,
		// "canonicalName" : "D",
		// "state" : NaN,
		// "notes" : ''
		// } ],
		"addEdges" : [ {
			"id" : "3",
			"label" : "CD",
			"directed" : true,
			"source" : "-2",
			"target" : "-3",
			"interaction" : "1",
			"refs" : '',
			"notes" : ''
		} ],
		"updateEdges" : [ {
			"id" : "1",
			"interaction" : "-1"
		} ]
	};

	processMutations(data, true);
}

/**
 * 
 * 
 * @param data.
 *            Contains for the updates only the id and the fields that need to
 *            be .. updated, for now only
 */

function processMutations(data) {

	// // Add nodes.
	// if (typeof data.addNodes != 'undefined')
	// for ( var i = 0; i < data.addNodes.length; i++) {
	// if (i < 100)
	// y = 20 + 5 * i;
	// else
	// // limit to prevent nodes placed outside the visible interface
	// y = 20 + 500;
	// RP.vis.addNode(20, y, data.addNodes[i], true);
	// }
	//
	// // Update nodes.
	// if (typeof data.updateNodes != 'undefined')
	// for ( var i = 0; i < data.updateNodes.length; i++) {
	// var nodeData = data.updateNodes[i];
	// var node = RP.vis.nodes()[getNodeIndex("id", nodeData.id)];
	// node.data = nodeData;
	// RP.vis.updateData([ node ]);
	// }

	var indexMap = allEdgesIndex();

	// Add edges.
	if (typeof data.addEdges != 'undefined')
		for ( var i = 0, len = data.addEdges.length; i < len; i++) {
			var edgeData = data.addEdges[i];
			edgeData.mutState = RP.mutState.ADDED;
			RP.vis.addEdge(edgeData, true);
		}

	// Update edges
	if (typeof data.updateEdges != 'undefined')
		// with caching len the loop is 2 times faster
		for ( var i = 0, len = data.updateEdges.length; i < len; i++) {
			var edgeData = data.updateEdges[i];
			var edge = RP.vis.edges()[indexMap[edgeData.id]];
			if (typeof (edge.data.interactionOld) == 'undefined') {
				var field = {
					name : "interactionOld",
					type : "string"
				};
				RP.vis.addDataField("edges", field);
			}

			// TODO replace with update call with ids
			// it then just updates the given fields
			// see f.e. addEditEdge
			edge.data.interactionOld = edge.data.interaction;
			edge.data.interaction = edgeData.interaction;
			edge.data.mutState = RP.mutState.UPDATED;
			RP.vis.updateData([ edge ]);
		}

	// Remove edges. Remove before nodes, as nodes will also remove edges and
	// then later try to remove these edges generates error
	// removeEdges contains solely an array of ids
	if (typeof data.removeEdges != 'undefined')
		for ( var i = 0, len = data.removeEdges.length; i < len; i++) {
			// removeEdge('id', data.removeEdges[i], null, null);
			var j = indexMap[data.removeEdges[i]];
			if (j == -1) {
				// TODO look up for the label of the node
				alert("Edge with id " + data.removeEdges[i] + 'not found!');
			} else {
				var edge = RP.vis.edges()[j];
				edge.data.mutState = RP.mutState.DELETED;
				// TODO replace with update call with ids
				// it then just updates the given fields
				// see f.e. addEditEdge
				RP.vis.updateData([ edge ]);
			}
		}

	// // Remove nodes.
	// if (typeof data.removeNodes != 'undefined')
	// for ( var i = 0; i < data.removeNodes.length; i++)
	// removeNode(data.removeNodes[i]);
	setColorScheme(RP.colorScheme.MUTSTATE);

}

function getCnoResult() {
	var successFunction = function(data) {
		processMutations(data);
	};
	var data = {
		network : RP.vis.xgmml()
	};
	getResult(data, 'cno', "json", successFunction);
}

/**
 * returns a list of node ids which don't have an identifier for the specific
 * dataset
 */
function nodesWithoutDatasetIds(dataset) {
	var nodesWithout = new Array();
	for ( var i = 0; i < RP.vis.nodes().length; i++) {
		var node = RP.vis.nodes()[i];
		if (getIdMap(node.data, dataset) == null) {
			nodesWithout.push(node.data.canonicalName);
		}
	}
	return nodesWithout;
}

function implRevMutations() {

	var selectValues = getValues('select', false, null, 'name');

	// Work backwards as the nodes are move forward in the array soon as
	// a node before is deleted.
	for ( var len = RP.vis.edges().length, i = len - 1; i >= 0; i--) {
		var edge = RP.vis.edges()[i];
		if (edge.data.mutState != 'undefined')
			if (selectValues.implRevMut == RP.implRevMut.IMPL) {
				if (edge.data.mutState == RP.mutState.ADDED
						|| edge.data.mutState == RP.mutState.UPDATED) {
					edge.data.mutState = RP.mutState.UNCHANGED;
					// TODO replace with update call with ids
					// it then just updates the given fields
					// see f.e. addEditEdge
					RP.vis.updateData([ edge ]);
				} else if (edge.data.mutState == RP.mutState.DELETED)
					RP.vis.removeEdge(edge);
			} else { // Revert
				if (edge.data.mutState == RP.mutState.ADDED)
					RP.vis.removeEdge(edge);
				else if (edge.data.mutState == RP.mutState.UPDATED) {
					edge.data.interaction = edge.data.interactionOld;
					edge.data.mutState = RP.mutState.UNCHANGED;
					// TODO replace with update call with ids
					// it then just updates the given fields
					// see f.e. addEditEdge
					RP.vis.updateData([ edge ]);
				} else if (edge.data.mutState == RP.mutState.DELETED) {
					edge.data.mutState = RP.mutState.UNCHANGED;
					// TODO replace with update call with ids
					// it then just updates the given fields
					// see f.e. addEditEdge
					RP.vis.updateData([ edge ]);
				}
			}
	}
}

function getDbiResult() {
	var successFunction = function(data) {
		// setWait('visible') in index.jsp
		processMutations(data);
		createForm('implRevMutations');
		setWait('hidden');
	};
	var textValues = getValues('input:text', false, null, 'name');

	var selectValues = getValues('select', false, null, 'name');

	var timeCourseData = null;
	if (selectValues.dataset == 'upload' && RP.timeCourseData == null) {
		setWait('hidden');
		alert('There is no uploaded dataset! Upload or use existing dataset.');
	} else {
		var cont = true;
		if (selectValues.dataset == 'upload')
			timeCourseData = RP.timeCourseData;

		if (selectValues.dataset == 'hill_2011') {
			var nodesWithout = nodesWithoutDatasetIds();
			if (nodesWithout.length > 0) {
				var text = "For dataset '";
				text += selectValues.dataset;
				text += "' , there are nodes without mapped ids: ";
				for ( var i = 0; i < nodesWithout.length; i++) {
					text += nodesWithout[i];
					if (i < nodesWithout.length - 1)
						text += ",";
				}
				;
				text += ". Map id via node edit or remove node from network.";
				alert(text);
				cont = false;
			}
		}

		if (cont) {
			var data = {
				network : RP.vis.xgmml(),
				dataset : selectValues.dataset,
				timeCourseData : timeCourseData,
				maxInDegree : textValues.maxInDegree,
				lambdas : textValues.lambdas,
				threshold : textValues.threshold
			};
			setWait('visible');
			getResult(data, 'dbi', "json", successFunction);
		}
	}
}

// function importNetwork() {
// var successFunction = function(data) {
//
// var drawOptions = { // draw options apparantly contains copies,
// // no references
// network : data,
// edgeLabelsVisible : false,
// layout : "Preset",
// visualStyle : RP.visualStyle
// };
// RP.vis.ready(function() {
// completeNetwork();
// RP.vis.visualStyle(RP.visualStyle);
// });
// RP.vis.draw(drawOptions);
// };
// var data = {};
// getResult(data, 'importNetwork', "text", successFunction);
// }

function drawNetwork(data) {
	var drawOptions = { // draw options apparantly contains copies,
		// no references
		network : data,
		edgeLabelsVisible : false,
		layout : "Preset",
		visualStyle : RP.visualStyle
	};
	RP.vis.ready(function() {
		completeNetwork();
		RP.vis.visualStyle(RP.visualStyle);
	});
	RP.vis.draw(drawOptions);

}

/**
 * Load stored Network
 */
function storedNetwork(storedNetworkId) {
	var successFunction = function(data) {
		drawNetwork(data);
	};
	var data = {
		storedNetworkId : storedNetworkId
	};
	getResult(data, 'storedNetwork', "text", successFunction);
}

function createOption(value, text, sel) {
	var option = document.createElement("option");
	option.value = value;
	var optionText = document.createTextNode(text);
	option.selected = sel;
	option.appendChild(optionText);
	return option;
}

function selected(optValue, defValue, altSelValue) {
	var sel;
	if (defValue == null) {
		if (optValue == altSelValue)
			sel = true;
		else
			sel = false;
	} else {
		if (optValue == defValue)
			sel = true;
		else
			sel = false;
	}
	return sel;
}

function appendTextNode(frm, text) {
	var textNode = document.createTextNode(text);
	frm.appendChild(textNode);
}

/**
 * *
 * 
 * @param nodes
 * @param name
 * @param el(ement),
 *            can be a node or edge.
 * @returns {___select15}
 */

function appendSelect(frm, name, defValue) {
	var select = document.createElement("select");
	select.name = name;
	select.id = name;
	var options = new Array();
	if (name == "interaction" || name == 'type') {
		if (name == "interaction") {
			options[0] = {
				value : "1",
				text : 'Activation'
			};
			options[1] = {
				value : "-1",
				text : 'Inhibition'
			};

		} else if (name == 'type') {
			options.push({
				value : "-2",
				text : 'and-gate'
			});
			/*
			 * options.push( { value : "0", text : 'cue' });
			 */options.push({
				value : "1",
				text : 'signal'
			});
/*
 * options.push({ value : "2", text : 'response' });
 */
		}

		for ( var i = 0; i < options.length; i++) {
			var sel;
			if (name == "type" && typeof (element) != 'undefined')
				defValue = element.data.Type;
			sel = selected(options[i].value, defValue, 1);
			var option = createOption(options[i].value, options[i].text, sel);
			select.appendChild(option);
		}
	} else if (name == "source" || name == "target" || name == 'node') {
		var nodes = copySortOnNames(RP.vis.nodes());
		for ( var i = 0; i < nodes.length; i++) {
			var node = nodes[i];
			sel = selected(node.data.id, defValue, nodes[0].data.id);
			var option = createOption(node.data.id, node.data.label, sel);
			select.appendChild(option);
		}
	} else if (name == 'idMaps') {
		for ( var i = 0; i < RP.nodeIdsHill.length; i++) {
			var idH = RP.nodeIdsHill[i];
			sel = selected(idH, defValue, null);
			var option = createOption(idH, idH, sel);
			select.appendChild(option);
		}

	} else if (name == 'dataset') {
		option = {
			value : 'upload',
			text : 'Upload'
		};
		var sel;
		sel = selected(option.value, 'upload', 'upload');
		var option = createOption(option.value, option.text, sel);
		select.appendChild(option);
	} else if (name == 'implRevMut') {
		options[0] = {
			value : RP.implRevMut.IMPL,
			text : 'Implement'
		};
		options[1] = {
			value : RP.implRevMut.REV,
			text : 'Revert'
		};
		for ( var i = 0; i < options.length; i++) {
			var sel = null; // then first option is selected
			var option = createOption(options[i].value, options[i].text, sel);
			select.appendChild(option);
		}
	}

	frm.appendChild(select);
}

/**
 * 
 * 
 * @param element:
 *            edge, currently
 */

function addEditEdge(currentEdge) {
	var values = getValues('select', false, null, 'name');
	// the :0 are just to be able to use the in operator in getValues
	var inputText = getValues('input[type=text]', false, {
		"refKey" : 0,
		"refUrl" : 0
	}, 'name');

	var notes = jQuery("textarea#notes").val();

	// replace the \n for \\n
	notes = notes.replace(/\n/g, "\\n");

	// refs is a array of key (=label, fe. mirzoeva_2009, value (=url) pairs
	// TODO test if array is complete in case of empty value
	var refs = [];
	for ( var i = 0; i < inputText.refKey.length; i++) {
		var key = inputText.refKey[i];
		if (key == null && inputText.refUrl[i] != null) {
			var number = i + 1;
			alert('For ref ' + number + " there is an url, but not a name!");
		} else if (key != null) {
			var obj = {};
			obj[key] = inputText.refUrl[i];
			refs.push(obj);
		}
	}

// var weight = inputText.weight;

	var label = values.source + "_" + values.target;
	var data = {
		label : label,
		directed : true,
		source : values.source,
		target : values.target,
		interaction : values.interaction,
// weight : parseFloat(weight),
		refs : refs,
		notes : notes
	};

	var addEdge = false;
	// possible remove edge first in case of update source and/or target
	var remEdge = false;

	// in case of edit
	if (typeof (currentEdge) != 'undefined')
		// Cytoscape Web wil not update the target and source field
		// In that case remove the existing edge and create a new one
		if (currentEdge.data.source != data.source
				|| currentEdge.data.target != data.target) {
			// remove existing node
			remEdge = true;
			addEdge = true;
		} else {
			var ids = Array();
			ids.push(currentEdge.data.id);
			RP.vis.updateData("edges", ids, data);
			RP.networkChanged = true;
		}
	else
		// ic. add edge
		addEdge = true;

	if (addEdge) {
		var i = getEdgeIndex('srcTarget', null, values.source, values.target);
		if (i != -1)
			alert('Edge for source: ' + values.source + 'and target: '
					+ values.target + ' already exists!');
		else {
			if (remEdge)
				RP.vis.removeEdge(currentEdge);
			var edge = RP.vis.addEdge(data, true);
			RP.networkChanged = true;
		}

	}
	// close form
	// emptyElement(document.getElementById("divInput"));
}


function getNodeIdLabelPlayers() {
	
	var map = {};
	var players= getPlayers();
	for ( var i = 0, len =players.length; i < len; i++) 
		map[players[i].data.id] = players[i].data.label;
	
	return map;
	
	
}

/*
 * Returns 0 based index in RP.vis.nodes. If not found, -1 is returned. the
 * order of the nodes is not fixed field: can be "id" or "label"
 * 
 */
function getNodeIndex(field, value) {
	var nodes = RP.vis.nodes();
	var l = nodes.length;
	var i = -1;
	var cont = true;
	while (cont) {
		i++;
		if (i > l - 1) {
			cont = false;
			i = -1;
		} else {
			var node = nodes[i];
			if (field == "id" && node.data.id == value) {
				cont = false;
			} else if (field == "label"
					&& node.data.label.toLowerCase() == value.toLowerCase()) {
				cont = false;
			}
		}
	}
	return (i);
}
/**
 * produce an array of values for objects given the field
 * 
 * @param objects
 * @param field
 */

function getArray(objects, field, toLowerCase) {
	var arr = jQuery(objects).map(function() {
		var fieldArray = field.split(".");
		var f = this;
		for ( var i = 0; i < fieldArray.length; i++) {
			var f = f[fieldArray[i]];
		}
		if (toLowerCase)
			return f.toLowerCase();
		else
			return f;
	}).get();
	return arr;

}

function calcMinMaxValue(objects, field, min) {
	var arr = getArray(objects, field, false);
	var value;
	if (min) {
		value = Math.min.apply(Math, arr);
	} else {
		value = Math.max.apply(Math, arr);
	}
	if (!isFinite(value)) {
		value = 0;
	}
	return value;
}

function getSelectDataset(dataset) {
	var select = document.createElement("select");

	// for (int i=0; i < RP.nodeIdsHill.length; i++) {
	//		
	//		
	// }
	// // for now hill_2011
	// var options = new Array();
	// options[0] = {
	// value : '',
	// text : ''
	// };
	// options[1] = {
	// value : "BHqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq",
	// text : 'BHrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr'
	// };

	for ( var i = 0; i < options.length; i++) {
		var sel;
		var defValue = "BH";
		// if (name == "interaction" && typeof (element) != 'undefined')
		// defValue = element.data.interaction;
		sel = selected(options[i].value, defValue, 1);
		var option = createOption(options[i].value, options[i].text, sel);
		select.appendChild(option);
	}

	return select;
}

function addEditNode(currentNode) {
	var textValues = getValues('input:text', false, null, 'name');
	var selectValues = getValues('select', false, null, 'name');
	var notes = jQuery("textarea#notes").val();
	// replace the \n for \\n
	notes = notes.replace(/\n/g, "\\n");

	idMaps = new Array();
	var obj = {};
	obj['hill_2011'] = selectValues.idMaps;
	idMaps.push(obj);

	var nodeName = "";
	if (textValues.nodeName != null)
		nodeName = trim(textValues.nodeName);
	if (nodeName == "")
		alert("Give node a name.");
	else if (typeof (currentNode) != 'undefined') {
		// check if in case name is changed, no other node has already that name
		var idx = getNodeIndex("label", textValues.nodeName);
		if (idx != -1 && RP.vis.nodes()[idx].data.id != currentNode.data.id)
			alert('Node with this label already exists!');
		else {
			var data = {
				canonicalName : textValues.nodeName,
				label : textValues.nodeName,
				Type : parseInt(selectValues.type),
				// weight : parseFloat(textValues.weight),
				notes : notes,
				idMaps : idMaps
			};
			var ids = Array();
			ids.push(currentNode.data.id);
			RP.vis.updateData("nodes", ids, data);

		}
	} else {
		var i = getNodeIndex("label", textValues.nodeName);
		if (i != -1) {
			alert('Node with this label already exists!');
		} else {
			var nodes = RP.vis.nodes();
			var minValue = calcMinMaxValue(nodes, 'data.id', true);
			var data = {
				id : (minValue - 1).toString(),
				label : textValues.nodeName,
				Type : parseInt(selectValues.type),
				canonicalName : textValues.nodeName,
				state : 0,
				// weight : parseFloat(textValues.weight),
				notes : notes,
				idMaps : idMaps
			};

			var node = RP.vis.addNode(200, 200, data, true);
			// when adding the node, the null value for state is overridden by 0
			// TODO update it to null again, once find a way to have a default
			// value
			// for a continous mapper ic for color in case of simulation
			var state = {
				state : 0
			};
			RP.vis.updateData("nodes", node.id, state);

			RP.networkChanged = true;
			// close form
			// emptyElement(document.getElementById("divInput"));
		}
	}
}

function allEdgesIndex() {
	var all = new Array();
	for ( var i = 0, len = RP.vis.edges().length; i < len; i++)
		all[RP.vis.edges()[i].data.id] = i;

	return all;
}

/*
 * give the index position of the given edge in the RP edges array. returns 0
 * based position index in RP.vis.edges array. Returns -1 if not found.
 */
function getEdgeIndex(field, id, source, target) {
	var l = RP.vis.edges().length;
	var i = -1;
	var cont = true;
	while (cont) {
		i++;
		if (i > l - 1) {
			cont = false;
			i = -1;
		} else {
			var edge = RP.vis.edges()[i];
			if (field == 'srcTarget') {
				if (edge.data.source == source && edge.data.target == target)
					cont = false;
			} else { // ==id
				if (edge.data.id == id)
					cont = false;
			}
		}
	}
	return (i);
}

function selectRemoveEdge() {
	var values = getValues('select', false, null, 'name');
	removeEdge('srcTarget', null, values.source, values.target);
}

/*
 * Parameters: field : 'id' (number) vs 'srcTarget' to identify the edge
 */

function removeEdge(field, id, source, target) {
	var i = getEdgeIndex(field, id, source, target);
	if (i == -1) {
		// TODO look up for the label of the node
		alert('Edge for source: ' + source + ', target: ' + target
				+ 'not found!');
	} else {
		var edge = RP.vis.edges()[i];
		RP.vis.removeEdge(edge);
	}
}

/*  */

function selectRemoveNode() {
	var values = getValues('select', false, null, 'name');
	removeNode(values.node);
}

function removeNode(nodeId) {
	// Edges are removed by Cytoscape Web component

	var i = getNodeIndex("id", nodeId);
	if (i == -1) {
		alert('Node not found');
	} else {
		var node = RP.vis.nodes()[i];
		RP.vis.removeNode(node);
		
		// In the obsview only the nodes in the network are shown
		if (RP.divModelCheckVisible)
			crObsView(0);
			
	}
}

function changeDefaultValue() {
	var inputs = jQuery('select');
	jQuery.each(inputs, function(i, item) {
		if (item['value'] == 0) {
			item['value'] = 1;
		} else {
			item['value'] = 0;
		}
		;
	});

}

function trim(str) {
	return str.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
}

function getIndexObsName(name) {
	var arr = getArray(RP.obs, 'name', true);
	var index = arr.indexOf(name.toLowerCase());
	
}

/**
 * * 
 * @param name
 * @param values : set as properties / (simulating hashmap)
 * @returns {Boolean}
 */

function valuesBetween0and1(name,values) {

	text="";
	var map = getNodeIdLabelPlayers();
	jQuery.each(values, function(key, value) {
		if (values[key] < 0 || values[key] > 1) {
			if (text !="")
				text += ",";
			text += map[key];		
		}
	});
	if (text !="") {
		alert("Values for '" + name +" 'are not between 0 and 1 for nodes " + text);
		return false;
	}else return true;
	
}


/**
 * 
 * @param element
 * @param text :
 *            true : textfield, false: select field
 */

function addEditObs(element,text) {
	var name = getValues('input[id="name"]', true, null, 'name');
	if (name == null) {
		alert('Name is empty!');
	} else {
		name = trim(name);
		// null and empty is converted in Javascript to false
		if (!name) {
			alert('Name is empty!');
		} else {
			// TODO check if name already exists (case insensitive)
			var checkNameExists=false;
			if (typeof (element) != 'undefined' && name.toLowerCase()!=element.name.toLowerCase())
				checkNameExists=true;
			else 
				checkNameExists=true;
			
			var nameExistsWarning=false;
			if (checkNameExists)
				var index = getIndexObsName(name);
				if (index > -1)
					nameExistsWarning=true;
			
			if (nameExistsWarning)
				alert("Name '" + name + "' already exists!");
			else {
				if (typeof (element) != 'undefined')
					id= element.id;
				else id = calcMinMaxValue(RP.obs, 'id', false) + 1;
				var startTag;
				if (text) {
					startTag='input[id^="s"]';
					endTag='input[id^="e"]';
				} else {
					startTag='select[id^="s"]';
					endTag='input[id^="e"]';
				}
				
				var start = getValues(startTag, false, null, 'id',
						true);
				
				var end = getValues(endTag, false, null, 'id',
						true);

				if(valuesBetween0and1("start",start) &&  valuesBetween0and1("end",end)) {
					var fixed = getFixedValues();

					var obs = {
						id : id,
						name : name,
						start : start,
						fixed : fixed,
						end : end
					};

					var idx = 0;
					if (typeof (element) != 'undefined') {
						var ids = getArray(RP.obs,"id");
						idx = ids.indexOf(element.id);
						RP.obs[idx] = obs;
					} else
						RP.obs.push(obs);
					crObsView(idx);
				}
			}
		}
	}
};


function getPlayers() {
	return(getNodesByType([ 0, 1 ]));
} 


/**
 * Get nodes based on their type.
 * 
 * input is an array of types to be included
 */
function getNodesByType(inclTypes) {
	var result = new Array();
	for ( var i = 0; i < RP.vis.nodes().length; i++) {
		var node = RP.vis.nodes()[i];
		if (inclTypes.indexOf(node.data.Type) != -1)
			result.push(node);
	}

	result = copySortOnNames(result);
	return result;
}

/**
 * 
 * 
 * @param inclEnd
 * @param obs
 * @param text :
 *            if true , than input = textfield, other wise select field
 * @returns
 */

function createTableNodeValues(inclEnd, obs, inputText) {

	var table = document.createElement("table");
	var row = document.createElement("tr");
	var names = [ "", "Start", "Fix" ];
	if (inclEnd) {
		names.push("End");
	}
	for ( var i = 0; i < names.length; i++) {
		var td = document.createElement("td");
		var text = document.createTextNode(names[i]);
		td.appendChild(text);
		row.appendChild(td);
	}
	table.appendChild(row);
	var players = getPlayers();

	for ( var i = 0; i < players.length; i++) {
		var node = players[i];

		// TODO get the value for the players id in the obs
		if (typeof (obs) != 'undefined') {
			var start = obs.start[node.data.id];
			var end = obs.end[node.data.id];
			if (!inputText) {
				start=Math.round(start);
				end=Math.round(end);
			}
			
			var fixed = false;
			if (obs.fixed.indexOf(node.data.id) > -1)
				fixed = true;
			
			var obsRec = {
				start : start,
				fixed : fixed,
				end : end
			};

		}

		table.appendChild(addRow(node.data.id, node.data.label, inclEnd,
			obsRec,inputText));
	}
	return table;
}

function currentStateAsInput() {
	var inputs = jQuery('select[id^="s"]');
	var players = getPlayers();
	var playerIdArray = getNodeIdArray(players, 'canonicalName');
	jQuery.each(inputs, function(i, item) {
		var player = players[playerIdArray.indexOf(item.name)];
		item['value'] = player.data.state;
	});
}

/**
 * 
 * @param idx
 * @param oldRefKey
 * @param oldRefUrl
 * @returns references are returned under the same name as a list
 */

function crRefEntry(idx, oldRefKey, oldRefUrl) {

	entry = document.createElement("span");

	// 0th child. input for a new reference
	var refKey = document.createElement("input");
	refKey.id = "refKey" + idx;
	refKey.name = "refKey";
	refKey.size = 16;
	refKey.maxLength = 15;

	if (typeof (oldRefKey) != 'undefined')
		refKey.value = oldRefKey;
	refKey.type = "text";
	entry.appendChild(refKey);

	// 1th child
	var label = document.createTextNode("Url");
	entry.appendChild(label);

	// 2th child
	var refUrl = document.createElement("input");
	refUrl.id = "refUrl" + idx;
	refUrl.name = "refUrl";
	refUrl.size = 1;
	// refUrl.maxLength=250;
	refUrl.type = "text";

	if (typeof (oldRefUrl) != 'undefined')
		refUrl.value = oldRefUrl;

	refUrl.onclick = function() {
		var oldValue = this.value;
		var newValue = prompt("Url:", this.value);
		// igv cancel
		if (newValue == null)
			this.value = oldValue;
		else
			this.value = newValue;
	};
	entry.appendChild(refUrl);

	// 3th child. ref open url icon
	var linkIcon = document.createElement("img");
	linkIcon.src = "images/link.png";
	linkIcon.style.height = "25px";
	linkIcon.onclick = function() {
		var url = this.parentNode.childNodes[2].value;
		if (url == 'undefined' || url == "")
			alert('Specify url please.')
		else
			window.open(url, "refWindow");
	};
	entry.appendChild(linkIcon);

	return entry;

}

function appendNoteField(frm, value) {
	var label = document.createTextNode("Notes");
	frm.appendChild(label);
	appendBr(frm, 1);
	var notes = document.createElement("textArea");
	notes.id = "notes";
	notes.rows = 13;
	notes.cols = 22;
	notes.value = value;
	frm.appendChild(notes);
}

function createInput(id, name, size, maxLength, type, value) {
	var input = document.createElement("input");
	input.id = id;
	input.name = name;
	input.size = size;
	input.maxLength = maxLength;
	input.type = type;
	input.value = value;
	return input;
}

function addTd(tr, tdChildNode) {
	var td = document.createElement("td");
	td.appendChild(tdChildNode);
	tr.appendChild(td);

}

function appendHeader(frm, header) {
	var headerText = document.createTextNode(header);
	frm.appendChild(headerText);
	var br = document.createElement("BR");
	frm.appendChild(br);
}

function addParams(frm, params) {
	var tab = document.createElement("table");
	tab.id = "paramsTable";

	for ( var i = 0; i < params.length; i++) {
		var tr = document.createElement("tr");
		addTd(tr, document.createTextNode(params[i].label));
		var td = document.createElement("td");
		var input = createInput(params[i].id, params[i].name, params[i].size,
				params[i].maxLength, params[i].type, params[i].value);
		addTd(tr, input);
		frm.appendChild(tr);
	}
}

/**
 * 
 * @param formType
 * @param divInput
 * @param element :
 *            in case of edit the node or edge
 */

function appendBr(frm, times) {
	for ( var i = 0; i < times; i++) {
		var br = document.createElement("BR");
		frm.appendChild(br);
	}
}

function appendCaptionRow(table, color, descr) {
	var tr = document.createElement("tr");
	var td = document.createElement("td");
	var img = document.createElement("img");
	img.src = "images/caption/" + color + '.jpg';
	// img.width ="50";
	td.appendChild(img);
	tr.appendChild(td);
	var td = document.createElement("td");
	var text = document.createTextNode(descr);
	td.appendChild(text);
	tr.appendChild(td);
	table.appendChild(tr);
}

// function appendWeight(frm, title, defValue) {
// appendTextNode(frm, title + ":");
// var value = defValue;
// // TODO rename "nodeName" to label
// var input = createInput("weight", "weight", 3, 15, 'text', defValue);
// frm.appendChild(input);
// appendBr(frm, 1);
// }


function simFormSetNetwork(element) {

	createForm('simulation',element);
	
	var states = new Array();
	if (typeof (element) == "undefined") {
		var players = getPlayers();
		var startState = {};
		for ( var i = 0, len = players.length; i < len; i++) 
			startState[players[i].data.id] = 0;
		states[0]=startState;
		
	}else {
		// create state for all the nodes which are in the network
		var nodeIds = getArray(RP.vis.nodes(),'data.id');
		
		var state = {};
		// TODO set values to a null value and simulate with it
		for ( var i = 0, len = nodeIds.length; i < len; i++) 
			state[nodeIds[i]] = 0;
		
		jQuery.each(element.start, function(key, value) {
			var idx = nodeIds.indexOf(key);
			if (idx > -1) 
				state[key] =Math.round(value);
		});
		states[0]= state;
	}
		
	// execute query
	getSimResult();
/*
 * RP.states = states; updateNodesWithStartValues();
 */
}

function createForm(formType, element) {

	var div = document.getElementById('divInput');
	var frm = document.createElement("form");
	frm.id = "frmInput";
	resetMenuAndSim();

	var buttonText;
	var clickFunction;

	if (formType == "addEditNode") {
		var headerText = "NEW";
		if (typeof (element) != 'undefined') {
			headerText = "EDIT";
			appendHeader(frm, "Id: " + element.data.id);
		}

		appendHeader(frm, headerText + " NODE");
		appendTextNode(frm, "Label:");
		var value = "";
		if (typeof (element) != 'undefined')
			value = element.data.label;
		// TODO rename "nodeName" to label
		var input = createInput("nodeName", "nodeName", 17, 15, 'text', value);
		frm.appendChild(input);
		appendBr(frm, 1);

		// select type
		var label = document.createTextNode("Type: ");
		frm.appendChild(label);
		var defValue = "1";
		if (typeof (element) != 'undefined')
			defValue = element.data.type;
		appendSelect(frm, 'type', defValue);
		appendBr(frm, 1);

		// appendWeight(frm, "Regression intercept (resp.)", 0);

		// notes textarea
		var value = "";
		if (typeof (element) != 'undefined')
			value = element.data.notes;
		appendNoteField(frm, value);
		appendBr(frm, 1);

		// // idMaps
		// var defValue = null;
		// if (typeof (element) != 'undefined')
		// defValue = getIdMap(element.data, 'hill_2011');
		// appendTextNode(frm, "ID Hill 2011 (ft. improve network)");
		// appendSelect(frm, 'idMaps', defValue);
		// appendBr(frm, 1);

		buttonText = "Save";
		clickFunction = function() {
			addEditNode(element);
		};
	} else {// need list of all nodes
		if (formType == "addEditEdge" || formType == "removeEdge"
				|| formType == "removeNode") {
			var nodesSorted = copySortOnNames(RP.vis.nodes());
			if (formType == "addEditEdge") {
				var headerText = "ADD";
				if (typeof (element) != 'undefined')
					headerText = "EDIT";
				appendHeader(frm, "EDGE: " + headerText);

				if (typeof (element) != 'undefined')
					appendHeader(frm, "Id: " + element.data.id);

				appendTextNode(frm, "Source node: ");
				var defValue = null;
				if (typeof (element) != 'undefined')
					defValue = element.data.source;
				appendSelect(frm, 'source', defValue);

				appendTextNode(frm, "Target node: ");
				var defValue = null;
				if (typeof (element) != 'undefined')
					defValue = element.data.target;
				appendSelect(frm, 'target', defValue);

				appendTextNode(frm, "Interaction: ");
				var defValue = null;
				if (typeof (element) != 'undefined')
					defValue = element.data.interaction;
				appendSelect(frm, 'interaction', defValue);

/*
 * var defValue = 0.4; if (typeof (element) != 'undefined' &&
 * element.data.weight != null) defValue = element.data.weight;
 * appendWeight(frm, "Regression weight", defValue);
 */

				appendTextNode(frm, "References");
				appendBr(frm, 1);

				// show existing references
				var nextIdx = 0;
				if (typeof (element) != 'undefined'
						&& element.data.refs != null) {
					var refs = element.data.refs;

					for ( var i = 0; i < refs.length; i++) {
						var ref = refs[i];

						// Actually just one key value pair for each ref
						for (key in ref) {
							refEntry = crRefEntry(nextIdx, key, ref[key]);
							frm.appendChild(refEntry);
						}
						;
					}
					nextIdx = refs.length + 1;
				}

				refEntry = crRefEntry(nextIdx);
				frm.appendChild(refEntry);

				appendBr(frm, 1);

				// notes textarea
				var value = "";
				if (typeof (element) != 'undefined')
					value = element.data.notes;
				appendNoteField(frm, value);

				buttonText = "Save";
				clickFunction = function() {
					addEditEdge(element);
				};
			} else if (formType == "removeEdge") {
				appendHeader(frm, "REMOVE EDGE");

				appendTextNode(frm, "Source node: ");
				// wheen removing edge no def Value is defined
				appendSelect(frm, 'source', null);
				appendBr(frm, 1);

				appendTextNode(frm, "Target node: ");
				// wheen removing edge no def Value is defined
				appendSelect(frm, 'target', null);
				appendBr(frm, 1);

				buttonText = "Remove edge";
				clickFunction = function() {
					selectRemoveEdge();
				};
			} else if (formType == "removeNode") {
				appendHeader(frm, "REMOVE NODE");
				appendTextNode(frm, "Node: ");
				appendSelect(frm, "node", null);
				appendBr(frm, 1);
				buttonText = "Remove node";
				clickFunction = function() {
					selectRemoveNode();
				};
			}
		} else if (formType == "simulation") { 
			// need
			// list of need type 1
			appendHeader(frm, "DEFINE VALUES");

			// Input for simulation mode: timeCourse vs. steady State
			var values = [ "STEADYSTATE", "TIMECOURSE" ];
			var names = [ "Steady state", "Time course" ];
			for (i = 0; i < 2; i++) {
				var input = document.createElement("input");
				input.type = "radio";
				input.id = "simType" + i;
				input.name = "simType";
				input.value = values[i];
				if (i == 0)
					input.checked = true;
				frm.appendChild(input);
				var label = document.createTextNode(names[i]);
				frm.appendChild(label);
			}

			var buttonDef = document.createElement("button");
			buttonDef.id = "buttonDef";
			buttonDef.type = "button";
			var buttonDefFunction = function() {
				changeDefaultValue();
			};
			buttonDef.addEventListener('click', buttonDefFunction, false);
			var buttonText = document
					.createTextNode("Change default start value");
			buttonDef.appendChild(buttonText);
			frm.appendChild(buttonDef);

			var table = createTableNodeValues(false,element,false);
			frm.appendChild(table);

			// Input for number of iterations
			var label = document.createTextNode("Iterations time course:");
			frm.appendChild(label);
			var input = document.createElement("input");
			input.id = "iterations";
			input.name = "iterations";
			input.size = 3;
			input.maxLength = 3;
			input.type = "text";
			input.value = 50;
			frm.appendChild(input);

			buttonText = "Get simulation results";
			clickFunction = function() {
				getSimResult();
			};
		} else if (formType == "addEditObs") {
			var headerText = "NEW";
			var nameValue ="";
			if (typeof (element) != 'undefined') {
				headerText = "EDIT";
				nameValue = element.name;
			}	
			appendHeader(frm, "OBSERVATION: " + headerText);
			var label = document.createTextNode("Name:");
			frm.appendChild(label);
			var input = document.createElement("input");
			input.id = "name";
			input.name = "name";
			input.size = 22;
			input.maxLength = 20;
			input.type = "text";
			input.value = nameValue;
			frm.appendChild(input);

			/*
			 * // button to copy (f.e. steady state) values from network var
			 * buttonDef = document.createElement("button");
			 * buttonDef.id="buttonDef"; buttonDef.type="button"; var
			 * buttonDefFunction = function() { currentStateAsInput(); };
			 * buttonDef.addEventListener('click',buttonDefFunction, false); var
			 * buttonText = document.createTextNode("Current state network as
			 * input"); buttonDef.appendChild(buttonText);
			 * frm.appendChild(buttonDef);
			 */

			var table = createTableNodeValues(true, element,true);
			frm.appendChild(table);
			buttonText = "save";
			clickFunction = function() {
				addEditObs(element,true);
			};
		} else if (formType == "dbi") {
			appendHeader(frm, "RUN DBI");

			var label = document.createTextNode("Dataset: ");
			frm.appendChild(label);
			// dataset
			// options own upload
			appendSelect(frm, 'dataset', 'upload');

			// TODO add select box for regMode
			var params = [ {
				label : 'Max in degree:',
				id : 'maxInDegree',
				name : 'maxInDegree',
				size : 10,
				maxLength : 10,
				type : 'text',
				value : 4
			}, {
				label : 'Lambdas:',
				id : 'lambdas',
				name : 'lambdas',
				size : 10,
				maxLength : 10,
				type : 'text',
				value : 3
			}, {
				label : 'Threshold:',
				id : 'threshold',
				name : 'threshold',
				size : 10,
				maxLength : 10,
				type : 'text',
				value : 0.40
			} ];
			addParams(frm, params);

			// var select = document.createElement("select");
			// select.name = 'regMode';
			// select.id = 'regMode';
			// % reg_mode: String specifying regression model.
			// % 'full' - all interaction terms used (i.e up to product of all
			// components in model)
			// % 'quadratic' - interaction terms up to pairwise products only
			// % 'linear' - no interaction terms
			// var option = createOption(1, text, sel);
			// select.add(option);

			buttonText = "Propose mutations";
			clickFunction = function() {
				getDbiResult();
			};
		} else if (formType == 'implRevMutations') {
			appendHeader(frm, "IMPLEMENT MUTATIONS");

			var table = document.createElement("table");
			appendCaptionRow(table, 'purple', "add");
			appendCaptionRow(table, 'orange', "update (activate vs inhibit)");
			appendCaptionRow(table, 'yellow', "delete");
			frm.appendChild(table);

			appendSelect(frm, 'implRevMut', defValue);
			buttonText = "submit";
			clickFunction = function() {
				implRevMutations();
				emptyDivInput();
			};
		}
		;
	}
	var button = document.createElement("button");
	button.id = "button1";

	button.type = "button";

	var textNode1 = document.createTextNode(buttonText);
	button.appendChild(textNode1);
	button.addEventListener('click', clickFunction, false);

	frm.appendChild(button);
	div.appendChild(frm);

	if (formType == "dbi")
		populateDataset();

	Scroller.updateAll();

}

function setNetworkChanged(changed) {
	RP.networkChanged = changed;
}

/*
 * makeHandler function to have the parameter set at assignment and not at
 * trigger
 */
function makeHandler(name) {
	return function(evt) {
		storedNetwork(name);
	}
}

function createLi(el, text, click) {
	var li = document.createElement('li');
	var a = document.createElement('a');
	a.href = '#';
	a.onclick = click;
	var text = document.createTextNode(text);
	a.appendChild(text);
	li.appendChild(a);
	return (li);
}

function addStoredNetworks() {

	successFunction = function(names) {
		// var names = [ 'hill_2011_prior', 'vidal_2011_prior' ];
		if (names.length > 0) {
			var fileMenu = document.getElementById('fileMenu');
			for ( var i = 0, len = names.length; i < len; i++) {
				if (names[i] != ".empty.txt") {
					var name = names[i].substring(0, names[i].length - 6);
					var click = makeHandler(name);
					var li = createLi(fileMenu, name, click);
					fileMenu.appendChild(li);
				}
			}
			;
		}
		;
	};

	var data = {
		filesDir : 'networks'
	};

	getResult(data, 'namesFiles', "json", successFunction);

}

function datasetAppend(names) {
	// names = ['hill_2011','vidal_2011_mda_mb_231.tsv',
	// 'vidal_2011_t47d.tsv'];
	if (names.length > 0) {
		var dataset = document.getElementById('dataset');
		for ( var i = 0, len = names.length; i < len; i++) {
			var name = names[i].substring(0, names[i].length - 4);

			// Start with uppercase
			// replace _ by space
			var text = name.replace(/_/g, ' ');

			var option = createOption(name, text, null);
			dataset.appendChild(option);
		}
		;
	}
	;
}

function populateDataset() {
	if (RP.datasets == null) { // first time
		successFunction = function(names) {
			RP.datasets = names;
			datasetAppend(names);
		};

		var data = {
			filesDir : 'datasets'
		};

		getResult(data, 'namesFiles', "json", successFunction);
	} else
		datasetAppend(RP.datasets);

}

function initRpInterface() {

	addStoredNetworks();

	ddsmoothmenu.init({
		mainmenuid : "mainMenu", // Menu DIV id
		orientation : 'h', // Horizontal or vertical menu: Set to "h" or "v"
		classname : 'ddsmoothmenu', // class added to menu's outer DIV
		// customtheme: ["#804000", "#482400"],
		contentsource : "markup" // "markup" or ["container_id",
	// "path_to_menu_file"]
	});

	if (RP.states == null) // greyOut
		setOpacity(document.getElementById("divSimControlPanel"), 50);

	// initialization options
	var options = {
		swfPath : "swf/CytoscapeWeb",
		flashInstallerPath : "swf/playerProductInstall"
	};

	// populate Cytoscape Web container div
	RP.vis = new org.cytoscapeweb.Visualization("cytoscapeweb", options);

	RP.vis.addListener("click", "edges", function(evt) {
		var edge = evt.target;
		createForm('addEditEdge', edge);
	});

	RP.vis.addListener("click", "nodes", function(evt) {
		var node = evt.target;
		createForm('addEditNode', node);
	});

	var loadOptions = {
		swfPath : "swf/Importer",
		flashInstallerPath : "swf/playerProductInstall",
		data : function(data) {
			var drawOptions = {
				/*
				 * draw options apparantly contains copies, no references
				 */
				network : data.string,
				edgeLabelsVisible : false,
				layout : "Preset",
				visualStyle : RP.visualStyle
			};

			RP.vis.ready(function() {
				completeNetwork();
				RP.vis.visualStyle(RP.visualStyle);
			});
			RP.vis.draw(drawOptions);
			/*
			 * will done after draw is complete set RP.vis.ready code
			 */
		}
	};
	var networkImport = new org.cytoscapeweb.demo.Importer("networkImport",
			loadOptions);

	// Create seperate export objects for xgmml and sif
	var types = new Array("xgmml"); // ,"sif"
	for ( var i = 0; i < types.length; i++) {
		var type = types[i];
		var id = "exporter_" + type;

		var dataFunction = function() {
			return RP.vis['xgmml']();
		};

		if (type == 'sif')
			dataFunction = function() {
				return RP.vis['sif']();
			};

		var exportOptions = {
			swfPath : "swf/Exporter",
			flashInstallerPath : "swf/playerProductInstall",
			base64 : false,
			data : dataFunction,
			fileName : function() {
				return default_file_name(type);
			},
			ready : function() {
				$("#" + id).trigger("available");
			}
		};

		var divNameExt = 'Xgmml';
		if (type == 'sif')
			divNameExt = 'Sif';

		new org.cytoscapeweb.demo.Exporter("networkExport" + divNameExt,
				exportOptions);

	}

	// Prepare importer for observations
	var obsLoadOptions = {
		swfPath : "swf/Importer",
		flashInstallerPath : "swf/playerProductInstall",
		data : function(data) {
			RP.obs = JSON.parse(data.string);
			crObsView(0);
		}
	};
	new org.cytoscapeweb.demo.Importer("obsImport", obsLoadOptions);

	var obsFn = ".json";
	var obsId = "exporter_" + obsFn;
	exportOptions = {
		swfPath : "swf/Exporter",
		flashInstallerPath : "swf/playerProductInstall",
		base64 : false,
		data : function() {
			return JSON.stringify(RP.obs);
		},
		fileName : function() {
			return default_file_name(obsFn);
		},
		ready : function() {
			$("#" + id).trigger("available");
		}
	};
	var obsExport = new org.cytoscapeweb.demo.Exporter("obsExport",
			exportOptions);

/*
 * // Prepare importer for data var dataLoadOptions = { swfPath :
 * "swf/Importer", flashInstallerPath : "swf/playerProductInstall", data :
 * function(data) { RP.timeCourseData = data;
 * setUploadFileName(data.metadata.name); } };
 * 
 * new org.cytoscapeweb.demo.Importer("dataImport", dataLoadOptions);
 */

	// TODO put var in front again to make it a local variable
	startXml = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>\
	    <graph label="Network" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:cy="http://www.cytoscape.org" xmlns="http://www.cs.rpi.edu/XGMML"  directed="1">\
	    <att name="documentVersion" value="1.1"/>\
	    <att name="networkMetadata">\
	      <rdf:RDF>\
	        <rdf:Description rdf:about="http://www.cytoscape.org/">\
	          <dc:type>Protein-Protein Interaction</dc:type>\
	          <dc:description>N/A</dc:description>\
	          <dc:identifier>N/A</dc:identifier>\
	          <dc:date>2010-12-26 17:48:18</dc:date>\
	          <dc:title>Network</dc:title>\
	          <dc:source>http:// www.cytoscape.org/</dc:source>\
	          <dc:format>Cytoscape-XGMML</dc:format>\
	        </rdf:Description>\
	      </rdf:RDF>\
	    </att>\
	    <att type="real" name="GRAPH_VIEW_ZOOM" value="0.4507732469934438"/>\
	    <att type="real" name="GRAPH_VIEW_CENTER_X" value="473.8125"/>\
	    <att type="real" name="GRAPH_VIEW_CENTER_Y" value="585.0"/>\
	    <att type="string" name="__layoutAlgorithm" value="hierarchical" cy:hidden="true"/>\
	    <node label="S0" id="-1">\
	      <att type="integer" name="Type" value="1"/>\
	      <att type="string" name="canonicalName" value="S1canName"/>\
	      <graphics type="ELLIPSE" h="40.0" w="40.0" x="617.5" y="283" fill="#ffcccc" width="1" outline="#666666" cy:nodeTransparency="1.0" cy:nodeLabelFont="SansSerif.bold-0-12" cy:borderLineType="solid"/>\
	    </node>\
	    <node label="S1" id="-2">\
	      <att type="integer" name="Type" value="1"/>\
	      <att type="string" name="canonicalName" value="S2canName"/>\
	      <graphics type="ELLIPSE" h="40.0" w="40.0"  x="583" y="373" fill="#ffcccc" width="1" outline="#666666" cy:nodeTransparency="1.0" cy:nodeLabelFont="SansSerif.bold-0-12" cy:borderLineType="solid"/>\
	      </node>\
	    <node label="S2" id="-3">\
	      <att type="integer" name="Type" value="1"/>\
	      <att type="string" name="canonicalName" value="S3canName"/>\
	      <graphics type="ELLIPSE" h="40.0" w="40.0" x="654" y="373" fill="#ffcccc" width="1" outline="#666666" cy:nodeTransparency="1.0" cy:nodeLabelFont="SansSerif.bold-0-12" cy:borderLineType="solid"/>\
	    </node>\
	    <edge label="C1 (1) S1" source="-1" target="-2" id="1">\
	      <att type="string" name="canonicalName" value="C1 (1) S1"/>\
	      <att type="string" name="interaction" value="1" cy:editable="false"/>\
	      <att type="string" name="state" value="0"/>\
	      <graphics width="1" fill="#ff0000" cy:sourceArrow="0" cy:targetArrow="15" cy:sourceArrowColor="#000000" cy:targetArrowColor="#ff0000" cy:edgeLabelFont="Default-0-10" cy:edgeLineType="SOLID" cy:curved="STRAIGHT_LINES"/>\
	    </edge>\
	    <edge label="C1 (-1) S1" source="-1" target="-3" id="2">\
	    <att type="string" name="canonicalName" value="C1 (-1) S1"/>\
	    <att type="string" name="interaction" value="1" cy:editable="false"/>\
	    <graphics width="1" fill="#0000ff" cy:sourceArrow="0" cy:targetArrow="3" cy:sourceArrowColor="#000000" cy:targetArrowColor="#0000ff" cy:edgeLabelFont="Default-0-10" cy:edgeLineType="SOLID" cy:curved="STRAIGHT_LINES"/>\
	  </edge>\
	  </graph>\
	';
	  
	  /*
		 * <node label="R1" id="-4">\ <att type="integer" name="Type"
		 * value="2"/>\ <att type="string" name="canonicalName" value="C"/>\
		 * <att type="real" name="weight" value="0.2" cy:editable="false"/>\
		 * <graphics x="619" y="457" labelanchor="c"
		 * cy:nodeLabelFont="Arial-0-15" w="40" width="3" outline="#ffffff"
		 * type="DIAMOND" fill="#3ea99f" h="40" cy:nodeTransparency="0.8"/>\
		 * </node>\ <edge label="S1 (1) R1" source="-2" target="-4" id="3">\
		 * <att type="string" name="canonicalName" value="S1 (1) R1"/>\ <att
		 * type="string" name="interaction" value="1" cy:editable="false"/>\
		 * <att type="real" name="weight" value="0.2" cy:editable="false"/>\
		 * <graphics cy:sourceArrowColor="#000000" cy:sourceArrow="0" width="4"
		 * cy:targetArrow="6" cy:targetArrowColor="#000000" fill="#3ea99f"
		 * cy:edgeLineType="SOLID"/>\ </edge>\ <edge label="S2 (-1) R1"
		 * source="-3" target="-4" id="4">\ <att type="string"
		 * name="canonicalName" value="S2 (-1) R1"/>\ <att type="string"
		 * name="interaction" value="1" cy:editable="false"/>\ <att type="real"
		 * name="weight" value="0.8" cy:editable="false"/>\ <graphics
		 * cy:sourceArrowColor="#000000" cy:sourceArrow="0" width="4"
		 * cy:targetArrow="15" cy:targetArrowColor="#000000" fill="#3ea99f"
		 * cy:edgeLineType="SOLID"/>\ </edge>\
		 */
	var layoutOptions = {
		fitToScreen : false
	};

	var layout = {
		name : "Preset",
		options : {
			fitToScreen : false
		}
	}

	drawOptions = {
		network : startXml,
		edgeLabelsVisible : false,
		layout : layout,
		visualStyle : RP.visualStyle
	};

	RP.vis.ready(function() {
		completeNetwork();
		RP.vis.visualStyle(RP.visualStyle);
		RP.vis.zoomToFit();
	});

	RP.vis.draw(drawOptions);
	
	openCloseObsPane();

};

window.onbeforeunload = function(evt) {
	if (RP.networkChanged) {

		var message = 'Your network has unsaved changes.';
		if (typeof evt == 'undefined') {
			evt = window.event;
		}
		if (evt) {
			evt.returnValue = message;
		}
		return message;
	}
};

function drawDetails(tab, obsIndex) {
	var players = getPlayers();
	if (typeof (obsIndex) == 'undefined' || obsIndex == "")
		obsIndex = 0;

	// draw header
	var o = RP.obs[obsIndex];

	// rowname)
	// for start,fixed, end
	for ( var i = 0; i < 3; i++) {
		for ( var j = 0; j < players.length; j++) {
			var node = players[j];

			// check if an observation has an value for an particular node
			var value = "";
			if (i == 0) {
				if (node.data.id in o.start)
					value = o.start[node.data.id];
			} else if (i == 1) {
				if (o.fixed.indexOf(node.data.id) != -1)
					value = "T";
				else
					value = "F";
			} else if (i == 2) {
				if (node.data.id in o.end)
					value = o.end[node.data.id];
			}

			// last three rows
			// tr - td - textnode
			var startRow = 1 + RP.obs.length;

			var tr = tab.childNodes[startRow + i];
			// col 0: simulation, 1: edit 2:trash bin , 3: rowname
			var startCol = 4;
			var td = tr.childNodes[startCol + j];

			// textnode
			// initially tried to set the contentText of the TextNode
			// however childNodes of the td turns out to be empty
			if (td.hasChildNodes())
				while (td.childNodes.length >= 1)
					td.removeChild(td.firstChild);
			var txt = document.createTextNode(value);
			td.appendChild(txt);
		}
	}
}

function crObsView(obsIndex) {
	var div = document.getElementById("obs");
	emptyElement(div);
	var rowIds = new Array();
	for ( var i = 0; i < RP.obs.length; i++)
		rowIds.push(RP.obs[i].name);

	var players = getNodesByType([ 0, 1 ]);
	var colIds = getNodeIdArray(players, 'label');

	var data = new Array(rowIds.length);
	for (i = 0; i < rowIds.length; i++)
		data[i] = new Array(colIds.length);

	var tab = createObsTable(obsIndex, rowIds, colIds, data);

	div.appendChild(tab);
}

function getFirstChildWithTagName(element, tagName) {
	for ( var i = 0; i < element.childNodes.length; i++) {
		if (element.childNodes[i].nodeName == tagName)
			return element.childNodes[i];
	}
};

function getHash(url) {
	var hashPos = url.lastIndexOf('#');
	return url.substring(hashPos + 1);
};

/**
 * data is the data attribute of the node returns id used in specific dataset.
 * In case no id mapped, return null
 * 
 * @param node
 */

function getIdMap(data, dataset) {
	var id = null;
	if (data.idMaps != null)
		for ( var i = 0; i < data.idMaps.length; i++) {
			var idMap = data.idMaps[i];

			if (RP.nodeIdGroup in idMap)
				id = idMap[RP.nodeIdGroup];
		}

	return id;
}

function setNodeIdentifier() {

	// 1. First, create a function and add it to the Visualization object.
	RP.vis["setNodeIdentifier"] = function(data) {
		var id = getIdMap(data, RP.nodeIdGroup);
		if (id == null)
			id = "";
		return (id);
	};

	// 2. Now create a new visual style (or get the current one) and register
	// the custom mapper to one or more visual properties:
	var style = RP.vis.visualStyle();
	style.nodes.label = {
		customMapper : {
			functionName : "setNodeIdentifier"
		}
	},

	// 3. Finally set the visual style again:
	RP.vis.visualStyle(style);

}

function debugCheckUpdateEdge() {
	// <edge label="A (-1) C" source="-1" target="-3" id="e2">\
	// <att type="string" name="canonicalName" value="A (-1) C"/>\
	// <att type="string" name="interaction" value="-1" cy:editable="false"/>\
	// <att name="refs" type="list">\
	// </att>\
	// <att name="notes" type="string" value=""/>\

	updateEdges = [ {
		"id" : "e2",
		"label" : "CD",
		"directed" : true,
		"source" : -3,
		"target" : -4,
		"interaction" : 1,
		"refs" : [ "ref3" ]
	} ];
	var edgeData = updateEdges[0];
	var edge = RP.vis.edges()[getEdgeIndex("id", edgeData.id)];
	edge.data = edgeData;
	// TODO replace with update call with ids
	// it then just updates the given fields
	// see f.e. addEditEdge
	RP.vis.updateData([ edge ]);

}

function initUpload(fileType) {
	document.getElementById('frmInput').onsubmit = function() {
		document.getElementById('frmInput').target = 'upload_target';

		/*
		 * 'upload_target' is the name of the iframe
		 * 
		 * The following function should be called when the iframe has
		 * compleated loading. That will happen when the file is completely
		 * uploaded and the server has returned the data we need.
		 */
		document.getElementById("upload_target").onload = uploadDone(fileType);
	}
}

function uploadDone(fileType) { // Function will be called when iframe is loaded
	var iframe = frames['upload_target'];
	var doc = iframe.document;
	if (fileType == 'xml') {
		var graph0 = doc.getElementsByTagName("graph")[0];
		var strs = (new XMLSerializer()).serializeToString(graph0);
		drawNetwork(strs);
	} else {// 2
		var content = doc.body.innerHTML;
		RP.timeCourseData == doc.body.innerHTML;
	}
	// First remove content from iframe. Otherwise on the tab is still shown
	// that firefox is active
	var frame = document.getElementById('upload_target'), frameDoc = frame.contentDocument
			|| frame.contentWindow.document;
	frameDoc.documentElement.parentNode.removeChild(frameDoc.documentElement);

	emptyDivInput();
}

function setUploadFileName(fileName) {

	var div = document.getElementById('mainMenuLi0A');
	emptyElement(div);

	var textNode = document.createTextNode("Upload: " + fileName);
	div.appendChild(textNode);
}

function activateImporter() {
	var span = document.getElementById('networkImport');
	var q = 0;

}

function openCloseObsPane() {
	var divWeb = document.getElementById('cytoscapeweb');
	var divObs = document.getElementById('obs');
	if (RP.divModelCheckVisible) {
		divWeb.style.width = "82%";
		divObs.style.visibility = "hidden";
	} else {
		divWeb.style.width = "41%";
		divObs.style.visibility = "visible";
	}
	;

	RP.divModelCheckVisible = !RP.divModelCheckVisible;

}
function getDummySimResult() {
	resetSim();
	RP.states = [ {
		"-1" : 1,
		"-2" : 0,
		"-3" : 0,
		"-4" : 0
	}, {
		"-1" : 1,
		"-2" : 1,
		"-3" : 0,
		"-4" : 0
	}, {
		"-1" : 1,
		"-2" : 1,
		"-3" : 1,
		"-4" : 0.2
	}, {
		"-1" : 1,
		"-2" : 1,
		"-3" : 1,
		"-4" : 1
	} ];
	updateNodesWithStartValues();

}

function emptyNetwork() {
	// remove all nodes

	for ( var len = RP.vis.nodes().length, i = len - 1; i >= 0; i--) {
		var node = RP.vis.nodes()[i];
		RP.vis.removeNode(node);
	}
}


function getColor() {
	var x = jQuery.colors( 'lightCoral' ).model('HSL').get();
	
	return x;
}

function clearObs() {
	
	if (confirm("Are you sure you want to remove all observations?")) {
		RP.obs=[];
		crObsView();
	}
}

