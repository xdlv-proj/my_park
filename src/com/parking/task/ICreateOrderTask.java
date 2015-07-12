package com.parking.task;

import android.os.Message;

import com.parking.UserOrder;
import com.xdlv.async.task.ITaskProxy;

public interface ICreateOrderTask extends ITaskProxy{

	Message getCurrentOrderMax(int dealy, int code, String phone);

	Message createOrder(int delay, int code, UserOrder order);

	Message reconizeNo(int delay, int code, String path);

}