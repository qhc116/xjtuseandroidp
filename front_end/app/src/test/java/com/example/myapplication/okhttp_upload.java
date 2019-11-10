package com.example.myapplication;

import org.junit.Test;


import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class okhttp_upload {
    @Test
    public void test1() throws Exception{
        String path="D:/22.JPG";
        OkHttpClient client=new OkHttpClient();

        //上传文件域的请求体部分
        RequestBody formBody=  RequestBody
                .create(new File(path), MediaType.parse("image/jpeg"));

        //整个上传的请求体部分（普通表单+文件上传域）
        RequestBody requestBody=new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "Square Logo")
                //filename:avatar,originname:abc.jpg
                .addFormDataPart("avatar", "abc.jpg",formBody)
                .build();

        Request request = new Request.Builder()
                .url("http://localhost:8000/upload")
                .post(requestBody)
                .build();
        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }
}
