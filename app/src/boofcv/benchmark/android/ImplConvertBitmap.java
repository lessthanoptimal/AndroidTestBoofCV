package boofcv.benchmark.android;

import java.lang.reflect.Field;
import java.nio.IntBuffer;

import android.graphics.Bitmap;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;

/**
 * Low level implementations of Bitmap conversion routines. Contains functions
 * which are not being used for benchmarking purposes.
 * 
 * When converting into 565 format a lookup table is used.  The equations used to compute
 * the table round instead of flooring to minimize error.  I believe that this is what the Android
 * library does too, without looking at the code.
 * 
 * @author Peter Abeles
 * 
 */
public class ImplConvertBitmap {

	// used to grab a private field from Bitmap
	private static Field field;
	// storages values used to convert to 565 format
	private static int table5[] = new int[ 256 ];
	private static int table6[] = new int[ 256 ];
	
	static {
		try {
			field = Bitmap.class.getDeclaredField("mBuffer");
			field.setAccessible(true);
		} catch (NoSuchFieldException e) {
			// well this would be bad
		}
		
		for( int i = 0; i < table5.length; i++ ) {
			// minimize error by rounding instead of flooring
			table5[i] = (int)Math.round(i*0x1F/255.0);
			table6[i] = (int)Math.round(i*0x3F/255.0);
		}

	}

	public static void bitmapToGrayRGB(Bitmap input, ImageUInt8 output) {
		final int h = output.height;
		final int w = output.width;

		for (int y = 0; y < h; y++) {
			int index = output.startIndex + y * output.stride;
			for (int x = 0; x < w; x++) {
				int rgb = input.getPixel(x, y);

				int value = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
				output.data[index++] = (byte) value;
			}
		}
	}
	
	public static void bitmapToGrayRGB(Bitmap input, ImageFloat32 output) {
		final int h = output.height;
		final int w = output.width;

		for (int y = 0; y < h; y++) {
			int index = output.startIndex + y * output.stride;
			for (int x = 0; x < w; x++) {
				int rgb = input.getPixel(x, y);

				int value = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
				output.data[index++] = value;
			}
		}
	}
	
	public static void bitmapToMultiRGB_U8(Bitmap input, MultiSpectral<ImageUInt8> output) {
		final int h = output.height;
		final int w = output.width;
		
		ImageUInt8 R = output.getBand(0);
		ImageUInt8 G = output.getBand(1);
		ImageUInt8 B = output.getBand(2);
		ImageUInt8 A = output.getBand(3);

		for (int y = 0; y < h; y++) {
			int index = output.startIndex + y * output.stride;
			for (int x = 0; x < w; x++, index++ ) {
				int rgb = input.getPixel(x, y);

				A.data[index] = (byte)(rgb >> 24);
				R.data[index] = (byte)((rgb >> 16) & 0xFF);
				G.data[index] = (byte)((rgb >> 8) & 0xFF);
				B.data[index] = (byte)(rgb & 0xFF);
			}
		}
	}
	
	public static void bitmapToMultiRGB_F32(Bitmap input, MultiSpectral<ImageFloat32> output) {
		final int h = output.height;
		final int w = output.width;
		
		ImageFloat32 R = output.getBand(0);
		ImageFloat32 G = output.getBand(1);
		ImageFloat32 B = output.getBand(2);
		ImageFloat32 A = output.getBand(3);

		for (int y = 0; y < h; y++) {
			int index = output.startIndex + y * output.stride;
			for (int x = 0; x < w; x++, index++ ) {
				int rgb = input.getPixel(x, y);

				A.data[index] = (rgb >> 24) & 0xFF;
				R.data[index] = (rgb >> 16) & 0xFF;
				G.data[index] = (rgb >> 8) & 0xFF;
				B.data[index] = rgb & 0xFF;
			}
		}
	}

	public static void grayToBitmapRGB(ImageUInt8 input, Bitmap output) {
		final int h = input.height;
		final int w = input.width;

		for (int y = 0; y < h; y++) {
			int index = input.startIndex + y * input.stride;
			for (int x = 0; x < w; x++) {
				int gray = input.data[index++] & 0xFF;

				output.setPixel(x, y, 0xFF << 24 | gray << 16 | gray << 8
						| gray);
			}
		}
	}
	
