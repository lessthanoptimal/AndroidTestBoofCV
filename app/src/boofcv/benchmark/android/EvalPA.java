package boofcv.benchmark.android;

public abstract class EvalPA implements EvaluationProcess {

	@Override
	public void process(long numTrials) {
		for( long i = 0; i < numTrials; i++ ) {
			_process();
		}
	}
	
	public abstract void _process();

}
