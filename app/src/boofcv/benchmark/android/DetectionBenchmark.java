package boofcv.benchmark.android;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;
import boofcv.abst.feature.detect.edge.DetectEdgeContour;
import boofcv.abst.feature.detect.extract.GeneralFeatureDetector;
import boofcv.abst.feature.detect.interest.InterestPointDetector;
import boofcv.abst.feature.detect.line.DetectLineHoughPolar;
import boofcv.alg.filter.derivative.GImageDerivativeOps;
import boofcv.factory.feature.detect.edge.FactoryDetectEdgeContour;
import boofcv.factory.feature.detect.interest.FactoryDetectPoint;
import boofcv.factory.feature.detect.interest.FactoryInterestPoint;
import boofcv.factory.feature.detect.line.FactoryDetectLineAlgs;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.ImageUInt8;

public class DetectionBenchmark extends BenchmarkThread {

	private static final long serialVersionUID = 1L;
	
	Bitmap bitmap;
	Bitmap bitmapLines;
	InterestPointDetector detector;
	DetectLineHoughPolar detectorLine;
	
	@Override
	public void configure( TextView view, Resources resources, Listener listener ) {
		super.configure(view,resources,listener);
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		
	   	bitmap = BitmapFactory.decodeResource(resources,R.drawable.sundial01_left,options);
	   	bitmapLines = BitmapFactory.decodeResource(resources,R.drawable.lines_indoors,options);
	}
	
	@Override
	public void run() {
	  	if( bitmap == null ) {
	  		finished();
    		return;
    	}
    	
	  	// TODO smaller image 320x240
	  	
	  	publishText(" Point input size = "+bitmap.getWidth()+" x "+bitmap.getHeight()+"\n");
	  	publishText(" Line input size = "+bitmapLines.getWidth()+" x "+bitmapLines.getHeight()+"\n");
	  	publishText("\n");
	  	
    	benchmark(ImageUInt8.class,"U8");
    	benchmark(ImageFloat32.class,"F32");

    	publishText("Done\n");
    	finished();	
	}
	
	private <T extends ImageSingleBand> void benchmark( Class<T> imageType , String imageName ) {
		
		T imagePoint = ConvertBitmap.bitmapToGray(bitmap, null, imageType);
		benchmarkPoints(imagePoint,imageName);
		imagePoint = null;
		
		T imageLine = ConvertBitmap.bitmapToGray(bitmapLines, null, imageType);
		benchmarkLines(imageLine,imageName);
		benchmarkContour(imageLine,imageName);
	}
	
	private <T extends ImageSingleBand> void benchmarkPoints( final T image , String imageName ) {
		Class<T> imageType = (Class)image.getClass();
		Class derivType = GImageDerivativeOps.getDerivativeType(imageType);
		
		int maxFeatures = 300;
		int r = 2;
		
		GeneralFeatureDetector corner = FactoryDetectPoint.createFast(r,45,maxFeatures,imageType);
		detector = FactoryInterestPoint.wrapCorner(corner, imageType, derivType);
    	benchmark("Fast Corner "+imageName,new EvalPA() {
			public void _process() {detector.detect(image);}});
    	
		corner = FactoryDetectPoint.createHarris(r,false,1,maxFeatures,derivType);
		detector = FactoryInterestPoint.wrapCorner(corner, imageType, derivType);
    	benchmark("Harris "+imageName,new EvalPA() {
			public void _process() {detector.detect(image);}});
    	
		corner = FactoryDetectPoint.createShiTomasi(r,false,1,maxFeatures,derivType);
		detector = FactoryInterestPoint.wrapCorner(corner, imageType, derivType);
    	benchmark("Shi-Tomasi "+imageName,new EvalPA() {
			public void _process() {detector.detect(image);}});
		
		detector = FactoryInterestPoint.fastHessian(10, 2, 120, 2, 9, 4, 4);
    	benchmark("Fast Hessian "+imageName,new EvalPA() {
			public void _process() {detector.detect(image);}});
    	
    	detector = null;
	}
	
	private <T extends ImageSingleBand> void benchmarkLines( final T image , String imageName ) {
		Class<T> imageType = (Class)image.getClass();
		Class derivType = GImageDerivativeOps.getDerivativeType(imageType);
		
		int maxLines = 10;
		float edgeThreshold = 25;
		
		detectorLine = FactoryDetectLineAlgs.houghPolar(3, 30, 2, Math.PI / 180,
				edgeThreshold, maxLines, imageType, derivType);
		
		benchmark("Hough Polar "+imageName,new EvalPA() {
			public void _process() {detectorLine.detect(image);}});
		detectorLine = null;
		
//		The line segement detector currently only supports floating point images
//      Once the algorithm is better create an integer version.
		
//		final DetectLineSegmentsGridRansac detectorLS = 
//				FactoryDetectLineAlgs.lineRansac(40, 30, 2.36, true, imageType, derivType);
//		
//		benchmark("Line Seg. Grid "+imageName,new EvalPA() {
//			public void _process() {detectorLS.detect(image);}});	
	}
	
	private <T extends ImageSingleBand> void benchmarkContour( final T image , String imageName ) {
		Class<T> imageType = (Class)image.getClass();
		Class derivType = GImageDerivativeOps.getDerivativeType(imageType);
		
		final DetectEdgeContour<T> canny =
				FactoryDetectEdgeContour.canny(30,200,false,imageType,derivType);
		
		benchmark("Canny Edge "+imageName,new EvalPA() {
			public void _process() {canny.process(image);}});
	}

	
	@Override
	public String getDescription() {
		return "Runs different types of feature detectors (e.g. point, line, edge) on an image." +
				"Some of these operations can be slow depending on your hardware.\n"+
		        "\n"+
		        "BoofCV Image Type\n"+
		        "  U8 = ImageUInt8\n"+
		        "  F32 = ImageFloat32\n"+
		        "  MU8 = MultSpectral<ImageUInt8>\n"+
		        "  MF32 = MultiSpectral<ImageFloat32>";
	}
}
