package fr.inria.wimmics.explanation.evaluation.test;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;

public class DCGSurveyTest {
	//files/evaluation/dcg/survey/inf1/results-survey31573.csv
	

	
	@Test
	public void testCSVInput() throws IOException {
		System.out.println("Hello there!");
		DCGSurveyEntry en = new DCGSurveyEntry();
		en.setValues("files/evaluation/dcg/survey/inf1/results-survey31573.csv");
		en.printValues();
	}
}
