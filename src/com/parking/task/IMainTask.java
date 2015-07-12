package com.parking.task;

import android.os.Message;

import com.parking.User;
import com.xdlv.async.task.ITaskProxy;

public interface IMainTask extends ITaskProxy{

	Message getIncomeAndTotalNum(int delay, int code, String phone);

	Message applyTotalNum(int delay,int code, String phone, int totalNum);

	Message updateUserParkSlotNumber(int delay,int code, User user);

}