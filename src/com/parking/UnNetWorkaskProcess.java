package com.parking;

import android.os.Message;

import com.xdlv.vistor.CommonTask;

public class UnNetWorkaskProcess {
	
	public static void waitValidateCode(Object handler, final int code, Integer count) {
		new CommonTask<Integer>(handler, code) {
			@Override
			protected Message execute2(Integer... para) throws Exception {
				Thread.sleep(1000);
				return obtainMessage(code, para[0]-1);
			}
		}.execute(count);
	}
	
	
}
