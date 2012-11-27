package fr.inria.wimmics.explanation.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import fr.inria.wimmics.algorithms.stdlib.In;
import fr.inria.wimmics.explanation.GenericTree;
import fr.inria.wimmics.explanation.GenericTreeNode;
import fr.inria.wimmics.explanation.JSONTreeGenrator;
import fr.inria.wimmics.explanation.JustificationProcessor;

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
	public void trigJustificationIOTest() throws RDFParseException, RDFHandlerException, IOException {
		JustificationProcessor jp = new JustificationProcessor();
		jp.parseJustificationFile("rdf/justification.trig","http://www.example.com/" );
		jp.summarizedProofTree("http://alphubel.unice.fr:8080/lodutil/data/d8");
	}

}
