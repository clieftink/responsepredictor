package nl.nki.responsepredictor.utils;

public class QualityMetrics {

    /*
     * #Measure of fitness as used by Gaudet 2005. #
     * "A compendium of signals and Responses triggers by Prodeath and Prosurvival Cytokines"
     * . # Molecular & Cellular Proteomics.
     */
    public double fitness(double[] predicted, double[] observed) {


	//TODO build in check predicted.length == observed.length
	
	double n = 0;
	for (int i = 0; i < predicted.length; i++) {
	    n += Math.pow(predicted[i] - observed[i], 2);
	}

	double p2sum = 0;
	double psum = 0;
	for (int i = 0; i < predicted.length; i++) {
	    p2sum += Math.pow(predicted[i],2);
	    psum += predicted[i];
	}	
	double d = p2sum - (Math.pow(psum,2)/predicted.length);
	
	return 1-(n/d);
    }

}
