package boofcv.benchmark.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String WHICH_MESSAGE = "WhichBenchmark";
	public static final String RESULTS_NAME = "results.txt";
	
	BenchmarkResultsCodec codec;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setTitle(R.string.title_activity_main);
        
        CentralMemory.reset();
        
        codec = new BenchmarkResultsCodec(this);
        
        // Load the baseline results
        CentralMemory.storageBaseLine = codec.read(null,true);
        if( CentralMemory.storageBaseLine == null ) {
        	Toast.makeText(this, "Can't find baseline results", Toast.LENGTH_SHORT).show();
        	CentralMemory.storageBaseLine = new HashMap<String,BenchmarkResults>();
        }
        // Load previously computed results
        CentralMemory.storageResults = codec.read(RESULTS_NAME,false);
        if( CentralMemory.storageResults == null ) {
        	CentralMemory.storageResults = new HashMap<String,BenchmarkResults>();
        } else {
        	refreshScoreDisplay();
        }
        
        // get the app version
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			CentralMemory.APP_VERSION_CODE = pInfo.versionCode;
			CentralMemory.APP_VERSION_NAME = pInfo.versionName;
		} catch (NameNotFoundException e) {
			CentralMemory.APP_VERSION_CODE  = -1;
			CentralMemory.APP_VERSION_NAME = "Unknown";
		}
       

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
    			refreshScoreDisplay();

				List<BenchmarkResults> results = new ArrayList<BenchmarkResults>(
						CentralMemory.storageResults.values());
				codec.write(RESULTS_NAME, results);
    		}
    	}
    }
    
    /**
     * Updates the scores shown to the right of each benchmark button
     */
    private void refreshScoreDisplay() {
		updateScore(LowLevelBenchmark.NAME, R.id.textLowLevel);
		updateScore(ImageConvertBenchmark.NAME, R.id.textConvert);
		updateScore(BinaryOpsBenchmark.NAME, R.id.textBinary);
		updateScore(FeatureBenchmark.NAME, R.id.textFeatures);
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
    					
    			text.setText(String.format("%6.1f",100*(scoreB/score)));
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
        	case R.id.menu_about:
        		onAbout();
        		return true;
        		
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
    	Intent intent = new Intent(this, BenchmarkActivity.class);
    	CentralMemory.setBenchmark(RunAllBenchmark.class);
    	startActivity(intent);
    }
    
    public void onAbout() {
    	Intent intent = new Intent(this, AboutActivity.class);
    	startActivity(intent);
    }
    
    public void onSubmitResults() {
    	if( CentralMemory.storageResults.size() < 4 ) {
    		Toast.makeText(this, "Run all tests before submitting!", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	Intent i = new Intent(Intent.ACTION_SEND);
    	i.setType("text/plain");
    	i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"boofcv.benchmark@gmail.com"});
    	i.putExtra(Intent.EXTRA_SUBJECT, "Boofcv Android Benchmark");
    	i.putExtra(Intent.EXTRA_TEXT   , "See attachment");
    	
    	// put results in a location the e-mail program can access it
    	codec.copyToExternal(RESULTS_NAME);
    	
    	String rawFolderPath = "file://"+getExternalFilesDir("")+"/"+RESULTS_NAME;
    	Log.v(getClass().getSimpleName(), "Attachment URI "+Uri.parse(rawFolderPath ).toString());

    	i.putExtra(Intent.EXTRA_STREAM, Uri.parse(rawFolderPath ));

    	try {
    	    startActivity(Intent.createChooser(i, "Submit results..."));
    	} catch (android.content.ActivityNotFoundException ex) {
    	    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
    	}    	
    }
    
    /**
     * Copy results file to a location which can be accessed by mounting the phone as a hard disk
     */
    public void onCopyResultsFile() {
		if (!codec.copyToExternal(RESULTS_NAME)) {
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Copy Failed!");
			dialog.setButton("OK", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    } }); 
			dialog.show();
		} else {
			Toast.makeText(this, RESULTS_NAME+" to /Android/data/boofcv.benchmark.android/files/", Toast.LENGTH_SHORT).show();
		}
    }
}
