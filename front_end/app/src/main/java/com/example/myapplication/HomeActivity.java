package com.example.myapplication;

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
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myapplication.SearchAllstar.WebActivity;
import com.example.myapplication.SearchAllstar.githubActivity;
import com.example.myapplication.utils.AipBodyUtils;
import com.example.myapplication.utils.AipFaceUtils;
import com.example.myapplication.utils.MarkFaces;
import com.example.myapplication.utils.NameUtile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    private String username;
    private HashMap nameMap = NameUtile.getHashMap();
    private JSONObject name;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_home);

        photo=findViewById(R.id.photo);
        token = getIntent().getStringExtra("token");
        Log.i("token", "...."+token);

        username = getIntent().getStringExtra("username");
        Log.i("用户名", "--------"+username);
        aip_client = new Aip_faceDetect();
        faceUser = "";
        userListLenth = 0;
        notRegister = false;

        initData();
        new Thread(getUserListLenth).start();
    }


    private void initData() {
        toolbar = findViewById(R.id.toolbar_tb);
        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigationView = findViewById(R.id.nav_view);
        navigationView = findViewById(R.id.nav_left);

        navigationView.setItemIconTintList(null);
        //点击开启侧滑菜单
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        //底部导航条的点击事件
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.searchAllStar:
                        searchAllStar(photo);
                        break;
                    case R.id.makeCertificationPhoto:
                        makeCertificationPhoto(photo);
                        break;
                    case R.id.github:
                        Intent intent=new Intent(HomeActivity.this, githubActivity.class);
                        startActivity(intent);

                        break;
                }
                return true;
            }
        });

        //侧滑菜单的点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.select:
                        select(photo);
                        break;
                    case R.id.shot:
                        shot(photo);
                        break;
                    case R.id.up:
                        try {
                            upload(photo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.detect:
                        toYunDisk(photo);
                        break;
                    case R.id.more:
                        allStar(photo);
                        break;
                }  return true;
            }
        });
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
        if(base64_data.isEmpty()){
            Toast.makeText(HomeActivity.this, "请先选择照片再上传", Toast.LENGTH_LONG).show();
            return;
        }
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
                        .addFormDataPart("username", username)
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
                intent.putExtra("username", username);
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
        String shitName = "";
        try {
            if(name!=null){
                int face_num = (int) name.getJSONObject("result").get("face_num");
                JSONArray jsonArray = name.getJSONObject("result").getJSONArray("face_list");
                for (int i =0; i < face_num; i++) {
                    String who = ((JSONObject) ((JSONObject) jsonArray.get(i)).getJSONArray("user_list").get(0)).getString("user_id");
                    if(nameMap.get(who)!=null){
                        shitName = shitName + "," + nameMap.get(who);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(HomeActivity.this, WebActivity.class);
        intent.putExtra("name",shitName);
        startActivity(intent);
    }

    public void searchAllStar(View view) {
        new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = AipFaceUtils.multiSearch(fileBuf);
                    name = jsonObject;
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

    public void makeCertificationPhoto(View view) {
        new Thread() {
            @Override
            public void run() {
                try {
                    bitmap = AipBodyUtils.getCertificationPhoto(fileBuf);
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
