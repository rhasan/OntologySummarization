package fr.inria.wimmics.explanation.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import fr.inria.wimmics.explanation.JustificationProcessor;
import fr.inria.wimmics.explanation.KnowledgeStatement;
import fr.inria.wimmics.util.GeoNames;

public class Inference3UnitTest {
	@Test
	public void trigJustificationProofTreeTest() throws RDFParseException, RDFHandlerException, IOException, RepositoryException {
		JustificationProcessor jp = new JustificationProcessor();
		jp.parseJustificationFile("rdf/inference3-just.trig","http://www.example.com/" );
		//jp.loadResourceLabelFromOntology("rdf/ontology/geonames_ontology_v3.1.rdf", GeoNames.base);
		jp.loadResourceLabelFromOntology("rdf/inference3-instance.rdf", GeoNames.base);
//		/jp.loadResourceLabelFromOntology("rdf/ontology/dbpedia_3.8.owl", "http://www.example.com/");
		String jsonStr = jp.summarizedProofTree("http://alphubel.unice.fr:8080/lodutil/data/d1");
		
        FileOutputStream file=new FileOutputStream("visualization/tree/inference3.json");
        file.write(jsonStr.getBytes());
        file.close();		
	}
	@Test
	public void topStatementSummaryTest() throws Exception {
		JustificationProcessor jp = new JustificationProcessor();
		jp.parseJustificationFile("rdf/inference3-just.trig","http://www.example.com/" );
		List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatements("http://alphubel.unice.fr:8080/lodutil/data/d1");
		int index = 1;
		for(KnowledgeStatement kst:kStmts) {
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("ReRanked Score:"+kst.getReRankedScore());
			System.out.println(kst.getStatement().getContext().stringValue()+" "+index++);
		}
	}
	@Test
	public void topStatementSummaryReRankTest() throws Exception {
		JustificationProcessor jp = new JustificationProcessor();
		jp.parseJustificationFile("rdf/inference3-just.trig","http://www.example.com/" );
		//List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatements("http://alphubel.unice.fr:8080/lodutil/data/d21");
		List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatementsRerank("http://alphubel.unice.fr:8080/lodutil/data/d1",null, null, null);
		int index = 1;
		for(KnowledgeStatement kst:kStmts) {
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("ReRanked Score:"+kst.getReRankedScore());
			System.out.println(kst.getStatement().getContext().stringValue()+" "+index++);
		}
	}
	@Test
	public void topRelevantStatementSummaryTest() throws Exception {
		JustificationProcessor jp = new JustificationProcessor();
		List<String> prefs = new ArrayList<String>();
		prefs.add("http://dbpedia.org/ontology/Place");
		List<String> ontologyLocations = new ArrayList<String>();
		List<String> instanceLocations = new ArrayList<String>();
		ontologyLocations.add("rdf/ontology/dbpedia_3.8.owl");
		ontologyLocations.add("rdf/ontology/geonames_ontology_v3.1.rdf");
		
		instanceLocations.add("rdf/inference3-instance.rdf");
		jp.parseJustificationFile("rdf/inference3-just.trig","http://www.example.com/" );
		List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatements("http://alphubel.unice.fr:8080/lodutil/data/d1",prefs,ontologyLocations,instanceLocations);
		int index = 1;
		for(KnowledgeStatement kst:kStmts) {
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("ReRanked Score:"+kst.getReRankedScore());
			System.out.println(kst.getStatement().getContext().stringValue()+" "+index++);
		}	
	}	
}
