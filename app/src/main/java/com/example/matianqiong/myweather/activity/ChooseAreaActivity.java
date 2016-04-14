package com.example.matianqiong.myweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matianqiong.myweather.R;
import com.example.matianqiong.myweather.entity.City;
import com.example.matianqiong.myweather.entity.County;
import com.example.matianqiong.myweather.entity.MyWeatherDB;
import com.example.matianqiong.myweather.entity.Province;
import com.example.matianqiong.myweather.util.HttpCallbackListener;
import com.example.matianqiong.myweather.util.HttpUtil;
import com.example.matianqiong.myweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MaTianQiong on 2016/4/10.
 */
public class ChooseAreaActivity extends Activity {
    private ListView listView;
    private TextView textView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<>();
    private MyWeatherDB myWeatherDB;
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private int currentLevel;
    private ProgressDialog progressDialog;
    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;

    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity",false);

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("city_selected",false)&&!isFromWeatherActivity){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        initView();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if (currentLevel==LEVEL_COUNTY){
                    String countyCode=countyList.get(position).getCountyCode();
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }
    public void initView(){
        listView= (ListView) findViewById(R.id.list_view);
        textView= (TextView) findViewById(R.id.title_text);
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,dataList);
        myWeatherDB=MyWeatherDB.getInstance(this);
    }
    /**
     * 根据传入代号和类型从服务器上查询省市县数据
     */
    private void queryFromServer(final String code,final String type){
        String url;
        if (!TextUtils.isEmpty(code)){
            url="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else {
            url="http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if ("province".equals(type)){
                    result= Utility.handleProvinceResponse(myWeatherDB,response);
                }else if ("city".equals(type)){
                    result=Utility.handleCityReaponse(myWeatherDB,response,selectedProvince.getId());
                }
                else if ("county".equals(type)){
                    result=Utility.handleCountyResponse(myWeatherDB,response,selectedCity.getId());
                }
                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }

                        }
                    });
                }
            }



            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"数据加载失败",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    /**
     * 查询选择市的所有县，优先从数据库查询，没有再从服务器查询
     */

    private void queryCounties() {
        countyList=myWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size()>0){
            dataList.clear();
            for (County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();;
            listView.setSelection(0);
            textView.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }

    }
    /**
     * 查询选择省下面的所有市，优先从数据库查询，没有再从服务器查询
     */
    private void queryCities() {
        cityList=myWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            textView.setText(selectedProvince.getProvinceName());
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }
    /**
     * 查询全国的省，优先从数据库查询，没有再从服务器查询
     */
    private void queryProvinces() {
        provinceList=myWeatherDB.loadProvince();
        if (provinceList.size()>0){
            dataList.clear();
            for (Province p:provinceList){
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }else {
            queryFromServer(null,"province");
        }
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载中。。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();

    }
    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if (currentLevel==LEVEL_CITY){
            queryProvinces();
        }else {
            if (isFromWeatherActivity){
                Intent intent=new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
