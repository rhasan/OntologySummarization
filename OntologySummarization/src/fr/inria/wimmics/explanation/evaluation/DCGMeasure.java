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
	 * format in the file: entry_name judgement_score
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
			if(name.equals("0") && judgementScoreStr.equals("0.0")) {
				break;
			}
			RankEntry e = new RankEntry();
			e.setName(name);
			e.setJudgmentScore(Double.valueOf(judgementScoreStr));
			list1.add(e);
		}
		in.close();
		return list1;
		
	}
	/**
	 * reads a list of ranked entries 
	 * format in the file: entry_name
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	
	public static List<RankEntry> readSentenceRank(String filePath) throws Exception {
		List<RankEntry> list1 = new ArrayList<RankEntry>();
		
		Scanner in = new Scanner(new File(filePath));
			
		//start list1
		while(in.hasNext()) {
			String name = in.next();
			
			if(name.startsWith("#")) {
				in.nextLine();
				continue;
			}
			RankEntry e = new RankEntry();
			e.setName(name);
			list1.add(e);
		}
		in.close();
		return list1;		
	}
	/**
	 * computes DCG measure up to position p.
	 * works with a list of entries with judgment scores
	 * http://en.wikipedia.org/wiki/Discounted_cumulative_gain#Example
	 * @param list
	 * @param p
	 * @return
	 */
	public static double computeDCG(List<RankEntry> list, int p) {
		if(p==0) return 0;
		//assert(p>=1);
		
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
	 * the maximum possible DCG till position , also called Ideal DCG (IDCG) till that position.
	 * works with a list of entries with judgment scores
	 * @param a list of entries with judgment scores
	 * @param p
	 * @return
	 */
	public static double computeIDCG(List<RankEntry> list, int p) {
		List<RankEntry> nList = new ArrayList<RankEntry>(list); //to avoid sorting the original array
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(nList, cmp);
		
		double idcg = computeDCG(nList, p);
		
		return idcg;
	}
	
	/**
	 * computes Normalized DCG for a list of entries with judgment scores
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
	/**
	 * computes Normalized DCG for a list of ranked entries taking into account the judgment scores by humans
	 * @param rakedListByHuman List of entries with judgment score assigned to each entry
	 * @param rakedListByAlgorithm List of ranked entries (without judgment scores)
	 * @param p rank position (i.e. NDCG will be computed for top p entries in rakedListByAlgorithm)
	 * @return normalized discounted cumulative gain (nDCG) value
	 */
	public static double computeNDCG(List<RankEntry> rakedListByHuman, List<RankEntry> rakedListByAlgorithm, int p) {
		
		
		List<RankEntry> rakedListByHuman1 = copyRankEntryList(rakedListByHuman);
		List<RankEntry> rakedListByAlgorithm1 = copyRankEntryList(rakedListByAlgorithm);
		//EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		//Collections.sort(rakedListByHuman1,cmp);
		
		for(RankEntry reH:rakedListByHuman1) {
			for(RankEntry reA:rakedListByAlgorithm1) {
				if(reH.getName().equals(reA.getName())) {
					reA.setJudgmentScore(reH.getJudgmentScore());
				}
			}
		}
		return computeNDCG(rakedListByAlgorithm1, p);
	}

	/**
	 * copies a list of RankEntry without referencing
	 * @param reList
	 * @return
	 */
	public static List<RankEntry> copyRankEntryList(List<RankEntry> reList) {
		List<RankEntry> res = new ArrayList<RankEntry>();
		for(RankEntry re:reList) {
			RankEntry ren = new RankEntry(re);
			res.add(ren);
		}
		return res;
	}
	
	/**
	 * computes Normalized DCG for a list of ranked entries described in a file
	 * taking into account the judgment scores by humans described in another file
	 * @param humanEntriesFile file path of the list of entries with judgment score assigned to each entry
	 * @param algorithmEntriesFile file path of the list of ranked entries (without judgment scores)
	 * @param p rakedListByAlgorithm
	 * @return normalized discounted cumulative gain (nDCG) value
	 * @throws Exception
	 */
	public static double computeNDCG(String humanEntriesFile, String algorithmEntriesFile, int p) throws Exception {
		List<RankEntry> list = readSentenceDCGJudgments(humanEntriesFile);
		List<RankEntry> list1 = readSentenceRank(algorithmEntriesFile);
		return computeNDCG(list, list1, p);
	}
	
	public static void main(String[] args) throws Exception {
		
		/*
		List<RankEntry> list = readSentenceDCGJudgments("files/evaluation/dcg/dcg-test.in");
		double o = computeNDCG(list, 6);
		System.out.println("nDCG₆:"+o);
		*/
		double o = computeNDCG("files/evaluation/dcg/dcg-test.in", "files/evaluation/dcg/dcg-algo-test.in", 6);
		System.out.println("nDCG₆:"+o);
	}
}
