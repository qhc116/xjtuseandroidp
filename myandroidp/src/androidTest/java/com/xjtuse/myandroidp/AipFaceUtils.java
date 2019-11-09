package com.xjtuse.myandroidp;

import android.util.Log;

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

    static JSONObject multiSearch(File imagePath, HashMap<String, String> options) throws JSONException {
        String image = readImagePath2Base64(imagePath);
        JSONObject res = client.multiSearch(image, imageType, groupId, options);
        return res;
    }

    static JSONObject detect(File imagePath, HashMap<String, String> options) throws JSONException {
        String image = readImagePath2Base64(imagePath);
        JSONObject res = client.detect(image, imageType, options);
        return res;
    }

    private static String readImagePath2Base64(File imagePath) {
        try {
            InputStream is = new FileInputStream(imagePath);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();
            String image = new String(Base64.getEncoder().encode(bytes));
            return  image;
        } catch (Exception e) {
            Log.i(TAG,"文件不存在");
            e.printStackTrace();
        }
        return  null;
    }
}
