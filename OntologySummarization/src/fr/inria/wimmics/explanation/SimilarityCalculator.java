package fr.inria.wimmics.explanation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.inria.acacia.corese.cg.datatype.CoreseDouble;
import fr.inria.acacia.corese.exceptions.EngineException;
import fr.inria.edelweiss.kgram.api.query.Result;
import fr.inria.edelweiss.kgram.api.query.Results;
import fr.inria.edelweiss.kgram.core.Mappings;
import fr.inria.edelweiss.kgram.tool.ResultsImpl;
import fr.inria.edelweiss.kgraph.core.Graph;
import fr.inria.edelweiss.kgraph.query.QueryProcess;
import fr.inria.edelweiss.kgtool.load.Load;
import fr.inria.edelweiss.kgtool.print.ResultFormat;
import fr.inria.wimmics.exception.NoTypeFoundInTheOntology;
import fr.inria.wimmics.util.MyMultiMap;

public class SimilarityCalculator {
	List<String> prefs;
	Graph graph;
	Map<String,Double> simVals = null;
	MyMultiMap<String, String> resTypes = null;
	Set<String> classes = null;
	
	public void initialize(List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws EngineException {
		this.prefs = prefs;
		graph = Graph.create();
		
		Load ld = Load.create(graph);
		//ld.load("rdf/summary.rdf");
		
		for(String ontLoc:ontologyLocations) {
			ld.load(ontLoc);
		}
		
		for(String insLoc:instanceLocations) {
			ld.load(insLoc);
		}
		simVals = new HashMap<String,Double>();
		resTypes = new MyMultiMap<String,String>();
		classes = new HashSet<String>();
		preComputeClassSimilarities();
		preComputeResourceTypes();
	}
	
	private void preComputeClassSimilarities() throws EngineException {
		QueryProcess exec = QueryProcess.create(graph);
		String query = "SELECT distinct ?class1 ?class2 (kg:similarity(?class1, ?class2) as ?sim)" +
				" WHERE" +
				" { ?class1 rdf:type owl:Class ." +
				" ?class2  rdf:type owl:Class .}";
		Mappings map = exec.query(query);
		
		ResultsImpl rs = ResultsImpl.create(map);
		
		
		
		//System.out.println(f);
		Iterator<Result> rsIt = rs.iterator();
		while(rsIt.hasNext()) {
			Result res = rsIt.next();
			String key1 = res.getNode("?class1").getLabel();
			String key2 = res.getNode("?class2").getLabel();
			CoreseDouble val = (CoreseDouble) res.getNode("?sim").getValue();
			
			insertValue(key1, key2, val.doubleValue());

//			System.out.println("class1: "+key1);
//			System.out.println("class2: "+key2);
//
//			System.out.println("sim: "+getValue(key1, key2));
			
		}
		
	}
	
	private void preComputeResourceTypes() throws EngineException {
		QueryProcess exec = QueryProcess.create(graph);


		String query = "select ?resource ?class" +
		" where {" +
		"?resource a ?class ." +
		"}";

		Mappings map = exec.query(query);
		
		
		ResultsImpl rs = ResultsImpl.create(map);		
		Iterator<Result> rsIt = rs.iterator();
		while(rsIt.hasNext()) {
			Result res = rsIt.next();
			String resUri = res.getNode("?resource").getLabel();
			String typeUri = res.getNode("?class").getLabel();
			
			resTypes.put(resUri, typeUri);
			
			//System.out.println("resource: "+resUri);
			//System.out.println("type: "+typeUri + ", all:"+getResourceTypes(resUri).toString());
		}		
	}
	private String getCombinedURI(String uri1,String uri2) {
		return uri1+"|"+uri2;
	}
	private void insertValue(String key1, String key2,double val) {
		classes.add(key1);
		classes.add(key2);
		String key = getCombinedURI(key1, key2);
		simVals.put(key, val);
	}
	
	private double getValue(String key1, String key2) {
		String key = getCombinedURI(key1, key2);
		if(simVals.containsKey(key)==false)
			return 0.0;
		return simVals.get(key);
	}
	
	public double similarity(String classURI1, String classURI2) {
		return getValue(classURI1, classURI2);
	}
	
	public List<String> getResourceTypes(String resUri) {
		return resTypes.get(resUri);
	}
	
	private boolean isClass(String resURI) {
		return classes.contains(resURI);
	}
	
	public double resourceSimilarityScore(String resURI) {
		List<Double> scores = new ArrayList<Double>();
		if(isClass(resURI)) { // j \in SC, when the resURI is a class that is in the ontologies/schemas
			
			for(String pref:prefs) {
				
				double d = similarity(resURI, pref);
				scores.add(d);
				//System.out.println("Pref:"+pref);
				//System.out.println("Score:"+d);
			}
		}
		
		List<String> types=null;
		
		types = getResourceTypes(resURI);
		if(types!=null) {
			for(String type:types) {
				//System.out.println("Type:"+type);
				for(String pref:prefs) {
					
					double d = similarity(type, pref);
					scores.add(d);
					//System.out.println("Pref:"+pref);
					//System.out.println("Score:"+d);
				}
			}
		}
		double max = scores.size()>0? Collections.max(scores):0.0;
		
		return max;
	}
	
	public double resourceSimilarityScoreNew(String resURI) {
		
		List<Double> scores = new ArrayList<Double>();
		List<String> types = new ArrayList<String>();
		if(isClass(resURI)) { // j \in SC, when the resURI is a class that is in the ontologies/schemas
			types.add(resURI);

		} else {
			types = getResourceTypes(resURI);
		}
		
		if(types==null) return 0.0;
		if(prefs.size()==0) return 0.0;
		double maxSum = 0.0;
		for(String pref:prefs) {
			//System.out.println("Type:"+type);
			double max = Double.NEGATIVE_INFINITY;
			for(String type:types) {
				
				double d = similarity(type, pref);
				if(max<d)
					max = d;
			}
			maxSum += max;
		}
		
		
		
		return maxSum/prefs.size();
	}
	
//	public double statementSimilarityScore(String subjectUri, String predicateUri, String objectUri) {
//		
//		return (resourceSimilarityScore(subjectUri)+resourceSimilarityScore(predicateUri)+resourceSimilarityScore(objectUri))/3.0;
//	}
	
	public double statementSimilarityScore(String subjectUri, String predicateUri, String objectUri) {
		
		return (resourceSimilarityScoreNew(subjectUri)+resourceSimilarityScoreNew(predicateUri)+resourceSimilarityScoreNew(objectUri))/3.0;
	}
}
