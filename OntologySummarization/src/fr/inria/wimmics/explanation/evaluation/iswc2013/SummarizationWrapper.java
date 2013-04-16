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
	public static String getStatementName(String statementURI) {
		String[] parts = statementURI.split("/");
		String name = parts[parts.length-1];
		return name;
	}
	
	
	
}
