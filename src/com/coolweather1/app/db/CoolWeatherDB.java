package com.coolweather1.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather1.app.model.City;
import com.coolweather1.app.model.County;
import com.coolweather1.app.model.Province;

public class CoolWeatherDB {
	public static final String DB_NAME="cool_weather";
	public static final int VERSION=1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;
	/*
	 * private constructor
	 */
	private CoolWeatherDB(Context c){
		CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper(c, DB_NAME, null, VERSION);
		db=dbHelper.getWritableDatabase();
	}
	/*
	 * 获取CoolWeather实例
	 */
	public synchronized static CoolWeatherDB getInstance(Context c){
		if(coolWeatherDB==null){
			coolWeatherDB=new CoolWeatherDB(c);
		}
		return coolWeatherDB;
	}
	/*
	 * Province实例存储到数据库
	 */
	public void saveProvince(Province p){
		if(p==null)
			return;
		ContentValues values=new ContentValues();
		values.put("province_name", p.getProvinceName());
		values.put("province_code", p.getProvinceCode());
	    db.insert("Province", null, values);
	}
	/*
	 * 从数据库读取省份信息
	 */
	public List<Province> loadProvinces(){
		List<Province> list=new ArrayList<Province>();
		Cursor cursor=db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Province p=new Province();
				p.setId(cursor.getInt(cursor.getColumnIndex("id")));
				p.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				p.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				list.add(p);
			}while(cursor.moveToNext());
		}
		if(cursor!=null)
			cursor.close();
		return list;
	}
	/*
	 * City实例存储到数据库
	 */
	public void saveCity(City c){
		if(c==null)
			return;
		ContentValues values=new ContentValues();
		values.put("city_name", c.getCityName());
		values.put("city_code", c.getCityCode());
		values.put("province_id", c.getProvinceId());
		db.insert("City", null, values);
	}
	/*
	 * 从数据库某省下读取所有城市信息
	 */
	public List<City> loadCities(int pid){
		List<City> list=new ArrayList<City>();
		Cursor cursor=db.query("City", null, "province_id=?", new String[]{String.valueOf(pid)}, null, null, null);
        if(cursor.moveToFirst()){
        	do{
        		City city=new City();
        		city.setId(cursor.getInt(cursor.getColumnIndex("id")));
        		city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
        		city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
        		city.setProvinceId(pid);
        		list.add(city);
        	}while(cursor.moveToNext());
        }
        if(cursor!=null)
        	cursor.close();
        return list;
	}
	/*
	 * County实例存储到数据库
	 */
	public void saveCounty(County c){
		if(c==null)
			return;
		ContentValues values=new ContentValues();
		values.put("id", c.getId());
		values.put("county_code",c.getCountyCode());
		values.put("county_name", c.getCountyName());
		values.put("city_id", c.getCityId());
		db.insert("County", null, values);
	}
	/*
	 * 从数据库读取某城市下所有县
	 */
	public List<County> loadCounties(int cid){
		List<County> list=new ArrayList<County>();
		Cursor cursor=db.query("County", null, "city_id=?", new String[]{String.valueOf(cid)}, null, null, null);		
		if(cursor.moveToFirst()){
			do{
				County c=new County();
				c.setCityId(cid);
				c.setId(cursor.getInt(cursor.getColumnIndex("id")));
				c.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				c.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				list.add(c);
			}while(cursor.moveToNext());
		}
		if(cursor!=null)
			cursor.close();
		return list;
	}
}
