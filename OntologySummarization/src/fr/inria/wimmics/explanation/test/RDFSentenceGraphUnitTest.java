package fr.inria.wimmics.explanation.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import fr.inria.wimmics.explanation.RDFSentenceGraph;
import fr.inria.wimmics.explanation.SentenceGraphSummarizer;
import fr.inria.wimmics.explanation.GenericRDFSentence;
import fr.inria.wimmics.openrdf.util.SesameUtil;

public class RDFSentenceGraphUnitTest {

	@Test
	public void test() throws RepositoryException, RDFParseException, IOException, MalformedQueryException, QueryEvaluationException, RDFHandlerException {
		//fail("Not yet implemented");
		String filePath = "rdf/animal.nt";
		String baseURI = "http://www.example.com/";
		Repository myRepository = SesameUtil.getMemoryBasedRepository();
		
		RepositoryConnection con = myRepository.getConnection();
		
		File inputFile1 = new File(filePath);

		
		try {
			   con.setAutoCommit(false);

			   // Add the first file
			   con.add(inputFile1, baseURI, RDFFormat.NTRIPLES);

			   // If everything went as planned, we can commit the result
			   con.commit();
			}
			catch (RepositoryException e) {
			   // Something went wrong during the transaction, so we roll it back
			   con.rollback();
			}
			finally {
			   // Whatever happens, we want to close the connection when we are done.
			   con.close();
			}
		
		
		//SesameUtil.printSPARQLQueryhResult(myRepository, "select * where {?s ?p ?o}");
		List<Statement> stmts = SesameUtil.evaluateConstructQuery(myRepository, "CONSTRUCT {?s ?p ?o} where {?s ?p ?o.}");
		Set<Statement> statementsSet = new HashSet<Statement>(stmts);
		//Set<Statement> statementsSet = SesameUtil.evaluateConstructQueryReturnSet(myRepository, "CONSTRUCT {?s ?p ?o} where {?s ?p ?o.}");
		
		RDFSentenceGraph rdfSentenceGraph = new RDFSentenceGraph(statementsSet, 0.5);
		

		SentenceGraphSummarizer summmarizer = new SentenceGraphSummarizer(rdfSentenceGraph);

		//summmarizer.summarizeByCentralityThreshold(.1);
		//summmarizer.summarizeBySizeWOreRank(.20);
		
		
		Set<Statement> summarizedStmts = summmarizer.summarizeBySize(.20);
		
		String outputFilePath = "rdf/summary.rdf";
		File outputFile = new File(outputFilePath); 
		//SesameUtil.writeNTripletoFile(summarizedStmts, outputFile);
		SesameUtil.writeXMLtoFile(summarizedStmts, outputFile);
		
	}

}
