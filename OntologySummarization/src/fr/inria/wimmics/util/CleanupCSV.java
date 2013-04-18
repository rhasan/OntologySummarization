package fr.inria.wimmics.util;


import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import fr.inria.wimmics.explanation.evaluation.test.DCGSurveyEntry;
import fr.inria.wimmics.explanation.evaluation.test.DCGSurveyEntryProcessor;

public class CleanupCSV {
	public static String QUESTION_REGEX = "^(.+)\\s+\\[(.+)\\]$";
	public static String EXCLUDE_HEADER = "q-specialisation  [other]";
	public static String SPECIALISATION_HEADER = "q-specialisation";
	public static String OTHER_SPECIALISATION_HEADER = "q-specialisation  [other]";
	public static String RDF_KNOWLEDGE_HEADER = "q-rdf-knowledge";
	public static String AGE_HEADER = "q-age";
	public static String GENDER_HEADER = "q-gender";
	List<String[]> allOriginalValues = null;
	
	int qSpecialisationIndex = -1;
	int qSpecialisationOtherIndex = -1;
	int rdfKnowledgeIndex = -1;
	int genderHeaderIndex = -1;
	int ageHeaderIndex = -1;
	
	
	Map<String,Integer> specialisation = new HashMap<String,Integer>();
	Map<String,Integer> rdfKnowledge = new HashMap<String,Integer>();
	Map<String,Integer> gender = new HashMap<String,Integer>();
	Map<String,Integer> age = new HashMap<String,Integer>();

	Map<String, Map<String, Double> > avgRatings = new HashMap<String, Map<String, Double>>();
	
	List<DCGSurveyEntry> surveyEntries = new ArrayList<DCGSurveyEntry>();
	
	
	private class QuestionHeader {
		String questionName = "";
		String statementName = "";
		int index;
		
		public QuestionHeader(String questionHeaderName, String statementName) {
			super();
			this.questionName = questionHeaderName;
			this.statementName = statementName;
		}
		public QuestionHeader() {
			String questionHeaderName = "";
			String statementName = "";
			index = -1;
			
		}
	}
	
	

	public static void main(String[] args) throws IOException {
		CleanupCSV processor = new CleanupCSV("files/evaluation/dcg/survey/inf1/results-survey31573.csv");
		
		processor.printCSV();
		
		processor.cleanUp();
		processor.printCSV();
		
		processor.writeCSV("files/evaluation/dcg/survey/inf1/testcase1.csv");
	}
	
