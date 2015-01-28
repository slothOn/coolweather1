package com.coolweather1.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
		new Thread(
			new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection=null;
				try {
					URL url=new URL(address);
					connection=(HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setReadTimeout(8000);
					connection.setConnectTimeout(8000);
					InputStream input=connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(input));
					String line;
					StringBuilder response=new StringBuilder();
					while((line=reader.readLine())!=null){
						response.append(line);
					}
					if(listener!=null)
						listener.onFinish(response.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if(listener!=null)
						listener.onError(e);
				}finally{
					if(connection!=null)
						connection.disconnect();
				}
			}
		}).start();
	}
}
