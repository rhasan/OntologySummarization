package fr.inria.wimmics.explanation;

import org.openrdf.model.Statement;

public class KnowledgeStatement {
	private Statement statement;
	private double degreeCentrality;
	private double similarityScore;
	private double score;
	private double reRankedScore; //this one also includes centrality score
	private double proofTreeLevelScore; //abstraction score
	private double subTreeWeight;
	
	public void setSubTreeWeight(double subTreeWeight) {
		this.subTreeWeight = subTreeWeight;
	}
	public double getSubTreeWeight() {
		return subTreeWeight;
	}
	
	public void setProofTreeLevelScore(double proofTreeLevelScore) {
		this.proofTreeLevelScore = proofTreeLevelScore;
	}
	
	public double getProofTreeLevelScore() {
		return proofTreeLevelScore;
	}
	
	
	public double getReRankedScore() {
		return reRankedScore;
	}
	public void setReRankedScore(double reRankedScore) {
		this.reRankedScore = reRankedScore;
	}
	public Statement getStatement() {
		return statement;
	}
	public void setStatement(Statement statement) {
		this.statement = statement;
	}
	public double getScore() {
		return score;
		//return degreeCentrality+similarityScore;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	public KnowledgeStatement(Statement statement) {
		
		score = 0.0;
		degreeCentrality = 0.0;
		similarityScore = 0.0;

		this.statement = statement;

	}
	
	public KnowledgeStatement() {
		statement = null;
		score = 0.0;
		degreeCentrality = 0.0;
		similarityScore = 0.0;
	}
	public double getDegreeCentrality() {
		return degreeCentrality;
	}
	public void setDegreeCentrality(double degreeCentrality) {
		this.degreeCentrality = degreeCentrality;
	}
	public double getSimilarityScore() {
		return similarityScore;
	}
	public void setSimilarityScore(double similarityScore) {
		this.similarityScore = similarityScore;
	}
	
}
