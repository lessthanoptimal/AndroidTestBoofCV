package boofcv.benchmark.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class LowLevelActivity extends Activity implements BenchmarkThread.Listener {

	int numberOfClicks = 0;
	BenchmarkThread thread;
	boolean running = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_low_level);
        
        // get the message from the intent
        Intent intent = getIntent();

        thread = (BenchmarkThread)intent.getSerializableExtra(MainActivity.WHICH_MESSAGE);
        
        TextView textView = (TextView) findViewById(R.id.textViewResults);
        textView.setMovementMethod(new ScrollingMovementMethod());
        

        textView.setText(thread.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_low_level, menu);
        return true;
    }
    
    public void onStartButton( View view ) {
    	TextView textView = (TextView) findViewById(R.id.textViewResults);

    	if( !running ) {
    		running = true;
    		textView.setText("");
    		try {
				thread = thread.getClass().newInstance();
			} catch (Exception e) {
				textView.setText("Failed!");
				running = false;
				return;
			} 
    		thread.configure(textView, getResources(), this);

    		thread.start();
			
    	}	
    }

	@Override
	public void benchmarkFinished() {
		TextView textView = (TextView) findViewById(R.id.textViewResults);
		textView.append("Called Finished\n");
		running = false;
	}

    
}
