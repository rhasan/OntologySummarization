package fr.inria.wimmics.explanation;

import org.openrdf.model.Statement;

public class KnowledgeStatement {
	private Statement statement;
	private double score;
	public Statement getStatement() {
		return statement;
	}
	public void setStatement(Statement statement) {
		this.statement = statement;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public KnowledgeStatement(Statement statement, double score) {
		super();
		this.statement = statement;
		this.score = score;
	}
	
	public KnowledgeStatement() {
		statement = null;
		score = 0.0;
	}
	
}
