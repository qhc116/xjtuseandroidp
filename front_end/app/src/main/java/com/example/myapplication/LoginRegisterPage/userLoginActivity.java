package com.example.myapplication.LoginRegisterPage;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baidu.aip.fl.RegActivity;
import com.example.myapplication.HomeActivity;
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
    private SharedPreferences sp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] permissions = new String[]{
                Manifest.permission.INTERNET
        };
        //进行sdcard的读写请求
        if (ContextCompat.checkSelfPermission(userLoginActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(userLoginActivity.this, permissions, 1);
        }


        setContentView(R.layout.login_user);

        LoginNameText = findViewById(R.id.et_1);
        LoginPasswordText = findViewById(R.id.et_2);
        Lname = LoginNameText.getText().toString().trim();
        Lpassword = LoginPasswordText.getText().toString().trim();
        // readData();
        //回写账号密码,判断次数,如果第三次登录之前的账号,需要填写密码
        sp= getSharedPreferences("Minfo",MODE_PRIVATE);
        int time=sp.getInt("times",1);
        LoginNameText.setText(sp.getString("name",""));
        if(time%3==0) {
            LoginPasswordText.setText("");
        }else
            LoginPasswordText.setText(sp.getString("password",""));


        mBtnLogin = findViewById(R.id.login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        String username = LoginNameText.getText().toString().trim();

                        RequestBody body = new FormBody.Builder()
                                .add("username", username)
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
                            //成功登录,将数据写入SharedPreferences
                            if (err.equals("0")) {
                                //返回结果中获取是否人脸注册
                                String FaceRegisterCode = jsonObject.getString("hasFace");
                                Boolean hasRegisterFace = FaceRegisterCode.equals("true");
                                Log.i("hasface", "...."+FaceRegisterCode);
                                //将数据写入SharedPreferences
                                SharedPreferences.Editor edit=sp.edit();

                                edit.putString("token",jsonObject.getString("msg"));
                                edit.putString("name",username);
                                edit.putString("password",LoginPasswordText.getText().toString().trim());
                                int time2=sp.getInt("times",1);
                                edit.putInt("times",++time2);
                                edit.commit();

                                Log.d("kaiqi1","服务");
                                //开启Service
//                                Intent intent2=new Intent(userLoginActivity.this, Myservice.class);
//                                intent2.putExtra("token",jsonObject.getString("msg"));
//                                startService(intent2);
                                String token = jsonObject.getString("msg");

                                //以注册直接进入，未注册需要注册
                                if(hasRegisterFace){
                                    Intent intent = new Intent(userLoginActivity.this, HomeActivity.class);
                                    intent.putExtra("username", username);
                                    intent.putExtra("token", token);
                                    startActivity(intent);
                                }else{
                                    Intent intent = new Intent(userLoginActivity.this, RegActivity.class);
                                    intent.putExtra("username", username);
                                    intent.putExtra("token", token);
                                    startActivity(intent);
                                }




                                //




                                userLoginActivity.this.finish();









                            } else {
                                Message msg = new Message();
                                msg.what = 1;
                                Bundle bundle = new Bundle();
                                bundle.putString("ret", "用户名或密码错误");
                                msg.setData(bundle);
                                LoginNameText.setText("");
                                LoginPasswordText.setText("");
                                handler.sendMessage(msg);
                            }

                        } catch (IOException | JSONException e) {
                            Log.e("err", e.toString());
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
//        mBtnError = findViewById(R.id.login_error);
//        mBtnError.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(userLoginActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
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

