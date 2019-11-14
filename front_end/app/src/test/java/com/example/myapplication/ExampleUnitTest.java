package com.example.myapplication;

import android.renderscript.ScriptGroup;


import com.baidu.aip.face.AipFace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private static final String APP_ID = "17625304";
    private static final String API_KEY = "ZMl7vt24zlnkE0wgkGhauwfR";
    private static final String SECRET_KEY = "qoC86MwzOwLRXZAub1G5KKZZ3pzkQFUQ";
    private AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
    private Object FileInputStream;

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void substring(){
        String a = "[123213123123123123123123]";
        System.out.println(a.substring(1, a.length()-1));
    }

    @Test
    public void dateTest(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());
        String cur = format.format(curDate);
        String str = "2019年11月6日";
        try {
            Date bigDate = format.parse(str);
            System.out.println(curDate + ((curDate.compareTo(bigDate))>0?"is":"is not")+"bigger than"+bigDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void dateSort() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());
        String str = "2019年11月6日";
        HashMap<Date, String> map = new HashMap<>();
        map.put(curDate, "111fdsf");
        map.put(format.parse(str), "321323");

        List<Map.Entry<Date, String>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));

        for(Map.Entry<Date, String> mapping:list){
            System.out.println(mapping.getKey()+":"+mapping.getValue());
        }
    }

    @Test
    public void subList(){
        List<String> t = new ArrayList<>();
        t.add("dsad");
        t.add("321sdf");
        t.add("dsffdsfs");
        int s = t.size();
        for(int i = 0;i < s;i++){
            t.add("fdsfsda");
        }
        t.subList(0, s).clear();
        System.out.println(t);
    }

    @Test
    public void registerT() throws Exception {
        FileInputStream  filein= new FileInputStream("C:\\Users\\Administrator\\Desktop\\face1.jpg");
        byte[] buf = convertToBytes(filein);
        String image = Base64.getEncoder().encodeToString(buf);
        HashMap<String, String> options = new HashMap<>();
        options.put("user_info", "user's info");
        options.put("quality_control", "NONE");
        options.put("liveness_control", "NONE");
        options.put("action_type", "APPEND");



        String imageType = "BASE64";
        String groupId = "caixukun";
        String userId = "user1";

        JSONObject res = client.updateUser(image, imageType, groupId, userId, options);
        System.out.println(res.toString(2));

    }

    @Test
    public void nMatch() throws Exception {
        HashMap<String, String> options = new HashMap<>();
        options.put("max_face_num", "3");
        options.put("match_threshold", "20");
        options.put("quality_control", "NONE");
        options.put("liveness_control", "NONE");
        options.put("max_user_num", "3");

        FileInputStream  filein= new FileInputStream("C:\\Users\\Administrator\\Desktop\\noface.jpg");
        byte[] buf = convertToBytes(filein);
        String image = Base64.getEncoder().encodeToString(buf);
        String imageType = "BASE64";
        String groupIdList = "caixukun";

        JSONObject res = client.multiSearch(image, imageType, groupIdList, options);
        System.out.println(res.toString(2));
    }
    @Test
    public void sampleT() throws JSONException {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<>();
        options.put("start", "0");
        options.put("length", "50");

        String groupId = "caixukun";

        // 获取用户列表
        JSONObject res = client.getGroupUsers(groupId, options);
        System.out.println(res.toString(2));
    }

    private byte[] convertToBytes(InputStream inputStream) throws Exception{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        inputStream.close();
        return  out.toByteArray();
    }



}