package nl.nki.responsepredictor;

public class RpTestData {

    // Example network with 3 play nodes: A,B,C and one And-gate node
    // A activates the And-gate (ic. C), and B inhibits the And-gate (ic. C)
    static String network1 = "<graph label=\"Cytoscape Web\" directed=\"1\" Graphic=\"1\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:cy=\"http://www.cytoscape.org\" xmlns=\"http://www.cs.rpi.edu/XGMML\">"
	    + "<att name=\"documentVersion\" value=\"0.1\"/>"
	    + "<att type=\"string\" name=\"backgroundColor\" value=\"#ffffff\"/>"
	    + "<att type=\"real\" name=\"GRAPH_VIEW_ZOOM\" value=\"1\"/>"
	    + "<att type=\"real\" name=\"GRAPH_VIEW_CENTER_X\" value=\"619\"/>"
	    + "<att type=\"real\" name=\"GRAPH_VIEW_CENTER_Y\" value=\"360.5\"/>"
	    + "<node label=\"andAB\" name=\"\" id=\"-4\">"
	    + "<att name=\"Type\" type=\"real\" value=\"-2\"/>"
	    + "<att name=\"canonicalName\" type=\"string\" value=\"andAB\"/>"
	    + "<att name=\"state\" type=\"real\" value=\"0\"/>"
	    + "<graphics x=\"618.5\" y=\"360.5\" w=\"20\" cy:nodeLabelFont=\"Arial-0-15\" labelanchor=\"c\" fill=\"#f85b5b\" width=\"3\" h=\"20\" outline=\"#ffffff\" type=\"ELLIPSE\" cy:nodeTransparency=\"0.8\"/>"
	    + "</node>"
	    + "<node label=\"A\" name=\"\" id=\"-1\">"
	    + "<att name=\"Type\" type=\"real\" value=\"1\"/>"
	    + "<att name=\"canonicalName\" type=\"string\" value=\"A\"/>"
	    + "<att name=\"state\" type=\"real\" value=\"1\"/>"
	    + "<graphics x=\"568.5\" y=\"286.5\" w=\"40\" cy:nodeLabelFont=\"Arial-0-15\" labelanchor=\"c\" fill=\"#00ff00\" width=\"3\" h=\"40\" outline=\"#ffffff\" type=\"ELLIPSE\" cy:nodeTransparency=\"0.8\"/>"
	    + "</node>"
	    + "<node label=\"B\" name=\"\" id=\"-2\">"
	    + "<att name=\"Type\" type=\"real\" value=\"1\"/>"
	    + "<att name=\"canonicalName\" type=\"string\" value=\"B\"/>"
	    + "<att name=\"state\" type=\"real\" value=\"1\"/>"
	    + "<graphics x=\"669.5\" y=\"286.5\" w=\"40\" cy:nodeLabelFont=\"Arial-0-15\" labelanchor=\"c\" fill=\"#00ff00\" width=\"3\" h=\"40\" outline=\"#ffffff\" type=\"ELLIPSE\" cy:nodeTransparency=\"0.8\"/>"
	    + "</node>"
	    + "<node label=\"C\" name=\"\" id=\"-3\">"
	    + "<att name=\"Type\" type=\"real\" value=\"1\"/>"
	    + "<att name=\"canonicalName\" type=\"string\" value=\"C\"/>"
	    + "<att name=\"state\" type=\"real\" value=\"0\"/>"
	    + "<graphics x=\"617.5\" y=\"434.5\" w=\"40\" cy:nodeLabelFont=\"Arial-0-15\" labelanchor=\"c\" fill=\"#f85b5b\" width=\"3\" h=\"40\" outline=\"#ffffff\" type=\"ELLIPSE\" cy:nodeTransparency=\"0.8\"/>"
	    + "</node>"
	    + "<edge label=\"A (1) andAB\" source=\"-1\" target=\"-4\" directed=\"true\" id=\"1\">"
	    + "<att name=\"interaction\" type=\"string\" value=\"1\"/>"
	    + "<att name=\"canonicalName\" type=\"string\" value=\"A (1) andAB\"/>"
	    + "<graphics width=\"3\" cy:targetArrowColor=\"#000000\" cy:sourceArrow=\"0\" cy:targetArrow=\"6\" fill=\"#0b94b1\" cy:sourceArrowColor=\"#000000\" cy:edgeLineType=\"SOLID\"/>"
	    + "</edge>"
	    + "<edge label=\"B (-1) andAB\" source=\"-2\" target=\"-4\" directed=\"true\" id=\"2\">"
	    + "<att name=\"interaction\" type=\"string\" value=\"-1\"/>"
	    + "<att name=\"canonicalName\" type=\"string\" value=\"B (-1) andAB\"/>"
	    + "<graphics width=\"3\" cy:targetArrowColor=\"#000000\" cy:sourceArrow=\"0\" cy:targetArrow=\"15\" fill=\"#0b94b1\" cy:sourceArrowColor=\"#000000\" cy:edgeLineType=\"SOLID\"/>"
	    + "</edge>"
	    + "<edge label=\"andAB (1) C\" source=\"-4\" target=\"-3\" directed=\"true\" id=\"3\">"
	    + "<att name=\"interaction\" type=\"string\" value=\"1\"/>"
	    + "<att name=\"canonicalName\" type=\"string\" value=\"andAB (1) C\"/>"
	    + "<graphics width=\"3\" cy:targetArrowColor=\"#000000\" cy:sourceArrow=\"0\" cy:targetArrow=\"6\" fill=\"#0b94b1\" cy:sourceArrowColor=\"#000000\" cy:edgeLineType=\"SOLID\"/>"
	    + "</edge>" + "</graph>";

