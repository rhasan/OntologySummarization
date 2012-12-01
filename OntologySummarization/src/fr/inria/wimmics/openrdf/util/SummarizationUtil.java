package fr.inria.wimmics.openrdf.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openrdf.model.Statement;

import fr.inria.wimmics.explanation.KnowledgeStatement;

public class SummarizationUtil {
	public static void normalizeDegreeCentrality(List<KnowledgeStatement> kStatements) {
		
		KnowledgeStatement max = Collections.max(kStatements, new Comparator<KnowledgeStatement>() {

			@Override
			public int compare(KnowledgeStatement o1, KnowledgeStatement o2) {
				if(o1.getDegreeCentrality()>o2.getDegreeCentrality()) return 1;
				else if(o1.getDegreeCentrality()<o2.getDegreeCentrality()) return -1;
				return 0;
			}
		});
		
		double maxScore = max.getDegreeCentrality();
		for(KnowledgeStatement st:kStatements) {
			double d = st.getDegreeCentrality()/maxScore;
			st.setDegreeCentrality(d);
		}
		
	}
	
	public static void normalizeScores(List<KnowledgeStatement> kStatements) {
		
		KnowledgeStatement max = Collections.max(kStatements, new Comparator<KnowledgeStatement>() {

			@Override
			public int compare(KnowledgeStatement o1, KnowledgeStatement o2) {
				if(o1.getScore()>o2.getScore()) return 1;
				else if(o1.getScore()<o2.getScore()) return -1;
				return 0;
			}
		});
		
		double maxScore = max.getScore();
		for(KnowledgeStatement st:kStatements) {
			double d = st.getScore()/maxScore;
			st.setScore(d);
		}
		
	}	
	
	public static List<Statement> getRDFStatementList(List<KnowledgeStatement> kstmts) {
		List<Statement> rdfStmts = new ArrayList<Statement>(kstmts.size());
		for(KnowledgeStatement kstmt:kstmts) {
			rdfStmts.add(kstmt.getStatement());
		}
		return rdfStmts;
	}	
}
