package com.example.matianqiong.myweather.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by MaTianQiong on 2016/4/10.
 */
public class HttpUtil {
    public static void sendHttpRequest(final String url, final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try {
                    URL mUrl=new URL(url);
                    connection= (HttpURLConnection) mUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    InputStream is=connection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(is));
                    StringBuilder content=new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null){
                        content.append(line);
                    }
                    if (listener!=null){
                        listener.onFinish(content.toString());
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                   if (listener!=null){
                       listener.onError(e);
                   }

                }finally {
                    if (connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
