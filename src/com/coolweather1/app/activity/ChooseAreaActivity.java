package com.coolweather1.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather1.app.R;
import com.coolweather1.app.db.CoolWeatherDB;
import com.coolweather1.app.model.City;
import com.coolweather1.app.model.County;
import com.coolweather1.app.model.Province;
import com.coolweather1.app.util.HttpCallbackListener;
import com.coolweather1.app.util.HttpUtil;
import com.coolweather1.app.util.Util;


public class ChooseAreaActivity extends Activity{
	public final static int LEVEL_PROVINCE=0;
	public final static int LEVEL_CITY=1;
	public final static int LEVEL_COUNTY=2;
	private ProgressDialog progressDialog;
	private TextView textview;
	private ListView listview;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList=new ArrayList<String>();
	private List<Province> provincelist;
	private List<City> citylist;
	private List<County> countylist;
	private Province selectedProvince;
	private City selectedCity;
	private int level=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		coolWeatherDB=CoolWeatherDB.getInstance(this);
		listview=(ListView) findViewById(R.id.list_view);
	    textview=(TextView) findViewById(R.id.title_text);	
	    adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
	    listview.setAdapter(adapter);
	    listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				// TODO Auto-generated method stub
				if(level==LEVEL_PROVINCE){
					selectedProvince=provincelist.get(position);
					queryCity();
				}else if(level==LEVEL_CITY){
					selectedCity=citylist.get(position);
					queryCounty();
				}
//				else if(level==LEVEL_COUNTY){
//					String countycode = countylist.get(position).getCountyCode();
//					Intent intent=new Intent(ChooseAreaActivity.this, ChooseAreaActivity.class);
//					intent.putExtra("countycode", countycode);
//					startActivity(intent);
//					finish();
//				}	
			}
		});
	    queryProvince();
	}
	private void queryProvince() {
		// TODO Auto-generated method stub
		provincelist=coolWeatherDB.loadProvinces();
		if(provincelist.size()>0){
			dataList.clear();
			for(Province p:provincelist){
				dataList.add(p.getProvinceName());
			}
			listview.setSelection(0);
			textview.setText("China");
			level=LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	
	private void queryCity() {
		// TODO Auto-generated method stub
		citylist=coolWeatherDB.loadCities(selectedProvince.getId());
		if(citylist.size()>0){
			dataList.clear();
			for(City city:citylist){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			textview.setText(selectedProvince.getProvinceName());
			level=LEVEL_CITY;
		}else
			queryFromServer(selectedProvince.getProvinceCode(), "city");
	}
	
	private void queryCounty() {
		// TODO Auto-generated method stub
		countylist = coolWeatherDB.loadCounties(selectedCity.getId());
		if(countylist.size()>0){
			dataList.clear();
			for(County county:countylist){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			textview.setText(selectedCity.getCityName());
			level=LEVEL_COUNTY;
		}else
			queryFromServer(selectedCity.getCityCode(), "county");
	}

	
	private void queryFromServer(String code, final String type) {
		// TODO Auto-generated method stub
		String address;
		if(!TextUtils.isEmpty(code))
			address="http://www.weather.com.cn/data/list3/city" + code + ".xml";
		else
			address="http://www.weather.com.cn/data/list3/city.xml";
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result=false;
				if("province".equals(type))
					result=Util.handleProvincesResponse(coolWeatherDB, response);
				else if("city".equals(type))
					result=Util.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
				else if("county".equals(type))
				    result=Util.handleCountiesResponse(coolWeatherDB, response, selectedCity.getId());
			    if(result){
			    	//runOnUiThread回到主线程处理逻辑
			    	runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if("province".equals(type))
								queryProvince();
							else if("city".equals(type))
								queryCity();
							else if("county".equals(type))
								queryCounty();
						}
					});
			    }
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "exception", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog!=null)
			progressDialog.dismiss();
	}
	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 * 捕获back键，选择回退键功能
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(level==LEVEL_COUNTY){
			queryCity();
		}else if(level==LEVEL_CITY){
			queryProvince();
		}else
			finish();
	}
}