	public static void grayToBitmapRGB(ImageFloat32 input, Bitmap output) {
		final int h = input.height;
		final int w = input.width;

		for (int y = 0; y < h; y++) {
			int index = input.startIndex + y * input.stride;
			for (int x = 0; x < w; x++) {
				int gray = (int)input.data[index++];

				output.setPixel(x, y, 0xFF << 24 | gray << 16 | gray << 8
						| gray);
			}
		}
	}
	
	public static void multiToBitmapRGB_U8(MultiSpectral<ImageUInt8> input, Bitmap output) {
		final int h = input.height;
		final int w = input.width;

		ImageUInt8 R = input.getBand(0);
		ImageUInt8 G = input.getBand(1);
		ImageUInt8 B = input.getBand(2);
		ImageUInt8 A = input.getBand(3);
		
		for (int y = 0; y < h; y++) {
			int index = input.startIndex + y * input.stride;
			for (int x = 0; x < w; x++,index++) {

				output.setPixel(x, y, B.data[index] << 24 | G.data[index] << 16 | R.data[index] << 8
						| A.data[index]);
			}
		}
	}

	public static void multiToBitmapRGB_F32(MultiSpectral<ImageFloat32> input, Bitmap output) {
		final int h = input.height;
		final int w = input.width;

		ImageFloat32 R = input.getBand(0);
		ImageFloat32 G = input.getBand(1);
		ImageFloat32 B = input.getBand(2);
		ImageFloat32 A = input.getBand(3);
		
		for (int y = 0; y < h; y++) {
			int index = input.startIndex + y * input.stride;
			for (int x = 0; x < w; x++,index++) {

				int r = (int)R.data[index];
				int g = (int)G.data[index];
				int b = (int)B.data[index];
				int a = (int)A.data[index];
	
				output.setPixel(x, y, b << 24 | g << 16 | r << 8 | a );
			}
		}
	}
	
	public static void bitmapToGrayArray(Bitmap input, ImageUInt8 output,
			int storage[]) {
		final int h = output.height;
		final int w = output.width;

		input.copyPixelsToBuffer(IntBuffer.wrap(storage));
		int indexSrc = 0;
		
		switch (input.getConfig()) {
		case ARGB_8888:
			for (int y = 0; y < h; y++) {
				int indexDst = output.startIndex + y * output.stride;
				int end = indexDst + w;
				// for (int x = 0; x < w; x++) {
				while (indexDst < end) {
					int rgb = storage[indexSrc++];

					int value = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
					output.data[indexDst++] = (byte) value;
				}
			}
			break;

		default:
			throw new RuntimeException("Image type not yet supported: "
					+ input.getConfig());
		}
	}
	
	public static void bitmapToGrayArray(Bitmap input, ImageFloat32 output,
			int storage[]) {
		final int h = output.height;
		final int w = output.width;

		input.copyPixelsToBuffer(IntBuffer.wrap(storage));
		int indexSrc = 0;
		
		switch (input.getConfig()) {
		case ARGB_8888:
			for (int y = 0; y < h; y++) {
				int indexDst = output.startIndex + y * output.stride;
				int end = indexDst + w;
				// for (int x = 0; x < w; x++) {
				while (indexDst < end) {
					int rgb = storage[indexSrc++];

					int value = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
					output.data[indexDst++] = value;
				}
			}
			break;

		default:
			throw new RuntimeException("Image type not yet supported: "
					+ input.getConfig());
		}
	}
	
