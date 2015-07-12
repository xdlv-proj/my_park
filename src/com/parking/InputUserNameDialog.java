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

public class InputUserNameDialog extends Dialog implements View.OnClickListener{

	User user;
	CallBack callback;
	IInputDialogTask task;

	public InputUserNameDialog(Context context, User user) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.user = user;
		setContentView(R.layout.input_username_dialog);
		task = ProxyCommonTask.createTaskProxy(InputDialogTask.class, IInputDialogTask.class,(Activity)context, this);
	}

	public InputUserNameDialog init(CallBack callback) {
		((EditText) findViewById(R.id.user_name)).setText(user.getUserName());
		((EditText) findViewById(R.id.id_card)).setText(user.getIdCardNumber());
		findViewById(R.id.confirm_neworder).setOnClickListener(this);
		this.callback = callback;
		return this;
	}

	public void onClick(View view) {
		user.setUserName(((EditText) findViewById(R.id.user_name)).getText().toString());
		user.setIdCardNumber(((EditText) findViewById(R.id.id_card)).getText().toString());
		if (TextUtils.isEmpty(user.getUserName()) || TextUtils.isEmpty(user.getIdCardNumber())){
			Toast.makeText(getContext(), "����д��ȷ����Ϣ", Toast.LENGTH_LONG).show();
		}
		task.updateUserAccountName(0, R.id.confirm_neworder, user);
	}

	@Proc(R.id.confirm_neworder)
	void procConfirNewOrder(Message message) {
		if (message.obj != null && message.obj instanceof Throwable) {
			Toast.makeText(getContext(), "�޸��û�����Ϣʧ��:" + message.obj,
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
