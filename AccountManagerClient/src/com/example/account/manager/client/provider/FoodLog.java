package com.example.account.manager.client.provider;

import java.util.Date;

public class FoodLog {
	public FoodLog() {}
	private int id = 0;
	private String key = null;
    private long version = 0L;
    private String user = null;
    private String logDate = null;
    private String time = null;
    private String food = null;
    private double kcal = 0.0;
    private double salt = 0.0;
    private String createDate = null;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getLogDate() {
		return logDate;
	}
	public void setLogDate(String logDate) {
		this.logDate = logDate;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getFood() {
		return food;
	}
	public void setFood(String food) {
		this.food = food;
	}
	public double getKcal() {
		return kcal;
	}
	public void setKcal(double kcal) {
		this.kcal = kcal;
	}
	public double getSalt() {
		return salt;
	}
	public void setSalt(double salt) {
		this.salt = salt;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
}
