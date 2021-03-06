package fr.inria.wimmics.explanation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.openrdf.rio.trig.TriGParser;

import fr.inria.edelweiss.kgraph.logic.RDFS;
import fr.inria.wimmics.util.GeoNames;
import fr.inria.wimmics.util.MyMultiMap;
import fr.inria.wimmics.util.SesameUtil;

public class JustificationProcessor {
	
	GenericTree<Statement> g = new GenericTree<Statement>();
	GenericTree<KnowledgeStatement> gk = new GenericTree<KnowledgeStatement>();
	//Map<String,ArrayList<Statement>> stmtMap = new HashMap<String,ArrayList<Statement>>();
	
	MyMultiMap<String,Statement> stmtMap = new MyMultiMap<String,Statement>();
	MyMultiMap<String,KnowledgeStatement> kStmtMap = new MyMultiMap<String,KnowledgeStatement>();
	
	Map<String,String> resourceLabels = new HashMap<String,String>();
	

	Map<String,String> nameSpaces = null;
	private List<String> ontologyLocations;
	private List<String> instanceLocations;
	private Repository myRepository = null;
	List<Statement> rdfStatements = null;
	
	ArrayList<KnowledgeStatement> knStatements = new ArrayList<KnowledgeStatement>();
	
	public ArrayList<KnowledgeStatement> getKnStatements() {
		return knStatements;
	}
	
	public void parseJustificationFile(String filePath,String baseURI) throws RDFParseException, RDFHandlerException, IOException, RepositoryException {
		
		//linked data read by dereferencing
		//java.net.URL documentUrl = new URL(“http://example.org/example.ttl”);
		//InputStream inputStream = documentUrl.openStream();
		
		//local file read
		InputStream inputStream = new FileInputStream(filePath);
		RDFParser rdfParser = new TriGParser();
		
		ArrayList<Statement> myList = new ArrayList<Statement>();
		StatementCollector collector = new StatementCollector(myList);
		rdfParser.setRDFHandler(collector);
		rdfParser.parse(inputStream, baseURI);
		nameSpaces = collector.getNamespaces();
		
		for(Statement st:myList) {
			String stKey = st.getContext().stringValue();
			stmtMap.put(stKey, st);
			//System.out.println(stKey);


		}
		
		for(Statement st:myList) {
			
			if(st.getPredicate().toString().equals(Ratio4TA.derivedFrom)) {
				String keyA = st.getSubject().stringValue();
				String keyB = st.getObject().stringValue();
				
				//System.out.println("### "+keyA+" is derivedFrom"+keyB);
				
				Statement a = stmtMap.getFirst(keyA);
				Statement b = stmtMap.getFirst(keyB);
				g.insterEdge(a, b);

				if(kStmtMap.containsKey(keyA)==false) {
					KnowledgeStatement kst = new KnowledgeStatement(a);
					kStmtMap.put(keyA, kst);
					knStatements.add(kst);
				}
				if(kStmtMap.containsKey(keyB)==false) {
					KnowledgeStatement kst = new KnowledgeStatement(b);
					kStmtMap.put(keyB, kst);
					knStatements.add(kst);
				}

				KnowledgeStatement kst1 = kStmtMap.getFirst(keyA);
				KnowledgeStatement kst2 = kStmtMap.getFirst(keyB);
				gk.insterEdge(kst1, kst2);				
				
			}
						
		}
		
		rdfStatements = myList;
		loadStatementsInRepo();
		//System.out.println("Loaded knowledge statements:"+kStmtMap.size());
	}
	
	private void loadStatementsInRepo() throws RepositoryException {
		myRepository = SesameUtil.getMemoryBasedRepository();
		
		RepositoryConnection con = myRepository.getConnection();

		try {
			con.setAutoCommit(false);

			// Add the first file
			con.add(this.rdfStatements);

			// If everything went as planned, we can commit the result
			con.commit();
		} catch (RepositoryException e) {
			// Something went wrong during the transaction, so we roll it back
			con.rollback();
		} finally {
			// Whatever happens, we want to close the connection when we are
			// done.
			con.close();
		}		
	}
	
