package com.sacherus.partynow.pojos;
public class Token {
	private String accessToken;
	private String scope;
	private int expiresIn;
	private String refreshToken;

	public Token() {
		
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	


	
}
