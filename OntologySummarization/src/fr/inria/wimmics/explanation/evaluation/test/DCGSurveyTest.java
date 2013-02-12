package fr.inria.wimmics.explanation.evaluation.test;



import java.io.IOException;

import org.junit.Test;


public class DCGSurveyTest {
	//files/evaluation/dcg/survey/inf1/results-survey31573.csv
	

	
	@Test
	public void testCSVInput() throws IOException {
		System.out.println("Hello there!");
		DCGSurveyEntryProcessor en = new DCGSurveyEntryProcessor();
		en.setValues("files/evaluation/dcg/survey/inf1/results-survey31573.csv");
		en.printValues();
	}
}
