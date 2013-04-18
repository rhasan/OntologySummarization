package fr.inria.wimmics.explanation.evaluation.iswc2013;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.xy.XYSeries;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import fr.inria.wimmics.explanation.KnowledgeStatement;
import fr.inria.wimmics.explanation.evaluation.CosineSimilarity;
import fr.inria.wimmics.explanation.evaluation.DCGMeasure;
import fr.inria.wimmics.explanation.evaluation.PrecisionRecall;
import fr.inria.wimmics.explanation.evaluation.PrecisionRecallCalculator;
import fr.inria.wimmics.explanation.evaluation.RankEntry;
import fr.inria.wimmics.explanation.evaluation.test.DCGSurveyEntryProcessor;








import fr.inria.wimmics.util.Util;

public class TestCaseEvaluator {
	
	double MAX_RATING = 10.0;
	
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
		
		etcResult = new EvaluationTestCaseResult();
		
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
		
		etcResult.setCr_values(cr_values);
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
			//System.out.println("P_{"+(i+1)+"} agv:"+Util.round(iAvgAgreement));
			
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
		
		//System.out.println("Avg cosine similarity (new):"+Util.round(totalAvgCosine));
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
			//System.out.println("P_{"+(i+1)+"} agv:"+Util.round(iAvgAgreement));
		}
		
		double totalAvgCosine = cosSimSum/totalValueCount;
		etcResult.setAvgCosineSimilarityQuestion2(totalAvgCosine);
				
		//double stdDev = Statistics.standardDeviation(avgCosine, cosineSimValues);
		
		//System.out.println("Avg cosine similarity  (with concept similarity)(new):"+Util.round(totalAvgCosine));
		//System.out.println("Std dev:"+stdDev);
		
		

	}
	/**
	 * test summary with salience measure 
	 * @throws Exception
	 */
	public void test_salience() throws Exception {
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);			
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = SummarizationWrapper.summarySoloCentrality(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);
		List<RankEntry> sList = new ArrayList<RankEntry>();
		
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = SummarizationWrapper.getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		List<Double> ndcgValues = new ArrayList<Double>();
		computeNDCGMeasure(reList, sList, ndcgValues);
		//System.out.println("S_{SL} ndcg size:"+ndcgValues.size());
		
		String key = "S_{SL}";
		etcResult.recordNdcgValues(key, ndcgValues);
		
		List<Double> fMeasures = new ArrayList<Double>();
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		computeFmeasure(reList, sList, fMeasures,th);
		etcResult.recordfmeasureValues(key, fMeasures);
	}
	
	public void test_salience_coherence() throws Exception {
		
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);	
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);
		
		List<KnowledgeStatement> kstmts = SummarizationWrapper.summarySalientReRank(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);
		List<RankEntry> sList = new ArrayList<RankEntry>();
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = SummarizationWrapper.getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		List<Double> ndcgValues = new ArrayList<Double>();
		
		computeNDCGMeasure(reList, sList, ndcgValues);
		String key = "S_{SL}+S_{CO}";
		etcResult.recordNdcgValues(key, ndcgValues);	
		
		List<Double> fMeasures = new ArrayList<Double>();
		computeFmeasure(reList, sList, fMeasures,th);
		etcResult.recordfmeasureValues(key, fMeasures);
	}
	
	/**
	 * sentence graph
	 * @throws RDFParseException
	 * @throws RDFHandlerException
	 * @throws RepositoryException
	 * @throws IOException
	 */
	public void testSentenceGraph() throws RDFParseException, RDFHandlerException, RepositoryException, IOException {
		
	
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);		
		List<RankEntry> reList1 = surveyProcessor.getAvgRankEntities(questionName);
		
		List<RankEntry> reList2 = SummarizationWrapper.summarySentenceGraph(FILE_JUSTIFICATION_INF, ROOT_STATEMENT,SG_Nagivational_p);
		
		List<Double> ndcgValues = new ArrayList<Double>();

		computeNDCGMeasure(reList1, reList2, ndcgValues);	
		
		String key = "This is the sentence graph S_{SG}";
		etcResult.recordNdcgValues(key, ndcgValues);	
		
		List<Double> fMeasures = new ArrayList<Double>();	
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList1,cmp);
		
		
		computeFmeasure(reList1, reList2, fMeasures,th);
		etcResult.recordfmeasureValues(key, fMeasures);
		
	
		
	}
	
	/**
	 * salience+abstraction
	 * @throws Exception
	 */
	
	public void test_salience_abstraction() throws Exception {
		
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);			
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = SummarizationWrapper.summaryBySalientProofTreeAbstraction(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = SummarizationWrapper.getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		List<Double> ndcgValues = new ArrayList<Double>();
		computeNDCGMeasure(reList, sList, ndcgValues);	
		String key = "S_{SL}+S_{AB}";
		etcResult.recordNdcgValues(key, ndcgValues);	
		
		List<Double> fMeasures = new ArrayList<Double>();	
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		
		computeFmeasure(reList, sList, fMeasures,th);
		etcResult.recordfmeasureValues(key, fMeasures);
	}	
	
	
	/**
	 * salience+subtree weight 
	 * @throws Exception
	 */
	public void test_salience_subtreeWeight() throws Exception {
		
	
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = SummarizationWrapper.summaryBySalientAndProofTreeSubtreeWeight(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);
		List<RankEntry> sList = new ArrayList<RankEntry>();
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = SummarizationWrapper.getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		List<Double> ndcgValues = new ArrayList<Double>();

		computeNDCGMeasure(reList, sList, ndcgValues);		
		
		
		String key = "S_{SL}+S_{ST}";
		etcResult.recordNdcgValues(key, ndcgValues);	
		List<Double> fMeasures = new ArrayList<Double>();	
		
		
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);
		computeFmeasure(reList, sList, fMeasures,th);
		
		etcResult.recordfmeasureValues(key, fMeasures);

	}
	
	/**
	 * salience+abstraction+subtree weight
	 * @throws Exception
	 */
	public void test_salience_abstraction_subtreeWeight() throws Exception {
		
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);			
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = SummarizationWrapper.summaryBySalientAbstractSubtree(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = SummarizationWrapper.getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		
		List<Double> ndcgValues = new ArrayList<Double>();

		computeNDCGMeasure(reList, sList, ndcgValues);		
		
		
		String key = "S_{SL}+S_{AB}+S_{ST}";
		etcResult.recordNdcgValues(key, ndcgValues);	
		List<Double> fMeasures = new ArrayList<Double>();	
		
		
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);
		computeFmeasure(reList, sList, fMeasures,th);
		
		etcResult.recordfmeasureValues(key, fMeasures);
	}	

	/**
	 * salience+abstraction+subtree weight+coherence
	 * @throws Exception
	 */
	public void test_salience_abstraction_subtreeWeight_coherence() throws Exception {
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);		
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts =  SummarizationWrapper.summaryBySalientAbstractionSubtreeReRank(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name =  SummarizationWrapper.getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		
		List<Double> ndcgValues = new ArrayList<Double>();

		computeNDCGMeasure(reList, sList, ndcgValues);		
		
		
		String key = "S_{SL}+S_{AB}+S_{ST}+S_{CO}";
		etcResult.recordNdcgValues(key, ndcgValues);	
		List<Double> fMeasures = new ArrayList<Double>();	
		
		
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);
		computeFmeasure(reList, sList, fMeasures,th);
		
		etcResult.recordfmeasureValues(key, fMeasures);
	}
	
	/**
	 * salience+abstraction+coherence
	 * @throws Exception
	 */
	public void test_salience_abstraction_coherence() throws Exception {
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);		
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = SummarizationWrapper.summaryBySalientAbstractionReRank(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = SummarizationWrapper.getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		List<Double> ndcgValues = new ArrayList<Double>();

		computeNDCGMeasure(reList, sList, ndcgValues);		
		
		
		String key = "S_{SL}+S_{AB}+S_{CO}";
		etcResult.recordNdcgValues(key, ndcgValues);	
		List<Double> fMeasures = new ArrayList<Double>();	
		
		
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);
		computeFmeasure(reList, sList, fMeasures,th);
		
		etcResult.recordfmeasureValues(key, fMeasures);
	}	
	
	/**
	 * salience+subtree weight+coherence
	 * @throws Exception
	 */
	
	public void test_salience_subtreeWeight_coherence() throws Exception {
		
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = SummarizationWrapper.summaryBySalientSubtreeReRank(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);
		List<RankEntry> sList = new ArrayList<RankEntry>();
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = SummarizationWrapper.getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		
		List<Double> ndcgValues = new ArrayList<Double>();

		computeNDCGMeasure(reList, sList, ndcgValues);		
		
		
		String key = "S_{SL}+S_{ST}+S_{CO}";
		etcResult.recordNdcgValues(key, ndcgValues);	
		List<Double> fMeasures = new ArrayList<Double>();	
		
		
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);
		computeFmeasure(reList, sList, fMeasures,th);
		
		etcResult.recordfmeasureValues(key, fMeasures);
	}	

	
	//with similarity measures below
	
	/**
	 * salience + similarity
	 * @throws Exception
	 */
	public void test_salience_similarity() throws Exception {
		
		String questionName = QUESTION2_NAME;
		double th = getAvgGroundTruthRating(questionName);			
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		
		
		//List<String> prefs = new ArrayList<String>();
		
		
		List<KnowledgeStatement> kstmts = SummarizationWrapper.summarySimCentrality(FILE_JUSTIFICATION_INF, ROOT_STATEMENT, similarityConceptList, ontologyLocationList, instanceLocationList);
		List<RankEntry> sList = new ArrayList<RankEntry>();
		
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = SummarizationWrapper.getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		List<Double> ndcgValues = new ArrayList<Double>();
		
		computeNDCGMeasure(reList, sList, ndcgValues);
		String key = "S_{SL}+S_{SM}";
		etcResult.recordNdcgValuesWithSimilarity(key, ndcgValues);
		
		List<Double> fMeasures = new ArrayList<Double>();
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		computeFmeasure(reList, sList, fMeasures,th);
		etcResult.recordfmeasureValuesWithSimilarity(key, fMeasures);
	}	
	
	
	
	/**
	 *	salience+similarity+coherence 
	 * @throws Exception
	 */

	public void test_salience_similarity_coherence() throws Exception {
		
		String questionName = QUESTION2_NAME;
		double th = getAvgGroundTruthRating(questionName);	
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);
		
		List<KnowledgeStatement> kstmts = SummarizationWrapper.summarySimSalientReRank(FILE_JUSTIFICATION_INF, ROOT_STATEMENT,similarityConceptList,ontologyLocationList,instanceLocationList);
		List<RankEntry> sList = new ArrayList<RankEntry>();
		
	

		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = SummarizationWrapper.getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		List<Double> ndcgValues = new ArrayList<Double>();
		
		computeNDCGMeasure(reList, sList, ndcgValues);
		String key = "S_{SL}+S_{SM}+S_{CO}";
		etcResult.recordNdcgValuesWithSimilarity(key, ndcgValues);
		
		List<Double> fMeasures = new ArrayList<Double>();
		
		computeFmeasure(reList, sList, fMeasures,th);
		etcResult.recordfmeasureValuesWithSimilarity(key, fMeasures);

	}	
	

	/**
	 * salience+abstraction+similarity
	 * @throws Exception
	 */
	
	public void test_salience_abstraction_similarity() throws Exception {
		
			
		String questionName = QUESTION2_NAME;
		double th = getAvgGroundTruthRating(questionName);		
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = SummarizationWrapper.summaryBySalientProofTreeAbstractionWithSimilarity(FILE_JUSTIFICATION_INF, ROOT_STATEMENT, similarityConceptList, ontologyLocationList, instanceLocationList);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = SummarizationWrapper.getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		List<Double> ndcgValues = new ArrayList<Double>();
		
		computeNDCGMeasure(reList, sList, ndcgValues);	
		String key = "S_{SL}+S_{AB}+S_{SM}";
		etcResult.recordNdcgValuesWithSimilarity(key, ndcgValues);
		
		List<Double> fMeasures = new ArrayList<Double>();
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		
		computeFmeasure(reList, sList, fMeasures,th);
		etcResult.recordfmeasureValuesWithSimilarity(key, fMeasures);
	}
	
	/**
	 * salience+similarity+subtree weight
	 * @throws Exception
	 */
	public void test_salience_similarity_subtreeWeight() throws Exception {
		
		
		String questionName = QUESTION2_NAME;
		double th = getAvgGroundTruthRating(questionName);
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = SummarizationWrapper.summaryBySalientAndProofTreeSubtreeWeightWithSimilarity(FILE_JUSTIFICATION_INF, ROOT_STATEMENT, similarityConceptList, ontologyLocationList, instanceLocationList);
		List<RankEntry> sList = new ArrayList<RankEntry>();
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = SummarizationWrapper.getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		List<Double> ndcgValues = new ArrayList<Double>();
		
		computeNDCGMeasure(reList, sList, ndcgValues);	
		String key = "S_{SL}+S_{SM}+S_{ST}";
		etcResult.recordNdcgValuesWithSimilarity(key, ndcgValues);
		
		List<Double> fMeasures = new ArrayList<Double>();
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		
		computeFmeasure(reList, sList, fMeasures,th);
		etcResult.recordfmeasureValuesWithSimilarity(key, fMeasures);
	}		
	
	/**
	 * salience+abstraction+similarity+subtree weight
	 * @throws Exception
	 */
	public void test_salience_abstraction_similarity_subtreeWeight() throws Exception {
		
		
		String questionName = QUESTION2_NAME;
		double th = getAvgGroundTruthRating(questionName);		
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = SummarizationWrapper.summaryBySalientAbstractionSimilaritySubtreeWeight(FILE_JUSTIFICATION_INF, ROOT_STATEMENT, similarityConceptList, ontologyLocationList, instanceLocationList);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = SummarizationWrapper.getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		List<Double> ndcgValues = new ArrayList<Double>();
		
		computeNDCGMeasure(reList, sList, ndcgValues);	
		String key = "S_{SL}+S_{AB}+S_{SM}+S_{ST}";
		etcResult.recordNdcgValuesWithSimilarity(key, ndcgValues);
		
		List<Double> fMeasures = new ArrayList<Double>();
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		
		computeFmeasure(reList, sList, fMeasures,th);
		etcResult.recordfmeasureValuesWithSimilarity(key, fMeasures);
	}	
	
	/**
	 * salience+abstraction+similarity+subtree weight+coherence
	 * @throws Exception
	 */
	public void test_salience_abstraction_similarity_subtreeWeight_coherence() throws Exception {
		
		
		String questionName = QUESTION2_NAME;
		double th = getAvgGroundTruthRating(questionName);		
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = SummarizationWrapper.summaryBySalientAbstractionSimilaritySubtreeWeightReRank(FILE_JUSTIFICATION_INF, ROOT_STATEMENT, similarityConceptList, ontologyLocationList, instanceLocationList);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = SummarizationWrapper.getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		List<Double> ndcgValues = new ArrayList<Double>();
		
		computeNDCGMeasure(reList, sList, ndcgValues);	
		String key = "S_{SL}+S_{AB}+S_{SM}+S_{ST}+S_{CO}";
		etcResult.recordNdcgValuesWithSimilarity(key, ndcgValues);
		
		List<Double> fMeasures = new ArrayList<Double>();
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		
		computeFmeasure(reList, sList, fMeasures,th);
		etcResult.recordfmeasureValuesWithSimilarity(key, fMeasures);
	}
	
	/**
	 * salience+abstraction+similarity+coherence
	 * @throws Exception
	 */
	public void test_salience_abstraction_similarity_coherence() throws Exception {
		
		
		String questionName = QUESTION2_NAME;
		double th = getAvgGroundTruthRating(questionName);		
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = SummarizationWrapper.summaryBySalientAbstractionSimilarityReRank(FILE_JUSTIFICATION_INF, ROOT_STATEMENT, similarityConceptList, ontologyLocationList, instanceLocationList);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = SummarizationWrapper.getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		List<Double> ndcgValues = new ArrayList<Double>();
		
		computeNDCGMeasure(reList, sList, ndcgValues);	
		String key = "S_{SL}+S_{AB}+S_{SM}+S_{CO}";
		etcResult.recordNdcgValuesWithSimilarity(key, ndcgValues);
		
		List<Double> fMeasures = new ArrayList<Double>();
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		
		computeFmeasure(reList, sList, fMeasures,th);
		etcResult.recordfmeasureValuesWithSimilarity(key, fMeasures);
	}

	/**
	 * salience+similarity+subtree weight+coherence
	 * @throws Exception
	 */
	public void testSalientSubtreeSimilarityReRank() throws Exception {
		
		
		String questionName = QUESTION2_NAME;
		double th = getAvgGroundTruthRating(questionName);		
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = SummarizationWrapper.summaryBySalientSubtreeSimilarityReRank(FILE_JUSTIFICATION_INF, ROOT_STATEMENT, similarityConceptList, ontologyLocationList, instanceLocationList);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = SummarizationWrapper.getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		
		List<Double> ndcgValues = new ArrayList<Double>();
		
		computeNDCGMeasure(reList, sList, ndcgValues);	
		String key = "S_{SL}+S_{SM}+S_{ST}+S_{CO}";
		etcResult.recordNdcgValuesWithSimilarity(key, ndcgValues);
		
		List<Double> fMeasures = new ArrayList<Double>();
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		
		computeFmeasure(reList, sList, fMeasures,th);
		etcResult.recordfmeasureValuesWithSimilarity(key, fMeasures);
	}	
	



	//evaluation measure related below
	
	
	public void computeFmeasure(List<RankEntry> groundTruthList, List<RankEntry> summaryToCompareList,List<Double> fMeasures , double th) {
		//Map<Double, Double> res = new HashMap<Double, Double>();
		//System.out.println("Ground truth:"+th);
		List<String> groundTruth = getSummaryByThresholdRating(groundTruthList, th);		
		int n = summaryToCompareList.size();
		for(int ci=0;ci<cr_values.length;ci++) {
			double cr = cr_values[ci];
			int p = (int) (cr * n);
			
			//List<String> groundTruth = getSummaryOfSizeP(groundTruthList, p);

			//System.out.println("Summary");
			List<String> summaryToCompare = getSummaryOfSizeP(summaryToCompareList, p);
			PrecisionRecall pr = PrecisionRecallCalculator.calculatePrecisionRecall(groundTruth,summaryToCompare);
			double precision = pr.getPrecision();
			double recall = pr.getRecall();
			Double f_measure = (2*precision*recall)/(precision+recall);
			if(f_measure.isNaN()) {
				f_measure = 0.0;
				//System.out.println("*********************************");
			}
			//double cr = (double) p / (double) groundTruthList.size();
			//System.out.println("Precision:"+precision);
			//System.out.println("Recall:"+recall);
			//System.out.println("F:"+f_measure+" CR:"+cr+" p:"+p);
			//System.out.println("Precision:"+precision+" Recall:"+recall);
			fMeasures.add(f_measure);
			//res.put(cr, f_measure);
		}

	}	
	
	public void computeNDCGMeasure(List<RankEntry> reList, List<RankEntry> sList, List<Double> ndcg) {
		
		int n = reList.size();
		for(int ci=0;ci<cr_values.length;ci++) {
			double cr = cr_values[ci];
			int p = (int) (cr * n);
			double d = DCGMeasure.computeNDCG(reList, sList, p);
			//double cr = (double) p / (double) reList.size();
			//System.out.println("NCG["+p+"]:"+d+ " CR:"+cr);
			ndcg.add(d);			
		}		
	}	
	
	//for scaled summary and uncalled summary modify in this method
	public List<String> getSummaryByThresholdRating(List<RankEntry> reList2, double th) {
		
		List<RankEntry> reList1 = new ArrayList<RankEntry>(reList2);
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList1,cmp);
		
		double max = reList1.get(0).getJudgmentScore();
		
		List<String> res = new ArrayList<String>();
		for(int i=0;i<reList1.size();i++) {
			//double scaled = reList1.get(i).getJudgmentScore();
			double scalled = (reList1.get(i).getJudgmentScore() / max) * MAX_RATING;
			if( scalled >=th) {
				RankEntry re = reList1.get(i);
				res.add(re.getName());
				//System.out.println(re.getName());
			}
		}
		return res;
	}
	
	public List<String> getSummaryOfSizeP(List<RankEntry> reList, int p) {
		List<String> res = new ArrayList<String>();
		for(int i=0;i<p;i++) {
			RankEntry re = reList.get(i);
			res.add(re.getName());
			//System.out.println(re.getName());
		}
		return res;
	}
	

	
	class EntryJudgmentDscCmp implements Comparator<RankEntry> {
		
		@Override
		public int compare(RankEntry o1, RankEntry o2) {
			
			if(o2.getJudgmentScore()>o1.getJudgmentScore()) return 1;
			if(o2.getJudgmentScore()<o1.getJudgmentScore()) return -1;
			return o1.getName().compareTo(o2.getName());
		}
		
	}
	
	
	public EvaluationTestCaseResult evaluate() throws Exception {
		setProfileInfo();
		
		testHumanAgreementQuestion1();
		testHumanAgreementQuestion2();
		
		
		test_salience();
		test_salience_coherence();
		test_salience_abstraction();
		test_salience_subtreeWeight();
		test_salience_abstraction_subtreeWeight();
		test_salience_abstraction_subtreeWeight_coherence();
		test_salience_abstraction_coherence();
		test_salience_subtreeWeight_coherence();
		
		testSentenceGraph();
		
		//with similarity
		test_salience_similarity();
		test_salience_similarity_coherence();
		test_salience_abstraction_similarity();
		test_salience_similarity_subtreeWeight();
		test_salience_abstraction_similarity_subtreeWeight();
		test_salience_abstraction_similarity_subtreeWeight_coherence();
		test_salience_abstraction_similarity_coherence();
		testSalientSubtreeSimilarityReRank();
		
		
		
		return etcResult;
	}	
}
