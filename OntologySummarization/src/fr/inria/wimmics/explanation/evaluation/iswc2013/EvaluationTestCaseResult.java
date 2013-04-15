package fr.inria.wimmics.explanation.evaluation.iswc2013;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.wimmics.explanation.evaluation.CosineSimilarity;

public class EvaluationTestCaseResult {

	List<Double> cosineSimilarityQuestion1;
	double avgCosineSimilarityQuestion1;

	List<Double> cosineSimilarityQuestion2;
	double avgCosineSimilarityQuestion2;	
	
	Map<String,Integer> specialisation;
	Map<String,Integer> rdfKnowledge;
	Map<String,Integer> gender;
	Map<String,Integer> age;	
	
	public EvaluationTestCaseResult() {
		cosineSimilarityQuestion1 = new ArrayList<Double>();
		cosineSimilarityQuestion2 = new ArrayList<Double>();
	}
	
	public void AddCosineSimilarityQuestion1(Double d) {
		cosineSimilarityQuestion1.add(d);
	}

	public void AddCosineSimilarityQuestion2(Double d) {
		cosineSimilarityQuestion2.add(d);
	}

	//generated code below
	public void setAvgCosineSimilarityQuestion1(
			double avgCosineSimilarityQuestion1) {
		this.avgCosineSimilarityQuestion1 = avgCosineSimilarityQuestion1;
	}
	
	public double getAvgCosineSimilarityQuestion1() {
		return avgCosineSimilarityQuestion1;
	}

	public List<Double> getCosineSimilarityQuestion1() {
		return cosineSimilarityQuestion1;
	}

	public void setCosineSimilarityQuestion1(List<Double> cosineSimilarityQuestion1) {
		this.cosineSimilarityQuestion1 = cosineSimilarityQuestion1;
	}

	public Map<String, Integer> getSpecialisation() {
		return specialisation;
	}

	public void setSpecialisation(Map<String, Integer> specialisation) {
		this.specialisation = specialisation;
	}

	public Map<String, Integer> getRdfKnowledge() {
		return rdfKnowledge;
	}

	public void setRdfKnowledge(Map<String, Integer> rdfKnowledge) {
		this.rdfKnowledge = rdfKnowledge;
	}

	public Map<String, Integer> getGender() {
		return gender;
	}

	public void setGender(Map<String, Integer> gender) {
		this.gender = gender;
	}

	public Map<String, Integer> getAge() {
		return age;
	}

	public void setAge(Map<String, Integer> age) {
		this.age = age;
	}
	
	public void setAvgCosineSimilarityQuestion2(
			double avgCosineSimilarityQuestion2) {
		this.avgCosineSimilarityQuestion2 = avgCosineSimilarityQuestion2;
	}
	public List<Double> getCosineSimilarityQuestion2() {
		return cosineSimilarityQuestion2;
	}
	public void setCosineSimilarityQuestion2(List<Double> cosineSimilarityQuestion2) {
		this.cosineSimilarityQuestion2 = cosineSimilarityQuestion2;
	}	
	
	public double getAvgCosineSimilarityQuestion2() {
		return avgCosineSimilarityQuestion2;
	}
}