	public static void bitmapToMultiArray_U8(Bitmap input, MultiSpectral<ImageUInt8> output,
			int storage[]) {
		final int h = output.height;
		final int w = output.width;

		ImageUInt8 R = output.getBand(0);
		ImageUInt8 G = output.getBand(1);
		ImageUInt8 B = output.getBand(2);
		ImageUInt8 A = output.getBand(3);
		
		input.copyPixelsToBuffer(IntBuffer.wrap(storage));
		int indexSrc = 0;
		
		switch (input.getConfig()) {
		case ARGB_8888:
			for (int y = 0; y < h; y++) {
				int indexDst = output.startIndex + y * output.stride;
				int end = indexDst + w;
				// for (int x = 0; x < w; x++) {
				while (indexDst < end) {
					int rgb = storage[indexSrc++];

					A.data[indexDst] = (byte)(rgb >> 24);
					B.data[indexDst] = (byte)(rgb >> 16);
					G.data[indexDst] = (byte)(rgb >> 8);
					R.data[indexDst] = (byte)rgb;
					indexDst++;
				}
			}
			break;

		default:
			throw new RuntimeException("Image type not yet supported: "
					+ input.getConfig());
		}
	}
	
	public static void bitmapToMultiArray_F32(Bitmap input, MultiSpectral<ImageFloat32> output,
			int storage[]) {
		final int h = output.height;
		final int w = output.width;

		ImageFloat32 R = output.getBand(0);
		ImageFloat32 G = output.getBand(1);
		ImageFloat32 B = output.getBand(2);
		ImageFloat32 A = output.getBand(3);
		
		input.copyPixelsToBuffer(IntBuffer.wrap(storage));
		int indexSrc = 0;
		
		switch (input.getConfig()) {
		case ARGB_8888:
			for (int y = 0; y < h; y++) {
				int indexDst = output.startIndex + y * output.stride;
				int end = indexDst + w;
				// for (int x = 0; x < w; x++) {
				while (indexDst < end) {
					int rgb = storage[indexSrc++];

					A.data[indexDst] = (rgb >> 24) & 0xFF;
					B.data[indexDst] = (rgb >> 16) & 0xFF;
					G.data[indexDst] = (rgb >> 8) & 0xFF;
					R.data[indexDst] = rgb & 0xFF;
					indexDst++;
				}
			}
			break;

		default:
			throw new RuntimeException("Image type not yet supported: "
					+ input.getConfig());
		}
	}

	public static void bitmapToGrayReflection(Bitmap input, ImageUInt8 output) {
		final int h = output.height;
		final int w = output.width;

		byte[] array = extractByteArray(input);
		if (array == null)
			throw new RuntimeException("The reflection trick failed!");

		int indexSrc = 0;

		switch (input.getConfig()) {
		case ARGB_8888: {
			for (int y = 0; y < h; y++) {
				int indexDst = output.startIndex + y * output.stride;
				int end = indexDst + w;
				// for (int x = 0; x < w; x++, indexSrc++) {
				while (indexDst < end) {
					int value = ((array[indexSrc++] & 0xFF)
							+ (array[indexSrc++] & 0xFF) + (array[indexSrc++] & 0xFF)) / 3;
					output.data[indexDst++] = (byte) value;
					indexSrc++;// skip over alpha channel
				}
			}
		}
			break;

		case RGB_565:{
			for (int y = 0; y < h; y++) {
				int indexDst = output.startIndex + y * output.stride;
				for (int x = 0; x < w; x++) {

					int value = (array[indexSrc++] & 0xFF)
							| ((array[indexSrc++] & 0xFF) << 8 );
					
					int r = (value >> 11)*256/32;
					int g = ((value & 0x07E0) >> 5)*256/64;
					int b = (value & 0x001F)*256/32;

					output.data[indexDst++] = (byte) ((r + g + b) / 3);
				}
			}}
			break;

		case ALPHA_8:
			throw new RuntimeException("ALPHA_8 seems to have some weired internal format and is not currently supported");
//			for (int y = 0; y < h; y++) {
//				int indexDst = output.startIndex + y * output.stride;
//				for (int x = 0; x < w; x++) {
//					output.data[indexDst++] = array[indexSrc++];
//				}
//			}
//			break;

		case ARGB_4444:
			throw new RuntimeException("Isn't 4444 deprecated?");
		}

	}

