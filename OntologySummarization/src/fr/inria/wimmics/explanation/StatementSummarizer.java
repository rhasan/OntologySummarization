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


import fr.inria.wimmics.openrdf.util.SesameUtil;
import fr.inria.wimmics.openrdf.util.SummarizationUtil;

public class StatementSummarizer {
	private List<KnowledgeStatement> statements;
	private StatementGraph statementGraph = null;
	String baseURI;
	
	public StatementSummarizer() {
		statements = null;
	}
	public StatementSummarizer(List<KnowledgeStatement> knowledgeStatements) throws RepositoryException  {
		this.statements = knowledgeStatements;
		
		statementGraph = StatementGraph.getInstance(SummarizationUtil.getRDFStatementList(this.statements));
	}
	
	public StatementSummarizer(List<Statement> stmts,double initialScore) throws Exception {
		statements = new ArrayList<KnowledgeStatement>();
		for(Statement stmt:stmts) {
			KnowledgeStatement kStatement  = new KnowledgeStatement(stmt, initialScore);
			statements.add(kStatement);
		}
		statementGraph = StatementGraph.getInstance(SummarizationUtil.getRDFStatementList(this.statements));
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
			KnowledgeStatement kst = new KnowledgeStatement(st,0.0);
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
		List<KnowledgeStatement> kStatements = null;
		kStatements = statementGraph.computeDegreeCentrality();
		
		//to-do: re-ranking
		//add similarity measure
		
		SummarizationUtil.normalizeKnowledgeStatements(kStatements);
		MyComparatorDescending myCmpD = new MyComparatorDescending();
		Collections.sort(kStatements,myCmpD);		
		
		for(KnowledgeStatement st:kStatements) {
			System.out.println("Statement: "+ st.getStatement().toString());
			System.out.println("Score:"+st.getScore());
		}
		
		return kStatements;
	}
	
	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}
	public String getBaseURI() {
		return baseURI;
	}	

}
