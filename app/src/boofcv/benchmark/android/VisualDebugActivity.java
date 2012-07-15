package boofcv.benchmark.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;

public class VisualDebugActivity extends Activity implements OnItemSelectedListener {

	Spinner spinnerAndroid;
	Spinner spinnerBoof;
	
	Bitmap.Config selectedAndroid = Bitmap.Config.ARGB_8888;
	int selectedBoofcv = 0;
	boolean isInitialized = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visual_debug);

		spinnerAndroid = (Spinner) findViewById(R.id.spinnerAndroidTypes);
		spinnerBoof = (Spinner) findViewById(R.id.spinnerBoofcvTypes);

		setupSpinner(R.id.spinnerAndroidTypes,R.array.AndroidTypes);
		setupSpinner(R.id.spinnerBoofcvTypes,R.array.BoofcvTypes);
		
		render();
		isInitialized = true;

	}
	
	private void setupSpinner( int spinnerID, int textArrayID ) {
		Spinner spinner = (Spinner) findViewById(spinnerID);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, textArrayID,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_visual_debug, menu);
		return true;
	}
	
	private void render() {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		
		Bitmap input = BitmapFactory.decodeResource(getResources(),R.drawable.sundial01_left,options);
		
		// convert the input image into the desired image type
		if( input.getConfig() != selectedAndroid ) {
			input = input.copy(selectedAndroid, true);
		}
		
		// Declare the appropriate BoofCV Type
		ImageBase middle = null;
		
		switch( selectedBoofcv ) {
		case 0:
			middle = new ImageUInt8(input.getWidth(),input.getHeight());
			break;
			
		case 1:
			middle = new ImageFloat32(input.getWidth(),input.getHeight());
			break;
			
		case 2:
			middle = new MultiSpectral<ImageUInt8>(ImageUInt8.class,input.getWidth(),input.getHeight(),4);
			break;

		case 3:
			middle = new MultiSpectral<ImageFloat32>(ImageFloat32.class,input.getWidth(),input.getHeight(),4);
			break;
		}
		
		// convert into the correct boofcv type
		if( selectedBoofcv <= 1 ) {
			ConvertBitmap.bitmapToGray(input, (ImageSingleBand)middle, (Class)middle.getClass());
		} else {
			MultiSpectral ms = (MultiSpectral)middle;
			ConvertBitmap.bitmapToMS(input, ms, ms.getType());
		}
		
		// convert it back into the android type
		if( selectedBoofcv <= 1 ) {
			ConvertBitmap.grayToBitmap((ImageSingleBand)middle, input);
		} else {
			MultiSpectral ms = (MultiSpectral)middle;
			ConvertBitmap.multiToBitmap(ms,input);
		}
		
		// update the view
		ImageView view = (ImageView) findViewById(R.id.imageViewTest);
		view.setImageBitmap(input);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

		if( parent == spinnerAndroid ) {
			switch( pos ) {
			case 0:
				this.selectedAndroid = Bitmap.Config.ARGB_8888;
				break;
				
			case 1:
				this.selectedAndroid = Bitmap.Config.RGB_565;
				break;
				
			default:
				throw new RuntimeException("Unknown android selection");
			}
		} else if( parent == spinnerBoof ){
			this.selectedBoofcv = pos;
		}
		if( isInitialized )
			render();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}

}
