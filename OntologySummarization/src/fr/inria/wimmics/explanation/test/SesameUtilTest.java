package fr.inria.wimmics.explanation.test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.util.RDFRemover;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import fr.inria.wimmics.openrdf.util.SesameUtil;


public class SesameUtilTest {

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testTrigIO() throws RepositoryException, RDFParseException, IOException, MalformedQueryException, QueryEvaluationException {
		Repository repo = SesameUtil.getMemoryBasedRepository(false);
		
		RepositoryConnection con = repo.getConnection();
		
		File file = new File("rdf/justification.trig");
		
		con.add(file, "http://www.example.com/", RDFFormat.TRIG);
		SesameUtil.printSPARQLQueryhResult(repo, "select ?g ?s ?p ?o where { GRAPH ?g {?s ?p ?o}}");
		//SesameUtil.evaluateAndPrintConstructQuery(repo, "construct { ?s ?p ?o} where { GRAPH ?g {?s ?p ?o}}");
	}

}
