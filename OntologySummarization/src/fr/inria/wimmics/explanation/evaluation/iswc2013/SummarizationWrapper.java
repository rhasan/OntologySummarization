package fr.inria.wimmics.explanation.evaluation.iswc2013;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

import fr.inria.wimmics.explanation.JustificationProcessor;
import fr.inria.wimmics.explanation.KnowledgeStatement;
import fr.inria.wimmics.explanation.RDFSentenceGraph;
import fr.inria.wimmics.explanation.SentenceGraphSummarizer;
import fr.inria.wimmics.explanation.evaluation.RankEntry;

public class SummarizationWrapper {
	public static List<KnowledgeStatement> summarySoloCentrality(String justificationFile, String rootStmtId) throws Exception {
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
	
	
	public static List<KnowledgeStatement> summarySalientReRank(String justificationFile, String rootStmtId) throws Exception {
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
	
	public static List<RankEntry> summarySentenceGraph(String justificationFile, String rootStmtId, double SG_Nagivational_p) throws RDFParseException, RDFHandlerException, RepositoryException, IOException {
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
		
		RDFSentenceGraph rdfSentenceGraph = new RDFSentenceGraph(statementSet,SG_Nagivational_p);
		

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
	
	public static List<KnowledgeStatement> summaryBySalientProofTreeAbstraction(String justificationFile, String rootStmtId) throws Exception {
		JustificationProcessor jp = new JustificationProcessor();
		return summaryBySalientProofTreeAbstraction(justificationFile, rootStmtId,jp );
		
	}
	public static List<KnowledgeStatement> summaryBySalientProofTreeAbstraction(String justificationFile, String rootStmtId,JustificationProcessor jp ) throws Exception {
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
	public static List<KnowledgeStatement> summaryBySalientAndProofTreeSubtreeWeight(String justificationFile, String rootStmtId) throws Exception {
		JustificationProcessor jp = new JustificationProcessor();
		return summaryBySalientAndProofTreeSubtreeWeight(justificationFile, rootStmtId,jp);
	}
	
	
	public static List<KnowledgeStatement> summaryBySalientAndProofTreeSubtreeWeight(String justificationFile, String rootStmtId,JustificationProcessor jp) throws Exception {
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
	
	public static List<KnowledgeStatement> summaryBySalientAbstractionSubtreeReRank(String justificationFile, String rootStmtId) throws Exception {
		
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
	
	public static List<KnowledgeStatement> summaryBySalientAbstractionReRank(String justificationFile, String rootStmtId) throws Exception {
		
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
	
		
	public static List<KnowledgeStatement> summaryBySalientSubtreeReRank(String justificationFile, String rootStmtId) throws Exception {
		
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
	
	//similarity below
	
	public static List<KnowledgeStatement> summarySimCentrality(String justificationFile, String rootStmtId, List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
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
	
	public static List<KnowledgeStatement> summarySimSalientReRank(String justificationFile, String rootStmtId, List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
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
	
	
	
	public static List<KnowledgeStatement> summaryBySalientProofTreeAbstractionWithSimilarity(String justificationFile, String rootStmtId,
			List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		JustificationProcessor jp = new JustificationProcessor();
		return summaryBySalientProofTreeAbstractionWithSimilarity(justificationFile, rootStmtId,
				prefs, ontologyLocations, instanceLocations,jp);
	}
	
	public static List<KnowledgeStatement> summaryBySalientProofTreeAbstractionWithSimilarity(String justificationFile, String rootStmtId,
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
	
	public static List<KnowledgeStatement> summaryBySalientAndProofTreeSubtreeWeightWithSimilarity(String justificationFile, String rootStmtId,
			List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		JustificationProcessor jp = new JustificationProcessor();
		return summaryBySalientAndProofTreeSubtreeWeightWithSimilarity( justificationFile,  rootStmtId,
				 prefs, ontologyLocations, instanceLocations,jp);
		
	}
	
	public static List<KnowledgeStatement> summaryBySalientAndProofTreeSubtreeWeightWithSimilarity(String justificationFile, String rootStmtId,
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
	
	public static List<KnowledgeStatement> summaryBySalientAbstractSubtree(String justificationFile, String rootStmtId) throws Exception { 
		JustificationProcessor jp = new JustificationProcessor();
		return summaryBySalientAbstractSubtree(justificationFile,rootStmtId,jp);
	}
	
	public static List<KnowledgeStatement> summaryBySalientAbstractSubtree(String justificationFile, String rootStmtId, JustificationProcessor jp) throws Exception { 
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
	
	
	public static List<KnowledgeStatement> summaryBySalientAbstractionSimilaritySubtreeWeight(String justificationFile, String rootStmtId,
			List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		JustificationProcessor jp = new JustificationProcessor();
		return summaryBySalientAbstractionSimilaritySubtreeWeight( justificationFile,  rootStmtId,
				 prefs,  ontologyLocations,  instanceLocations,jp);
	}
	
	
	public static List<KnowledgeStatement> summaryBySalientAbstractionSimilaritySubtreeWeight(String justificationFile, String rootStmtId,
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
	
	public static List<KnowledgeStatement> summaryBySalientAbstractionSimilaritySubtreeWeightReRank(String justificationFile, String rootStmtId,
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
	
	public static List<KnowledgeStatement> summaryBySalientAbstractionSimilarityReRank(String justificationFile, String rootStmtId,
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
	
	public static List<KnowledgeStatement> summaryBySalientSubtreeSimilarityReRank(String justificationFile, String rootStmtId,
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
	
	
	public static String getStatementName(String statementURI) {
		String[] parts = statementURI.split("/");
		String name = parts[parts.length-1];
		return name;
	}
	
	
	
}