	public static void bitmapToGrayReflection(Bitmap input, ImageFloat32 output) {
		final int h = output.height;
		final int w = output.width;

		byte[] array = extractByteArray(input);
		if (array == null)
			throw new RuntimeException("The reflection trick failed!");

		int indexSrc = 0;

		switch (input.getConfig()) {
		case ARGB_8888: {
			for (int y = 0; y < h; y++) {
				int indexDst = output.startIndex + y * output.stride;
				int end = indexDst + w;
				// for (int x = 0; x < w; x++, indexSrc++) {
				while (indexDst < end) {
					int value = ((array[indexSrc++] & 0xFF)
							+ (array[indexSrc++] & 0xFF) + (array[indexSrc++] & 0xFF)) / 3;
					output.data[indexDst++] = value;
					indexSrc++;// skip over alpha channel
				}
			}
		}
			break;

		case RGB_565:{
			for (int y = 0; y < h; y++) {
				int indexDst = output.startIndex + y * output.stride;
				for (int x = 0; x < w; x++) {

					int value = (array[indexSrc++] & 0xFF)
							| ((array[indexSrc++] & 0xFF) << 8 );
					
					int r = (value >> 11)*256/32;
					int g = ((value & 0x07E0) >> 5)*256/64;
					int b = (value & 0x001F)*256/32;

					output.data[indexDst++] = (r + g + b) / 3;
				}
			}}
			break;

		case ALPHA_8:
			throw new RuntimeException("ALPHA_8 seems to have some weired internal format and is not currently supported");

		case ARGB_4444:
			throw new RuntimeException("Isn't 4444 deprecated?");
		}
	}
	
	public static void bitmapToMultiReflection_U8(Bitmap input, MultiSpectral<ImageUInt8> output) {
		final int h = output.height;
		final int w = output.width;
		
		ImageUInt8 R = output.getBand(0);
		ImageUInt8 G = output.getBand(1);
		ImageUInt8 B = output.getBand(2);

		byte[] array = extractByteArray(input);
		if (array == null)
			throw new RuntimeException("The reflection trick failed!");

		int indexSrc = 0;

		switch (input.getConfig()) {
		case ARGB_8888: {
			ImageUInt8 A = output.getBand(3);
			for (int y = 0; y < h; y++) {
				int indexDst = output.startIndex + y * output.stride;
				int end = indexDst + w;
				// for (int x = 0; x < w; x++, indexSrc++) {
				while (indexDst < end) {
					R.data[indexDst] = array[indexSrc++];
					G.data[indexDst] = array[indexSrc++];
					B.data[indexDst] = array[indexSrc++];
					A.data[indexDst] = array[indexSrc++];
					
					indexDst++;
				}
			}
		}
			break;

		case RGB_565:{
			for (int y = 0; y < h; y++) {
				int indexDst = output.startIndex + y * output.stride;
				for (int x = 0; x < w; x++,indexDst++) {

					int value = (array[indexSrc++] & 0xFF)
							| ((array[indexSrc++] & 0xFF) << 8 );
					
					int r = (value >> 11)*0xFF/0x1F;
					int g = ((value >> 5) & 0x3F )*0xFF/0x3F;
					int b = (value & 0x1F)*0xFF/0x1F;
					
					B.data[indexDst] = (byte)b;
					G.data[indexDst] = (byte)g;
					R.data[indexDst] = (byte)r;
				}
			}}
			break;

		case ALPHA_8:
			throw new RuntimeException("ALPHA_8 seems to have some weired internal format and is not currently supported");

		case ARGB_4444:
			throw new RuntimeException("Isn't 4444 deprecated?");
		}

	}
	
