package boofcv.benchmark.android;

import android.content.res.Resources;

/**
 * Benchmark which runs all the other benchmarks
 * 
 * @author Peter Abeles
 *
 */
public class RunAllBenchmark extends BenchmarkThread {

	Resources resources;

	public RunAllBenchmark() {
		super(null);
	}

	@Override
	public void configure(Resources resources) {
		this.resources = resources;
	}
	
	@Override
	public void run() {
		if( runBencmark(new Binary() ) ) return;
		if( runBencmark(new Feature() ) ) return;
		if( runBencmark(new Convert() ) ) return;
		if( runBencmark(new LowLevel() ) ) return;
		
		finished();
	}
	
	private boolean runBencmark( BenchmarkThread thread ) {
		thread.configure(resources);
		thread.run();
		if( checkInterrupted() ) 
			return true;
		return false;
	}
	
	private boolean checkInterrupted() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			interrupted = true;
			finished();
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Runs every benchmarks one after another.  Can take a bit to finish.";
	}
	
	@Override
	public void finished() {
		CentralMemory.markFinished();
	}

	private class Binary extends BinaryOpsBenchmark {
	
		@Override
		public void finished() {
			if( !interrupted ) {
				resultsStorage.setText(CentralMemory.text);
				CentralMemory.storageResults.put(resultsStorage.name, resultsStorage);
				CentralMemory.storageUpdated = true;
			}
		}
	}
	
	private class Feature extends FeatureBenchmark {
		@Override
		public void finished() {
			if( !interrupted ) {
				resultsStorage.setText(CentralMemory.text);
				CentralMemory.storageResults.put(resultsStorage.name, resultsStorage);
				CentralMemory.storageUpdated = true;
			}
		}
	}
	
	private class Convert extends ImageConvertBenchmark {
		@Override
		public void finished() {
			if( !interrupted ) {
				resultsStorage.setText(CentralMemory.text);
				CentralMemory.storageResults.put(resultsStorage.name, resultsStorage);
				CentralMemory.storageUpdated = true;
			}
		}
	}
	
	private class LowLevel extends LowLevelBenchmark {
		@Override
		public void finished() {
			if( !interrupted ) {
				resultsStorage.setText(CentralMemory.text);
				CentralMemory.storageResults.put(resultsStorage.name, resultsStorage);
				CentralMemory.storageUpdated = true;
			}
		}
	}
	
}
