package fr.inria.wimmics.explanation;

import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONObject;

public class JSONTreeGenrator<Item> {
	
	GenericTreeNode<Item> root;
	GenericTree<Item> tree;
	
	public String generateJASON(GenericTree<Item> g, GenericTreeNode<Item> root) {
		
		this.root = root;
		this.tree = g;
		JSONObject jObjRoot = new JSONObject();
		traverseTree(root, jObjRoot);
		
		return jObjRoot.toJSONString();
	}
	
	public void traverseTree(GenericTreeNode<Item> parent, JSONObject jObjParent) {
		
		List<GenericTreeNode<Item>> list = tree.getAdjacent(parent);
		
		jObjParent.put("name", parent.getObject().toString());
		jObjParent.put("nScore", parent.getNormalizedScore());
		jObjParent.put("subtree", parent.getnSubTree());

		if(list.size()==0) {
			return;
		}
		
		LinkedList<JSONObject> jObjChildren = new LinkedList<JSONObject>();
		jObjParent.put("children", jObjChildren);
		
		for(GenericTreeNode<Item> child:list) {
			JSONObject jObjChild = new JSONObject();
			jObjChildren.add(jObjChild);
			traverseTree(child, jObjChild);
		}
	}

}
