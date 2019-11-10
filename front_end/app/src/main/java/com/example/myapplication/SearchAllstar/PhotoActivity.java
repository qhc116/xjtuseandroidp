package com.example.myapplication.SearchAllstar;

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
import android.graphics.BitmapRegionDecoder;
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

import com.baidu.aip.face.AipFace;
import com.baidu.aip.util.Base64Util;
import com.example.myapplication.R;
import com.example.myapplication.utils.AipFaceHelper;
import com.example.myapplication.utils.AipFaceUtils;
import com.example.myapplication.utils.MarkFaces;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class PhotoActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;//相机
    private static final String TAG = "TAG";
    private ImageView photo = null;
    private String uploadFileName;
    private byte[] fileBuf;
    private Bitmap bitmap;
    private String uploadUrl = "http://114.55.66.103:8000/upload";
    private Uri uri;

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
                default:
                    super.handleMessage(msg);
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
    }

    public void selectImage(View view) {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
        };
        //进行sdcard的读写请求
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1);
            ActivityCompat.requestPermissions(this, permissions, 2);
        } else {
            openGallery(); //打开相册，进行选择
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Toast.makeText(this, "读相册的操作被拒绝", Toast.LENGTH_LONG).show();
                }
        }
    }

    private void openGallery() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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
        bitmap = data.getParcelableExtra("data");
        int bytes = bitmap.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer
        fileBuf = buffer.array(); //Get the bytes array of the bitmap
        photo = findViewById(R.id.photo);
        photo.setImageBitmap(bitmap);
        Button bt = findViewById(R.id.upload);
        bt.setVisibility(View.VISIBLE);
        bt = findViewById(R.id.searchAllStar);
        bt.setVisibility(View.VISIBLE);
    }

    //选择后照片的读取工作
    private void handleSelect(Intent intent) {
        Cursor cursor = null;
        uri = intent.getData();
        //如果直接是从"相册"中选择，则Uri的形式是"content://xxxx"的形式
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                uploadFileName = cursor.getString(columnIndex);
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    fileBuf = new byte[inputStream.available()];
                    inputStream.read(fileBuf);
                    bitmap = BitmapFactory.decodeByteArray(fileBuf, 0, fileBuf.length);
                    photo = findViewById(R.id.photo);
                    photo.setImageBitmap(bitmap);
                    Button bt = findViewById(R.id.upload);
                    bt.setVisibility(View.VISIBLE);
                    bt = findViewById(R.id.searchAllStar);
                    bt.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.i("other", "其它数据类型.....");
        }
        cursor.close();
    }

    //识别并标记人脸
    public void uploadImage(View view) {
        new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = AipFaceUtils.multiSearch(fileBuf);
                    bitmap = MarkFaces.markFaces(jsonObject, bitmap);
                    Log.d(TAG, "uploadImage: " + jsonObject);
                    Message msg=new Message();
                    msg.what=1;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void searchAllStar(final View view) {
        new Thread() {
            @Override
            public void run() {
                AipFace client = AipFaceHelper.getClient();
                String image = Base64Util.encode(fileBuf);
                String imageType = "BASE64";
                String groupId = "allstar";
                JSONObject res = client.search(image, imageType, groupId, null);
                Log.i("Tag", res.toString());
                try {
                    JSONObject jsonObject = (JSONObject) res.getJSONObject("result").getJSONArray("user_list").get(0);
                    Map msg = new HashMap();
                    msg.put("user_id",jsonObject.getString("user_id"));
                    msg.put("score",jsonObject.getString("score"));
                    msg.put("filebuf",fileBuf);
                    Intent intent = new Intent(PhotoActivity.this,ShowSearchActivity.class);
                    intent.putExtra("msg", (Serializable) msg);
                    intent.putExtra("buf", fileBuf);

                    startActivity(intent);
                    Log.i("Tag", msg.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void takePhoto(View view) {
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(intent, 2);
    }
}

