package com.example.matianqiong.myweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.matianqiong.myweather.entity.City;
import com.example.matianqiong.myweather.entity.County;
import com.example.matianqiong.myweather.entity.MyWeatherDB;
import com.example.matianqiong.myweather.entity.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by MaTianQiong on 2016/4/10.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvinceResponse(MyWeatherDB myWeatherDB,String response){
        if (!TextUtils.isEmpty(response)){
            String[] provinces=response.split(",");
            if (provinces!=null&&provinces.length>0){
                for (String p:provinces){
                    String[] array=p.split("\\|");
                    Province province=new Province();
                    province.setProvinceName(array[1]);
                    province.setProvinceCode(array[0]);
                    //将解析的数据存储到Province表当中
                    myWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityReaponse(MyWeatherDB myWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] cities=response.split(",");
            if (cities!=null&&cities.length>0){
                for (String c:cities){
                    String[] array=c.split("\\|");
                    City city=new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    myWeatherDB.saveCity(city);
                }
              return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(MyWeatherDB myWeatherDB,String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            String [] counties=response.split(",");
            if (counties!=null&&counties.length>0){
                for (String c:counties){
                    String[] array=c.split("\\|");
                    County county=new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    myWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析服务器返回的JSOn数据，并将解析的数据存储到本地
     */
    public static void handleWeatherResponse(Context context,String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weahterInfo=jsonObject.getJSONObject("weatherinfo");
            String cityName=weahterInfo.getString("city");
            String weatherCode=weahterInfo.getString("cityid");
            String temp1=weahterInfo.getString("temp1");
            String temp2=weahterInfo.getString("temp2");
            String weatherDesp=weahterInfo.getString("weather");
            String publishTime=weahterInfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 将服务器返回的天气信息存储到shareepreferences中
     */

    private static void saveWeatherInfo(Context context, String cityName,
                                        String weatherCode, String temp1,
                                        String temp2, String weatherDesp,
                                        String publishTime) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.
                getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_time",sdf.format(new Date()));
        editor.commit();


    }


}
