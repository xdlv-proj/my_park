package com.parking.task;

import java.io.ByteArrayOutputStream;

import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap.CompressFormat;
import android.os.Message;

import com.lidroid.xutils.DbUtils;
import com.parking.ParkingmBaseTask;
import com.parking.UserOrder;

public class CreateOrderTask extends ParkingmBaseTask {

	public CreateOrderTask(Activity context, Object handler) {
		super(context, handler);
	}

	protected Message getCurrentOrderMax(int code,String phone) throws Exception {
		DbUtils db = getDbUtils();
		int max = 0;
		if (db.tableIsExist(UserOrder.class)) {
			long currentTime = getTodayStartTime();
			Cursor cursor = db.execQuery("select max(orderId) from UserOrder where createTime>" + currentTime
					+ " and mobilePhone='" + phone + "'");
			while (cursor.moveToNext()) {
				max = cursor.getInt(0);
				break;
			}
			cursor.close();
		}

		return obtainMessage(code, max);
	}
	
	protected Message createOrder(int code,UserOrder order) throws Exception {
		// convert picture to byte array
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		order.getBitMap().compress(CompressFormat.PNG, 50, baos);
		order.setPicData(baos.toByteArray());
		baos.close();
		/*
		 * order.setPlateCode(String.format("¾©A %05d", Math.abs(new
		 * Random().nextInt(99999))));
		 */

		JSONObject ret = getJasonForServer("pmorder/createOrder",
				new String[][] { { "phone", order.getMobilePhone() }, { "carnum", order.getPlateCode() },
						{ "paytype", String.valueOf(order.getFeeType()) }, { "price", String.valueOf(order.getPrice()) },
						{ "carimageid", String.valueOf(order.getOrderId()) }, { "lng", String.valueOf(order.getLng()) },
						{ "lat", String.valueOf(order.getLat()) }, });
		assertFlag(ret, "failed to new order");
		int orderId = ret.getInt("orderid");
		order.setOrderId(orderId);
		getDbUtils().save(order);
		return obtainMessage(code, null);
	}

}
