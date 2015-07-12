package com.parking;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.parking.task.IInputDialogTask;
import com.parking.task.InputDialogTask;
import com.xdlv.async.task.Proc;
import com.xdlv.async.task.ProxyCommonTask;

public class InputUserCountDialog extends Dialog implements View.OnClickListener{

	User user;
	CallBack callback;
	IInputDialogTask task;

	public InputUserCountDialog(Context context, User user) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.user = user;
		setContentView(R.layout.input_usercount_dialog);
		task = ProxyCommonTask.createTaskProxy(InputDialogTask.class,
				IInputDialogTask.class, (Activity)context, this);
	}

	public InputUserCountDialog init(CallBack callback) {
		((EditText) findViewById(R.id.weixing)).setText(user.getWeiXing());
		((EditText) findViewById(R.id.alipay)).setText(user.getAliplay());
		((EditText) findViewById(R.id.bank_card)).setText(user.getBankCard());
		findViewById(R.id.confirm_neworder).setOnClickListener(this);
		this.callback = callback;
		return this;
	}

	public void onClick(View view) {
		user.setWeiXing(((EditText) findViewById(R.id.weixing)).getText().toString());
		user.setAliplay(((EditText) findViewById(R.id.alipay)).getText().toString());
		user.setBankCard(((EditText) findViewById(R.id.bank_card)).getText().toString());
		if (TextUtils.isEmpty(user.getUserName()) || TextUtils.isEmpty(user.getIdCardNumber())){
			Toast.makeText(getContext(), "请填写正确的信息", Toast.LENGTH_LONG).show();
		}
		task.updateUserAccountName(0, R.id.confirm_neworder, user);
	}

	@Proc(R.id.confirm_neworder)
	void procConfirNewOrder(Message message) {
		if (message.obj != null && message.obj instanceof Throwable) {
			Toast.makeText(getContext(), "修改用户名信息失败:" + message.obj,
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
