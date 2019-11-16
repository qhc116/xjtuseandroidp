package com.example.myapplication.LoginRegisterPage.Broadcast;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Myservice extends Service {
    private Timer setTimer;

    public Myservice() {
    }

    private String token;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
    }

     @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        token = intent.getStringExtra("token");
        Log.d("token", token);
        OnStart();
 return super.onStartCommand(intent, flags, startId);

    }

    private void OnStart() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient client = new OkHttpClient();

                RequestBody body = new FormBody.Builder()
                        .add("token", token)
                        .build();

                Request request = new Request.Builder()
                        .url("http://114.55.36.148:8000/auth")
                        .post(body)
                        .build();

                try {

                    Response response = client.newCall(request).execute();

                    JSONObject jsonObject = new JSONObject(response.body().string()); //获取Http响应报文的结果
                    String err = jsonObject.getString("err");
                    Log.d("中文", jsonObject.getString("err"));
                    Intent intent = new Intent();
                    if (err.equals("0")) {
                        Log.d("运行", "到了吗");
                        intent.putExtra("msg", jsonObject.getString("msg"));
                        intent.setAction("test");
                        sendBroadcast(intent);
                        Log.d("运行", "到了吗444");
                    } else if (err.equals("2")) {
                        //判断token,如果二次登录
                        Log.d("运行", "到了吗2");
                        intent.setAction("otherLogin");
                        sendBroadcast(intent);
                    } else if (err.equals("1")) {
                        //判断token,如果登陆过期
                        Log.d("eeee", "iwiwi");
                        intent.setAction("LoginOut");
                        sendBroadcast(intent);
                    }
                    //成功登陆

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

        }).start();

    }
}