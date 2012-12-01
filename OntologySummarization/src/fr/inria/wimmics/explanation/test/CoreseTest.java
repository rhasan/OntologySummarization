package fr.inria.wimmics.explanation.test;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import fr.inria.acacia.corese.api.IDatatype;
import fr.inria.acacia.corese.cg.datatype.CoreseDouble;
import fr.inria.acacia.corese.cg.datatype.CoreseInteger;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgenv.result.XMLResult;
import fr.inria.edelweiss.kgram.api.core.Node;
import fr.inria.edelweiss.kgram.api.query.Result;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgram.tool.ResultsImpl;
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
		ld.load("rdf/ontology/dbpedia_3.8.owl");
		ld.load("rdf/instance.rdf");
		
		//users preference/interest class <http://dbpedia.org/ontology/Scientist>
		QueryProcess exec = QueryProcess.create(graph);
//		String query = "select ?class1 (kg:similarity(?class1, <http://dbpedia.org/ontology/Scientist>) as ?sim) " +
//				" where {" +
//				"<http://www.example.org/exampleDocument#Bob> a ?class1 ." +
//				"}" +
//				"ORDER BY DESC(?sim)";

		String query = "select ?resource ?class1" +
		" where {" +
		"?resource a ?class1 ." +
		"}";

		Mappings map = exec.query(query);
		ResultFormat f = ResultFormat.create(map);
		System.out.println(f);		

		
		
//		ResultsImpl rs = ResultsImpl.create(map);		
//		Iterator<Result> rsIt = rs.iterator();
//		while(rsIt.hasNext()) {
//			Result res = rsIt.next();
//			System.out.println("class1: "+res.getNode("?class1"));
//			CoreseDouble val = (CoreseDouble) res.getNode("?sim").getValue();
//			
//			System.out.println("sim: "+val.doubleValue());
//			//System.out.println(res.toString());
//			
//			
//		}
		
		
		

	}

}
