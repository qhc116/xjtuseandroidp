package com.example.myapplication.SearchAllstar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShowSearchActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    Handler handler=new Handler(){
        private static  final int SUCCESS=1;
        private static  final int FAIL=0;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    byte[] bytes = msg.getData().getByteArray("bytes");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    ImageView imageView = findViewById(R.id.targetImage);
                    imageView.setImageBitmap(bitmap);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_search);
        final Intent intent = getIntent();
        setTargetImage(intent);
        setOriginal(intent);


    }


    private void setOriginal(Intent intent) {
        TextView textView = findViewById(R.id.like);
        Serializable s = intent.getSerializableExtra("msg");
        Map map = (Map)s;
        byte[] fileBuf = intent.getByteArrayExtra("buf");

        textView.setText("您最像的全明星是：" + map.get("user_id"));

        Bitmap bitmap = BitmapFactory.decodeByteArray(fileBuf, 0, fileBuf.length);
        ImageView imageView = findViewById(R.id.originalImage);
        imageView.setImageBitmap(bitmap);
    }

    private void setTargetImage(final Intent intent) {
        new Thread(){
            @Override
            public void run() {
                try {
                    getallstar(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void getallstar(Intent intent) throws IOException {
        OkHttpClient client=new OkHttpClient();
        Serializable s = intent.getSerializableExtra("msg");
        Map map = (Map)s;

        //整个上传的请求体部分（普通表单+文件上传域）
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", (String) map.get("user_id"))
                .build();

        Request request = new Request.Builder()
                .url("http://114.55.66.103:8000/getallstarbyname")
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();
        String res = response.body().string();
        Gson gson = new Gson();
        LinkedTreeMap o = (LinkedTreeMap) gson.fromJson(res,Object.class);
        ArrayList arrayList =  (ArrayList)((LinkedTreeMap)o.get("response")).get("data");
        int size = arrayList.size();
        byte[] bytes = new byte[arrayList.size()];
        for (int i = 0; i < size; i++){
            bytes[i] = ((Double)arrayList.get(i)).byteValue();
        }
        Message msg=new Message();
        msg.what=1;
        Bundle bundle=new Bundle();
        bundle.putByteArray("bytes", bytes);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    public void getSon(View view) {
    }
}
