package fr.inria.wimmics.explanation.evaluation.test;



import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import fr.inria.wimmics.explanation.JustificationProcessor;
import fr.inria.wimmics.explanation.KnowledgeStatement;
import fr.inria.wimmics.explanation.RDFSentenceGraph;
import fr.inria.wimmics.explanation.SentenceGraphSummarizer;
import fr.inria.wimmics.explanation.evaluation.CosineSimilarity;
import fr.inria.wimmics.explanation.evaluation.DCGMeasure;
import fr.inria.wimmics.explanation.evaluation.PrecisionRecall;
import fr.inria.wimmics.explanation.evaluation.PrecisionRecallCalculator;

import fr.inria.wimmics.explanation.evaluation.RankEntry;
import fr.inria.wimmics.util.Statistics;
import fr.inria.wimmics.util.Util;


public class DCGSurveyInference1Test {
	//files/evaluation/dcg/survey/inf1/results-survey31573.csv
	
	static int P_START_VALUE = 1;
	
	static String QUESTION1_NAME = "q1";
	static String QUESTION2_NAME = "q2";//context question
	static DCGSurveyEntryProcessor surveyProcessor = new DCGSurveyEntryProcessor();
	static String filePath = "files/evaluation/dcg/survey/inf1/results-survey31573.csv";
	static String ROOT_STATEMENT = "http://alphubel.unice.fr:8080/lodutil/data/d21";
	
	static String FILE_JUSTIFICATION_INF = "rdf/inference1.trig";
	static String FILE_JUSTIFICATION_INSTANCE = "rdf/inference1-instance.rdf";
	static String SIMILARITY_CONCEPT = "http://dbpedia.org/ontology/Event";
	static String DBPEDIA_SCHEMA_LOCATION = "rdf/ontology/dbpedia_3.8.owl";
	static String GEONAMES_SCHEMA_LOCATION = "rdf/ontology/geonames_ontology_v3.1.rdf";
	
	
	//Scoresalience : SSL Scoresimilarity : SSM Scoresubtree: SST ScoreAbstraction: SAB ReRank by coherence: SCO
	
//	static XYSeries soloCentralityNdcgCR = 												new XYSeries("SSL");
//	static XYSeries soloReRankNdcgCR = 													new XYSeries("SSL+SCO");
//	
//	static XYSeries soloAbstractionNdcgCR = 											new XYSeries("SSL+SAB");
//	static XYSeries salientAbstractionReRankNdcgCr = 									new XYSeries("SSL+SAB+SCO");	
//	
//	static XYSeries soloProofTreeSubtreeWeightNdcgCR = 									new XYSeries("SSL+SST");//rerank
//	static XYSeries salientSubtreeReRankNdcgCR = 										new XYSeries("SSL+SST+SCO");
//	
//	static XYSeries salienceAndAbstractAndSubtreeWeightNdcgCR = 						new XYSeries("SSL+SAB+SST");
//	static XYSeries salientAbstractionSubtreeReRankNdcgCr = 							new XYSeries("SSL+SAB+SST+SCO");
	
	static XYSeries soloCentralityNdcgCR = 												new XYSeries("salience");
	static XYSeries soloReRankNdcgCR = 													new XYSeries("salience+coherence");
	
	static XYSeries soloAbstractionNdcgCR = 											new XYSeries("salience+abstraction");
	static XYSeries salientAbstractionReRankNdcgCr = 									new XYSeries("salience+abstraction+coherence");	
	
	static XYSeries soloProofTreeSubtreeWeightNdcgCR = 									new XYSeries("salience+subtree weight");//rerank
	static XYSeries salientSubtreeReRankNdcgCR = 										new XYSeries("salience+subtree weight+coherence");
	
	static XYSeries salienceAndAbstractAndSubtreeWeightNdcgCR = 						new XYSeries("salience+abstraction+subtree weight");
	static XYSeries salientAbstractionSubtreeReRankNdcgCr = 							new XYSeries("salience+abstraction+subtree weight+coherence");	
	
	
	
//	static XYSeries sentenceGraphNdcgCR = new XYSeries("SG");
	static XYSeries sentenceGraphNdcgCR = new XYSeries("sentence graph");
	
	
	
	
	static XYSeries soloCentralityFMeasureCR = 							new XYSeries("salience");
	static XYSeries soloReRankFMeasureCR = 								new XYSeries("salience+coherence");
	
	static XYSeries soloAbstractionFMeasureCR = 						new XYSeries("salience+abstraction");
	static XYSeries salientAbstractionReRankFMeasureCR = 				new XYSeries("salience+abstraction+coherence");
	
	static XYSeries soloProofTreeSubtreeWeightFMeasureCR = 				new XYSeries("salience+subtree weight");
	static XYSeries salientSubtreeReRankFMeasureCR = 					new XYSeries("salience+subtree weight+coherence");
	
	static XYSeries salienceAndAbstractAndSubtreeWeightFMeasureCR = 	new XYSeries("salience+abstraction+subtree weight");
	static XYSeries salientAbstractionSubtreeReRankFMeasureCR = 		new XYSeries("salience+abstraction+subtree weight+coherence");	
	

	static XYSeries sentenceGraphFMeasureCR = new XYSeries("sentence graph");
	
	
	
	
	//with similarity
	static XYSeries simCentralityNdcgCR = 												new XYSeries("salience+similarity");
	static XYSeries simReRankNdcgCR = 													new XYSeries("salience+similarity+coherence");
	
	static XYSeries simAbstractionNdcgCR = 												new XYSeries("salience+abstraction+similarity");
	static XYSeries salientAbstractionSimilarityReRankNdcgCR = 							new XYSeries("salience+abstraction+similarity+coherence");	
	
	static XYSeries simProofTreeSubtreeWeightNdcgCR = 									new XYSeries("salience+similarity+subtree weight");
	static XYSeries salientSubtreeSimilarityReRankNdcgCR = 								new XYSeries("salience+similarity+subtree weight+coherence");
	
	static XYSeries salienceAbstractSimilaritySubtreeNdcgCr = 							new XYSeries("salience+abstraction+similarity+subtree weight");
	static XYSeries salienceAbstractionSimilaritySubtreeWeightReRankNdcgCr = 			new XYSeries("salience+abstraction+similarity+subtree weight+coherence");
	
	

	
	
	static XYSeries simCentralityFMeasureCR = 											new XYSeries("salience+similarity");
	static XYSeries simReRankFMeasureCR = 												new XYSeries("salience+similarity+coherence");
	
	static XYSeries simAbstractionFMeasureCR = 											new XYSeries("salience+abstraction+similarity");
	static XYSeries salientAbstractionSimilarityReRankFMeasureCR = 						new XYSeries("salience+abstraction+similarity+coherence");	
	
	static XYSeries simProofTreeSubtreeWeightFMeasureCR = 								new XYSeries("salience+similarity+subtree weight");
	static XYSeries salientSubtreeSimilarityReRankFMeasureCR = 							new XYSeries("salience+similarity+subtree weight+coherence");
	
