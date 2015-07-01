package com.parking;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationMg {

	private Location location;
	private LocationManager locMgr;
	private LocationListener locationListener;

	public LocationMg(Context context) {
		locMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (!locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationListener = new LocationListener() {
				@Override
				public void onLocationChanged(Location arg0) {
					location = arg0;
				}

				@Override
				public void onProviderDisabled(String arg0) {

				}

				@Override
				public void onProviderEnabled(String arg0) {

				}

				@Override
				public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

				}

			};
			locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
			location = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}

	}
	
	public void destory(){
		if (locationListener != null){
			locMgr.removeUpdates(locationListener);
		}
	}

	public Location getLocation() {
		if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			location = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		return location;

	}
}
