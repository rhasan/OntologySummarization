package fr.inria.wimmics.explanation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;




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
				if(v.getPath()==null) {
					v.setPath(u);
				}
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
		
		if(u.getObject() instanceof KnowledgeStatement) {
			double dc = countScore(u);
			u.setCombinedScore(dc);
			
		}
	}
	private void calculateScore() {
		double uScore = 0.0;
		double maxScore = Double.NEGATIVE_INFINITY;

		for(GenericTreeNode<Item> u:nodes) {
			GenericTreeNode<Item> parentOfu = u.getPath();
			
			if(parentOfu==null) {
				//uScore = 1.0 + (SUBTREE_SIZE_IMPORTANCE * (1.0/(double)u.getSize()));
				uScore = 1.0;
			}
			else  {
				//uScore = ((double)u.getnSubTree()/ (double)parentOfu.getnSubTree()) + (SUBTREE_SIZE_IMPORTANCE * (1.0/(double)u.getSize()));
				//for second part (not using this): increases the current tree size by u.getSize()
				
				uScore = ((double)u.getnSubTree()/ (double)parentOfu.getnSubTree()) + (SUBTREE_SIZE_IMPORTANCE * (1.0/(double)parentOfu.getSize()));
						
				//first part: percentage of explanation this sub-branch will cover if expanded with respect to total subtrees this branch has
						
				//second part: keep the proof tree size smaller (second).. 
				//the calculated value is the inverse of the maximum number of nodes this branch will have if this node is expanded, assuming all 
				//the other subtrees of this branch are expanded.. so if this node is expanded, the number of nodes will be equal to total nodes
				//this branch have ((i.e. the maximum possible size))
				//the maximum number of nodes in this branch without expanding the current node = parentOfu.getSize() - u.getSize()
				//when the current node is expanded, maximum number of nodes added are u.getSize()
				//therefore, if the current node is expanded then the maximum number of nodes this branch will have are 
				//parentOfu.getSize() - u.getSize() + u.getSize()
				//works relative to the whole branch size.. good for tree with depth
			}
			u.setScore(uScore);
			if(maxScore<uScore) {
				maxScore = uScore;
			}
		}
		
		for(GenericTreeNode<Item> u:nodes) {
			u.setNormalizedScore(u.getScore()/maxScore);
		}
		
		
	}
	
//	private double countScore(GenericTreeNode<Item> u) {
//		//double sum = ((KnowledgeStatement)u.getObject()).getDegreeCentrality();
//		List<GenericTreeNode<Item>> adjacents = getAdjacent(u);
//		if(adjacents.size()==0) return ((KnowledgeStatement)u.getObject()).getScore();
//		double max = Double.NEGATIVE_INFINITY;
//		for(GenericTreeNode<Item> v:adjacents) {
//			//double vc = ((KnowledgeStatement)v.getObject()).getScore();
//			double vc = v.getCombinedScore();
//			if(max<vc) {
//				max = vc;
//			}
//		}
//		//double res = sum / (adjacents.size()+1);
//		//System.out.println("In tree centrality:"+max);
//		return max;
//	}
	/**
	 * computes the weight of a branch in a proof tree by summing the score of
	 * all the nodes in that branch and dividing the sum by the number of nodes 
	 * in that branch.
	 * @param u
	 * @return
	 */
	private double countScore(GenericTreeNode<Item> u) {
	List<GenericTreeNode<Item>> adjacents = getAdjacent(u);
	if(adjacents.size()==0) return ((KnowledgeStatement)u.getObject()).getScore();

	double sum = ((KnowledgeStatement)u.getObject()).getScore();

	for(GenericTreeNode<Item> v:adjacents) {
		double vc = v.getCombinedScore();
		sum += vc;
	}
	double res = sum / (adjacents.size()+1);
	//System.out.println("In tree centrality:"+max);
	return res;
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
	
	public void expandSubTree(GenericTreeNode<Item> s, double threshold) {
		for(GenericTreeNode<Item> u:nodes) {
			u.setColor(WHITE);
			u.setDistance(Integer.MAX_VALUE);
			u.setPath(null);
		}
		s.setColor(GRAY);
		s.setDistance(0);
		s.setPath(null);
		Queue<GenericTreeNode<Item>> Q=new LinkedList<GenericTreeNode<Item>>();
		Q.add(s);
		while(Q.isEmpty()==false) {
			GenericTreeNode<Item> u = Q.poll();
			List<GenericTreeNode<Item>> list = getAdjacent(u);
			for(GenericTreeNode<Item> v:list) {
				if(v.getNormalizedScore()>threshold && v.getColor() == WHITE) {
					v.setColor(GRAY);
					v.setDistance(u.getDistance()+1);
					v.setPath(u);
					Q.add(v);
				}
			}
			u.setColor(BLACK);
		}
	}
	
	public List<KnowledgeStatement> traverseByScoreThreshold(GenericTreeNode<Item> s, double threshold) {
		List<KnowledgeStatement> kList = new ArrayList<KnowledgeStatement>();
		if((s.getObject() instanceof KnowledgeStatement)==false) return null;
		for(GenericTreeNode<Item> u:nodes) {
			u.setColor(WHITE);
			u.setDistance(Integer.MAX_VALUE);
			u.setPath(null);
		}		
		s.setColor(GRAY);
		s.setDistance(0);
		s.setPath(null);
		Queue<GenericTreeNode<Item>> Q=new LinkedList<GenericTreeNode<Item>>();
		Q.add(s);
		while(Q.isEmpty()==false) {
			GenericTreeNode<Item> u = Q.poll();
			//System.out.println("In Tree:"+((KnowledgeStatement) u.getObject()).getStatement().toString());
			//System.out.println("Combined Score:"+u.getCombinedScore());
			//System.out.println("Statement Score:"+((KnowledgeStatement) u.getObject()).getScore());
			kList.add((KnowledgeStatement)u.getObject());
			List<GenericTreeNode<Item>> list = getAdjacent(u);
			for(GenericTreeNode<Item> v:list) {
				//KnowledgeStatement vkst = (KnowledgeStatement) v.getObject();
				if(v.getCombinedScore()>=threshold && v.getColor() == WHITE) {
				//if(v.getColor() == WHITE) {
					v.setColor(GRAY);
					v.setDistance(u.getDistance()+1);
					v.setPath(u);
					Q.add(v);
				}
			}
			u.setColor(BLACK);
		}
		return kList;
	}
	

}
