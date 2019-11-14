package com.example.myapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ImageShower extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageshower);
        final ImageLoadingDialog dialog = new ImageLoadingDialog(this);
        dialog.show();
        // 两秒后关闭后dialog
        new Handler().postDelayed(() -> dialog.dismiss(), 1000 * 2);
        //设置图片
        ImageView bigiv = findViewById(R.id.bigiv);
//        bigiv.setVisibility(View.GONE);
        ContentResolver resolver = getContentResolver();

        Cursor cursor = resolver.query(Uri.parse("content://cn.drake.imageprovider/single"),
                new String[]{"_id","data"}, null, null, null);
        byte[] buf;
        while (cursor.moveToNext()){
            buf = Base64.decode(cursor.getString(1), 0);

//            Bitmap bitmap = BitmapFactory.decodeByteArray(buf, 0, buf.length);
//            DisplayMetrics dm = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(dm);
//            int screenWidth=dm.widthPixels;
//
//            if(bitmap.getWidth() <= screenWidth){
//                bigiv.setImageBitmap(bitmap);
//            }else{
//                bigiv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, screenWidth,
//                        bitmap.getHeight()*screenWidth/bitmap.getWidth(), true));
//            }
            Bitmap bitmap = BitmapFactory.decodeByteArray(buf, 0, buf.length);
            Bitmap bm = compressImage(bitmap);
            bigiv.setImageBitmap(bm);
            bigiv.setVisibility(View.VISIBLE);
        }
        cursor.close();

    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

        @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        finish();
        return true;
    }
}
