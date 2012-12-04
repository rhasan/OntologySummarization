package fr.inria.wimmics.explanation.test;

import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import fr.inria.wimmics.explanation.JustificationProcessor;
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
}
