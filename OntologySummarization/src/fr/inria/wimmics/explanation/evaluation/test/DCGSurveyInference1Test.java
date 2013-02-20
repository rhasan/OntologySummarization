package fr.inria.wimmics.explanation.evaluation.test;



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


public class DCGSurveyInference1Test {
	//files/evaluation/dcg/survey/inf1/results-survey31573.csv
	
	static int P_START_VALUE = 2;
	
	static String QUESTION1_NAME = "q1";
	static String QUESTION2_NAME = "q2";//context question
	static DCGSurveyEntryProcessor processorInf1 = new DCGSurveyEntryProcessor();
	static String filePath = "files/evaluation/dcg/survey/inf1/results-survey31573.csv";
	static String ROOT_INF1 = "http://alphubel.unice.fr:8080/lodutil/data/d21";
	static String FILE_JUSTIFICATION_INF1 = "rdf/inference1.trig";
	
	static XYSeries soloCentralityNdcgCR = new XYSeries("Centrality");
	static XYSeries soloReRankNdcgCR = new XYSeries("Coherence");
	static XYSeries sentenceGraphNdcgCR = new XYSeries("Sentence Graph");
	static XYSeries soloAbstractionNdcgCR = new XYSeries("Abstraction");
	static XYSeries soloProofTreeSubtreeWeightNdcgCR = new XYSeries("Sub Tree Weight");
	
	
	static XYSeries soloReRankFMeasureCR = new XYSeries("Coherence");
	static XYSeries soloCentralityFMeasureCR = new XYSeries("Centrality");
	static XYSeries sentenceGraphFMeasureCR = new XYSeries("Sentence Graph");
	static XYSeries soloAbstractionFMeasureCR = new XYSeries("Abstraction");
	static XYSeries soloProofTreeSubtreeWeightFMeasureCR = new XYSeries("Sub Tree Weight");

	static XYSeries humansNdcgCR = new XYSeries("Human agreement (Avg)");
	static DefaultCategoryDataset cosineDataset = new DefaultCategoryDataset();

