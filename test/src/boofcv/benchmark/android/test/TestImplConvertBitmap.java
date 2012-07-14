package boofcv.benchmark.android.test;

import android.graphics.Bitmap;
import android.test.AndroidTestCase;
import boofcv.benchmark.android.ImplConvertBitmap;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;

public class TestImplConvertBitmap extends AndroidTestCase {

	public void testBitmapToGrayReflection_8888_U8() {
		Bitmap orig = Bitmap.createBitmap(5,10, Bitmap.Config.ARGB_8888);
		// large alpha value that should be ignored and rgb each have different values
		orig.setPixel(1, 2, 0xFF010203 );
		
		ImageUInt8 found = new ImageUInt8(5,10);
		
		ImplConvertBitmap.bitmapToGrayReflection(orig, found);
		
		assertEquals(2,found.get(1,2));
		assertEquals(0,found.get(0,0));
	}
	
	public void testBitmapToGrayArray_8888_U8() {
		Bitmap orig = Bitmap.createBitmap(5,10, Bitmap.Config.ARGB_8888);
		// large alpha value that should be ignored and rgb each have different values
		orig.setPixel(1, 2, 0xFF010203 );
		
		ImageUInt8 found = new ImageUInt8(5,10);
		
		int storage[] = new int[found.width*found.height];
		ImplConvertBitmap.bitmapToGrayArray(orig, found,storage);
		
		assertEquals(2,found.get(1,2));
		assertEquals(0,found.get(0,0));
	}
	
	public void testBitmapToGrayRGB_U8() {
		Bitmap orig = Bitmap.createBitmap(5,10, Bitmap.Config.ARGB_8888);
		// large alpha value that should be ignored and rgb each have different values
		orig.setPixel(1, 2, 0xFF010203 );
		
		ImageUInt8 found = new ImageUInt8(5,10);
		
		ImplConvertBitmap.bitmapToGrayRGB(orig, found);
		
		assertEquals(2,found.get(1,2));
		assertEquals(0,found.get(0,0));
	}
	
	public void testBitmapToGrayReflection_565_U8() {
		Bitmap orig = Bitmap.createBitmap(5,10, Bitmap.Config.RGB_565);
		// large alpha value that should be ignored and rgb each have different values
		orig.setPixel(1, 2, 0xFF204010 );
		
		ImageUInt8 found = new ImageUInt8(5,10);
		
		ImplConvertBitmap.bitmapToGrayReflection(orig, found);

		assertEquals(37,found.get(1,2));
		assertEquals(0,found.get(0,0));
	}

	
	public void testGrayToBitmapReflection_U8_8888() {
		ImageUInt8 orig = new ImageUInt8(5,10);
		orig.set(1,2,16);
		orig.set(1,3,0xFF);
		
		Bitmap found = Bitmap.createBitmap(5,10, Bitmap.Config.ARGB_8888);

		ImplConvertBitmap.grayToBitmapReflection(orig, found);
		
		assertEquals(0xFF101010,(int)found.getPixel(1,2));
		assertEquals(0xFFFFFFFF,(int)found.getPixel(1,3));
		assertEquals((int)0xFF000000,found.getPixel(0,0));
	}
	
	public void testGrayToBitmapReflection_U8_565() {
		ImageUInt8 orig = new ImageUInt8(5,10);
		orig.set(1,2,16);
		orig.set(1,3,0xFF);
		
		Bitmap found = Bitmap.createBitmap(5,10, Bitmap.Config.RGB_565);

		ImplConvertBitmap.grayToBitmapReflection(orig, found);
		
		assertEquals(expected565(16,16,16),(int)found.getPixel(1,2));
		assertEquals(expected565(255,255,255),(int)found.getPixel(1,3));
		assertEquals((int)0xFF000000,found.getPixel(0,0));
	}
	
	public void testBitmapToGrayReflection_8888_F32() {
		Bitmap orig = Bitmap.createBitmap(5,10, Bitmap.Config.ARGB_8888);
		// large alpha value that should be ignored and rgb each have different values
		orig.setPixel(1, 2, 0xFF010203 );
		
		ImageFloat32 found = new ImageFloat32(5,10);
		
		ImplConvertBitmap.bitmapToGrayReflection(orig, found);
		
		assertEquals(2,(int)found.get(1,2));
		assertEquals(0,(int)found.get(0,0));
	}
	
