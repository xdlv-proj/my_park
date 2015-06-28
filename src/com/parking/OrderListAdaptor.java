package com.parking;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderListAdaptor extends ArrayAdapter<UserOrder> {

	UserOrder[] allData;
	int resource;
	Filter filter;
	static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm",
			Locale.getDefault());

	StatusListener statusListener;

	public OrderListAdaptor(Context context, int resource) {
		super(context, resource);
		this.resource = resource;
	}

	public void setData(List<UserOrder> data) {
		allData = data.toArray(new UserOrder[data.size()]);
	}

	public void refresh(Filter filter) {
		clear();
		Arrays.sort(allData, comparator);
		for (UserOrder uo : allData) {
			if (filter == null || (filter != null && filter.accept(uo))) {
				add(uo);
			}
		}
		notifyDataSetChanged();
		this.filter = filter;
	}
	public void refresh(){
		refresh(this.filter);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final UserOrder order = getItem(position);
		View view = LayoutInflater.from(getContext()).inflate(
				order.getStatus() == UserOrder.PARK_STATUS ? resource
						: R.layout.order_list_item2, null);

		((ImageView) view.findViewById(R.id.car_pic)).setImageBitmap(order
				.getBitMap());
		// 京A 209870\n06-01 11:23

		((TextView) view.findViewById(R.id.car_plate)).setText(order
				.getPlateCode()
				+ "\n"
				+ sdf.format(new Date(order.getCreateTime())));

		((TextView) view.findViewById(R.id.fee_type)).setText(order
				.getFeeType() == UserOrder.FEE_TYPE_TIME ? "按时" : "全天");
		((TextView) view.findViewById(R.id.price)).setText(String.format(
				"￥%.1f", order.getPrice()));

		if (order.getStatus() == UserOrder.PARK_STATUS) {
			view.findViewById(R.id.status_leaved).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							if (statusListener != null) {
								statusListener.statusClick(view, order);
							}
						}
					});
		} else {
			((TextView) view.findViewById(R.id.park_time)).setText(String
					.format("停车%s  己离开", order.getLTime(false)));
		}

		return view;
	}
	
	static Comparator<UserOrder> comparator = new Comparator<UserOrder>() {
		@Override
		public int compare(UserOrder arg0, UserOrder arg1) {
			if (arg0.getStatus() == arg1.getStatus()){
				return arg0.getCreateTime() - arg1.getCreateTime() > 0 ? -1 : 1;
			}
			return arg0.getStatus() - arg1.getStatus();
		}
	};

	public static interface StatusListener {
		void statusClick(View view, UserOrder uo);
	}

	public static interface Filter {
		boolean accept(UserOrder uo);
	}

}
