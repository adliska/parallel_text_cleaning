package filter;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import filter.tools.ExportFormatProcessor;
import filter.tools.ExportMode;

/** Implementation of the number filter.
 * 
 * @author Adam Liska
 *
 */
public class NumberFilter {
	
	HashMap<String, String[]> numberMap;
	String errorSign = "ErRoR_numberFilter";
	
	/** Initializes an empty number translation map. 
	 * 
	 */
	public NumberFilter() {
		numberMap = new HashMap<String, String[]>();
	}
	
	/** Processes a number translation map file.
	 *  
	 * @param mapFile Translation map file path
	 * @throws IOException
	 */
	public void readMap(String mapFile) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(mapFile));
		String line;
		while ((line = in.readLine()) != null) {
			String[] parts = line.split(("\t"));
			String number = parts[0];
			String[] versions = parts[1].split(",");
			numberMap.put(number, versions);
		}
		in.close();
	}
	
	/** Checks whether the sentence pair is correct.
	 * 
	 * @param line Sentence pair: firstLang_segment \t secondLang_segment
	 * @return true is the alignment is correct, false otherwise
	 */
	public boolean checkAlignment(String line) {
		boolean isCorrect = true;
		
		String[] sentences = line.split("\\t");
		String eng = sentences[0].toLowerCase();
		String cz = sentences[1].toLowerCase();
		
		LinkedList<String> numbers = extractNumbers(eng);
	
		int missing = getNumberOfMissingNumbers(cz, numbers);
		
		if (missing > 0) {
			if (!containSameNumerals(eng, cz)) {
				isCorrect = false;
			}
		}
				
		return isCorrect;
	}

	/** Returns the number of numbers that weren't covered in the Czech segment. 
	 * 
	 * @param cz Czech sentence
	 * @param numbers List of numbers in the English segment
	 * @return Number of numbers that weren't covered in the Czech segment. 
	 */
	private int getNumberOfMissingNumbers(String cz, LinkedList<String> numbers) {
		int missing = 0;
		for(String number:numbers) {	
			if (!cz.contains(number)) {

				boolean containsTrans = false;
				if (numberMap.containsKey(number)) {
					String[] trans = numberMap.get(number);
					for (String translation:trans) {
						if (cz.contains(translation)) {
							containsTrans = true;
							break;
						}						
					}					
				}

				if (!containsTrans) {
					missing++;
				}
			}
		}
		
		return missing;
	}

	/** Extracts numbers from an English sentence
	 * 
	 * @param eng English sentence
	 * @return List of numbers
	 */
	private LinkedList<String> extractNumbers(String eng) {
		LinkedList<String> numbers = new LinkedList<String>();
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(eng);
		while (m.find()) {
			numbers.add(m.group());
		}
		return numbers;
	}

	/** Checks whether two sentences contain the same numerals.
	 * 
	 * @param eng English sentence
	 * @param cz Czech sentence
	 * @return True if they containt the same numerals, false otherwise.
	 */
	private boolean containSameNumerals(String eng, String cz) {
		boolean sameNumChar = true;
		Integer i = 0;
		while (i < 10) {
			if ((cz.contains(i.toString()) && !eng.contains(i.toString())) || (!cz.contains(i.toString()) && eng.contains(i.toString()))) {
				sameNumChar = false;
				break;
			}
			i++;
		}
		return sameNumChar;
	}
	
	/** Reads sentence pairs from the standard input and processes them. Expected format of the input: 
	 * english_segment \t czech(or_other)_segment. 
	 * 
	 * Correctly aligned segments are output on the standard output unchanged.
	 * Incorrect segments are output on the standard output in the following format:
	 * english_segment \t czech(or_other)_segment \t ErRoR_numberFilter
	 *
	 * @param args Command line arguments. This program takes one optional argument - translation map file path. 
	 * The file is in the following format:
	 * number \t trans1,trans2,trans3,...,
	 * having one number per line.
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		NumberFilter filter = new NumberFilter(); 
		ExportFormatProcessor reader = new ExportFormatProcessor(ExportMode.PLAIN); 
		
		if ((args.length == 2) && args[0].equals("-t")) {
			filter.readMap(args[1]);
		}
		
		String line;
		while((line = reader.readLine()) != null) {
			try {
				if (!filter.checkAlignment(line)) {
					System.out.println(line + "\t" + filter.errorSign);
				} else {
					System.out.println(line);
				}
			} catch (Exception e) {
				System.out.println("Error evaluating sentence pair!");
				System.out.println(line);
				e.printStackTrace();
			}
		}
	}	
}
