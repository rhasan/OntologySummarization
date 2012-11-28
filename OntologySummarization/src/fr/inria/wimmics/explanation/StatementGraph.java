package fr.inria.wimmics.explanation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import fr.inria.wimmics.openrdf.util.SesameUtil;
import fr.inria.wimmics.openrdf.util.SummarizationUtil;

public class StatementGraph {
	private List<Statement> rdfStatements;
	private Set<String> resourceURIs;
	private Repository myRepository = null;
	private Map<String,Integer> outDegree = null;
	private Map<String,Integer> inDegree = null;
	private Map<Statement,Double> centrality = null;
	
	private static StatementGraph instance = null;

	private void init() throws RepositoryException {
		resourceURIs = new HashSet<String>();
		myRepository = SesameUtil.getMemoryBasedRepository();
		outDegree = new HashMap<String, Integer>();
		inDegree = new HashMap<String, Integer>();
		centrality = new HashMap<Statement,Double>();
		
	}
	
	private StatementGraph(List<Statement> rdfStatements) throws RepositoryException {
		this.rdfStatements = rdfStatements;
		init();
		loadStatementsInRepo();
	}
	private StatementGraph() throws RepositoryException {
		init();
	}
	
	public static StatementGraph getInstance(List<Statement> rdfStatements) throws RepositoryException {
		if(instance==null) {
			instance = new StatementGraph(rdfStatements);
		}
		return instance;
	}

	public static StatementGraph getInstance() throws RepositoryException {
		if(instance==null) {
			instance = new StatementGraph();
		}
		return instance;
	}	
	
	public List<Statement> getRdfStatements() {
		return rdfStatements;
	}

	public void setRdfStatements(List<Statement> rdfStatements) {
		this.rdfStatements = rdfStatements;
	}
	
	private void loadStatementsInRepo() throws RepositoryException {
		RepositoryConnection con = myRepository.getConnection();


		try {
			con.setAutoCommit(false);

			// Add the first file
			con.add(this.rdfStatements);

			// If everything went as planned, we can commit the result
			con.commit();
		} catch (RepositoryException e) {
			// Something went wrong during the transaction, so we roll it back
			con.rollback();
		} finally {
			// Whatever happens, we want to close the connection when we are
			// done.
			con.close();
		}		
	}
	
	public List<KnowledgeStatement> computeDegreeCentrality() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		
		for(Statement st:rdfStatements) {
			resourceURIs.add(st.getSubject().toString());
			if((st.getObject() instanceof Literal)==false) {
				resourceURIs.add(st.getObject().toString());
			}
		}
	
		
		for(String resUri:resourceURIs) {

			inDegree.put(resUri, calculateIndegree(resUri)) ;
			outDegree.put(resUri, calculateOutdegree(resUri)) ;
		}
		
		List<KnowledgeStatement> kStatements = new ArrayList<KnowledgeStatement>();
		
		for(Statement st:rdfStatements) {
			double d = getInAndOutDegreeSum(st.getSubject().toString()); 
			
			
			if((st.getObject() instanceof Literal)==false) {
				d += getInAndOutDegreeSum(st.getObject().toString());
			}
			centrality.put(st, d);
			System.out.println(d+" : "+st.toString());
			KnowledgeStatement kStmt = new KnowledgeStatement(st,d);
			kStatements.add(kStmt);
		}
		
		
		return kStatements;
	}
	
	public int getInAndOutDegreeSum(String resUri) {
		return getInDegree(resUri)+getOutDegree(resUri);
	}
	
	public int getInDegree(String resUri) {
		if(inDegree.containsKey(resUri)==false) return 0;
		return inDegree.get(resUri);
	}
	
	public int getOutDegree(String resUri) {
		if(outDegree.containsKey(resUri)==false) return 0;
		return outDegree.get(resUri);
	}

	
	private int calculateIndegree(String resUri) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		RepositoryConnection con = myRepository.getConnection();
		int res = 0;
		
		String queryString = "SELECT (COUNT(?p) AS ?count) " +
				"WHERE {" +
				"?s ?p <"+resUri+"> ." +
				"}";		
		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		
		TupleQueryResult result = tupleQuery.evaluate();
		try {
			if(result.hasNext()==false) return 0;
			
			BindingSet bindingSet = result.next();
			Literal val = (Literal)bindingSet.getValue("count");
			res = val.intValue();
			//System.out.println(val.intValue());
		} finally {
			result.close();
			con.close();
		}
		return res;
	}

	private int calculateOutdegree(String resUri) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		RepositoryConnection con = myRepository.getConnection();
		int res = 0;
		
		String queryString = "SELECT (COUNT(?p) AS ?count) " +
				"WHERE {" +
				"<"+resUri+"> ?p ?o ." +
				"}";		
		TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		
		TupleQueryResult result = tupleQuery.evaluate();
		try {
			if(result.hasNext()==false) return 0;
			
			BindingSet bindingSet = result.next();
			Literal val = (Literal)bindingSet.getValue("count");
			res = val.intValue();
			//System.out.println(val.intValue());
		} finally {
			result.close();
			con.close();
		}
		return res;
	}
	
	
}
