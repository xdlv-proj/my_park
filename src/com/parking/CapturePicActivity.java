package com.parking;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.parking.task.CapturePicTask;
import com.parking.task.ICapturePicTask;
import com.xdlv.async.task.Proc;
import com.xdlv.async.task.ProxyCommonTask;

public class CapturePicActivity extends Activity implements PictureCallback {

	@ViewInject(R.id.pre_view)
	PreView preView;

	@ViewInject(R.id.top_view)
	TopView topView;

	
	ICapturePicTask task = ProxyCommonTask.createTaskProxy(
			CapturePicTask.class,ICapturePicTask.class, this, this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture_pic);
		ViewUtils.inject(this);
	}

	@OnClick(R.id.btnShutter)
	void captureImage(View view) {
		preView.captureImage(this);
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		task.procRawImage(0, R.id.btnShutter,data);
		camera.stopPreview();
	}
	@Proc({R.id.btnShutter, -R.id.btnShutter})
	void procRawPic(Message msg){
		if (msg.obj instanceof Throwable){
			Toast.makeText(this, "无法创建图像", Toast.LENGTH_LONG).show();
			setResult(RESULT_CANCELED);
		} else {
			setResult(RESULT_OK,
					new Intent().putExtra("path", (String)msg.obj));
		}
		finish();
	}

}
