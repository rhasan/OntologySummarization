package fr.inria.wimmics.explanation.evaluation.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inria.wimmics.explanation.JustificationProcessor;
import fr.inria.wimmics.explanation.KnowledgeStatement;
import fr.inria.wimmics.explanation.evaluation.PrecisionRecall;
import fr.inria.wimmics.explanation.evaluation.PrecisionRecallCalculator;

/**
 * run test cases for inferences separately
 * @author hrakebul
 *
 */
public class ProofTreeSummarySimilarityTest {

	static Map<String, String> humanInf1 = new HashMap<String,String>();
	static Map<String, String> humanInf2 = new HashMap<String,String>();
	static Map<String, String> humanInf3 = new HashMap<String,String>();
	static List< Entry<String, String> > humanEntriesInf1;
	static List< Entry<String, String> > humanEntriesInf2;
	static List< Entry<String, String> > humanEntriesInf3;
	static List<String> rootNodes = new ArrayList<String>();
	static List<Integer> fullSize = new ArrayList<Integer>(); 
	static List<String> instance = new ArrayList<String>();
	static List<String> concept = new ArrayList<String>();	
	static List<String> ontologyLocations = new ArrayList<String>();
	
	static double[] threshordValues = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
	@BeforeClass
	public static void init() {
		humanInf1.put("Sum-Alan","files/evaluation/inf1/inf1-sum-sim-alan.txt");
		humanInf1.put("Sum-Franck","files/evaluation/inf1/inf1-sum-sim-franck.txt");
		humanInf1.put("Sum-Gessica","files/evaluation/inf1/inf1-sum-sim-gessica.txt");
		humanInf1.put("Sum-Julien","files/evaluation/inf1/inf1-sum-sim-julien.txt");
		
		humanInf2.put("Sum-Alan","files/evaluation/inf2/inf2-sum-sim-alan.txt");
		humanInf2.put("Sum-Franck","files/evaluation/inf2/inf2-sum-sim-franck.txt");
		humanInf2.put("Sum-Gessica","files/evaluation/inf2/inf2-sum-sim-gessica.txt");
		humanInf2.put("Sum-Julien","files/evaluation/inf2/inf2-sum-sim-julien.txt");
		
		humanInf3.put("Sum-Alan","files/evaluation/inf3/inf3-sum-sim-alan.txt");
		humanInf3.put("Sum-Franck","files/evaluation/inf3/inf3-sum-sim-franck.txt");
		humanInf3.put("Sum-Gessica","files/evaluation/inf3/inf3-sum-sim-gessica.txt");
		humanInf3.put("Sum-Julien","files/evaluation/inf3/inf3-sum-sim-julien.txt");
		
		humanEntriesInf1 = new ArrayList< Entry<String, String> >(humanInf1.entrySet());
		Collections.sort(humanEntriesInf1,new MyStrCmp());		

		humanEntriesInf2 = new ArrayList< Entry<String, String> >(humanInf2.entrySet());
		Collections.sort(humanEntriesInf2,new MyStrCmp());		
		
		humanEntriesInf3 = new ArrayList< Entry<String, String> >(humanInf3.entrySet());
		Collections.sort(humanEntriesInf3,new MyStrCmp());
		
		rootNodes.add("http://alphubel.unice.fr:8080/lodutil/data/d21");//inf1
		rootNodes.add("http://alphubel.unice.fr:8080/lodutil/data/d24");//inf2
		rootNodes.add("http://alphubel.unice.fr:8080/lodutil/data/d1");//inf3
		fullSize.add(21); //inf1
		fullSize.add(21); //inf2
		fullSize.add(23); //inf3
		instance.add("rdf/inference1-instance.rdf");//inf1
		instance.add("rdf/inference2-instance.rdf");//inf2
		instance.add("rdf/inference3-instance.rdf");//inf3
		concept.add("http://dbpedia.org/ontology/Event");//inf1
		concept.add("http://dbpedia.org/ontology/Infrastructure");//inf2
		concept.add("http://dbpedia.org/ontology/Place");//inf3

		ontologyLocations.add("rdf/ontology/dbpedia_3.8.owl");
		ontologyLocations.add("rdf/ontology/geonames_ontology_v3.1.rdf");
		//"rdf/inference1.trig"
	}
	
	@Test
	public void treeSummarizedStatementsInf1() throws Exception {

		List<String> instanceLocations = new ArrayList<String>();
		List<String> prefs = new ArrayList<String>();
		prefs.add(concept.get(0));
		instanceLocations.add(instance.get(0));
		//String rootStmtId = ;
		System.out.println("Inference 1");
		Map<Double,Measures> resInf1 = computeMeasures("rdf/inference1.trig", rootNodes.get(0), humanEntriesInf1, fullSize.get(0),instanceLocations,prefs);
		printMeasures(resInf1);
	
	}
	