	static XYSeries salienceAbstractSimilaritySubtreeFMeasureCR = 						new XYSeries("salience+abstraction+similarity+subtree weight");
	static XYSeries salienceAbstractionSimilaritySubtreeWeightReRankFMeasureCR = 		new XYSeries("salience+abstraction+similarity+subtree weight+coherence");
	
	
	

	

	


	

	
	static XYSeries humansNdcgCR = new XYSeries("Human agreement (Avg)");
	
	static DefaultCategoryDataset cosineDataset = new DefaultCategoryDataset();
	static DefaultCategoryDataset cosineConceptSimDataset = new DefaultCategoryDataset();
	
	
	static double[] cr_values = {0.0, 0.05, 0.1, 0.15, 0.20, 0.25, 0.30, 0.35, 0.40, 0.45, 0.50, 0.55, 0.60, 0.65, 0.70, 0.75, 0.80, 0.85, 0.90, 1.0};
	
	static List<String> instanceLocationList = new ArrayList<String>();
	static List<String> similarityConceptList = new ArrayList<String>();
	static List<String> ontologyLocationList = new ArrayList<String>();
	
	
	//uncomment initialization method calls in init() if the ground truth summary should be with the statements greater than avg rating 
	static double AVG_GROUND_TRUTH_RATING_Q1 = 6.0;
	static double AVG_GROUND_TRUTH_RATING_Q2 = 6.0;
			
	
	@BeforeClass
	public static void init() throws IOException {
		surveyProcessor.setValues(filePath);
		instanceLocationList.add(FILE_JUSTIFICATION_INSTANCE);
		similarityConceptList.add(SIMILARITY_CONCEPT);
		
		
		ontologyLocationList.add(DBPEDIA_SCHEMA_LOCATION);
		ontologyLocationList.add(GEONAMES_SCHEMA_LOCATION);		
		
		//uncomment these lines if the ground truth summary should be with the statements greater than avg rating 
		//AVG_GROUND_TRUTH_RATING_Q1 = avgGroundTruthRating(QUESTION1_NAME);
		//AVG_GROUND_TRUTH_RATING_Q2 = avgGroundTruthRating(QUESTION2_NAME);
	}
	
