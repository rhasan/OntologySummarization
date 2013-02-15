package fr.inria.wimmics.explanation.evaluation.test;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inria.wimmics.explanation.JustificationProcessor;
import fr.inria.wimmics.explanation.KnowledgeStatement;
import fr.inria.wimmics.explanation.evaluation.CosineSimilarity;
import fr.inria.wimmics.explanation.evaluation.DCGMeasure;
import fr.inria.wimmics.explanation.evaluation.PrecisionRecall;
import fr.inria.wimmics.explanation.evaluation.PrecisionRecallCalculator;

import fr.inria.wimmics.explanation.evaluation.RankEntry;


public class DCGSurveyInference1Test {
	//files/evaluation/dcg/survey/inf1/results-survey31573.csv
	
	static String QUESTION1_NAME = "q1";
	static String QUESTION2_NAME = "q2";//context question
	static DCGSurveyEntryProcessor processorInf1 = new DCGSurveyEntryProcessor();
	static String filePath = "files/evaluation/dcg/survey/inf1/results-survey31573.csv";
	static String ROOT_INF1 = "http://alphubel.unice.fr:8080/lodutil/data/d21";
	static String FILE_JUSTIFICATION_INF1 = "rdf/inference1.trig";
	
	static XYSeries soloCentralityNdcgCR = new XYSeries("Centrality");
	static XYSeries soloReRankNdcgCR = new XYSeries("Re-Ranking");
	static XYSeries soloReRankFMeasureCR = new XYSeries("Re-Ranking");
	static XYSeries soloCentralityFMeasureCR = new XYSeries("Centrality");
	//static XYSeries humansNdcgCR = new XYSeries("Human average");
	static DefaultCategoryDataset cosineDataset = new DefaultCategoryDataset();

	@BeforeClass
	public static void init() throws IOException {
		processorInf1.setValues(filePath);
	}
	
	@AfterClass
	public static void drawChars() {
		// Add the series to your data set
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(soloCentralityNdcgCR);
		dataset.addSeries(soloReRankNdcgCR);
		//dataset.addSeries(humansNdcgCR);
		
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
		try {
			ChartUtilities.saveChartAsJPEG(new File("files/evaluation/dcg/survey/inf1/ndcg_v_cr.jpg"), chart,
					500, 300);
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
		
		
		
		// Generate the ndcg vs cr graph
		JFreeChart fMeasureCRchart = ChartFactory.createXYLineChart("F-Measure vs CR",
				// Title
				"CR",
				// x-axis Label
				"F-Measure",
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
		try {
			ChartUtilities.saveChartAsJPEG(new File("files/evaluation/dcg/survey/inf1/fmeasure_v_cr.jpg"), fMeasureCRchart,
					500, 300);
		} catch (IOException e) {
			System.err.println("Problem occurred creating fmeasure v cr chart.");
		}	
				
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
		List<List<RankEntry>> reList = processorInf1.getAllRankEntries(QUESTION1_NAME);
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
				
				//double d = DCGMeasure.computeNDCG(reList.get(firstIndex), reList.get(secondIndex), reList.get(firstIndex).size());
				//System.out.println("nDCG: "+d);
				
				
				//printRankEntryList(reList.get(firstIndex));
				//printRankEntryList(reList.get(secondIndex));	
				//System.out.println();
				pairCount++;
				cosineDataset.setValue(cosineSim, "Cosine similarity", String.valueOf(pairCount));
			}
		}
		
		double avgCosine = totalCosine/pairCount;
		
		System.out.println("Avg cosine similarity:"+avgCosine);

	}
	
	
//	@Test
//	public void humanRankingByRatings() {
//		
//		System.out.println("Humans");
//		System.out.println("#####################################");
//		
//		List<RankEntry> reList = processorInf1.getAvgRankEntities(QUESTION1_NAME);
//		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
//		Collections.sort(reList,cmp);
//		
//		for(int p=2;p<=reList.size();p++) {
//			double d = DCGMeasure.computeNDCG(reList, p);
//			double cr = (double) p / (double) reList.size();
//			System.out.println("NCG["+p+"]:"+d+ " CR:"+cr);
//			humansNdcgCR.add(cr, d);
//		}
//	}
	
