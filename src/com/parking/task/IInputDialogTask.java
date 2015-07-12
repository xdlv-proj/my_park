package com.parking.task;

import android.os.Message;

import com.parking.User;
import com.parking.UserOrder;
import com.xdlv.async.task.ITaskProxy;

public interface IInputDialogTask extends ITaskProxy{

	Message updateUserOrderStatus(int delay, int code, UserOrder uo);

	Message updateUserAccountName(int delay, int code, User user);

	Message getOrderTimeAndFee(int delay, int code, UserOrder uo);

}