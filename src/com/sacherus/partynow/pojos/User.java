package com.sacherus.partynow.pojos;

import com.sacherus.partynow.provider.PartiesContract.UserColumnHelper;

import android.content.ContentValues;

public class User implements IContentValuesPOJO {
	private int id;
	private String username;
	private String password;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public User() {};
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	@Override
	public ContentValues toContent() {
		ContentValues cv = new ContentValues();
		cv.put(UserColumnHelper.USERNAME, getUsername());
		cv.put(UserColumnHelper._ID, getId());
		return cv;
	}
	
	@Override
	public void fromContentLocal(ContentValues cv) {
		setUsername(cv.getAsString(UserColumnHelper.USERNAME));
		setId(cv.getAsInteger(UserColumnHelper._ID));
	}
	
	public User(ContentValues cv) {
		fromContentLocal(cv);
	}
	
}
