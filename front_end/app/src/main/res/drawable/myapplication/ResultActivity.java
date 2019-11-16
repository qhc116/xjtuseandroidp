package com.example.myapplication;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private List<PictureList> pictureLists;
    private RecyclerView rv;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);

        init();

        readPiclist();

        ininData();

        initView();


    }

    private void init(){
        pictureLists = new ArrayList<>();
    }

    private void ininData(){
        mLayoutManager = new StaggeredGridLayoutManager(2,  StaggeredGridLayoutManager.VERTICAL);
        mAdapter = new ResultAdapter(pictureLists);
    }

    private void initView(){
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(mLayoutManager);
        rv.setAdapter(mAdapter);
    }

    private void readPiclist(){
        ContentResolver resolver = getContentResolver();

        Cursor cursor = resolver.query(Uri.parse("content://cn.drake.imageprovider/multi"),
                new String[]{"_id","data", "resultJson"}, null, null, null);

        while(cursor.moveToNext()){
            byte[] buf = Base64.decode(cursor.getString(1), 0);
            PictureList aPicture = new PictureList(buf);
            aPicture.setResultJson(cursor.getString(2));
            pictureLists.add(aPicture);

        }
    }
}
