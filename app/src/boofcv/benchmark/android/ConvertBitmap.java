package boofcv.benchmark.android;

import android.graphics.Bitmap;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.ImageUInt8;

/**
 * Functions for converting Android Bitmap images into BoofCV formats.
 * 
 * @author Peter Abeles
 *
 */
public class ConvertBitmap {
	
	public static <T extends ImageSingleBand>T bitmapToGray( Bitmap input , T output , Class<T> imageType ) {
		if( imageType == ImageFloat32.class )
			return (T)bitmapToGray(input,(ImageFloat32)output);
		else if( imageType == ImageUInt8.class )
			return (T)bitmapToGray(input,(ImageUInt8)output);
		else
			throw new RuntimeException("Unsupport Boofcv Image Type");
		
	}
	
	public static ImageUInt8 bitmapToGray( Bitmap input , ImageUInt8 output ) {
		if( output == null ) {
			output = new ImageUInt8( input.getWidth() , input.getHeight() );
		} else if( output.getWidth() != input.getWidth() || output.getHeight() != input.getHeight() ) {
			throw new IllegalArgumentException("Image shapes are not the same");
		}
		
		ImplConvertBitmap.bitmapToGrayReflection(input, output);
		
		return output;
	}
	
	public static ImageFloat32 bitmapToGray( Bitmap input , ImageFloat32 output ) {
		if( output == null ) {
			output = new ImageFloat32( input.getWidth() , input.getHeight() );
		} else if( output.getWidth() != input.getWidth() || output.getHeight() != input.getHeight() ) {
			throw new IllegalArgumentException("Image shapes are not the same");
		}
		
		ImplConvertBitmap.bitmapToGrayReflection(input, output);
		
		return output;
	}
	
	public static void grayToBitmap( ImageUInt8 input , Bitmap output ) {
		if( output.getWidth() != input.getWidth() || output.getHeight() != input.getHeight() ) {
			throw new IllegalArgumentException("Image shapes are not the same");
		}
		
		ImplConvertBitmap.grayToBitmapReflection(input, output);
	}
	
	public static void grayToBitmap( ImageFloat32 input , Bitmap output ) {
		if( output.getWidth() != input.getWidth() || output.getHeight() != input.getHeight() ) {
			throw new IllegalArgumentException("Image shapes are not the same");
		}
		
		ImplConvertBitmap.grayToBitmapReflection(input, output);
	}
	
	public static Bitmap grayToBitmap( ImageUInt8 input , Bitmap.Config config ) {
		Bitmap output = Bitmap.createBitmap(input.width, input.height, config);
		
		ImplConvertBitmap.grayToBitmapReflection(input, output);
		
		return output;
	}
	
	public static Bitmap grayToBitmap( ImageFloat32 input , Bitmap.Config config ) {
		Bitmap output = Bitmap.createBitmap(input.width, input.height, config);
		
		ImplConvertBitmap.grayToBitmapReflection(input, output);
		
		return output;
	}
}
