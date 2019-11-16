package com.example.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.baidu.aip.bodyanalysis.AipBodyAnalysis;

import org.json.JSONException;
import org.json.JSONObject;

public class AipBodyUtils {
    private static final String TAG = "TAG";
    static String APP_ID = "17766727";
    static String API_KEY = "TlNwx35p8pIzfD6ik5IsyGwW";
    static String SECRET_KEY = "OKGeTBlMI4BBIhhjetNVafSfGTfwBPf1";

    public static Bitmap getCertificationPhoto(byte[] bytes) throws JSONException {
        // 初始化一个AipBodyAnalysis
        AipBodyAnalysis client = new AipBodyAnalysis(APP_ID, API_KEY, SECRET_KEY);
        JSONObject res = client.bodySeg(bytes, null);
        byte[] foregrounds = Base64.decode(res.getString("foreground"), 0);
        return BitmapFactory.decodeByteArray(foregrounds,0,foregrounds.length);
    }
}
