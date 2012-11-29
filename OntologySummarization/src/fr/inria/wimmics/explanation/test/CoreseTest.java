package fr.inria.wimmics.explanation.test;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.load.Load;
import fr.inria.edelweiss.kgtool.print.ResultFormat;

public class CoreseTest {

	@Test
	public void test() throws EngineException {
		Graph graph = Graph.create();
		
		Load ld = Load.create(graph);
		//ld.load("rdf/summary.rdf");
		ld.load("/user/hrakebul/home/Documents/sw/ontology-summary/dbpedia/dbpedia_3.8.owl");
		ld.load("rdf/instance.rdf");
		
		//users preference/interest class <http://dbpedia.org/ontology/Scientist>
		QueryProcess exec = QueryProcess.create(graph);
		String query = "select ?class1 (kg:similarity(?class1, <http://dbpedia.org/ontology/Scientist>) as ?sim) " +
				" where {" +
				"<http://www.example.org/exampleDocument#Bob> a ?class1 ." +
				"}" +
				"ORDER BY DESC(?sim)";
		Mappings map = exec.query(query);
		ResultFormat f = ResultFormat.create(map);
		System.out.println(f);		
		

	}

}
