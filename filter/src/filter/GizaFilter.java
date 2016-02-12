package filter;

import java.io.*;


/** Implementation of the translation probablity filter.
 * 
 * @author Adam Liska
 *
 */
public class GizaFilter {
	
	Double THRESHOLD;
	String errorSign = "ErRoR_gizaFilter";
	
	/** Sets threshold at -10.
	 *   
	 */
	public GizaFilter() {
		THRESHOLD = -10D;
	}
	
	/** Sets a custom threshold.
	 * 
	 * @param threshold The threshold for sentence alignment score.
	 */
	public GizaFilter(Double threshold) {
		THRESHOLD = threshold;
	}

	/** Processes input coming from two GIZA++ output files (directions firstLang->secondLang and secondLang->firstLang).
	 * In case of a bad alignment, the filter adds a new field at the end of the appropriate corpus line: ErRoR_gizaFilter,
	 * which is output to the standard output.
	 * 
	 * @param giza1 prefix1.A3.final, firstLang->secondLang GIZA++ output file
	 * @param giza2 prefix2.A3.final, secondLang->firstLang GIZA++ output file
	 * @param corpusFile corpus file path
	 * @throws IOException Exception if there is an error reading input.
	 */
	public void processInput(String giza1, String giza2, String corpusFile) throws IOException {
		BufferedReader in1 = new BufferedReader(new FileReader(giza1));
		BufferedReader in2 = new BufferedReader(new FileReader(giza2));
		BufferedReader corpus = new BufferedReader(new FileReader(corpusFile));

		String line1;
		String line2;
		String pair;
		
		int i = 0;
		while(((line1 = in1.readLine()) != null) && ((line2 = in2.readLine()) != null) && ((pair = corpus.readLine()) != null)) {
			i++;
			String[] parts1 = line1.split(" ");
			String[] parts2 = line2.split(" ");
			
			Double prob1 = new Double(parts1[13]);
			Double length1 = new Double(parts1[9]);
			Double prob2 =  new Double(parts2[13]);
			Double length2 = new Double(parts2[9]);
			
			Double probability = (Math.log(prob1) / (length1)) + (Math.log(prob2) / (length2));
			
			if (probability < THRESHOLD) {
				System.out.println(pair + "\t" + errorSign);
			} else {
				System.out.println(pair);
			}
			
			in1.readLine();in1.readLine();
			in2.readLine();in2.readLine();
		}
	}
	
	/** Prints help.
	 * 
	 */
	public static void printHelp() {
		System.out.println("Use:");
		System.out.println("GizaFilter GIZA_firstLang_secondLang_file GIZA_secondLang_firstLang_file corpus");
	}
	
	/** Main method. 
	 * 
	 * @param args Command line arguments. Program takes three arguments: GIZA_firstLang_secondLang_file, GIZA_secondLang_firstLang_file, corpus (in this order, all required)
	 */
	public static void main(String[] args) {
		if (args.length == 3) {
			GizaFilter filter = new GizaFilter();
			try {
				filter.processInput(args[0], args[1], args[2]);	
			} catch (IOException e) {
				throw new RuntimeException("Error processing input!");
			}			
		} else {
			printHelp();
		}
	}
}
