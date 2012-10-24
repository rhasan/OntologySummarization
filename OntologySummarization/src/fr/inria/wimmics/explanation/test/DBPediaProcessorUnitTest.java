package fr.inria.wimmics.explanation.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import fr.inria.wimmics.explanation.DBPediaProcessor;
import fr.inria.wimmics.openrdf.util.SesameUtil;

public class DBPediaProcessorUnitTest {

	@Test
	public void testLoadOntologyandQuery() throws RepositoryException, RDFParseException, IOException, MalformedQueryException, QueryEvaluationException {
		Repository repo = SesameUtil.getMemoryBasedRepository(true);
		DBPediaProcessor dbp = new DBPediaProcessor(repo);
		dbp.loadDBPediaOntology("rdf/ontology/dbpedia_3.8.owl",RDFFormat.RDFXML);
		
		dbp.loadDBPediaData("rdf/data/dbpedia/Becky_Taylor.nt", RDFFormat.NTRIPLES);
		
		String queryString = null;
		String prefix = "PREFIX dbpedia-owl: <"+DBPediaProcessor.NAMESPACE_DBPedia_ONTOLOGY+">" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";

		queryString = prefix + "SELECT * WHERE " +
					"{<http://dbpedia.org/resource/Becky_Taylor> rdf:type ?object.}";
		
		dbp.queryAndPrint(queryString);

		
	}

	@Test
	public void testLoadOntologyandConstructQuery() throws RepositoryException, RDFParseException, IOException, MalformedQueryException, QueryEvaluationException {
		Repository repo = SesameUtil.getMemoryBasedRepository(true);
		DBPediaProcessor dbp = new DBPediaProcessor(repo);
		dbp.loadDBPediaOntology("rdf/ontology/dbpedia_3.8.owl",RDFFormat.RDFXML);
		
		dbp.loadDBPediaData("rdf/data/dbpedia/Becky_Taylor.nt", RDFFormat.NTRIPLES);
		
		String queryString = null;
		String prefix = "PREFIX dbpedia-owl: <"+DBPediaProcessor.NAMESPACE_DBPedia_ONTOLOGY+">" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";

		queryString = prefix + "CONSTRUCT { <http://dbpedia.org/resource/Becky_Taylor> rdf:type ?object  }   WHERE " +
					"{<http://dbpedia.org/resource/Becky_Taylor> rdf:type ?object.}";
		
		dbp.constructQueryAndPrint(queryString);

		
	}	
	
}
