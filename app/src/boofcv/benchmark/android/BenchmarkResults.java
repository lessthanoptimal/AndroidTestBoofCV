package boofcv.benchmark.android;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains results of benchmark test
 * 
 * @author Peter Abeles
 *
 */
public class BenchmarkResults implements Serializable {

	private static final long serialVersionUID = 1838240192L;
	public String name;
	public String text;
	public List<Pair> results = new ArrayList<Pair>();
	
	public void setText( String text ) {
		this.text = text;
	}
	
	public void addResult( String testName , double result ) {
		results.add( new Pair(testName,result));
	}
	
	public double computeScore() {
		double total = 0;
		
		for( Pair p : results ) {
			total += p.result;
		}
		
		return total;
	}
	
	public static class Pair implements Serializable
	{
		private static final long serialVersionUID = 93384L;
		
		public String testName;
		public double result;
		
		public Pair( String testName , double result ) {
			this.testName = testName;
			this.result = result;
		}

		public String getTestName() {
			return testName;
		}

		public double getResult() {
			return result;
		}
		
		
	}

}
