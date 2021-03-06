package com.parking;

import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parking.task.IInputDialogTask;
import com.parking.task.InputDialogTask;
import com.xdlv.async.task.Proc;
import com.xdlv.async.task.ProxyCommonTask;

public class InputMoneyDialog extends Dialog implements View.OnClickListener{

	UserOrder uo;
	EditText playMoney;
	
	CallBack callback;
	IInputDialogTask task;

	public InputMoneyDialog(Context context, UserOrder uo) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.uo = uo;
		setContentView(R.layout.input_dialog);
		task = ProxyCommonTask.createTaskProxy(InputDialogTask.class, IInputDialogTask.class, (Activity)context, this);
		
	}

	public InputMoneyDialog init(CallBack callback) {
		findViewById(R.id.confirm_neworder).setOnClickListener(this);
		this.callback = callback;
		task.getOrderTimeAndFee(0, R.layout.input_dialog,uo);
		return this;
	}
	
	@Proc({R.layout.input_dialog,-R.layout.input_dialog})
	void procGetTimeAndFee(Message msg){
		if (msg.obj instanceof Throwable){
			Log.e("ERROR", "can not obtian order time and fee");
			Toast.makeText(getContext(), "当前网络不给力，无法提交订单", Toast.LENGTH_LONG).show();
			return;
		}

		int v[] = (int[]) msg.obj;
		((TextView) findViewById(R.id.consume_time)).setText(
				String.format(Locale.getDefault(),"%02d:%02d", (v[0] / 60)/60, (v[0]/60) % 60));
		(playMoney = ((EditText) findViewById(R.id.play_money))).setHint(
				String.format(Locale.getDefault(), "  应收 %d 元",
				v[1]));
		uo.setPrice(v[1]);
		uo.setLeavedTime(System.currentTimeMillis() + v[0] * 1000);
		findViewById(R.id.confirm_neworder).setEnabled(true);
	
	}

	public void onClick(View view) {
		float realPrice;
		if (playMoney.getText().toString().length() < 1) {
			realPrice = uo.getPrice();
		} else {
			realPrice = Float.parseFloat(playMoney.getText().toString());
		}
		uo.setRealPrice(realPrice);
		task.updateUserOrderStatus(0, R.id.confirm_neworder, uo);
	}

	@Proc({R.id.confirm_neworder,-R.id.confirm_neworder})
	void procConfirNewOrder(Message message) {
		if (message.obj != null && message.obj instanceof Throwable) {
			Toast.makeText(getContext(), "修改停车状态失败:" + message.obj,
					Toast.LENGTH_LONG).show();
		}
		if (callback != null){
			callback.postExecute();
		}
		dismiss();
	}
	
	static interface CallBack{
		void postExecute();
	}

}
