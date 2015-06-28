package com.parking;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

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
import com.lidroid.xutils.db.sqlite.WhereBuilder;
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
				/*JSONObject ret = getJasonForServer("getValidateCode", new String[][]{
						{"phone", para[0]}
				});*/
				return obtainMessage(code, null);
			}
		}.execute(telphone);
	}
	
	//analysis the plate number from picture
	public static void getPlateNo(Object handler, final int code, int[] piex,
			int width, int height) {
		new CommonTask<Object[]>(handler, code) {
			@Override
			protected Message execute2(Object[]... para) throws Exception {

				String plateNo = PlateProc.plateProc((int[]) para[0][0],
						(Integer) para[0][1], (Integer) para[0][2]);
				return obtainMessage(code, plateNo);
			}
		}.execute(new Object[] { piex, width, height });
	}
	//get local user info
	public static void getLocalUserInfo(final Activity activity,Object handler, final int code){
		new CommonTask<String>(handler, code) {
			@Override
			protected Message execute2(String... para) throws Exception {
				DbUtils db = DbUtils.create(activity);
				User user = db.findFirst(User.class);
				return obtainMessage(code, user);
			}
		}.execute("");
	}
	//login in and save user info to current
	public static void login(final Activity handler,final int code,String telphone, String validate){
		new CommonTask<String>(handler, code) {
			@Override
			protected Message execute2(String... para) throws Exception {
				//TODO get user info from server side
				DbUtils db = DbUtils.create(handler);
				User user = new User();
				user.setMobilePhone("15951928975");
				user.setParkSlotNumber(20);
				user.setAliplay("my@163.com");
				user.setWeiXing("weixing@163.com");
				
				user.setTotalForLastMonth(123454.98f);
				user.setUserName("¬¿œ˛∂¨");
				user.setAuthorizedName("π‹¿Ì‘±");
				user.setIdCardNumber("340321198110104058");
				user.setBankCard("6225880283421876");
				
				user.setLogin(true);
				if (db.findFirst(User.class) != null){
					db.update(user);
				} else {
					db.save(user);
				}
				return obtainMessage(code, user);
			}
		}.execute(telphone,validate);
	}
	
	public static void quiteLogin(final Activity activity, Object handler, final int code, User user){
		new CommonTask<User>(handler, code) {
			@Override
			protected Message execute2(User... para) throws Exception {
				DbUtils db = DbUtils.create(activity);
				db.update(para[0], "login");
				return obtainMessage(code, null);
			}
		}.execute(user);
	}
	//create order
	public static void createOrder(final Activity activity, final Object handler
			, final int code, UserOrder order){
		new CommonTask<UserOrder>(handler, code) {
			@Override
			protected Message execute2(UserOrder... para) throws Exception {
				DbUtils db = DbUtils.create(activity);
				UserOrder order = para[0];
				//convert picture to byte array
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				order.getBitMap().compress(CompressFormat.PNG, 50, baos);
				order.setPicData(baos.toByteArray());
				baos.close();
				//
				// TODO String plateCode = PlateProc.plateProc(null,0,0);
				order.setPlateCode(String.format("À’A %05d", Math.abs(new Random().nextInt(99999))));
				db.save(order);
				return obtainMessage(code, null);
			}
		}.execute(order);
	}
	
	//get the max number of order for primary key
	public static void getCurrentOrderMax(final  Activity activity,
			final int code, String userTel) {
		new CommonTask<String>(activity, code) {
			@Override
			protected Message execute2(String... para) throws Exception {
				DbUtils db = DbUtils.create(activity);
				int max = 0;
				if (db.tableIsExist(UserOrder.class)){
					long currentTime = getTodayStartTime();
					Cursor cursor = db.execQuery("select max(orderId) from UserOrder where createTime>"+currentTime 
							+ " and mobilePhone='" + para[0] +"'");
					while (cursor.moveToNext()){
						max = cursor.getInt(0);
						break;
					}
					cursor.close();
				}
				return obtainMessage(code, max);
			}
		}.execute(userTel);
	}

	public static void getTodayOrder(final  Activity activity,final Object handler,
			final int code, String userTel) {
		new CommonTask<String>(handler, code) {
			@Override
			protected Message execute2(String... para) throws Exception {
				DbUtils db = DbUtils.create(activity);
				
				//obtain all orders with status = PARR
				List<UserOrder> orders = db.findAll(Selector.from(UserOrder.class
						).where(WhereBuilder.b("createTime",">", getTodayStartTime()
								).or("status", "=", UserOrder.PARK_STATUS)
						).and("mobilePhone", "=", para[0]).orderBy("createTime", true));
				orders = orders == null ? new ArrayList<UserOrder>() : orders; 
				for (UserOrder uo : orders){
					uo.setBitMap(BitmapFactory.decodeByteArray(uo.getPicData(), 0, uo.getPicData().length));
				}
				return obtainMessage(code, orders);
			}
		}.execute(userTel);
	}
	
	public static void updateUserParkSlotNumber(final  Activity activity,final Object handler,
			final int code, User user) {
		new CommonTask<User>(handler, code) {
			@Override
			protected Message execute2(User... para) throws Exception {
				//TODO update the capacity to server side
				DbUtils db = DbUtils.create(activity);
				db.execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(db, para[0], "parkSlotNumber"));
				return obtainMessage(code, null);
			}
		}.execute(user);
	}
	
	public static void updateUserOrderStatus(final Context context,Object handler,
			final int code, final UserOrder uo) {
		new CommonTask<UserOrder>(handler, code) {
			@Override
			protected Message execute2(UserOrder... para) throws Exception {
				DbUtils db = DbUtils.create(context);
				uo.setStatus(UserOrder.LEAVED_STATUS);
				uo.setLeavedTime(System.currentTimeMillis());
				db.execNonQuery(SqlInfoBuilder.buildUpdateSqlInfo(db, para[0], "realPrice","status","leavedTime"));
				return obtainMessage(code, null);
			}
		}.execute(uo);
	}
	
	static final String host = "http://localhost:4000/ParkHere/";
	static final HttpUtils httpUtils = new HttpUtils(1000 * 5);
	static JSONObject getJasonForServer(String url, String[][] paras)throws Exception{
		ResponseStream resp = null;
		try {
			RequestParams requestParams = new RequestParams("UTF-8");
			for (int i = 0;paras != null && i < paras.length;i++){
				requestParams.addBodyParameter(paras[i][0], paras[i][1]);
			}
			resp = httpUtils.sendSync(HttpMethod.POST, url,requestParams);
			return new JSONObject(resp.readString());
		} finally {
			resp.close();
		}
	}
	
	private static long getTodayStartTime() throws ParseException{
		String current = sdf.format(new Date());
		return sdf.parse(current).getTime();
	}
}
