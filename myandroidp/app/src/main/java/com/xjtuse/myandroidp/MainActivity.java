package com.xjtuse.myandroidp;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EdgeEffect;
import android.widget.EditText;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("T",this.getFilesDir().toString());
    }

    public void login(View view) {
        File fileDir = new File(this.getFilesDir(), "user");
        EditText userName = findViewById(R.id.userName);
        EditText password = findViewById(R.id.password);

        try(OutputStream outputStream=
                    openFileOutput("aa.txt",MODE_PRIVATE|MODE_APPEND)) {
            String s = (userName.getText() + "+++" + password.getText() + "\n");
            outputStream.write(s.getBytes());
            Log.i("T","OK");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
