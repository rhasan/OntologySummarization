package fr.inria.wimmics.util;

import java.util.List;

public class Statistics {

	
	public static double standardDeviation(double mean, double[] values) {
		
		double sum = 0.0;
		for(int i=0;i<values.length;i++) {
			sum += ((values[i]-mean)*(values[i]-mean));
		}
		return Math.sqrt(sum/values.length);
	}
	
	public static double standardDeviation(double mean, List<Double> values) {
		
		double[] valuesArray = new double[values.size()];
		for(int i=0;i<values.size();i++) {
			valuesArray[i] = values.get(i);
		}
		return standardDeviation(mean, valuesArray);
	}
}
