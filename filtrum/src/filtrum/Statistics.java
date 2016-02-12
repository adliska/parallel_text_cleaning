package filtrum;

import java.util.*;

/** Collection of filter statistics and methods to update them. It keeps track of individual filters 
 * statistics and also of their combined statistics. 
 *  
 * @author Adam Liska
 *
 */
public class Statistics {

	HashMap<String, FilterStats> errorMap;
	String combined = "combined";
	
	public Statistics() {
		errorMap = new HashMap<String, FilterStats>();
		errorMap.put(combined, new FilterStats(combined));
	}
	
	public void calculateAndSetRecalls(int totalBadSegments) {
		for(FilterStats filter:errorMap.values()) {
			filter.setRecall(totalBadSegments);
		}
	}

	/** Increments falsePositive property for all given errors.
	 * 
	 * @param errors An array of error names.
	 */
	public void falsePositive(String[] errors) {
		for(String error:errors) {
			if (!errorMap.containsKey(error)) {
				FilterStats fstat = new FilterStats(error);
				errorMap.put(error, fstat);
			}
			errorMap.get(error).incFalsePositives();
		}
		if (errors.length > 0) {
			errorMap.get(combined).incFalsePositives();
		}
	}
	
	/** Increments truePositive property for all given errors.
	 * 
	 * @param errors An array of error names.
	 */
	public void truePositive(String[] errors) {
		for(String error:errors) {
			if (!errorMap.containsKey(error)) {
				FilterStats fstat = new FilterStats(error);
				errorMap.put(error, fstat);
			}
			errorMap.get(error).incTruePositives();
		}
		if (errors.length > 0) {
			errorMap.get(combined).incTruePositives();
		}
	}
	
	/** Returns the collection of filter statistics.
	 * 
	 * @return Collection of filter statistics.
	 */
	public Collection<FilterStats> getStats() {
		return errorMap.values();
	}
}