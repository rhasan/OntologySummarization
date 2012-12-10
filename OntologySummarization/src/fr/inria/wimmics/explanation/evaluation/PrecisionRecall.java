package fr.inria.wimmics.explanation.evaluation;

public class PrecisionRecall {
	private double precision;
	private double recall;
	public double getPrecision() {
		return precision;
	}
	public void setPrecision(double precision) {
		this.precision = precision;
	}
	public double getRecall() {
		return recall;
	}
	public void setRecall(double recall) {
		this.recall = recall;
	}
	public PrecisionRecall(double precision, double recall) {

		this.precision = precision;
		this.recall = recall;
	}
	

}
