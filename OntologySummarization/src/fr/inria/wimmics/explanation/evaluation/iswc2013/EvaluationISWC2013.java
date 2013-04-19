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
		
		
		
	}
	
	public static void main(String[] args) throws Exception {
		
		EvaluationISWC2013 iswc2013 = new EvaluationISWC2013();
		EvaluationTestCase etc1 = iswc2013.getTestCase1();
		
		EvaluationTestCaseResult etcResult1 = etc1.evaluate();
		
		iswc2013.generateMatlabCode(etcResult1);
		
		
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
	
	private HashMap<String,Integer> order = new HashMap<String,Integer>();
		
}
