package fr.inria.wimmics.explanation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;


import fr.inria.wimmics.util.SesameUtil;
import fr.inria.wimmics.util.SummarizationUtil;

public class StatementSummarizer {
	private List<KnowledgeStatement> statements;
	private StatementGraph statementGraph = null;
	String baseURI;
	private List<String> ontologyLocations;
	private List<String> instanceLocations;
	
	public StatementSummarizer() {
		statements = null;
	}
	public StatementSummarizer(ArrayList<KnowledgeStatement> knowledgeStatements, boolean dummy) throws RepositoryException  {
		
		this.statements = knowledgeStatements;
		
		statementGraph = new StatementGraph(this.statements);
	}
	
	public StatementSummarizer(List<Statement> stmts) throws Exception {
		statements = new ArrayList<KnowledgeStatement>();
		for(Statement stmt:stmts) {
			KnowledgeStatement kStatement  = new KnowledgeStatement(stmt);
			statements.add(kStatement);
		}
		statementGraph = new StatementGraph(statements);
	}
	
	
	public List<KnowledgeStatement> getKnowledgeStatements() {
		return statements;
	}
	public void setKnowledgeStatements(List<KnowledgeStatement> statements) {
		this.statements = statements;
	}
	
	/**
	 * Reads RDF statements from a file and sets the list of KnowledgeStatments with score 0.0 for each statement
	 * 
	 * @param filePath path of the RDF file to read
	 * @param baseURI base URI of the RDF file
	 * @param dataFormat {@link RDFFormat} of the file
	 * @return a {@link List} of {@link KnowledgeStatement}
	 * @throws RDFParseException
	 * @throws RDFHandlerException
	 * @throws IOException
	 */
	
	public List<KnowledgeStatement> readStatements(String filePath,  String baseURI, RDFFormat dataFormat) throws RDFParseException, RDFHandlerException, IOException {
		if(statements==null) {
			statements = new ArrayList<KnowledgeStatement>();
		}
		else {
			statements.clear();
		}
		this.baseURI = baseURI;
		InputStream inputStream = new FileInputStream(filePath);
		ArrayList<Statement> myList = new ArrayList<Statement>();
		
		RDFParser rdfParser = Rio.createParser(dataFormat);
		StatementCollector collector = new StatementCollector(myList);
		rdfParser.setRDFHandler(collector);
		rdfParser.parse(inputStream, baseURI);
		
		for(Statement st:myList) {
			KnowledgeStatement kst = new KnowledgeStatement(st);
			statements.add(kst);
		}
		return statements;
	}

	class MyComparatorDescending implements Comparator<KnowledgeStatement> {

		@Override
		public int compare(KnowledgeStatement o1, KnowledgeStatement o2) {
			
			if(o1.getScore()>o2.getScore()) return -1;
			else if(o1.getScore()<o2.getScore()) return 1;
			return 0;
		}
		
	}
	public List<KnowledgeStatement> summarize() throws Exception {
		return summarize(null,null,null);
		
	}
	
	public List<KnowledgeStatement> summarize(  List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		List<KnowledgeStatement> kStatements = null;
		
	
		kStatements = statementGraph.computeDegreeCentrality();
			
		SummarizationUtil.normalizeDegreeCentrality(kStatements);
		
		//SummarizationUtil.normalizeDegreeKnowledgeStatements(kStatements);
		
		//similarity measure
		if(prefs!=null && ontologyLocations!=null && instanceLocations!=null) {
			statementGraph.computeSimilarity(prefs, ontologyLocations, instanceLocations);
			//it's already normalized.. value is between 0 to 1
			statementGraph.computeScoreSim();
		}
		else {
			statementGraph.computeScoreDeg();
		}
		
		
		
		
		//SummarizationUtil.normalizeScores(kStatements);
		MyComparatorDescending myCmpD = new MyComparatorDescending();
		Collections.sort(kStatements,myCmpD);		
		
//		for(KnowledgeStatement st:statements) {
//			System.out.println("Statement: "+ st.getStatement().toString());
//			System.out.println("Score:"+st.getScore());
//		}
//		
		return kStatements;
	}
	
	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}
	public String getBaseURI() {
		return baseURI;
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
