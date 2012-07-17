package boofcv.benchmark.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	public static final String WHICH_MESSAGE = "WhichBenchmark";
	
	BenchmarkResultsCodec codec;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setTitle(R.string.title_activity_main);
        
        CentralMemory.reset();
        
        codec = new BenchmarkResultsCodec(this);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if( CentralMemory.isThreadRunning() ) {
    		new KillBenchmarkThread(this).start();
    	} else {
    		CentralMemory.reset();
    	
    		if( CentralMemory.storageUpdated ) {
    			CentralMemory.storageUpdated = false;
				updateScore(LowLevelBenchmark.NAME, R.id.textLowLevel);
				updateScore(ImageConvertBenchmark.NAME, R.id.textConvert);
				updateScore(BinaryOpsBenchmark.NAME, R.id.textBinary);
				updateScore(FeatureBenchmark.NAME, R.id.textFeatures);

				List<BenchmarkResults> results = new ArrayList<BenchmarkResults>(
						CentralMemory.storageResults.values());
				codec.write("local_results.txt", results);
    		}
    	}
    
    	
    }
    
    private void updateScore( String name , int textID ) {
    	if( CentralMemory.storageResults.containsKey(name) ) {
    		BenchmarkResults results = CentralMemory.storageResults.get(name);
    		BenchmarkResults resultsB = CentralMemory.storageBaseLine.get(name);
    		
    		TextView text = (TextView) findViewById(textID);
    		
    		if( resultsB == null ) {
    			text.setText("--");
    		} else {
    			double score = results.computeScore();
    			double scoreB = resultsB.computeScore();
    					
    			text.setText(String.format("%6.1f",100*(score/scoreB)));
    		}
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_submit:
            	onSubmitResults();
                return true;
            case R.id.menu_copy:
            	onCopyResultsFile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void switchImageConvert( View view ) {
    	Intent intent = new Intent(this, BenchmarkActivity.class);
    	CentralMemory.setBenchmark(ImageConvertBenchmark.class);
    	startActivity(intent);
    }

    public void switchLowLevel( View view ) {
    	Intent intent = new Intent(this, BenchmarkActivity.class);
    	CentralMemory.setBenchmark(LowLevelBenchmark.class);
    	startActivity(intent);
    }
    
    public void switchBinary( View view ) {
    	Intent intent = new Intent(this, BenchmarkActivity.class);
    	CentralMemory.setBenchmark(BinaryOpsBenchmark.class);
    	startActivity(intent);
    }
    
    public void switchFeatures( View view ) {
    	Intent intent = new Intent(this, BenchmarkActivity.class);
    	CentralMemory.setBenchmark(FeatureBenchmark.class);
    	startActivity(intent);
    }
    
    public void switchVisualTest( View view ) {
    	Intent intent = new Intent(this, VisualDebugActivity.class);
    	startActivity(intent);
    }
    
    public void switchRunAllTests( View view ) {
//    	Intent intent = new Intent(this, VisualDebugActivity.class);
//    	startActivity(intent);
    }
    
    public void onSubmitResults() {
    	
    }
    
    /**
     * Copy results file to a location which can be accessed by mounting the phone as a hard disk
     */
    public void onCopyResultsFile() {
		if (!codec.copyToExternal("local_results.txt")) {
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Copy Failed!");
			dialog.setButton("OK", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    } }); 
			dialog.show();
		} else {
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Copied to /Android/data/boofcv.benchmark.android/files/");
			dialog.setButton("OK", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    } }); 
			dialog.show();
		}
    }
}
