package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PictureList {
    /*
    记录图片的位图信息
     */
    private Bitmap bitmap;
    private String userField;
    private String id;

    public String getDateInfo() {
        return dateInfo;
    }

    public void setDateInfo(String dateInfo) {
        this.dateInfo = dateInfo;
    }

    private String dateInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUserField() {
        return userField;
    }

    public void setUserField(String userField) {
        this.userField = userField;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    
    public PictureList(byte[] picBuf) {
        bitmap = BitmapFactory.decodeByteArray(picBuf, 0, picBuf.length);
    }
}
