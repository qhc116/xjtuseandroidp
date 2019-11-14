package com.example.myapplication.utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.baidu.aip.face.AipFace;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;

public class AipFaceUtils {
    private static final String TAG = "TAG";
    static String APP_ID = "17623743";
    static String API_KEY = "GEbKIip9cumjdQWZ2xsARaZR";
    static String SECRET_KEY = "usng5UP2bPRz58dkNz05DYntNGXRmQHf";
    static AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
    static String imageType = "BASE64";
    static String groupId = "allstar";

    public static JSONObject multiSearch(byte[] bytes) throws JSONException {
        String image = Base64Util.encode(bytes);
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("max_face_num", "10");
        options.put("match_threshold", "10");
        options.put("quality_control", "NONE");
        options.put("liveness_control", "NONE");
        options.put("max_user_num", "1");
        JSONObject res = client.multiSearch(image, imageType, groupId, options);
        return res;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static JSONObject detect(byte[] bytes, HashMap<String, String> options) throws JSONException {
        String image = Base64Util.encode(bytes);
        JSONObject res = client.detect(image, imageType, options);
        return res;
    }
}