	public void testBitmapToGrayArray_8888_F32() {
		Bitmap orig = Bitmap.createBitmap(5,10, Bitmap.Config.ARGB_8888);
		// large alpha value that should be ignored and rgb each have different values
		orig.setPixel(1, 2, 0xFF010203 );
		
		ImageFloat32 found = new ImageFloat32(5,10);
		
		int storage[] = new int[found.width*found.height];
		ImplConvertBitmap.bitmapToGrayArray(orig, found,storage);
		
		assertEquals(2,(int)found.get(1,2));
		assertEquals(0,(int)found.get(0,0));
	}
	
	public void testBitmapToGrayRGB_F32() {
		Bitmap orig = Bitmap.createBitmap(5,10, Bitmap.Config.ARGB_8888);
		// large alpha value that should be ignored and rgb each have different values
		orig.setPixel(1, 2, 0xFF010203 );
		
		ImageFloat32 found = new ImageFloat32(5,10);
		
		ImplConvertBitmap.bitmapToGrayRGB(orig, found);
		
		assertEquals(2,(int)found.get(1,2));
		assertEquals(0,(int)found.get(0,0));
	}
	
	public void testBitmapToGrayReflection_565_F32() {
		Bitmap orig = Bitmap.createBitmap(5,10, Bitmap.Config.RGB_565);
		// large alpha value that should be ignored and rgb each have different values
		orig.setPixel(1, 2, 0xFF204010 );
		
		ImageFloat32 found = new ImageFloat32(5,10);
		
		ImplConvertBitmap.bitmapToGrayReflection(orig, found);

		assertEquals(37,(int)found.get(1,2));
		assertEquals(0,(int)found.get(0,0));
	}

	
	public void testGrayToBitmapReflection_F32_8888() {
		ImageFloat32 orig = new ImageFloat32(5,10);
		orig.set(1,2,16);
		orig.set(1,3,255);
		
		Bitmap found = Bitmap.createBitmap(5,10, Bitmap.Config.ARGB_8888);

		ImplConvertBitmap.grayToBitmapReflection(orig, found);

		assertEquals(0xFF101010,(int)found.getPixel(1,2));
		assertEquals(0xFFFFFFFF,(int)found.getPixel(1,3));
		assertEquals((int)0xFF000000,found.getPixel(0,0));
	}
	
	public void testGrayToBitmapReflection_F32_565() {
		ImageFloat32 orig = new ImageFloat32(5,10);
		orig.set(1,2,16);
		orig.set(1,3,255);
		
		Bitmap found = Bitmap.createBitmap(5,10, Bitmap.Config.RGB_565);

		ImplConvertBitmap.grayToBitmapReflection(orig, found);
		
		assertEquals(expected565(16,16,16),(int)found.getPixel(1,2));
		assertEquals(expected565(255,255,255),(int)found.getPixel(1,3));
		assertEquals((int)0xFF000000,found.getPixel(0,0));
	}
	
	public void testBitmapToMultiReflection_8888_U8() {
		Bitmap orig = Bitmap.createBitmap(5,10, Bitmap.Config.ARGB_8888);
		// large alpha value that should be ignored and rgb each have different values
		orig.setPixel(1, 2, 0xFF010203 );
		
		MultiSpectral<ImageUInt8> found = new MultiSpectral<ImageUInt8>(ImageUInt8.class, 5, 10, 4);
		
		ImplConvertBitmap.bitmapToMultiReflection_U8(orig, found);
		
		assertEquals(0x01,found.getBand(0).get(1,2));
		assertEquals(0x02,found.getBand(1).get(1,2));
		assertEquals(0x03,found.getBand(2).get(1,2));
		assertEquals(0xFF,found.getBand(3).get(1,2));
	}
	
	public void testBitmapToMultiArray_8888_U8() {
		Bitmap orig = Bitmap.createBitmap(5,10, Bitmap.Config.ARGB_8888);
		// large alpha value that should be ignored and rgb each have different values
		orig.setPixel(1, 2, 0xFF010203 );
		
		MultiSpectral<ImageUInt8> found = new MultiSpectral<ImageUInt8>(ImageUInt8.class, 5, 10, 4);
		
		int storage[] = new int[found.width*found.height];
		ImplConvertBitmap.bitmapToMultiArray_U8(orig, found,storage);
		
		assertEquals(0x01,found.getBand(0).get(1,2));
		assertEquals(0x02,found.getBand(1).get(1,2));
		assertEquals(0x03,found.getBand(2).get(1,2));
		assertEquals(0xFF,found.getBand(3).get(1,2));
	}
	
