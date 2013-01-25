package fr.inria.wimmics.explanation.evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Discounted cumulative gain
 * http://en.wikipedia.org/wiki/Discounted_cumulative_gain
 * @author rakebul
 *
 */
public class DCGMeasure {

	/**
	 * reads dcg judgment scores 
	 * format in the file: name judgement_score
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	
	public static List<RankEntry> readSentenceDCGJudgments(String filePath) throws Exception {
		List<RankEntry> list1 = new ArrayList<RankEntry>();
	
		Scanner in = new Scanner(new File(filePath));
			
		//start list1
		while(in.hasNext()) {
			String name = in.next();
			
			if(name.startsWith("#")) {
				in.nextLine();
				continue;
			}
			String judgementScoreStr = in.next();
			if(name.equals("0") && judgementScoreStr.equals("0")) {
				break;
			}
			RankEntry e = new RankEntry();
			e.setName(name);
			e.setJudgmentScore(Integer.valueOf(judgementScoreStr));
			list1.add(e);
		}
		in.close();
		return list1;
		
	}
	
	/**
	 * computes DCG measure up to position p
	 * http://en.wikipedia.org/wiki/Discounted_cumulative_gain#Example
	 * @param list
	 * @param p
	 * @return
	 */
	public static double computeDCG(List<RankEntry> list, int p) {
		assert(p>=1);
		
		double dcg = list.get(0).getJudgmentScore();
		
		for(int i=2;i<=p;i++) {
			int index = i - 1;
			double log_base_2 = Math.log(i)/Math.log(2);
			double rel_i = list.get(index).getJudgmentScore();
			dcg += (rel_i/log_base_2);

		}
		
		return dcg;
	}
	
	/**
	 * the maximum possible DCG till position , also called Ideal DCG (IDCG) till that position
	 * @param list
	 * @param p
	 * @return
	 */
	public static double computeIDCG(List<RankEntry> list, int p) {
		List<RankEntry> nList = new ArrayList<RankEntry>(list);
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(nList, cmp);
		
		double idcg = computeDCG(nList, p);
		
		return idcg;
	}
	
	/**
	 * computes Normalized DCG
	 * @param list
	 * @param p
	 * @return
	 */
	
	public static double computeNDCG(List<RankEntry> list, int p) {
		double dcg = computeDCG(list, p);
		double idcg = computeIDCG(list, p);
		
		double ndcg = dcg/idcg;
		
		return ndcg;
	}
	
	public static void main(String[] args) throws Exception {
		
		List<RankEntry> list = readSentenceDCGJudgments("files/evaluation/dcg/dcg-test.in");
		double o = computeNDCG(list, 6);
		System.out.println("nDCG₆:"+o);
	}
}
