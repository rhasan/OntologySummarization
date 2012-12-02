package fr.inria.wimmics.explanation.evaluation;

import java.util.Collections;
import java.util.List;


public class KendallTauCalculator {
	

	public static int find(String name, List<Entry> entryList) {
		
		for(Entry e:entryList) {
			if(e.getName().equals(name)) 
				return e.getRank();
		}
		return -1;
		
	}
	
	public static double computeKendallTau(List<Entry> entryList1, List<Entry> entryList2) throws Exception {
		int[] concordant = new int[entryList1.size()];
		int[] disconcordant = new int[entryList1.size()];
		
		EntryCmp cmp = new EntryCmp();
		Collections.sort(entryList1, cmp);
		Collections.sort(entryList2, cmp);
		
		for(Entry entry:entryList2) {
			int oRank = find(entry.getName(),entryList1);
			if(oRank<0) throw new Exception("Check whether both your datasets contains entries about the same entities");
			entry.setOtherRank(oRank);
			
		}
		
		for(int i=0;i<entryList2.size();i++) {
			Entry entry1 = entryList2.get(i);
			int c = 0;
			int d = 0;
			for(int j=i+1;j<entryList2.size();j++) {
				Entry entry2 = entryList2.get(j);
				if(entry2.getOtherRank()>entry1.getOtherRank()) {
					c++;
				}
				else if(entry2.getOtherRank()<entry1.getOtherRank()) {
					d++;
				}
			}
			concordant[i] = c;
			disconcordant[i] = d;
			//System.out.println("Rank: "+entry1.getRank()+" Other:"+entry1.getOtherRank() + " C:" +concordant[i]+ " D:"+ disconcordant[i]);
			
		}
		
		int C = 0;
		int D = 0;
		for(int i=0;i<concordant.length;i++) {
			C += concordant[i];
			D += disconcordant[i];
		}
		
		double tau = (double)(C-D)/(double)(C+D);
		
		
		return tau;
	}
}
