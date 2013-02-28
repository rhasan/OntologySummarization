package fr.inria.wimmics.explanation;


public class GenericTreeNode<Item> {
	private Item object;
	private int nSubTree;
	private int size;
	private double score;
	private double normalizedScore;
	private double degreeCentrality;
	private double similarityScore;
	private double reRankScore;
	private double combinedScore;

	private int color;
	private GenericTreeNode<Item> path;
	private int distance;
	private int finish;


	public GenericTreeNode() {
		nSubTree = 0;
		size = 0;
		score = 0.0;
		degreeCentrality = 0.0;
		similarityScore = 0.0;
		reRankScore = 0.0;
		combinedScore = 0.0;
		
	}
	
	public double getCombinedScore() {
		return combinedScore;
	}

	public void setCombinedScore(double combinedScore) {
		this.combinedScore = combinedScore;
	}

	public double getNormalizedScore() {
		return normalizedScore;
	}
	public void setNormalizedScore(double normalizedScore) {
		this.normalizedScore = normalizedScore;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getFinish() {
		return finish;
	}

	public void setFinish(int finish) {
		this.finish = finish;
	}

	public int getColor() {
		return color;
	}
	public GenericTreeNode<Item> getPath() {
		return path;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public void setPath(GenericTreeNode<Item> path) {
		this.path = path;
	}
	public Item getObject() {
		return object;
	}
	public void setObject(Item object) {
		this.object = object;
	}
	public int getnSubTree() {
		return nSubTree;
	}
	public void setnSubTree(int nSubTree) {
		this.nSubTree = nSubTree;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}

	public double getDegreeCentrality() {
		return degreeCentrality;
	}

	public void setDegreeCentrality(double degreeCentrality) {
		this.degreeCentrality = degreeCentrality;
	}

	public double getSimilarityScore() {
		return similarityScore;
	}

	public void setSimilarityScore(double similarityScore) {
		this.similarityScore = similarityScore;
	}

	public double getReRankScore() {
		return reRankScore;
	}

	public void setReRankScore(double reRankScore) {
		this.reRankScore = reRankScore;
	}
	
}
