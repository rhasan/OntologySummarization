package fr.inria.wimmics.explanation.evaluation.test;



import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inria.wimmics.explanation.evaluation.CosineSimilarity;
import fr.inria.wimmics.explanation.evaluation.DCGMeasure;

import fr.inria.wimmics.explanation.evaluation.RankEntry;


public class DCGSurveyTest {
	//files/evaluation/dcg/survey/inf1/results-survey31573.csv
	
	static String QUESTION1_NAME = "q1";
	static String QUESTION2_NAME = "q2";//context question
	static DCGSurveyEntryProcessor en = new DCGSurveyEntryProcessor();
	static String filePath = "files/evaluation/dcg/survey/inf1/results-survey31573.csv";
	

	@BeforeClass
	public static void init() throws IOException {
		en.setValues(filePath);
	}
	
	@Test
	public void testCSVInput() throws IOException {
		System.out.println("Hello there!");
		//DCGSurveyEntryProcessor en = new DCGSurveyEntryProcessor();
		//en.setValues("files/evaluation/dcg/survey/inf1/results-survey31573.csv");
		//en.printValues();
	}
	
	public void printRankEntryList(List<RankEntry> rl) {
		for(RankEntry re:rl) {
			System.out.print("\t"+re.getName());
		}
		System.out.println();
		for(RankEntry re:rl) {
			System.out.print("\t"+re.getJudgmentScore());
		}
		System.out.println();
	}
	@Test
	public void testHumanAgreementQuestion1() {
		List<List<RankEntry>> reList = en.getRankEntries(QUESTION1_NAME);
		//en.printValues();
		
		double totalCosine = 0.0;
		int pairCount = 0;
		for(int i=0;i<reList.size();i++) {
			for(int j=i+1;j<reList.size();j++) {
				int firstIndex = i;
				int secondIndex = j;
				//EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
				//Collections.sort(reList.get(firstIndex),cmp);
				
				//Collections.sort(reList.get(secondIndex),cmp);
				
				//printRankEntryList(reList.get(firstIndex));
				//printRankEntryList(reList.get(secondIndex));
				
				double cosineSim = CosineSimilarity.computeCosineSimilarity(reList.get(firstIndex), reList.get(secondIndex));
				System.out.println("Cosine similarity: "+cosineSim);
				totalCosine += cosineSim;
				
				double d = DCGMeasure.computeNDCG(reList.get(firstIndex), reList.get(secondIndex), reList.get(firstIndex).size());
				System.out.println("nDCG: "+d);
				
				
				//printRankEntryList(reList.get(firstIndex));
				//printRankEntryList(reList.get(secondIndex));	
				System.out.println();
				pairCount++;
			}
		}
		
		double avgCosine = totalCosine/pairCount;
		
		System.out.println("Avg cosine count:"+avgCosine);
		

	}
	

	class EntryJudgmentDscCmp implements Comparator<RankEntry> {
	
		@Override
		public int compare(RankEntry o1, RankEntry o2) {
			
			if(o2.getJudgmentScore()>o1.getJudgmentScore()) return 1;
			if(o2.getJudgmentScore()<o1.getJudgmentScore()) return -1;
			return 0;
		}
		
	}
}