	public void loadResourceLabelFromOntology(String ontologyLocation,String baseURI) throws RDFParseException, RDFHandlerException, IOException {
		
		InputStream inputStream = new FileInputStream(ontologyLocation);
		RDFParser rdfParser = new RDFXMLParser();
		
		ArrayList<Statement> myList = new ArrayList<Statement>();
		StatementCollector collector = new StatementCollector(myList);
		rdfParser.setRDFHandler(collector);
		rdfParser.parse(inputStream, baseURI);
		
		for(Statement st:myList) {
			//System.out.println(st.toString());
			String predicateUri = st.getPredicate().stringValue().trim();
			//System.out.println(predicateUri.equals(GeoNames.name));
			//System.out.println("["+predicateUri+"]="+"["+GeoNames.name+"]");
			if(predicateUri.equals(RDFS.LABEL)
					|| predicateUri.equals(GeoNames.name)) {
				String key = st.getSubject().stringValue();
				String value = st.getObject().stringValue();
				resourceLabels.put(key, value);
				//System.out.println(key+":"+value);
			}
		}
		
	}
		
	/**
	 * summarizes the parsed justification file and returns the result in JSON
	 * the javascript program takes this jason and outputs the proof tree
	 * @param rootURL
	 * the root node of the proof tree from which the summarization should start
	 */
	
	public String summarizedProofTree(String rootURL) {
		g.countSubtrees();

		Statement rootStmt = stmtMap.getFirst(rootURL);
		
		GenericTreeNode<Statement> root = g.getNodeByObject(rootStmt);
		JSONTreeGenrator<Statement> json = new JSONTreeGenrator<Statement>(nameSpaces, resourceLabels);
		String jsonString = json.generateJASON(g, root);
		//to-do: file write/method return
		System.out.println(jsonString);	
		return jsonString;
	}
	
	
	/**
	 * expands a branch of the proof tree based on the collective score of that branch (computed from the subtrees in that branch)
	 * this could be an interesting way to visualise proof trees
	 * @param threshold
	 * @param initFlag
	 * @param statementURI
	 * @param prefs
	 * @param ontologyLocations
	 * @param instanceLocations
	 * @return
	 * @throws Exception
	 */
	public List<KnowledgeStatement> summarizeProofTreeKnowledgeStatements(double threshold, boolean initFlag, String statementURI, List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		
		if(initFlag) {
			Set<Entry<String, ArrayList<KnowledgeStatement>>> entries = kStmtMap.entrySet();
			Set<KnowledgeStatement> kstmts = new HashSet<KnowledgeStatement>();
			//ArrayList<KnowledgeStatement> kstmts = new ArrayList<KnowledgeStatement>();

			int totalStmts = 0;
			for(Entry<String, ArrayList<KnowledgeStatement>> entry:entries) {
				
				ArrayList<KnowledgeStatement> entryStatements = entry.getValue();
				for(KnowledgeStatement entryStmt:entryStatements) {
					
					//if(entryStmt.getStatement().getPredicate().toString().equals(Ratio4TA.derivedFrom)) {
						//stmts.add(entryStmt);
						String key1 = entryStmt.getStatement().getContext().toString();
						KnowledgeStatement st1 = kStmtMap.getFirst(key1);
						if(st1.getStatement().getContext().toString().equals(statementURI)==false) {
							kstmts.add(st1);
							//System.out.println(entryStmt.getStatement().toString());
							totalStmts++;
						}
	
	
						
	
	
					//}
				}
			}
			//System.out.println("JP total stmts Size:"+totalStmts);
			
			ArrayList<KnowledgeStatement> statements = new ArrayList<KnowledgeStatement>();
			for(KnowledgeStatement kst:kstmts) {
				statements.add(kst);
			}
			StatementSummarizer summerizer = new StatementSummarizer(statements,true);
			summerizer.summarize(prefs,ontologyLocations,instanceLocations);
			
			
//			for(KnowledgeStatement ks:statements) {
//				System.out.println("Score:"+ks.getScore());
//				System.out.println("Degree score:"+ks.getDegreeCentrality());
//				System.out.println("Sim score:"+ks.getSimilarityScore());
//			}
			gk.countSubtrees();
		}
		KnowledgeStatement root = kStmtMap.getFirst(statementURI);
		if(root==null) throw new Exception("Root statement not found");
		GenericTreeNode<KnowledgeStatement> rootNode = gk.getNodeByObject(root);
		List<KnowledgeStatement> kStatements = gk.traverseByScoreThreshold(rootNode,threshold);
		
		return kStatements;

	}	
	/**
	 * ranks statements taking into consideration centrality and proof tree abstraction level.
	 * higher a statement is in the proof tree, more abstract it is and higher the rank.
	 * score = 0.5 * degree centrality + 0.5 * proof-tree-level-score
	 * proof-tree-level-score = 1.0/ proof-tree-level //root starts with level 1
	 * @param initFlag
	 * @param statementURI
	 * @param prefs
	 * @param ontologyLocations
	 * @param instanceLocations
	 * @return
	 * @throws Exception
	 */
	
