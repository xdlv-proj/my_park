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
import com.xdlv.vistor.Proc;

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
	@ViewInject(R.id.today_income)
	TextView todayIncome;
	@ViewInject(R.id.last_month_income)
	TextView lastMonthIncome;

	User currentUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		currentUser = ((MainActivity) getActivity()).currentUser;
		TaskProcess.queryUserInfo(getActivity(),this,R.layout.accout_layout2,
				((MainActivity) getActivity()).currentUser.getMobilePhone());
		return view;
	}
	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.accout_layout2, container, false);
		ViewUtils.inject(this, view);
		return view;
	}
	@Proc({R.layout.accout_layout2, -R.layout.accout_layout2})
	void procQueryUserInfo(Message msg){
		if (msg.obj instanceof Throwable){
			Toast.makeText(getActivity(), "获取用户信息失败", Toast.LENGTH_LONG).show();
			return;
		}
		initValue((User)msg.obj);
	}

	@OnClick(R.id.quite_login)
	public void quiteLogin(View view) {
		currentUser.setLogin(false);
		TaskProcess.quiteLogin(getActivity(), this, R.id.quite_login, currentUser);
	}

	@Proc(R.id.quite_login)
	void procQuiteLogin(Message message) {
		startActivity(new Intent(getActivity(), LoginActivity.class)
				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

		getActivity().finish();
	}

	void initValue(User user) {
		userName.setText(user.getUserName());
		authroizedName.setText(user.getAuthorizedName());

		idCard.setText(mask(user.getIdCardNumber(), 4, 4));
		alipay.setText(user.getAliplay());
		weiXing.setText(user.getWeiXing());
		bankCard.setText(mask(user.getBankCard(), 2, 6));
		todayIncome.setText(user.getTotalForToday() + "");
		lastMonthIncome.setText(user.getTotalForLastMonth() + "");
	}

	String mask(String content, int front, int last) {
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
