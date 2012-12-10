package fr.inria.wimmics.explanation.evaluation.test;

public class Measures {
	private double fMeasure;
	private double compressionRatio;
	private double threshold;
	private double precision;
	private double recall;
	
	public Measures() {

	}
	public Measures(double fMeasure, double compressionRatio, double threshold,
			double precision, double recall) {
		super();
		this.fMeasure = fMeasure;
		this.compressionRatio = compressionRatio;
		this.threshold = threshold;
		this.precision = precision;
		this.recall = recall;
	}
	public void setRecall(double recall) {
		this.recall = recall;
	}
	public void setPrecision(double precision) {
		this.precision = precision;
	}
	public double getRecall() {
		return recall;
	}
	public double getPrecision() {
		return precision;
	}
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	public double getfMeasure() {
		return fMeasure;
	}
	public void setfMeasure(double fMeasure) {
		this.fMeasure = fMeasure;
	}
	public double getCompressionRatio() {
		return compressionRatio;
	}
	public void setCompressionRatio(double compressionRatio) {
		this.compressionRatio = compressionRatio;
	}
	
}
