package boofcv.benchmark.android;

import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;
import android.os.Handler;
import boofcv.benchmark.android.BenchmarkThread.Listener;

public class CentralMemory {

	public static Class<BenchmarkThread> benchmarkType;
	public static volatile BenchmarkThread thread;
	public static volatile String text;
	public static volatile boolean isRunning = false;
	public static volatile boolean hasResults = false;
	
	public static volatile Handler handler;
	public static volatile Listener listener;
	
	public static Map<String,BenchmarkResults> storageResults = new HashMap<String,BenchmarkResults>();
	public static Map<String,BenchmarkResults> storageBaseLine = new HashMap<String,BenchmarkResults>();
	public static boolean storageUpdated = false;
	
	public static void setBenchmark( Class type ) {
		benchmarkType = type;
		declareThread();
	}
	

	public static void startThread( Resources resources, Listener listener ) {
		if( isRunning )
			throw new RuntimeException("Thread has not finished yet");
		isRunning = true;
		hasResults = true;
		CentralMemory.handler = new Handler();
		CentralMemory.listener = listener;
		declareThread();
		text = "";
		thread.configure( resources );
		thread.start();
	}
	
	public static boolean isThreadRunning() {
		return isRunning;
	}
	
	public static boolean hasResults() {
		return hasResults;
	}
	
	public static void reset() {
		hasResults = false;
		handler = null;
		listener = null;
	}
	
	public static void markFinished() {
		appendText("\nFinished");
		isRunning = false;
		
		if (handler != null) {
			handler.post(new Runnable() {
				public void run() {
					if( listener != null )
						listener.benchmarkFinished();
				}
			});
		}
	}
	
	public static void updateActivity( Listener listener ) {
		synchronized (thread) {
			CentralMemory.handler = new Handler();
			CentralMemory.listener = listener;
		}
	}
	
	public static void appendText( final String text ) {
		synchronized( thread ) {
			CentralMemory.text += text;
			
			if (handler != null) {
				handler.post(new Runnable() {
					public void run() {
						if( listener != null )
							listener.updateText();
					}
				});
			}
		}
	}
	
	private static void declareThread() {
		try {
			thread = benchmarkType.newInstance();		
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
}
