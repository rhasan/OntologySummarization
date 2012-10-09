package fr.inria.wimmics.explanation.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import fr.inria.wimmics.openrdf.util.SesameUtil;

public class DBPediaProcessor {
	Repository myRepository = null;
	public static String DBPediaGraphURI = "http://dbpedia.org";
	public static String NAMESPACE_DBPedia_ONTOLOGY = "http://dbpedia.org/ontology/";
	
	public DBPediaProcessor(Repository myRepository) throws RepositoryException {
		this.myRepository = myRepository;
		//SesameUtil.getMemoryBasedRepository(true);
	}
	
	public void loadRDF(String filePath, String baseURI, RDFFormat serializationFormat) throws RDFParseException, IOException, RepositoryException{
		RepositoryConnection con = myRepository.getConnection();
		
		File inputFile1 = new File(filePath);

		
		try {
			   con.setAutoCommit(false);

			   // Add the first file
			   con.add(inputFile1, baseURI, serializationFormat);

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
	}
	
	
	public void loadDBPediaOntology(String filePath, RDFFormat serializationFormat) throws RDFParseException, RepositoryException, IOException {
		
		loadRDF(filePath,"http://dbpedia.org/ontology/",serializationFormat);
	}
	
	public void loadDBPediaData(String filePath, RDFFormat serializationFormat) throws RDFParseException, RepositoryException, IOException {
		loadRDF(filePath,null,serializationFormat);
	}


	public void queryAndPrint(String queryString) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		SesameUtil.printSPARQLQueryhResult(myRepository, queryString);
	}
	
	public void constructQueryAndPrint(String queryString) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		SesameUtil.evaluateAndPrintConstructQuery(myRepository,queryString);		
	}

	
}
