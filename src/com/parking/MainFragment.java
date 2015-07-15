package com.parking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.parking.task.IMainTask;
import com.parking.task.MainTask;
import com.xdlv.async.task.Proc;
import com.xdlv.async.task.ProxyCommonTask;

public class MainFragment extends AbstractFragment {
	final int REQUEST_CODE_CAPTURE_CAMEIA = 0xff;
	final int REQUEST_CODE_CREATE_ORDER = 0xef;

	User user;
	@ViewInject(R.id.allCount)
	EditText allParkCount;
	@ViewInject(R.id.freeCount)
	EditText freeCount;
	@ViewInject(R.id.todaycash)
	TextView todayCash;
	@ViewInject(R.id.edit_all_count)
	Button editButton;

	IMainTask task = null;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		user = ((MainActivity) getActivity()).currentUser;
		if (task == null){
			task = ProxyCommonTask.createTaskProxy(MainTask.class, IMainTask.class, getActivity(), this);
		}
		task.getIncomeAndTotalNum(0, R.layout.main_layout2,
				user.getMobilePhone());
		return view;
	}

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_layout2, container, false);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		task.cancle(R.layout.main_layout2);
	}

	@Proc({ R.layout.main_layout2, -R.layout.main_layout2 })
	void procIncomeAndTotalNum(Message msg) {
		if (msg.obj instanceof Throwable) {
			Toast.makeText(getActivity(), "��ȡ��Ϣʧ�ܣ���������", Toast.LENGTH_LONG).show();
			return;
		}
		Object[] value = (Object[]) msg.obj;

		user.setParkSlotNumber((Integer) value[0]);
		user.setTotalForToday(((Double) value[1]).floatValue());
		user.setFreeCount((Integer) value[2]);
		int totalNum = (Integer) (((Object[]) msg.obj)[0]);
		if (totalNum > 0) {
			allParkCount.setText(user.getParkSlotNumber() + "");
			allParkCount.setEnabled(false);
		} else {
			editButton.setText(getString(R.string.confirm2));
		}
		todayCash.setText(String.format("%.1f", user.getTotalForToday()));
		
		refreshFreeCount();
	}
	@OnClick({R.id.refresh_data})
	public void onClickFresh(View v){
		task.getIncomeAndTotalNum(0, R.layout.main_layout2,
				user.getMobilePhone());
	}

	@OnClick({R.id.create_order,R.id.take_photo})
	public void onClickOrderCreate(View v) {
		if (allParkCount.getText().toString().length() < 1) {
			Toast.makeText(getActivity(), "������д�ܳ�λ��", Toast.LENGTH_LONG).show();
			return;
		}
		if (user.getFreeCount() < 1){
			Toast.makeText(getActivity(), "��ǰ���޿ճ�λ", Toast.LENGTH_LONG).show();
			return;
		}
		startActivityForResult(new Intent(getActivity(),CapturePicActivity.class),
				REQUEST_CODE_CAPTURE_CAMEIA);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		
		if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
			String path = data.getExtras().getString("path");
			
			startActivityForResult(
					new Intent(getActivity(), CreateOrderActivity.class).putExtra("user", user
							).putExtra("path", path), REQUEST_CODE_CREATE_ORDER);
		}
		if (requestCode == REQUEST_CODE_CREATE_ORDER){
			user.setFreeCount(user.getFreeCount() - 1);
			refreshFreeCount();
		}
	}
	
	private void refreshFreeCount(){
		freeCount.setText(String.valueOf(user.getFreeCount()));
	}

	@Proc(R.id.allCount)
	void procUpdateParkSlotNumber(Message msg) {
		allParkCount.setEnabled(false);
	}

	@OnClick(R.id.edit_all_count)
	void onLongClickAllCount(View view) {
		if (editButton.getText().equals(getString(R.string.modify))){
			allParkCount.setEnabled(true);
			allParkCount.requestFocus();
			editButton.setText(getString(R.string.confirm2));
		} else {
			int parkCount = Integer.parseInt(allParkCount.getText().toString());
			if (user.getParkSlotNumber() != parkCount) {
				user.setParkSlotNumber(parkCount);
				task.updateUserParkSlotNumber(0, R.id.edit_all_count, user);
			}
			allParkCount.setEnabled(false);
			editButton.setText(getString(R.string.modify));
		}
	}
	@Proc(R.id.edit_all_count)
	void procEditAllCount(Message msg){
		if (msg.obj instanceof Throwable){
			Toast.makeText(getActivity(), "�޸�ʧ��", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getActivity(), "�޸ĳɹ�", Toast.LENGTH_LONG).show();
			task.getIncomeAndTotalNum(0, R.layout.main_layout2,
					user.getMobilePhone());
		}
	}
}
