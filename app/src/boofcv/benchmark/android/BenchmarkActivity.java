package boofcv.benchmark.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


public class BenchmarkActivity extends Activity implements BenchmarkThread.Listener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benchmark);

		
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);
        
        TextView textView = (TextView) findViewById(R.id.textViewResults);
        textView.setMovementMethod(new ScrollingMovementMethod());
        // prevents the screen from going to sleep and slowing down the CPU
        // Unlike the power manager approach no extra permission is required and I can't
        // accidentially leave it on
        textView.setKeepScreenOn(true);
        
	   	if( CentralMemory.hasResults() ) {
	   		CentralMemory.updateActivity(this);
	   		textView.setText(CentralMemory.text);
	   	} else{
	   		textView.setText(CentralMemory.thread.getDescription());
	   	}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	// make sure it has full control over the CPU
    	
    	if( CentralMemory.hasResults() ) {
	   		CentralMemory.updateActivity(this);
	   		TextView textView = (TextView) findViewById(R.id.textViewResults);
	   		textView.setText(CentralMemory.text);
	   	}
    	
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
        Button button = (Button) findViewById(R.id.buttonStart);
        
    	if( CentralMemory.isThreadRunning() ) {
    		pb.setVisibility(View.VISIBLE);
            button.setText("Stop");
    		button.setTextColor(0xFFFF0000);
    	} else {
    		pb.setVisibility(View.INVISIBLE);
            button.setText("Start");
            button.setTextColor(0xFF000000);
    	}
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	CentralMemory.updateActivity( null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_low_level, menu);
        return true;
    }
    
    public void onStartButton( View view ) {
    	TextView textView = (TextView) findViewById(R.id.textViewResults);
    	Button button = (Button) findViewById(R.id.buttonStart);
    	
    	if( !CentralMemory.isThreadRunning() ) {
    		CentralMemory.startThread(getResources(),this);
    		textView.setText(CentralMemory.text);
    		
    		ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
    		pb.setVisibility(View.VISIBLE);
    		
    		button.setText("Stop");
    		button.setTextColor(0xFFFF0000);
    	} else {
    		new KillBenchmarkThread(this).start();
    	}
    }

	@Override
	public void benchmarkFinished() {
		Button button = (Button) findViewById(R.id.buttonStart);
		button.setText("Start");
		button.setTextColor(0xFF000000);
		
		ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
		pb.setVisibility(View.INVISIBLE);
	}

	@Override
	public void updateText() {
		TextView textView = (TextView) findViewById(R.id.textViewResults);
		textView.setText(CentralMemory.text);
	}
    
}
