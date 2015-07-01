package com.parking;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class AbstractFragment extends Fragment {

	private View rootView = null;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView == null){
			rootView = createView(inflater, container, savedInstanceState);
		}
		ViewGroup viewGroup = (ViewGroup)rootView.getParent();
		if (viewGroup != null){
			viewGroup.removeView(rootView);
		}
		return rootView;
	}
	
	protected abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
	
}
