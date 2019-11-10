package com.example.myapplication.LoginRegisterPage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private Button mBtnRegister;
    private Button mBtnCancel;
    private EditText mEdittext;
    private EditText mName_tv;
    private EditText mPassword_tv;
    private EditText mPassword2_tv;
    private String name;
    private String psw;
    private String spPsw;
    private String url = "http://114.55.36.148:8000/register";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mName_tv = findViewById(R.id.name_tv);
        mPassword_tv = findViewById(R.id.password_tv);
        mPassword2_tv = findViewById(R.id.password2_tv);
        mEdittext = findViewById(R.id.password2_tv);

        mBtnRegister = findViewById(R.id.mBtn_register1);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                psw = mPassword_tv.getText().toString().trim();
                spPsw = mPassword2_tv.getText().toString().trim();
                name =mName_tv.getText().toString().trim();
               if (!psw.equals(spPsw))
                    Toast.makeText(RegisterActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                else {
                   String[] permissions = new String[]{
                           Manifest.permission.INTERNET
                   };
                   //进行sdcard的读写请求
                   if (ContextCompat.checkSelfPermission( RegisterActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                       ActivityCompat.requestPermissions( RegisterActivity.this, permissions, 1);
                   }
                    new Thread(){
                        @Override
                        public void run() {


                                OkHttpClient client = new OkHttpClient();
                                RequestBody body = new FormBody.Builder()    //构建body
                                        .add("username",name)
                                        .add("password", psw)
                                        .build();
                                Request request = new Request.Builder()
                                        .url(url)
                                        .post(body)
                                        .build();
                                try {
                                    Response response = client.newCall(request).execute();
                                    String result = response.body().string(); //获取Http响应报文的结果
                                    JSONObject jsonObject = new JSONObject(result); //获取Http响应报文的结果
                                    String err = jsonObject.getString("err");

                                    if(err.equals("0")) {Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }else  System.out.println("用户名或密码错误");



                                } catch (IOException | JSONException e) {
                                    Log.e("err", e.toString());
                                    e.printStackTrace();

                                }
                            }

                    }.start();
               }


                //写一个方法发送数据到后台*/

            }
        });

        mBtnCancel = findViewById(R.id.mBtn_register2);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转去登录界面
                Intent intent = new Intent(RegisterActivity.this, userLoginActivity.class);
                startActivity(intent);
            }
        });


        mEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当注册界面三行中任有一行为空时,注册按钮不能点击
                if (TextUtils.isEmpty(mName_tv.getText()) || TextUtils.isEmpty(mPassword_tv.getText()) || TextUtils.isEmpty(mPassword2_tv.getText())) {
                    mBtnRegister.setBackground(getDrawable(R.drawable.bg_button_register_false));
                    mBtnRegister.setEnabled(Boolean.FALSE);
                } else {
                    mBtnRegister.setBackground(getDrawable(R.drawable.bg_button_1));
                    mBtnRegister.setEnabled(Boolean.TRUE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}

