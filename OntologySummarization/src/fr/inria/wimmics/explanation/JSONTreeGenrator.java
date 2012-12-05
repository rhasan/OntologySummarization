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
	Map<String,String> resourceLabels;
	
	public JSONTreeGenrator() {

	}	
	
	public JSONTreeGenrator(Map<String,String> nameSpaces, Map<String,String> resourceLabels) {
		this.nameSpaces = nameSpaces;
		this.resourceLabels = resourceLabels;
	}

	public String generateJASON(GenericTree<Item> g, GenericTreeNode<Item> root) {
		
		this.root = root;
		this.tree = g;
		JSONObject jObjRoot = new JSONObject();
		traverseTree(root, jObjRoot);
		
		return jObjRoot.toJSONString();
	}
	
	private String getPreetyName(Value r) {
		String label = getPreetyLabelName(r);
		//System.out.println(r.stringValue()+"="+label);
		if(label.isEmpty()==false) return label;
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
	private String getPreetyLabelName(Value r) {
		String rUri = r.stringValue();
		String label = "";
		if(resourceLabels.containsKey(rUri)==false)
			return "";
		if(nameSpaces!=null && resourceLabels!=null) {
			String nsp = "";
			for(Entry<String, String> en:nameSpaces.entrySet()) {
				String key = en.getKey();
				String val = en.getValue();
				if(r.stringValue().contains(val)) {
					//System.out.println(en.toString());
					//System.out.println(r.stringValue());					
					//return r.stringValue().replace(val, key+":");
					nsp = key;
					break;
				}			
			}
	
			
			label = resourceLabels.get(rUri);
			if(nsp.isEmpty()==false)
				label = nsp+":"+label;
			
		}
		return label;

	}
	
//	private String getPreetyName(Value r) {
//		if(r instanceof Resource)
//		
////		if(nameSpaces!=null) {
////			for(Entry<String, String> en:nameSpaces.entrySet()) {
////				String key = en.getKey();
////				String val = en.getValue();
////				if(r.stringValue().contains(val)) {
////					return r.stringValue().replace(val, key+":");
////				}
////			}
////		}
//		return r.stringValue();
//		
//	}	
	private String getStringValue(GenericTreeNode<Item> parent) {
		Statement st = (Statement) parent.getObject();
		String predicateStr = getPreetyName(st.getPredicate());
		predicateStr = predicateStr.equals("rdf:type")?"a":predicateStr;
		
		String  s = getPreetyName(st.getSubject())+" "+ predicateStr +" "+getPreetyName(st.getObject());
		return s;
	}
	public void traverseTree(GenericTreeNode<Item> parent, JSONObject jObjParent) {
		
		List<GenericTreeNode<Item>> list = tree.getAdjacent(parent);
		
		if(parent.getObject() instanceof Statement) {
			
			Statement st = (Statement) parent.getObject();
			String subjectStr = getPreetyName(st.getSubject());
			
			String predicateStr = getPreetyName(st.getPredicate());
			predicateStr = predicateStr.equals("rdf:type")?"a":predicateStr;
			
			String objectStr = getPreetyName(st.getObject());
			
			String  s = subjectStr +" "+ predicateStr +" "+predicateStr;			
			String str = getStringValue(parent);
			jObjParent.put("name", str);
			jObjParent.put("subject", subjectStr);
			jObjParent.put("predicate", predicateStr);
			jObjParent.put("object", objectStr);
			
			
			
			
			System.out.println(st.getContext().stringValue()+","+str);
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
