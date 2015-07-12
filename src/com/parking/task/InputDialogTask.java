package com.parking.task;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Message;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.SqlInfoBuilder;
import com.parking.ParkingmBaseTask;
import com.parking.User;
import com.parking.UserOrder;

public class InputDialogTask extends ParkingmBaseTask{

	public InputDialogTask(Activity context, Object handler) {
		super(context, handler);
	}

	/* (non-Javadoc)
	 * @see com.parking.task.IInputDialogTask#updateUserOrderStatus(int, int, com.parking.UserOrder)
	 */
	public Message updateUserOrderStatus(int delay,int code, UserOrder uo) throws Exception {
		JSONObject ret = getJasonForServer(
				"pmorder/collectFee",
				new String[][] { { "phone", uo.getMobilePhone() },
						{ "orderid", String.valueOf(uo.getOrderId()) },
						{ "fee", String.valueOf(uo.getRealPrice()) } });
		assertFlag(ret, "failed to collect fee");
		
		DbUtils db = DbUtils.create(context);
		if (db.findById(UserOrder.class, uo.getOrderId()) != null){
			uo.setStatus(UserOrder.LEAVED_STATUS);
			uo.setLeavedTime(System.currentTimeMillis());
			db.execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(db, uo, "realPrice", "status",
					"leavedTime"));
		}
		return obtainMessage(code, null);
	}

	/* (non-Javadoc)
	 * @see com.parking.task.IInputDialogTask#updateUserAccountName(int, int, com.parking.User)
	 */
	public Message updateUserAccountName(int delay,int code,User user) throws Exception {
		JSONObject ret = getJasonForServer("pmuser/changeUserInfo", new String[][] {
				{ "phone", user.getMobilePhone() }, 
				{ "name", user.getUserName() }, 
				{ "idcard", user.getIdCardNumber() },
				{ "alipay", user.getAliplay() },
				{ "weixin", user.getWeiXing() }, 
				{ "bankcard", user.getBankCard() } });
		assertFlag(ret, "failed to change user name");
		DbUtils db = getDbUtils();
		db.execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(db, user, "userName"
				,"idCardNumber","alipay", "weiXing",
				"bankCard"));
		return obtainMessage(code, null);
	}
	
	/* (non-Javadoc)
	 * @see com.parking.task.IInputDialogTask#getOrderTimeAndFee(int, int, com.parking.UserOrder)
	 */
	public Message getOrderTimeAndFee(int delay,int code, UserOrder uo) throws Exception{
		JSONObject ret = getJasonForServer("pmorder/getOrderTimeAndFee", new String[][]{
				{"phone", uo.getMobilePhone()},
				{"orderid", String.valueOf(uo.getOrderId())}
		});
		return obtainMessage(code, new int[]{ret.optInt("ordersec"),(int)ret.optDouble("fee")});
	}
}
