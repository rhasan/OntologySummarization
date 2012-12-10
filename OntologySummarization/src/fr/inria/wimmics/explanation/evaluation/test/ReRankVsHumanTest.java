package fr.inria.wimmics.explanation.evaluation.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.AfterClass;
import org.junit.Test;

import fr.inria.wimmics.explanation.evaluation.SentenceRankCorrelation;
import fr.inria.wimmics.explanation.evaluation.test.SalienceVsHumanTest.MyStrCmp;
import fr.inria.wimmics.util.Util;

public class ReRankVsHumanTest {


	Map<String, String> humanDegreeInf1 = new HashMap<String,String>();
	Map<String, String> humanDegreeInf2 = new HashMap<String,String>();
	Map<String, String> humanDegreeInf3 = new HashMap<String,String>();
	public static Map<String,Double>  pMapValues = new HashMap<String,Double>();
		
	@Test
	public void Inference1Degree() throws Exception {
		//############################### Inference 1 ##########################################

		//humanDegreeInf1.put("Degree-Algo","files/evaluation/inf1/inf1-degree.txt");
		humanDegreeInf1.put("Degree-Alan","files/evaluation/inf1/inf1-degree-alan.txt");
		humanDegreeInf1.put("Degree-Franck","files/evaluation/inf1/inf1-degree-franck.txt");
		humanDegreeInf1.put("Degree-Gessica","files/evaluation/inf1/inf1-degree-gessica.txt");
		humanDegreeInf1.put("Degree-Julien","files/evaluation/inf1/inf1-degree-julien.txt");
		
		String degreeFile = "files/evaluation/inf1/inf1-rerank.txt";
		
		List< Entry<String, String> > humanDegreeEntriesInf1 = new ArrayList< Entry<String, String> >(humanDegreeInf1.entrySet());
		Collections.sort(humanDegreeEntriesInf1,new MyStrCmp());
		int humanDegreeCount = 0;
		double humanDegreeCountSum = 0.0;
		
		for(int j=0;j<humanDegreeEntriesInf1.size();j++) {
			
			
				double tau = SentenceRankCorrelation.sentenceRankCorrelation(degreeFile,humanDegreeEntriesInf1.get(j).getValue());
				humanDegreeCountSum += tau;

				
				System.out.println("Degree vs "+humanDegreeEntriesInf1.get(j).getKey()+": "+Util.round(tau));
				humanDegreeCount++;
		
				double pMapVal = pMapValues.containsKey(humanDegreeEntriesInf1.get(j).getKey())?pMapValues.get(humanDegreeEntriesInf1.get(j).getKey()):0.0;
				pMapVal += tau;
				pMapValues.put(humanDegreeEntriesInf1.get(j).getKey(), pMapVal);

		
		}
			
			

		System.out.println("Degree vs Human agreement avg Inference 1: "+Util.round(humanDegreeCountSum/humanDegreeCount));
		//System.out.println("Human agreement avg Inference 1: "+Util.round(pSumSum/humanDegreeEntriesInf1.size()));		

		double xAvg  = pMapValues.containsKey("X-Avg")?pMapValues.get("X-Avg"):0.0;
		 xAvg += (humanDegreeCountSum/humanDegreeCount);
		pMapValues.put("X-Avg", xAvg );
	
	}
	
	
	@AfterClass
	public static void after() {
		System.out.println("after");
		List<Entry<String,Double>> list = new ArrayList<Entry<String,Double>>(pMapValues.entrySet());
		Collections.sort(list,new Comparator<Entry<String,Double>>() {
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
		for(Entry<String, Double> entry:list) {
			System.out.println(entry.getKey()+" avg: "+Util.round(entry.getValue()/3.0));
		}
	}
	
	@Test
	public void Inference2Degree() throws Exception {
		//############################### Inference 2 ##########################################
		//humanDegreeInf1.put("Degree-Algo","files/evaluation/inf1/inf1-degree.txt");
		humanDegreeInf2.put("Degree-Alan","files/evaluation/inf2/inf2-degree-alan.txt");
		humanDegreeInf2.put("Degree-Franck","files/evaluation/inf2/inf2-degree-franck.txt");
		humanDegreeInf2.put("Degree-Gessica","files/evaluation/inf2/inf2-degree-gessica.txt");
		humanDegreeInf2.put("Degree-Julien","files/evaluation/inf2/inf2-degree-julien.txt");
		
		String degreeFile = "files/evaluation/inf2/inf2-rerank.txt";
		
		List< Entry<String, String> > humanDegreeEntriesInf1 = new ArrayList< Entry<String, String> >(humanDegreeInf2.entrySet());
		Collections.sort(humanDegreeEntriesInf1,new MyStrCmp());
		int humanDegreeCount = 0;
		double humanDegreeCountSum = 0.0;
		
		for(int j=0;j<humanDegreeEntriesInf1.size();j++) {
			
			
				double tau = SentenceRankCorrelation.sentenceRankCorrelation(degreeFile,humanDegreeEntriesInf1.get(j).getValue());
				humanDegreeCountSum += tau;

				
				System.out.println("Degree vs "+humanDegreeEntriesInf1.get(j).getKey()+": "+Util.round(tau));
				humanDegreeCount++;
		
				double pMapVal = pMapValues.containsKey(humanDegreeEntriesInf1.get(j).getKey())?pMapValues.get(humanDegreeEntriesInf1.get(j).getKey()):0.0;
				pMapVal += tau;
				pMapValues.put(humanDegreeEntriesInf1.get(j).getKey(), pMapVal);

		
		}	
		System.out.println("Degree vs Human agreement avg Inference 2: "+Util.round(humanDegreeCountSum/humanDegreeCount));
		//System.out.println("Human agreement avg Inference 1: "+Util.round(pSumSum/humanDegreeEntriesInf1.size()));		

		double xAvg  = pMapValues.containsKey("X-Avg")?pMapValues.get("X-Avg"):0.0;
		 xAvg += (humanDegreeCountSum/humanDegreeCount);
		pMapValues.put("X-Avg", xAvg );		
		
	}
	
	
	@Test
	public void Inference3Degree() throws Exception {
		//############################### Inference 2 ##########################################
		//humanDegreeInf1.put("Degree-Algo","files/evaluation/inf1/inf1-degree.txt");
		humanDegreeInf3.put("Degree-Alan","files/evaluation/inf3/inf3-degree-alan.txt");
		humanDegreeInf3.put("Degree-Franck","files/evaluation/inf3/inf3-degree-franck.txt");
		humanDegreeInf3.put("Degree-Gessica","files/evaluation/inf3/inf3-degree-gessica.txt");
		humanDegreeInf3.put("Degree-Julien","files/evaluation/inf3/inf3-degree-julien.txt");
		
		String degreeFile = "files/evaluation/inf3/inf3-rerank.txt";
		
		List< Entry<String, String> > humanDegreeEntriesInf1 = new ArrayList< Entry<String, String> >(humanDegreeInf3.entrySet());
		Collections.sort(humanDegreeEntriesInf1,new MyStrCmp());
		int humanDegreeCount = 0;
		double humanDegreeCountSum = 0.0;
		
		for(int j=0;j<humanDegreeEntriesInf1.size();j++) {
			
			
				double tau = SentenceRankCorrelation.sentenceRankCorrelation(degreeFile,humanDegreeEntriesInf1.get(j).getValue());
				humanDegreeCountSum += tau;

				
				System.out.println("Degree vs "+humanDegreeEntriesInf1.get(j).getKey()+": "+Util.round(tau));
				humanDegreeCount++;
		
				double pMapVal = pMapValues.containsKey(humanDegreeEntriesInf1.get(j).getKey())?pMapValues.get(humanDegreeEntriesInf1.get(j).getKey()):0.0;
				pMapVal += tau;
				pMapValues.put(humanDegreeEntriesInf1.get(j).getKey(), pMapVal);

		
		}	
		System.out.println("Degree vs Human agreement avg Inference 3: "+Util.round(humanDegreeCountSum/humanDegreeCount));
		//System.out.println("Human agreement avg Inference 1: "+Util.round(pSumSum/humanDegreeEntriesInf1.size()));		

		double xAvg  = pMapValues.containsKey("X-Avg")?pMapValues.get("X-Avg"):0.0;
		 xAvg += (humanDegreeCountSum/humanDegreeCount);
		pMapValues.put("X-Avg", xAvg );		
		

		
	}	
	class MyStrCmp implements Comparator<Entry<String, String>> {
		
		@Override
		public int compare(Entry<String, String> o1, Entry<String, String> o2) {
			// TODO Auto-generated method stub
			return o1.getKey().compareTo(o2.getKey());
		}
	
	
		
	}



}
