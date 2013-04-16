package fr.inria.wimmics.explanation.evaluation.iswc2013;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class EvaluationISWC2013 {

	public EvaluationTestCase getTestCase1() {
		EvaluationTestCase etc = new EvaluationTestCase();
		etc.setQuestion1Name("q1");
		etc.setQuestion2Name("q2");

		etc.setRatingFilePath("files/evaluation/dcg/survey/inf1/results-survey31573.csv");
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
		
		
		
		int count = 0;
		
		
		
		for(Entry<String, List<Double>> entry:etcResult.getNdcgValues().entrySet()) {
			String mlCode = plotVector(crValues, entry.getValue(), entry.getKey(),"CR","nDCG", count);
			//System.out.println(entry.getValue().size());
			System.out.println(mlCode);
			count++;
		}
		
		
		
		//with similarity
		System.out.println("######################### with similarity ################################");
		for(Entry<String, List<Double>> entry:etcResult.getNdcgValuesWithSimilarity().entrySet()) {
			String mlCode = plotVector(crValues, entry.getValue(), entry.getKey(),"CR","nDCG", count);
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
}
