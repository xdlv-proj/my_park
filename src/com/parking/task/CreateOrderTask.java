package com.parking.task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap.CompressFormat;
import android.os.Message;

import com.CarOCR.CarOCREngine;
import com.lidroid.xutils.DbUtils;
import com.parking.ParkingmBaseTask;
import com.parking.UserOrder;
import com.xdlv.async.log.FileLogUtils;

public class CreateOrderTask extends ParkingmBaseTask {

	static CarOCREngine engine;
	static FileLogUtils logger = FileLogUtils.getInstance("CreateOrderTask");
	
	public CreateOrderTask(Activity context, Object handler) {
		super(context, handler);
		if (engine == null) {
			engine = new CarOCREngine();
			engine.init();
			String assetNames = "mPcaLda.dic";
			byte[] pDicData;
			InputStream is = null;
			try {
				is = context.getAssets().open(assetNames);
				int size = is.available();
				pDicData = new byte[size];
				is.read(pDicData);
				is.close();
				engine.loadDiction(pDicData, size);
			} catch (IOException e) {
				logger.e("failed to init engine", e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.parking.task.ICreateOrderTask#getCurrentOrderMax(int, int, java.lang.String)
	 */
	public Message getCurrentOrderMax(int dealy,int code,String phone) throws Exception {
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
	
	/* (non-Javadoc)
	 * @see com.parking.task.ICreateOrderTask#createOrder(int, int, com.parking.UserOrder)
	 */
	public Message createOrder(int delay,int code,UserOrder order) throws Exception {
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
	
	/* (non-Javadoc)
	 * @see com.parking.task.ICreateOrderTask#reconizeNo(int, int, java.lang.String)
	 */
	public Message reconizeNo(int delay,int code, String path) throws Exception{
		char[] resultString = new char[100];
		engine.recogpageFile(path, resultString);
		String resultStr = "";
		for (int i = 0; i < resultString.length; i++) {
			if (resultString[i] == 0x0) {
				break;
			}
			resultStr += resultString[i];
		}
		if (resultStr.length() < 7){
			throw new Exception("can not reconize the plateno");
		}
		return obtainMessage(code, resultStr);
	}

}
