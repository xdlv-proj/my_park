package com.parking.task;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Message;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.parking.ParkingmBaseTask;
import com.parking.User;

public class LoginTask extends ParkingmBaseTask {

	public LoginTask(Activity context, Object handler) {
		super(context, handler);
	}

	Message getLocalUserInfo(int code) throws Exception {
		DbUtils db = DbUtils.create(context);
		User user = db.findFirst(Selector.from(User.class).orderBy("lastLoginTime", true));
		return obtainMessage(code, user);
	}

	Message login(int code, String phone, String validateCode) throws Exception {
		JSONObject ret = getJasonForServer("pmuser/checkValidateCode", new String[][] { 
				{ "phone", phone },
				{ "code", validateCode } });
		assertFlag(ret, "failed to check validate code");
		DbUtils db = getDbUtils();
		User user = db.findById(User.class, phone);
		if (user == null) {
			user = new User();
		}
		user.setMobilePhone(phone);
		// save login user status
		user.setLogin(true);
		user.setLastLoginTime(System.currentTimeMillis());
		db.saveOrUpdate(user);
		return obtainMessage(code, user);
	}

	Message getValidate(int code, String phone) throws Exception {
		JSONObject ret = getJasonForServer("pmuser/getValidateCode", new String[][] { 
				{ "phone", phone } });
		assertFlag(ret, "failed to get validate code");
		return obtainMessage(code, null);
	}
	
	Message waitValidateCode(int code, int count) throws Exception {
		Thread.sleep(1000);
		return obtainMessage(code, count-1);
	}

}
