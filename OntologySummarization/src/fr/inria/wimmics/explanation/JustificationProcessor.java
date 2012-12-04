package fr.inria.wimmics.explanation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.openrdf.rio.trig.TriGParser;

import fr.inria.edelweiss.kgraph.logic.RDFS;
import fr.inria.wimmics.util.GeoNames;
import fr.inria.wimmics.util.MyMultiMap;
import fr.inria.wimmics.util.SesameUtil;

public class JustificationProcessor {
	
	GenericTree<Statement> g = new GenericTree<Statement>();
	//Map<String,ArrayList<Statement>> stmtMap = new HashMap<String,ArrayList<Statement>>();
	
	MyMultiMap<String,Statement> stmtMap = new MyMultiMap<String,Statement>();
	
	Map<String,String> resourceLabels = new HashMap<String,String>();
	

	Map<String,String> nameSpaces = null;
	private List<String> ontologyLocations;
	private List<String> instanceLocations;
	private Repository myRepository = null;
	List<Statement> rdfStatements = null;
	
	
	
	public void parseJustificationFile(String filePath,String baseURI) throws RDFParseException, RDFHandlerException, IOException, RepositoryException {
		
		//linked data read by dereferencing
		//java.net.URL documentUrl = new URL(“http://example.org/example.ttl”);
		//InputStream inputStream = documentUrl.openStream();
		
		//local file read
		InputStream inputStream = new FileInputStream(filePath);
		RDFParser rdfParser = new TriGParser();
		
		ArrayList<Statement> myList = new ArrayList<Statement>();
		StatementCollector collector = new StatementCollector(myList);
		rdfParser.setRDFHandler(collector);
		rdfParser.parse(inputStream, baseURI);
		nameSpaces = collector.getNamespaces();
		
		for(Statement st:myList) {
			String stKey = st.getContext().stringValue();
			stmtMap.put(stKey, st);

		}
		
		for(Statement st:myList) {
			
			if(st.getPredicate().toString().equals(Ratio4TA.derivedFrom)) {
				String keyA = st.getSubject().stringValue();
				String keyB = st.getObject().stringValue();
				
				Statement a = stmtMap.getFirst(keyA);
				Statement b = stmtMap.getFirst(keyB);
				
				g.insterEdge(a, b);
				
			}
						
		}
		
		rdfStatements = myList;
		loadStatementsInRepo();
	}
	
	private void loadStatementsInRepo() throws RepositoryException {
		myRepository = SesameUtil.getMemoryBasedRepository();
		
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
	
	public void loadResourceLabelFromOntology(String ontologyLocation,String baseURI) throws RDFParseException, RDFHandlerException, IOException {
		
		InputStream inputStream = new FileInputStream(ontologyLocation);
		RDFParser rdfParser = new RDFXMLParser();
		
		ArrayList<Statement> myList = new ArrayList<Statement>();
		StatementCollector collector = new StatementCollector(myList);
		rdfParser.setRDFHandler(collector);
		rdfParser.parse(inputStream, baseURI);
		
		for(Statement st:myList) {
			//System.out.println(st.toString());
			String predicateUri = st.getPredicate().stringValue().trim();
			//System.out.println(predicateUri.equals(GeoNames.name));
			//System.out.println("["+predicateUri+"]="+"["+GeoNames.name+"]");
			if(predicateUri.equals(RDFS.LABEL)
					|| predicateUri.equals(GeoNames.name)) {
				String key = st.getSubject().stringValue();
				String value = st.getObject().stringValue();
				resourceLabels.put(key, value);
				//System.out.println(key+":"+value);
			}
		}
		
	}
		
	/**
	 * summarizes the parsed justification file and returns the result in JSON
	 * the javascript program takes this jason and outputs the proof tree
	 * @param rootURL
	 * the root node of the proof tree from which the summarization should start
	 */
	
	public String summarizedProofTree(String rootURL) {
		g.countSubtrees();

		Statement rootStmt = stmtMap.getFirst(rootURL);
		
		GenericTreeNode<Statement> root = g.getNodeByObject(rootStmt);
		JSONTreeGenrator<Statement> json = new JSONTreeGenrator<Statement>(nameSpaces, resourceLabels);
		String jsonString = json.generateJASON(g, root);
		//to-do: file write/method return
		System.out.println(jsonString);	
		return jsonString;
	}
	
	
	public List<KnowledgeStatement> summarizeJustificationKnowledgeStatements(String statementURI, List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		
		Set<Entry<String, ArrayList<Statement>>> entries = stmtMap.entrySet();
		Set<Statement> stmts = new HashSet<Statement>();
		
		for(Entry<String, ArrayList<Statement>> entry:entries) {
			
			ArrayList<Statement> entryStatements = entry.getValue();
			for(Statement entryStmt:entryStatements) {
				if(entryStmt.getPredicate().toString().equals(Ratio4TA.derivedFrom)) {
					//stmts.add(entryStmt);
					String key1 = entryStmt.getSubject().toString();
					String key2 = entryStmt.getObject().toString();
					Statement st1 = stmtMap.getFirst(key1);
					Statement st2 = stmtMap.getFirst(key2);
					if(st1.getContext().toString().equals(statementURI)==false)
						stmts.add(st1);
					if(st2.getContext().toString().equals(statementURI)==false)
						stmts.add(st2);
				}
			}
		}
		
		ArrayList<Statement> statements = new ArrayList<Statement>(stmts);
		StatementSummarizer summerizer = new StatementSummarizer(statements);
		List<KnowledgeStatement> kStatements = summerizer.summarize(prefs,ontologyLocations,instanceLocations);
		return kStatements;

	}
	
	public List<KnowledgeStatement> summarizeJustificationKnowledgeStatementsRerank(String statementURI, List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {

		List<KnowledgeStatement> kStatements = summarizeJustificationKnowledgeStatements(statementURI, prefs, ontologyLocations, instanceLocations);
		List<KnowledgeStatement> kStatementsReRanked = reRank(statementURI, kStatements);
		Collections.sort(kStatementsReRanked, new Comparator<KnowledgeStatement>() {

			@Override
			public int compare(KnowledgeStatement o1, KnowledgeStatement o2) {
				if(o1.getReRankedScore()>o2.getReRankedScore()) return -1;
				if(o1.getReRankedScore()<o2.getReRankedScore()) return 1;
				return 0;
			}
		});
		
		System.out.println("After reRanking:");
		for(KnowledgeStatement kst:kStatementsReRanked) {
			System.out.println("Statement:"+kst.getStatement().toString());
			System.out.println("ReRanked Score:"+kst.getReRankedScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+index++);
		}
		return kStatementsReRanked;

	}

	
	public int oneStepProofTreeLink(List<KnowledgeStatement> S) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		RepositoryConnection con = myRepository.getConnection();
		Set<String> summaryUris = new HashSet<String>();
		
		for(KnowledgeStatement st:S) {
			String stUri = st.getStatement().getContext().stringValue();
			summaryUris.add(stUri);
		}
		int count = 0;
		
		for(KnowledgeStatement st:S) {	
			
			String stUri = st.getStatement().getContext().stringValue();
			String queryString = "PREFIX  r4ta:  <http://ns.inria.fr/ratio4ta/v2#> " +
					"SELECT ?o " +
					"WHERE {" +
					"<"+stUri+"> r4ta:derivedFrom ?o ." +
					"}";

			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			
			TupleQueryResult result = tupleQuery.evaluate();
			
			try {
				while(result.hasNext()) {
					
					String foundUri = result.next().getValue("o").stringValue();
					if(summaryUris.contains(foundUri))
						count++;
					
				}
			} finally {
				result.close();

			}

			
			
			String queryString1 = "PREFIX  r4ta:  <http://ns.inria.fr/ratio4ta/v2#> " +
					"SELECT ?s " +
					"WHERE {" +
					"?s r4ta:derivedFrom <"+stUri+"> ." +
					"}";

			TupleQuery tupleQuery1 = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString1);
			
			TupleQueryResult result1 = tupleQuery1.evaluate();
			
			try {
				while(result1.hasNext()) {
					String foundUri = result1.next().getValue("s").stringValue();
					if(summaryUris.contains(foundUri))
						count++;

				}
			} finally {
				result1.close();

			}
					
		
		}

		return count;
	}
	public double reward(KnowledgeStatement st, List<KnowledgeStatement> S) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		
		 List<KnowledgeStatement> newS = new ArrayList<KnowledgeStatement>(S);
		 newS.add(st);
		 
