package nl.nki.responsepredictor;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import nl.nki.responsepredictor.bool.EquationModel;
import nl.nki.responsepredictor.bool.InvalidEquationException;
import nl.nki.responsepredictor.xgmml.XgmmlDomParser;

public class NetworkFactory {

	public static Network fromXgmmlAsString(String file)
			throws SAXException, IOException, InvalidEquationException,
			ParserConfigurationException {
		XgmmlDomParser parser = new XgmmlDomParser();
		Network network = parser.network(file, true);
		return network;
	}

	public static Network fromXgmmlFileSystem(String path)
			throws SAXException, IOException, InvalidEquationException,
			ParserConfigurationException {
		XgmmlDomParser parser = new XgmmlDomParser();
		Network network = parser.network(path, false);
		return network;
	}
}
