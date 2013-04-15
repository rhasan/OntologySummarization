package fr.inria.wimmics.explanation.evaluation.iswc2013;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.inria.wimmics.explanation.evaluation.CosineSimilarity;
import fr.inria.wimmics.explanation.evaluation.RankEntry;
import fr.inria.wimmics.explanation.evaluation.test.DCGSurveyEntryProcessor;
import fr.inria.wimmics.util.Util;

public class TestCaseEvaluator {
	
	
	String QUESTION1_NAME;
	String QUESTION2_NAME;//context question
	DCGSurveyEntryProcessor surveyProcessor = new DCGSurveyEntryProcessor();
	String filePath;
	String ROOT_STATEMENT;
	
	String FILE_JUSTIFICATION_INF;
	String FILE_JUSTIFICATION_INSTANCE;
	String SIMILARITY_CONCEPT;
	String DBPEDIA_SCHEMA_LOCATION;
	String GEONAMES_SCHEMA_LOCATION;
	double SG_Nagivational_p = 0.8;
	
	
	
	double[] cr_values = {0.000, 0.025, 0.050, 0.075, 0.100, 0.125, 0.150, 0.175, 0.200, 0.225, 0.250, 0.275, 0.300,
			0.325, 0.350, 0.375, 0.400, 0.425, 0.450, 0.475, 0.500, 0.525, 0.550, 0.575, 0.600, 0.625, 0.650, 0.675,
			0.700, 0.725, 0.750, 0.775, 0.800, 0.825, 0.850, 0.875, 0.900, 0.925, 0.950, 0.975, 1.000};
	
	List<String> instanceLocationList = new ArrayList<String>();
	List<String> similarityConceptList = new ArrayList<String>();
	List<String> ontologyLocationList = new ArrayList<String>();
	
	
	//uncomment initialization method calls in init() if the ground truth summary should be with the statements greater than avg rating 
	double AVG_GROUND_TRUTH_RATING_Q1 = 6.0;
	double AVG_GROUND_TRUTH_RATING_Q2 = 6.0;
	
	EvaluationTestCaseResult etcResult;
	
	public TestCaseEvaluator(EvaluationTestCase etc) throws IOException {
		
		QUESTION1_NAME = etc.getQuestion1Name();
		QUESTION2_NAME = etc.getQuestion2Name();
		filePath = etc.getRatingFilePath();
		ROOT_STATEMENT = etc.getRootStatement();
		
		FILE_JUSTIFICATION_INF = etc.getJustificationFilePath();
		FILE_JUSTIFICATION_INSTANCE = etc.getInstanceFilePath();
		
		SIMILARITY_CONCEPT = etc.getSimilarityConcept();
		DBPEDIA_SCHEMA_LOCATION = etc.getDbpediaSchemaLocation();
		GEONAMES_SCHEMA_LOCATION = etc.getGeonamesSchemaLocation();
		
		init();
		
		etcResult = new EvaluationTestCaseResult();
	}
	
	public void init() throws IOException {
		surveyProcessor.setValues(filePath);
		instanceLocationList.add(FILE_JUSTIFICATION_INSTANCE);
		similarityConceptList.add(SIMILARITY_CONCEPT);
		
		
		ontologyLocationList.add(DBPEDIA_SCHEMA_LOCATION);
		ontologyLocationList.add(GEONAMES_SCHEMA_LOCATION);		
		
		//uncomment these lines if the ground truth summary should be with the statements greater than avg rating 
		//AVG_GROUND_TRUTH_RATING_Q1 = avgGroundTruthRating(QUESTION1_NAME);
		//AVG_GROUND_TRUTH_RATING_Q2 = avgGroundTruthRating(QUESTION2_NAME);
	}
	
	public double getAvgGroundTruthRating(String questionName) {
		if(questionName.equals(QUESTION1_NAME))
			return AVG_GROUND_TRUTH_RATING_Q1;
		else if(questionName.equals(QUESTION2_NAME))
			return AVG_GROUND_TRUTH_RATING_Q2;
		return 0.0;
		
	}
	public double avgGroundTruthRating(String questionName) {
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		
		return avgRating(reList);
	}
	
	public static double avgRating(List<RankEntry> reList) {
		
		double sum = 0.0;
		for(RankEntry r:reList) {
			sum += r.getJudgmentScore();
		}
		return sum/reList.size();
	}
	
	public void printParticipantProfiles() {
		
		System.out.println("Profile information:");
		surveyProcessor.printValues();
	}
	
	
	/**
	 * prints a {@link List} of {@link RankEntry}
	 * @param rl
	 */
	
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
	
