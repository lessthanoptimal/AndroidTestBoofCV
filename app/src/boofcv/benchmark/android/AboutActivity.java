package boofcv.benchmark.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.widget.TextView;

public class AboutActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        TextView textView = (TextView) findViewById(R.id.textViewAbout);
        
        String text =
        		"BoofCV: http://boofcv.org\n"+
                "Benchmark Version: "+MainActivity.VERSION+"\n"+
        	    "\n"+
                "- Higher scores are better.\n"+
        	    "- All times are in milliseconds\n"+
                "\n"+
        	    "BoofCV is an open source computer vision library written entirely in Java and "+
        	    "released under an Apache 2.0 license.  "+
        		"This application performs a series of diagnostic tests and "+
        	    "benchmarks to evaluate BoofCV's performance on different Android "+
        		"platforms.  Please help improve BoofCV on Android by submitting by "+
        	    "running all the benchmarks then submitting the results.  To submit "+
        		"results click on the options menu.\n"+
        		"\n"+
        		"- Peter Abeles\n";
        
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(text);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_about, menu);
        return true;
    }

    
}