	public static double getAvgGroundTruthRating(String questionName) {
		if(questionName.equals(QUESTION1_NAME))
			return AVG_GROUND_TRUTH_RATING_Q1;
		else if(questionName.equals(QUESTION2_NAME))
			return AVG_GROUND_TRUTH_RATING_Q2;
		return 0.0;
		
	}
	public static double avgGroundTruthRating(String questionName) {
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
	
	@AfterClass
	public static void drawSimilarityCharts() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(simCentralityNdcgCR);
		dataset.addSeries(simReRankNdcgCR);
		
		
		dataset.addSeries(simAbstractionNdcgCR);
		dataset.addSeries(salientAbstractionSimilarityReRankNdcgCR);
		
		dataset.addSeries(simProofTreeSubtreeWeightNdcgCR);
		dataset.addSeries(salientSubtreeSimilarityReRankNdcgCR);
		
		dataset.addSeries(salienceAbstractSimilaritySubtreeNdcgCr);
		dataset.addSeries(salienceAbstractionSimilaritySubtreeWeightReRankNdcgCr);
		
		
		
		// Generate the ndcg vs cr graph
		JFreeChart chart = ChartFactory.createXYLineChart("nDCG vs CR",
				// Title
				"CR",
				// x-axis Label
				"nDCG",
				// y-axis Label
				dataset,
				// Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				true,
				// Show Legend
				true,
				// Use tooltips
				false
		// Configure chart to generate URLs?
				);
		
		chart.setBorderVisible(true);
		
		XYPlot p = chart.getXYPlot();
		XYLineAndShapeRenderer growthRenderer = new XYLineAndShapeRenderer(true,true);
//		        
//		growthRenderer.setSeriesPaint(0, Color.black);
//		growthRenderer.setSeriesPaint(1, Color.cyan);
//		growthRenderer.setShape(new Rectangle(-3,-3,6,6));
//		growthRenderer.setOutlineStroke(new BasicStroke(2));
//		growthRenderer.setOutlinePaint(Color.red);
//		growthRenderer.setUseOutlinePaint(true); 
//		        
		//p.setRenderer(0,growthRenderer);	
		p.setRenderer(growthRenderer);		
		
		try {
			ChartUtilities.saveChartAsJPEG(new File("files/evaluation/dcg/survey/inf1/sim_ndcg_v_cr.jpg"), chart,
					800, 600);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}			
		
		
		// Add the series to your data set
		XYSeriesCollection fMeasureCRdataset = new XYSeriesCollection();
		fMeasureCRdataset.addSeries(simCentralityFMeasureCR);
		fMeasureCRdataset.addSeries(simReRankFMeasureCR);
		
		fMeasureCRdataset.addSeries(simAbstractionFMeasureCR);
		fMeasureCRdataset.addSeries(salientAbstractionSimilarityReRankFMeasureCR);
		
		fMeasureCRdataset.addSeries(simProofTreeSubtreeWeightFMeasureCR);
		fMeasureCRdataset.addSeries(salientSubtreeSimilarityReRankFMeasureCR);
		
		fMeasureCRdataset.addSeries(salienceAbstractSimilaritySubtreeFMeasureCR);
		fMeasureCRdataset.addSeries(salienceAbstractionSimilaritySubtreeWeightReRankFMeasureCR);
		
		
		
		
		
		// Generate the ndcg vs cr graph
		JFreeChart fMeasureCRchart = ChartFactory.createXYLineChart("F-Score vs CR",
				// Title
				"CR",
				// x-axis Label
				"F-Score",
				// y-axis Label
				fMeasureCRdataset,
				// Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				true,
				// Show Legend
				true,
				// Use tooltips
				false
		// Configure chart to generate URLs?
				);
		
		fMeasureCRchart.setBorderVisible(true);
		
		XYPlot p1 = fMeasureCRchart.getXYPlot();
		XYLineAndShapeRenderer growthRenderer1 = new XYLineAndShapeRenderer(true,true);
		
		p1.setRenderer(growthRenderer1);		
		try {
			ChartUtilities.saveChartAsJPEG(new File("files/evaluation/dcg/survey/inf1/sim_fmeasure_v_cr.jpg"), fMeasureCRchart,
					800, 600);
		} catch (IOException e) {
			System.err.println("Problem occurred creating fmeasure v cr chart.");
		}	
		
		
		// generate cosine barchart
		JFreeChart cosineChart = ChartFactory.createBarChart(
				"Consine similarity between human ratings", null,
				"Cosine similarity", cosineConceptSimDataset, PlotOrientation.VERTICAL,
				false, true, false);
		try {
			ChartUtilities.saveChartAsJPEG(new File(
					"files/evaluation/dcg/survey/inf1/sim_cosine_similarity.jpg"),
					cosineChart, 500, 300);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
		
		
	}
	
	/**
	 * draws the charts after executing all the test cases
	 */
	
	@AfterClass
	public static void drawChars() {
		// Add the series to your data set
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(soloCentralityNdcgCR);
		dataset.addSeries(soloReRankNdcgCR);
		
		dataset.addSeries(soloAbstractionNdcgCR);
		dataset.addSeries(salientAbstractionReRankNdcgCr);
		
		dataset.addSeries(soloProofTreeSubtreeWeightNdcgCR);
		dataset.addSeries(salientSubtreeReRankNdcgCR);
		
		dataset.addSeries(salienceAndAbstractAndSubtreeWeightNdcgCR);
		dataset.addSeries(salientAbstractionSubtreeReRankNdcgCr);
		
		
		
		//last one 
		dataset.addSeries(sentenceGraphNdcgCR);
		
		
		//dataset.addSeries(humansNdcgCR); // nDCG vs CR is a comparison against avg human opinions (avg ratings)
		
		// Generate the ndcg vs cr graph
		
		JFreeChart chart = ChartFactory.createXYLineChart("nDCG vs CR",
				// Title
				"CR",
				// x-axis Label
				"nDCG",
				// y-axis Label
				dataset,
				// Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				true,
				// Show Legend
				true,
				// Use tooltips
				false
		// Configure chart to generate URLs?
				);
		
		chart.setBorderVisible(true);
		
		XYPlot p = chart.getXYPlot();
		XYLineAndShapeRenderer growthRenderer = new XYLineAndShapeRenderer(true,true);
//		        
//		growthRenderer.setSeriesPaint(0, Color.black);
//		growthRenderer.setSeriesPaint(1, Color.cyan);
//		growthRenderer.setShape(new Rectangle(-3,-3,6,6));
//		growthRenderer.setOutlineStroke(new BasicStroke(2));
//		growthRenderer.setOutlinePaint(Color.red);
//		growthRenderer.setUseOutlinePaint(true); 
//		        
		//p.setRenderer(0,growthRenderer);	
		p.setRenderer(growthRenderer);
		
		try {
			ChartUtilities.saveChartAsJPEG(new File("files/evaluation/dcg/survey/inf1/ndcg_v_cr.jpg"), chart,
					800, 600);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}	
		
		
		// generate cosine barchart
		JFreeChart cosineChart = ChartFactory.createBarChart(
				"Consine similarity between human ratings", null,
				"Cosine similarity", cosineDataset, PlotOrientation.VERTICAL,
				false, true, false);
		try {
			ChartUtilities.saveChartAsJPEG(new File(
					"files/evaluation/dcg/survey/inf1/cosine_similarity.jpg"),
					cosineChart, 500, 300);
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
		
		
		
		// Add the series to your data set
		XYSeriesCollection fMeasureCRdataset = new XYSeriesCollection();
		fMeasureCRdataset.addSeries(soloCentralityFMeasureCR);
		fMeasureCRdataset.addSeries(soloReRankFMeasureCR);
		
		
		fMeasureCRdataset.addSeries(soloAbstractionFMeasureCR);
		fMeasureCRdataset.addSeries(salientAbstractionReRankFMeasureCR);
		
		
		fMeasureCRdataset.addSeries(soloProofTreeSubtreeWeightFMeasureCR);
		fMeasureCRdataset.addSeries(salientSubtreeReRankFMeasureCR);		
		
		fMeasureCRdataset.addSeries(salienceAndAbstractAndSubtreeWeightFMeasureCR);
		fMeasureCRdataset.addSeries(salientAbstractionSubtreeReRankFMeasureCR);

		
		//last one
		fMeasureCRdataset.addSeries(sentenceGraphFMeasureCR);	
		
		
		// Generate the ndcg vs cr graph
		JFreeChart fMeasureCRchart = ChartFactory.createXYLineChart("F-Score vs CR",
				// Title
				"CR",
				// x-axis Label
				"F-Score",
				// y-axis Label
				fMeasureCRdataset,
				// Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				true,
				// Show Legend
				true,
				// Use tooltips
				false
		// Configure chart to generate URLs?
				);
		
		fMeasureCRchart.setBorderVisible(true);
		
		XYPlot p1 = fMeasureCRchart.getXYPlot();
		XYLineAndShapeRenderer growthRenderer1 = new XYLineAndShapeRenderer(true,true);
		
		p1.setRenderer(growthRenderer1);
		
		try {
			ChartUtilities.saveChartAsJPEG(new File("files/evaluation/dcg/survey/inf1/fmeasure_v_cr.jpg"), fMeasureCRchart,
					800, 600);
		} catch (IOException e) {
			System.err.println("Problem occurred creating fmeasure v cr chart.");
		}	
				
	}
	
	/**
	 * A simple test case for testing purpose
	 * @throws IOException
	 */
	@Test
	public void testCSVInput() throws IOException {
		System.out.println("Hello there!");
		//DCGSurveyEntryProcessor en = new DCGSurveyEntryProcessor();
		//en.setValues("files/evaluation/dcg/survey/inf1/results-survey31573.csv");
		//en.printValues();
	}
	
	@Test 
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
				
		//double stdDev = Statistics.standardDeviation(avgCosine, cosineSimValues);
		
		System.out.println("Avg cosine similarity (new):"+Util.round(totalAvgCosine));
		//System.out.println("Std dev:"+stdDev);
		
		

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
			System.out.println("P_{"+(i+1)+"} agv:"+Util.round(iAvgAgreement));
		}
		
		double totalAvgCosine = cosSimSum/totalValueCount;
				
		//double stdDev = Statistics.standardDeviation(avgCosine, cosineSimValues);
		
		System.out.println("Avg cosine similarity  (with concept similarity)(new):"+Util.round(totalAvgCosine));
		//System.out.println("Std dev:"+stdDev);
		
		

	}	
//	@Test
//	public void testHumanAgreementQuestion2() throws Exception {
//		List<List<RankEntry>> reList = surveyProcessor.getAllRankEntries(QUESTION2_NAME);
//		//en.printValues();
//		List<Double> cosineSimValues = new ArrayList<Double>();
//		double totalCosine = 0.0;
//		int pairCount = 0;
//		for(int i=0;i<reList.size();i++) {
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
//				totalCosine += cosineSim;
//				cosineSimValues.add(cosineSim);
//				
//			
//				//printRankEntryList(reList.get(firstIndex));
//				//printRankEntryList(reList.get(secondIndex));	
//				//System.out.println();
//				pairCount++;
//				cosineConceptSimDataset.setValue(cosineSim, "Cosine similarity", String.valueOf(pairCount));
//			}
//		}
//		
//		double avgCosine = totalCosine/pairCount;
//		double stdDev = Statistics.standardDeviation(avgCosine, cosineSimValues);
//		
//		System.out.println("Avg cosine similarity (with concept similarity):"+avgCosine);
//		System.out.println("Std dev (with concept similarity):"+stdDev);
//		
//
//	}	
	