	@Test
	public void treeSummarizedStatementsInf2() throws Exception {
		
		
		System.out.println("Inference 2");
		
		List<String> instanceLocations = new ArrayList<String>();
		List<String> prefs = new ArrayList<String>();
		prefs.add(concept.get(1));
		instanceLocations.add(instance.get(1));
		
		Map<Double,Measures> resInf2 = computeMeasures("rdf/inference2-just.trig", rootNodes.get(1), humanEntriesInf2, fullSize.get(1),instanceLocations,prefs);
		printMeasures(resInf2);			
	}
	@Test
	public void treeSummarizedStatementsInf3() throws Exception {
		System.out.println("Inference 3");
		List<String> instanceLocations = new ArrayList<String>();
		List<String> prefs = new ArrayList<String>();
		prefs.add(concept.get(2));
		instanceLocations.add(instance.get(2));		
		Map<Double,Measures> resInf3 = computeMeasures("rdf/inference3-just.trig", rootNodes.get(2), humanEntriesInf3, fullSize.get(2),instanceLocations,prefs);
		printMeasures(resInf3);
		
	}
	
	public void printMeasures(Map<Double,Measures> resInf1) {
		System.out.print("\nThreshold");
		for(int i=0;i<threshordValues.length;i++) {
			double th = threshordValues[i];

			if(resInf1.containsKey(th)) {
				Measures m = resInf1.get(th);
				System.out.print("\t"+th);
			}
		}
		System.out.print("\nF");
		for(int i=0;i<threshordValues.length;i++) {
			double th = threshordValues[i];

			if(resInf1.containsKey(th)) {
				Measures m = resInf1.get(th);
				System.out.print("\t"+m.getfMeasure());
			}
		}
		System.out.print("\nCR");
		for(int i=0;i<threshordValues.length;i++) {
			double th = threshordValues[i];

			if(resInf1.containsKey(th)) {
				Measures m = resInf1.get(th);
				System.out.print("\t"+m.getCompressionRatio());
			}
		}		
		System.out.print("\nPrecision");
		for(int i=0;i<threshordValues.length;i++) {
			double th = threshordValues[i];

			if(resInf1.containsKey(th)) {
				Measures m = resInf1.get(th);
				System.out.print("\t"+m.getPrecision());
			}
		}		
		System.out.print("\nRecall");
		for(int i=0;i<threshordValues.length;i++) {
			double th = threshordValues[i];

			if(resInf1.containsKey(th)) {
				Measures m = resInf1.get(th);
				System.out.print("\t"+m.getRecall());
			}
		}
		System.out.println("\nend");		
	}	
	public Map<Double,Measures> computeMeasures(String justificationFile, String rootStmtId, List< Entry<String, String> > entries, int fullSize, List<String> instanceLocations, List<String> prefs) throws Exception {
		
		JustificationProcessor jp = new JustificationProcessor();
		jp.parseJustificationFile( justificationFile,"http://www.example.com/");
		//List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatements("http://alphubel.unice.fr:8080/lodutil/data/d21");
		Map<Double,Measures> res = new HashMap<Double,Measures>();
		res.clear();
		//int i=0;
		for(int i=0;i<threshordValues.length;i++) {
			double th=threshordValues[i];
			boolean initFlag = false;
			//System.out.println("###################### Threshold:"+th+" ###################### ");
			if(i==0) {
				initFlag = true;
			}
			//i++;

			List<KnowledgeStatement> kStmts = jp.summarizeProofTreeKnowledgeStatements(th,initFlag,rootStmtId, prefs, ontologyLocations,instanceLocations);
			
			List<String> S = new ArrayList<String>();
			for(KnowledgeStatement kst:kStmts) {
				String stmtId = kst.getStatement().getContext().stringValue();
				if(stmtId.equals(rootStmtId)) continue;
				S.add(stmtId);
				//System.out.println(kst.getStatement().toString());
			}
			//System.out.println("Summarized statement size:"+S.size());
			
			//if(S.size()==0) continue;
			
			double pSum = 0;
			double rSum = 0;
			for(Entry<String, String> en:entries) {
				PrecisionRecall pr = PrecisionRecallCalculator.calculatePrecisionRecall(S, en.getValue());
				pSum += pr.getPrecision();
				rSum += pr.getRecall();
				//System.out.println("Human:"+en.getKey());
				//System.out.println("Threshold:"+th);
				//System.out.println("Precission:"+pr.getPrecision());
				//System.out.println("Recall:"+pr.getRecall());
			}
			double pAvg = pSum/entries.size();
			double rAvg = rSum/entries.size();
			double f_measure = (2*pAvg*rAvg)/(pAvg+rAvg);
			double compression_ratio = (double)S.size()/(double)(fullSize);
			//System.out.println("###################### Threshold:"+th+" ###################### ");
			//System.out.println("Size:"+S.size());
			//System.out.println("Precission:"+pAvg);
			//System.out.println("Recall:"+rAvg);

			//System.out.println("F:"+f_measure);
			//System.out.println("CR: "+compression_ratio);
			Measures m = new Measures(f_measure, compression_ratio, th, pAvg, rAvg);
			res.put(th, m);
		}		
		return res;
	}
}
