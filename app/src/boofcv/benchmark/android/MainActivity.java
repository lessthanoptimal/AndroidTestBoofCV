package boofcv.benchmark.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	public static final String WHICH_MESSAGE = "WhichBenchmark";
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        CentralMemory.reset();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if( CentralMemory.isThreadRunning() ) {
    		new KillBenchmarkThread(this).start();
    	} else {
    		CentralMemory.reset();
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void switchImageConvert( View view ) {
    	if( CentralMemory.isThreadRunning() )
    		throw new RuntimeException("Failed sanity check!");
    	
    	Intent intent = new Intent(this, LowLevelActivity.class);
    	CentralMemory.setBenchmark(ImageConvertBenchmark.class);
    	startActivity(intent);
    }

    public void switchLowLevel( View view ) {
    	Intent intent = new Intent(this, LowLevelActivity.class);
    	CentralMemory.setBenchmark(LowLevelBenchmark.class);
    	startActivity(intent);
    }
    
    public void switchBinary( View view ) {
    	Intent intent = new Intent(this, LowLevelActivity.class);
    	CentralMemory.setBenchmark(BinaryOpsBenchmark.class);
    	startActivity(intent);
    }
    
    public void switchFeatures( View view ) {
    	Intent intent = new Intent(this, LowLevelActivity.class);
    	CentralMemory.setBenchmark(FeatureBenchmark.class);
    	startActivity(intent);
    }
    
    public void switchVisualTest( View view ) {
    	Intent intent = new Intent(this, VisualDebugActivity.class);
    	startActivity(intent);
    }
}
