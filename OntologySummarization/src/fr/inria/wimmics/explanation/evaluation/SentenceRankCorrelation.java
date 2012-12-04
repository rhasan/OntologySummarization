package fr.inria.wimmics.explanation.evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.ibm.icu.text.DecimalFormat;

import fr.inria.wimmics.util.Util;


public class SentenceRankCorrelation {
	
	public static double sentenceRankCorrelation(String filePath1, String filePath2) throws Exception {
		List<Entry> list1 = new ArrayList<Entry>();
		List<Entry> list2 = new ArrayList<Entry>();
		
		Scanner in = new Scanner(new File(filePath1));
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

			Scanner in1 = new Scanner(new File(filePath2));
			//start list2
			while(in1.hasNext()) {
				String name = in1.next();
				if(name.startsWith("#")) {
					in1.nextLine();
					continue;
				}

				String rankStr = in1.next();
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
		
		
		in1.close();
		in.close();
		return tau;
	}
	

	public static void main(String[] args) throws Exception {
		double tau = sentenceRankCorrelation("files/degree.txt","files/similarity.txt");
		
		System.out.println("Degree vs Similarity: "+Util.round(tau));
		
		tau = sentenceRankCorrelation("files/similarity.txt","files/rerank.txt");
		System.out.println("Similarity vs Rerank: "+Util.round(tau));		
		
		tau = sentenceRankCorrelation("files/degree.txt","files/rerank.txt");
		System.out.println("Degree vs Rerank: "+Util.round(tau));
		
	}
}
