package com.xjtuse.myandroidp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.xjtuse.myandroidp.utils.AipFaceHelper;
import com.xjtuse.myandroidp.utils.AipFaceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = "TAG";
    ImageView imageView;
    Bitmap bitmap;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        
    }

    public void show(View view) {
        String[] permissions = new String[]{
                Manifest.permission.INTERNET
        };
        //请求网络权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, permissions, 1);
            ActivityCompat.requestPermissions(this, permissions, 2);
        }

    }

    private void drawRectangles(Bitmap imageBitmap, int[] keywordRects) {
        int left, top, right, bottom;
        Bitmap mutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        //Canvas canvas = new Canvas(imageBitmap);
        Paint paint = new Paint();
            left = keywordRects[0];
            top = keywordRects[1];
            right = keywordRects[2];
            bottom = keywordRects[3];

            paint.setColor(Color.GREEN);
            paint.setDither(true);//防抖动
            paint.setFilterBitmap(true);//抗锯齿
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);//框框
            paint.setStrokeWidth(1);//框框宽度
            paint.setTextSize(16);

            canvas.rotate(-10,left,top);
            canvas.drawRect(left, top, right, bottom, paint);
            canvas.drawText ("刘醒", left, top-1, paint);
            imageView.setImageBitmap(mutableBitmap);
    }


//    private void drawRectangles(Bitmap imageBitmap, int[] keywordRects) {
//        int left, top, right, bottom;
//        Bitmap mutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
//        Canvas canvas = new Canvas(mutableBitmap);
//        //Canvas canvas = new Canvas(imageBitmap);
//        Paint paint = new Paint();
//            left = keywordRects[0];
//            top = keywordRects[1];
//            right = keywordRects[2];
//            bottom = keywordRects[3];
//
//            paint.setColor(Color.GREEN);
//            paint.setDither(true);//防抖动
//            paint.setFilterBitmap(true);//抗锯齿
//            paint.setAntiAlias(true);
//            paint.setStyle(Paint.Style.STROKE);//框框
//            paint.setStrokeWidth(1);//框框宽度
//            paint.setTextSize(16);
//
//            canvas.rotate(-10,left,top);
//            canvas.drawRect(left, top, right, bottom, paint);
//            canvas.drawText ("刘醒", left, top-1, paint);
//            imageView.setImageBitmap(mutableBitmap);
//    }

}

