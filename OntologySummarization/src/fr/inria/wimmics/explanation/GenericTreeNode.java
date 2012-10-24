package fr.inria.wimmics.explanation;


public class GenericTreeNode<Item> {
	private Item object;
	private int nSubTree;
	private int size;
	private double score;
	private double normalizedScore;

	private int color;
	private GenericTreeNode<Item> path;
	private int distance;
	private int finish;


	public GenericTreeNode() {
		nSubTree = 0;
		size = 0;
		score = 0.0;
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
}
