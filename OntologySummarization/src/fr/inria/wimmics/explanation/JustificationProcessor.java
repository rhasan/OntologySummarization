package fr.inria.wimmics.explanation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.openrdf.model.Statement;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.rio.trig.TriGParser;

import fr.inria.wimmics.openrdf.util.MyMultiMap;

public class JustificationProcessor {
	
	GenericTree<Statement> g = new GenericTree<Statement>();
	//Map<String,ArrayList<Statement>> stmtMap = new HashMap<String,ArrayList<Statement>>();
	
	MyMultiMap<String,Statement> stmtMap = new MyMultiMap<String,Statement>();
	
	Map<String,String> nameSpaces = null;
	private List<String> ontologyLocations;
	private List<String> instanceLocations;
	
	
	
	public void parseJustificationFile(String filePath,String baseURI) throws RDFParseException, RDFHandlerException, IOException {
		
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
		JSONTreeGenrator<Statement> json = new JSONTreeGenrator<Statement>(nameSpaces);
		String jsonString = json.generateJASON(g, root);
		//to-do: file write/method return
		System.out.println(jsonString);	
		return jsonString;
	}
	
	
	public void summarizeJustificationKnowledgeStatements(String statementURI, List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		
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
		summerizer.summarize(prefs,ontologyLocations,instanceLocations);
		
	}
	public void summarizeJustificationKnowledgeStatements(String statementURI) throws Exception {
		summarizeJustificationKnowledgeStatements( statementURI,  null, null, null);
	}
	public void summarizeRelevantJustificationKnowledgeStatements(String statementURI, List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		summarizeJustificationKnowledgeStatements( statementURI,  prefs, ontologyLocations, instanceLocations);
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

}
