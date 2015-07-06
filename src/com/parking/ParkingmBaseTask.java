package com.parking;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.app.Activity;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseStream;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.xdlv.async.task.CommonProcess;

public class ParkingmBaseTask extends CommonProcess {
	static final String host = "http://223.68.191.71:48080/ParkingHere/mobile/";
	static final HttpUtils httpUtils = new HttpUtils(1000 * 100);
	static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

	public ParkingmBaseTask(Activity context, Object handler) {
		super(context, handler);
	}
	
	protected JSONObject getJasonForServer(String url, String[][] paras) throws Exception {
		RequestParams requestParams = requestParams(paras);
		return getJasonForServer(url, requestParams);

	}

	protected JSONObject getJasonForServer(String url, RequestParams requestParams) throws Exception {
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

	protected RequestParams requestParams(String[][] paras) {
		RequestParams requestParams = new RequestParams("UTF-8");
		for (int i = 0; paras != null && i < paras.length; i++) {
			requestParams.addBodyParameter(paras[i][0], paras[i][1]);
		}
		return requestParams;
	}

	protected long getTodayStartTime() throws ParseException {
		String current = sdf.format(new Date());
		return sdf.parse(current).getTime();
	}

	protected void assertFlag(JSONObject ret, String desc) throws Exception {
		int flag = ret.getInt("flag");
		if (flag != 1) {
			throw new Exception(desc + " flag:" + flag);
		}
	}
	
	protected DbUtils getDbUtils(){
		return DbUtils.create(context);
	}

}
