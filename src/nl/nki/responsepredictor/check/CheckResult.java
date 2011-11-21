package nl.nki.responsepredictor.check;

import nl.nki.responsepredictor.Matrix;

public class CheckResult {
    Matrix matrix;
    double score;
    
    public Matrix getMatrix() {
        return matrix;
    }
    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }
    public double getScore() {
        return score;
    }
    public void setScore(double score) {
        this.score = score;
    }
    public CheckResult(Matrix matrix, double score) {
	super();
	this.matrix = matrix;
	this.score = score;
    }
    

}
