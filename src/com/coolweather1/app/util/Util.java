package com.coolweather1.app.util;

import android.text.TextUtils;

import com.coolweather1.app.db.CoolWeatherDB;
import com.coolweather1.app.model.City;
import com.coolweather1.app.model.County;
import com.coolweather1.app.model.Province;

public class Util {
	/*
	 * 解析和处理服务器返回的省级数据
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allprovinces=response.split(",");
			if(allprovinces!=null&&allprovinces.length>0){
				for(String p:allprovinces){
					String[] array=p.split("\\|");
					Province p1=new Province();
					p1.setProvinceCode(array[0]);
					p1.setProvinceName(array[1]);
					coolWeatherDB.saveProvince(p1);
				}
				return true;
			}
		}
		return false;
	}
	/*
	 * 解析和处理服务器返回的市级数据
	 */
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCities=response.split(",");
			if(allCities!=null&&allCities.length>0){
				for(String c:allCities){
					String[] array=c.split("\\|");
					City city=new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	/*
	 * 解析和处理服务器返回的县级数据
	 */
	public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties=response.split(",");
			if(allCounties!=null&&allCounties.length>0){
				for(String c:allCounties){
					String[] array=c.split("\\|");
					County county=new County();
					county.setCityId(cityId);
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
}
