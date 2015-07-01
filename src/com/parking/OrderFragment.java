package com.parking;

import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.parking.OrderListAdaptor.Filter;
import com.parking.OrderListAdaptor.StatusListener;
import com.xdlv.vistor.Proc;

public class OrderFragment extends AbstractFragment{

	@ViewInject(R.id.order_list)
	ListView orderList;

	OrderListAdaptor adaptor;
	int currentIndex = R.id.order_type_0;
	
	@ViewInject(R.id.order_type_0)
	TextView viewType0;
	@ViewInject(R.id.order_type_1)
	TextView viewType1;
	@ViewInject(R.id.order_type_2)
	TextView viewType2;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		TaskProcess.getTodayOrder(getActivity(), this, R.layout.order_layout,
				((MainActivity) getActivity()).currentUser.getMobilePhone());
		return view;
	}
	
	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.order_layout, container, false);
		ViewUtils.inject(this, view);

		adaptor = new OrderListAdaptor(getActivity(), R.layout.order_list_item);
		adaptor.statusListener = new StatusListener() {
			@Override
			public void statusClick(View view, UserOrder uo) {
				new InputMoneyDialog(getActivity(),uo).init(new InputMoneyDialog.CallBack() {
					@Override
					public void postExecute() {
						adaptor.refresh();
					}
				}).show();
			}
		};
		orderList.setAdapter(adaptor);
		return view;
	}

	@SuppressWarnings("unchecked")
	@Proc({ R.layout.order_layout, -R.layout.order_layout })
	void procOrderList(Message message) {
		if (message.obj instanceof Throwable) {
			Toast.makeText(getActivity(), "获取订单列表失败", Toast.LENGTH_LONG).show();
			return;
		}

		adaptor.setData((List<UserOrder>) message.obj);
		adaptor.refresh(null);
	}

	@OnClick({ R.id.order_type_0, R.id.order_type_1, R.id.order_type_2 })
	public void onClickTitle(TextView view) {
		if (currentIndex == view.getId()){
			return;
		}
		unSelect(viewType0);
		unSelect(viewType1);
		unSelect(viewType2);
		if (view.getId() == R.id.order_type_0){
			adaptor.refresh(null);
			select(viewType0);
		} else if (view.getId() == R.id.order_type_1){
			adaptor.refresh(type1);
			select(viewType1);
		} else {
			adaptor.refresh(type2);
			select(viewType2);
		}
		currentIndex = view.getId();
	}
	
	private void unSelect(TextView view){
		view.setTextColor(getResources().getColor(R.color.mainColor));
		view.setBackground(getResources().getDrawable(R.drawable.orderlist_title_bkg2));
	}
	private void select(TextView view){
		view.setTextColor(getResources().getColor(R.color.white));
		view.setBackground(getResources().getDrawable(R.drawable.orderlist_title_bkg));
	}
	
	static Filter type1 = new Filter() {
		@Override
		public boolean accept(UserOrder uo) {
			return uo.getFeeType() == UserOrder.FEE_TYPE_DAY;
		}
	};
	static Filter type2 = new Filter() {
		@Override
		public boolean accept(UserOrder uo) {
			return uo.getFeeType() == UserOrder.FEE_TYPE_TIME;
		}
	};

}