	public List<KnowledgeStatement> summarizeByProofTreeAbstraction(boolean initFlag, String statementURI, List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {

		Set<KnowledgeStatement> kstmts = new HashSet<KnowledgeStatement>();	
		if(initFlag) {
			Set<Entry<String, ArrayList<KnowledgeStatement>>> entries = kStmtMap.entrySet();

			//ArrayList<KnowledgeStatement> kstmts = new ArrayList<KnowledgeStatement>();

			int totalStmts = 0;
			for(Entry<String, ArrayList<KnowledgeStatement>> entry:entries) {
				
				ArrayList<KnowledgeStatement> entryStatements = entry.getValue();
				for(KnowledgeStatement entryStmt:entryStatements) {
					
					//if(entryStmt.getStatement().getPredicate().toString().equals(Ratio4TA.derivedFrom)) {
						//stmts.add(entryStmt);
						String key1 = entryStmt.getStatement().getContext().toString();
						KnowledgeStatement st1 = kStmtMap.getFirst(key1);
						if(st1.getStatement().getContext().toString().equals(statementURI)==false) {
							kstmts.add(st1);
							//System.out.println(entryStmt.getStatement().toString());
							totalStmts++;
						}

					//}
				}
			}
			//System.out.println("JP total stmts Size:"+totalStmts);
			
			ArrayList<KnowledgeStatement> statements = new ArrayList<KnowledgeStatement>();
			for(KnowledgeStatement kst:kstmts) {
				statements.add(kst);
			}
			StatementSummarizer summerizer = new StatementSummarizer(statements,true);
			summerizer.summarize(prefs,ontologyLocations,instanceLocations);
			
			
//			for(KnowledgeStatement ks:statements) {
//				System.out.println("Score:"+ks.getScore());
//				System.out.println("Degree score:"+ks.getDegreeCentrality());
//				System.out.println("Sim score:"+ks.getSimilarityScore());
//			}
			gk.countSubtrees();
		}
		KnowledgeStatement root = kStmtMap.getFirst(statementURI);
		if(root==null) throw new Exception("Root statement not found");
		GenericTreeNode<KnowledgeStatement> rootNode = gk.getNodeByObject(root);
		gk.traverseByScoreThreshold(rootNode,0.0);

		List<KnowledgeStatement> kStatements = computeSortProofTreeAbstractionScore(kstmts);
	
		return kStatements;

	}	
	
	/**
	 * assigns score = 0.5 * degree centrality + 0.5 * proof-tree-level-score
	 * and returns a sorted list by score
	 * @param kstmts
	 * @return
	 */
	private List<KnowledgeStatement> computeSortProofTreeAbstractionScore(Set<KnowledgeStatement> kstmts) {
		List<KnowledgeStatement> kStatements = new ArrayList<KnowledgeStatement>();
		for(KnowledgeStatement kst:kstmts) {
			//System.out.println("Statement:"+kst.getStatement().toString());
			//System.out.println("Degree Centrality:"+kst.getDegreeCentrality() + " Level Score: "+kst.getProofTreeLevelScore());
			double score = (kst.getDegreeCentrality() + kst.getProofTreeLevelScore())/2.0;
			kst.setScore(score);
			
			kStatements.add(kst);
		}
		Collections.sort(kStatements,new Comparator<KnowledgeStatement>() {

			@Override
			public int compare(KnowledgeStatement o1, KnowledgeStatement o2) {
				if(o1.getScore()>o2.getScore()) return -1;
				if(o1.getScore()<o2.getScore()) return 1;
				return o1.getStatement().getContext().stringValue().compareTo(o2.getStatement().getContext().stringValue());
			}
			
		});
		return kStatements;	

	}
	
	public List<KnowledgeStatement> summarizeByProofTreeSubtreeWeight(boolean initFlag, String statementURI, List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {

		Set<KnowledgeStatement> kstmts = new HashSet<KnowledgeStatement>();	
		if(initFlag) {
			Set<Entry<String, ArrayList<KnowledgeStatement>>> entries = kStmtMap.entrySet();

			//ArrayList<KnowledgeStatement> kstmts = new ArrayList<KnowledgeStatement>();

			int totalStmts = 0;
			for(Entry<String, ArrayList<KnowledgeStatement>> entry:entries) {
				
				ArrayList<KnowledgeStatement> entryStatements = entry.getValue();
				for(KnowledgeStatement entryStmt:entryStatements) {
					
					//if(entryStmt.getStatement().getPredicate().toString().equals(Ratio4TA.derivedFrom)) {
						//stmts.add(entryStmt);
						String key1 = entryStmt.getStatement().getContext().toString();
						KnowledgeStatement st1 = kStmtMap.getFirst(key1);
						if(st1.getStatement().getContext().toString().equals(statementURI)==false) {
							kstmts.add(st1);
							//System.out.println(entryStmt.getStatement().toString());
							totalStmts++;
						}

					//}
				}
			}
			//System.out.println("JP total stmts Size:"+totalStmts);
			
			ArrayList<KnowledgeStatement> statements = new ArrayList<KnowledgeStatement>();
			for(KnowledgeStatement kst:kstmts) {
				statements.add(kst);
			}
			StatementSummarizer summerizer = new StatementSummarizer(statements,true);
			summerizer.summarize(prefs,ontologyLocations,instanceLocations);
			
			
//			for(KnowledgeStatement ks:statements) {
//				System.out.println("Score:"+ks.getScore());
//				System.out.println("Degree score:"+ks.getDegreeCentrality());
//				System.out.println("Sim score:"+ks.getSimilarityScore());
//			}
			gk.countSubtrees();
		}
		//KnowledgeStatement root = kStmtMap.getFirst(statementURI);
		//if(root==null) throw new Exception("Root statement not found");
		//GenericTreeNode<KnowledgeStatement> rootNode = gk.getNodeByObject(root);
		//gk.traverseByScoreThreshold(rootNode,0.0);

		List<KnowledgeStatement> kStatements = computeSortProofTreeSubtreeWeightScore(statementURI);
	
		return kStatements;

	}	
	
	/**
	 * Call it only after summarizing by some other measures
	 * @param rootStatementUri
	 * @return
	 */
	public List<KnowledgeStatement> summarizeByProofTreeSubtreeWeight(String rootStatementUri) {
		gk.countSubtrees();
		List<KnowledgeStatement> kStatements = computeSortProofTreeSubtreeWeightScore(rootStatementUri);
		
		return kStatements;
	}
	
	
	
	private List<KnowledgeStatement> computeSortProofTreeSubtreeWeightScore(String rootStatementUri) {
		 List<KnowledgeStatement> res = new ArrayList<KnowledgeStatement>();
		
		for(GenericTreeNode<KnowledgeStatement> gkn: gk.getNodes()) {
			
			KnowledgeStatement kst = (KnowledgeStatement) gkn.getObject();
			
			
			double subtreeWeight = gkn.getCombinedScore();
			kst.setSubTreeWeight(subtreeWeight);
			kst.setScore(subtreeWeight);
			//double score = (kst.getDegreeCentrality() + subtreeWeight)/2.0;
			if(rootStatementUri.equals(kst.getStatement().getContext().stringValue())) continue;
			res.add(kst);

			//System.out.println(kst.getStatement().toString());
			//System.out.println("Sub tree weight:"+ subtreeWeight);
		
		}
		
		Collections.sort(res, new Comparator<KnowledgeStatement>() {

			@Override
			public int compare(KnowledgeStatement o1, KnowledgeStatement o2) {
				if(o1.getSubTreeWeight()>o2.getSubTreeWeight()) return -1;
				if(o1.getSubTreeWeight()<o2.getSubTreeWeight()) return 1;
				return o1.getStatement().getContext().stringValue().compareTo(o2.getStatement().getContext().stringValue());
			}
			
		});
		
		return res;
	}

	public List<KnowledgeStatement> summarizeJustificationKnowledgeStatements(String statementURI, List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		
		Set<Entry<String, ArrayList<Statement>>> entries = stmtMap.entrySet();
		Set<Statement> stmts = new HashSet<Statement>();
		
		for(Entry<String, ArrayList<Statement>> entry:entries) {
			
			ArrayList<Statement> entryStatements = entry.getValue();
			for(Statement entryStmt:entryStatements) {
				if(entryStmt.getPredicate().toString().equals(Ratio4TA.derivedFrom)) {
					//stmts.add(entryStmt);
					String key1 = entryStmt.getSubject().toString();
					String key2 = entryStmt.getObject().toString();
					Statement st1 = stmtMap.getFirst(key1);
					Statement st2 = stmtMap.getFirst(key2);
					if(st1.getContext().toString().equals(statementURI)==false) {
						stmts.add(st1);
					}
					if(st2.getContext().toString().equals(statementURI)==false) {
						stmts.add(st2);
					}
				}
			}
		}
		
		ArrayList<Statement> statements = new ArrayList<Statement>(stmts);
		StatementSummarizer summerizer = new StatementSummarizer(statements);
		List<KnowledgeStatement> kStatements = summerizer.summarize(prefs,ontologyLocations,instanceLocations);
		return kStatements;

	}
	
	
	/**
	 * ranks based on the coherence of the statements with respect to their proof tree positions.
	 * if S is the set of summarised statements
	 * 	for a statement i, if we take i in the set S, we reward i if it has a one step link in 
	 *  the proof tree with any of the statements currently in S
	 * @param statementURI
	 * @param prefs
	 * @param ontologyLocations
	 * @param instanceLocations
	 * @return
	 * @throws Exception
	 */
	public List<KnowledgeStatement> summarizeJustificationKnowledgeStatementsRerank(String statementURI, List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {

		List<KnowledgeStatement> kStatements = summarizeJustificationKnowledgeStatements(statementURI, prefs, ontologyLocations, instanceLocations);
		List<KnowledgeStatement> kStatementsReRanked = reRank(statementURI, kStatements);
		Collections.sort(kStatementsReRanked, new Comparator<KnowledgeStatement>() {

			@Override
			public int compare(KnowledgeStatement o1, KnowledgeStatement o2) {
				if(o1.getReRankedScore()>o2.getReRankedScore()) return -1;
				if(o1.getReRankedScore()<o2.getReRankedScore()) return 1;
				return o1.getStatement().getContext().stringValue().compareTo(o2.getStatement().getContext().stringValue());
			}
		});
		
//		System.out.println("After reRanking:");
//		for(KnowledgeStatement kst:kStatementsReRanked) {
//			System.out.println("Statement:"+kst.getStatement().toString());
//			System.out.println("ReRanked Score:"+kst.getReRankedScore());
//			//System.out.println(kst.getStatement().getContext().stringValue()+" "+index++);
//		}
		return kStatementsReRanked;

	}
	
	/**
	 * Reranks a ranked list by coherence
	 * @param statementURI
	 * @param kStatements
	 * @return
	 * @throws RepositoryException
	 * @throws MalformedQueryException
	 * @throws QueryEvaluationException
	 */

	public List<KnowledgeStatement> reRankByCoherence(String statementURI,List<KnowledgeStatement> kStatements) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		
		
//		System.out.println("Before reRanking:");
//		for(KnowledgeStatement kst:kStatements) {
//			System.out.println("Statement:"+kst.getStatement().toString());
//			System.out.println("Score:"+kst.getScore());
//			System.out.println("ReRanked Score:"+kst.getReRankedScore());
//			//System.out.println(kst.getStatement().getContext().stringValue()+" "+index++);
//
//		}		
		List<KnowledgeStatement> kStatementsReRanked = reRank(statementURI, kStatements);
		Collections.sort(kStatementsReRanked, new Comparator<KnowledgeStatement>() {

			@Override
			public int compare(KnowledgeStatement o1, KnowledgeStatement o2) {
				if(o1.getReRankedScore()>o2.getReRankedScore()) return -1;
				if(o1.getReRankedScore()<o2.getReRankedScore()) return 1;
				return 0;
			}
		});
		
//		System.out.println("After reRanking:");
		for(KnowledgeStatement kst:kStatementsReRanked) {
//			System.out.println("Statement:"+kst.getStatement().toString());
//			System.out.println("Score:"+kst.getScore());
//			System.out.println("ReRanked Score:"+kst.getReRankedScore());
			//System.out.println(kst.getStatement().getContext().stringValue()+" "+index++);
			kst.setScore(kst.getReRankedScore());

		}
		return kStatementsReRanked;		
	}
	
	public int oneStepProofTreeLink(List<KnowledgeStatement> S) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		RepositoryConnection con = myRepository.getConnection();
		Set<String> summaryUris = new HashSet<String>();
		
		for(KnowledgeStatement st:S) {
			String stUri = st.getStatement().getContext().stringValue();
			summaryUris.add(stUri);
		}
		int count = 0;
		
		for(KnowledgeStatement st:S) {	
			
			String stUri = st.getStatement().getContext().stringValue();
			String queryString = "PREFIX  r4ta:  <http://ns.inria.fr/ratio4ta/v2#> " +
					"SELECT ?o " +
					"WHERE {" +
					"<"+stUri+"> r4ta:derivedFrom ?o ." +
					"}";

			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			
			TupleQueryResult result = tupleQuery.evaluate();
			
			try {
				while(result.hasNext()) {
					
					String foundUri = result.next().getValue("o").stringValue();
					if(summaryUris.contains(foundUri))
						count++;
					
				}
			} finally {
				result.close();

			}

			
			
			String queryString1 = "PREFIX  r4ta:  <http://ns.inria.fr/ratio4ta/v2#> " +
					"SELECT ?s " +
					"WHERE {" +
					"?s r4ta:derivedFrom <"+stUri+"> ." +
					"}";

			TupleQuery tupleQuery1 = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString1);
			
			TupleQueryResult result1 = tupleQuery1.evaluate();
			
			try {
				while(result1.hasNext()) {
					String foundUri = result1.next().getValue("s").stringValue();
					if(summaryUris.contains(foundUri))
						count++;

				}
			} finally {
				result1.close();

			}
					
		
		}

		return count;
	}
	public double reward(KnowledgeStatement st, List<KnowledgeStatement> S) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		
		 List<KnowledgeStatement> newS = new ArrayList<KnowledgeStatement>(S);
		 newS.add(st);
		 