	public void testBitmapToMultiRGB_U8() {
		Bitmap orig = Bitmap.createBitmap(5,10, Bitmap.Config.ARGB_8888);
		// large alpha value that should be ignored and rgb each have different values
		orig.setPixel(1, 2, 0xFF010203 );
		
		MultiSpectral<ImageUInt8> found = new MultiSpectral<ImageUInt8>(ImageUInt8.class, 5, 10, 4);
		
		ImplConvertBitmap.bitmapToMultiRGB_U8(orig, found);
		
		assertEquals(0x01,found.getBand(0).get(1,2));
		assertEquals(0x02,found.getBand(1).get(1,2));
		assertEquals(0x03,found.getBand(2).get(1,2));
		assertEquals(0xFF,found.getBand(3).get(1,2));
	}
	
	public void testBitmapToMultiReflection_565_U8() {
		Bitmap orig = Bitmap.createBitmap(5,10, Bitmap.Config.RGB_565);
		// large alpha value that should be ignored and rgb each have different values
		orig.setPixel(1, 2, 0xFF204010 );
		
		MultiSpectral<ImageUInt8> found = new MultiSpectral<ImageUInt8>(ImageUInt8.class, 5, 10, 3);
		
		ImplConvertBitmap.bitmapToMultiReflection_U8(orig, found);

		assertEquals(32,found.getBand(0).get(1,2));
		assertEquals(64,found.getBand(1).get(1,2));
		assertEquals(16,found.getBand(2).get(1,2));
	}
	
	public void testMultiToBitmapReflection_FU8_8888() {
		MultiSpectral<ImageUInt8> orig = new MultiSpectral<ImageUInt8>(ImageUInt8.class, 5, 10, 4);
		orig.getBand(0).set(1, 2, 0x12);
		orig.getBand(1).set(1, 2, 0xA0);
		orig.getBand(2).set(1, 2, 0xFF);
		orig.getBand(3).set(1, 2, 0xFF);
		
		Bitmap found = Bitmap.createBitmap(5,10, Bitmap.Config.ARGB_8888);

		ImplConvertBitmap.multiToBitmapReflection_U8(orig, found);

		assertEquals((int)0xFF12A0FF,(int)found.getPixel(1,2));
		assertEquals((int)0x00000000,found.getPixel(0,0));
	}
	
	public void testMultiToBitmapReflection_U8_565() {
		MultiSpectral<ImageUInt8> orig = new MultiSpectral<ImageUInt8>(ImageUInt8.class, 5, 10, 3);
		orig.getBand(0).set(1, 2, 56);
		orig.getBand(1).set(1, 2, 100);
		orig.getBand(2).set(1, 2, 255);
		
		Bitmap found = Bitmap.createBitmap(5,10, Bitmap.Config.RGB_565);

		ImplConvertBitmap.multiToBitmapReflection_U8(orig, found);
		
		assertEquals(expected565(56,100,255),(int)found.getPixel(1,2));
		assertEquals((int)0xFF000000,found.getPixel(0,0));
	}
	
	public void testBitmapToMultiReflection_8888_32F() {
		Bitmap orig = Bitmap.createBitmap(5,10, Bitmap.Config.ARGB_8888);
		// large alpha value that should be ignored and rgb each have different values
		orig.setPixel(1, 2, 0xFF010203 );
		
		MultiSpectral<ImageFloat32> found = new MultiSpectral<ImageFloat32>(ImageFloat32.class, 5, 10, 4);
		
		ImplConvertBitmap.bitmapToMultiReflection_F32(orig, found);
		
		assertEquals(0x01,(int)found.getBand(0).get(1,2));
		assertEquals(0x02,(int)found.getBand(1).get(1,2));
		assertEquals(0x03,(int)found.getBand(2).get(1,2));
		assertEquals(0xFF,(int)found.getBand(3).get(1,2));
	}
	
