package com.parking;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
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

public class MainFragment extends Fragment {
	final int REQUEST_CODE_CAPTURE_CAMEIA = 0xff;

	User user;
	@ViewInject(R.id.allCount)
	EditText allParkCount;
	@ViewInject(R.id.yesterdaycash)
	TextView yesterdayCash;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mainLayout = inflater.inflate(R.layout.main_layout2, container,
				false);
		ViewUtils.inject(this, mainLayout);

		user = ((MainActivity) getActivity()).currentUser;
		if (user.getParkSlotNumber() > 0) {
			allParkCount.setText(user.getParkSlotNumber() + "");
			allParkCount.setEnabled(false);
		}
		yesterdayCash
				.setText(String.format("%.2f", user.getTotalForYesterday()));
		return mainLayout;
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
			user.setParkSlotNumber(Integer.parseInt(allParkCount.getText().toString()));
			TaskProcess.updateUserParkSlotNumber(getActivity(),this, R.id.allCount, user);
		}
	}
	@Proc(R.id.allCount)
	void procUpdateParkSlotNumber(Message msg){
		allParkCount.setEnabled(false);
	}
}
