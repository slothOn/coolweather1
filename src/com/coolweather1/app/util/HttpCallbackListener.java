package com.coolweather1.app.util;

public interface HttpCallbackListener {
	public void onFinish(String response);
	public void onError(Exception e);
}
