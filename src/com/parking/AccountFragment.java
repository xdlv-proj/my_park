package com.parking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.xdlv.vistor.Proc;

public class AccountFragment extends Fragment {

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

	User user;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.accout_layout2, container, false);
		ViewUtils.inject(this, view);
		user = ((MainActivity) getActivity()).currentUser;
		initValue();
		return view;
	}

	@OnClick(R.id.quite_login)
	public void quiteLogin(View view) {
		user.setLogin(false);
		TaskProcess.quiteLogin(getActivity(), this, R.id.quite_login, user);
	}

	@Proc(R.id.quite_login)
	void procQuiteLogin(Message message) {
		startActivity(new Intent(getActivity(), LoginActivity.class)
				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

		getActivity().finish();
	}

	void initValue() {
		userName.setText(user.getUserName());
		authroizedName.setText(user.getAuthorizedName());

		idCard.setText(mask(user.getIdCardNumber(), 4, 4));
		alipay.setText(user.getAliplay());
		weiXing.setText(user.getWeiXing());
		bankCard.setText(mask(user.getBankCard(), 2, 6));
		todayIncome.setText(user.getTotalForLastMonth() + "");
		lastMonthIncome.setText(user.getTotalForLastMonth() + "");
	}

	String mask(String content, int front, int last) {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(content, 0, front);
		for (int i = 0; i < content.length() - front - last; i++) {
			sBuffer.append("*");
		}
		sBuffer.append(content, content.length() - last, content.length());
		return sBuffer.toString();
	}

}