	@Test
	public void testSoloCentrality() throws Exception {
		List<RankEntry> reList = processorInf1.getAvgRankEntities(QUESTION1_NAME);
		List<KnowledgeStatement> kstmts = summarySoloCentrality(FILE_JUSTIFICATION_INF1, ROOT_INF1);
		List<RankEntry> sList = new ArrayList<RankEntry>();
		System.out.println("Centrality");
		System.out.println("#####################################");
		
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		for(int p=2;p<=reList.size();p++) {
			double d = DCGMeasure.computeNDCG(reList, sList, p);
			double cr = (double) p / (double) reList.size();
			System.out.println("NCG["+p+"]:"+d+ " CR:"+cr);
			soloCentralityNdcgCR.add(cr,d);
		}
	}
	
	
	@Test
	public void testSoloReRank() throws Exception {
		List<RankEntry> reList = processorInf1.getAvgRankEntities(QUESTION1_NAME);
		List<KnowledgeStatement> kstmts = summarySoloReRank(FILE_JUSTIFICATION_INF1, ROOT_INF1);
		List<RankEntry> sList = new ArrayList<RankEntry>();
		System.out.println("ReRank");
		System.out.println("#####################################");		
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			sList.add(re);
		}
		
		for(int p=2;p<=reList.size();p++) {
			double d = DCGMeasure.computeNDCG(reList, sList, p);
			double cr = (double) p / (double) reList.size();
			System.out.println("NCG["+p+"]:"+d+ " CR:"+cr);
			soloReRankNdcgCR.add(cr,d);
		}
	}
	@Test
	public void testFMeasureSoloReRank() throws Exception {
		List<RankEntry> reList1 = processorInf1.getAvgRankEntities(QUESTION1_NAME);
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList1,cmp);
		//printRankEntryList(reList1);
		
		List<KnowledgeStatement> kstmts = summarySoloReRank(FILE_JUSTIFICATION_INF1, ROOT_INF1);
		List<RankEntry> reList2 = new ArrayList<RankEntry>();
		
		System.out.println("F-Measure ReRank");
		System.out.println("#####################################");
		
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			reList2.add(re);
		}
		
		//printRankEntryList(reList2);
		computeFmeasure(reList1, reList2, soloReRankFMeasureCR);
		

	}
	
	
	@Test
	public void testFMeasureSoloCentrality() throws Exception {
		List<RankEntry> reList1 = processorInf1.getAvgRankEntities(QUESTION1_NAME);
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList1,cmp);
		//printRankEntryList(reList1);
		
		List<KnowledgeStatement> kstmts = summarySoloCentrality(FILE_JUSTIFICATION_INF1, ROOT_INF1);
		List<RankEntry> reList2 = new ArrayList<RankEntry>();
		
		System.out.println("F-Measure Centrality");
		System.out.println("#####################################");
		
		//System.out.println(reList.size()+":"+kstmts.size());
		for(KnowledgeStatement kst:kstmts) {
			String name = getStatementName(kst.getStatement().getContext().stringValue());
			RankEntry re = new RankEntry();
			re.setName(name);
			reList2.add(re);
		}
		
		//printRankEntryList(reList2);
		computeFmeasure(reList1, reList2, soloCentralityFMeasureCR);
		

	}	
	public Map<Double, Double> computeFmeasure(List<RankEntry> reList1, List<RankEntry> reList2, XYSeries series) {
		Map<Double, Double> res = new HashMap<Double, Double>();
		for(int p=2;p<=reList1.size();p++) {
			List<String> S = getSummaryOfSizeP(reList1, p);
			List<String> H = getSummaryOfSizeP(reList2, p);
			PrecisionRecall pr = PrecisionRecallCalculator.calculatePrecisionRecall(S,H);
			double precision = pr.getPrecision();
			double recall = pr.getRecall();
			Double f_measure = (2*precision*recall)/(precision+recall);
			if(f_measure.isNaN()) {
				f_measure = 0.0;
				//System.out.println("*********************************");
			}
			double cr = (double) p / (double) reList1.size();
			//System.out.println("Precision:"+precision);
			//System.out.println("Recall:"+recall);
			System.out.println("F:"+f_measure+" CR:"+cr+" p:"+p);
			System.out.println("Precision:"+precision+" Recall:"+recall);
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
	
	
	public List<KnowledgeStatement> summarySoloReRank(String justificationFile, String rootStmtId) throws Exception {
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
	

	class EntryJudgmentDscCmp implements Comparator<RankEntry> {
	
		@Override
		public int compare(RankEntry o1, RankEntry o2) {
			
			if(o2.getJudgmentScore()>o1.getJudgmentScore()) return 1;
			if(o2.getJudgmentScore()<o1.getJudgmentScore()) return -1;
			return 0;
		}
		
	}
}


