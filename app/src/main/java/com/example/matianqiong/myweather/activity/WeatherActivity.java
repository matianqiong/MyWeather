package com.example.matianqiong.myweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.matianqiong.myweather.R;
import com.example.matianqiong.myweather.util.HttpCallbackListener;
import com.example.matianqiong.myweather.util.HttpUtil;
import com.example.matianqiong.myweather.util.Utility;

/**
 * Created by MaTianQiong on 2016/4/12.
 */
public class WeatherActivity extends Activity {
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView  temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        initView();
        String countyCode=getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)){
            publishText.setText("同步中。。。");
            weatherInfoLayout.setVisibility(TextView.INVISIBLE);
            cityNameText.setVisibility(TextView.INVISIBLE);
            queryWeatherCode(countyCode);
        }else {
            showWeather();
        }
    }
    /**
     * 从sharedpreferences中读取存储的天气信息，并显示到界面上
     */
    private void showWeather() {

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temp1",""));
        temp2Text.setText(prefs.getString("temp2",""));
        weatherDespText.setText(prefs.getString("weather_desp",""));
        publishText.setText(prefs.getString("publish_time","")+"发布");
        currentDateText.setText(prefs.getString("current_date",""));
        weatherInfoLayout.setVisibility(TextView.VISIBLE);
        cityNameText.setVisibility(TextView.VISIBLE);

    }
    /**
     * 查询县级代号所对应的天气代号
     */

    private void queryWeatherCode(String countyCode) {
        String url="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromServer(url, "countyCode");
    }

    private void queryFromServer(final String url, final String type) {
        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] arry = response.split("\\|");
                        if (arry != null && arry.length == 2) {
                            String weatherCode = arry[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if ("weatherCode".equals(type)){
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });

    }
    /**
     * 查询天气代号所对应的天气
     */
    private void queryWeatherInfo(String weatherCode) {
        String rul="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        queryFromServer(rul,"weatherCode");
    }

    private void initView(){
        weatherInfoLayout= (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText= (TextView) findViewById(R.id.city_name);
        publishText= (TextView) findViewById(R.id.public_text);
        weatherDespText= (TextView) findViewById(R.id.weather_desp);
        temp1Text= (TextView) findViewById(R.id.temp1);
        temp2Text= (TextView) findViewById(R.id.temp2);
        currentDateText= (TextView) findViewById(R.id.current_date);
    }
}
