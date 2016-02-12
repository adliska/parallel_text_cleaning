package filtrum;

/** Individual filter statistics
 * 
 * @author Adam Liska
 *
 */
public class FilterStats {
	private String name;
	private int tp; // true positives (positive that it's a bad segment) xx (first is annotation)
	private int fp; //okx
	private double recall;
	
	public FilterStats(String name) {
		this.name = name;
		tp = 0;
		fp = 0;
	}
	
	public double getPrecision() {
		return tp / (double) (tp + fp);
	}
	
	public int getTimesFired() {
		return tp + fp;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setTruePositives(int tp) {
		this.tp = tp;
	}
	
	public void incTruePositives() {
		tp++;
	}
	
	public int getTruePositives() {
		return tp;
	}
	
	public void setFalsePositives(int fp) {
		this.fp = fp;
	}
	
	public void incFalsePositives() {
		fp++;
	}
	
	public int getFalsePositives() {
		return fp;
	}
	
	public void setRecall(int totalNumOfBadSegments) {
		recall = tp / (double) totalNumOfBadSegments;
	}
	
	public double getRecall() {
		return recall;
	}
}