    // Network with S0 activates S1 and S1 activates R(esponse)
    static String network2 = "<graph label=\"Cytoscape Web\" directed=\"1\" Graphic=\"1\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:cy=\"http://www.cytoscape.org\" xmlns=\"http://www.cs.rpi.edu/XGMML\">"
	    + "<att name=\"documentVersion\" value=\"0.1\"/>"
	    + "<att type=\"string\" name=\"backgroundColor\" value=\"#ffffff\"/>"
	    + "<att type=\"real\" name=\"GRAPH_VIEW_ZOOM\" value=\"1\"/>"
	    + "<att type=\"real\" name=\"GRAPH_VIEW_CENTER_X\" value=\"620.75\"/>"
	    + "<att type=\"real\" name=\"GRAPH_VIEW_CENTER_Y\" value=\"370\"/>"
	    + "<node label=\"S0\" id=\"-1\" name=\"\">"
	    + "<att name=\"canonicalName\" type=\"string\" value=\"A.A\"/>"
	    + "<att name=\"Type\" type=\"integer\" value=\"1\"/>"
	    + "<att name=\"state\" type=\"real\" value=\"NaN\"/>"
	    + "<graphics x=\"618\" y=\"290\" w=\"40\" fill=\"#ff99ff\" width=\"3\" outline=\"#ffffff\" labelanchor=\"c\" h=\"40\" cy:nodeTransparency=\"0.8\" cy:nodeLabelFont=\"Arial-0-15\" type=\"ELLIPSE\"/>"
	    + "</node>"
	    + "<node label=\"S1\" id=\"-2\" name=\"\">"
	    + "<att name=\"canonicalName\" type=\"string\" value=\"B\"/>"
	    + "<att name=\"Type\" type=\"integer\" value=\"1\"/>"
	    + "<att name=\"state\" type=\"real\" value=\"NaN\"/>"
	    + "<graphics x=\"621.5\" y=\"373\" w=\"40\" fill=\"#ff99ff\" width=\"3\" outline=\"#ffffff\" labelanchor=\"c\" h=\"40\" cy:nodeTransparency=\"0.8\" cy:nodeLabelFont=\"Arial-0-15\" type=\"ELLIPSE\"/>"
	    + "</node>"
	    + "<node label=\"R\" id=\"-3\" name=\"\">"
	    + "<att name=\"canonicalName\" type=\"string\" value=\"R\"/>"
	    + "<att name=\"Type\" type=\"integer\" value=\"2\"/>"
	    + "<att name=\"state\" type=\"real\" value=\"NaN\"/>"
	    + "<att name=\"weight\" type=\"real\" value=\"0.3\"/>"
	    + "<graphics x=\"623.5\" y=\"450\" w=\"40\" fill=\"#ff99ff\" width=\"3\" outline=\"#ffffff\" labelanchor=\"c\" h=\"40\" cy:nodeTransparency=\"0.8\" cy:nodeLabelFont=\"Arial-0-15\" type=\"ELLIPSE\"/>"
	    + "</node>"
	    + "<edge label=\"-1_-2\" id=\"1\" target=\"-2\" directed=\"true\" source=\"-1\">"
	    + "<att name=\"interaction\" type=\"string\" value=\"1\"/>"
	    + "<graphics cy:sourceArrow=\"0\" cy:targetArrow=\"6\" width=\"3\" cy:sourceArrowColor=\"#000000\" cy:edgeLineType=\"SOLID\" fill=\"#0b94b1\" cy:targetArrowColor=\"#000000\"/>"
	    + "</edge>"
	    + "<edge label=\"-2_-3\" id=\"2\" target=\"-3\" directed=\"true\" source=\"-2\">"
	    + "<att name=\"interaction\" type=\"string\" value=\"1\"/>"
	    + "<att name=\"weight\" type=\"real\" value=\"0.3\"/>"	    
	    + "<graphics cy:sourceArrow=\"0\" cy:targetArrow=\"6\" width=\"3\" cy:sourceArrowColor=\"#000000\" cy:edgeLineType=\"SOLID\" fill=\"#0b94b1\" cy:targetArrowColor=\"#000000\"/>"
	    + "</edge>" + "</graph>";

