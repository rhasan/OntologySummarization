package fr.inria.wimmics.explanation.evaluation;

import java.util.Comparator;


public class RankEntry {
	int rank;
	String name;
	int otherRank;
	int judgmentScore; //relavence score
	
	public void setJudgmentScore(int judgementScore) {
		this.judgmentScore = judgementScore;
	}
	public int getJudgmentScore() {
		return judgmentScore;
	}
	
	public int getOtherRank() {
		return otherRank;
	}
	public void setOtherRank(int otherRank) {
		this.otherRank = otherRank;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
class EntryRankCmp implements Comparator<RankEntry> {

	@Override
	public int compare(RankEntry o1, RankEntry o2) {
		
		return o1.getRank()-o2.getRank();
	}
	
}

class EntryJudgmentDscCmp implements Comparator<RankEntry> {

	@Override
	public int compare(RankEntry o1, RankEntry o2) {
		
		return o2.getJudgmentScore()-o1.getJudgmentScore();
	}
	
}