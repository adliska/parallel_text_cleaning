package filtrum;

import java.util.*;
import java.io.*;

/** Statistics for filters with parameters.
 * 
 * @author Adam Liska
 *
 */

public class ParameterStats {
	public static void main(String[] args) throws IOException {
		ArrayList<Pair> data = new ArrayList<Pair>();
		
		String line;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		int incorrectSegs = 0;
		double tp = 0;
		double fp = 0;
		double total = 0;
		while((line = in.readLine()) != null) {
			total++;
			String[] split = line.split("\\t");
			if (split[1].equals("x")) {
				incorrectSegs++;
				Pair pair = new Pair(false, new Double(split[0]));
				data.add(pair);
			} else {
				Pair pair = new Pair(true, new Double(split[0]));
				data.add(pair);				
			}
		}
		
		for(int i = 0; i < data.size(); i++) {		
			if (data.get(i).anotation) {
				fp++;
			} else {
				tp++;
			}

			System.out.println(tp/(fp+tp)*100 + "\t" + tp/incorrectSegs*100 + "\t" + data.get(i).score);
		}
		
		
	}
}

class Pair {
	public boolean anotation;
	public double score;
	
	public Pair(boolean anotation, double score) {
		this.anotation = anotation;
		this.score = score;
	}
}