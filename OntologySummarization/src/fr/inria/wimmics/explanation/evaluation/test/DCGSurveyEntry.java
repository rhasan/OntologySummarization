package fr.inria.wimmics.explanation.evaluation.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.inria.wimmics.explanation.evaluation.RankEntry;

public class DCGSurveyEntry {
	Map<String, String> profileInfo = new HashMap<String, String>();
	Map<String, Map<String, Double>> questions = new HashMap<String, Map<String, Double>>();
	
	
	public void putProfileInfo(String key, String value) {
		profileInfo.put(key, value);
	}
	
	public void putQustionAnswer(String questionName, String statementName, String answerVal) {
		Map<String, Double> val = null;
		val = questions.get(questionName);
		if(val==null) {
			val = new HashMap<String, Double>();
			questions.put(questionName, val);
		}
		val.put(statementName, Double.valueOf(answerVal));
	}
	
	public List<RankEntry> getQuestionAnswers(String questionName) {
		Map<String, Double> val = questions.get(questionName);
		//System.out.println(val);
		List<RankEntry> res = new ArrayList<RankEntry>();
		for(Entry<String, Double> e: val.entrySet()) {
			
			RankEntry re = new RankEntry();
			re.setName(e.getKey());
			re.setJudgmentScore(e.getValue());
			res.add(re);
		}
		//System.out.println(res.size());
		return res;
		
	}
}
