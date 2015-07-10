package com.parking;

import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class PreView extends SurfaceView implements SurfaceHolder.Callback {
	Camera mCamera;

	public PreView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		setDrawingCacheEnabled(false);
		getHolder().addCallback(this);
		getHolder().setKeepScreenOn(true);
	}

	public void captureImage(PictureCallback jePictureCallback) {
		mCamera.takePicture(new android.hardware.Camera.ShutterCallback() {
			public void onShutter() {
			}
		}, null, jePictureCallback);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera = Camera.open();
			mCamera.setDisplayOrientation(90);
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (Exception e) {
			Log.e("ERROR", "open camer error", e);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		try {
			Parameters parameters = mCamera.getParameters();
			parameters.setPreviewSize(w, h);
			// find the minimum size
			List<Size> sizes = parameters.getSupportedPictureSizes();
			Size size = null, tmp;
			for (int i=0;sizes != null && i<sizes.size();i++){
				tmp = sizes.get(i);
				if (size == null || tmp.width * tmp.height < size.width * size.height){
					size = tmp;
				}
			}
			if (size != null){
				parameters.setPictureSize(size.width, size.height);
			}
			mCamera.startPreview();
		} catch (Exception e) {
			Log.e("ERROR", "modify preview size error", e);
		}
	}
}