		 int one_step_pt_links = oneStepProofTreeLink(S);
		 int one_step_pt_links_st = oneStepProofTreeLink(newS);
		 if(one_step_pt_links_st==0)
			 return 0.0;
		 
		 double r = 1.0 - (double) one_step_pt_links/ (double) one_step_pt_links_st;
		
		return r;
	}
	
	List<KnowledgeStatement> reRank(String statementURI, List<KnowledgeStatement> kStatements) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		
		Statement rootRdfStmt = stmtMap.getFirst(statementURI);
		KnowledgeStatement kRootStmt = new KnowledgeStatement(rootRdfStmt);
		
		//adding a root statement
		List<KnowledgeStatement> S = new ArrayList<KnowledgeStatement>();
		S.add(kRootStmt);
		
		int i=0;
		while(i<kStatements.size()) {
			
			double maxScore = Double.NEGATIVE_INFINITY;
			int iMax = -1;
			for(int index=0;index<kStatements.size();index++) {
				
				KnowledgeStatement kstmt = kStatements.get(index);
				if(S.contains(kstmt)==false) {
					double sc = kstmt.getScore();
					double rsc = reward(kstmt, S);
					//double rScore = (.6 * sc+ .4 * rsc);
					double rScore = (sc+ rsc)/2.0;
					//double rScore = sc;
					if(maxScore<rScore) {
						iMax = index;
						maxScore = rScore;
					}
				}
			}
			
			kStatements.get(iMax).setReRankedScore(maxScore);
			S.add(kStatements.get(iMax));
			
			//last statement
			i++;
		}
		//removing the root statement
		S.remove(0);
		return S;
	}
	public List<KnowledgeStatement>  summarizeJustificationKnowledgeStatements(String statementURI) throws Exception {
		return summarizeJustificationKnowledgeStatements( statementURI,  null, null, null);
	}
	public List<KnowledgeStatement>  summarizeRelevantJustificationKnowledgeStatements(String statementURI, List<String> prefs, List<String> ontologyLocations, List<String> instanceLocations) throws Exception {
		return summarizeJustificationKnowledgeStatements( statementURI,  prefs, ontologyLocations, instanceLocations);
	}
	public List<String> getOntologyLocations() {
		return ontologyLocations;
	}
	public void setOntologyLocations(List<String> ontologyLocations) {
		this.ontologyLocations = ontologyLocations;
	}
	public List<String> getInstanceLocations() {
		return instanceLocations;
	}
	public void setInstanceLocations(List<String> instanceLocations) {
		this.instanceLocations = instanceLocations;
	}	
	public MyMultiMap<String, Statement> getStmtMap() {
		return stmtMap;
	}

	public void setStmtMap(MyMultiMap<String, Statement> stmtMap) {
		this.stmtMap = stmtMap;
	}
}
