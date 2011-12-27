package nl.nki.responsepredictor;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import nl.nki.responsepredictor.bool.EquationModel;
import nl.nki.responsepredictor.bool.InvalidEquationException;
import nl.nki.responsepredictor.xgmml.XgmmlDomParser;

import org.xml.sax.SAXException;

public class EquationModelFactory {

	public static EquationModel fromXgmmlAsString(String file)
			throws SAXException, IOException, InvalidEquationException,
			ParserConfigurationException {
		
		Network network = NetworkFactory.fromXgmmlAsString(file);
		return new EquationModel(network.equations());
	}

	public static EquationModel fromXgmmlFileSystem(String path)
			throws SAXException, IOException, InvalidEquationException,
			ParserConfigurationException {
		Network network = NetworkFactory.fromXgmmlFileSystem(path);
		return new EquationModel(network.equations());
	}

	public static EquationModel fromNetwork(Network network)
			throws InvalidEquationException {
		return new EquationModel(network.equations());
	}


}
