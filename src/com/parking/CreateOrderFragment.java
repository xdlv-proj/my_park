package com.parking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
@Deprecated
public class CreateOrderFragment extends Fragment {

	Bitmap pic;
	User userInfo;

	@ViewInject(R.id.fee_type)
	Spinner feeType;

	@ViewInject(R.id.play_money)
	EditText money;

	@ViewInject(R.id.time_neworder)
	TextView timeStamp;
	
	@ViewInject(R.id.car_pic_new)
	ImageView carPic;
	
	@ViewInject(R.id.play_money)
	TextView playMoney;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.new_order, null);
		ViewUtils.inject(this, view);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.spinner);
		adapter.add("����/ÿ��");
		adapter.add("��ʱ/ÿСʱ");
		feeType.setAdapter(adapter);

		carPic.setImageBitmap(pic);
		timeStamp.setText(new SimpleDateFormat("HH:mm", Locale.getDefault())
				.format(new Date()));
		return view;
	}
	
	@OnClick(R.id.back_neworder)
	public void onClickBack(View view){
		((MainActivity)getActivity()).changeTab(0);
	}
	
	@OnClick(R.id.confirm_neworder)
	public void onClickConfirm(View view){
		if (playMoney.getText().toString().length() < 1){
			Toast.makeText(getActivity(), "������д���", Toast.LENGTH_LONG).show();
			return;
		}
		UserOrder order = new UserOrder();
		order.setFeeType(feeType.getSelectedItemPosition() + 1);
		order.setPrice(Float.parseFloat(playMoney.getText().toString()));
		order.setMobilePhone(userInfo.getMobilePhone());
		order.setBitMap(pic);
		TaskProcess.createOrder(getActivity(), this, R.id.confirm_neworder, order);
	}
}
