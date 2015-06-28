package com.parking;

public class PlateProc {

	static{
		System.loadLibrary("plate_proc");
	}
	native public static String plateProc(int[] piex, int width, int height); 
}
