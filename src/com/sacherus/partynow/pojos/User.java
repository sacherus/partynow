package com.sacherus.partynow.pojos;

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
	public IContentValuesPOJO toContent(ContentValues cv) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ContentValues fromContentLocal(IContentValuesPOJO cv) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