	/**
	 * computes cosine similarities between the rating vectors of each evaluators 
	 * (without similarity)
	 * @throws Exception 
	 */
	@Test
	public void testHumanAgreementQuestion1() throws Exception {
		List<List<RankEntry>> reList = surveyProcessor.getAllRankEntries(QUESTION1_NAME);
		//en.printValues();
		//List<Double> cosineSimValues = new ArrayList<Double>();
		//double totalCosine = 0.0;
		//int pairCount = 0;
		
		double cosSimSum = 0;
		double totalValueCount = 0;
		for(int i=0;i<reList.size();i++) {
			List<Double> iCosineSimValues = new ArrayList<Double>();
			double iCosSimSum = 0;
			int iPairCount = 0;
			for(int j=0;j<reList.size();j++) {
				
				if(i!=j) {
					double cosineSim = CosineSimilarity.computeCosineSimilarity(reList.get(i), reList.get(j));
					iCosSimSum += cosineSim;
					iPairCount++;
					
					cosSimSum += cosineSim;
					totalValueCount++;
					iCosineSimValues.add(cosineSim);
				}
			}
			double iAvgAgreement = iCosSimSum/iPairCount;
			etcResult.AddCosineSimilarityQuestion1(iAvgAgreement);
			System.out.println("P_{"+(i+1)+"} agv:"+Util.round(iAvgAgreement));
			
			//double iStdDev = Statistics.standardDeviation(iAvgAgreement, iCosineSimValues);
			//System.out.println("P_{"+(i+1)+"} agv:"+iAvgAgreement + " StdDev:"+iStdDev);
			
//			
//			for(int j=i+1;j<reList.size();j++) {
//				int firstIndex = i;
//				int secondIndex = j;
//				//EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
//				//Collections.sort(reList.get(firstIndex),cmp);
//				
//				//Collections.sort(reList.get(secondIndex),cmp);
//				
//				//printRankEntryList(reList.get(firstIndex));
//				//printRankEntryList(reList.get(secondIndex));
//				
//				double cosineSim = CosineSimilarity.computeCosineSimilarity(reList.get(firstIndex), reList.get(secondIndex));
//				//System.out.println("Cosine similarity: "+cosineSim);
//				
//				totalCosine += cosineSim;
//				cosineSimValues.add(cosineSim);
//			
//				//printRankEntryList(reList.get(firstIndex));
//				//printRankEntryList(reList.get(secondIndex));	
//				//System.out.println();
//				pairCount++;
//				cosineDataset.setValue(cosineSim, "Cosine similarity", String.valueOf(pairCount));
//			}
		}
		
//		double avgCosine = totalCosine/pairCount;
//		
//		
//		double stdDev = Statistics.standardDeviation(avgCosine, cosineSimValues);
//		
//		System.out.println("Avg cosine similarity:"+avgCosine);
//		System.out.println("Std dev:"+stdDev);
		
		double totalAvgCosine = cosSimSum/totalValueCount;
		
		etcResult.setAvgCosineSimilarityQuestion1(totalAvgCosine);
				
		//double stdDev = Statistics.standardDeviation(avgCosine, cosineSimValues);
		
		System.out.println("Avg cosine similarity (new):"+Util.round(totalAvgCosine));
		//System.out.println("Std dev:"+stdDev);
		
		

	}
	
	/**
	 * sets profile related info in the results
	 */
	
	private void setProfileInfo() {
		etcResult.setAge(surveyProcessor.getAge());
		etcResult.setGender(surveyProcessor.getGender());
		etcResult.setRdfKnowledge(surveyProcessor.getRdfKnowledge());
		etcResult.setSpecialisation(surveyProcessor.getSpecialisation());
	}
	
	/**
	 * computes cosine similarities between the rating vectors of each evaluators 
	 * (with conecept similarity)
	 * @throws Exception 
	 */
	@Test
	public void testHumanAgreementQuestion2() throws Exception {
		List<List<RankEntry>> reList = surveyProcessor.getAllRankEntries(QUESTION2_NAME);

		
		double cosSimSum = 0;
		double totalValueCount = 0;
		for(int i=0;i<reList.size();i++) {
			List<Double> iCosineSimValues = new ArrayList<Double>();
			double iCosSimSum = 0;
			int iPairCount = 0;
			for(int j=0;j<reList.size();j++) {
				
				if(i!=j) {
					double cosineSim = CosineSimilarity.computeCosineSimilarity(reList.get(i), reList.get(j));
					iCosSimSum += cosineSim;
					iPairCount++;
					
					cosSimSum += cosineSim;
					totalValueCount++;
					iCosineSimValues.add(cosineSim);
				}
			}
			double iAvgAgreement = iCosSimSum/iPairCount;
			etcResult.AddCosineSimilarityQuestion2(iAvgAgreement);
			System.out.println("P_{"+(i+1)+"} agv:"+Util.round(iAvgAgreement));
		}
		
		double totalAvgCosine = cosSimSum/totalValueCount;
		etcResult.setAvgCosineSimilarityQuestion2(totalAvgCosine);
				
		//double stdDev = Statistics.standardDeviation(avgCosine, cosineSimValues);
		
		System.out.println("Avg cosine similarity  (with concept similarity)(new):"+Util.round(totalAvgCosine));
		//System.out.println("Std dev:"+stdDev);
		
		

	}
	
	
	
	public EvaluationTestCaseResult evaluate() throws Exception {
		setProfileInfo();
		
		testHumanAgreementQuestion1();
		testHumanAgreementQuestion2();
		
		return etcResult;
	}
}
