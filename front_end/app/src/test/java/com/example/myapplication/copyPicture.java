package com.example.myapplication;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

public class copyPicture {
    @Test
    public void test1() throws Exception{
        InputStream inputStream= null;
        inputStream = new FileInputStream("D:\\23.TXT");
        OutputStream outputStream=new FileOutputStream("D:/24.TXT");


        byte[] buf= new byte[0];
        buf = new byte[inputStream.available()];
        inputStream.read(buf);


//        String s=new String(Base64.getEncoder().encode(buf));

//        byte[] buf1 = Base64.getDecoder().decode(s);
        outputStream.write(buf);

        outputStream.close();
        inputStream.close();
    }
}
