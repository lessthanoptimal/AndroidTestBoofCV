package boofcv.benchmark.android;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;

/**
 * Functions for converting Android Bitmap images into BoofCV formats.
 * 
 * @author Peter Abeles
 *
 */
public class ConvertBitmap {
	
	public static <T extends ImageSingleBand>T bitmapToGray( Bitmap input , T output , Class<T> imageType , byte[] storage) {
		if( imageType == ImageFloat32.class )
			return (T)bitmapToGray(input,(ImageFloat32)output,storage);
		else if( imageType == ImageUInt8.class )
			return (T)bitmapToGray(input,(ImageUInt8)output,storage);
		else
			throw new RuntimeException("Unsupport Boofcv Image Type");
		
	}
	
	public static ImageUInt8 bitmapToGray( Bitmap input , ImageUInt8 output , byte[] storage ) {
		if( output == null ) {
			output = new ImageUInt8( input.getWidth() , input.getHeight() );
		} else if( output.getWidth() != input.getWidth() || output.getHeight() != input.getHeight() ) {
			throw new IllegalArgumentException("Image shapes are not the same");
		}
		
		int byteCount = input.getConfig() == Bitmap.Config.ARGB_8888 ? 4 : 2;
		
		if( storage == null )
			storage = new byte[ input.getWidth()*input.getHeight()*byteCount ];
		input.copyPixelsToBuffer(ByteBuffer.wrap(storage));
		
		ImplConvertBitmap.arrayToGray(storage, input.getConfig(), output);
		
		return output;
	}
	
	public static ImageFloat32 bitmapToGray( Bitmap input , ImageFloat32 output , byte[] storage) {
		if( output == null ) {
			output = new ImageFloat32( input.getWidth() , input.getHeight() );
		} else if( output.getWidth() != input.getWidth() || output.getHeight() != input.getHeight() ) {
			throw new IllegalArgumentException("Image shapes are not the same");
		}
		
		int byteCount = input.getConfig() == Bitmap.Config.ARGB_8888 ? 4 : 2;
		
		if( storage == null )
			storage = new byte[ input.getWidth()*input.getHeight()*byteCount ];
		input.copyPixelsToBuffer(ByteBuffer.wrap(storage));
		
		ImplConvertBitmap.arrayToGray(storage, input.getConfig(), output);
		
		return output;
	}
	
	public static <T extends ImageSingleBand>
	MultiSpectral<T> bitmapToMS( Bitmap input , MultiSpectral<T> output , Class<T> type , byte[] storage ) {
		if( output == null ) {
			output = new MultiSpectral<T>( type , input.getWidth() , input.getHeight() , 4 );
		} else if( output.getWidth() != input.getWidth() || output.getHeight() != input.getHeight() ) {
			throw new IllegalArgumentException("Image shapes are not the same");
		}
		
		int byteCount = input.getConfig() == Bitmap.Config.ARGB_8888 ? 4 : 2;
		
		if( storage == null )
			storage = new byte[ input.getWidth()*input.getHeight()*byteCount ];
		input.copyPixelsToBuffer(ByteBuffer.wrap(storage));
		
		if( type == ImageUInt8.class )
			ImplConvertBitmap.arrayToMulti_U8(storage, input.getConfig(), (MultiSpectral)output);
		else if( type == ImageFloat32.class )
			ImplConvertBitmap.arrayToMulti_F32(storage, input.getConfig(), (MultiSpectral)output);
		else
			throw new IllegalArgumentException("Unsupported BoofCV Type");

		return output;
	}
	
	public static void grayToBitmap( ImageSingleBand input , Bitmap output , byte[] storage) {
		if( input instanceof ImageUInt8 )
			grayToBitmap((ImageUInt8)input,output,storage);
		else if( input instanceof ImageFloat32 )
			grayToBitmap((ImageFloat32)input,output,storage);
		else
			throw new IllegalArgumentException("Unsupported BoofCV Type");
	}
	
	public static void grayToBitmap( ImageUInt8 input , Bitmap output , byte[] storage) {
		if( output.getWidth() != input.getWidth() || output.getHeight() != input.getHeight() ) {
			throw new IllegalArgumentException("Image shapes are not the same");
		}
		
		int byteCount = output.getConfig() == Bitmap.Config.ARGB_8888 ? 4 : 2;
		
		if( storage == null )
			storage = new byte[ input.getWidth()*input.getHeight()*byteCount ];
		ImplConvertBitmap.grayToArray(input, storage,output.getConfig());
		output.copyPixelsFromBuffer(ByteBuffer.wrap(storage));
	}
	
	public static void grayToBitmap( ImageFloat32 input , Bitmap output , byte[] storage ) {
		if( output.getWidth() != input.getWidth() || output.getHeight() != input.getHeight() ) {
			throw new IllegalArgumentException("Image shapes are not the same");
		}
		
		int byteCount = output.getConfig() == Bitmap.Config.ARGB_8888 ? 4 : 2;
		
		if( storage == null )
			storage = new byte[ input.getWidth()*input.getHeight()*byteCount ];
		ImplConvertBitmap.grayToArray(input, storage,output.getConfig());
		output.copyPixelsFromBuffer(ByteBuffer.wrap(storage));
	}
	
	public static <T extends ImageSingleBand>
	void multiToBitmap(  MultiSpectral<T> input , Bitmap output , byte[] storage ) {
		if( output.getWidth() != input.getWidth() || output.getHeight() != input.getHeight() ) {
			throw new IllegalArgumentException("Image shapes are not the same");
		}
		
		int byteCount = output.getConfig() == Bitmap.Config.ARGB_8888 ? 4 : 2;
		
		if( storage == null )
			storage = new byte[ input.getWidth()*input.getHeight()*byteCount ];
		if( input.getType() == ImageUInt8.class )
			ImplConvertBitmap.multiToArray_U8((MultiSpectral)input, storage,output.getConfig());
		else if( input.getType() == ImageFloat32.class )
			ImplConvertBitmap.multiToArray_F32((MultiSpectral)input, storage,output.getConfig());
		else
			throw new IllegalArgumentException("Unsupported BoofCV Type");
		output.copyPixelsFromBuffer(ByteBuffer.wrap(storage));
	}
	
	public static Bitmap grayToBitmap( ImageUInt8 input , Bitmap.Config config ) {
		Bitmap output = Bitmap.createBitmap(input.width, input.height, config);
		
		grayToBitmap(input,output,null);
		
		return output;
	}
	
	public static Bitmap grayToBitmap( ImageFloat32 input , Bitmap.Config config ) {
		Bitmap output = Bitmap.createBitmap(input.width, input.height, config);
		
		grayToBitmap(input,output,null);
		
		return output;
	}
}
