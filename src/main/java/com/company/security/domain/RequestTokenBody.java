package com.company.security.domain;

import java.io.Serializable;

public class RequestTokenBody implements Serializable{
	private String token;
	private String requestBody;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getRequestBody() {
		return requestBody;
	}
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}
	@Override
	public String toString() {
		return "RequestTokenBody [token=" + token + ", requestBody=" + requestBody + "]";
	}
	
	
}
