package com.parking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.parking.task.AccountTask;
import com.parking.task.IAccountTask;
import com.xdlv.async.task.Proc;
import com.xdlv.async.task.ProxyCommonTask;

public class AccountFragment extends AbstractFragment {

	@ViewInject(R.id.user_name)
	TextView userName;
	@ViewInject(R.id.authroized_name)
	TextView authroizedName;
	@ViewInject(R.id.id_card)
	TextView idCard;
	@ViewInject(R.id.aliplay)
	TextView alipay;
	@ViewInject(R.id.wei_xing)
	TextView weiXing;
	@ViewInject(R.id.bank_card)
	TextView bankCard;
	@ViewInject(R.id.month_income)
	TextView todayIncome;
	@ViewInject(R.id.last_month_income)
	TextView lastMonthIncome;

	User queryUser;
	private IAccountTask task;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		if (task == null){
			task = ProxyCommonTask.createTaskProxy(
					AccountTask.class,IAccountTask.class, getActivity(), this);
		}
		task.queryUserInfo(inflaterView ? 0 : 3, R.layout.accout_layout2,
				((MainActivity) getActivity()).currentUser.getMobilePhone());
		return view;
	}
	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.accout_layout2, container, false);
		ViewUtils.inject(this, view);
		return view;
	}
	@Override
	public void onPause() {
		super.onPause();
		task.cancle(R.layout.accout_layout2);
	}
	
	@Proc({R.layout.accout_layout2, -R.layout.accout_layout2})
	void procQueryUserInfo(Message msg){
		if (msg.obj instanceof Throwable){
			Toast.makeText(getActivity(), "获取用户信息失败", Toast.LENGTH_LONG).show();
			return;
		}
		queryUser = (User)msg.obj;
		initValue();
	}

	@OnClick(R.id.quite_login)
	public void quiteLogin(View view) {
		((MainActivity) getActivity()).currentUser.setLogin(false);
		task.quiteLogin(0, R.id.quite_login, 
				((MainActivity) getActivity()).currentUser);
	}

	@Proc(R.id.quite_login)
	void procQuiteLogin(Message message) {
		startActivity(new Intent(getActivity(), LoginActivity.class)
				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		getActivity().finish();
	}

	void initValue() {
		userName.setText(queryUser.getUserName());
		authroizedName.setText(queryUser.getAuthorizedName());

		idCard.setText(mask(queryUser.getIdCardNumber(), 4, 4));
		alipay.setText(queryUser.getAliplay());
		weiXing.setText(queryUser.getWeiXing());
		bankCard.setText(mask(queryUser.getBankCard(), 2, 6));
		todayIncome.setText(queryUser.getTotalForToday() + "");
		lastMonthIncome.setText(queryUser.getTotalForLastMonth() + "");
	}
	@OnClick(R.id.user_edit)
	void editUserName(View view){
		new InputUserNameDialog(getActivity(),queryUser).init(new InputUserNameDialog.CallBack() {
			@Override
			public void postExecute() {
				userName.setText(queryUser.getUserName());
				idCard.setText(mask(queryUser.getIdCardNumber(), 4, 4));
			}
		}).show();
	}
	@OnClick(R.id.user_account_edit)
	void editUserCountName(View view){
		new InputUserCountDialog(getActivity(),queryUser).init(new InputUserCountDialog.CallBack() {
			@Override
			public void postExecute() {
				alipay.setText(queryUser.getAliplay());
				weiXing.setText(queryUser.getWeiXing());
				bankCard.setText(mask(queryUser.getBankCard(), 2, 6));
			}
		}).show();
	}

	private String mask(String content, int front, int last) {
		if (content == null || content.length() < front + last){
			return "";
		}
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(content, 0, front);
		for (int i = 0; i < content.length() - front - last; i++) {
			sBuffer.append("*");
		}
		sBuffer.append(content, content.length() - last, content.length());
		return sBuffer.toString();
	}

}
