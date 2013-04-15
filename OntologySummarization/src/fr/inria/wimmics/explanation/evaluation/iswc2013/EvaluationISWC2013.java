package fr.inria.wimmics.explanation.evaluation.iswc2013;

import java.io.IOException;

public class EvaluationISWC2013 {

	public EvaluationTestCase getTestCase1() {
		EvaluationTestCase etc = new EvaluationTestCase();
		etc.setQuestion1Name("q1");
		etc.setQuestion2Name("q2");

		etc.setRatingFilePath("files/evaluation/dcg/survey/inf1/results-survey31573.csv");
		etc.setRootStatement("http://alphubel.unice.fr:8080/lodutil/data/d21");
		
		etc.setJustificationFilePath("rdf/inference1.trig");
		
		etc.setInstanceFilePath("rdf/inference1-instance.rdf");
		etc.setSimilarityConcept("http://dbpedia.org/ontology/Event");
		etc.setDbpediaSchemaLocation("rdf/ontology/dbpedia_3.8.owl");
		etc.setGeonamesSchemaLocation("rdf/ontology/geonames_ontology_v3.1.rdf");
		
		return etc;
	}
	
	public static void main(String[] args) throws Exception {
		
		EvaluationISWC2013 iswc2013 = new EvaluationISWC2013();
		EvaluationTestCase etc1 = iswc2013.getTestCase1();
		
		EvaluationTestCaseResult etcResult1 = etc1.evaluate();
		
		
	}
}
