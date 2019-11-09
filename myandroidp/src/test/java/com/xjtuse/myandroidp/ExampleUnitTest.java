package com.xjtuse.myandroidp;

import com.baidu.aip.face.AipFace;

import net.coobird.thumbnailator.Thumbnails;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void test5() throws Exception {
        String path = "C:\\Users\\86155\\Desktop\\leijun.jpg";
        OkHttpClient client = new OkHttpClient();

        //上传文件域的请求体部分
        RequestBody formBody = RequestBody
                .create(new File(path), MediaType.parse("image/jpeg"));

        //整个上传的请求体部分（普通表单+文件上传域）
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "Square Logo")
                //filename:avatar,originname:abc.jpg
                .addFormDataPart("avatar", "abc.jpg", formBody)
                .build();

        Request request = new Request.Builder()
                .url("http://114.55.66.103:8000/upload")
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }

    @Test
    public void Sample() throws Exception {
        //设置APPID/AK/SK
        String APP_ID = "17623743";
        String API_KEY = "GEbKIip9cumjdQWZ2xsARaZR";
        String SECRET_KEY = "usng5UP2bPRz58dkNz05DYntNGXRmQHf";

        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        InputStream is = new FileInputStream("C:\\Users\\86155\\Desktop\\全明星\\leijun.jpg");
        byte[] bytes = new byte[is.available()];
        is.read(bytes);
        String s = new String(Base64.getEncoder().encode(bytes));

        // 调用接口
        String image = s;
        String imageType = "BASE64";


        // 人脸检测
        JSONObject res = client.detect(image, imageType, null);
        try {
            System.out.println(res.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //人脸库人脸注册
    @Test
    public void sample() throws IOException, JSONException, InterruptedException {
        String APP_ID = "17623743";
        String API_KEY = "GEbKIip9cumjdQWZ2xsARaZR";
        String SECRET_KEY = "usng5UP2bPRz58dkNz05DYntNGXRmQHf";
        InputStream is;

        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
        File file = new File("C:\\Users\\86155\\Desktop\\allstar - 副本\\caixukun\\");
        String[] list = file.list();


        for (String s : list) {
            //String[] split = s.split(".");

            is = new FileInputStream("C:\\Users\\86155\\Desktop\\allstar - 副本\\caixukun\\" + s);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);

            String image = new String(Base64.getEncoder().encode(bytes));
            String imageType = "BASE64";
            String groupId = "users";
            String userId = "caixukun";

            JSONObject res = client.addUser(image, imageType, groupId, userId, null);
            System.out.println(res.toString(2));
            Thread.sleep(2000);
        }
    }


    @Test
    public void sample1() throws IOException, JSONException, InterruptedException {
        String APP_ID = "17623743";
        String API_KEY = "GEbKIip9cumjdQWZ2xsARaZR";
        String SECRET_KEY = "usng5UP2bPRz58dkNz05DYntNGXRmQHf";
        InputStream is;

        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
        File file = new File("C:\\Users\\86155\\Desktop\\全明星");
        String[] list = file.list();


        is = new FileInputStream("C:\\Users\\86155\\Desktop\\heying3.jpg");
        byte[] bytes = new byte[is.available()];
        is.read(bytes);

        String image = new String(Base64.getEncoder().encode(bytes));
        String imageType = "BASE64";
        String groupId = "allstar";

        //JSONObject res = client.multiSearch(image, imageType, groupId, null);
        JSONObject res = client.detect(image, imageType, null);
        System.out.println(res.toString(2));
    }

    @Test
    public void multiSearch() throws IOException, JSONException {
        File file = new File("C:\\Users\\86155\\Desktop\\heying4.jpg");
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("max_face_num", "10");
        options.put("match_threshold", "10");
        options.put("quality_control", "NONE");
        options.put("liveness_control", "NONE");
        options.put("max_user_num", "1");
        JSONObject res = AipFaceUtils.multiSearch(file, options);

        System.out.println(res);
    }

    @Test
    public void test1() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("C:\\Users\\86155\\Desktop\\heying2.jpg");

        //压缩后转向到内存中
        Thumbnails
                .of(resourceAsStream)//可以是文件名或输入流
                .size(100, 100)
                .rotate(90)//转90度
                .keepAspectRatio(true)//保持比例(默认)
                .toOutputStream(out);

        //将压缩后的图片变成Base64
        String s = Base64.getEncoder().encodeToString(out.toByteArray());
        System.out.println(s);

    }


}