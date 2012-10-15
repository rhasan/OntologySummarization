package fr.inria.wimmics.explanation.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import fr.inria.wimmics.explanation.model.GenericRDFSentence;

public class RDFSentenceGraph {
	
	private Set<Statement> T = null;//set of original statements
	private HashMap<Statement,Set<Statement>> bConnected = null;
	private Set<GenericRDFSentence> graphSentences = null;
	private double navigProp;
	
	private HashMap<GenericRDFSentence,Set<GenericRDFSentence>> coordinateLinks = null;
	private HashMap<GenericRDFSentence,Set<GenericRDFSentence>> sequentialLinks = null;
	private HashMap<GenericRDFSentence,HashMap<GenericRDFSentence,Double>> allSentenceLinks = null;
	
	
	public Set<GenericRDFSentence> getGraphSentences() {
		return graphSentences;
	}
	
	public RDFSentenceGraph(Set<Statement> statements, double p) {
		bConnected = new HashMap<Statement,Set<Statement>>();
		graphSentences = new HashSet<GenericRDFSentence>();
		navigProp = p;
		T = statements;
		
		constructBconnected();
		constructGenericRDFSentences();
		constructRDFSentenceLinks();
	}
	
	
	public void createLink(HashMap<GenericRDFSentence,Set<GenericRDFSentence>> links, GenericRDFSentence a, GenericRDFSentence b) {
		
		Set<GenericRDFSentence> d2 = null;
		d2 = links.get(a);
		if(d2 == null) {
			
			d2 = new HashSet<GenericRDFSentence>();
			links.put(a, d2);
		}
		d2.add(b);
	}
	
	
	public void createWeightedLink(HashMap<GenericRDFSentence,HashMap<GenericRDFSentence, Double>> links, GenericRDFSentence a, GenericRDFSentence b, double w) {
		
		HashMap<GenericRDFSentence, Double> d2 = null;
		d2 = links.get(a);
		if(d2 == null) {
			d2 = new HashMap<GenericRDFSentence, Double>();
			links.put(a, d2);
		}
		d2.put(b, w);
		
	}

	public double getWeight(GenericRDFSentence a, GenericRDFSentence b) {
		
		HashMap<GenericRDFSentence, Double> d2 = allSentenceLinks.get(a);
		Double w = d2.get(b);
		return w;
	}
	
	
	public void printGraph() {
		for(GenericRDFSentence s1:allSentenceLinks.keySet()) {
			HashMap<GenericRDFSentence, Double> d2 = allSentenceLinks.get(s1);
			
			for(GenericRDFSentence s2:d2.keySet()) {
				double w = d2.get(s2);
				
				System.out.print("\nLink from \n------------------\n"+s1.toString());
				System.out.print("to \n------------------\n"+s2.toString());
				System.out.print("Weight:"+w);
				System.out.print("\n#################################\n\n");
			}
			
		}
	}
	
	
	public void constructRDFSentenceLinks() {
		coordinateLinks = new HashMap<GenericRDFSentence,Set<GenericRDFSentence>>();
		sequentialLinks = new HashMap<GenericRDFSentence,Set<GenericRDFSentence>>();
		allSentenceLinks = new HashMap<GenericRDFSentence,HashMap<GenericRDFSentence, Double>>();
		
		for(GenericRDFSentence sentence1: graphSentences) {
			for(GenericRDFSentence sentence2: graphSentences ) {
				if(sentence2.equals(sentence1)==false) {
					int seq = 0;
					int cor = 0;

					if(sentence1.hasSequentialLink(sentence2)) {
						createLink(sequentialLinks, sentence1, sentence2);
						seq = 1;

					}
					
					if(sentence1.hasCoordinateLink(sentence2)) {
						createLink(coordinateLinks, sentence1, sentence2);
						cor = 1;

					}
					double w = navigProp * seq + (1.0 - navigProp) * cor;
					if(seq == 1 || cor==1) {
						createWeightedLink(allSentenceLinks, sentence1, sentence2, w);
					}
					
				}
				
			}
		}
	}
	
	public boolean commonBlanNode(BNode bn, Statement st) {
		
		if(st.getSubject() instanceof BNode) {
			BNode subBn = (BNode)st.getSubject();
			if(subBn.equals(bn))
				return true;
		}
		if(st.getObject() instanceof BNode) {
			BNode objBn = (BNode) st.getObject();
			if(objBn.equals(bn)) 
				return true;
		}
		return false;
	}
	
	public void constructBconnected(){
		
		for(Statement i:T){
			Resource stSubject = i.getSubject();
			Value stObject = i.getObject();
			//RDF sentence starts with statement i
			Set<Statement> sTmp = new HashSet<Statement>();
			for(Statement j:T) {
				if(i.equals(j)==false) {
					BNode bnI = null;
					if(stSubject instanceof BNode) {
						bnI = (BNode) stSubject;
						//check if subject or object of j is the same blank node or not
						if(commonBlanNode(bnI, j)) {
							//add the statement j to the RDF sentence
							
							sTmp.add(j);
						}
						
					} 
					
					if(stObject instanceof BNode) {
						bnI = (BNode) stObject;
						if(commonBlanNode(bnI, j)) {
							sTmp.add(j);
						}
					}
				
				}
			}
			bConnected.put(i, sTmp);
		}
		
	}
	
	
	public boolean isBConnected(Statement i, Statement j) {
		Set<Statement> stmts = bConnected.get(i);
		if(stmts.contains(j)) {
			return true;
		}
		return false;
	}
	
	public Set<Value> getSentenceObjects(Statement i, Statement j) {
		
		Set<Value> set = new HashSet<Value>();
		Set<Value> iSet = new HashSet<Value>();

		iSet.add(i.getSubject());
		iSet.add(i.getPredicate());
		
		if((i.getObject() instanceof BNode)==false) {
			set.add(i.getObject());
			
		}
		
		if(j==null)
			return set;
		
		
		if((j.getSubject() instanceof BNode )==false && iSet.contains(j.getSubject()) == false) {
			set.add(j.getSubject());
		}
		if(iSet.contains(j.getPredicate())==false) {
			set.add(j.getPredicate());
			
		}
		
		if(iSet.contains(j.getObject())==false) {
			set.add(j.getObject());
		}
		
		return set;
	}
	
	public void constructGenericRDFSentences() {

		for(Statement i:T){
			if((i.getSubject() instanceof BNode)==false) {
				GenericRDFSentence sentence = new GenericRDFSentence();
				sentence.addStatement(i);
				sentence.setMainStmt(i);
				sentence.setSubject(i.getSubject());
				sentence.setPredicate(i.getPredicate());
				Set<Value> objectsTmp = new HashSet<Value>();
				int addedCount = 0;
				for(Statement j:T) {
					if(i.equals(j)==false) {
						if(isBConnected(i, j) && (j.getSubject() instanceof BNode)) {
							sentence.addStatement(j);
							objectsTmp.addAll(getSentenceObjects(i, j));
							addedCount++;
							
						}
					}
				}
				if(addedCount==0) {
					objectsTmp.addAll(getSentenceObjects(i, null));
				}
				sentence.setObjects(objectsTmp);
				graphSentences.add(sentence);
				
			}
		}
	}
}
