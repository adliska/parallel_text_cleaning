package filter;

import java.util.*;
import java.io.*;

import filter.tools.ExportFormatProcessor;
import filter.tools.ExportMode;

/** Implementation of the dictionary filter.
 *
 * @author Adam Liska
 *
 */
public class DictionaryFilter {
	HashMap<String, LinkedList<String>> dictionary;
	LinkedList<String> skip;
	Double LIMIT;
	String errorSign = "ErRoR_dictionaryFilter"; 
	
	/** Initialises the dictionary, sets coverage limit to 0.25
	 * 
	 */
	public DictionaryFilter() {
		dictionary = new HashMap<String, LinkedList<String>>();
		skip = new LinkedList<String>();
		skip.add("in");
		skip.add("at");
		skip.add("by");
		skip.add("to");
		LIMIT = 0.25;
	}
	
	/** Initialises the dictionary, sets custom coverage limit.
	 * 
	 * @param limit Coverage limit 
	 */
	public DictionaryFilter(Double limit) {
		this();
		LIMIT = limit;
	}
	
	/** Reads and processes dictionary in the format conforming to GNU FDL dictionary at slovnik.zcu.cz
	 * 
	 * @param file Path to the dictionary
	 */
	public void readDict(String file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while((line = br.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				String[] parts = line.split("\\t");
				if ((parts.length < 2) || (parts[1].trim().equals(""))) {
					continue;
				}
				if (dictionary.containsKey(parts[0])) {
					dictionary.get(parts[0]).add(parts[1]);
				} else {
					LinkedList<String> newlist = new LinkedList<String>();
					newlist.add(parts[1]);
					dictionary.put(parts[0], newlist);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Error reading dictionary file!");
		}
	}

	
	/** Reads a HashMap<String, String> dictionary produced by filter.tools.GizaTranslations tool.
	 * 
	 * @param file Path to the serialised dictionary HashMap<String, String> object.
	 */
	public void readGizaInput(String file) {
		HashMap<String, String> gizaTranslations;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			gizaTranslations = (HashMap<String, String>) ois.readObject();
			ois.close();
		}
		catch (Exception e) { 
			throw new RuntimeException("Error reading GIZA input!"); 
		}
		
		for(String word:gizaTranslations.keySet()) {
			if (dictionary.containsKey(word)) {
				if (!dictionary.get(word).contains(gizaTranslations.get(word))) {
					dictionary.get(word).add(gizaTranslations.get(word));
				}
			} else {
				LinkedList<String> newlist = new LinkedList<String>();
				newlist.add(word);
				dictionary.put(word, newlist);
			}
		}
		
	}

	/** Checks whether the word is to be skipped.
	 * 
	 * @param word Source language word.
	 * @return true if the source language word is to be skipped when looking for translations.  
	 */
	private boolean isSkipped(String word) {
		if (skip.contains(word)) {
			return true;
		} else {
			return false;
		}
	}
	
	/** Checks whether the sentence pair is correct.
	 * 
	 * @param line Lemmatised sentence pair: first_language_segment \t second_language_segment 
	 * @return True if the pair is correct, false otherwise
	 */
	public boolean checkAlignment(String line) {
		String[] parts = line.split("\\t");
		String en = parts[0].toLowerCase().replace(" .", "");
		String cs = parts[1].toLowerCase().replace(" .", "");
		
		boolean result = true;
		
		String[] enwords = en.split(" ");
		String[] cswords = cs.split(" ");
		
		Double cslength = new Double(cswords.length);
		
		if (enwords.length > 1 && cslength > 2) {
			double ratio = getRatio(en, cs);
			
			if ((ratio) < LIMIT) {
				result = false;
			}
		}
		
		return result;
	}
	
	
	/** This method computes the second language words coverage.
	 * 
	 * @param en Source language sentence
	 * @param cs Target language sentence
	 * @return Target words coverage
	 */
	public double getRatio(String en, String cs) {

		String[] enwords = en.split(" ");
		String[] cswords = cs.split(" ");
	
		Double cslength = new Double(cswords.length);
		int coveredWords = 0;
		for (int i = 0; i < enwords.length; i++) {
			String word = enwords[i];
			if (!isSkipped(word)) {
				LinkedList<String> translations = getListOfTranslations(word);
				if (isCovered(cs, translations)) {
					coveredWords++;
				}
			}
		}

		return coveredWords / cslength;
		
	}

	/** Checks whether a word from the English segment is covered in the Czech segment.
	 * 
	 * @param cs Czech sentence
	 * @param translations List of translations of the English word. 
	 * @return
	 */
	private boolean isCovered(String cs, LinkedList<String> translations) {
		boolean isCovered = false;
		for (String translation:translations) {
			if (translation.contains(" si")) {
				translation = translation.substring(0, translation.indexOf(" si"));
			} else if (translation.contains(" se")) {
				translation = translation.substring(0, translation.indexOf(" se"));
			}

			if (translation.length() > 4) {
				translation = translation.substring(0, 4);
			}
			
			if (cs.contains(translation)) {
				isCovered = true;
				break;
			}
		}
		return isCovered;
	}

	/** Gets list of translations of an English word.
	 * 
	 * @param word English word
	 * @return List of translations.
	 */
	private LinkedList<String> getListOfTranslations(String word) {
		LinkedList<String> translations;
		if (dictionary.containsKey(word)) {
			translations = dictionary.get(word);
		} else {
			translations = new LinkedList<String>();
		}

		translations.add(word);
		return translations;
	}
	
	/** Prints help.
	 * 
	 */
	public static void printHelp() {
		System.out.println("Use:");
		System.out.println("DictionaryFilter -d dictionary_file -g giza_dictionary_file");
	}
	
	/** Processes sentence pairs in the format: lemmatised_source_segment \t lemmatised_target_segment
	 * In case of a bad alignment, the filter adds a new field at the end of the appropriate corpus line: ErRoR_dictionaryFilter,
	 * which is output to the standard output.
	 * 
	 * 
	 * @throws IOException Exception if there is an error reading input.
	 */
	public void processInputStream() throws IOException {
		ExportFormatProcessor in = new ExportFormatProcessor(ExportMode.LEMMA);
		String line;
		while((line = in.readLine()) != null) {
			if (!checkAlignment(line)) {
				System.out.println(line + "\t" + errorSign);
			} else {
				System.out.println(line);
			}
		}

	}

	/** The main method of the filter, it sets up the DictionaryFilter object.
	 * 
	 *  
	 * @param args Command line arguments. Program options: -t dictionary_file_path -g giza_dictionary_file_path; both required. 
	 */
	public static void main(String[] args) {
		DictionaryFilter filter = new DictionaryFilter();
		
		if (args.length == 4) {
			if (args[0].equals("-d") && args[2].equals("-g")) {
				filter.readDict(args[1]);
				filter.readGizaInput(args[3]);
			} else if (args[0].equals("-g") && args[2].equals("-d")) {
				filter.readGizaInput(args[1]);
				filter.readDict(args[3]);
			} else {
				printHelp();
				return;
			}
		} else if (args.length == 2) {
			if (args[0].equals("-d")) {
				filter.readDict(args[1]);
			} else if (args[0].equals("-g")) {
				filter.readGizaInput(args[1]);
			} else {
				printHelp();
				return;
			}
		} else {
			printHelp();
			return;
		}

		try {
			filter.processInputStream();
		} catch (IOException e) {
			throw new RuntimeException("Error processing input stream!");
		}
		
		
	}
}