	public static void bitmapToMultiReflection_F32(Bitmap input, MultiSpectral<ImageFloat32> output) {
		final int h = output.height;
		final int w = output.width;
		
		ImageFloat32 R = output.getBand(0);
		ImageFloat32 G = output.getBand(1);
		ImageFloat32 B = output.getBand(2);

		byte[] array = extractByteArray(input);
		if (array == null)
			throw new RuntimeException("The reflection trick failed!");

		int indexSrc = 0;

		switch (input.getConfig()) {
		case ARGB_8888: {
			ImageFloat32 A = output.getBand(3);
			for (int y = 0; y < h; y++) {
				int indexDst = output.startIndex + y * output.stride;
				int end = indexDst + w;
				// for (int x = 0; x < w; x++, indexSrc++) {
				while (indexDst < end) {
					R.data[indexDst] = array[indexSrc++] & 0xFF;
					G.data[indexDst] = array[indexSrc++] & 0xFF;
					B.data[indexDst] = array[indexSrc++] & 0xFF;
					A.data[indexDst] = array[indexSrc++] & 0xFF;
					
					indexDst++;
				}
			}
		}
			break;

		case RGB_565:{
			for (int y = 0; y < h; y++) {
				int indexDst = output.startIndex + y * output.stride;
				for (int x = 0; x < w; x++,indexDst++) {

					int value = (array[indexSrc++] & 0xFF)
							| ((array[indexSrc++] & 0xFF) << 8 );
					
					int r = (value >> 11)*0xFF/0x1F;
					int g = ((value >> 5) & 0x3F )*0xFF/0x3F;
					int b = (value & 0x1F)*0xFF/0x1F;
					
					B.data[indexDst] = b & 0xFF;
					G.data[indexDst] = g & 0xFF;
					R.data[indexDst] = r & 0xFF;
				}
			}}
			break;

		case ALPHA_8:
			throw new RuntimeException("ALPHA_8 seems to have some weired internal format and is not currently supported");

		case ARGB_4444:
			throw new RuntimeException("Isn't 4444 deprecated?");
		}

	}

	public static void grayToBitmapReflection(ImageUInt8 input, Bitmap output) {
		final int h = input.height;
		final int w = input.width;

		byte[] array = extractByteArray(output);

		int indexDst = 0;

		switch (output.getConfig()) {
		case ARGB_8888:
			for (int y = 0; y < h; y++) {
				int indexSrc = input.startIndex + y * input.stride;
				for (int x = 0; x < w; x++) {
					int value = input.data[indexSrc++] & 0xFF;

					array[indexDst++] = (byte) value;
					array[indexDst++] = (byte) value;
					array[indexDst++] = (byte) value;
					array[indexDst++] = (byte) 0xFF;
				}
			}
			break;

		case RGB_565:
			for (int y = 0; y < h; y++) {
				int indexSrc = input.startIndex + y * input.stride;
				for (int x = 0; x < w; x++) {
					int value = input.data[indexSrc++] & 0xFF;
					int value5 = table5[value];
					int value6 = table6[value];

					int rgb565 = (value5 << 11) | (value6 << 5) | value5;
					array[indexDst++] = (byte) rgb565;
					array[indexDst++] = (byte) (rgb565 >> 8);
				}
			}
			break;

		case ALPHA_8:
			throw new RuntimeException("ALPHA_8 seems to have some weired internal format and is not currently supported");
//			for (int y = 0; y < h; y++) {
//				int indexSrc = input.startIndex + y * input.stride;
//				for (int x = 0; x < w; x++) {
//					array[indexDst++] = input.data[indexSrc++];
//				}
//			}
//			break;

		case ARGB_4444:
			throw new RuntimeException("Isn't 4444 deprecated?");
		}

	}
	
	public static void grayToBitmapReflection(ImageFloat32 input, Bitmap output) {
		final int h = input.height;
		final int w = input.width;
		
		byte[] array = extractByteArray(output);

		int indexDst = 0;

		switch (output.getConfig()) {
		case ARGB_8888:
			for (int y = 0; y < h; y++) {
				int indexSrc = input.startIndex + y * input.stride;
				for (int x = 0; x < w; x++) {
					int value = (int)input.data[indexSrc++];
					
					array[indexDst++] = (byte) value;
					array[indexDst++] = (byte) value;
					array[indexDst++] = (byte) value;
					array[indexDst++] = (byte) 0xFF;
				}
			}
			break;

		case RGB_565:
			for (int y = 0; y < h; y++) {
				int indexSrc = input.startIndex + y * input.stride;
				for (int x = 0; x < w; x++) {
					int value = (int)input.data[indexSrc++];
					int value5 = table5[value];
					int value6 = table6[value];

					int rgb565 = (value5 << 11) | (value6 << 5) | value5;
					array[indexDst++] = (byte) rgb565;
					array[indexDst++] = (byte) (rgb565 >> 8);
				}
			}
			break;

		case ALPHA_8:
			throw new RuntimeException("ALPHA_8 seems to have some weired internal format and is not currently supported");

		case ARGB_4444:
			throw new RuntimeException("Isn't 4444 deprecated?");
		}
	}
	
