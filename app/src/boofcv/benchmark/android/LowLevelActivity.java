package boofcv.benchmark.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

// TODO on rotation the view and listener will change
public class LowLevelActivity extends Activity implements BenchmarkThread.Listener {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_level);

        TextView textView = (TextView) findViewById(R.id.textViewResults);
        textView.setMovementMethod(new ScrollingMovementMethod());
        
	   	if( CentralMemory.hasResults() ) {
	   		CentralMemory.updateActivity(this);
	   		textView.setText(CentralMemory.text);
	   	}else{
	   		textView.setText(CentralMemory.thread.getDescription());
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

    	if( !CentralMemory.isThreadRunning() ) {
    		CentralMemory.startThread(getResources(),this);
    		textView.setText(CentralMemory.text);
    	}	
    }

	@Override
	public void benchmarkFinished() {
		TextView textView = (TextView) findViewById(R.id.textViewResults);
		textView.append("Called Finished\n");
	}

	@Override
	public void updateText() {
		TextView textView = (TextView) findViewById(R.id.textViewResults);
		textView.setText(CentralMemory.text);
	}

    
}
