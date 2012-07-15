package boofcv.benchmark.android;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.misc.GPixelMath;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSInt32;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.ImageUInt8;

public class BinaryOpsBenchmark extends BenchmarkThread {

	private static final long serialVersionUID = 1L;
	
	Bitmap bitmap;
	
	@Override
	public void configure( Resources resources ) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		
	   	bitmap = BitmapFactory.decodeResource(resources,R.drawable.simple_objects,options);
	}
	
	@Override
	public void run() {
	  	if( bitmap == null ) {
	  		finished();
    		return;
    	}
	  	
	  	publishText(" Input size = "+bitmap.getWidth()+" x "+bitmap.getHeight()+"\n");
	  	publishText("\n");
	  	
	  	ImageUInt8 binary = new ImageUInt8(bitmap.getWidth(),bitmap.getHeight());
	  	benchmarkThreshold(ImageUInt8.class,"U8",binary);
	  	benchmarkThreshold(ImageFloat32.class,"F32",binary);
	  	
	  	benchmark(binary);
	  	
	  	finished();
	}
	
	private <T extends ImageSingleBand> void benchmarkThreshold( Class<T> imageType , String imageName , 
			final ImageUInt8 binary ) {
		
		final T image = ConvertBitmap.bitmapToGray(bitmap, null, imageType);
		
		// the mean pixel value is often a reasonable threshold when creating a binary image
		final double mean = GPixelMath.sum(image)/(image.width*image.height);
				
		benchmark("Threshold "+imageName,new EvalPA() {
				public void _process() {GThresholdImageOps.threshold(image,binary,mean,true);}});
		
	}
	
	private <T extends ImageSingleBand> void benchmark( final ImageUInt8 binary ) {
		
		final ImageUInt8 output = new ImageUInt8(binary.width,binary.height);

    	benchmark("Erode4",new EvalPA() {
			public void _process() {BinaryImageOps.erode4(binary,output);}});
    	benchmark("Erode8",new EvalPA() {
			public void _process() {BinaryImageOps.erode8(binary,output);}});
       	benchmark("Dilate4",new EvalPA() {
    			public void _process() {BinaryImageOps.dilate4(binary,output);}});
       	benchmark("Dilate8",new EvalPA() {
    			public void _process() {BinaryImageOps.dilate8(binary,output);}});
    	benchmark("Edge4",new EvalPA() {
			public void _process() {BinaryImageOps.edge4(binary,output);}});
    	benchmark("Edge8",new EvalPA() {
			public void _process() {BinaryImageOps.edge8(binary,output);}});
    	
    	benchmark("Logic And",new EvalPA() {
			public void _process() {BinaryImageOps.logicAnd(binary,binary,output);}});
    	benchmark("Logic Or",new EvalPA() {
			public void _process() {BinaryImageOps.logicOr(binary,binary,output);}});
    	benchmark("Logic Xor",new EvalPA() {
			public void _process() {BinaryImageOps.logicXor(binary,binary,output);}});

    	final ImageSInt32 labeled = new ImageSInt32(binary.width,binary.height);
    	
    	benchmark("Label Blobs4",new EvalPA() {
			public void _process() {BinaryImageOps.labelBlobs4(binary,labeled);}});
    	benchmark("Label Blobs8",new EvalPA() {
			public void _process() {BinaryImageOps.labelBlobs8(binary,labeled);}});
	}
	
	@Override
	public String getDescription() {
		return "Computes a binary image by thresholding. The binary operators are then applied " +
				"to the resulting image.  The number next to an operation refers to the connectivity rule.\n"+
		        "\n"+
		        "BoofCV Image Type\n"+
		        "  U8 = ImageUInt8\n"+
		        "  F32 = ImageFloat32\n"+
		        "  MU8 = MultSpectral<ImageUInt8>\n"+
		        "  MF32 = MultiSpectral<ImageFloat32>";
	}

}
