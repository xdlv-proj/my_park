package com.parking;

import java.io.Serializable;
import java.util.Locale;

import android.graphics.Bitmap;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "UserOrder")
public class UserOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int PARK_STATUS = 1, LEAVED_STATUS = 2;
	public static final int FEE_TYPE_DAY = 1, FEE_TYPE_TIME = 2;

	@Id(column = "orderId")
	private int orderId;
	@Column(column = "mobilePhone")
	private String mobilePhone;
	@Column(column = "feeType")
	private int feeType;

	@Column(column = "price")
	private float price;

	@Column(column = "picData")
	private byte[] picData;

	@Column(column = "status")
	private int status = 1;

	@Column(column = "createTime")
	private long createTime = System.currentTimeMillis();

	@Column(column = "leavedTime")
	private long leavedTime;

	@Column(column = "plateCode")
	private String plateCode;
	
	@Column(column = "realPrice")
	private float realPrice;

	private Bitmap bitMap;

	public String getLTime(boolean forNow) {
		long parkTime = ((forNow ? System.currentTimeMillis() : getLeavedTime()) - getCreateTime()) / (1000 * 60);
		return String.format(Locale.getDefault(),"%02d:%02d", parkTime / 60, parkTime % 60);
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public int getFeeType() {
		return feeType;
	}

	public void setFeeType(int feeType) {
		this.feeType = feeType;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public byte[] getPicData() {
		return picData;
	}

	public void setPicData(byte[] picData) {
		this.picData = picData;
	}

	public void setBitMap(Bitmap bitMap) {
		this.bitMap = bitMap;
	}

	public Bitmap getBitMap() {
		return bitMap;
	}

	public String getPlateCode() {
		return plateCode;
	}

	public void setPlateCode(String plateCode) {
		this.plateCode = plateCode;
	}

	public long getLeavedTime() {
		return leavedTime;
	}

	public void setLeavedTime(long leavedTime) {
		this.leavedTime = leavedTime;
	}

	public float getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(float realPrice) {
		this.realPrice = realPrice;
	}
	
}
