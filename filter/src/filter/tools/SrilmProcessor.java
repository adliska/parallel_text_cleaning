package filter.tools;

import java.io.*;
/** SRILM outuput processor.
 * 
 * @author Adam Liska
 *
 */

public class SrilmProcessor {
	public static void processInputStream(BufferedReader srilm) throws IOException {
		String line;
		int i = 0;
		while((line = srilm.readLine()) != null) {
			i++;

			if (line.startsWith("file")) {
				break;
			}
			
			line = srilm.readLine();

			Double numchars = new Double(line.split(" ")[2]);
			line = srilm.readLine();
			if (numchars > 35) {
				Double prob = new Double(line.split(" ")[3]);
				Double result = (prob / numchars);
				System.out.println(result);
			} else {
				System.out.println("1000");
			}
			
			srilm.readLine();

		}		
	}
	
	public static void printHelp() {
		System.out.println("Use:");
		System.out.println("SrilmProcessor srilm_output > result");
	}

	
	public static void main(String[] args) {
		if (args.length == 1) {
			try {
				BufferedReader srilm = new BufferedReader(new FileReader(args[0]));
				processInputStream(srilm);
			}
			catch (IOException e) {
				throw new RuntimeException("Error reading input!");
			}
		} else {
			printHelp();
		}
		

		
	}
}
