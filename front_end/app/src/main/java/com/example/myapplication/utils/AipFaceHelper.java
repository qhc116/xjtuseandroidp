package com.example.myapplication.utils;

import com.baidu.aip.face.AipFace;


public class AipFaceHelper {
    //获取Aip client
    public static AipFace getClient(){
        String APP_ID = "17623743";
        String API_KEY = "GEbKIip9cumjdQWZ2xsARaZR";
        String SECRET_KEY = "usng5UP2bPRz58dkNz05DYntNGXRmQHf";
        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
        return client;
    }



}
