package fr.inria.wimmics.explanation;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hamcrest.core.IsInstanceOf;
import org.json.simple.JSONObject;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

public class JSONTreeGenrator<Item> {
	
	GenericTreeNode<Item> root;
	GenericTree<Item> tree;
	Map<String,String> nameSpaces;
	
	public JSONTreeGenrator() {

	}	
	
	public JSONTreeGenrator(Map<String,String> nameSpaces) {
		this.nameSpaces = nameSpaces;
	}

	public String generateJASON(GenericTree<Item> g, GenericTreeNode<Item> root) {
		
		this.root = root;
		this.tree = g;
		JSONObject jObjRoot = new JSONObject();
		traverseTree(root, jObjRoot);
		
		return jObjRoot.toJSONString();
	}
	
	private String getPreetyName(Resource r) {
		if(nameSpaces!=null) {
			//System.out.println(":)");
			for(Entry<String, String> en:nameSpaces.entrySet()) {

				String key = en.getKey();
				String val = en.getValue();
				if(r.stringValue().contains(val)) {
					//System.out.println(en.toString());
					//System.out.println(r.stringValue());					
					return r.stringValue().replace(val, key+":");
				}
				
			}
		}
		return r.stringValue();
		
	}
	
	private String getPreetyName(Value r) {
		if(nameSpaces!=null) {
			for(Entry<String, String> en:nameSpaces.entrySet()) {
				String key = en.getKey();
				String val = en.getValue();
				if(r.stringValue().contains(val)) {
					return r.stringValue().replace(val, key+":");
				}
			}
		}
		return r.stringValue();
		
	}	
	private String getStringValue(GenericTreeNode<Item> parent) {
		Statement st = (Statement) parent.getObject();
		String  s = getPreetyName(st.getSubject())+" "+ getPreetyName(st.getPredicate())+" "+getPreetyName(st.getObject());
		return s;
	}
	public void traverseTree(GenericTreeNode<Item> parent, JSONObject jObjParent) {
		
		List<GenericTreeNode<Item>> list = tree.getAdjacent(parent);
		
		if(parent.getObject() instanceof Statement) {
			String str = getStringValue(parent);
			jObjParent.put("name", str);
			//System.out.println(":):)");
		}
		else {
			jObjParent.put("name", parent.getObject().toString());
			//System.out.println(":):):)");
		}
		
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
