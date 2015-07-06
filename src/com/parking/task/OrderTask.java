package com.parking.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lidroid.xutils.DbUtils;
import com.parking.ParkingmBaseTask;
import com.parking.UserOrder;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Message;

public class OrderTask extends ParkingmBaseTask {

	public OrderTask(Activity context, Object handler) {
		super(context, handler);
	}

	Message getTodayOrder(int code, String phone) throws Exception {
		JSONObject ret = getJasonForServer("pmorder/getTodayOrder", new String[][] { { "phone",
				phone } });
		DbUtils db = getDbUtils();
		List<UserOrder> orders = new ArrayList<UserOrder>();
		String stringRecords = "todayOrder";
		if (!ret.isNull(stringRecords)) {
			JSONArray records = ret.getJSONArray(stringRecords);
			JSONObject record;
			UserOrder localRecord;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			for (int i = 0; records != null && i < records.length(); i++) {
				UserOrder uo = new UserOrder();
				record = (JSONObject) records.get(i);
				uo.setOrderId(record.getInt("id"));
				uo.setPlateCode(record.getString("carnum"));
				uo.setCreateTime(sdf.parse(record.getString("btime")).getTime());
				uo.setFeeType(record.getInt("ptype"));
				uo.setPrice((float) record.getDouble("price"));
				uo.setStatus(record.getInt("status"));
				if (uo.getStatus() == UserOrder.LEAVED_STATUS){
					uo.setLeavedTime(sdf.parse(record.getString("etime")).getTime());
					uo.setRealPrice((float)record.optDouble("paid", uo.getPrice()));
				}
				uo.setMobilePhone(phone);
				localRecord = db.findById(UserOrder.class, uo.getOrderId());
				
				if (localRecord != null) {
					uo.setBitMap(BitmapFactory.decodeByteArray(localRecord.getPicData(), 0,
							localRecord.getPicData().length));
					/*if (uo.getLeavedTime() < 1){
						uo.setLeavedTime(localRecord.getLeavedTime());
					}*/
				}
				orders.add(uo);
			}
		}
		return obtainMessage(code, orders);
	}
}
