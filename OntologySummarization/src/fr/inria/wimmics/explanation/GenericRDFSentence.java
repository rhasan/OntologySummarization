package fr.inria.wimmics.explanation;

import java.util.Set;
import java.util.HashSet;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.springframework.beans.PropertyAccessException;

public class GenericRDFSentence {
	Set<Statement> statements;
	Statement mainStmt;
	Resource subject;
	URI predicate;
	Set<Value> objects;
	
	public GenericRDFSentence() {
		statements = new HashSet<Statement>();
		objects = new HashSet<Value>();
	}
	
	@Override
	public String toString() {
		String str = "Sentence: {\n";
		for(Statement s:statements) {
			str += s.toString()+"\n";
		}
		str += "\n}\n";
		/*
		str += "Main statement: "+mainStmt.toString();
		str += "\nSubject: "+subject.toString();
		str += "\nPredicate:"+predicate.toString();
		str += "\nObjects:\n";
		
		for(Value v:objects) {
			str += v.toString()+"\n";
		}*/
		return str;
	}
	
	public void addStatement(Statement s) {
		statements.add(s);
	}
	
	public void setMainStmt(Statement mainStmt) {
		this.mainStmt = mainStmt;
	}
	public Statement getMainStmt() {
		return mainStmt;
	}
	
	public void addObjects(Value r) {
		objects.add(r);
	}
	public Set<Statement> getStatements() {
		return statements;
	}
	public void setStatements(Set<Statement> statements) {
		this.statements = statements;
	}
	public Resource getSubject() {
		return subject;
	}
	public void setSubject(Resource subject) {
		this.subject = subject;
	}
	public URI getPredicate() {
		return predicate;
	}
	public void setPredicate(URI predicate) {
		this.predicate = predicate;
	}
	public Set<Value> getObjects() {
		return objects;
	}
	public void setObjects(Set<Value> objects) {
		this.objects = objects;
	}
	
	
	public boolean hasSequentialLink(GenericRDFSentence b) {
		
		//System.out.println(predicate.toString());
		//System.out.println(b.getSubject().toString());
		
		if(predicate.toString().equals(b.getSubject().toString())) {
			//System.out.println("true");
			
			return true;
		}
		
		for(Value obj:objects) {
			//System.out.println(obj.toString());
			//System.out.println(b.getSubject().toString());
			

			if(obj.toString().equals(b.getSubject().toString())) {
				//System.out.println("true");
				
				return true;
			}
		}
		//System.out.println("false");
		//System.out.println("...........");
		
		return false;
	}
	
	public boolean hasCoordinateLink(GenericRDFSentence b) {
		
		if(subject.toString().equals(b.getSubject().toString())) {
			return true;
		}
		
		return false;
	}
}
