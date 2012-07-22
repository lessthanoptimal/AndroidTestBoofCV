package boofcv.benchmark.android;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import boofcv.android.ConvertBitmap;
import boofcv.android.ImplConvertBitmap;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;

public class ImageConvertBenchmark extends BenchmarkThread {

	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "ImageConvert";
	
	Bitmap bitmap;
	Bitmap bitmapOut;
	
	int storage32[];
	byte storage8[];
	
	
	public ImageConvertBenchmark() {
		super(NAME);
	}
	
	@Override
	public void configure( Resources resources ) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		
	   	bitmap = BitmapFactory.decodeResource(resources,R.drawable.sundial01_left,options);
	   	
    	storage32 = new int[bitmap.getWidth()*bitmap.getHeight()];
    	storage8 = new byte[bitmap.getWidth()*bitmap.getHeight()*4];
	}
	
	@Override
	public void run() {
	  	if( bitmap == null ) {
	  		finished();
    		return;
    	}
    	
	  	publishText(" Input size = "+bitmap.getWidth()+" x "+bitmap.getHeight()+"\n");
	  	publishText("\n");
    	
	  	highLevelU8();
    	benchmarkU8();
    	benchmarkF32();
    	benchmarMultiU8();
    	benchmarMultiF32();

    	finished();	
	}

	private void highLevelU8() {
    	final ImageUInt8 gray = new ImageUInt8(bitmap.getWidth(),bitmap.getHeight());
    	    	
    	benchmark("User bToG null U8",new EvalPA() {
			public void _process() {ConvertBitmap.bitmapToGray(bitmap,gray,null);}});
    	benchmark("User bToG data U8",new EvalPA() {
			public void _process() {ConvertBitmap.bitmapToGray(bitmap,gray,storage8);}});
	}
	
	private void benchmarkU8() {
    	final ImageUInt8 gray = new ImageUInt8(bitmap.getWidth(),bitmap.getHeight());
		
    	bitmap.copyPixelsToBuffer(IntBuffer.wrap(storage32));
    	bitmap.copyPixelsToBuffer(ByteBuffer.wrap(storage8));
    	    	
    	benchmark("Array32 8888 to U8",new EvalPA() {
			public void _process() {ImplConvertBitmap.arrayToGray(storage32, Bitmap.Config.ARGB_8888,gray);}});
    	
    	benchmark("Array8 8888 to U8",new EvalPA() {
			public void _process() {ImplConvertBitmap.arrayToGray(storage8, Bitmap.Config.ARGB_8888,gray);}});
    	
    	benchmark("RGB 8888 to U8",new EvalPA() {
			public void _process() {ImplConvertBitmap.bitmapToGrayRGB(bitmap, gray);}});
    	
    	bitmapOut = Bitmap.createBitmap(gray.width, gray.height, Bitmap.Config.ARGB_8888);
    	
    	benchmark("Array8 U8 to 8888",new EvalPA() {
			public void _process() {ImplConvertBitmap.grayToArray(gray, storage8, Bitmap.Config.ARGB_8888);}});
    	
    	benchmark("RGB U8 to 8888",new EvalPA() {
			public void _process() {ImplConvertBitmap.grayToBitmapRGB(gray, bitmapOut);}});
    	
    	bitmapOut = Bitmap.createBitmap(gray.width, gray.height, Bitmap.Config.RGB_565);

    	benchmark("Array8 U8 to 565",new EvalPA() {
			public void _process() {ImplConvertBitmap.grayToArray(gray, storage8, Bitmap.Config.RGB_565 );}});
	}
	
	private void benchmarkF32() {
    	final ImageFloat32 gray = new ImageFloat32(bitmap.getWidth(),bitmap.getHeight());
		
    	bitmap.copyPixelsToBuffer(IntBuffer.wrap(storage32));
    	bitmap.copyPixelsToBuffer(ByteBuffer.wrap(storage8));	    	
    	
    	benchmark("Array8 8888 to F32",new EvalPA() {
			public void _process() {ImplConvertBitmap.arrayToGray(storage8, Bitmap.Config.ARGB_8888,gray);}});
    	
    	benchmark("RGB 8888 to F32",new EvalPA() {
			public void _process() {ImplConvertBitmap.bitmapToGrayRGB(bitmap, gray);}});
    	
    	bitmapOut = Bitmap.createBitmap(gray.width, gray.height, Bitmap.Config.ARGB_8888);
    	
    	benchmark("Array8 F32 to 8888",new EvalPA() {
			public void _process() {ImplConvertBitmap.grayToArray(gray, storage8, Bitmap.Config.ARGB_8888);}});
    	
    	benchmark("RGB F32 to 8888",new EvalPA() {
			public void _process() {ImplConvertBitmap.grayToBitmapRGB(gray, bitmapOut);}});
    	
    	bitmapOut = Bitmap.createBitmap(gray.width, gray.height, Bitmap.Config.RGB_565);

    	benchmark("Array8 F32 to 565",new EvalPA() {
			public void _process() {ImplConvertBitmap.grayToArray(gray, storage8, Bitmap.Config.RGB_565 );}});
	}
	
	private void benchmarMultiU8() {
    	final MultiSpectral<ImageUInt8> color = new MultiSpectral<ImageUInt8>(ImageUInt8.class,bitmap.getWidth(),bitmap.getHeight(),4);
		
    	benchmark("Array32 8888 to MU8",new EvalPA() {
			public void _process() {ImplConvertBitmap.arrayToMulti_U8(storage32, Bitmap.Config.ARGB_8888,color);}});
    	
    	benchmark("Array8 8888 to MU8",new EvalPA() {
			public void _process() {ImplConvertBitmap.arrayToMulti_U8(storage8, Bitmap.Config.ARGB_8888,color);}});
    	
    	benchmark("RGB 8888 to MU8",new EvalPA() {
			public void _process() {ImplConvertBitmap.bitmapToMultiRGB_U8(bitmap, color);}});
    	
    	bitmapOut = Bitmap.createBitmap(color.width, color.height, Bitmap.Config.ARGB_8888);
    	
    	benchmark("Array8 MU8 to 8888",new EvalPA() {
			public void _process() {ImplConvertBitmap.multiToArray_U8(color,storage8, Bitmap.Config.ARGB_8888);}});
    	
    	benchmark("RGB MU8 to 8888",new EvalPA() {
			public void _process() {ImplConvertBitmap.multiToBitmapRGB_U8(color, bitmapOut);}});
    	
    	bitmapOut = Bitmap.createBitmap(color.width, color.height, Bitmap.Config.RGB_565);

    	benchmark("Array8 MU8 to 565",new EvalPA() {
			public void _process() {ImplConvertBitmap.multiToArray_U8(color, storage8, Bitmap.Config.RGB_565 );}});
	}
	
	private void benchmarMultiF32() {
    	final MultiSpectral<ImageFloat32> color = new MultiSpectral<ImageFloat32>(ImageFloat32.class,bitmap.getWidth(),bitmap.getHeight(),4);
		
    	benchmark("Array32 8888 to MF32",new EvalPA() {
			public void _process() {ImplConvertBitmap.arrayToMulti_F32(storage32, Bitmap.Config.ARGB_8888,color);}});
    	
    	benchmark("Array8 8888 to MF32",new EvalPA() {
			public void _process() {ImplConvertBitmap.arrayToMulti_F32(storage8, Bitmap.Config.ARGB_8888,color);}});
    	
    	benchmark("RGB 8888 to MF32",new EvalPA() {
			public void _process() {ImplConvertBitmap.bitmapToMultiRGB_F32(bitmap, color);}});
    	
    	bitmapOut = Bitmap.createBitmap(color.width, color.height, Bitmap.Config.ARGB_8888);
    	
    	benchmark("Array8 MF32 to 8888",new EvalPA() {
			public void _process() {ImplConvertBitmap.multiToArray_F32(color,storage8, Bitmap.Config.ARGB_8888);}});
    	
    	benchmark("RGB MF32 to 8888",new EvalPA() {
			public void _process() {ImplConvertBitmap.multiToBitmapRGB_F32(color, bitmapOut);}});
    	
    	bitmapOut = Bitmap.createBitmap(color.width, color.height, Bitmap.Config.RGB_565);

    	benchmark("Array8 MF32 to 565",new EvalPA() {
			public void _process() {ImplConvertBitmap.multiToArray_F32(color, storage8, Bitmap.Config.RGB_565 );}});
	}

	@Override
	public String getDescription() {
		return "Converts between Android (Bitmap) and BoofCV image types using different techniques.\n"+
		        "\n"+
		        "Android Bitmap accessor technique\n"+
		        "  RGB = Using Bitmap.getPixel() and Bitmap.setPixel()\n"+
		        "  Array8 = byte array using Bitmap.copyPixelsToBuffer().\n"+
		        "  Array32 = integer array using Bitmap.copyPixelsToBuffer().\n"+
		        "\n"+
		        "BoofCV Image Type\n"+
		        "  U8 = ImageUInt8\n"+
		        "  F32 = ImageFloat32\n"+
		        "  MU8 = MultSpectral<ImageUInt8>\n"+
		        "  MF32 = MultiSpectral<ImageFloat32>";
	}

}
