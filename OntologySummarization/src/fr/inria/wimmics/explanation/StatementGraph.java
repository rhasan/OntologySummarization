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

import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.wimmics.exception.NoTypeFoundInTheOntology;
import fr.inria.wimmics.util.SesameUtil;
import fr.inria.wimmics.util.SummarizationUtil;

public class StatementGraph {
	private List<Statement> rdfStatements;
	private List<KnowledgeStatement> knowledgeStatements;
	private Set<String> resourceURIs;
	private Repository myRepository = null;
	private Map<String,Integer> outDegree = null;
	private Map<String,Integer> inDegree = null;
	private Map<Statement,Double> centrality = null;
	private Map<Statement,KnowledgeStatement> stmtKstmtMap = null;
	
	private static StatementGraph instance = null;

	private void init() throws RepositoryException {
		resourceURIs = new HashSet<String>();
		myRepository = SesameUtil.getMemoryBasedRepository();
		outDegree = new HashMap<String, Integer>();
		inDegree = new HashMap<String, Integer>();
		centrality = new HashMap<Statement,Double>();
		stmtKstmtMap = new HashMap<Statement,KnowledgeStatement>();
		
		for(KnowledgeStatement kst:knowledgeStatements) {
			stmtKstmtMap.put(kst.getStatement(), kst);
		}
		
		
		
		
	}
	
	public StatementGraph(List<KnowledgeStatement> kStatements) throws RepositoryException {
		knowledgeStatements = kStatements;
		this.rdfStatements = SummarizationUtil.getRDFStatementList(kStatements);
		init();
		loadStatementsInRepo();
	}
	
//	public static StatementGraph getInstance(List<KnowledgeStatement> kStatements) throws RepositoryException {
//		//if(instance==null) {
//			instance = new StatementGraph(kStatements);
//		//}
//		return instance;
//	}


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
		
		//List<KnowledgeStatement> kStatements = new ArrayList<KnowledgeStatement>();
		
		for(KnowledgeStatement kst:knowledgeStatements) {
			Statement st = kst.getStatement();
			double d = getInAndOutDegreeSum(st.getSubject().toString()); 
			
			
			if((st.getObject() instanceof Literal)==false) {
				d += getInAndOutDegreeSum(st.getObject().toString());
			}
			centrality.put(st, d);
			//System.out.println(d+" : "+st.toString());
			//KnowledgeStatement kStmt = new KnowledgeStatement(st,d);
			//KnowledgeStatement kStmt = stmtKstmtMap.get(st);
			kst.setDegreeCentrality(d);
			//System.out.println(kst.getDegreeCentrality()+" : "+st.toString());
			//kStmt.setScore(d)
			//kStatements.add(kStmt);
		}
		
		
		return knowledgeStatements;
	}
	
	public List<KnowledgeStatement> computeSimilarity(List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws EngineException {

		
		SimilarityCalculator simCal = new SimilarityCalculator();
		simCal.initialize(prefs, ontologyLocations, instanceLocations);
		
		for(KnowledgeStatement st:knowledgeStatements) {
			//System.out.println("Statement: "+ st.getStatement().toString());
			//System.out.println("Subject:"+st.getStatement().getSubject().stringValue());
			double statementSimilarity = simCal.statementSimilarityScore(st.getStatement().getSubject().stringValue(),st.getStatement().getPredicate().stringValue() , st.getStatement().getObject().stringValue());
			st.setSimilarityScore(statementSimilarity);
			
//			System.out.println("Similarity Score:"+st.getSimilarityScore());
//			System.out.println("Score:"+st.getScore());

		}		
		return knowledgeStatements;
	}
	public  List<KnowledgeStatement> computeScoreSim() {
		for(KnowledgeStatement st:knowledgeStatements) {
			st.setScore(0.5 * st.getDegreeCentrality()+0.5 * st.getSimilarityScore());

		}		
		
		return knowledgeStatements;
	}
	public  List<KnowledgeStatement> computeScoreDeg() {
		for(KnowledgeStatement st:knowledgeStatements) {
			st.setScore(st.getDegreeCentrality());
			//System.out.println("Setting degree:"+st.getDegreeCentrality());

		}		
		
		return knowledgeStatements;
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
			//System.out.println("count:"+val.intValue());
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
