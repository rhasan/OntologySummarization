package fr.inria.wimmics.explanation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.rio.trig.TriGParser;

public class JustificationProcessor {
	
	GenericTree<Statement> g = new GenericTree<Statement>();
	Map<String,Statement> stmtMap = new HashMap<String,Statement>();
	Map<String,String> nameSpaces = null;
	
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
			stmtMap.put(st.getContext().stringValue(), st);
		}
		//System.out.println(collector.getNamespaces().keySet().toString());
		
		for(Statement st:myList) {
			
			//System.out.println(st.toString());
			if(st.getPredicate().toString().equals(Ratio4TA.derivedFrom)) {
				//System.out.println(st.toString());
				Statement a = stmtMap.get(st.getSubject().stringValue());
				Statement b = stmtMap.get(st.getObject().stringValue());
				
				g.insterEdge(a, b);
				
			}
						
		}
		
		
	}
	
	public void summarizedProofTree(String rootURL) {
		g.countSubtrees();
		Statement rootStmt = stmtMap.get(rootURL);
		
		GenericTreeNode<Statement> root = g.getNodeByObject(rootStmt);
		JSONTreeGenrator<Statement> json = new JSONTreeGenrator<Statement>(nameSpaces);
		String jsonString = json.generateJASON(g, root);
		System.out.println(jsonString);			
	}

}