		 int one_step_pt_links = oneStepProofTreeLink(S);
		 int one_step_pt_links_st = oneStepProofTreeLink(newS);
		 if(one_step_pt_links_st==0)
			 return 0.0;
		 
		 double r = 1.0 - (double) one_step_pt_links/ (double) one_step_pt_links_st;
		
		return r;
	}
	
	List<KnowledgeStatement> reRank(String statementURI, List<KnowledgeStatement> kStatements) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		
		Statement rootRdfStmt = stmtMap.getFirst(statementURI);
		KnowledgeStatement kRootStmt = new KnowledgeStatement(rootRdfStmt);
		
		//adding a root statement
		List<KnowledgeStatement> S = new ArrayList<KnowledgeStatement>();
		S.add(kRootStmt);
		
		int i=0;
		while(i<kStatements.size()) {
			
			double maxScore = Double.NEGATIVE_INFINITY;
			int iMax = -1;
			for(int index=0;index<kStatements.size();index++) {
				
				KnowledgeStatement kstmt = kStatements.get(index);
				if(S.contains(kstmt)==false) {
					double sc = kstmt.getScore();
					double rsc = reward(kstmt, S);
					double rScore = (.6 * sc+ .4 * rsc);
					//double rScore = sc;
					if(maxScore<rScore) {
						iMax = index;
						maxScore = rScore;
					}
				}
			}
			
			kStatements.get(iMax).setReRankedScore(maxScore);
			S.add(kStatements.get(iMax));
			
			//last statement
			i++;
		}
		//removing the root statement
		S.remove(0);
		return S;
	}
	public List<KnowledgeStatement>  summarizeJustificationKnowledgeStatements(String statementURI) throws Exception {
		return summarizeJustificationKnowledgeStatements( statementURI,  null, null, null);
	}
	public List<KnowledgeStatement>  summarizeRelevantJustificationKnowledgeStatements(String statementURI, List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		return summarizeJustificationKnowledgeStatements( statementURI,  prefs, ontologyLocations, instanceLocations);
	}
	public List<String> getOntologyLocations() {
		return ontologyLocations;
	}
	public void setOntologyLocations(List<String> ontologyLocations) {
		this.ontologyLocations = ontologyLocations;
	}
	public List<String> getInstanceLocations() {
		return instanceLocations;
	}
	public void setInstanceLocations(List<String> instanceLocations) {
		this.instanceLocations = instanceLocations;
	}	
	public MyMultiMap<String, Statement> getStmtMap() {
		return stmtMap;
	}

	public void setStmtMap(MyMultiMap<String, Statement> stmtMap) {
		this.stmtMap = stmtMap;
	}
}
