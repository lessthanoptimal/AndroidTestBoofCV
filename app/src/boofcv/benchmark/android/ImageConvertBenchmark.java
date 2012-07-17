package boofcv.benchmark.android;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;

public class ImageConvertBenchmark extends BenchmarkThread {

	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "ImageConvert";
	
	Bitmap bitmap;
	Bitmap bitmapOut;
	
	public ImageConvertBenchmark() {
		super(NAME);
	}
	
	@Override
	public void configure( Resources resources ) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		
	   	bitmap = BitmapFactory.decodeResource(resources,R.drawable.sundial01_left,options);
	}
	
	@Override
	public void run() {
	  	if( bitmap == null ) {
	  		finished();
    		return;
    	}
    	
	  	publishText(" Input size = "+bitmap.getWidth()+" x "+bitmap.getHeight()+"\n");
	  	publishText("\n");
    	
    	int storage[] = new int[bitmap.getWidth()*bitmap.getHeight()];
    	
    	benchmarkU8(storage);
    	benchmarkF32(storage);
    	benchmarMultiU8(storage);
    	benchmarMultiF32(storage);

    	finished();	
	}

	private void benchmarkU8( final int[] storage) {
    	final ImageUInt8 gray = new ImageUInt8(bitmap.getWidth(),bitmap.getHeight());
		
    	benchmark("Array 8888 to U8",new EvalPA() {
			public void _process() {ImplConvertBitmap.bitmapToGrayArray(bitmap, gray,storage);}});
    	
    	benchmark("Reflect 8888 to U8",new EvalPA() {
			public void _process() {ImplConvertBitmap.bitmapToGrayReflection(bitmap, gray);}});
    	
    	benchmark("RGB 8888 to U8",new EvalPA() {
			public void _process() {ImplConvertBitmap.bitmapToGrayRGB(bitmap, gray);}});
    	
    	bitmapOut = Bitmap.createBitmap(gray.width, gray.height, Bitmap.Config.ARGB_8888);
    	
    	benchmark("Reflect U8 to 8888",new EvalPA() {
			public void _process() {ImplConvertBitmap.grayToBitmapReflection(gray, bitmapOut);}});
    	
    	benchmark("RGB U8 to 8888",new EvalPA() {
			public void _process() {ImplConvertBitmap.grayToBitmapRGB(gray, bitmapOut);}});
    	
    	bitmapOut = Bitmap.createBitmap(gray.width, gray.height, Bitmap.Config.RGB_565);

    	benchmark("Reflect U8 to 565",new EvalPA() {
			public void _process() {ImplConvertBitmap.grayToBitmapReflection(gray, bitmapOut);}});
	}
	
	private void benchmarkF32( final int[] storage) {
    	final ImageFloat32 gray = new ImageFloat32(bitmap.getWidth(),bitmap.getHeight());
		
    	benchmark("Array 8888 to F32",new EvalPA() {
			public void _process() {ImplConvertBitmap.bitmapToGrayArray(bitmap, gray,storage);}});
    	
    	benchmark("Reflect 8888 to F32",new EvalPA() {
			public void _process() {ImplConvertBitmap.bitmapToGrayReflection(bitmap, gray);}});
    	
    	benchmark("RGB 8888 to F32",new EvalPA() {
			public void _process() {ImplConvertBitmap.bitmapToGrayRGB(bitmap, gray);}});
    	
    	bitmapOut = Bitmap.createBitmap(gray.width, gray.height, Bitmap.Config.ARGB_8888);
    	
    	benchmark("Reflect F32 to 8888",new EvalPA() {
			public void _process() {ImplConvertBitmap.grayToBitmapReflection(gray, bitmapOut);}});
    	
    	benchmark("RGB F32 to 8888",new EvalPA() {
			public void _process() {ImplConvertBitmap.grayToBitmapRGB(gray, bitmapOut);}});
    	
    	bitmapOut = Bitmap.createBitmap(gray.width, gray.height, Bitmap.Config.RGB_565);

    	benchmark("Reflect F32 to 565",new EvalPA() {
			public void _process() {ImplConvertBitmap.grayToBitmapReflection(gray, bitmapOut);}});
	}
	
	private void benchmarMultiU8( final int[] storage) {
    	final MultiSpectral<ImageUInt8> color = new MultiSpectral<ImageUInt8>(ImageUInt8.class,bitmap.getWidth(),bitmap.getHeight(),4);
		
    	benchmark("Array 8888 to MU8",new EvalPA() {
			public void _process() {ImplConvertBitmap.bitmapToMultiArray_U8(bitmap, color,storage);}});
    	
    	benchmark("Reflect 8888 to MU8",new EvalPA() {
			public void _process() {ImplConvertBitmap.bitmapToMultiReflection_U8(bitmap, color);}});
    	
    	benchmark("RGB 8888 to MU8",new EvalPA() {
			public void _process() {ImplConvertBitmap.bitmapToMultiRGB_U8(bitmap, color);}});
    	
    	bitmapOut = Bitmap.createBitmap(color.width, color.height, Bitmap.Config.ARGB_8888);
    	
    	benchmark("Reflect MU8 to 8888",new EvalPA() {
			public void _process() {ImplConvertBitmap.multiToBitmapReflection_U8(color, bitmapOut);}});
    	
    	benchmark("RGB MU8 to 8888",new EvalPA() {
			public void _process() {ImplConvertBitmap.multiToBitmapRGB_U8(color, bitmapOut);}});
    	
    	bitmapOut = Bitmap.createBitmap(color.width, color.height, Bitmap.Config.RGB_565);

    	benchmark("Reflect MU8 to 565",new EvalPA() {
			public void _process() {ImplConvertBitmap.multiToBitmapReflection_U8(color, bitmapOut);}});
	}
	
	private void benchmarMultiF32( final int[] storage) {
    	final MultiSpectral<ImageFloat32> color = new MultiSpectral<ImageFloat32>(ImageFloat32.class,bitmap.getWidth(),bitmap.getHeight(),4);
		
    	benchmark("Array 8888 to MF32",new EvalPA() {
			public void _process() {ImplConvertBitmap.bitmapToMultiArray_F32(bitmap, color,storage);}});
    	
    	benchmark("Reflect 8888 to MF32",new EvalPA() {
			public void _process() {ImplConvertBitmap.bitmapToMultiReflection_F32(bitmap, color);}});
    	
    	benchmark("RGB 8888 to MF32",new EvalPA() {
			public void _process() {ImplConvertBitmap.bitmapToMultiRGB_F32(bitmap, color);}});
    	
    	bitmapOut = Bitmap.createBitmap(color.width, color.height, Bitmap.Config.ARGB_8888);
    	
    	benchmark("Reflect MF32 to 8888",new EvalPA() {
			public void _process() {ImplConvertBitmap.multiToBitmapReflection_F32(color, bitmapOut);}});
    	
    	benchmark("RGB MF32 to 8888",new EvalPA() {
			public void _process() {ImplConvertBitmap.multiToBitmapRGB_F32(color, bitmapOut);}});
    	
    	bitmapOut = Bitmap.createBitmap(color.width, color.height, Bitmap.Config.RGB_565);

    	benchmark("Reflect MF32 to 565",new EvalPA() {
			public void _process() {ImplConvertBitmap.multiToBitmapReflection_F32(color, bitmapOut);}});
	}

	@Override
	public String getDescription() {
		return "Converts between Android (Bitmap) and BoofCV image types using different techniques.\n"+
		        "\n"+
		        "Android Bitmap accessor technique\n"+
		        "  RGB = Using Bitmap.getPixel() and Bitmap.setPixel()\n"+
		        "  Reflect = Using Java Reflection to access the private data array in Bitmap.\n"+
		        "  Array = Using Bitmap.copyPixelsToBuffer().\n"+
		        "\n"+
		        "BoofCV Image Type\n"+
		        "  U8 = ImageUInt8\n"+
		        "  F32 = ImageFloat32\n"+
		        "  MU8 = MultSpectral<ImageUInt8>\n"+
		        "  MF32 = MultiSpectral<ImageFloat32>";
	}

}
