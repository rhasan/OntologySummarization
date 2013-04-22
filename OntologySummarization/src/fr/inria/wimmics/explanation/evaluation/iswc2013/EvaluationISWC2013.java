package fr.inria.wimmics.explanation.evaluation.iswc2013;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import fr.inria.wimmics.explanation.evaluation.RankEntry;
import fr.inria.wimmics.util.DefaultTreeMap;

public class EvaluationISWC2013 {

	public EvaluationTestCase getTestCase1() {
		EvaluationTestCase etc = new EvaluationTestCase();
		etc.setQuestion1Name("q1");
		etc.setQuestion2Name("q2");

		etc.setRatingFilePath("files/evaluation/dcg/survey/inf1/testcase1.csv");
		etc.setRootStatement("http://alphubel.unice.fr:8080/lodutil/data/d21");
		
		etc.setJustificationFilePath("rdf/inference1.trig");
		
		etc.setInstanceFilePath("rdf/inference1-instance.rdf");
		etc.setSimilarityConcept("http://dbpedia.org/ontology/Event");
		etc.setDbpediaSchemaLocation("rdf/ontology/dbpedia_3.8.owl");
		etc.setGeonamesSchemaLocation("rdf/ontology/geonames_ontology_v3.1.rdf");
		
		
		
		return etc;
	}

	public EvaluationTestCase getTestCase2() {
		EvaluationTestCase etc = new EvaluationTestCase();
		etc.setQuestion1Name("q1");
		etc.setQuestion2Name("q2");

		etc.setRatingFilePath("files/evaluation/dcg/survey/inf2/results-survey22277.csv");
		etc.setRootStatement("http://alphubel.unice.fr:8080/lodutil/data/d24");
		
		etc.setJustificationFilePath("rdf/inference2-just.trig");
		
		etc.setInstanceFilePath("rdf/inference2-instance.rdf");
		etc.setSimilarityConcept("http://dbpedia.org/ontology/Infrastructure");
		etc.setDbpediaSchemaLocation("rdf/ontology/dbpedia_3.8.owl");
		etc.setGeonamesSchemaLocation("rdf/ontology/geonames_ontology_v3.1.rdf");
		
		
		
		return etc;
	}

	public EvaluationTestCase getTestCase3() {
		EvaluationTestCase etc = new EvaluationTestCase();
		etc.setQuestion1Name("q1");
		etc.setQuestion2Name("q2");

		etc.setRatingFilePath("files/evaluation/dcg/survey/inf3/results-survey58921.csv");
		etc.setRootStatement("http://alphubel.unice.fr:8080/lodutil/data/d1");
		
		etc.setJustificationFilePath("rdf/inference3-just.trig");
		
		etc.setInstanceFilePath("rdf/inference3-instance.rdf");
		etc.setSimilarityConcept("http://dbpedia.org/ontology/Place");
		etc.setDbpediaSchemaLocation("rdf/ontology/dbpedia_3.8.owl");
		etc.setGeonamesSchemaLocation("rdf/ontology/geonames_ontology_v3.1.rdf");
		
		
		
		return etc;
	}
	
	
	public String plotVector(List<Double> x, List<Double> y, String legend,String xLabel, String yLabel, int count) {
		
		
		//System.out.println("count:"+x.size());
		String xStr = "x"+String.valueOf(count)+" = [";
		
		for(double v:x) {
			String t = (" "+String.valueOf(v));
			xStr += t;
			//System.out.println("val:"+v);
		}
		xStr += "];\n";
		
		String yStr = "y"+String.valueOf(count)+" = [";
		for(double v:y) {
			String t = (" "+String.valueOf(v));
			yStr += t;
		}
		yStr += "];\n";
		
		/*String plot = "plot(x"+String.valueOf(count)+",y"+String.valueOf(count)+")\n";
		
		String grid = "grid on\n";
		String xl = "xlabel('"+xLabel+"')\n";
		String yl = "ylabel('"+yLabel+"')\n";
		String axis = "axis([0 1 0 1])\n";
		String lg = "legend('"+legend+"')\n";
		String hold = "hold on";
		
		
		return xStr+yStr+plot+grid+xl+yl+axis+lg+hold;
		*/
		System.out.println("%"+legend);
		return yStr;
	}
	public String matlabVector(List<Double> x, String x_or_y_var, int count) {
		
		String str =x_or_y_var+String.valueOf(count)+" = [";
		
		for(double v:x) {
			String t = (" "+String.valueOf(v));
			str += t;
			//System.out.println("val:"+v);
		}
		str += "];\n";
	
		return str;
	}
	
