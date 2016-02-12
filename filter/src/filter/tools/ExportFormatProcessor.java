package filter.tools;

import java.io.*;

/** Implementation of a Czeng 0.9 Export Format processor.
 * 
 * @author Adam Liska
 *
 */
public class ExportFormatProcessor {
	
	ExportMode mode;
	BufferedReader reader;
	
	public ExportFormatProcessor(ExportMode mode) {
		this.mode = mode;
		reader = new BufferedReader(new InputStreamReader(System.in));
	}
	
	/** Returns a processed line.
	 * 
	 * @return Processed line.
	 * @throws IOException
	 */
	public String readLine() throws IOException {
		String input = reader.readLine();
		
		if (input == null) {
			return null;
		} else {
			return processLine(input);
		}
		
	}
	
	/** Processes a line of Export Format file
	 * 
	 * @param line Input line
	 * @param mode Mode, i.e. what's to extract.
	 * @return String with extracted sentence pair
	 */
	private String processLine(String line) {
		String[] input = line.split("\\t");
		
		StringBuilder result = new StringBuilder();
		result.append(extract(input[1]));
		result.append("\t");
		result.append(extract(input[5]));
		
		return result.toString();
	}
	
	/** Extracts requested data.
	 * 
	 * @param line Sentence with additional information (lemmas, tags, etc)
	 * @param mode Mode, i.e. what's to extract
	 * @return String with extracted data.
	 */
	private String extract(String line) {
		String[] columns = line.split(" ");
		
		StringBuilder result = new StringBuilder();
		
		switch(mode) {

		case PLAIN:
			for(int i = 0; i<columns.length; i++) {
				result.append(columns[i].split("\\|")[0]);
				if (i < columns.length-1) {
					result.append(" ");
				}
			}
			break;
			
		case LEMMA: 
			for(int i = 0; i<columns.length; i++) {
				result.append(columns[i].split("\\|")[1]);
				if (i < columns.length-1) {
					result.append(" ");
				}
			}			
			break;
			
		case TAG:
			for(int i = 0; i<columns.length; i++) {
				result.append(columns[i].split("\\|")[2]);
				if (i < columns.length-1) {
					result.append(" ");
				}
			}
			break;
			
		case PSEUDOLEMMA:
			for(int i = 0; i<columns.length; i++) {
				String word = columns[i].split("\\|")[0].toLowerCase();
				if (word.length() > 5) {
					word = word.substring(0, 5);
				}
				result.append(word);
				if (i < columns.length-1) {
					result.append(" ");
				}
			}
			break;
		}

		return result.toString();		
	}
	
	/** This method prints help. 
	 * 
	 */
	public static void printHelp() {
		System.out.println("Use:");
		System.out.println("ExportFormatProcessor -mode");
		System.out.println("Mode options: lemma, pseudolemma, tag, plain");
	}
	
	/** Reads input stream and calls processLine method. Output is given on the standard output.
	 *  
	 * @param args Command line arguments. Just one option - mode, i.e. what to extract.
	 */
	public static void main(String args[]) {
		if (args.length != 1) {
			printHelp();
			return;
		}
		
		String option = args[0];
		ExportMode mode;
		
		if (option.equals("plain")) {
			mode = ExportMode.PLAIN;
		} else if (option.equals("lemma")) {
			mode = ExportMode.LEMMA;
		} else if (option.equals("tag")) {
			mode = ExportMode.TAG;
		} else if (option.equals("pseudolemma")) {
			mode = ExportMode.PSEUDOLEMMA;
		} else {
			printHelp();
			return;
		}
		
		ExportFormatProcessor processor = new ExportFormatProcessor(mode);
		
		String line; 
		try {
			while((line = processor.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			throw new RuntimeException("Error reading input!");
		}
	}

}
