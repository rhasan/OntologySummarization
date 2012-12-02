package fr.inria.wimmics.explanation.evaluation;

import java.util.Comparator;


public class Entry {
	int rank;
	String name;
	int otherRank;
	
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
class EntryCmp implements Comparator<Entry> {

	@Override
	public int compare(Entry o1, Entry o2) {
		
		return o1.getRank()-o2.getRank();
	}
	
}