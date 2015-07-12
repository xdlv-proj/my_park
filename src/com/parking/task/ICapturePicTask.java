package com.parking.task;

import com.xdlv.async.task.ITaskProxy;

import android.os.Message;

public interface ICapturePicTask extends ITaskProxy{

	Message procRawImage(int delay, int code, byte[] data);

}