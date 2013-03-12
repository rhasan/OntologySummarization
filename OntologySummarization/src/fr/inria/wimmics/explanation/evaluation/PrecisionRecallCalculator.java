package fr.inria.wimmics.explanation.evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PrecisionRecallCalculator {
	
	
//	public static PrecisionRecall calculatePrecisionRecall(List<String> S, List<String> H) {
//		
//		int overlap = 0;
//		for(String s:S) {
//			for(String h:H) {
//				if(s.equals(h)) {
//					overlap++;
//				}
//			}
//		}
//		double recall = (double) overlap/ (double) H.size();
//		double precision = (double) overlap/ (double) S.size();
//		PrecisionRecall res = new PrecisionRecall(precision, recall);
//		
//		return res;
//	}
	
	public static PrecisionRecall calculatePrecisionRecall(List<String> S, List<String> H) {
		
		double correct = 0;
		double missed = 0;
		double wrong = 0;
		
		for(String s:S) {
			for(String h:H) {
				if(s.equals(h)) {
					correct++;
				}
			}
		}
		
		for(String s:S) {
			boolean humanExtracted = false;
			for(String h:H) {
				if(s.equals(h)) {
					humanExtracted = true;
					break;
				}
			}
			if(humanExtracted==false) {
				wrong++;
			}
			
		}
		
		for(String h:H) {
			boolean systemExtracted = false;
			for(String s:S) {
				if(h.equals(s)) {
					systemExtracted = true;
					break;
				}
			}
			if(systemExtracted==false) {
				missed++;
			}
		}
		
		double recall = correct/ (correct + missed);
		double precision = correct/ (correct + wrong);;
		PrecisionRecall res = new PrecisionRecall(precision, recall);
		
		return res;
	}
	
	public static PrecisionRecall calculatePrecisionRecall(List<String> S, String filePath) throws FileNotFoundException {
		
		List<String> H = new ArrayList<String>();
		Scanner in = new Scanner(new File(filePath));
		while(in.hasNext()) {
			String str = in.next();
			H.add(str.trim());
		}
		in.close();
		return calculatePrecisionRecall(S, H);
	}
}
