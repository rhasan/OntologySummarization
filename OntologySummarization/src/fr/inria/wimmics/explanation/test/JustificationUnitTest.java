package fr.inria.wimmics.explanation.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import fr.inria.wimmics.algorithms.stdlib.In;
import fr.inria.wimmics.explanation.GenericTree;
import fr.inria.wimmics.explanation.GenericTreeNode;
import fr.inria.wimmics.explanation.JSONTreeGenrator;
import fr.inria.wimmics.explanation.JustificationProcessor;
import fr.inria.wimmics.explanation.KnowledgeStatement;

public class JustificationUnitTest {

	
	@Test
	public void countSubtreesTest() {
		In in = new In("files/countsubtree.txt");
		GenericTree<Integer> g = new GenericTree<Integer>();
		while(in.isEmpty()==false) {
			int a = in.readInt();
			int b = in.readInt();
			g.insterEdge(a, b);
			//System.out.println(a+" "+b+ " Inserted: "+g.isEdge(a, b));
		}
		//System.out.println(g.getNodeByObject(10).getObject());
		//System.out.println(g.getNodes());
		
		g.countSubtrees();
		
		for(GenericTreeNode<Integer> n:g.getNodes()) {
			System.out.println("Node: "+n.getObject()+", Subtree(s):"+n.getnSubTree()+", Size of subtree:"+n.getSize()+" , Score:"+n.getScore() + " , Normalized score:"+n.getNormalizedScore());
		}
	}
	
	@Test
	public void generateJASONTest() {
		In in = new In("files/countsubtree.txt");
		GenericTree<Integer> g = new GenericTree<Integer>();
		while(in.isEmpty()==false) {
			int a = in.readInt();
			int b = in.readInt();
			g.insterEdge(a, b);
		}
		
		g.countSubtrees();
		
		GenericTreeNode<Integer> root = g.getNodeByObject(0);
		JSONTreeGenrator<Integer> json = new JSONTreeGenrator<Integer>();
		String jsonString = json.generateJASON(g, root);
		System.out.println(jsonString);
		
	}
	
	@Test
	public void trigJustificationIOTest() throws RDFParseException, RDFHandlerException, IOException, RepositoryException {
		JustificationProcessor jp = new JustificationProcessor();
		jp.parseJustificationFile("rdf/justification.trig","http://www.example.com/" );
		jp.summarizedProofTree("http://alphubel.unice.fr:8080/lodutil/data/d8");
	}
	
	@Test
	public void trigJustificationOneStepProofTreeLinkeCountTest() throws RDFParseException, RDFHandlerException, IOException, RepositoryException, MalformedQueryException, QueryEvaluationException {
		JustificationProcessor jp = new JustificationProcessor();
		jp.parseJustificationFile("rdf/justification.trig","http://www.example.com/" );
		//jp.summarizedProofTree("http://alphubel.unice.fr:8080/lodutil/data/d8");
		List<KnowledgeStatement> S = new ArrayList<KnowledgeStatement>();
		Statement rootRdfStmt = jp.getStmtMap().getFirst("http://alphubel.unice.fr:8080/lodutil/data/d8");
		//http://alphubel.unice.fr:8080/lodutil/data/d7
		KnowledgeStatement kRootStmt = new KnowledgeStatement(rootRdfStmt);
		S.add(kRootStmt);
		S.add( new KnowledgeStatement(jp.getStmtMap().getFirst("http://alphubel.unice.fr:8080/lodutil/data/d7")));
		jp.oneStepProofTreeLink(S);
	}
	
	@Test
	public void topStatementSummaryTest() throws Exception {
		JustificationProcessor jp = new JustificationProcessor();
		jp.parseJustificationFile("rdf/justification.trig","http://www.example.com/" );
		List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatements("http://alphubel.unice.fr:8080/lodutil/data/d8");
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
		prefs.add("http://dbpedia.org/ontology/Scientist");
		List<String> ontologyLocations = new ArrayList<String>();
		List<String> instanceLocations = new ArrayList<String>();
		ontologyLocations.add("rdf/ontology/dbpedia_3.8.owl");
		instanceLocations.add("rdf/instance.rdf");
		jp.parseJustificationFile("rdf/justification.trig","http://www.example.com/" );
		List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatements("http://alphubel.unice.fr:8080/lodutil/data/d8",prefs,ontologyLocations,instanceLocations);
		int index = 1;
		for(KnowledgeStatement kst:kStmts) {
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("ReRanked Score:"+kst.getReRankedScore());
			System.out.println(kst.getStatement().getContext().stringValue()+" "+index++);
		}	
	}
	@Test
	public void topRelevantStatementSummaryRerankTest() throws Exception {
		JustificationProcessor jp = new JustificationProcessor();
		List<String> prefs = new ArrayList<String>();
		prefs.add("http://dbpedia.org/ontology/Scientist");
		List<String> ontologyLocations = new ArrayList<String>();
		List<String> instanceLocations = new ArrayList<String>();
		ontologyLocations.add("rdf/ontology/dbpedia_3.8.owl");
		instanceLocations.add("rdf/instance.rdf");
		jp.parseJustificationFile("rdf/justification.trig","http://www.example.com/" );
		List<KnowledgeStatement> kStatementsReRanked = jp.summarizeJustificationKnowledgeStatementsRerank("http://alphubel.unice.fr:8080/lodutil/data/d8",prefs,ontologyLocations,instanceLocations);
		int index = 1;
		
		for(KnowledgeStatement kst:kStatementsReRanked) {
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("ReRanked Score:"+kst.getReRankedScore());
			System.out.println(kst.getStatement().getContext().stringValue()+" "+index++);
		}
	
	}	

}
