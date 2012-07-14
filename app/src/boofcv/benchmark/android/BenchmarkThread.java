package boofcv.benchmark.android;

import java.io.Serializable;

import android.content.res.Resources;
import android.widget.TextView;

public abstract class BenchmarkThread extends Thread implements Serializable {
	
	TextView view;
	Listener listener;
	
	long targetTime = 1000;
	
	public BenchmarkThread() {
		setPriority(MAX_PRIORITY);
	}
	
	public void configure( TextView textView, Resources resources, Listener listener ) {
		this.view = textView;
		this.listener = listener;
	}
	
	public abstract String getDescription();
	
	public void publishText( final String text ) {
		view.post(new Runnable() {
            public void run() {
            	view.append(text);
            }
        });
	}
	
	public void benchmark( String name , EvaluationProcess process ) {
		long N = 1;
		long found = evaluate(N,process);
		
		while( found < targetTime/5 ) {
			N = 2+N*N;
			found = evaluate(N,process);
		}
		
		int sanity = 0;
		while( found < targetTime && sanity++ < 1000 ) {
			// linearly estimate how long many trials it should take, plus a fudge factor
			long testN = (long)Math.ceil((1.1*N*targetTime)/found);
			if( testN < 0 )
				System.out.println();
			if( testN <= N )
				testN = N+1;
			found = evaluate(testN,process);
			N = testN;
		}
		
		publishResults(name,(double)found/(double)N );
	}
	
	private long evaluate( long trials , EvaluationProcess target ) {
		long before = System.currentTimeMillis();
		target.process(trials);
		long after = System.currentTimeMillis();
		
		return after-before;
	}
	
	public void publishResults( final String benchmarkName , final double results ) {
		view.post(new Runnable() {
            public void run() {
            	view.append(String.format("%22.22s | %6.1f\n",benchmarkName,results));
            }
        });
	}
	
	public void finished() {
		view.post(new Runnable() {
            public void run() {
            	listener.benchmarkFinished();
            }
        });
	}

	public interface Listener {
		public void benchmarkFinished();
	}
}
