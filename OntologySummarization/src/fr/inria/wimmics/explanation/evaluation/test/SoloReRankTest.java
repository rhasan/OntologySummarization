package fr.inria.wimmics.explanation.evaluation.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import fr.inria.wimmics.explanation.JustificationProcessor;
import fr.inria.wimmics.explanation.KnowledgeStatement;
import fr.inria.wimmics.explanation.evaluation.PrecisionRecall;
import fr.inria.wimmics.explanation.evaluation.PrecisionRecallCalculator;


public class SoloReRankTest {
	static Map<String, String> humanInf1 = new HashMap<String,String>();
	static Map<String, String> humanInf2 = new HashMap<String,String>();
	static Map<String, String> humanInf3 = new HashMap<String,String>();
	static List< Entry<String, String> > humanEntriesInf1;
	static List< Entry<String, String> > humanEntriesInf2;
	static List< Entry<String, String> > humanEntriesInf3;
	static List<String> rootNodes = new ArrayList<String>();
	static List<Integer> fullSize = new ArrayList<Integer>(); 
	static double[] threshordValues = {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
	@BeforeClass
	public static void init() {
		humanInf1.put("Sum-Alan","files/evaluation/inf1/inf1-sum-alan.txt");
		humanInf1.put("Sum-Franck","files/evaluation/inf1/inf1-sum-franck.txt");
		humanInf1.put("Sum-Gessica","files/evaluation/inf1/inf1-sum-gessica.txt");
		humanInf1.put("Sum-Julien","files/evaluation/inf1/inf1-sum-julien.txt");
		
		humanInf2.put("Sum-Alan","files/evaluation/inf2/inf2-sum-alan.txt");
		humanInf2.put("Sum-Franck","files/evaluation/inf2/inf2-sum-franck.txt");
		humanInf2.put("Sum-Gessica","files/evaluation/inf2/inf2-sum-gessica.txt");
		humanInf2.put("Sum-Julien","files/evaluation/inf2/inf2-sum-julien.txt");
		
		humanInf3.put("Sum-Alan","files/evaluation/inf3/inf3-sum-alan.txt");
		humanInf3.put("Sum-Franck","files/evaluation/inf3/inf3-sum-franck.txt");
		humanInf3.put("Sum-Gessica","files/evaluation/inf3/inf3-sum-gessica.txt");
		humanInf3.put("Sum-Julien","files/evaluation/inf3/inf3-sum-julien.txt");
		
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
		//"rdf/inference1.trig"
	}

	@Test
	public void topStatementSummaryReRankTestInf1() throws Exception {
		System.out.println("Inference 1");
		Map<Double,Measures> resInf1 = computeMeasures("rdf/inference1.trig", rootNodes.get(0), humanEntriesInf1, fullSize.get(0));
		printMeasures(resInf1);
	}

	@Test
	public void topStatementSummaryReRankTestInf2() throws Exception {
		System.out.println("Inference 2");
		Map<Double,Measures> resInf1 = computeMeasures("rdf/inference2-just.trig", rootNodes.get(1), humanEntriesInf2, fullSize.get(1));
		printMeasures(resInf1);
	}
	
	@Test
	public void topStatementSummaryReRankTestInf3() throws Exception {
		System.out.println("Inference 3");
		Map<Double,Measures> resInf1 = computeMeasures("rdf/inference3-just.trig", rootNodes.get(2), humanEntriesInf3, fullSize.get(2));
		printMeasures(resInf1);
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
	
	public List<KnowledgeStatement> summaryReRank(Double threshold, String justificationFile, String rootStmtId) throws Exception {
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();
		JustificationProcessor jp = new JustificationProcessor();
		jp.parseJustificationFile(justificationFile,"http://www.example.com/" );
		//List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatements("http://alphubel.unice.fr:8080/lodutil/data/d21");
		List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatementsRerank(rootStmtId,null, null, null);
		int index = 1;
		for(KnowledgeStatement kst:kStmts) {
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("ReRanked Score:"+kst.getReRankedScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+index++);
			if(kst.getReRankedScore()>=threshold) {
				stmts.add(kst);
			}
		}
		return stmts;
	}
	
	public Map<Double,Measures> computeMeasures(String justificationFile, String rootStmtId, List< Entry<String, String> > entries, int fullSize) throws Exception {
		//List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatements("http://alphubel.unice.fr:8080/lodutil/data/d21");
		Map<Double,Measures> res = new HashMap<Double,Measures>();
		res.clear();
		//int i=0;
		for(int i=0;i<threshordValues.length;i++) {
			double th=threshordValues[i];
			boolean initFlag = false;
			
			if(i==0) {
				initFlag = true;
			}
			//i++;
			//List<KnowledgeStatement> kStmts = jp.summarizeProofTreeKnowledgeStatements(th,initFlag,rootStmtId, null, null, null);
			List<KnowledgeStatement> kStmts = summaryReRank(th, justificationFile, rootStmtId);
			
			List<String> S = new ArrayList<String>();
			for(KnowledgeStatement kst:kStmts) {
				String stmtId = kst.getStatement().getContext().stringValue();
				if(stmtId.equals(rootStmtId)) continue;
				S.add(stmtId);
			}
			
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
