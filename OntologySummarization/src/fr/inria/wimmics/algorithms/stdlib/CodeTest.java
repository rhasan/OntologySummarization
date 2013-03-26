package fr.inria.wimmics.algorithms.stdlib;

import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.text.CharacterIterator;

public class CodeTest {
	
	
	
	public static void main(String[] args) {
		
		AttributedString as = new AttributedString("I love you 104 gazillion");
		as.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, 13, 14);
		CharacterIterator i = as.getIterator();
		char c;
		StringBuffer sb = new StringBuffer();
		while ((c = i.current()) != CharacterIterator.DONE) {
		    sb.append(c);
		    i.next();
		}

		System.out.println(sb.toString());
	}

}
