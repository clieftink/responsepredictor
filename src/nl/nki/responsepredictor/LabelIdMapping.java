package nl.nki.responsepredictor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import nl.nki.responsepredictor.bool.InvalidEquationException;

import org.xml.sax.SAXException;

public class LabelIdMapping {

	/**
	 * @param args
	 * @throws ParserConfigurationException
	 * @throws InvalidEquationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static void main(String[] args) throws SAXException, IOException,
			InvalidEquationException, ParserConfigurationException {

		String file = "/home/cor/ws_csbc/csbc/network/csbc_prior.xgmml";
		NetworkFactory nf = new NetworkFactory();
		Network n = nf.fromXgmmlFileSystem(file);

		HashMap<String, String> idLabels = n.nodeLabels();

		Iterator<String> it = idLabels.keySet().iterator();

		FileWriter fstream = new FileWriter("/tmp/idLabels.tsv");
		BufferedWriter out = new BufferedWriter(fstream);

		while (it.hasNext()) {
			String id = (String) it.next();
			String label = (String) idLabels.get(id);
			out.write("\"" + id + "\"\t\"" + label + "\"" + "\n");

		}
		
		
		// Close the output stream
		out.close();

	}
}
