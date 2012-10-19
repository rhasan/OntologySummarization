package fr.inria.wimmics.explanation.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.runners.ParentRunner;



public class GenericTree<Item> {

	public final static int WHITE = 0;
	public final static int GRAY = 1;
	public final static int BLACK = 3;
	public final static double SUBTREE_SIZE_IMPORTANCE = 0.5;
	
	private Map<GenericTreeNode<Item>, List<GenericTreeNode<Item>>> graph;
	private Set<GenericTreeNode<Item>> nodes;
	private Map<Item,GenericTreeNode<Item>> nodeObjects;
	private Map<GenericTreeNode<Item>, Integer> visit;
	
	int dfsTime; //take care of the synchronization issues in case of multiple threads calling dfs() 
	boolean dfsBackEdge;	
	

	public GenericTree() {
		graph = new HashMap<GenericTreeNode<Item>, List<GenericTreeNode<Item>>>();
		nodes = new HashSet<GenericTreeNode<Item>>();
		nodeObjects = new HashMap<Item,GenericTreeNode<Item>>();
	}
	
	public Set<GenericTreeNode<Item>> getNodes() {
		return nodes;
	}
	
	public void setNodes(Set<GenericTreeNode<Item>> nodes) {
		this.nodes = nodes;
	}
	
	public GenericTreeNode<Item> getNodeByObject(Item object) {
		
		return nodeObjects.get(object);
	}
	
	public GenericTreeNode<Item> createNode(Item aItem) {
		GenericTreeNode<Item> a = new GenericTreeNode<Item>();
		a.setObject(aItem);
		nodes.add(a);
		nodeObjects.put(aItem, a);
		return a;
	}
	
	public void insterEdge(GenericTreeNode<Item> a, GenericTreeNode<Item> b) {
		List<GenericTreeNode<Item>> list= graph.get(a);
		if(list==null) {
			list = new LinkedList<GenericTreeNode<Item>>();
			graph.put(a, list);
		}
		list.add(b);		
	}	
	
	public void insterEdge(Item aItem, Item bItem) {
		GenericTreeNode<Item> a = getNodeByObject(aItem);
		if(a == null) {
			a = createNode(aItem);
		}
		GenericTreeNode<Item> b = getNodeByObject(bItem);
		if(b == null) {
			b = createNode(bItem);
		}
		insterEdge(a, b);
	}
	
	public boolean isEdge(Item aItem, Item bItem) {
		GenericTreeNode<Item> a = getNodeByObject(aItem);
		if(a == null) {
			throw new NoSuchElementException("Not a valid node");
		}
		GenericTreeNode<Item> b = getNodeByObject(bItem);
		if(b == null) {
			throw new NoSuchElementException("Not a valid node");
		}
		assert a!=null;
		assert b!=null;
		
		return isEdge(a, b);
	}
	public boolean isEdge(GenericTreeNode<Item> a, GenericTreeNode<Item> b) {
		List<GenericTreeNode<Item>> neighbours = getAdjacent(a);
		
		return neighbours.contains(b);
	}
	
	public List<GenericTreeNode<Item>> getAdjacent(GenericTreeNode<Item> a) {
		List<GenericTreeNode<Item>> neighbours  = graph.get(a);
		if(neighbours==null)
			return new LinkedList<GenericTreeNode<Item>>();
		return neighbours;
	}
	
	
	
	public void countSubtrees(){
		
		for(GenericTreeNode<Item> u:nodes) {
			u.setColor(WHITE);
			u.setPath(null);
		}
		dfsBackEdge = false;
		dfsTime = 0;
		for(GenericTreeNode<Item> u:nodes) {
			if(u.getColor()==WHITE) {
				dfsVisit(u);
			}
		}
		
		calculateScore();
	}
	
	private void dfsVisit(GenericTreeNode<Item> u) {
		dfsTime++;
		u.setDistance(dfsTime);
		u.setColor(GRAY);
		for(GenericTreeNode<Item> v:getAdjacent(u)) {
			//dfs of undirected graphs can have only tree edges and back edges (Theorem 22.10, Cormen 3rd Ed.)
			
			if(v.getColor()==WHITE) {
				//a tree edge
				v.setPath(u);
				dfsVisit(v);
				
			}
			else if(v.getColor()==GRAY) {
				//a back edge
				dfsBackEdge = true;
				//if dfs is done only to check for dag, then it can be returned form here after the discovery of the first back edge
			}
			//if the color was black then it would have been a forward or cross edge
			else if(v.getColor()==BLACK) {
				v.setPath(u);
				// this setPath was not in the original cormen implementation
				// if you do dfs visit of the nodes in a grap in a random order then 
				// in a tree, the nodes which are down can become black before the nodes which are early
				// so they are recognized as forward edge and the black node was the start of a subtree
				// so it's path was not set to a parent. therefore, we need to specify it's parent here
			}
		}
		u.setColor(BLACK);
		dfsTime++;
		u.setFinish(dfsTime);
		
		int subTreeCount = countSubTrees(u);
		u.setnSubTree(subTreeCount);
		
		int sizeOfSubTree = countSubTreeSize(u);
		u.setSize(sizeOfSubTree);
		
		
	}
	public void calculateScore() {
		double uScore = 0.0;

		for(GenericTreeNode<Item> u:nodes) {
			GenericTreeNode<Item> parentOfu = u.getPath();
			
			if(parentOfu==null) {
				uScore = 1.0 + (SUBTREE_SIZE_IMPORTANCE * (1.0/(double)u.getSize()));
			}
			else  {
				uScore = ((double)u.getnSubTree()/ (double)parentOfu.getnSubTree()) + (SUBTREE_SIZE_IMPORTANCE * (1.0/(double)u.getSize()));

			}
			u.setScore(uScore);
		}
		
		
	}
	
	private int countSubTreeSize(GenericTreeNode<Item> u) {
		int count = u.getSize();
		List<GenericTreeNode<Item>> adjacents = getAdjacent(u);
		
		for(GenericTreeNode<Item> v:adjacents) {
			count += v.getSize();
		}
		return count+1;
		
	}
	
	private int countSubTrees(GenericTreeNode<Item> u) {
		int count = u.getnSubTree();
		List<GenericTreeNode<Item>> adjacents = getAdjacent(u);
		if(adjacents.size()==0) return 0;
		for(GenericTreeNode<Item> v:adjacents) {
			count += v.getnSubTree();
		}
		return count+1;
	}

}
