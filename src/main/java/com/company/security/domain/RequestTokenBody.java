package com.company.security.domain;

import java.io.Serializable;

public class RequestTokenBody implements Serializable{
	private String token;
	private Object requestBody;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Object getRequestBody() {
		return requestBody;
	}
	public void setRequestBody(Object requestBody) {
		this.requestBody = requestBody;
	}
	@Override
	public String toString() {
		return "RequestTokenBody [token=" + token + ", requestBody=" + requestBody + "]";
	}
	
	
}