	@BeforeClass
	public static void init() throws IOException {
		processorInf1.setValues(filePath);
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
		dataset.addSeries(sentenceGraphNdcgCR);
		dataset.addSeries(soloAbstractionNdcgCR);
		dataset.addSeries(soloProofTreeSubtreeWeightNdcgCR);
		
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
		fMeasureCRdataset.addSeries(sentenceGraphFMeasureCR);
		fMeasureCRdataset.addSeries(soloAbstractionFMeasureCR);
		fMeasureCRdataset.addSeries(soloProofTreeSubtreeWeightFMeasureCR);
		
		
		
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
	 */
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
	
	/**
	 * computes nDCG between each pairs of evaluators for different p values
	 * then computes average nDCG value for each p value
	 * 
	 * NOTE: currently not plotting, intention was to plot avarage human agreement
	 */
	@Test
	public void testHumanAgreementNDCG() {
		List<List<RankEntry>> reList = processorInf1.getAllRankEntries(QUESTION1_NAME);
		
		int n = reList.get(0).size();
		for(int p=P_START_VALUE;p<=n;p++) {
			//int p = n;
			double nDCGSum = 0.0;
			int pairCount=0;
			for(int i=0;i<reList.size();i++) {
				int firstIndex = i;
				List<RankEntry> list1 = DCGMeasure.copyRankEntryList(reList.get(firstIndex));
				EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
				Collections.sort(list1,cmp);
				for(int j=0;j<reList.size();j++) {
					if(i!=j) {
						pairCount++;
						//System.out.println("Pair:"+i+","+j);

						int secondIndex = j;
						List<RankEntry> list2 = DCGMeasure.copyRankEntryList(reList.get(secondIndex));
						
						
						Collections.sort(list2,cmp);
						
						//printRankEntryList(list1);
						//printRankEntryList(list2);
						double d = DCGMeasure.computeNDCG(list1, list2, p);
						//System.out.println("nDCG: "+d);	
						nDCGSum+=d;
					}
				}
			}
			double avgNDCG = nDCGSum/pairCount;
			double cr = (double) p / (double) n;
			System.out.println("Average nDCG["+p+"]:"+avgNDCG + " CR:"+cr);
			humansNdcgCR.add(cr, avgNDCG);
			
		}
		
	}
	
	
	@Test
	public void testSoloCentrality() throws Exception {
		List<RankEntry> reList = processorInf1.getAvgRankEntities(QUESTION1_NAME);
		List<KnowledgeStatement> kstmts = summarySoloCentrality(FILE_JUSTIFICATION_INF1, ROOT_INF1);
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
		computeFmeasure(reList, sList, soloCentralityFMeasureCR);
	}
	
//	@Test
//	public void testFMeasureSoloCentrality() throws Exception {
//		List<RankEntry> reList1 = processorInf1.getAvgRankEntities(QUESTION1_NAME);
//		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
//		Collections.sort(reList1,cmp);
//		//printRankEntryList(reList1);
//		
//		List<KnowledgeStatement> kstmts = summarySoloCentrality(FILE_JUSTIFICATION_INF1, ROOT_INF1);
//		List<RankEntry> reList2 = new ArrayList<RankEntry>();
//		
//		System.out.println("F-Measure Centrality");
//		System.out.println("#####################################");
//		
//		//System.out.println(reList.size()+":"+kstmts.size());
//		for(KnowledgeStatement kst:kstmts) {
//			String name = getStatementName(kst.getStatement().getContext().stringValue());
//			RankEntry re = new RankEntry();
//			re.setName(name);
//			reList2.add(re);
//		}
//		
//		//printRankEntryList(reList2);
//		computeFmeasure(reList1, reList2, soloCentralityFMeasureCR);
//		
//
//	}		
	@Test
	public void testSoloReRank() throws Exception {
		List<RankEntry> reList = processorInf1.getAvgRankEntities(QUESTION1_NAME);
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList,cmp);
		
		List<KnowledgeStatement> kstmts = summarySoloReRank(FILE_JUSTIFICATION_INF1, ROOT_INF1);
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
		
		computeFmeasure(reList, sList, soloReRankFMeasureCR);
	}
	
//	@Test
//	public void testFMeasureSoloReRank() throws Exception {
//		List<RankEntry> reList1 = processorInf1.getAvgRankEntities(QUESTION1_NAME);
//		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
//		Collections.sort(reList1,cmp);
//		//printRankEntryList(reList1);
//		
//		List<KnowledgeStatement> kstmts = summarySoloReRank(FILE_JUSTIFICATION_INF1, ROOT_INF1);
//		List<RankEntry> reList2 = new ArrayList<RankEntry>();
//		
//		System.out.println("F-Measure ReRank");
//		System.out.println("#####################################");
//		
//		//System.out.println(reList.size()+":"+kstmts.size());
//		for(KnowledgeStatement kst:kstmts) {
//			String name = getStatementName(kst.getStatement().getContext().stringValue());
//			RankEntry re = new RankEntry();
//			re.setName(name);
//			reList2.add(re);
//		}
//		
//		//printRankEntryList(reList2);
//		computeFmeasure(reList1, reList2, soloReRankFMeasureCR);
//		
//
//	}
//	
	

	
	@Test
	public void testSentenceGraph() throws RDFParseException, RDFHandlerException, RepositoryException, IOException {
		
		System.out.println("F-Measure Sentence Graph");
		System.out.println("#####################################");
		System.out.println("NCG Sentence Graph");
		System.out.println("#####################################");
		
		List<RankEntry> reList1 = processorInf1.getAvgRankEntities(QUESTION1_NAME);
		
		List<RankEntry> reList2 = summarySentenceGraph(FILE_JUSTIFICATION_INF1, ROOT_INF1);

		computeNDCGMeasure(reList1, reList2, sentenceGraphNdcgCR);	
		
		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
		Collections.sort(reList1,cmp);
		
		
		computeFmeasure(reList1, reList2, sentenceGraphFMeasureCR);
		
	
		
	}
	
	
//	@Test
//	public void testSentenceGraph() throws Exception {
//		List<RankEntry> reList = processorInf1.getAvgRankEntities(QUESTION1_NAME);
//		List<RankEntry> sList = summarySentenceGraph(FILE_JUSTIFICATION_INF1, ROOT_INF1);
//		System.out.println("NCG Sentence Graph");
//		System.out.println("#####################################");
//		computeNDCGMeasure(reList, sList, sentenceGraphNdcgCR);
//		
//
//	}
	
