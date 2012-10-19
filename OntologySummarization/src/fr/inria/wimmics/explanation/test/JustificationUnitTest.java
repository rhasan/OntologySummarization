package fr.inria.wimmics.explanation.test;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.inria.wimmics.algorithms.stdlib.In;
import fr.inria.wimmics.explanation.controller.GenericTree;
import fr.inria.wimmics.explanation.controller.GenericTreeNode;

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
			System.out.println("Node: "+n.getObject()+", Subtree(s):"+n.getnSubTree()+", Size of subtree:"+n.getSize()+" , Score:"+n.getScore());
		}
	}

}
