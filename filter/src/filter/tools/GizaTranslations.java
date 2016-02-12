package filter.tools;

import java.io.*;
import java.util.*;

/** Extraction of a GIZA++ translation dictionary.
 * 
 * @author Adam Liska
 *
 */
public class GizaTranslations {
	
	/** Processes GIZA output in one direction.
	 * 
	 * @param folder Path to the folder which GIZA++ output.
	 * @param prefix Prefix of GIZA++ files.
	 * @return HashMap with words in source language and their translations in target language, together with their probabilities.
	 * @throws IOException Exception thrown if there is an error reading file input.
	 */
	public static HashMap<String, HashMap<String, Double>> getTranslations(String folder, String prefix) throws IOException {
		HashMap<String, String> src = new HashMap<String, String>();
		HashMap<String, String> trg = new HashMap<String, String>();

		String filename = folder + "/" + prefix + ".trn.src.vcb";
		BufferedReader in = new BufferedReader(new FileReader(filename)); 
		String line;
		while ((line = in.readLine()) != null) {
			String[] parts = line.split(" ");
			src.put(parts[0], parts[1]);
		}

		filename = folder + "/" + prefix + ".trn.trg.vcb";
		in = new BufferedReader(new FileReader(filename));
		while ((line = in.readLine()) != null) {
			String[] parts = line.split(" ");
			trg.put(parts[0], parts[1]);
		}
		
		filename = folder + "/" + prefix + ".t3.final";
		in = new BufferedReader(new FileReader(filename));
		HashMap<String, HashMap<String, Double>> result = new HashMap<String, HashMap<String, Double>>();
		while ((line = in.readLine()) != null) {
			String[] parts = line.split(" ");
			if (parts[0].equals("0") || parts[1].equals("0")) {
				continue;
			}
			
			String source = src.get(parts[0]);
			String trans = trg.get(parts[1]);
			Double prob = new Double(parts[2]);
			
			if (result.containsKey(source)) {
				result.get(source).put(trans, prob);
			} else {
				HashMap<String, Double> nova = new HashMap<String, Double>();
				nova.put(trans, prob);
				result.put(source, nova);
			}
		}
		
		return result;
		
	}
	
	/** Takes output from getTranslation method (run in both directions: first->second, second->first) and extracts
	 * the best translation for each word in the first language.  
	 * @param first first->second
	 * @param second second->first
	 * @return HashMap of words in the first language and a best translation for each of them. 
	 */
	public static HashMap<String, String> getBestTranslations(HashMap<String, HashMap<String, Double>> first, HashMap<String, HashMap<String, Double>> second) {

		Set<String> keys = first.keySet();
		
		HashMap<String, HashMap<String, Double>> trans = new HashMap<String, HashMap<String, Double>>();
		int nulove = 0;
		for(String key:keys) {
			HashMap<String, Double> result = new HashMap<String, Double>();
			
			HashMap<String, Double> translations = first.get(key);
			for(String target:translations.keySet()) {
				if ((second.get(target) != null) && second.get(target).containsKey(key)) {
					result.put(target, first.get(key).get(target) + second.get(target).get(key));
				}
			}
			
			
			if (result.size() > 0) {
				trans.put(key, result);
			} else {
				nulove++;
			}
			
			
		}
		
		
		HashMap<String, String> result = new HashMap<String, String>();
		for(String enword:trans.keySet()) {
			HashMap<String, Double> cswords = trans.get(enword);
			String finalword = "";
			Double finalprob = 0D;
			for(String csword:cswords.keySet()) {
				if (cswords.get(csword) > finalprob) {
					finalword = csword;
					finalprob = cswords.get(csword);
				}
			}
			result.put(enword.toLowerCase(), finalword.toLowerCase());
		}
				
		
		return result;
	}
	
	/** Serializes dictionary in a file.
	 * 
	 * @param model HashMap<String, String> dictionary
	 * @param file	path of the output file
	 * @throws IOException Exception thrown if there is an error saving file.
	 */
	public static void saveModel(HashMap<String, String> model, String file) throws IOException {

			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(model);
			oos.close();
			System.out.println("Model successfully saved. Size: " + model.size());
	}
	
	/** Prints help.
	 * 
	 */
	public static void printHelp() {
		System.out.println("Use:");
		System.out.println("GizaTranslations firstLang_secondLang_folder prefix1 secondLang_firstLang_folder prefix2 model_output_file");
	}

	/** Program takes five arguments: firstLang_secondLang_folder prefix1 secondLang_firstLang_folder prefix2 model_output_file, in this order.
	 * 
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		if (args.length == 5) {
			HashMap<String, HashMap<String, Double>> one;
			HashMap<String, HashMap<String, Double>> two;
			
			try {
				one = GizaTranslations.getTranslations(args[0], args[1]);
				two = GizaTranslations.getTranslations(args[2], args[3]);
			} catch (IOException e) {
				throw new RuntimeException("Error reading file input!");
			}
			
			HashMap<String, String> model = GizaTranslations.getBestTranslations(one, two);
			
			try {
				GizaTranslations.saveModel(model, args[4]);
			} catch (IOException e) {
				throw new RuntimeException("Error saving model!");
			}			
		} else {
			printHelp();
		}
		
	}
}