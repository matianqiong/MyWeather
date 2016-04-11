package com.example.matianqiong.myweather.util;

/**
 * Created by MaTianQiong on 2016/4/10.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
