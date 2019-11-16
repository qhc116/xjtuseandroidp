package com.example.myapplication.LoginRegisterPage.Broadcast;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.LinkedList;

public class BaseActicity extends AppCompatActivity {
    private static LinkedList<AppCompatActivity> activities;//存储activity集合
    private ForceofflineReceiver myservice;
    private PopupWindow window;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (activities == null) {
            activities = new LinkedList<>();
        } else {
            activities.add(this);
            String[] permissions = new String[]{
                    Manifest.permission.SYSTEM_ALERT_WINDOW
            };
            //进行sdcard的读写请求
            if (ContextCompat.checkSelfPermission(activities.getLast(), Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activities.getLast(), permissions, 1);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //当从activity中退出时,从列表中移除
        if (activities != null) {
            activities.remove(this);
        }

    }

    protected static void exitApp() {
        //彻底退出app
        for (Activity baseActivity : activities) {
            baseActivity.finish();
        }
        activities.clear();
        activities = null;
    }


    public void startActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    public static class ForceofflineReceiver extends BroadcastReceiver {
        private PopupWindow window;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("接收到广播", "sd");
            if ("LoginOut".equals(intent.getAction())) {
                Log.d("登录过期", "126");
                //   LoginOut(context, intent);
            } else if ("OtherLogin".equals(intent.getAction())) {
                Log.d("二次登陆", "126");
                //OtherLogin(context, intent);
            } else if ("test".equals(intent.getAction())) {
                Log.d("测试通过", "123");
                OtherLogin(context, intent);

            }
        }

        private void OtherLogin(Context context, Intent intent) {
            Toast.makeText(context, "二次登陆", Toast.LENGTH_SHORT).show();
            exitApp();

        }


        private void LoginOut(Context context, Intent intent) {
            Toast.makeText(context, "登录过期", Toast.LENGTH_SHORT).show();
            exitApp();
       /* if (activities.getLast().getClass().equals(this.getClass())) {
            AlertDialog.Builder LoginoutDialog = new AlertDialog.Builder(context);

            LoginoutDialog.setTitle("警告");
            LoginoutDialog.setMessage("您的登录已过期,请重新登录");
            LoginoutDialog.setCancelable(false);
            LoginoutDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(context, userLoginActivity.class);
                    context.startActivity(intent);
                }

            });*/
         /*   AlertDialog alterDialog = LoginoutDialog.create();
            //添加对话框类型：保证在广播中正常弹出
            alterDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            //对话框展示
            alterDialog.show();*/

        }
    }
}