	public static void multiToBitmapReflection_U8(MultiSpectral<ImageUInt8> input, Bitmap output) {
		final int h = input.height;
		final int w = input.width;

		ImageUInt8 R = input.getBand(0);
		ImageUInt8 G = input.getBand(1);
		ImageUInt8 B = input.getBand(2);
		
		byte[] array = extractByteArray(output);

		int indexDst = 0;

		switch (output.getConfig()) {
		case ARGB_8888:
			ImageUInt8 A = input.getBand(3);
			for (int y = 0; y < h; y++) {
				int indexSrc = input.startIndex + y * input.stride;
				for (int x = 0; x < w; x++,indexSrc++) {
					array[indexDst++] = R.data[indexSrc];
					array[indexDst++] = G.data[indexSrc];
					array[indexDst++] = B.data[indexSrc];
					array[indexDst++] = A.data[indexSrc];
				}
			}
			break;

		case RGB_565:
			for (int y = 0; y < h; y++) {
				int indexSrc = input.startIndex + y * input.stride;
				for (int x = 0; x < w; x++,indexSrc++) {
					int r = R.data[indexSrc] & 0xFF;
					int g = G.data[indexSrc] & 0xFF;
					int b = B.data[indexSrc] & 0xFF;
					
					int valueR = table5[r];
					int valueG = table6[g];
					int valueB = table5[b];

					int rgb565 = (valueR << 11) | (valueG << 5) | valueB;
					
					array[indexDst++] = (byte) rgb565;
					array[indexDst++] = (byte) (rgb565 >> 8);
				}
			}
			break;

		case ALPHA_8:
			throw new RuntimeException("ALPHA_8 seems to have some weired internal format and is not currently supported");

		case ARGB_4444:
			throw new RuntimeException("Isn't 4444 deprecated?");
		}
	}
	
	public static void multiToBitmapReflection_F32(MultiSpectral<ImageFloat32> input, Bitmap output) {
		final int h = input.height;
		final int w = input.width;

		ImageFloat32 R = input.getBand(0);
		ImageFloat32 G = input.getBand(1);
		ImageFloat32 B = input.getBand(2);
		
		byte[] array = extractByteArray(output);

		int indexDst = 0;

		switch (output.getConfig()) {
		case ARGB_8888:
			ImageFloat32 A = input.getBand(3);
			for (int y = 0; y < h; y++) {
				int indexSrc = input.startIndex + y * input.stride;
				for (int x = 0; x < w; x++,indexSrc++) {
					array[indexDst++] = (byte)R.data[indexSrc];
					array[indexDst++] = (byte)G.data[indexSrc];
					array[indexDst++] = (byte)B.data[indexSrc];
					array[indexDst++] = (byte)A.data[indexSrc];
				}
			}
			break;

		case RGB_565:
			for (int y = 0; y < h; y++) {
				int indexSrc = input.startIndex + y * input.stride;
				for (int x = 0; x < w; x++,indexSrc++) {
					int r = (int)R.data[indexSrc];
					int g = (int)G.data[indexSrc];
					int b = (int)B.data[indexSrc];
					
					int valueR = table5[r];
					int valueG = table6[g];
					int valueB = table5[b];

					int rgb565 = (valueR << 11) | (valueG << 5) | valueB;
					
					array[indexDst++] = (byte) rgb565;
					array[indexDst++] = (byte) (rgb565 >> 8);
				}
			}
			break;

		case ALPHA_8:
			throw new RuntimeException("ALPHA_8 seems to have some weired internal format and is not currently supported");

		case ARGB_4444:
			throw new RuntimeException("Isn't 4444 deprecated?");
		}
	}

	/**
	 * Use reflections to gain access to the private internal data array
	 */
	private static byte[] extractByteArray(Bitmap input) {
		try {
			// grab the array
			return (byte[]) field.get(input);
		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}
}
