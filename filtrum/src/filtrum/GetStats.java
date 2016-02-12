package filtrum;

import java.util.*;
import java.io.*;

/** Individual and combined filter statistics calculation.
 * 
 * @author Adam Liska
 *
 */
public class GetStats {

	BufferedReader in;
	Statistics stats;
	
	/** Constructor, takes a BufferedReader with the input as a parameter.
	 *  
	 * @param in BufferedReader with the input in the format: annotation \t firstLang_segment \t secondLang_segment \t errorList, 
	 * where errors are separated by |.
	 */
	public GetStats(BufferedReader in) {
		this.in = in;
		stats = new Statistics(); 
	}
		
	/** Calculates individual and combined filter statistics from the input.
	 * 
	 * @return Collection of filter statistics
	 */
	public Collection<FilterStats> calculateFilterStats() {
		String line;
	
		int i = 0;
		int totalBadSegments = 0;
		
		try {
			while((line = in.readLine()) != null) {
				i++;
				String[] fields = line.split("\\t");
				
				if (!fields[0].equals("x") && !fields[0].equals("ok")) {
					throw new RuntimeException("Incorrect annotation on line: " + i + "!");
				}				
				if (fields[0].equals("x")) {
					totalBadSegments++;
				}				
				processLine(fields);
			}
			stats.calculateAndSetRecalls(totalBadSegments);
		} catch (IOException e) {
			throw new RuntimeException("Error reading input!");
		}
		
		return stats.getStats();
	}

	/** Processes input line.
	 * 
	 * @param fields fields of the input.
	 */
	private void processLine(String[] fields) {
		if (fields.length >= 4) {
			String[] errors = extractErrorNames(fields[3]);
			if (fields[0].equals("x")) {
				stats.truePositive(errors);
			} else if (fields[0].equals("ok")) {
				stats.falsePositive(errors);
			}		
		}
	}
	
	/** Extracts errors from the error field.
	 * 
	 * @param errorField 
	 * @return Array of errors.
	 */
	public static String[] extractErrorNames(String errorField) {
		String[] errors = errorField.split("\\|");
		return errors;
	}

	/** Prints final filter statistics.
	 * 
	 * @param stats Collection of FilterStats objects.
	 */
	public void printFilterStats(Collection<FilterStats> stats) {
		
		System.out.println("Filter name" + "\t" + "Times Fired" + "\t" + "Precision" + "\t" + "Recall");

		for(FilterStats filter:stats) {
			StringBuilder output = new StringBuilder();
			output.append(filter.getName() + "\t");
			output.append(filter.getTimesFired() + "\t");
			output.append(filter.getPrecision() + "\t");
			output.append(filter.getRecall());			
			
			System.out.println(output.toString());
		}
	}

	/** Takes input from the standard input and prints results on the standard output.
	 * 
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		
		GetStats process = new GetStats(new BufferedReader(new InputStreamReader(System.in)));
		Collection<FilterStats> result = process.calculateFilterStats();
		process.printFilterStats(result);
	}
}
