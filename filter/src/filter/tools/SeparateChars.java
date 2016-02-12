package filter.tools;

import java.io.*;

/** Separation of characters for SRILM toolkit.
 * 
 * @author Adam Liska
 *
 */
public class SeparateChars {
	public static void main(String[] args) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String line;
			while((line = in.readLine()) != null) {
				String[] words = line.split(" ");
				StringBuilder result = new StringBuilder();
				for(String word:words) {
					if (!word.equals("&pipe")) {
						char[] chars = word.toLowerCase().toCharArray();
						for(int i = 0; i < chars.length; i++) {
							if (chars[i] == ' ') {
								result.append("SpAcE");
							} else {
								result.append(chars[i]);	
							}
							result.append(" ");
						}
						result.append("SpAcE ");
					}
				}
				System.out.println(result);
			}
			in.close();
		} catch (IOException e) {
			throw new RuntimeException("Error reading input!");
		}
	}
}