	/**
	 * computes nDCG between each pairs of evaluators for different p values
	 * then computes average nDCG value for each p value
	 * 
	 * (without similarity)
	 * NOTE: currently not plotting, intention was to plot avarage human agreement
	 */
	@Test
	public void testHumanAgreementNDCG() {
		String questionName = QUESTION1_NAME;
		//List<RankEntry> humanRank = surveyProcessor.getAvgRankEntities(questionName);
		List<List<RankEntry>> reList = surveyProcessor.getAllRankEntries(QUESTION1_NAME);
		
		int n = reList.get(0).size();
		for(int ci=0;ci<cr_values.length;ci++) {
			
			double cr = cr_values[ci];
			int p = (int) (cr * n);
			//System.out.println("###################");
			//System.out.println("p:"+p);
			
			double nDCGSum = 0.0;
			int pairCount=0;
			for(int i=0;i<reList.size();i++) {
			//int i = 0; {
				int firstIndex = i;
				List<RankEntry> list1 = DCGMeasure.copyRankEntryList(reList.get(firstIndex));
				EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
				Collections.sort(list1,cmp);
				for(int j=0;j<reList.size();j++) {
				//int j = 2; {
					if(i!=j) {
						pairCount++;
						//System.out.println("Pair:"+i+","+j);

						int secondIndex = j;
						List<RankEntry> list2 = DCGMeasure.copyRankEntryList(reList.get(secondIndex));
						
						
						Collections.sort(list2,cmp);
						
						//System.out.println("list1:"+firstIndex);
						//printRankEntryList(list1);
						
						//System.out.println("list1:"+secondIndex);
						//printRankEntryList(list2);
						double d = DCGMeasure.computeNDCG(list1, list2, p);
						
						//System.out.println("nDCG: "+d);	
						nDCGSum+=d;
					}
				}
			}
			double avgNDCG = nDCGSum/pairCount;
			//System.out.println("Average nDCG["+p+"]:"+avgNDCG + " CR:"+cr);
			humansNdcgCR.add(cr, avgNDCG);
			
		}
		
	}
	
	
	@Test
	public void testSoloCentrality() throws Exception {
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);			
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = summarySoloCentrality(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);
		List<RankEntry> sList = new ArrayList<RankEntry>();
		System.out.println("NCG Centrality");
		System.out.println("#####################################");
		System.out.println("F-Measure Centrality");
		System.out.println("#####################################");		
		
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		computeNDCGMeasure(reList, sList, soloCentralityNdcgCR);
		
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		computeFmeasure(reList, sList, soloCentralityFMeasureCR,th);
	}
	
	@Test
	public void testSimCentrality() throws Exception {
		
		String questionName = QUESTION2_NAME;
		double th = getAvgGroundTruthRating(questionName);			
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		
		
		//List<String> prefs = new ArrayList<String>();
		
		
		List<KnowledgeStatement> kstmts = summarySimCentrality(FILE_JUSTIFICATION_INF, ROOT_STATEMENT, similarityConceptList, ontologyLocationList, instanceLocationList);
		List<RankEntry> sList = new ArrayList<RankEntry>();
		System.out.println("NCG Sim Centrality");
		System.out.println("#####################################");
		System.out.println("F-Measure Sim Centrality");
		System.out.println("#####################################");		
		
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		computeNDCGMeasure(reList, sList, simCentralityNdcgCR);
		
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		computeFmeasure(reList, sList, simCentralityFMeasureCR,th);
	}	

	
	
	@Test
	public void testSalientReRank() throws Exception {
		
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);	
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);
		
		List<KnowledgeStatement> kstmts = summarySalientReRank(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);
		List<RankEntry> sList = new ArrayList<RankEntry>();
		
		
		System.out.println("NCG ReRank");
		System.out.println("#####################################");		
		System.out.println("F-Measure ReRank");
		System.out.println("#####################################");

		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		computeNDCGMeasure(reList, sList, soloReRankNdcgCR);
		
		computeFmeasure(reList, sList, soloReRankFMeasureCR,th);
	}
	
	
	@Test
	public void testSimSalientReRank() throws Exception {
		
		String questionName = QUESTION2_NAME;
		double th = getAvgGroundTruthRating(questionName);	
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);
		
		List<KnowledgeStatement> kstmts = summarySimSalientReRank(FILE_JUSTIFICATION_INF, ROOT_STATEMENT,similarityConceptList,ontologyLocationList,instanceLocationList);
		List<RankEntry> sList = new ArrayList<RankEntry>();
		
		
		System.out.println("NCG Sim ReRank");
		System.out.println("#####################################");		
		System.out.println("F-Measure Sim ReRank");
		System.out.println("#####################################");

		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		computeNDCGMeasure(reList, sList, simReRankNdcgCR);
		
		computeFmeasure(reList, sList, simReRankFMeasureCR,th);
	}	
	

	
	@Test
	public void testSentenceGraph() throws RDFParseException, RDFHandlerException, RepositoryException, IOException {
		
		System.out.println("F-Measure Sentence Graph");
		System.out.println("#####################################");
		System.out.println("NCG Sentence Graph");
		System.out.println("#####################################");
		
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);		
		List<RankEntry> reList1 = surveyProcessor.getAvgRankEntities(questionName);
		
		List<RankEntry> reList2 = summarySentenceGraph(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);

		computeNDCGMeasure(reList1, reList2, sentenceGraphNdcgCR);	
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList1,cmp);
		
		
		computeFmeasure(reList1, reList2, sentenceGraphFMeasureCR,th);
		
	
		
	}
	
	
	@Test
	public void testSalientProofTreeAbstraction() throws Exception {
		
		System.out.println("NCG Proof Tree Abrstraction");
		System.out.println("#####################################");			
		System.out.println("F-Measure Proof Tree Abrstraction");
		System.out.println("#####################################");	
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);			
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = summaryBySalientProofTreeAbstraction(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		computeNDCGMeasure(reList, sList, soloAbstractionNdcgCR);	
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		
		computeFmeasure(reList, sList, soloAbstractionFMeasureCR,th);
	}
	
	@Test
	public void testSimSalientProofTreeAbstraction() throws Exception {
		
		System.out.println("NCG Proof Tree Abrstraction with concept similarity");
		System.out.println("#####################################");			
		System.out.println("F-Measure Proof Tree Abrstraction with concept similarity");
		System.out.println("#####################################");		
		
		
		String questionName = QUESTION2_NAME;
		double th = getAvgGroundTruthRating(questionName);		
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = summaryBySalientProofTreeAbstractionWithSimilarity(FILE_JUSTIFICATION_INF, ROOT_STATEMENT, similarityConceptList, ontologyLocationList, instanceLocationList);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		computeNDCGMeasure(reList, sList, simAbstractionNdcgCR);	
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		
		computeFmeasure(reList, sList, simAbstractionFMeasureCR,th);
	}

	
	@Test
	public void testSalientProofTreeSubtreeWeight() throws Exception {
		
		System.out.println("NCG Proof Tree sub tree weight");
		System.out.println("#####################################");		
		
		System.out.println("F-Measure Proof Tree sub tree weight");
		System.out.println("#####################################");		
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = summaryBySalientAndProofTreeSubtreeWeight(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);
		List<RankEntry> sList = new ArrayList<RankEntry>();
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		computeNDCGMeasure(reList, sList, soloProofTreeSubtreeWeightNdcgCR);		
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);
		computeFmeasure(reList, sList, soloProofTreeSubtreeWeightFMeasureCR,th);
	}
	
	
	@Test
	public void testSimSalientProofTreeSubtreeWeight() throws Exception {
		
		System.out.println("NCG Proof Tree sub tree weight with similarity");
		System.out.println("#####################################");		
		
		System.out.println("F-Measure Proof Tree sub tree weight with similarity");
		System.out.println("#####################################");		
		
		String questionName = QUESTION2_NAME;
		double th = getAvgGroundTruthRating(questionName);
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = summaryBySalientAndProofTreeSubtreeWeightWithSimilarity(FILE_JUSTIFICATION_INF, ROOT_STATEMENT, similarityConceptList, ontologyLocationList, instanceLocationList);
		List<RankEntry> sList = new ArrayList<RankEntry>();
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		computeNDCGMeasure(reList, sList, simProofTreeSubtreeWeightNdcgCR);		
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);
		
		computeFmeasure(reList, sList, simProofTreeSubtreeWeightFMeasureCR,th);
	}		
	
	public void computeNDCGMeasure(List<RankEntry> reList, List<RankEntry> sList, XYSeries series) {
		
		int n = reList.size();
		for(int ci=0;ci<cr_values.length;ci++) {
			double cr = cr_values[ci];
			int p = (int) (cr * n);
			double d = DCGMeasure.computeNDCG(reList, sList, p);
			//double cr = (double) p / (double) reList.size();
			//System.out.println("NCG["+p+"]:"+d+ " CR:"+cr);
			series.add(cr,d);
			
		}		
	}
	
	public List<RankEntry> summarySentenceGraph(String justificationFile, String rootStmtId) throws RDFParseException, RDFHandlerException, RepositoryException, IOException {
		//List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();
		JustificationProcessor jp = new JustificationProcessor();
		jp.parseJustificationFile(justificationFile,"http://www.example.com/" );
		List<KnowledgeStatement> originalKStatements = jp.getKnStatements();
		
		//Set<Statement> statementSet = new HashSet<Statement>();
		List<Statement> statementSet = new  ArrayList<Statement>();
		
		for(KnowledgeStatement kn:originalKStatements) {
			
			Statement st = kn.getStatement();
			
			String statementId = st.getContext().stringValue();
			if(statementId.equals(rootStmtId)) continue;
			
			statementSet.add(st);
		}
		
		RDFSentenceGraph rdfSentenceGraph = new RDFSentenceGraph(statementSet, 0.5);
		

		SentenceGraphSummarizer summmarizer = new SentenceGraphSummarizer(rdfSentenceGraph);
		
		List<Statement> summary = summmarizer.getAllreRankedStatements();
		List<RankEntry> res = new ArrayList<RankEntry>();
		for(Statement st:summary) {
			String statementId = st.getContext().stringValue();
			//if(statementId.equals(rootStmtId)) continue;
			
			RankEntry re = new RankEntry();
			String name = getStatementName(statementId);
			re.setName(name);
			res.add(re);
		}
		return res;
		
	}	
	public Map<Double, Double> computeFmeasure(List<RankEntry> groundTruthList, List<RankEntry> summaryToCompareList, XYSeries series, double th) {
		Map<Double, Double> res = new HashMap<Double, Double>();
		System.out.println("Ground truth:"+th);
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
			System.out.println("F:"+f_measure+" CR:"+cr+" p:"+p);
			//System.out.println("Precision:"+precision+" Recall:"+recall);
			series.add(cr, f_measure);
			res.put(cr, f_measure);
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
	
	//for sacelled summary and unscalled summary modify in this method
	public List<String> getSummaryByThresholdRating(List<RankEntry> reList2, double th) {
		
		List<RankEntry> reList1 = new ArrayList<RankEntry>(reList2);
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList1,cmp);
		
		double max = reList1.get(0).getJudgmentScore();
		
		List<String> res = new ArrayList<String>();
		for(int i=0;i<reList1.size();i++) {
			//double scalled = reList1.get(i).getJudgmentScore();
			double scalled = (reList1.get(i).getJudgmentScore() / max) * 10.0;
			if( scalled >=th) {
				RankEntry re = reList1.get(i);
				res.add(re.getName());
				System.out.println(re.getName());
			}
		}
		return res;
	}	
	
	public String getStatementName(String statementURI) {
		String[] parts = statementURI.split("/");
		String name = parts[parts.length-1];
		return name;
	}
	

	
	public List<KnowledgeStatement> summarySoloCentrality(String justificationFile, String rootStmtId) throws Exception {
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();
		JustificationProcessor jp = new JustificationProcessor();
		jp.parseJustificationFile(justificationFile,"http://www.example.com/" );
		//List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatements("http://alphubel.unice.fr:8080/lodutil/data/d21");
		List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatements(rootStmtId,null, null, null);
		//int p = (int) (cr*kStmts.size());
		
		for(int i=0;i<kStmts.size();i++) {
			

			
			KnowledgeStatement kst = kStmts.get(i);
			String stmtId = kst.getStatement().getContext().stringValue();
			if(stmtId.equals(rootStmtId)) continue;
			
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("Score:"+kst.getScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
			
			
			stmts.add(kst);
		}

		return stmts;
	}
	
	public List<KnowledgeStatement> summarySimCentrality(String justificationFile, String rootStmtId, List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();
		JustificationProcessor jp = new JustificationProcessor();
		jp.parseJustificationFile(justificationFile,"http://www.example.com/" );
		//List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatements("http://alphubel.unice.fr:8080/lodutil/data/d21");
		List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatements(rootStmtId,prefs, ontologyLocations, instanceLocations);
		//int p = (int) (cr*kStmts.size());
		
		for(int i=0;i<kStmts.size();i++) {
			

			
			KnowledgeStatement kst = kStmts.get(i);
			String stmtId = kst.getStatement().getContext().stringValue();
			if(stmtId.equals(rootStmtId)) continue;
			
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("Score:"+kst.getScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
			
			
			stmts.add(kst);
		}

		return stmts;
	}
	
	public List<KnowledgeStatement> summarySimSalientReRank(String justificationFile, String rootStmtId, List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();
		JustificationProcessor jp = new JustificationProcessor();
		jp.parseJustificationFile(justificationFile,"http://www.example.com/" );
		//List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatements("http://alphubel.unice.fr:8080/lodutil/data/d21");
		List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatementsRerank(rootStmtId,prefs,ontologyLocations,instanceLocations);
		//int p = (int) (cr*kStmts.size());
		
		for(int i=0;i<kStmts.size();i++) {
			

			KnowledgeStatement kst = kStmts.get(i);
			String stmtId = kst.getStatement().getContext().stringValue();
			if(stmtId.equals(rootStmtId)) continue;
			
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("Score:"+kst.getScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
			
			
			stmts.add(kst);
		}

		return stmts;
	}	
	
	
	public List<KnowledgeStatement> summarySalientReRank(String justificationFile, String rootStmtId) throws Exception {
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();
		JustificationProcessor jp = new JustificationProcessor();
		jp.parseJustificationFile(justificationFile,"http://www.example.com/" );
		//List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatements("http://alphubel.unice.fr:8080/lodutil/data/d21");
		List<KnowledgeStatement> kStmts = jp.summarizeJustificationKnowledgeStatementsRerank(rootStmtId,null, null, null);
		//int p = (int) (cr*kStmts.size());
		
		for(int i=0;i<kStmts.size();i++) {
			

			KnowledgeStatement kst = kStmts.get(i);
			String stmtId = kst.getStatement().getContext().stringValue();
			if(stmtId.equals(rootStmtId)) continue;
			
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("Score:"+kst.getScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
			
			
			stmts.add(kst);
		}

		return stmts;
	}
	
	
	public List<KnowledgeStatement> summaryBySalientAndProofTreeSubtreeWeight(String justificationFile, String rootStmtId) throws Exception {
		JustificationProcessor jp = new JustificationProcessor();
		return summaryBySalientAndProofTreeSubtreeWeight(justificationFile, rootStmtId,jp);
	}
	
	
	public List<KnowledgeStatement> summaryBySalientAndProofTreeSubtreeWeight(String justificationFile, String rootStmtId,JustificationProcessor jp) throws Exception {
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();
		
		jp.parseJustificationFile(justificationFile,"http://www.example.com/" );
		List<KnowledgeStatement> kStmts = jp.summarizeByProofTreeSubtreeWeight(true, rootStmtId, null, null, null);
		
		for(int i=0;i<kStmts.size();i++) {
			
			KnowledgeStatement kst = kStmts.get(i);
			String stmtId = kst.getStatement().getContext().stringValue();
			if(stmtId.equals(rootStmtId)) continue;
			
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("Score:"+kst.getScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
			
			
			stmts.add(kst);
		}	
		return stmts;
	}
	
	public List<KnowledgeStatement> summaryBySalientAndProofTreeSubtreeWeightWithSimilarity(String justificationFile, String rootStmtId,
			List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		JustificationProcessor jp = new JustificationProcessor();
		return summaryBySalientAndProofTreeSubtreeWeightWithSimilarity( justificationFile,  rootStmtId,
				 prefs, ontologyLocations, instanceLocations,jp);
		
	}
	
	public List<KnowledgeStatement> summaryBySalientAndProofTreeSubtreeWeightWithSimilarity(String justificationFile, String rootStmtId,
			List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations,JustificationProcessor jp) throws Exception {
		
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();
		
		jp.parseJustificationFile(justificationFile,"http://www.example.com/" );
		List<KnowledgeStatement> kStmts = jp.summarizeByProofTreeSubtreeWeight(true, rootStmtId, prefs, ontologyLocations, instanceLocations);
		
		for(int i=0;i<kStmts.size();i++) {
			
			KnowledgeStatement kst = kStmts.get(i);
			String stmtId = kst.getStatement().getContext().stringValue();
			if(stmtId.equals(rootStmtId)) continue;
			
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("Score:"+kst.getScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
			
			
			stmts.add(kst);
		}	
		return stmts;
	}
	
	
	@Test
	public void testSalientAbstractSubtree() throws Exception {
		
		System.out.println("NCG Salient+Abstract+Subtree");
		System.out.println("#####################################");			
		System.out.println("F-Measure Salient+Abstract+Subtree");
		System.out.println("#####################################");	
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);			
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = summaryBySalientAbstractSubtree(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		computeNDCGMeasure(reList, sList, salienceAndAbstractAndSubtreeWeightNdcgCR);	
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		
		computeFmeasure(reList, sList, salienceAndAbstractAndSubtreeWeightFMeasureCR,th);
	}	
	
	public List<KnowledgeStatement> summaryBySalientAbstractSubtree(String justificationFile, String rootStmtId) throws Exception { 
		JustificationProcessor jp = new JustificationProcessor();
		return summaryBySalientAbstractSubtree(justificationFile,rootStmtId,jp);
	}
	
	public List<KnowledgeStatement> summaryBySalientAbstractSubtree(String justificationFile, String rootStmtId, JustificationProcessor jp) throws Exception { 
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();
		
		jp.parseJustificationFile(justificationFile,"http://www.example.com/" );
		List<KnowledgeStatement> kStmts = jp.summarizeByProofTreeAbstraction(true, rootStmtId, null, null, null);
		
//		Map<String, Double> scores = new HashMap<String,Double>();
//		for(int i=0;i<kStmts.size();i++) {
//			
//			KnowledgeStatement kst = kStmts.get(i);
//			String stmtId = kst.getStatement().getContext().stringValue();
//			//if(stmtId.equals(rootStmtId)) continue;
//			scores.put(stmtId, kst.getScore());
//			
//			System.out.println("Statement:"+kst.getStatement().toString());
//			System.out.println("Score:"+kst.getScore());
//			System.out.println("SubtreeWeight Score:"+kst.getSubTreeWeight());
//			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
//			
//			
//			//stmts.add(kst);
//		}		
		
		kStmts = jp.summarizeByProofTreeSubtreeWeight(rootStmtId);
		
		for(int i=0;i<kStmts.size();i++) {
			
			KnowledgeStatement kst = kStmts.get(i);
			String stmtId = kst.getStatement().getContext().stringValue();
			if(stmtId.equals(rootStmtId)) continue;
			
			//KnowledgeStatement newKst = new KnowledgeStatement();
			//newKst.setStatement(kst.getStatement());
			//newKst.setScore(kst.getScore());
			
//			double stn = 0.5 * scores.get(stmtId) + 0.5 * kst.getSubTreeWeight();
//			System.out.println("Statement:"+kst.getStatement().toString());
//			System.out.println("Score:"+kst.getScore());
//			System.out.println("SubtreeWeight Score:"+kst.getSubTreeWeight());
//			System.out.println("Recomp SubtreeWeight Score:"+stn);
			
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
			
			
			stmts.add(kst);
		}			
		
		return stmts;
		
	}
	public List<KnowledgeStatement> summaryBySalientProofTreeAbstraction(String justificationFile, String rootStmtId) throws Exception {
		JustificationProcessor jp = new JustificationProcessor();
		return summaryBySalientProofTreeAbstraction(justificationFile, rootStmtId,jp );
		
	}
	
	public List<KnowledgeStatement> summaryBySalientProofTreeAbstraction(String justificationFile, String rootStmtId,JustificationProcessor jp ) throws Exception {
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();
		
		jp.parseJustificationFile(justificationFile,"http://www.example.com/" );
		List<KnowledgeStatement> kStmts = jp.summarizeByProofTreeAbstraction(true, rootStmtId, null, null, null);
		
		for(int i=0;i<kStmts.size();i++) {
			
			KnowledgeStatement kst = kStmts.get(i);
			String stmtId = kst.getStatement().getContext().stringValue();
			if(stmtId.equals(rootStmtId)) continue;
			
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("Score:"+kst.getScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
			
			
			stmts.add(kst);
		}	
		return stmts;
	}
	
	public List<KnowledgeStatement> summaryBySalientProofTreeAbstractionWithSimilarity(String justificationFile, String rootStmtId,
			List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		JustificationProcessor jp = new JustificationProcessor();
		return summaryBySalientProofTreeAbstractionWithSimilarity(justificationFile, rootStmtId,
				prefs, ontologyLocations, instanceLocations,jp);
	}
	
	public List<KnowledgeStatement> summaryBySalientProofTreeAbstractionWithSimilarity(String justificationFile, String rootStmtId,
			List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations, JustificationProcessor jp) throws Exception {
		
		
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();
		//JustificationProcessor jp = new JustificationProcessor();
		jp.parseJustificationFile(justificationFile,"http://www.example.com/" );
		List<KnowledgeStatement> kStmts = jp.summarizeByProofTreeAbstraction(true, rootStmtId, prefs, ontologyLocations, instanceLocations);
		
		for(int i=0;i<kStmts.size();i++) {
			
			KnowledgeStatement kst = kStmts.get(i);
			String stmtId = kst.getStatement().getContext().stringValue();
			if(stmtId.equals(rootStmtId)) continue;
			
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("Score:"+kst.getScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
			
			
			stmts.add(kst);
		}	
		return stmts;
	}
	
	public List<KnowledgeStatement> summaryBySalientAbstractionSimilaritySubtreeWeight(String justificationFile, String rootStmtId,
			List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		JustificationProcessor jp = new JustificationProcessor();
		return summaryBySalientAbstractionSimilaritySubtreeWeight( justificationFile,  rootStmtId,
				 prefs,  ontologyLocations,  instanceLocations,jp);
	}
	
	
	public List<KnowledgeStatement> summaryBySalientAbstractionSimilaritySubtreeWeight(String justificationFile, String rootStmtId,
			List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations, JustificationProcessor jp) throws Exception {
		
		
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();
		
		jp.parseJustificationFile(justificationFile,"http://www.example.com/" );
		List<KnowledgeStatement> kStmts = jp.summarizeByProofTreeAbstraction(true, rootStmtId, prefs, ontologyLocations, instanceLocations);
		
		kStmts = jp.summarizeByProofTreeSubtreeWeight(rootStmtId);
		
		for(int i=0;i<kStmts.size();i++) {
			
			KnowledgeStatement kst = kStmts.get(i);
			String stmtId = kst.getStatement().getContext().stringValue();
			if(stmtId.equals(rootStmtId)) continue;
			
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("Score:"+kst.getScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
			
			
			stmts.add(kst);
		}	
		return stmts;
	}	
	@Test
	public void testSalientAbstractionSimilaritySubtreeWeight() throws Exception {
		
		System.out.println("NCG Salient Abstraction Similarity SubtreeWeight");
		System.out.println("#####################################");			
		System.out.println("F-Measure Salient Abstraction Similarity SubtreeWeight");
		System.out.println("#####################################");		
		
		
		String questionName = QUESTION2_NAME;
		double th = getAvgGroundTruthRating(questionName);		
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = summaryBySalientAbstractionSimilaritySubtreeWeight(FILE_JUSTIFICATION_INF, ROOT_STATEMENT, similarityConceptList, ontologyLocationList, instanceLocationList);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		computeNDCGMeasure(reList, sList, salienceAbstractSimilaritySubtreeNdcgCr);	
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		
		computeFmeasure(reList, sList, salienceAbstractSimilaritySubtreeFMeasureCR,th);
	}	
	
	@Test
	public void testSalientAbstractionSubtreeReRank() throws Exception {
		
		System.out.println("NCG Salient Abstraction Subtree ReRank");
		System.out.println("#####################################");			
		System.out.println("F-Measure Salient Abstraction Subtree ReRank");
		System.out.println("#####################################");		
		
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);		
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = summaryBySalientAbstractionSubtreeReRank(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		computeNDCGMeasure(reList, sList, salientAbstractionSubtreeReRankNdcgCr);	
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		
		computeFmeasure(reList, sList, salientAbstractionSubtreeReRankFMeasureCR,th);
	}

	
	
	public List<KnowledgeStatement> summaryBySalientAbstractionSubtreeReRank(String justificationFile, String rootStmtId) throws Exception {
		
		JustificationProcessor jp = new JustificationProcessor();
		List<KnowledgeStatement> stmtsByScore = summaryBySalientAbstractSubtree(justificationFile, rootStmtId,jp);
		
		//rerank
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();

		List<KnowledgeStatement> kStmts = jp.reRankByCoherence(rootStmtId, stmtsByScore);
		
		for(int i=0;i<kStmts.size();i++) {
			

			KnowledgeStatement kst = kStmts.get(i);
			String stmtId = kst.getStatement().getContext().stringValue();
			if(stmtId.equals(rootStmtId)) continue;
			
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("Score:"+kst.getScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
			
			
			stmts.add(kst);
		}

		return stmts;
	}	
	
	
	
	@Test
	public void testSalientAbstractionReRank() throws Exception {
		
		System.out.println("NCG Salient Abstraction ReRank");
		System.out.println("#####################################");			
		System.out.println("F-Measure Salient Abstraction ReRank");
		System.out.println("#####################################");		
		
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);		
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = summaryBySalientAbstractionReRank(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		computeNDCGMeasure(reList, sList, salientAbstractionReRankNdcgCr);	
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		
		computeFmeasure(reList, sList, salientAbstractionReRankFMeasureCR,th);
	}	
	public List<KnowledgeStatement> summaryBySalientAbstractionReRank(String justificationFile, String rootStmtId) throws Exception {
		
		JustificationProcessor jp = new JustificationProcessor();
		List<KnowledgeStatement> stmtsByScore = summaryBySalientProofTreeAbstraction(justificationFile, rootStmtId,jp);
		
		//rerank
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();

		List<KnowledgeStatement> kStmts = jp.reRankByCoherence(rootStmtId, stmtsByScore);
		
		for(int i=0;i<kStmts.size();i++) {
			

			KnowledgeStatement kst = kStmts.get(i);
			String stmtId = kst.getStatement().getContext().stringValue();
			if(stmtId.equals(rootStmtId)) continue;
			
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("Score:"+kst.getScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
			
			
			stmts.add(kst);
		}

		return stmts;
	}	
	
	
	
	
	public List<KnowledgeStatement> summaryBySalientAbstractionSimilaritySubtreeWeightReRank(String justificationFile, String rootStmtId,
			List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		
		JustificationProcessor jp = new JustificationProcessor();
		List<KnowledgeStatement> stmtsByScore = summaryBySalientAbstractionSimilaritySubtreeWeight(justificationFile,rootStmtId,prefs,ontologyLocations,instanceLocations,jp);
		
		//rerank
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();

		List<KnowledgeStatement> kStmts = jp.reRankByCoherence(rootStmtId, stmtsByScore);
		
		for(int i=0;i<kStmts.size();i++) {
			

			KnowledgeStatement kst = kStmts.get(i);
			String stmtId = kst.getStatement().getContext().stringValue();
			if(stmtId.equals(rootStmtId)) continue;
			
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("Score:"+kst.getScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
			
			
			stmts.add(kst);
		}

		return stmts;
	}	

	@Test
	public void testSalientAbstractionSimilaritySubtreeWeightReRank() throws Exception {
		
		System.out.println("NCG Salient Abstraction Similarity SubtreeWeight ReRank");
		System.out.println("#####################################");			
		System.out.println("F-Measure Salient Abstraction Similarity SubtreeWeight ReRank");
		System.out.println("#####################################");		
		
		
		String questionName = QUESTION2_NAME;
		double th = getAvgGroundTruthRating(questionName);		
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = summaryBySalientAbstractionSimilaritySubtreeWeightReRank(FILE_JUSTIFICATION_INF, ROOT_STATEMENT, similarityConceptList, ontologyLocationList, instanceLocationList);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		computeNDCGMeasure(reList, sList, salienceAbstractionSimilaritySubtreeWeightReRankNdcgCr);	
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		
		computeFmeasure(reList, sList, salienceAbstractionSimilaritySubtreeWeightReRankFMeasureCR,th);
	}
	
	
	@Test
	public void testSalientAbstractionSimilarityReRank() throws Exception {
		
		System.out.println("NCG Salient Abstraction Similarity ReRank");
		System.out.println("#####################################");			
		System.out.println("F-Measure Salient Abstraction Similarity ReRank");
		System.out.println("#####################################");		
		
		
		String questionName = QUESTION2_NAME;
		double th = getAvgGroundTruthRating(questionName);		
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = summaryBySalientAbstractionSimilarityReRank(FILE_JUSTIFICATION_INF, ROOT_STATEMENT, similarityConceptList, ontologyLocationList, instanceLocationList);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		computeNDCGMeasure(reList, sList, salientAbstractionSimilarityReRankNdcgCR);	
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		
		computeFmeasure(reList, sList, salientAbstractionSimilarityReRankFMeasureCR,th);
	}
	
	
	public List<KnowledgeStatement> summaryBySalientAbstractionSimilarityReRank(String justificationFile, String rootStmtId,
			List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		
		JustificationProcessor jp = new JustificationProcessor();
		List<KnowledgeStatement> stmtsByScore = summaryBySalientProofTreeAbstractionWithSimilarity(justificationFile,  rootStmtId,
				 prefs,  ontologyLocations,  instanceLocations,jp);
		

		//rerank
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();

		List<KnowledgeStatement> kStmts = jp.reRankByCoherence(rootStmtId, stmtsByScore);
		
		for(int i=0;i<kStmts.size();i++) {
			

			KnowledgeStatement kst = kStmts.get(i);
			String stmtId = kst.getStatement().getContext().stringValue();
			if(stmtId.equals(rootStmtId)) continue;
			
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("Score:"+kst.getScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
			
			
			stmts.add(kst);
		}

		return stmts;
	}	
	
	
	@Test
	public void testSalientSubtreeSimilarityReRank() throws Exception {
		
		System.out.println("NCG Salient Abstraction Similarity ReRank");
		System.out.println("#####################################");			
		System.out.println("F-Measure Salient Abstraction Similarity ReRank");
		System.out.println("#####################################");		
		
		
		String questionName = QUESTION2_NAME;
		double th = getAvgGroundTruthRating(questionName);		
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = summaryBySalientSubtreeSimilarityReRank(FILE_JUSTIFICATION_INF, ROOT_STATEMENT, similarityConceptList, ontologyLocationList, instanceLocationList);
		List<RankEntry> sList = new ArrayList<RankEntry>();
	
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		computeNDCGMeasure(reList, sList, salientSubtreeSimilarityReRankNdcgCR);	
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);		
		
		computeFmeasure(reList, sList, salientSubtreeSimilarityReRankFMeasureCR,th);
	}	
	
	public List<KnowledgeStatement> summaryBySalientSubtreeSimilarityReRank(String justificationFile, String rootStmtId,
			List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		
		JustificationProcessor jp = new JustificationProcessor();
		List<KnowledgeStatement> stmtsByScore = summaryBySalientAndProofTreeSubtreeWeightWithSimilarity(justificationFile,  rootStmtId,
				 prefs,  ontologyLocations,  instanceLocations,jp);
		

		//rerank
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();

		List<KnowledgeStatement> kStmts = jp.reRankByCoherence(rootStmtId, stmtsByScore);
		
		for(int i=0;i<kStmts.size();i++) {
			

			KnowledgeStatement kst = kStmts.get(i);
			String stmtId = kst.getStatement().getContext().stringValue();
			if(stmtId.equals(rootStmtId)) continue;
			
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("Score:"+kst.getScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
			
			
			stmts.add(kst);
		}

		return stmts;
	}	
	
	
	
	public List<KnowledgeStatement> summaryBySalientSubtreeReRank(String justificationFile, String rootStmtId) throws Exception {
		
		JustificationProcessor jp = new JustificationProcessor();
		List<KnowledgeStatement> stmtsByScore = summaryBySalientAndProofTreeSubtreeWeight(justificationFile, rootStmtId,jp);
		
		//rerank
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();

		List<KnowledgeStatement> kStmts = jp.reRankByCoherence(rootStmtId, stmtsByScore);
		
		for(int i=0;i<kStmts.size();i++) {
			

			KnowledgeStatement kst = kStmts.get(i);
			String stmtId = kst.getStatement().getContext().stringValue();
			if(stmtId.equals(rootStmtId)) continue;
			
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("Score:"+kst.getScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+i);
			
			
			stmts.add(kst);
		}

		return stmts;
	}	
	
	
	@Test
	public void testSalientSubtreeReRank() throws Exception {
		
		System.out.println("NCG Salient Subtree ReRank");
		System.out.println("#####################################");		
		
		System.out.println("F-Measure Salient Subtree ReRank");
		System.out.println("#####################################");		
		
		String questionName = QUESTION1_NAME;
		double th = getAvgGroundTruthRating(questionName);
		
		List<RankEntry> reList = surveyProcessor.getAvgRankEntities(questionName);
		List<KnowledgeStatement> kstmts = summaryBySalientSubtreeReRank(FILE_JUSTIFICATION_INF, ROOT_STATEMENT);
		List<RankEntry> sList = new ArrayList<RankEntry>();
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		
		computeNDCGMeasure(reList, sList, salientSubtreeReRankNdcgCR);		
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);
		computeFmeasure(reList, sList, salientSubtreeReRankFMeasureCR,th);
	}	
	
	class EntryJudgmentDscCmp implements Comparator<RankEntry> {
	
		@Override
		public int compare(RankEntry o1, RankEntry o2) {
			
			if(o2.getJudgmentScore()>o1.getJudgmentScore()) return 1;
			if(o2.getJudgmentScore()<o1.getJudgmentScore()) return -1;
			return o1.getName().compareTo(o2.getName());
		}
		
	}
}


