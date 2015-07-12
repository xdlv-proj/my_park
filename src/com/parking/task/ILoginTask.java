package com.parking.task;

import com.xdlv.async.task.ITaskProxy;

import android.os.Message;

public interface ILoginTask extends ITaskProxy{

	Message getLocalUserInfo(int delay, int code);

	Message login(int delay, int code, String phone, String validateCode);

	Message getValidate(int delay, int code, String phone);

	Message waitValidateCode(int delay, int code, int count);

}