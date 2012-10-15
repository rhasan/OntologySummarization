package fr.inria.wimmics.openrdf.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;



public class SesameUtil {
	/**
	 * Initializes and returns a RDFS inference enabled memory based repository 
	 * @return an instance of a {@link Repository}
	 * @throws RepositoryException
	 */

	public static Repository getMemoryBasedRepository(boolean RDFSEnable) throws RepositoryException {
		MemoryStore memory = new MemoryStore();		
		if(RDFSEnable) {
			Sail sail = new ForwardChainingRDFSInferencer( memory ); // to enable inference
	        Repository rep = new SailRepository( sail ); // SAIL stands for Storage And Inference Layer
	        rep.initialize();
	        return rep;
		}
		Repository rep1 = new SailRepository(memory);
		rep1.initialize();
		return rep1;
	}
	public static Repository getMemoryBasedRepository() throws RepositoryException {
		MemoryStore memory = new MemoryStore();
		Repository rep1 = new SailRepository(memory);
		rep1.initialize();
		return rep1;
	}
	
	public static void printSPARQLQueryhResult(Repository repo, String queryString) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		RepositoryConnection con = repo.getConnection();
		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		
		TupleQueryResult result = tupleQuery.evaluate();
		try {
			
			int i = 0;
			while (result.hasNext()) {
				System.out.println("==== Result " + (++i) + " ====");
				BindingSet bindingSet = result.next();
				List<String> names = result.getBindingNames();
				for (String name : names) {
					System.out.println(name + ": " + bindingSet.getValue(name));
				}
				System.out.println();
			}
		} finally {
			result.close();
			con.close();
		}
	}
	
	public static void evaluateAndPrintConstructQuery(Repository repo, String queryString) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		RepositoryConnection con = repo.getConnection();
		GraphQuery graphQuery = con.prepareGraphQuery(QueryLanguage.SPARQL, queryString);
		GraphQueryResult graphResult = graphQuery.evaluate();
		
		try {
			while (graphResult.hasNext()) {
				   Statement st = graphResult.next();
				   System.out.println(st);
				   // ... do something with the resulting statement here.
				}			
		} finally {
			graphResult.close();
			con.close();
		}
		
	}
	
	public static List<Statement> evaluateConstructQuery(Repository repo, String queryString) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		RepositoryConnection con = repo.getConnection();
		GraphQuery graphQuery = con.prepareGraphQuery(QueryLanguage.SPARQL, queryString);
		GraphQueryResult graphResult = graphQuery.evaluate();
		List<Statement> stmts = new ArrayList<Statement>();
		try {
			while (graphResult.hasNext()) {
				   Statement st = graphResult.next();
				   stmts.add(st);
				   //System.out.println(st);
				   // ... do something with the resulting statement here.
				}			
		} finally {
			graphResult.close();
			con.close();
		}
		return stmts;
	}
	
	
}
