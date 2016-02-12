package filtrum;

import java.io.*;
import java.util.*;

/** Tool for extracting sentence pairs for evaluation.
 * 
 * @author Adam Liska
 *
 */
public class SelectForAnnotation {
	/** This method prints help.
	 * 
	 */
	private static void printHelp() {
		System.out.println("Use:");
		System.out.println("SelectForAnnotation first_limit error_limit");
		System.out.println("");
		
	}
	
	public static void main(String[] args) {
		if (args.length < 2) {
			printHelp();
			return;
		}
		
		int firstLimit;
		int errorLimit;
		HashMap<String, Integer> errorList = new HashMap<String, Integer>();
		try {
			firstLimit = Integer.decode(args[0]);
			errorLimit = Integer.decode(args[1]);
			for(int i = 2; i < args.length; i++) {
				errorList.put(args[i], 0);
			}			
		} catch (Exception e) {
			printHelp();
			return;
		}
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				
		try {
			int i = 0;
			String line;
			
			while((line = in.readLine()) != null) {
				i++;
				boolean print = false;
				if (i <= firstLimit) {
					print = true;
				}
				
				String[] fields = line.split("\\t");
				if (fields.length >= 4) {
					String[] errors = GetStats.extractErrorNames(fields[3]);
					for (String error:errors) {
						if (errorList.containsKey(error)) {
							print = true;
							int count = errorList.get(error);
							count++;
							if (count >= errorLimit) {
								errorList.remove(error);
							} else {
								errorList.put(error, count);
							}
						}
					}					
				}
				
				if (print) {
					System.out.println(line);
				}
				if (errorList.isEmpty()) {
					break;
				}
			}
			
			if (!errorList.isEmpty()) {
				System.out.println("There were some errors that didn't fire enough times!");
				System.out.println("Error name" + "\t" + "Times fired");
				for(String error:errorList.keySet()) {
					System.out.println(error + "\t" + errorList.get(error));
				}
			}
			
		} catch (IOException e) {
			throw new RuntimeException("Error reading input!");
		}
	}
}