package fr.inria.wimmics.explanation.controller;



import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Statement;

import fr.inria.wimmics.explanation.model.GenericRDFSentence;
import fr.inria.wimmics.explanation.model.RDFSentenceWithRank;

public class SentenceGraphSummarizer {

	final static double LAMBDA = 0.5;
	Double maxCiValue;
	RDFSentenceGraph graph;
	HashMap<GenericRDFSentence, RDFSentenceWithRank> rankedSentences;
	
	public SentenceGraphSummarizer(RDFSentenceGraph graph){
		this.graph = graph;
		this.rankedSentences = new HashMap<GenericRDFSentence, RDFSentenceWithRank>();
		computeIndegreeCentrality();
	}
	
	
	private void initMeasures(HashMap<GenericRDFSentence, RDFSentenceWithRank> x) {
		 //initialize Ci
		 for(GenericRDFSentence st:graph.getGraphSentences()) {
			 RDFSentenceWithRank sr = new RDFSentenceWithRank();
			 sr.setSentence(st);
			 sr.setCi(0.0);
			 x.put(st, sr);
		 }
	}	

	private void normalizeMeasures(HashMap<GenericRDFSentence, RDFSentenceWithRank> x) {
		//normalize Ci
		double maxCi = Double.NEGATIVE_INFINITY;
		for(GenericRDFSentence i:x.keySet()) {
			double value = x.get(i).getCi();
			if(maxCi<value) {
				maxCi = value;
			}			
		}
		maxCiValue = maxCi;
		if(maxCi==0.0)return;
		
		for(GenericRDFSentence i:x.keySet()) {
			double value = x.get(i).getCi();
			double nValue = value/maxCi;
			x.get(i).setNormalizedCi(nValue);
		}
	
	}
	
	private double getCi(GenericRDFSentence i) {
		//return Ci.get(i);
		
		return rankedSentences.get(i).getCi();
	}
	
	private void setCi(GenericRDFSentence i,double value) {
		//Ci.put(i, value);
		rankedSentences.get(i).setCi(value);
	}
	
	public void computeIndegreeCentrality() {
		//HashMap<GenericRDFSentence, Double> Cx = null;
		//initMeasure(Ci);
		initMeasures(rankedSentences);
		System.out.println("Summarizing : "+rankedSentences.size()+" sentences");
		for(GenericRDFSentence sj:graph.getEdgeList().keySet()) {
			HashMap<GenericRDFSentence, Double> d2 = graph.getEdgeList().get(sj);
			double wSum = 0.0;
			for(GenericRDFSentence si:d2.keySet()) {
				double Ci4i = getCi(si);
				Ci4i += graph.getWeight(sj, si);
				setCi(si, Ci4i);
			}
		}
		//normalizeMeasure(Ci);
		normalizeMeasures(rankedSentences);
	}
	

	public Set<Statement> summarizeByCentralityThresholdWOreRank(double threshold) {

		assert threshold <= 1.0;
		
		//double threshold = maxCiValue*ratio;
		Set<Statement> statements = new HashSet<Statement>();
		
		//Set<GenericRDFSentence> stmt= new HashSet<GenericRDFSentence>();
		for(GenericRDFSentence st:rankedSentences.keySet()) {
			double val = rankedSentences.get(st).getNormalizedCi();
			
			if(val>threshold) {
				//stmt.add(st);
				statements.addAll(st.getStatements());
				System.out.println(st.getStatements().toString());
				System.out.println("score:"+val);
			}
		}
		
		System.out.println("Threshold:" + threshold);
		System.out.println("Statements in original graph: "+graph.getStatements().size());
		//System.out.println("RDF-sentences in summarized graph: "+stmt.size());
		System.out.println("Statements in summarized graph: "+statements.size());
		
		return statements;
	}

	
	public Set<Statement> summarizeBySizeWOreRank(double percentage) {

		assert percentage <= 1.0;
		
		Set<Statement> statements = new HashSet<Statement>();
		
	    
	    List<RDFSentenceWithRank> ranked = new LinkedList<RDFSentenceWithRank>(rankedSentences.values());

		Collections.sort(ranked, new Comparator<RDFSentenceWithRank>() {

	        public int compare(RDFSentenceWithRank o1, RDFSentenceWithRank o2) {
	        	
	        	if(o2.getNormalizedCi()>o1.getNormalizedCi()) return 1;
	        	if(o2.getNormalizedCi()<o1.getNormalizedCi()) return -1;
	        	return 0;
	        }
	    });	    
	    
	    int summarizedSize = (int)  Math.ceil(ranked.size() * percentage);
	    
	    int count = 0;
	    for(RDFSentenceWithRank sent:ranked) {
	    	if(count>=summarizedSize) break;
	    	count++;
	    	statements.addAll(sent.getSentence().getStatements());

	    	System.out.println(sent.getSentence().getStatements().toString());
			System.out.println("score:"+sent.getNormalizedCi());
	    
	    }
	    
	    System.out.println("Percentage:" + percentage);
	    System.out.println("RDF-sentences in original graph: "+rankedSentences.size());
		System.out.println("RDF-sentences in summarized graph: "+count);

	    System.out.println("Statements in original graph: "+graph.getStatements().size());
		System.out.println("Statements in summarized graph: "+statements.size());
	    
	    
		return statements;
		
	}	
	