    // Network with A act C and B inh C
    static String network3 = "<graph label=\"Cytoscape Web\" directed=\"1\" Graphic=\"1\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:cy=\"http://www.cytoscape.org\" xmlns=\"http://www.cs.rpi.edu/XGMML\">"
	    + "<att name=\"documentVersion\" value=\"0.1\"/>"
	    + "<att type=\"string\" name=\"backgroundColor\" value=\"#ffffff\"/>"
	    + "<att type=\"real\" name=\"GRAPH_VIEW_ZOOM\" value=\"1\"/>"
	    + "<att type=\"real\" name=\"GRAPH_VIEW_CENTER_X\" value=\"538.75\"/>"
	    + "<att type=\"real\" name=\"GRAPH_VIEW_CENTER_Y\" value=\"231.5\"/>"
	    + "<node label=\"A\" name=\"\" id=\"-1\">"
	    + "<att name=\"canonicalName\" type=\"string\" value=\"A\"/>"
	    + "<att name=\"state\" type=\"real\" value=\"NaN\"/>"
	    + "<att name=\"Type\" type=\"integer\" value=\"1\"/>"
	    + "<graphics x=\"498.5\" y=\"192\" w=\"40\" fill=\"#ff99ff\" width=\"3\" outline=\"#ffffff\" labelanchor=\"c\" h=\"40\" cy:nodeTransparency=\"0.8\" cy:nodeLabelFont=\"Arial-0-15\" type=\"ELLIPSE\"/>"
	    + "</node>"
	    + "<node label=\"B\" name=\"\" id=\"-2\">"
	    + "<att name=\"state\" type=\"real\" value=\"NaN\"/>"
	    + "<att name=\"canonicalName\" type=\"string\" value=\"B\"/>"
	    + "<att name=\"Type\" type=\"integer\" value=\"1\"/>"
	    + "<graphics x=\"579\" y=\"194\" w=\"40\" fill=\"#ff99ff\" width=\"3\" outline=\"#ffffff\" labelanchor=\"c\" h=\"40\" cy:nodeTransparency=\"0.8\" cy:nodeLabelFont=\"Arial-0-15\" type=\"ELLIPSE\"/>"
	    + "</node>"
	    + "<node label=\"C\" name=\"\" id=\"-3\">"
	    + "<att name=\"canonicalName\" type=\"string\" value=\"C\"/>"
	    + "<att name=\"state\" type=\"real\" value=\"NaN\"/>"
	    + "<att name=\"Type\" type=\"integer\" value=\"1\"/>"
	    + "<graphics x=\"536\" y=\"271\" w=\"40\" fill=\"#ff99ff\" width=\"3\" outline=\"#ffffff\" labelanchor=\"c\" h=\"40\" cy:nodeTransparency=\"0.8\" cy:nodeLabelFont=\"Arial-0-15\" type=\"ELLIPSE\"/>"
	    + "</node>"
	    + "<edge label=\"-1_-3\" directed=\"true\" id=\"e1\" target=\"-3\" source=\"-1\">"
	    + "<att name=\"interaction\" type=\"string\" value=\"1\"/>"
	    + "<graphics cy:targetArrow=\"6\" width=\"3\" cy:sourceArrowColor=\"#000000\" cy:edgeLineType=\"SOLID\" fill=\"#0b94b1\" cy:targetArrowColor=\"#000000\" cy:sourceArrow=\"0\"/>"
	    + "</edge>"
	    + "<edge label=\"-2_-3\" directed=\"true\" id=\"e2\" target=\"-3\" source=\"-2\">"
	    + "<att name=\"interaction\" type=\"string\" value=\"-1\"/>"
	    + "<graphics cy:targetArrow=\"15\" width=\"3\" cy:sourceArrowColor=\"#000000\" cy:edgeLineType=\"SOLID\" fill=\"#0b94b1\" cy:targetArrowColor=\"#000000\" cy:sourceArrow=\"0\"/>"
	    + "</edge>" + "</graph>";
    