	public void generateMatlabCode(EvaluationTestCaseResult etcResult) {
		
		List<Double> crValues = etcResult.getCrValuesList();
		
		
		System.out.println("%######################### [CR vs nDCG: without similarity] ################################");
		int count = 0;
		
		EntryResCmp cmp = new EntryResCmp();
		
		DefaultTreeMap<String, List<Double>> tt = new DefaultTreeMap<String, List<Double>>(cmp,null);
		tt.putAll(etcResult.getNdcgValues());
		
		for(Entry<String, List<Double>> entry:tt.entrySet()) {
			String mlCode = plotVector(crValues, entry.getValue(), entry.getKey(),"CR","nDCG", count);
			//System.out.println(entry.getValue().size());
			System.out.println(mlCode);
			count++;
		}
		
		
		
		//with similarity
		TreeMap<String, List<Double>> tt1 = new TreeMap<String, List<Double>>(cmp);
		tt1.putAll(etcResult.getNdcgValuesWithSimilarity());
		
		System.out.println("%######################### [CR vs nDCG: with similarity] ################################");
		for(Entry<String, List<Double>> entry:tt1.entrySet()) {
			String mlCode = plotVector(crValues, entry.getValue(), entry.getKey(),"CR","nDCG", count);
			//System.out.println(entry.getValue().size());
			System.out.println(mlCode);
			count++;
		}
		
		
		
		//cr vs f-measure without similarity
		count =0;
		System.out.println("%######################### [CR vs F-score: without similarity] ################################");
		TreeMap<String, List<Double>> f_score1 = new TreeMap<String, List<Double>>(cmp);
		f_score1.putAll(etcResult.getFmeasureValues());
		for(Entry<String, List<Double>> entry:f_score1.entrySet()) {
			String mlCode = plotVector(crValues, entry.getValue(), entry.getKey(),"CR","F-score", count);
			//System.out.println(entry.getValue().size());
			System.out.println(mlCode);
			count++;
			
		}
		
		System.out.println("%######################### [CR vs F-score: with similarity] ################################");
		TreeMap<String, List<Double>> f_score2 = new TreeMap<String, List<Double>>(cmp);
		f_score2.putAll(etcResult.getFmeasureValuesWithSimilarity());
		for(Entry<String, List<Double>> entry:f_score2.entrySet()) {
			String mlCode = plotVector(crValues, entry.getValue(), entry.getKey(),"CR","F-score", count);
			//System.out.println(entry.getValue().size());
			System.out.println(mlCode);
			count++;
			
		}		
		
		System.out.println("%######################### [Cosine similarity without similarity] ################################");
		String mlCode = matlabVector(etcResult.getCosineSimilarityQuestion1(), "x", 0);
		System.out.println(mlCode);
		
		System.out.println("%######################### [Cosine similarity with similarity] ################################");
		String mlCode1 = matlabVector(etcResult.getCosineSimilarityQuestion2(), "x", 1);
		System.out.println(mlCode1);
		
		System.out.println("%######################### [Kendall tau human agreement without similarity] ################################");
		String mlCode2 = matlabVector(etcResult.getKendallTauQuestion1(), "x", 2);
		System.out.println(mlCode2);	
		
		System.out.println("%######################### [Kendall tau human agreement with similarity] ################################");
		String mlCode3 = matlabVector(etcResult.getKendallTauQuestion2(), "x", 3);
		System.out.println(mlCode3);
		
		
		
	}

	
	class EntryResCmp implements Comparator<String> {
		
	@Override
	public int compare(String o1, String o2) {
		int o1Val = getOrder(o1);
		int o2Val = getOrder(o2);
		return o1Val-o2Val;
	}

//		@Override
//		public int compare(Entry<String, List<Double>> o1,
//				Entry<String, List<Double>> o2) {
//
//			int o1Val = getOrder(o1.getKey());
//			int o2Val = getOrder(o2.getKey());
//			return o1Val-o2Val;
//		}
		
	}
	
