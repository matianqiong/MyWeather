package com.example.matianqiong.myweather.util;

import android.text.TextUtils;

import com.example.matianqiong.myweather.entity.City;
import com.example.matianqiong.myweather.entity.County;
import com.example.matianqiong.myweather.entity.MyWeatherDB;
import com.example.matianqiong.myweather.entity.Province;

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
}
