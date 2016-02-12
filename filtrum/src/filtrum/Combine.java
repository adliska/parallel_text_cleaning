package filtrum;

import java.io.*;
import java.util.*;

/** Combines multiple filter output files. 
 * 
 * @author Adam Liska
 *
 */
public class Combine {
	
	/** Prints help.
	 * 
	 */
	public static void printHelp() {
		System.out.println("Use:");
		System.out.println("Combine filter_outputs_to_combine > combined_output");
	}
	
	/** Reads multiple files (whose paths are given as parameters) and combines errors for each
	 * line. Output is given on the standard output.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			printHelp();
			return;
		}
	
		ArrayList<BufferedReader> files = new ArrayList<BufferedReader>();
		try {
			for(int i = 0; i < args.length; i++) {
				files.add(new BufferedReader(new FileReader(args[i])));
			}			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("File not found!");
		}
		
		String line;
		try {
			while((line = files.get(0).readLine()) != null) {
				String[] split = line.split("\\t");
				String pair = split[0] + "\t" + split[1];
				LinkedList<String> errors = new LinkedList<String>();
				if (split.length >= 3) {
					if (split[2].length() > 5) {
						errors.add(split[2]);
					}
				}
				for(int i = 1; i < files.size(); i++) {
					line = files.get(i).readLine();
					split = line.split("\\t");
					if (split.length >= 3) {
						if (split[2].length() > 5) {
							errors.add(split[2]);
						}
					}
				}
				StringBuilder result = new StringBuilder();
				result.append(pair);
				if (errors.size() > 0) {
					result.append("\t");
					for (int i = 0; i < errors.size(); i++) {
						result.append(errors.get(i));
						if (i < errors.size()-1) {
							result.append("|");
						}
					}
				}
				System.out.println(result);
			}			
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error reading input!");
		}

	}
}