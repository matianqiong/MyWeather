package com.example.matianqiong.myweather.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.matianqiong.myweather.db.MyWeatherOpenHelper;

import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MaTianQiong on 2016/4/10.
 */
public class MyWeatherDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME="my_weather";
    /**
     * 数据库版本
     */
    public static final int VERSION=1;

    private static MyWeatherDB myWeatherDB;

    private SQLiteDatabase db;
    /**
     * 将构造方法私有化
     */
    private MyWeatherDB(Context context){
        MyWeatherOpenHelper dbHelper=new MyWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db=dbHelper.getWritableDatabase();
    }
    /**
     * 获取MyWeatherDB实例
     */
    public synchronized static MyWeatherDB getInstance(Context context){
        if (myWeatherDB== null){
            myWeatherDB=new MyWeatherDB(context);
        }
        return myWeatherDB;
    }
    /**
     * 将Province实例存储到数据库
     */
    public void saveProvince(Province province){
        if (province!=null){
            ContentValues values=new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }
    /**
     * 读取全国所有的省份信息
     */
    public List<Province> loadProvince(){
        List<Province> provinces=new ArrayList<Province>();
        Cursor cursor=db.query("Province",null,null,null,null,null,null);
        while (cursor.moveToNext()){
            Province province=new Province();
            province.setId(cursor.getInt(cursor.getColumnIndex("id")));
            province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
            province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
            provinces.add(province);
        }
        if (cursor!=null){
            cursor.close();
        }
        return provinces;
    }
    /**
     * 将city实例存储到数据库
     */
    public void saveCity(City city){
        if (city!=null){
            ContentValues values=new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City", null, values);
        }
    }
    /**
     * 从数据库中读取某省的所有城市信息
     */
    public List<City> loadCities(int provinceId){
        List<City> cities=new ArrayList<>();
        Cursor cursor=db.query("City",null,"province_id=?",
                new String[]{String.valueOf(provinceId)},null,null,null );
        while (cursor.moveToNext()){
            City city=new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("id")));
            city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
            city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
            city.setProvinceId(provinceId);
            cities.add(city);
        }
        if (cursor!=null){
            cursor.close();
        }
        return cities;

    }
    /**
     * 将County实例存储在数据库中
     */
    public void saveCounty(County county){
        if (county!=null){
            ContentValues values=new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCityId());
            db.insert("County", null, values);
        }

    }
    /**
     * 从数据库中读取某个市下面的所有县
     */
    public List<County> loadCounties(int cityId){
        List<County> counties=new ArrayList<>();
        Cursor cursor=db.query("County",null,"city_id=?",
                new String[]{String.valueOf(cityId)},null,null,null);
        while (cursor.moveToNext()){
            County county=new County();
            county.setId(cursor.getInt(cursor.getColumnIndex("id")));
            county.setCityId(cityId);
            county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
            county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
            counties.add(county);
        }
        if (cursor!=null){
            cursor.close();
        }
        return counties;
    }
}