	public EvaluationISWC2013() {
		setOrder();
		
	}
	private HashMap<String,Integer> order = new HashMap<String,Integer>();

	public void setOrder() {
		order.put("S_{SL}",1);
		order.put("S_{SL}+S_{AB}",2);
		order.put("S_{SL}+S_{CO}",3);
		order.put("S_{SL}+S_{ST}",4);
		
		order.put("S_{SL}+S_{AB}+S_{CO}",5);
		order.put("S_{SL}+S_{AB}+S_{ST}",6);
		order.put("S_{SL}+S_{ST}+S_{CO}",7);
		order.put("S_{SL}+S_{AB}+S_{ST}+S_{CO}",8);
		
		
		order.put("S_{SG}",9);
		
		//with similarity
		
		order.put("S_{SL}+S_{SM}",10);
		order.put("S_{SL}+S_{AB}+S_{SM}",11);
		order.put("S_{SL}+S_{SM}+S_{CO}",12);
		order.put("S_{SL}+S_{SM}+S_{ST}",13);
		
		order.put("S_{SL}+S_{AB}+S_{SM}+S_{CO}",14);
		order.put("S_{SL}+S_{AB}+S_{SM}+S_{ST}",16);
		order.put("S_{SL}+S_{SM}+S_{ST}+S_{CO}",17);
		order.put("S_{SL}+S_{AB}+S_{SM}+S_{ST}+S_{CO}",18);
		
		
		
		
	}
	
	public int getOrder(String key) {
		if(order.containsKey(key))
			return order.get(key);
		
		return key.hashCode();
	}
	
	public void mergeList(List<Double> valueList1, List<Double> valueList2) {
		
		
		
		if(valueList2.size()==0) {
			
			for(double d:valueList1) {
				valueList2.add(d);
			}
			return;
		}
		
		
		
		for(int i=0;i<valueList1.size();i++) {
			double mVal = (valueList1.get(i)+valueList2.get(i))/2.0;
			valueList2.set(i, mVal);
		}
	}
	

