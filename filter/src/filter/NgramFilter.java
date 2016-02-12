package filter;

import java.io.*;

/** This class implements the filter based on n-grams. 
 * 
 * @author Adam Liska
 *
 */
public class NgramFilter {
	String errorSign = "ErRoR_ngramFilter";
	Double LIMIT;
	
	/** Constructor without parameters, setting default threshold at -1.5.
	 * 
	 */
	public NgramFilter() {
		LIMIT = -1.5;
	}
	
	/** Constructor with parameters.
	 * 
	 * @param limit Threshold for minimal sentece pair score.
	 */
	public NgramFilter(Double limit) {
		LIMIT = limit;
	}
	
	/** This method processed two input streams: SRILM Toolkit output and the corpus.
	 * 
	 * @param srilm SRILM Toolkit output for first language. 
	 * @param corpus Corpus
	 * @throws IOException Exception if there is an error reading input.
	 */
	public void processInputStream(BufferedReader srilm, BufferedReader corpus) throws IOException {
		String line;
		int i = 0;
		while((line = srilm.readLine()) != null) {
			String pair = corpus.readLine();
			i++;

			if (line.startsWith("file")) {
				break;
			}
			
			line = srilm.readLine();

			Double numchars = new Double(line.split(" ")[2]);
			line = srilm.readLine();
			Double prob = new Double(line.split(" ")[3]);
			Double result = (prob / numchars);

			if ((result < LIMIT) && (numchars > 35)) {
				System.out.println(pair + "\t" + errorSign);
			} else {
				System.out.println(pair);
			}
			
			srilm.readLine();

		}		
	}
	
	/** This method prints help.
	 * 
	 */
	public static void printHelp() {
		System.out.println("Use:");
		System.out.println("NgramFilter srilm_output corpus");
	}

	/** Main method. Checks arguments and sets up input streams and filter object.
	 * In case of a bad alignment, the filter adds a new field at the end: ErRoR_asciiFilter,
	 * which is output on the standard output.
	 * 
	 * @param args Command line arguments. SRILM output and Corpus, in this order, both required. 
	 */
	public static void main(String[] args) {
		if (args.length == 2) {
			try {
				BufferedReader srilm = new BufferedReader(new FileReader(args[0]));
				BufferedReader corpus = new BufferedReader(new FileReader(args[1]));
				NgramFilter filter = new NgramFilter();
				filter.processInputStream(srilm, corpus);
			}
			catch (IOException e) {
				throw new RuntimeException("Error reading input!");
			}
		} else {
			printHelp();
		}
		

		
	}
}
