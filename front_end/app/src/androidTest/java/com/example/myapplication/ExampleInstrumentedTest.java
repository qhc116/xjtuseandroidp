package com.example.myapplication;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import net.coobird.thumbnailator.Thumbnails;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Base64;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.example.myapplication", appContext.getPackageName());
    }

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
