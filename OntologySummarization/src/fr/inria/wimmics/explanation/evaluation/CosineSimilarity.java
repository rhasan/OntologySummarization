package fr.inria.wimmics.explanation.evaluation;

import java.util.List;

public class CosineSimilarity {


	public static double computeCosineSimilarity(List<RankEntry> a, List<RankEntry> b) throws Exception {
		
		double[] vecA = new double[a.size()];
		double[] vecB = new double[b.size()];
		
		//System.out.println("Vector A:");
		for(int i=0;i<a.size();i++) {
			if(a.get(i).getName().equals(b.get(i).getName())==false) {
				throw new Exception("Vectors don't have same orders");
			}
			vecA[i] = a.get(i).getJudgmentScore();
			//System.out.println(a.get(i).getName());
		}
		
		//System.out.println("Vector B:");
		for(int i=0;i<b.size();i++) {
			vecB[i] = b.get(i).getJudgmentScore();
			//System.out.println(b.get(i).getName());
		}
		
		
		return computeCosineSimilarity(vecA, vecB);
	}

	private static double computeCosineSimilarity(double[] vecA, double[] vecB)
	{
		double dotProduct = DotProduct(vecA, vecB);
		double magnitudeOfA = Magnitude(vecA);
		double magnitudeOfB = Magnitude(vecB);

		return dotProduct/(magnitudeOfA*magnitudeOfB);
	}

	private static double DotProduct(double[] vecA, double[] vecB)
	{
		// I'm not validating inputs here for simplicity.            
		double dotProduct = 0;
		for (int i = 0; i < vecA.length; i++)
		{
			dotProduct += (vecA[i] * vecB[i]);
		}

		return dotProduct;
	}

	
	private static double Magnitude(double[] vector)
	{
		return Math.sqrt(DotProduct(vector, vector));
	}	
	
	
	
	
}
