package fr.inria.wimmics.explanation.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.wimmics.exception.NoTypeFoundInTheOntology;
import fr.inria.wimmics.explanation.SimilarityCalculator;

public class SimilarityCalculatorTest {

	@Test
	public void test() throws EngineException, NoTypeFoundInTheOntology {
		//fail("Not yet implemented");
		SimilarityCalculator sc = new SimilarityCalculator();
		
		List<String> prefs = new ArrayList<String>();
		prefs.add("http://dbpedia.org/ontology/Scientist");
		prefs.add("http://dbpedia.org/ontology/Country");
		List<String> ontologyLocations = new ArrayList<String>();
		List<String> instanceLocations = new ArrayList<String>();
		ontologyLocations.add("rdf/ontology/dbpedia_3.8.owl");
		instanceLocations.add("rdf/instance.rdf");	
		sc.initialize(prefs, ontologyLocations, instanceLocations);
		System.out.println(sc.similarity("http://dbpedia.org/ontology/Country", "http://dbpedia.org/ontology/City"));
		System.out.println(sc.similarity("http://dbpedia.org/ontology/City", "http://dbpedia.org/ontology/Country"));
		System.out.println(sc.resourceSimilarityScore("http://www.example.org/exampleDocument#Bob"));
		System.out.println(sc.statementSimilarityScore("http://www.example.org/exampleDocument#London",
								"http://www.w3.org/1999/02/22-rdf-syntax-ns#type", 
								"http://dbpedia.org/ontology/City"));
		
	}

}
