package com.example.myapplication;

import android.util.Base64;


import com.baidu.aip.face.AipFace;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;


public class Aip_faceDetect {
    private static final String APP_ID = "17623743";
    private static final String API_KEY = "GEbKIip9cumjdQWZ2xsARaZR";
    private static final String SECRET_KEY = "usng5UP2bPRz58dkNz05DYntNGXRmQHf";
    private AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

    public JSONObject detect(byte[] fileBuf) throws JSONException {


        String image = new String(Base64.encode(fileBuf, 0));
        String imageType = "BASE64";

        //写入设置
        HashMap<String, String> options = new HashMap<>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");

        return client.detect(image, imageType, options);
    }

    public JSONObject nMatch(String base_data){
        HashMap<String, String> options = new HashMap<>();
        options.put("max_face_num", "3");
        options.put("match_threshold", "75");
        options.put("quality_control", "LOW");
        options.put("liveness_control", "NONE");
        options.put("max_user_num", "3");

        String imageType = "BASE64";
        String groupIdList = "users";

        return client.multiSearch(base_data, imageType, groupIdList, options);
    }

    public boolean updateUser(String base_data, int num){
        HashMap<String, String> options = new HashMap<>();
        options.put("user_info", "user's info");
        options.put("quality_control", "NONE");
        options.put("liveness_control", "NONE");
        options.put("action_type", "REPLACE");

        String image = base_data;
        String imageType = "BASE64";
        String groupId = "users";
        String userId = "user"+num;

        JSONObject res = client.updateUser(image, imageType, groupId, userId, options);
        return res.isNull("face_token");
    }

    public int getUserListLenth() throws JSONException {
        String groupId = "users";

        HashMap<String, String> options = new HashMap<String, String>();
        options.put("start", "0");
        options.put("length", "50");

        JSONObject jsonObject = client.getGroupUsers(groupId, options);

        JSONObject midWare = new JSONObject(jsonObject.getString("result"));
        JSONArray jsonArray = new JSONArray(midWare.getString("user_id_list"));

        return jsonArray.length();
    }
}