	public void writeCSV(String newFilePath) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(newFilePath));
        CSVWriter writer = new CSVWriter(out);


        //writer.writeAll(allOriginalValues);

		for(String row[]:allOriginalValues) {
			writer.writeNext(row);
		}
        out.close();
	
	}
	
	public List<String[]> readCSV(String filePath) throws IOException{
		CSVReader reader = new CSVReader(new FileReader(filePath));
		return reader.readAll();
	}
	
	public void printCSV() {
		for(String row[]:allOriginalValues) {
			for(String column:row) {
				System.out.print(column+" ");
			}
			System.out.println();
	
		}
		
	}
		
	
	
	public CleanupCSV(String filePath) throws IOException {
		allOriginalValues = readCSV(filePath);
	}

	public void cleanUp() throws IOException {
		
		
		assert(allOriginalValues.size()>1);
		
		String[] headers = allOriginalValues.get(0);
		List<String[]> values = allOriginalValues.subList(1, allOriginalValues.size());
				
		//header index
		
		Map<String,Integer> headerIndex = new HashMap<String,Integer>();
		
		for(int i=0;i<headers.length;i++) {
			headerIndex.put(headers[i].toLowerCase(), i);
		}
		
		//set specialisation values
		qSpecialisationIndex = headerIndex.get(SPECIALISATION_HEADER);
		qSpecialisationOtherIndex = headerIndex.get(OTHER_SPECIALISATION_HEADER);
		rdfKnowledgeIndex = headerIndex.get(RDF_KNOWLEDGE_HEADER);
		genderHeaderIndex = headerIndex.get(GENDER_HEADER);
		ageHeaderIndex = headerIndex.get(AGE_HEADER);
		
		
		for(String[] entries:values) {

			int spCount = 0;			
			
//			String specialisationStr = entries[qSpecialisationIndex].toLowerCase();
//			
//			if(specialisationStr.equals("other")) {
//				specialisationStr = entries[qSpecialisationOtherIndex].toLowerCase();
//				if(specialisationStr.isEmpty()) {
//					specialisationStr = "other";
//				}
//			}
			
			String specialisationStr = getSpecialisationValeu(entries,qSpecialisationIndex,qSpecialisationOtherIndex).toLowerCase();
			if(specialisation.containsKey(specialisationStr)) {
				spCount = specialisation.get(specialisationStr);
			}
			spCount++;
			
			specialisation.put(specialisationStr, spCount);
			
			
		
			putInCountHashMap(entries[rdfKnowledgeIndex], rdfKnowledge);
			putInCountHashMap(entries[genderHeaderIndex], gender);
			putInCountHashMap(entries[ageHeaderIndex], age);
			
			
		}
		
		
		List<QuestionHeader> questionHeaders = new ArrayList<QuestionHeader>();
		
		for(int i=0;i<headers.length;i++) {
			if(headers[i].matches(QUESTION_REGEX) && headers[i].equals(EXCLUDE_HEADER)==false) {
				//System.out.println(headers[i]);
				QuestionHeader qh = getStatementName(headers[i]);
				qh.index = i;
				questionHeaders.add(qh);
				//System.out.println(qh.questionHeaderName+":"+qh.statementName);
			}	
		}
		
		//compute average scores for all use cases/questions
	 	
			
		for(QuestionHeader qh:questionHeaders) {
			
			
			Map<String, Double> statementRatings = null;
			if(avgRatings.containsKey(qh.questionName)) {
				statementRatings = avgRatings.get(qh.questionName);
			
			} else {
				statementRatings = new HashMap<String,Double>();
				
			}
			
			//modify the statement ratings map
			double ratingVal = 0.0;
			if(statementRatings.containsKey(qh.statementName)) {
				ratingVal = statementRatings.get(qh.statementName);
			} 
//			else {
//				statementRatings.put(qh.statementName, 0.0);
//			}
			int rowNum =0;
			for(String[] entries:values) {
				String ratingStr = entries[qh.index];
				double trv = Double.valueOf(ratingStr);
				
				double nTrv = (trv/10)*5.0;
				double ceil = Math.ceil(nTrv) ;
				double floor = Math.ceil(nTrv);
				double fraction = nTrv - floor;
				double modified = fraction>0.5?ceil:floor;
				
				entries[qh.index] = Double.toString(modified);
				
				rowNum++;
			}
			

			avgRatings.put(qh.questionName, statementRatings);
			
		}
	}
	
	private String getSpecialisationValeu(String[] entries, int qSpecialisationIndex, int qSpecialisationOtherIndex) {
		String specialisationStr = entries[qSpecialisationIndex];
		
		if(specialisationStr.equals("Other")) {
			specialisationStr = entries[qSpecialisationOtherIndex];
			if(specialisationStr.isEmpty()) {
				specialisationStr = "Other";
			}
		}
		return specialisationStr;
		
	}
	
	private boolean isSpecialisationHead(String header) {
		return header.equals(SPECIALISATION_HEADER);
	}
	
	private boolean isProfileHeader(String header) {
		return header.equals("id") || header.equals(SPECIALISATION_HEADER) || header.equals(RDF_KNOWLEDGE_HEADER) || header.equals(AGE_HEADER) || header.equals(GENDER_HEADER);

	}
	private boolean isQuestionHeader(String header) {
		return (header.matches(QUESTION_REGEX) && header.equals(EXCLUDE_HEADER)==false);
	}
	
	private void putInCountHashMap(String key, Map<String,Integer> hashMap) {
		int count = 0;

		if(hashMap.containsKey(key)) {
			count = hashMap.get(key);
		}
		count++;
		
		hashMap.put(key, count);
		
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
	
	public QuestionHeader getStatementName(String line) {
	      // String to be scanned to find the pattern.

	      //String line = "q1 [d5]";

	      String pattern = QUESTION_REGEX;

	      // Create a Pattern object

	      Pattern r = Pattern.compile(pattern);

	      // Now create matcher object.

	      Matcher m = r.matcher(line);

	      if (m.find( )) {

	         return new QuestionHeader(m.group(1), m.group(2));

	      } else {

	         //System.out.println("NO MATCH");
	    	  return new QuestionHeader("", "");

	      }		
	}
	
}
