package com.parking;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.xdlv.vistor.Proc;

public class LoginActivity extends Activity {
	Uri SMS_URI = Uri.parse("content://sms/");
	
	//private SmsObserver smsObserver = new SmsObserver(null);

	@ViewInject(R.id.mobile_number)
	EditText mobilePhone;
	@ViewInject(R.id.validate_code)
	EditText validateCode;

	@ViewInject(R.id.login_button)
	Button loginButton;
	@ViewInject(R.id.get_validate_code)
	Button validateCodeButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		ViewUtils.inject(this);
		
		TaskProcess.getLocalUserInfo(this,this, R.layout.login);
	}
	@Proc(R.layout.login)
	void procGetLocalUserInfo(Message msg) {
		TextWatcher textWatcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				loginButton.setEnabled(validateCode.getText().toString()
						.length() == 6
						&& mobilePhone.getText().toString().length() == 11);
				//validateCodeButton.setEnabled(mobilePhone.getText().toString().length() == 11);
			}
		};
		validateCode.addTextChangedListener(textWatcher);
		mobilePhone.addTextChangedListener(textWatcher);
		
		if (msg.obj != null) {
			User user = (User) msg.obj;
			if (user.isLogin()){
				startMainUi(user);
			} else {
				mobilePhone.setText(user.getMobilePhone());
			}
		}
	}

	@OnClick(R.id.login_button)
	public void onLogin(View view) {
		TaskProcess.login(this, R.id.login_button, mobilePhone.getText()
				.toString(), validateCode.getText().toString());
		loginButton.setEnabled(false);
	}

	@Proc({R.id.login_button,-R.id.login_button})
	void procLogin(Message msg) {
		loginButton.setEnabled(true);
		if (msg.obj instanceof Throwable) {
			Toast.makeText(this, "登陆失败，请重新获取验证码", Toast.LENGTH_LONG).show();
			return;
		}
		startMainUi((User)msg.obj);
	}
	void startMainUi(User user){
		startActivity(new Intent(this,MainActivity.class).setFlags(
				Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("user", user));
		finish();
	}
	
	@OnClick(R.id.get_validate_code)
	public void onClickValidateCode(View view){
		TaskProcess.getValidate(this, R.id.get_validate_code, mobilePhone.getText().toString().trim());
		//registry SMS observer
	    //getContentResolver().registerContentObserver(SMS_URI, true, smsObserver);
	}
	@Proc({R.id.get_validate_code, -R.id.get_validate_code})
	void procValidateCode(Message msg){
		if (msg.obj instanceof Throwable){
			Toast.makeText(this, "获取验证码失败，请检查网络", Toast.LENGTH_LONG).show();
			validateCodeButton.setEnabled(true);
			return;
		}
		validateCodeButton.setEnabled(false);
		int start = 60;
		validateCodeButton.setText(getString(R.string.get_validate_code_last, start));
		UnNetWorkaskProcess.waitValidateCode(this, R.id.validate_code, start);
	}
	@Proc({R.id.validate_code})
	void procWaitValidateCode(Message msg){
		Integer count = (Integer)msg.obj;
		if (count < 1){
			validateCodeButton.setEnabled(true);
			validateCodeButton.setText(getString(R.string.get_validate_code));
			//getContentResolver().unregisterContentObserver(smsObserver);
		} else {
			UnNetWorkaskProcess.waitValidateCode(this, R.id.validate_code, count);
			validateCodeButton.setText(getString(R.string.get_validate_code_last, count));
		}
	}
	
	void obtainValidateCode(String code){
		validateCode.setText(code);
		//getContentResolver().unregisterContentObserver(smsObserver);
	}
	
	class SmsObserver extends ContentObserver{
		Pattern pattern = Pattern.compile("[0-9]{6}");
		public SmsObserver(Handler handler) {
			super(handler);
		}
		@Override
		public void onChange(boolean selfChange) {
			ContentResolver cr = getContentResolver();  
	        String[] projection = new String[] { "body" };//"_id", "address", "person",, "date", "type
	        String where = " address = '15951928735' AND date >  "  
	                + (System.currentTimeMillis() - 60 * 10 * 1000);  
	        Cursor cur = cr.query(SMS_URI, projection, where, null, "date desc");
	        if (cur == null || !cur.moveToFirst()){
	        	return;
	        }
	        String body = cur.getString(cur.getColumnIndex("body"));
	        Matcher matcher = pattern.matcher(body);
			if (matcher.find()){
				validateCode.setText(matcher.group(1));
			}
	        cur.close(); 
		}
		
	}
	
}
