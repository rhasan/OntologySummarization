package fr.inria.wimmics.explanation.model;

public class RDFSentenceWithRank {
	private GenericRDFSentence sentence;
	private double Ci;
	private double normalizedCi;
	private double reRankScore;
	
	
	public double getReRankScore() {
		return reRankScore;
	}
	public void setReRankScore(double reRankScore) {
		this.reRankScore = reRankScore;
	}
	public GenericRDFSentence getSentence() {
		return sentence;
	}
	public void setSentence(GenericRDFSentence sentence) {
		this.sentence = sentence;
	}
	public double getCi() {
		return Ci;
	}
	public void setCi(double ci) {
		Ci = ci;
	}
	public double getNormalizedCi() {
		return normalizedCi;
	}
	public void setNormalizedCi(double normalizedCi) {
		this.normalizedCi = normalizedCi;
	}
	
	

}
