package fr.inria.wimmics.explanation.evaluation.iswc2013;

import java.io.IOException;

public class EvaluationTestCase {

	String ratingFilePath;
	String question1Name;
	String question2Name;
	
	String justificationFilePath;
	String instanceFilePath;
	String rootStatement;
	String similarityConcept;
	
	String dbpediaSchemaLocation;
	String geonamesSchemaLocation;
	double sgNavigationalP;
	
	TestCaseEvaluator evaluator;
	EvaluationTestCaseResult result=null;
	
	/**
	 * set all the variables before calling this
	 * @throws Exception
	 */
	public EvaluationTestCaseResult evaluate() throws Exception {
		evaluator = new TestCaseEvaluator(this);
		result = evaluator.evaluate();
		return result;
	}
	
	public void printProfiles() {
		evaluator.printParticipantProfiles();
	}
	
	public EvaluationTestCaseResult evaluateTest() throws Exception {
		evaluator = new TestCaseEvaluator(this);
		result = evaluator.evaluateTest();
		return result;
	}	
	
	public EvaluationTestCaseResult getResult() throws Exception {
		if(result==null) {
			throw new Exception("test case not evaluated");
		}
		return result;
	}
	
	public String getRatingFilePath() {
		return ratingFilePath;
	}
	public void setRatingFilePath(String ratingFilePath) {
		this.ratingFilePath = ratingFilePath;
	}
	public String getQuestion1Name() {
		return question1Name;
	}
	public void setQuestion1Name(String question1Name) {
		this.question1Name = question1Name;
	}
	public String getQuestion2Name() {
		return question2Name;
	}
	public void setQuestion2Name(String question2Name) {
		this.question2Name = question2Name;
	}
	public String getJustificationFilePath() {
		return justificationFilePath;
	}
	public void setJustificationFilePath(String justificationFilePath) {
		this.justificationFilePath = justificationFilePath;
	}
	public String getInstanceFilePath() {
		return instanceFilePath;
	}
	public void setInstanceFilePath(String instanceFilePath) {
		this.instanceFilePath = instanceFilePath;
	}
	public String getRootStatement() {
		return rootStatement;
	}
	public void setRootStatement(String rootStatement) {
		this.rootStatement = rootStatement;
	}
	public String getSimilarityConcept() {
		return similarityConcept;
	}
	public void setSimilarityConcept(String similarityConcept) {
		this.similarityConcept = similarityConcept;
	}
	public String getDbpediaSchemaLocation() {
		return dbpediaSchemaLocation;
	}
	public void setDbpediaSchemaLocation(String dbpediaSchemaLocation) {
		this.dbpediaSchemaLocation = dbpediaSchemaLocation;
	}
	public String getGeonamesSchemaLocation() {
		return geonamesSchemaLocation;
	}
	public void setGeonamesSchemaLocation(String geonamesSchemaLocation) {
		this.geonamesSchemaLocation = geonamesSchemaLocation;
	}
	public double getSgNavigationalP() {
		return sgNavigationalP;
	}
	public void setSgNavigationalP(double sgNavigationalP) {
		this.sgNavigationalP = sgNavigationalP;
	}
	
	
	
}
