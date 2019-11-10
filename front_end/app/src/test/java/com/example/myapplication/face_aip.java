package com.example.myapplication;

import android.util.Log;

import com.baidu.aip.face.AipFace;

import org.json.JSONObject;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
//import android.util.Base64;
import java.util.Base64;
import java.util.HashMap;

public class face_aip {
    public static final String APP_ID = "17625304";
    public static final String API_KEY = "ZMl7vt24zlnkE0wgkGhauwfR";
    public static final String SECRET_KEY = "qoC86MwzOwLRXZAub1G5KKZZ3pzkQFUQ";

    @Test
    public void test1() throws Exception {

        InputStream inputStream=null;
        inputStream = new FileInputStream("D:/th.JPG");

        byte[] buf = new byte[inputStream.available()];
        inputStream.read(buf);
//        System.out.println("数组大小..."+buf.length/(1024));
        String image = new String(Base64.getEncoder().encode(buf));
        //初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
        //唯一核心，创建client后调用方法，得到json对象

        HashMap<String, String> options = new HashMap<>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);


        // 调用接口
//        String image = "取决于image_type参数，传入BASE64字符串或URL字符串或FACE_TOKEN字符串";
        String imageType = "BASE64";

        // 人脸检测,image,imageType是必选参数
        JSONObject res = client.detect(image, imageType, options);
        System.out.println(res.toString(2));
    }


}
