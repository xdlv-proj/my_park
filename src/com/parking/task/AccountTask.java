package com.parking.task;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Message;

import com.lidroid.xutils.DbUtils;
import com.parking.ParkingmBaseTask;
import com.parking.User;

public class AccountTask extends ParkingmBaseTask{

	public AccountTask(Activity context, Object handler) {
		super(context, handler);
	}

	public Message queryUserInfo(int delay,int code, String phone) throws Exception {
		JSONObject ret = getJasonForServer("pm/getIncome", new String[][] { { "phone", phone } });
		JSONObject userRet = getJasonForServer("pmuser/getUserInfo", new String[][] { 
				{ "phone", phone } });
		User user = new User();
		user.setMobilePhone(phone);
		user.setUserName(userRet.optString("name"));
		user.setIdCardNumber(userRet.optString("idcard"));
		user.setAliplay(userRet.optString("alipay"));
		user.setWeiXing(userRet.optString("weixin"));
		user.setBankCard(userRet.optString("bankcard"));
		user.setTotalForLastMonth((float) ret.optDouble("lastmonthincome", 0.0));
		user.setTotalForToday((float) ret.optDouble("monthincome", 0.0));
		return obtainMessage(code, user);
	}
	
	public Message quiteLogin(int delay,int code,User user) throws Exception {
		DbUtils db = getDbUtils();
		db.update(user, "login");
		return obtainMessage(code, null);
	}
}
