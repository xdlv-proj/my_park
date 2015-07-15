package com.parking;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "User")
public class User implements Serializable {
	private static final long serialVersionUID = 7475707710247689128L;
	
	@Id(column = "mobilePhone")
	private String mobilePhone;
	@Column(column = "parkSlotNumber")
	private int parkSlotNumber;
	@Column(column="totalForYesterday")
	private float totalForYesterday = 0f;
	@Column(column="userName")
	private String userName;
	@Column(column="authorizedName")
	private String authorizedName;
	@Column(column="idCardNumber")
	private String idCardNumber;
	@Column(column="aliplay")
	private String aliplay;
	@Column(column="weiXing")
	private String weiXing;
	@Column(column="bankCard")
	private String bankCard;
	
	@Column(column="totalForToday")
	private float totalForToday;
	@Column(column="totalForLastMonth")
	private float totalForLastMonth;
	
	@Column(column="login")
	private boolean login = false;
	@Column(column="lastLoginTime")
	private long lastLoginTime;
	
	//@Column(column="freeCount")
	private int freeCount;

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public int getParkSlotNumber() {
		return parkSlotNumber;
	}

	public void setParkSlotNumber(int parkSlotNumber) {
		this.parkSlotNumber = parkSlotNumber;
	}

	public float getTotalForYesterday() {
		return totalForYesterday;
	}

	public void setTotalForYesterday(float totalForYesterday) {
		this.totalForYesterday = totalForYesterday;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAuthorizedName() {
		return authorizedName;
	}

	public void setAuthorizedName(String authorizedName) {
		this.authorizedName = authorizedName;
	}

	public String getIdCardNumber() {
		return idCardNumber;
	}

	public void setIdCardNumber(String idCardNumber) {
		this.idCardNumber = idCardNumber;
	}

	public String getAliplay() {
		return aliplay;
	}

	public void setAliplay(String aliplay) {
		this.aliplay = aliplay;
	}

	public String getWeiXing() {
		return weiXing;
	}

	public void setWeiXing(String weiXing) {
		this.weiXing = weiXing;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	public float getTotalForToday() {
		return totalForToday;
	}

	public void setTotalForToday(float totalForToday) {
		this.totalForToday = totalForToday;
	}

	public float getTotalForLastMonth() {
		return totalForLastMonth;
	}

	public void setTotalForLastMonth(float totalForLastMonth) {
		this.totalForLastMonth = totalForLastMonth;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public int getFreeCount() {
		return freeCount;
	}

	public void setFreeCount(int freeCount) {
		this.freeCount = freeCount;
	}
	
}