	public Set<Statement> summarizeBySize(double percentage) {
		assert percentage <= 1.0;
		
		Set<Statement> statements = new HashSet<Statement>();
		
	    
	    List<RDFSentenceWithRank> reRanked = reRank();
	    
	    
	    int summarizedSize = (int) Math.ceil(reRanked.size() * percentage);
	    
	    int count = 0;
	    for(RDFSentenceWithRank sent:reRanked) {
	    	if(count>=summarizedSize) break;
	    	count++;
	    	statements.addAll(sent.getSentence().getStatements());

	    	System.out.println(sent.getSentence().getStatements().toString());
			System.out.println("score:"+sent.getReRankScore());
	    
	    }
	    
	    System.out.println("Percentage:" + percentage);
	    System.out.println("RDF-sentences in original graph: "+rankedSentences.size());
		System.out.println("RDF-sentences in summarized graph: "+count);

	    System.out.println("Statements in original graph: "+graph.getStatements().size());
		System.out.println("Statements in summarized graph: "+statements.size());
	    
	    
		return statements;
		
	}
	
	private double reward(RDFSentenceWithRank i, Set<RDFSentenceWithRank> S) {
		double seqs = 0.0;
		//convert it to dynamic programming for effeciency
		Iterator<RDFSentenceWithRank> ib = S.iterator();
		Iterator<RDFSentenceWithRank> ia = S.iterator();
		while(ia.hasNext()) {
			RDFSentenceWithRank a = ia.next();
			while(ib.hasNext()) {
				
				RDFSentenceWithRank b = ib.next();
				//System.out.println("a:"+a+" , b:"+b);
				if(a.equals(b)==false) {
					if(a.getSentence().hasSequentialLink(b.getSentence())) {
						seqs+=1.0;
					}
				}
			}
		}
		
		double iCount = 0.0;
		Iterator<RDFSentenceWithRank> ic = S.iterator();
		while(ic.hasNext()) {
			RDFSentenceWithRank a = ic.next();
			if(a.equals(i)==false) {
				if(a.getSentence().hasSequentialLink(i.getSentence())) {
					iCount+=1.0;
				}
			}
		}

		double score = 1.0;
		if((seqs+iCount)!=0.0)
			score -= (seqs/(seqs+iCount));
		
		//System.out.println("seqs:"+seqs);
		//System.out.println("iCount:"+iCount);		
		//System.out.println("reward:"+score);
		return score;
	}

	private double penalty(RDFSentenceWithRank i, Set<RDFSentenceWithRank> S) {
		double cors = 0.0;
		Iterator<RDFSentenceWithRank> ib = S.iterator();
		Iterator<RDFSentenceWithRank> ia = S.iterator();
		while(ia.hasNext()) {
			RDFSentenceWithRank a = ia.next();
			while(ib.hasNext()) {
				
				RDFSentenceWithRank b = ib.next();
				if(a.equals(b)==false) {
					if(a.getSentence().hasCoordinateLink(b.getSentence())) {
						cors+=1.0;
					}
				}
			}
		}
		
		double iCount = 0.0;
		Iterator<RDFSentenceWithRank> ic = S.iterator();
		while(ic.hasNext()) {
			RDFSentenceWithRank a = ic.next();
			if(a.equals(i)==false) {
				if(a.getSentence().hasCoordinateLink(i.getSentence())) {
					iCount+=1.0;
				}
			}
		}
		
		double score = 1.0;
		if((cors+iCount)!=0.0)
			score -= (cors/(cors+iCount));

		//System.out.println("cors:"+cors);
		//System.out.println("iCount:"+iCount);		
		//System.out.println("reward:"+score);
		
		return score;
	}

	private List<RDFSentenceWithRank> reRank() {
		
		Set<RDFSentenceWithRank> S = new HashSet<RDFSentenceWithRank>();
		
	    List<RDFSentenceWithRank> ranked = new LinkedList<RDFSentenceWithRank>(rankedSentences.values());

		Collections.sort(ranked, new Comparator<RDFSentenceWithRank>() {

	        public int compare(RDFSentenceWithRank o1, RDFSentenceWithRank o2) {
	        	
	        	if(o2.getNormalizedCi()>o1.getNormalizedCi()) return 1;
	        	if(o2.getNormalizedCi()<o1.getNormalizedCi()) return -1;
	        	return 0;
	        }
	    });		
		
		for(int iii=0;iii<ranked.size();iii++) {

			//System.out.println("iii:"+iii);
			RDFSentenceWithRank iMax=null;
			double maxScore = Double.NEGATIVE_INFINITY;
			//int jjj=0;
			for(RDFSentenceWithRank i:ranked) {
				/*if(iii==0 && jjj==0) {
					System.out.println("i:"+i.getSentence().toString());
					System.out.println("ci:"+i.getCi());
				}
				jjj++;*/
				if(S.contains(i)==false) {
					
					double score = i.getCi() + LAMBDA * reward(i, S) - (1 - LAMBDA) * penalty(i, S);
					//System.out.println("score:"+score);
					if(score>maxScore) {
						maxScore = score;
						iMax = i;
						//System.out.println("maxScore:"+maxScore);
						//System.out.println("iMax:"+i);
						//System.out.println("maxScore = Double.NEGATIVE_INFINITY"+(Double.NEGATIVE_INFINITY));

					}
				}
			}
			assert iMax!=null;
			iMax.setReRankScore(maxScore);
			S.add(iMax);
		}
		
		List<RDFSentenceWithRank> list = new LinkedList<RDFSentenceWithRank>(S);
	    
		Collections.sort(list, new Comparator<RDFSentenceWithRank>() {

	        public int compare(RDFSentenceWithRank o1, RDFSentenceWithRank o2) {
	        	
	        	if(o2.getReRankScore()>o1.getReRankScore()) return 1;
	        	if(o2.getReRankScore()<o1.getReRankScore()) return -1;
	        	return 0;
	        }
	    });
		
		return list;
	}
	
}
