package boofcv.benchmark.android;

import georegression.struct.affine.Affine2D_F32;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import boofcv.abst.filter.blur.BlurStorageFilter;
import boofcv.alg.distort.DistortImageOps;
import boofcv.alg.distort.PixelTransformAffine_F32;
import boofcv.alg.filter.derivative.GImageDerivativeOps;
import boofcv.alg.interpolate.InterpolatePixel;
import boofcv.core.image.GeneralizedImageOps;
import boofcv.core.image.border.BorderType;
import boofcv.factory.filter.blur.FactoryBlurFilter;
import boofcv.factory.interpolate.FactoryInterpolation;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.ImageUInt8;

public class LowLevelBenchmark extends BenchmarkThread {

	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "LowLevel";
	
	Bitmap bitmap;
	BlurStorageFilter filter;
	InterpolatePixel interp;
	
	public LowLevelBenchmark() {
		super(NAME);
	}
	
	@Override
	public void configure( Resources resources) {
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
    	
	  	// TODO smaller image 320x240
	  	
	  	publishText(" Input size = "+bitmap.getWidth()+" x "+bitmap.getHeight()+"\n");
	  	publishText(" Interpolate size = "+(bitmap.getWidth()/2)+" x "+(bitmap.getHeight()/2)+"\n");
	  	publishText("\n");
	  	
    	benchmark(ConvertBitmap.bitmapToGray(bitmap, (ImageUInt8)null, null),"U8");
    	benchmark(ConvertBitmap.bitmapToGray(bitmap, (ImageFloat32)null, null),"F32");

    	finished();	
	}
	
	private <T extends ImageSingleBand> void benchmark( final T image , String imageName ) {
		benchmarkBlur(image,imageName);
		benchmarkDerivative(image,imageName);
		benchmarkInterpolate(image,imageName);
	}
	
	private <T extends ImageSingleBand> void benchmarkBlur( final T image , String imageName ) {
		Class<T> imageType = (Class)image.getClass();
		
		final T output = GeneralizedImageOps.createSingleBand(imageType, image.width, image.height);
		
    	filter = FactoryBlurFilter.mean(imageType, 1);
    	benchmark("Blur Mean 3 "+imageName,new EvalPA() {
			public void _process() {filter.process(image, output);}});

    	filter = FactoryBlurFilter.mean(imageType, 2);
    	benchmark("Blur Mean 5 "+imageName,new EvalPA() {
			public void _process() {filter.process(image, output);}});
    	
    	filter = FactoryBlurFilter.mean(imageType, 3);
    	benchmark("Blur Mean 7 "+imageName,new EvalPA() {
			public void _process() {filter.process(image, output);}});
    	
		filter = FactoryBlurFilter.gaussian(imageType, -1, 1);
    	benchmark("Blur Gaussian 3 "+imageName,new EvalPA() {
			public void _process() {filter.process(image, output);}});
    	
    	filter = FactoryBlurFilter.gaussian(imageType, -1, 2);
    	benchmark("Blur Gaussian 5 "+imageName,new EvalPA() {
			public void _process() {filter.process(image, output);}});
    	
    	filter = FactoryBlurFilter.gaussian(imageType, -1, 3);
    	benchmark("Blur Gaussian 7 "+imageName,new EvalPA() {
			public void _process() {filter.process(image, output);}});
	}
	
	private <T extends ImageSingleBand> void benchmarkDerivative( final T image , String imageName ) {
		Class<T> imageType = (Class)image.getClass();
		
    	Class derivType = GImageDerivativeOps.getDerivativeType(imageType);
    	final ImageSingleBand derivX = GeneralizedImageOps.createSingleBand(derivType, image.width, image.height);
    	final ImageSingleBand derivY = GeneralizedImageOps.createSingleBand(derivType, image.width, image.height);
    	
    	benchmark("Derivative Sobel "+imageName,new EvalPA() {
			public void _process() {GImageDerivativeOps.sobel(image, derivX, derivY, BorderType.EXTENDED);}});
    	
    	benchmark("Derivative Prewitt "+imageName,new EvalPA() {
			public void _process() {GImageDerivativeOps.prewitt(image, derivX, derivY, BorderType.EXTENDED);}});
	}
	
	private <T extends ImageSingleBand> void benchmarkInterpolate( final T image , String imageName ) {
		Class<T> imageType = (Class)image.getClass();
		
		final T image2 = (T)image.subimage(0, 0, image.width/2, image.height/2);
		
		final ImageSingleBand output = GeneralizedImageOps.createSingleBand(imageType, image2.width, image2.height);
		final PixelTransformAffine_F32 model = new PixelTransformAffine_F32();
		model.set(new Affine2D_F32(1,0,0,1,0.5f,0.75f));
		
		interp = FactoryInterpolation.nearestNeighborPixel(imageType);
    	benchmark("Interp. NN "+imageName,new EvalPA() {
			public void _process() {DistortImageOps.distortSingle(image2,output,model,null,interp);}});
    	
    	interp = FactoryInterpolation.bilinearPixel(imageType);
    	benchmark("Interp. Bilinear "+imageName,new EvalPA() {
			public void _process() {DistortImageOps.distortSingle(image2,output,model,null,interp);}});
    	
    	interp = FactoryInterpolation.bicubic(-0.5f, 0, 255, imageType);
    	benchmark("Interp. Bicubic "+imageName,new EvalPA() {
			public void _process() {DistortImageOps.distortSingle(image2,output,model,null,interp);}});
    	
    	interp = FactoryInterpolation.polynomial(5, 0, 255, imageType);
    	benchmark("Interp. Poly5 "+imageName,new EvalPA() {
			public void _process() {DistortImageOps.distortSingle(image2,output,model,null,interp);}});
	}
	
	@Override
	public String getDescription() {
		return "Low level image processing routines: image blur, convolution, derivative, and interpolation.\n"+
		        "\n"+
		        "BoofCV Image Type\n"+
		        "  U8 = ImageUInt8\n"+
		        "  F32 = ImageFloat32\n"+
		        "  MU8 = MultSpectral<ImageUInt8>\n"+
		        "  MF32 = MultiSpectral<ImageFloat32>";
	}
}
