package com.parking.task;

import com.xdlv.async.task.ITaskProxy;

import android.os.Message;

public interface IOrderTask extends ITaskProxy{

	Message getTodayOrder(int delay, int code, String phone);

}