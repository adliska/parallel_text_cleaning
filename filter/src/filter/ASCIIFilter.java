package filter;

import java.io.*;
import java.util.regex.*;
import java.util.*;

import filter.tools.ExportFormatProcessor;
import filter.tools.ExportMode;
/** Implementation of the ASCII filter.
 * 
 * @author Adam Liska
 *
 */
public class ASCIIFilter {
	
	String errorSign = "ErRoR_asciiFilter";
	
	/** Checks whether the sentence pair conforms to the ASCII rule. 
	 * 
	 * @param line Sentence pair.
	 * @return True for a conforming pair, false otherwise.
	 */
	public boolean checkAlignment(String line) {
		boolean isCorrect = true;
		String[] sentences = line.split("\\t");
		String eng = sentences[0];
		String cs = sentences[1];
		
		if (!eng.matches("[\\p{ASCII}]*")) { 
			
			Pattern p = Pattern.compile("[^\\p{ASCII}“”´´``—–€‐‘‑‑]");
			Matcher m = p.matcher(eng);
			LinkedList<String> chars = new LinkedList<String>();
			while (m.find()) {
				chars.add(m.group());
			}
			
			boolean containsAll = true;
			for(String character:chars) {
				if (!cs.contains(character)) {
					containsAll = false;
					break;
				}
			}

			if (!containsAll) {
				isCorrect = false;
			}
			 
		}
		
		return isCorrect;
	}
	
	/** Reads sentence pairs from the standard input and processes them. Expected format of the input: 
	 * english_segment \t czech(or_other)_segment. 
	 * 
	 * Correctly aligned segments are output on the standard output unchanged.
	 * Incorrect segments are output on the standard output in the following format:
	 * english_segment \t czech(or_other)_segment \t ErRoR_asciiFilter
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ASCIIFilter filter = new ASCIIFilter(); 
				
		String line;
		ExportFormatProcessor reader = new ExportFormatProcessor(ExportMode.PLAIN);
		while((line = reader.readLine()) != null) {
			if (!filter.checkAlignment(line)) {
				System.out.println(line + "\t" + filter.errorSign);
			} else {
				System.out.println(line);
			}
		}		
	}
	
}
