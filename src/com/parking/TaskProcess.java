package com.parking;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Message;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfoBuilder;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.xdlv.vistor.CommonTask;

public class TaskProcess {
	static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

	public static void getValidate(Object handler, final int code, String telphone) {
		new CommonTask<String>(handler, code) {
			@Override
			protected Message execute2(String... para) throws Exception {
				JSONObject ret = getJasonForServer("pmuser/getValidateCode", new String[][] { { "phone", para[0] } });
				assertFlag(ret, "failed to get validate code");
				return obtainMessage(code, null);
			}
		}.execute(telphone);
	}

	// get local user info for user's telephone
	public static void getLocalUserInfo(final Activity activity, Object handler, final int code) {
		new CommonTask<String>(handler, code) {
			@Override
			protected Message execute2(String... para) throws Exception {
				DbUtils db = DbUtils.create(activity);
				User user = db.findFirst(Selector.from(User.class).orderBy("lastLoginTime", true));
				return obtainMessage(code, user);
			}
		}.execute("");
	}

	// login in and save user info to current
	public static void login(final Activity handler, final int code, String telphone, String validate) {
		new CommonTask<String>(handler, code) {
			@Override
			protected Message execute2(String... para) throws Exception {
				JSONObject ret = getJasonForServer("pmuser/checkValidateCode", new String[][] { { "phone", para[0] },
						{ "code", para[1] } });
				assertFlag(ret, "failed to check validate code");
				DbUtils db = DbUtils.create(handler);
				User user = db.findById(User.class, para[0]);
				if (user == null){
					user = new User();
				}
				// save login user status
				user.setLogin(true);
				user.setLastLoginTime(System.currentTimeMillis());
				DbUtils.create(handler).saveOrUpdate(user);
				return obtainMessage(code, user);
			}
		}.execute(telphone, validate);
	}

	public static void getIncomeAndTotalNum(Object handler, final int code, String phone) {
		new CommonTask<String>(handler, code) {
			@Override
			protected Message execute2(String... para) throws Exception {
				JSONObject ret = getJasonForServer("pm/getIncomeAndTotalNum", new String[][] { { "phone", para[0] } });
				return obtainMessage(code, new Object[] { ret.optInt("totalnum", 0), ret.optDouble("todayincome", 0.0) });
			}
		}.execute(phone);
	}

	public static void quiteLogin(final Activity activity, Object handler, final int code, User user) {
		new CommonTask<User>(handler, code) {
			@Override
			protected Message execute2(User... para) throws Exception {
				DbUtils db = DbUtils.create(activity);
				db.update(para[0], "login");
				return obtainMessage(code, null);
			}
		}.execute(user);
	}

	// create order
	public static void createOrder(final Activity activity, final Object handler, final int code, UserOrder order) {
		new CommonTask<UserOrder>(handler, code) {
			@Override
			protected Message execute2(UserOrder... para) throws Exception {
				UserOrder order = para[0];
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

				DbUtils db = DbUtils.create(activity);
				db.save(order);

				return obtainMessage(code, null);
			}
		}.execute(order);
	}

	// get the max number of order for primary key
	public static void getCurrentOrderMax(final Activity activity, final int code, String userTel) {
		new CommonTask<String>(activity, code) {
			@Override
			protected Message execute2(String... para) throws Exception {
				DbUtils db = DbUtils.create(activity);
				int max = 0;
				if (db.tableIsExist(UserOrder.class)) {
					long currentTime = getTodayStartTime();
					Cursor cursor = db.execQuery("select max(orderId) from UserOrder where createTime>" + currentTime
							+ " and mobilePhone='" + para[0] + "'");
					while (cursor.moveToNext()) {
						max = cursor.getInt(0);
						break;
					}
					cursor.close();
				}

				return obtainMessage(code, max);
			}
		}.execute(userTel);
	}

