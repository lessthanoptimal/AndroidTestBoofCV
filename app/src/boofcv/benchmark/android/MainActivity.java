package boofcv.benchmark.android;

import android.app.Activity;
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void switchImageConvert( View view ) {
    	Intent intent = new Intent(this, LowLevelActivity.class);
    	intent.putExtra(WHICH_MESSAGE, new ImageConvertBenchmark());
    	startActivity(intent);
    }

    public void switchLowLevel( View view ) {
    	Intent intent = new Intent(this, LowLevelActivity.class);
    	intent.putExtra(WHICH_MESSAGE, new LowLevelBenchmark());
    	startActivity(intent);
    }
    
    public void switchBinary( View view ) {
    	Intent intent = new Intent(this, LowLevelActivity.class);
    	intent.putExtra(WHICH_MESSAGE, new BinaryOpsBenchmark());
    	startActivity(intent);
    }
    
    public void switchFeatures( View view ) {
    	Intent intent = new Intent(this, LowLevelActivity.class);
    	intent.putExtra(WHICH_MESSAGE, new FeatureBenchmark());
    	startActivity(intent);
    }
    
    public void switchVisualTest( View view ) {
    	Intent intent = new Intent(this, VisualDebugActivity.class);
    	startActivity(intent);
    }
}
