package com.example.myapplication.LoginRegisterPage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.HomeActivity;
import com.example.myapplication.PictureList;
import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class userLoginActivity extends AppCompatActivity {
    private Button mBtnLogin;
    private Button mBtnRegister;
    private Button mBtnError;
    private TextView LoginNameText;
    private TextView LoginPasswordText;
    private String Lname;
    private String Lpassword;
    private String url = "http://114.55.36.148:8000/login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_user);

        LoginNameText = findViewById(R.id.et_1);
        LoginPasswordText = findViewById(R.id.et_2);
        Lname=LoginNameText.getText().toString().trim();
        Lpassword=LoginPasswordText.getText().toString().trim();
        mBtnLogin = findViewById(R.id.login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              new Thread(){
                  @Override
                  public void run() {

                      String[] permissions = new String[]{
                          Manifest.permission.INTERNET
                  };
                      //进行sdcard的读写请求
                      if (ContextCompat.checkSelfPermission(userLoginActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                          ActivityCompat.requestPermissions(userLoginActivity.this, permissions, 1);
                      }
                      OkHttpClient client = new OkHttpClient();

                      RequestBody body = new FormBody.Builder()
                              .add("username", LoginNameText.getText().toString().trim())
                              .add("password", LoginPasswordText.getText().toString().trim())
                              .build();

                      Request request = new Request.Builder()
                              .url(url)
                              .post(body)
                              .build();
                      try {
                          Response response = client.newCall(request).execute();
                          JSONObject jsonObject = new JSONObject(response.body().string()); //获取Http响应报文的结果

                          String err = jsonObject.getString("err");

                         if(err.equals("0")) {
                             Intent intent=new Intent(userLoginActivity.this, HomeActivity.class);
                             intent.putExtra("token", jsonObject.getString("msg"));
                             startActivity(intent);
                         }else {
                             Message msg = new Message();
                             msg.what = 1;
                             Bundle bundle = new Bundle();
                             bundle.putString("ret", "用户名或密码错误");
                             msg.setData(bundle);
                             handler.sendMessage(msg);
                         }

                      } catch (IOException | JSONException e) {
                          Log.e("err",e.toString());
                          e.printStackTrace();
                      }
                  }
              }.start();

           }
       });

        //为注册按钮添加事件
        mBtnRegister = findViewById(R.id.register);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userLoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });


        //为忘记密码按钮添加事件
        mBtnError = findViewById(R.id.login_error);
        mBtnError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userLoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //处理图片新加入的更新
                case 1:
                    Bundle data = msg.getData();
                    String toToast = data.getString("ret");
                    Toast.makeText(userLoginActivity.this, toToast, Toast.LENGTH_SHORT).show();
                case 2:
            }
        }
    };

 }

