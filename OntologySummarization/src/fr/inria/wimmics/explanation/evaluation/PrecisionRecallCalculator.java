package fr.inria.wimmics.explanation.evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PrecisionRecallCalculator {
	
	
	public static PrecisionRecall calculatePrecisionRecall(List<String> S, List<String> H) {
		
		int overlap = 0;
		for(String s:S) {
			for(String h:H) {
				if(s.equals(h)) {
					overlap++;
				}
			}
		}
		double recall = (double) overlap/ (double) H.size();
		double precision = (double) overlap/ (double) S.size();
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
