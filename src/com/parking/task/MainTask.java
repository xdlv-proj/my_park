package com.parking.task;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Message;

import com.parking.ParkingmBaseTask;
import com.parking.User;

public class MainTask extends ParkingmBaseTask {

	public MainTask(Activity context, Object handler) {
		super(context, handler);
	}

	protected Message getIncomeAndTotalNum(int code, String phone) throws Exception {
		JSONObject ret = getJasonForServer("pm/getIncomeAndTotalNum", new String[][] { 
				{ "phone", phone } });
		return obtainMessage(code,new Object[] { 
				ret.optInt("totalnum", 0), 
				ret.optDouble("todayincome", 0.0) 
				});
	}
	
	protected Message applyTotalNum(int code, String phone,int totalNum) throws Exception{
		
		return obtainMessage(code, null);
	}
	
	protected Message updateUserParkSlotNumber(int code,User user) throws Exception {
		JSONObject ret = getJasonForServer("pm/updateTotalNum", new String[][] { 
				{ "phone", user.getMobilePhone() },
				{ "totalnum", user.getParkSlotNumber() + "" } });
		assertFlag(ret, "failed to update total number");
		getDbUtils().update(user);
		return obtainMessage(code, null);
	}

}
