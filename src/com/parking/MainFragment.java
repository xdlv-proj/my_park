package com.parking;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.xdlv.vistor.Proc;

public class MainFragment extends AbstractFragment {
	final int REQUEST_CODE_CAPTURE_CAMEIA = 0xff;

	User user;
	@ViewInject(R.id.allCount)
	EditText allParkCount;
	@ViewInject(R.id.todaycash)
	TextView todayCash;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		user = ((MainActivity) getActivity()).currentUser;
		TaskProcess.getIncomeAndTotalNum(this, R.layout.main_layout2, user.getMobilePhone());
		return view;
	}
	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_layout2, container,
				false);
		ViewUtils.inject(this, view);
		return view;
	}
	
	@Proc({R.layout.main_layout2, -R.layout.main_layout2})
	void procIncomeAndTotalNum(Message msg){
		if (msg.obj instanceof Throwable){
			Toast.makeText(getActivity(), "获取信息失败，请检查网络", Toast.LENGTH_LONG).show();
			return;
		}
		Object[] value = (Object[])msg.obj;
		
		user.setParkSlotNumber((Integer)value[0]);
		user.setTotalForToday(((Double)value[1]).floatValue());
		int totalNum = (Integer)(((Object[])msg.obj)[0]);
		if (totalNum > 0) {
			allParkCount.setText(user.getParkSlotNumber() + "");
			allParkCount.setEnabled(false);
		}
		todayCash
				.setText(String.format("%.2f", user.getTotalForToday()));
	}

	@OnClick(R.id.create_order)
	void onClickOrderCreate(View v) {
		if (allParkCount.getText().toString().length() < 1) {
			Toast.makeText(getActivity(), "请先填写总车位数", Toast.LENGTH_LONG).show();
			return;
		}
		startActivityForResult(
				new Intent("android.media.action.IMAGE_CAPTURE"),
				REQUEST_CODE_CAPTURE_CAMEIA);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK){
			return;
		}
		if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA){
			Bitmap carPic = (Bitmap) data.getExtras().get("data");

			startActivityForResult(
					new Intent(getActivity(), CreateOrderActivity.class)
							.putExtra("user", user).putExtra("pic", carPic), 0xef);
		} else {
			//update the capacity of park station if create order successfully
			int parkCount = Integer.parseInt(allParkCount.getText().toString());
			if (user.getParkSlotNumber() != parkCount){
				user.setParkSlotNumber(parkCount);
				TaskProcess.updateUserParkSlotNumber(getActivity(),this, R.id.allCount, user);
			}
		}
	}
	@Proc(R.id.allCount)
	void procUpdateParkSlotNumber(Message msg){
		allParkCount.setEnabled(false);
	}
	
	@OnClick(R.id.edit_all_count)
	void onLongClickAllCount(View view){
		allParkCount.setEnabled(true);
	}
}
