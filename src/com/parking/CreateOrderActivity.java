package com.parking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.parking.task.CreateOrderTask;
import com.xdlv.async.task.Proc;

public class CreateOrderActivity extends Activity {

	Bitmap bitMap;
	User userInfo;
	@ViewInject(R.id.fee_type)
	Spinner feeType;

	@ViewInject(R.id.play_money)
	EditText money;

	@ViewInject(R.id.time_neworder)
	TextView timeStamp;
	
	@ViewInject(R.id.car_no)
	TextView carNo;
	
	@ViewInject(R.id.car_pic_new)
	ImageView carPic;
	
	@ViewInject(R.id.play_money)
	TextView playMoney;
	
	@ViewInject(R.id.car_num)
	EditText carNumber;
	@ViewInject(R.id.confirm_neworder)
	Button createNewOrderButton;
	
	private LocationMg locationMg;
	private CreateOrderTask task = new CreateOrderTask(this, this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_order);
		ViewUtils.inject(this);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.spinner);
		adapter.add("按时/每小时");
		adapter.add("按次/每天");
		feeType.setAdapter(adapter);
		
		Intent intent = getIntent();
		bitMap = (Bitmap)intent.getExtras().get("pic");
		userInfo = (User)intent.getExtras().get("user");
		
		carPic.setImageBitmap(bitMap);
		timeStamp.setText(new SimpleDateFormat("HH:mm", Locale.getDefault())
				.format(new Date()));
		task.request("getCurrentOrderMax", 0, R.layout.new_order, userInfo.getMobilePhone());
		locationMg = new LocationMg(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		locationMg.destory();
	}
	
	@Proc({R.layout.new_order ,-R.layout.new_order})
	void procOrderMax(Message message){
		if (message.obj instanceof Throwable){
			Toast.makeText(this, "当前没有订单信息", Toast.LENGTH_LONG).show();
			return;
		}
		int max = ((Integer)message.obj) + 1;
		carNo.setText(String.format("%03d", max));
		
	}
	@OnClick(R.id.back_neworder)
	public void onClickBack(View view){
		finish();
	}
	
	@OnClick(R.id.confirm_neworder)
	public void onClickConfirm(View view){
		if (playMoney.getText().toString().length() < 1){
			Toast.makeText(this, "必须填写金额", Toast.LENGTH_LONG).show();
			return;
		}
		if (carNumber.getText().toString().length() < 1){
			Toast.makeText(this, "必须填写车牌号", Toast.LENGTH_LONG).show();
			return;
		}
		createNewOrderButton.setEnabled(false);
		UserOrder order = new UserOrder();
		order.setFeeType(feeType.getSelectedItemPosition() + 1);
		order.setPrice(Float.parseFloat(playMoney.getText().toString()));
		order.setMobilePhone(userInfo.getMobilePhone());
		order.setBitMap(bitMap);
		order.setPlateCode(carNumber.getText().toString());
		Location location = locationMg.getLocation();
		if (location != null){
			order.setLng(location.getLongitude());
			order.setLat(location.getLatitude());
		}
		task.request("createOrder", 0, R.id.confirm_neworder, order);
	}
	@Proc({R.id.confirm_neworder, -R.id.confirm_neworder})
	void procNewOrder(Message msg){
		createNewOrderButton.setEnabled(true);
		if (msg.obj instanceof Throwable){
			Toast.makeText(this, "订单生成失败", Toast.LENGTH_LONG).show();
			return;
		}
		setResult(RESULT_OK);
		finish();
	}
}