	public EvaluationTestCaseResult mergeResults(List<EvaluationTestCaseResult> allResults) {
		EvaluationTestCaseResult mergedResult = new EvaluationTestCaseResult();
		
		
		EntryResCmp cmp = new EntryResCmp();
		
		List<Double> crValues = allResults.get(0).getCrValuesList();
		mergedResult.setCrValuesList(crValues);
		
		DefaultTreeMap<String,List<Double>> ndcgValues = new DefaultTreeMap<String,List<Double>>(new ArrayList<Double>());
		
		DefaultTreeMap<String,List<Double>> fscoreValues = new DefaultTreeMap<String,List<Double>>(new ArrayList<Double>());
		
		DefaultTreeMap<String,List<Double>> ndcgValuesSim = new DefaultTreeMap<String,List<Double>>(new ArrayList<Double>());
		
		DefaultTreeMap<String,List<Double>> fscoreValuesSim = new DefaultTreeMap<String,List<Double>>(new ArrayList<Double>());		
		
		List<Double> cosineQ1 = new ArrayList<Double>();
		List<Double> cosineQ2 = new ArrayList<Double>();
		double avgCosineQ1 = 0.0;
		double avgCosineQ2 = 0.0;
		
		//List<Double> fscoreValues = new ArrayList<Double>();
		
		for(EvaluationTestCaseResult result:allResults) {
			

			
			//ndcg
			DefaultTreeMap<String, List<Double>> tt = new DefaultTreeMap<String, List<Double>>(cmp,null);
			tt.putAll(result.getNdcgValues());
			
			for(Entry<String, List<Double>> entry:tt.entrySet()) {
				String key = entry.getKey();
				List<Double> valueList = entry.getValue();
				
				
				List<Double>  ndcgValuesList = ndcgValues.get(key);			
				
				mergeList(valueList, ndcgValuesList); //merge result is stored in the second one
				
				//if(ndcgValues.containsKey(key)==false) {
					ndcgValues.put(key, ndcgValuesList);
				//}
				
			}
			
			//f-score
			DefaultTreeMap<String, List<Double>> tt1 = new DefaultTreeMap<String, List<Double>>(cmp,null);
			tt1.putAll(result.getFmeasureValues());
			
			for(Entry<String, List<Double>> entry:tt1.entrySet()) {

				String key = entry.getKey();
				List<Double> valueList = entry.getValue();
				
				List<Double>  fscoreValuesList = fscoreValues.get(key);				
				
				mergeList(valueList, fscoreValuesList); //merge result is stored in the second one
				
				//if(fscoreValues.containsKey(key)==false) {
					fscoreValues.put(key, fscoreValuesList);
				//}			
				
			}
			
			//cosine q1
			cosineQ1.addAll(result.getCosineSimilarityQuestion1());
			avgCosineQ1 += result.getAvgCosineSimilarityQuestion1();
			
			
			//with similarity below
			
			//ndcg
			DefaultTreeMap<String, List<Double>> tt3 = new DefaultTreeMap<String, List<Double>>(cmp,null);
			tt3.putAll(result.getNdcgValuesWithSimilarity());
			
			for(Entry<String, List<Double>> entry:tt3.entrySet()) {
				String key = entry.getKey();
				List<Double> valueList = entry.getValue();
				
				
				List<Double>  ndcgValuesList = ndcgValuesSim.get(key);				
				
				mergeList(valueList, ndcgValuesList); //merge result is stored in the second one
				
				//if(ndcgValuesSim.containsKey(key)==false) {
					ndcgValuesSim.put(key, ndcgValuesList);
				//}				
				
			}
			
			//f-score
			DefaultTreeMap<String, List<Double>> tt4 = new DefaultTreeMap<String, List<Double>>(cmp,null);
			tt4.putAll(result.getFmeasureValuesWithSimilarity());
			
			for(Entry<String, List<Double>> entry:tt4.entrySet()) {

				String key = entry.getKey();
				List<Double> valueList = entry.getValue();
				
				List<Double>  fscoreValuesList = fscoreValuesSim.get(key);				
				
				mergeList(valueList, fscoreValuesList); //merge result is stored in the second one
				//if(fscoreValuesSim.containsKey(key)==false) {
					fscoreValuesSim.put(key, fscoreValuesList);
				//}
			}		
			
			//cosine q2
			cosineQ2.addAll(result.getCosineSimilarityQuestion2());
			avgCosineQ2 += result.getAvgCosineSimilarityQuestion2();
			
			
		}
		avgCosineQ1 = avgCosineQ1/allResults.size();
		avgCosineQ2 = avgCosineQ2/allResults.size();
		
		
		mergedResult.setCosineSimilarityQuestion1(cosineQ1);
		mergedResult.setCosineSimilarityQuestion2(cosineQ2);
		mergedResult.setFmeasureValues(fscoreValues);
		mergedResult.setNdcgValues(ndcgValues);
		mergedResult.setFmeasureValuesWithSimilarity(fscoreValuesSim);
		mergedResult.setNdcgValuesWithSimilarity(ndcgValuesSim);
		
		return mergedResult;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		
		EvaluationISWC2013 iswc2013 = new EvaluationISWC2013();
		EvaluationTestCase etc1 = iswc2013.getTestCase1();
		EvaluationTestCase etc2 = iswc2013.getTestCase2();
		EvaluationTestCase etc3 = iswc2013.getTestCase2();
		
		
		EvaluationTestCaseResult etcResult1 = etc1.evaluate();
		EvaluationTestCaseResult etcResult2 = etc2.evaluate();
		EvaluationTestCaseResult etcResult3 = etc3.evaluate();
		//EvaluationTestCaseResult etcResult1 = etc1.evaluateTest();
		//TODO: merge all the testcases to 1 result by averaging 
		
		List<EvaluationTestCaseResult> allResults = new ArrayList<EvaluationTestCaseResult>();
		allResults.add(etcResult1);
		allResults.add(etcResult2);
		allResults.add(etcResult3);
		
		
		EvaluationTestCaseResult mergedResult = iswc2013.mergeResults(allResults);
		iswc2013.generateMatlabCode(mergedResult);
		
		
	}	
}
