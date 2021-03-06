package com.parking.task;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Message;

import com.parking.ParkingmBaseTask;

public class CapturePicTask extends ParkingmBaseTask{

	public CapturePicTask(Activity context, Object handler) {
		super(context, handler);
	}

	/* (non-Javadoc)
	 * @see com.parking.task.ICapturePicTask#procRawImage(int, int, byte[])
	 */
	public Message procRawImage(int delay,int code, byte[] data) throws Exception {
		File picDir = Environment.getExternalStorageDirectory();
		picDir = new File(picDir.getAbsolutePath() + "/park/");
		if (!picDir.exists() && !picDir.mkdirs()) {
			throw new Exception("�޷�����ͼ��");
		}
		String strPath = picDir + "/CarNumber.jpg";
		FileOutputStream outStream = null;
		Bitmap bitmap = null,createBitmap = null;
		try {
			outStream = new FileOutputStream(strPath);
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);

			createBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
		} finally {
			if (outStream != null) {
				outStream.close();
			}
			if (bitmap != null){
				bitmap.recycle();
			}
			if (createBitmap != null){
				createBitmap.recycle();
			}
		}

		return obtainMessage(code, strPath);
	}
}
