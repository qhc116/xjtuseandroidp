package com.example.myapplication;

import net.coobird.thumbnailator.Thumbnails;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Base64;

public class TunmnailTest {
    @Test
    public void test1() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        URL url = Thread.currentThread().getContextClassLoader()
                .getResource("C:\\Users\\Administrator\\Desktop\\face4.jpg");

        //压缩后转向到内存中
        Thumbnails
                .of(url)//可以是文件名或输入流
                .size(100, 100)
                .rotate(90)//转90度
                .keepAspectRatio(true)//保持比例(默认)
                .toOutputStream(out);

        //将压缩后的图片变成Base64
        String s= Base64.getEncoder().encodeToString(out.toByteArray());
        System.out.println(s);

    }
}