	public static void getTodayOrder(final Activity activity, final Object handler, final int code, String userTel) {
		new CommonTask<String>(handler, code) {
			@Override
			protected Message execute2(String... para) throws Exception {
				JSONObject ret = getJasonForServer("pmorder/getTodayOrder", new String[][] { { "phone", para[0] } });
				DbUtils db = DbUtils.create(activity);
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
						uo.setMobilePhone(para[0]);
						localRecord = db.findById(UserOrder.class, uo.getOrderId());
						if (localRecord != null) {
							uo.setBitMap(BitmapFactory.decodeByteArray(localRecord.getPicData(), 0,
									localRecord.getPicData().length));
							uo.setLeavedTime(localRecord.getLeavedTime());
						}
						orders.add(uo);
					}
				}

				return obtainMessage(code, orders);
			}
		}.execute(userTel);
	}

	public static void updateUserParkSlotNumber(final Activity activity, final Object handler, final int code, User user) {
		new CommonTask<User>(handler, code) {
			@Override
			protected Message execute2(User... para) throws Exception {
				JSONObject ret = getJasonForServer("pm/updateTotalNum", new String[][] { 
						{ "phone", para[0].getMobilePhone() },
						{ "totalnum", para[0].getParkSlotNumber() + "" } });
				assertFlag(ret, "failed to update total number");
				DbUtils.create(activity).update(para[0]);
				return obtainMessage(code, null);
			}
		}.execute(user);
	}

	public static void updateUserOrderStatus(final Context context, Object handler, final int code, UserOrder uo) {
		new CommonTask<UserOrder>(handler, code) {
			@Override
			protected Message execute2(UserOrder... para) throws Exception {
				JSONObject ret = getJasonForServer("pmorder/collectFee", new String[][] { 
						{ "phone", para[0].getMobilePhone() },
						{ "orderid", String.valueOf(para[0].getOrderId()) }, 
						{ "fee", String.valueOf(para[0].getRealPrice()) } });
				assertFlag(ret, "failed to collect fee");
				DbUtils db = DbUtils.create(context);
				para[0].setStatus(UserOrder.LEAVED_STATUS);
				para[0].setLeavedTime(System.currentTimeMillis());
				db.execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(db, para[0], "realPrice", "status", "leavedTime"));
				return obtainMessage(code, null);
			}
		}.execute(uo);
	}

	public static void queryUserInfo(final Context context, Object handler, final int code, String phone) {
		new CommonTask<String>(handler, code) {
			@Override
			protected Message execute2(String... para) throws Exception {
				JSONObject ret = getJasonForServer("pm/getIncome"
						, new String[][] { { "phone", para[0] } });
				JSONObject userRet = getJasonForServer("pmuser/getUserInfo"
						, new String[][] { { "phone", para[0] } });
				User user = new User();
				user.setUserName(userRet.optString("name"));
				user.setIdCardNumber(userRet.optString("idcard"));
				user.setAliplay(userRet.optString("alipay"));
				user.setWeiXing(userRet.optString("weixin"));
				user.setBankCard(userRet.optString("bankcard"));
				user.setTotalForLastMonth((float)ret.optDouble("monthincome", 0.0));
				user.setTotalForToday((float)ret.optDouble("todayincome", 0.0));
				
				return obtainMessage(code, user);
			}
		}.execute(phone);
	}

	static final String host = "http://223.68.191.71:48080/ParkingHere/mobile/";
	static final HttpUtils httpUtils = new HttpUtils(1000 * 100);

	static JSONObject getJasonForServer(String url, String[][] paras) throws Exception {
		RequestParams requestParams = requestParams(paras);
		return getJasonForServer(url, requestParams);

	}

	static JSONObject getJasonForServer(String url, RequestParams requestParams) throws Exception {
		ResponseStream resp = null;
		try {
			resp = httpUtils.sendSync(HttpMethod.POST, host + url, requestParams);
			return new JSONObject(resp.readString());
		} finally {
			if (resp != null) {
				resp.close();
			}
		}
	}

	static RequestParams requestParams(String[][] paras) {
		RequestParams requestParams = new RequestParams("UTF-8");
		for (int i = 0; paras != null && i < paras.length; i++) {
			requestParams.addBodyParameter(paras[i][0], paras[i][1]);
		}
		return requestParams;
	}

	private static long getTodayStartTime() throws ParseException {
		String current = sdf.format(new Date());
		return sdf.parse(current).getTime();
	}

	private static void assertFlag(JSONObject ret, String desc) throws Exception {
		int flag = ret.getInt("flag");
		if (flag != 1) {
			throw new Exception(desc + " flag:" + flag);
		}
	}
	/*
	 * // analysis the plate number from picture public static void
	 * getPlateNo(Object handler, final int code, int[] piex, int width, int
	 * height) { new CommonTask<Object[]>(handler, code) {
	 * 
	 * @Override protected Message execute2(Object[]... para) throws Exception {
	 * 
	 * String plateNo = PlateProc.plateProc((int[]) para[0][0], (Integer)
	 * para[0][1], (Integer) para[0][2]); return obtainMessage(code, plateNo); }
	 * }.execute(new Object[] { piex, width, height }); }
	 */
}
