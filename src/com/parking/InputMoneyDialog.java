package com.parking;

import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.xdlv.vistor.Proc;

public class InputMoneyDialog extends Dialog implements View.OnClickListener{

	UserOrder uo;
	@ViewInject(R.id.play_money)
	EditText playMoney;
	
	CallBack callback;

	public InputMoneyDialog(Context context, UserOrder uo) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.uo = uo;
		setContentView(R.layout.input_dialog);
	}

	public InputMoneyDialog init(CallBack callback) {

		((TextView) findViewById(R.id.consume_time)).setText(uo.getLTime(true));

		(playMoney = ((EditText) findViewById(R.id.play_money))).setHint(String.format(Locale.getDefault(), "  应收 %.1f",
				uo.getPrice()));
		findViewById(R.id.confirm_neworder).setOnClickListener(this);
		this.callback = callback;
		return this;
	}

	public void onClick(View view) {
		float realPrice;
		if (playMoney.getText().toString().length() < 1) {
			realPrice = uo.getPrice();
		} else {
			realPrice = Float.parseFloat(playMoney.getText().toString());
		}
		uo.setRealPrice(realPrice);
		TaskProcess.updateUserOrderStatus(getContext(), this,
				R.id.confirm_neworder, uo);
	}

	@Proc(R.id.confirm_neworder)
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