	public void testBitmapToMultiArray_8888_F32() {
		Bitmap orig = Bitmap.createBitmap(5,10, Bitmap.Config.ARGB_8888);
		// large alpha value that should be ignored and rgb each have different values
		orig.setPixel(1, 2, 0xFF010203 );
		
		MultiSpectral<ImageFloat32> found = new MultiSpectral<ImageFloat32>(ImageFloat32.class, 5, 10, 4);
		
		int storage[] = new int[found.width*found.height];
		ImplConvertBitmap.bitmapToMultiArray_F32(orig, found,storage);
		
		assertEquals(0x01,(int)found.getBand(0).get(1,2));
		assertEquals(0x02,(int)found.getBand(1).get(1,2));
		assertEquals(0x03,(int)found.getBand(2).get(1,2));
		assertEquals(0xFF,(int)found.getBand(3).get(1,2));
	}
	
	public void testBitmapToMultiRGB_F32() {
		Bitmap orig = Bitmap.createBitmap(5,10, Bitmap.Config.ARGB_8888);
		// large alpha value that should be ignored and rgb each have different values
		orig.setPixel(1, 2, 0xFF010203 );
		
		MultiSpectral<ImageFloat32> found = new MultiSpectral<ImageFloat32>(ImageFloat32.class, 5, 10, 4);
		
		ImplConvertBitmap.bitmapToMultiRGB_F32(orig, found);
		
		assertEquals(0x01,(int)found.getBand(0).get(1,2));
		assertEquals(0x02,(int)found.getBand(1).get(1,2));
		assertEquals(0x03,(int)found.getBand(2).get(1,2));
		assertEquals(0xFF,(int)found.getBand(3).get(1,2));
	}
	
	public void testBitmapToMultiReflection_565_F32() {
		Bitmap orig = Bitmap.createBitmap(5,10, Bitmap.Config.RGB_565);
		// large alpha value that should be ignored and rgb each have different values
		orig.setPixel(1, 2, 0xFF204010 );
		
		MultiSpectral<ImageFloat32> found = new MultiSpectral<ImageFloat32>(ImageFloat32.class, 5, 10, 3);
		
		ImplConvertBitmap.bitmapToMultiReflection_F32(orig, found);

		assertEquals(32,(int)found.getBand(0).get(1,2));
		assertEquals(64,(int)found.getBand(1).get(1,2));
		assertEquals(16,(int)found.getBand(2).get(1,2));
	}
	
	public void testMultiToBitmapReflection_F32_8888() {
		MultiSpectral<ImageFloat32> orig = new MultiSpectral<ImageFloat32>(ImageFloat32.class, 5, 10, 4);
		orig.getBand(0).set(1, 2, 0x12);
		orig.getBand(1).set(1, 2, 0xA0);
		orig.getBand(2).set(1, 2, 0xFF);
		orig.getBand(3).set(1, 2, 0xFF);
		
		Bitmap found = Bitmap.createBitmap(5,10, Bitmap.Config.ARGB_8888);

		ImplConvertBitmap.multiToBitmapReflection_F32(orig, found);

		assertEquals((int)0xFF12A0FF,(int)found.getPixel(1,2));
		assertEquals((int)0x00000000,found.getPixel(0,0));
	}
	
	public void testMultiToBitmapReflection_F32_565() {
		MultiSpectral<ImageFloat32> orig = new MultiSpectral<ImageFloat32>(ImageFloat32.class, 5, 10, 3);
		orig.getBand(0).set(1, 2, 56);
		orig.getBand(1).set(1, 2, 100);
		orig.getBand(2).set(1, 2, 255);
		
		Bitmap found = Bitmap.createBitmap(5,10, Bitmap.Config.RGB_565);

		ImplConvertBitmap.multiToBitmapReflection_F32(orig, found);
		
		assertEquals(expected565(56,100,255),(int)found.getPixel(1,2));
		assertEquals((int)0xFF000000,found.getPixel(0,0));
	}
	private static int expected565( int r , int g , int b ) {
		int valR = (int)Math.round(r*0x1F/255.0)*0xFF/0x1F;
		int valG = (int)Math.round(g*0x3F/255.0)*0xFF/0x3F;
		int valB = (int)Math.round(b*0x1F/255.0)*0xFF/0x1F;
		
		return (0xFF << 24) | (valR << 16) | (valG << 8) | valB;
	}
}
