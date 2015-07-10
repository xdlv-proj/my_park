package com.parking.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class BitMapUtil {

	public static Bitmap compressImage(String imgPath) {
		Options bitmapFactoryOptions = new BitmapFactory.Options();
		// ������������ǽ�ͼƬ�߽粻�ɵ��ڱ�Ϊ�ɵ���
		bitmapFactoryOptions.inJustDecodeBounds = true;
		bitmapFactoryOptions.inSampleSize = 2;
		/*int outWidth = bitmapFactoryOptions.outWidth;
		int outHeight = bitmapFactoryOptions.outHeight;*/
		Bitmap bmap = BitmapFactory.decodeFile(imgPath,
				bitmapFactoryOptions);
		float imagew = 150;
		float imageh = 150;
		int yRatio = (int) Math.ceil(bitmapFactoryOptions.outHeight / imageh);
		int xRatio = (int) Math.ceil(bitmapFactoryOptions.outWidth / imagew);
		if (yRatio > 1 || xRatio > 1) {
			if (yRatio > xRatio) {
				bitmapFactoryOptions.inSampleSize = yRatio;
			} else {
				bitmapFactoryOptions.inSampleSize = xRatio;
			}
		}
		bitmapFactoryOptions.inJustDecodeBounds = false;
		bmap = BitmapFactory.decodeFile(imgPath,
				bitmapFactoryOptions);
		if (bmap != null) {
			return bmap;
		}
		return null;
	}
}
