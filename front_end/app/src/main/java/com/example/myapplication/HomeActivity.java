package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.util.Base64;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.util.Base64Util;
import com.example.myapplication.SearchAllstar.PhotoActivity;
import com.example.myapplication.SearchAllstar.ShowSearchActivity;
import com.example.myapplication.utils.AipFaceHelper;
import com.example.myapplication.utils.AipFaceUtils;
import com.example.myapplication.utils.MarkFaces;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.myapplication.utils.MarkFaces.markFaces;

public class HomeActivity extends AppCompatActivity {
    private static final int UPLOAD = 2;

    private byte[] fileBuf;
    private String uploadFileName;
    private ImageView photo;
    private String uploadUrl = "http://114.55.36.148:8000/upload";

    private Aip_faceDetect aip_client;
    private String faceUser;
    private int userListLenth;

    private String base64_data;

    private boolean notRegister;
    private String token;
    private Bitmap bitmap;
    private String resultJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        photo=findViewById(R.id.photo);
        token = getIntent().getStringExtra("token");
        Log.i("token", "...."+token);


        aip_client = new Aip_faceDetect();
        faceUser = "";
        userListLenth = 0;
        notRegister = false;
        new Thread(getUserListLenth).start();

    }


    Handler handler=new Handler(){
        private static  final int SUCCESS=1;
        private static  final int FAIL=0;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SUCCESS:
                    photo = findViewById(R.id.photo);
                    photo.setImageBitmap(bitmap);
                    break;
                case UPLOAD:
                    isFace();
                default:
                    super.handleMessage(msg);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openGallery();
                }
                else{
                    Toast.makeText(this,"读相册的操作被拒绝",Toast.LENGTH_LONG).show();
                }
                break;
            case 2:

        }
    }
    public void select(View view) {
        String[] permissions=new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE
        };
        //进行sdcard的读写请求
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,permissions,1);
        }
        else{
            openGallery(); //打开相册，进行选择
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                handleSelect(data);
                break;
            case 2:
                if (resultCode == Activity.RESULT_OK  && data != null){
                    handleTakePhoto(data);
                } else if(resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this,"您取消了拍照",Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void handleTakePhoto(Intent data) {
        notRegister = false;
        uploadFileName = "unknowuser";
        bitmap = data.getParcelableExtra("data");

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,bao);
        photo.setImageBitmap(this.bitmap);

        fileBuf =  bao.toByteArray();
        base64_data = Base64.encodeToString(fileBuf, 0);
//        bitmap = BitmapFactory.decodeByteArray(fileBuf, 0, fileBuf.length);
//        photo = findViewById(R.id.photo);


        isWhichGroup(bitmap);
//        Button bt = findViewById(R.id.upload);
//        bt.setVisibility(View.VISIBLE);
//        bt = findViewById(R.id.searchAllStar);
//        bt.setVisibility(View.VISIBLE);
    }

    private void handleSelect(Intent intent){
        Cursor cursor = null;
        notRegister = false;
        Uri uri = intent.getData();
        cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            uploadFileName = cursor.getString(columnIndex);
        }
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            fileBuf=convertToBytes(inputStream);

            //将字节数组写成位图显示
            bitmap = BitmapFactory.decodeByteArray(fileBuf, 0, fileBuf.length);
            photo.setImageBitmap(bitmap);
            //压缩后再上传
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,bao);
            fileBuf =  bao.toByteArray();

            base64_data = Base64.encodeToString(fileBuf, 0);
            Log.i("尺寸", "...."+base64_data.length()*2);
            //选择完图片后查询下是哪个组
            isWhichGroup(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        Log.i("图片路径", "..."+uploadFileName);
    }


    public void upload(View view) throws JSONException {
        new Thread() {
            @Override
            public void run() {
                if(notRegister) {
                    aip_client.updateUser(base64_data, userListLenth + 1);
                    //同步下一次新用户记录位置
                    userListLenth++;
                }
                //获取照片上传的日期
                SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
                Date curDate = new Date(System.currentTimeMillis());
                String dateInfo = format.format(curDate);

                OkHttpClient client = new OkHttpClient();
                //上传文件域的请求体部分
//                RequestBody formBody = RequestBody
//                        .create(base64_data, MediaType.parse("text"));
                //整个上传的请求体部分（普通表单+文件上传域）
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("filename", uploadFileName)
                        .addFormDataPart("filedata", base64_data)
                        .addFormDataPart("faceuser", faceUser)
                        .addFormDataPart("dateinfo", dateInfo)
                        .addFormDataPart("resultJson", resultJson)
                        .addFormDataPart("token", token)
                        //filename:avatar,originname:abc.jpg
//                        .addFormDataPart("avatar", uploadFileName, formBody)
                        .build();
                Request request = new Request.Builder()
                        .url(uploadUrl)
                        .post(requestBody)
                        .build();
                Log.i("请求体", "构造完成");

                try {
                    Response response = client.newCall(request).execute();
                    Message msg = new Message();
                    msg.what = UPLOAD;
                    handler.sendMessage(msg);
//                    JSONObject jsonObject = new JSONObject(response.body().string());
//                    JSONObject jsonObject1 = aip_client.detect(fileBuf);
//                    isFace(jsonObject1);
//                    Log.i("数据", response.body().string() + "....");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("错误", "error");
                }
            }
        }.start();





    }

    private void openGallery() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    private byte[] convertToBytes(InputStream inputStream) throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        inputStream.close();
        return  out.toByteArray();
    }
    //未完成
    public void shot(View view) {
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(intent, 2);
    }

    //解析返回结果
    private void isFace(){

        Toast.makeText(HomeActivity.this,
                "尼刚刚上传了一张"+(faceUser.equals("notface")?"普通":"人脸")+"照片",
                Toast.LENGTH_SHORT).show();

    }

    private void isWhichGroup(Bitmap bitmap) {
        new Thread() {
            @Override
            public void run() {

                JSONObject jsonObject = aip_client.nMatch(base64_data);
                resultJson = jsonObject.toString();
//                Log.i("resultJson", "...."+resultJson);

                try {

                    String error_msg = jsonObject.getString("error_msg");

                    switch (error_msg) {
                        case "match user is not found":
                            //人脸未被注册,以当前长度＋1注册
                            notRegister = true;
                            faceUser = "user" + (userListLenth + 1);
                            break;
                        case "pic not has face":
                            //照片中没有人脸
                            faceUser = "notface";

                            break;
                        case "SUCCESS":
                            JSONObject result = new JSONObject(jsonObject.getString("result"));
                            JSONArray face_list = new JSONArray(result.getString("face_list"));
                            JSONObject mid = new JSONObject(face_list.get(0).toString());
                            JSONArray user_list = new JSONArray(mid.getString("user_list"));
                            faceUser = new JSONObject(user_list.get(0).toString()).getString("user_id");
                            break;
                    }
                    Log.i("所属user", "....." + faceUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void toYunDisk(View view) {
        //注意进入新的activity需要开启线程
        new Thread(){
            @Override
            public void run() {
                Intent intent = new Intent(HomeActivity.this, YunDiskActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        }.start();

    }

    Runnable getUserListLenth = new Runnable() {
        @Override
        public void run() {
            try {
                userListLenth = aip_client.getUserListLenth();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("列表长度", "...."+userListLenth);
        }
    };

    public void allStar(View view) {
        Intent intent = new Intent(HomeActivity.this, PhotoActivity.class);
        startActivity(intent);
    }

    public void searchAllStar(View view) {
        new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = AipFaceUtils.multiSearch(fileBuf);
                    bitmap = MarkFaces.markFaces(jsonObject, bitmap);
                    Log.d("TAG", "uploadImage: " + jsonObject);
                    Message msg=new Message();
                    msg.what=1;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
