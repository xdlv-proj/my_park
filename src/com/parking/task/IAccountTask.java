package com.parking.task;

import android.os.Message;

import com.parking.User;
import com.xdlv.async.task.ITaskProxy;

public interface IAccountTask extends ITaskProxy{

	Message queryUserInfo(int delay, int code, String phone);

	Message quiteLogin(int delay, int code, User user);

}