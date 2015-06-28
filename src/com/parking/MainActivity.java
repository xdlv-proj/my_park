package com.parking;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.view.Window;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MainActivity extends FragmentActivity implements OnTabChangeListener{
	@ViewInject(android.R.id.tabhost)
	FragmentTabHost mTabHost;
	User currentUser;

	private Class<?>[] fragmentArray = { MainFragment.class,
			OrderFragment.class, AccountFragment.class };

	private int mImageViewArray[] = { R.drawable.main_tabhost,
			R.drawable.order_tabhost, R.drawable.account_tabhost };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main2);
		ViewUtils.inject(this);

		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		for (int i = 0; i < fragmentArray.length; i++) {
			TabSpec tabSpec = mTabHost.newTabSpec(fragmentArray[i].getName()).setIndicator(getView(i));
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			mTabHost.getTabWidget().getChildAt(i)
					.setBackgroundResource(mImageViewArray[i]);
		}
		mTabHost.setOnTabChangedListener(this);
		
		currentUser = (User)getIntent().getExtras().get("user");
	}
		
	void changeTab(int index){
		mTabHost.setCurrentTab(index);
	}
	
	private View getView(int index){
		TextView view = new TextView(this);
		return view;
	}

	@Override
	public void onTabChanged(String arg0) {
		
	}
}