    //A actives B, A inhibits C
    // files can contain \n , so introduced one in network4 at node A idMaps to test if it goes will
    
    static String network4 = "<graph label=\"Cytoscape Web\" directed=\"1\" Graphic=\"1\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:cy=\"http://www.cytoscape.org\" xmlns=\"http://www.cs.rpi.edu/XGMML\">"
	    + "<att name=\"documentVersion\" value=\"0.1\"/>"
	    + "<att type=\"string\" name=\"backgroundColor\" value=\"#ffffff\"/>"
	    + "<att type=\"real\" name=\"GRAPH_VIEW_ZOOM\" value=\"1\"/>"
	    + "<att type=\"real\" name=\"GRAPH_VIEW_CENTER_X\" value=\"600.5\"/>"
	    + "<att type=\"real\" name=\"GRAPH_VIEW_CENTER_Y\" value=\"283.5\"/>"
	    + "<node label=\"A\" name=\"\" id=\"-1\">"
  	    + "<att name=\"Type\" type=\"integer\" value=\"1\"/>"
  	    + "<att name=\"canonicalName\" type=\"string\" value=\"AA\"/>"
  	    + "<att name=\"state\" type=\"real\" value=\"0\"/>"
  	    + "<att name=\"notes\" type=\"string\" value=\"notesA\"/>"
  	    + "<att name=\"idMaps\" type=\"list\">\n"
	    + "<att name=\"hill_2011\" type=\"string\" value=\"AKT.pT308\"/>"
	    + "</att>"  	    
  	    + "<graphics x=\"599.5\" y=\"238.5\" w=\"40\" cy:nodeLabelFont=\"Arial-0-15\" fill=\"#ff99ff\" width=\"3\" h=\"40\" outline=\"#ffffff\" labelanchor=\"c\" cy:nodeTransparency=\"0.8\" type=\"ELLIPSE\"/>"
	    + "</node>"
	    + "<node label=\"B\" name=\"\" id=\"-2\">"
  	    + "<att name=\"Type\" type=\"integer\" value=\"1\"/>"
  	    + "<att name=\"canonicalName\" type=\"string\" value=\"BB\"/>"
  	    + "<att name=\"state\" type=\"real\" value=\"0\"/>"
  	    + "<att name=\"notes\" type=\"string\" value=\"notesB\"/>"
  	    + "<att name=\"idMaps\" type=\"list\">"
	    + "<att name=\"hill_2011\" type=\"string\" value=\"AMPK.PT172\"/>"
	    + "</att>"  	    
  	    + "<graphics x=\"565\" y=\"328.5\" w=\"40\" cy:nodeLabelFont=\"Arial-0-15\" fill=\"#ff99ff\" width=\"3\" h=\"40\" outline=\"#ffffff\" labelanchor=\"c\" cy:nodeTransparency=\"0.8\" type=\"ELLIPSE\"/>"
	    + "</node>"
	    + "<node label=\"C\" name=\"\" id=\"-3\">"
  	    + "<att name=\"Type\" type=\"integer\" value=\"1\"/>"
  	    + "<att name=\"canonicalName\" type=\"string\" value=\"CC\"/>"
  	    + "<att name=\"state\" type=\"real\" value=\"0\"/>"
	    + "<att name=\"idMaps\" type=\"list\">"
	    + "<att name=\"hill_2011\" type=\"string\" value=\"cJUN.pS73\"/>"
	    + "</att>" 
	    + "<att name=\"notes\" type=\"string\" value=\"notesC\"/>"
  	    + "<graphics x=\"636\" y=\"328.5\" w=\"40\" cy:nodeLabelFont=\"Arial-0-15\" fill=\"#ff99ff\" width=\"3\" h=\"40\" outline=\"#ffffff\" labelanchor=\"c\" cy:nodeTransparency=\"0.8\" type=\"ELLIPSE\"/>"
	    + "</node>"
	    + "<edge label=\"A (1) B\" source=\"-1\" target=\"-2\" directed=\"true\" id=\"1\">"
  	    + "<att name=\"interaction\" type=\"string\" value=\"1\"/>"
  	    + "<att name=\"canonicalName\" type=\"string\" value=\"A (1) B\"/>"
  	    + "<graphics width=\"3\" cy:targetArrowColor=\"#000000\" cy:sourceArrow=\"0\" fill=\"#0b94b1\" cy:targetArrow=\"6\" cy:sourceArrowColor=\"#000000\" cy:edgeLineType=\"SOLID\"/>"
	    + "</edge>"
	    + "<edge label=\"A (-1) C\" source=\"-1\" target=\"-3\" directed=\"true\" id=\"2\">"
  	    + "<att name=\"interaction\" type=\"string\" value=\"-1\"/>"
  	    + "<att name=\"canonicalName\" type=\"string\" value=\"A (-1) C\"/>"
  	    + "<graphics width=\"3\" cy:targetArrowColor=\"#000000\" cy:sourceArrow=\"0\" fill=\"#0b94b1\" cy:targetArrow=\"15\" cy:sourceArrowColor=\"#000000\" cy:edgeLineType=\"SOLID\"/>"
	    + "</edge>"
	    + "</graph>";

    public static String getNetworkAsString(int nr) {
	String network = null;
	if (nr == 1)
	    network = network1;
	else if (nr == 2)
	    return network = network2;
	else if (nr == 3)
	    return network = network3;
	else if (nr == 4)
	    return network = network4;
	
	return network;
    }

}
