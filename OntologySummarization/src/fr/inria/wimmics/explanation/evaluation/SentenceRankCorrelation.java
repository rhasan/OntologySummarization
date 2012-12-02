package fr.inria.wimmics.explanation.evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.ibm.icu.text.DecimalFormat;

import fr.inria.wimmics.openrdf.util.Util;


public class SentenceRankCorrelation {
	
	public static double sentenceRankCorrelation(String filePath) throws Exception {
		List<Entry> list1 = new ArrayList<Entry>();
		List<Entry> list2 = new ArrayList<Entry>();
		
		Scanner in = new Scanner(new File(filePath));
//		while(in.hasNext()) {
			
			//start list1
			while(in.hasNext()) {
				String name = in.next();
				
				if(name.startsWith("#")) {
					in.nextLine();
					continue;
				}
				String rankStr = in.next();
				if(name.equals("0") && rankStr.equals("0")) {
					break;
				}
				Entry e = new Entry();
				e.setName(name);
				e.setRank(Integer.valueOf(rankStr));
				list1.add(e);
			}

			
			//start list2
			while(in.hasNext()) {
				String name = in.next();
				if(name.startsWith("#")) {
					in.nextLine();
					continue;
				}

				String rankStr = in.next();
				if(name.equals("0") && rankStr.equals("0")) {
					break;
				}
				Entry e = new Entry();
				e.setName(name);
				e.setRank(Integer.valueOf(rankStr));
				list2.add(e);
				
			}
//
//		}
		double tau = KendallTauCalculator.computeKendallTau(list1, list2);
		
		return tau;
		
	}
	

	public static void main(String[] args) throws Exception {
		double tau = sentenceRankCorrelation("files/degree-vs-sim.txt");
		
		System.out.println("Degree vs Similarity: "+Util.round(tau));
		
		tau = sentenceRankCorrelation("files/sim-vs-rerank.txt");
		System.out.println("Similarity vs Rerank: "+Util.round(tau));		
		
		tau = sentenceRankCorrelation("files/degree-vs-rerank.txt");
		System.out.println("Degree vs Rerank: "+Util.round(tau));
		
	}
}
