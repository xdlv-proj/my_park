package com.parking.task;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Message;

import com.parking.ParkingmBaseTask;
import com.parking.User;

public class MainTask extends ParkingmBaseTask{

	public MainTask(Activity context, Object handler) {
		super(context, handler);
	}

	/* (non-Javadoc)
	 * @see com.parking.task.IMainTask#getIncomeAndTotalNum(int, int, java.lang.String)
	 */
	public Message getIncomeAndTotalNum(int delay, int code, String phone) throws Exception {
		JSONObject ret = getJasonForServer("pm/getIncomeAndTotalNum", new String[][] { { "phone",
				phone } });
		return obtainMessage(code,
				new Object[] { ret.optInt("totalnum", 0), ret.optDouble("todayincome", 0.0),ret.optInt("freenum",1) });
	}

	/* (non-Javadoc)
	 * @see com.parking.task.IMainTask#applyTotalNum(int, java.lang.String, int)
	 */
	public Message applyTotalNum(int delay,int code, String phone, int totalNum) throws Exception {

		return obtainMessage(code, null);
	}

	/* (non-Javadoc)
	 * @see com.parking.task.IMainTask#updateUserParkSlotNumber(int, com.parking.User)
	 */
	public Message updateUserParkSlotNumber(int delay,int code, User user) throws Exception {
		JSONObject ret = getJasonForServer(
				"pm/updateTotalNum",
				new String[][] { { "phone", user.getMobilePhone() },
						{ "totalnum", user.getParkSlotNumber() + "" } });
		assertFlag(ret, "failed to update total number");
		getDbUtils().update(user);
		return obtainMessage(code, null);
	}

}