	public void computeNDCGMeasure(List<RankEntry> reList, List<RankEntry> sList, XYSeries series) {
		
		for(int p=P_START_VALUE;p<=reList.size();p++) {
			double d = DCGMeasure.computeNDCG(reList, sList, p);
			double cr = (double) p / (double) reList.size();
			System.out.println("NCG["+p+"]:"+d+ " CR:"+cr);
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
	public Map<Double, Double> computeFmeasure(List<RankEntry> groundTruthList, List<RankEntry> summaryToCompareList, XYSeries series) {
		Map<Double, Double> res = new HashMap<Double, Double>();
		for(int p=P_START_VALUE;p<=groundTruthList.size();p++) {
			//System.out.println("Ground truth");
			List<String> groundTruth = getSummaryOfSizeP(groundTruthList, p);
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
			double cr = (double) p / (double) groundTruthList.size();
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
			//System.out.println(re.getName());
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
	
	
	@Test
	public void testSoloProofTreeAbstraction() throws Exception {
		
		System.out.println("NCG Proof Tree Abrstraction");
		System.out.println("#####################################");			
		System.out.println("F-Measure Proof Tree Abrstraction");
		System.out.println("#####################################");		
		List<RankEntry> reList = processorInf1.getAvgRankEntities(QUESTION1_NAME);
		List<KnowledgeStatement> kstmts = summaryByProofTreeAbstraction(FILE_JUSTIFICATION_INF1, ROOT_INF1);
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
		
		computeFmeasure(reList, sList, soloAbstractionFMeasureCR);
	}
	
//	@Test
//	public void testFMeasureSoloProofTreeAbstraction() throws Exception {
//		List<RankEntry> reList1 = processorInf1.getAvgRankEntities(QUESTION1_NAME);
//		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
//		Collections.sort(reList1,cmp);
//		//printRankEntryList(reList1);
//		
//		List<KnowledgeStatement> kstmts = summaryByProofTreeAbstraction(FILE_JUSTIFICATION_INF1, ROOT_INF1);
//		List<RankEntry> reList2 = new ArrayList<RankEntry>();
//		
//		System.out.println("F-Measure Proof Tree Abrstraction");
//		System.out.println("#####################################");
//		
//		//System.out.println(reList.size()+":"+kstmts.size());
//		for(KnowledgeStatement kst:kstmts) {
//			String name = getStatementName(kst.getStatement().getContext().stringValue());
//			RankEntry re = new RankEntry();
//			re.setName(name);
//			reList2.add(re);
//		}
//		
//		//printRankEntryList(reList2);
//		computeFmeasure(reList1, reList2, soloAbstractionFMeasureCR);
//		
//
//	}	
//

	
	@Test
	public void testSoloProofTreeSubtreeWeight() throws Exception {
		
		System.out.println("NCG Proof Tree sub tree weight");
		System.out.println("#####################################");		
		
		System.out.println("F-Measure Proof Tree sub tree weight");
		System.out.println("#####################################");		
		
		List<RankEntry> reList = processorInf1.getAvgRankEntities(QUESTION1_NAME);
		List<KnowledgeStatement> kstmts = summaryByProofTreeSubtreeWeight(FILE_JUSTIFICATION_INF1, ROOT_INF1);
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
		computeFmeasure(reList, sList, soloProofTreeSubtreeWeightFMeasureCR);
	}
	
//	@Test
//	public void testFMeasureSoloProofTreeSubtreeWeight() throws Exception {
//		List<RankEntry> reList1 = processorInf1.getAvgRankEntities(QUESTION1_NAME);
//		EntryJudgmentDscCmp cmp = new EntryJudgmentDscCmp();
//		Collections.sort(reList1,cmp);
//		//printRankEntryList(reList1);
//		
//		List<KnowledgeStatement> kstmts = summaryByProofTreeSubtreeWeight(FILE_JUSTIFICATION_INF1, ROOT_INF1);
//		List<RankEntry> reList2 = new ArrayList<RankEntry>();
//		
//		System.out.println("F-Measure Proof Tree sub tree weight");
//		System.out.println("#####################################");
//		
//		//System.out.println(reList.size()+":"+kstmts.size());
//		for(KnowledgeStatement kst:kstmts) {
//			String name = getStatementName(kst.getStatement().getContext().stringValue());
//			RankEntry re = new RankEntry();
//			re.setName(name);
//			reList2.add(re);
//		}
//		
//		//printRankEntryList(reList2);
//		computeFmeasure(reList1, reList2, soloProofTreeSubtreeWeightFMeasureCR);
//		
//
//	}		
	
	public List<KnowledgeStatement> summaryByProofTreeSubtreeWeight(String justificationFile, String rootStmtId) throws Exception {
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();
		JustificationProcessor jp = new JustificationProcessor();
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
		
	
	public List<KnowledgeStatement> summaryByProofTreeAbstraction(String justificationFile, String rootStmtId) throws Exception {
		List<KnowledgeStatement> stmts = new ArrayList<KnowledgeStatement>();
		JustificationProcessor jp = new JustificationProcessor();
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
	

	

	class EntryJudgmentDscCmp implements Comparator<RankEntry> {
	
		@Override
		public int compare(RankEntry o1, RankEntry o2) {
			
			if(o2.getJudgmentScore()>o1.getJudgmentScore()) return 1;
			if(o2.getJudgmentScore()<o1.getJudgmentScore()) return -1;
			return 0;
		}
		
	}
}


