package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.chad.library.adapter.base.BaseQuickAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.lang.Thread;
import java.util.Map;


public class YunDiskActivity extends AppCompatActivity {

    //从网络请求资源的id
    private List<String> filesId;
    private List<PictureList> pictureLists;
    //记录id和图片资源位置的映射
    private HashMap<String, Integer> Id2Pic;
    //记录id和时间顺序的映射
    private HashMap<String, Date> datesOrder;
    //记录人脸分组和图片id列表的映射
    private HashMap<String, List<String>> faceGroup2Id;
    private SimpleDateFormat sdf;

    private QuickAdapter mAdapter;

    private String downloadUrl;
    private String filesUrl;

    private RecyclerView rv;
    private GridDividerItemDecoration splitDecoratio;
    private OkHttpClient client;
    private int filCount;
    private boolean needRefresh;

    private boolean threadCanWork;

    private boolean multiSelectStatus;
    SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    private String token;

    //控制两种排序网格布局中item的尺寸
    private List<Integer> faceGroupItemSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yun_disk);
        
        //成员初始化
        init();
        
        //进入时先从服务器请求文件名
        new Thread(FetchFileName).start();

        //创建适配器
        //这里将要控制的布局和数据一起传入，内部控制holder的创建和绑定
        mAdapter = new QuickAdapter(R.layout.picture_layout, pictureLists);

        //创建布局管理
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        //当adapter被notify的时候布局管理器会自动检测每个单位应该设置的大小，也就是里面的逻辑并非一次性使用
        mAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                if(faceGroupItemSize.isEmpty()){
                    return 1;
                }
                else
                    return faceGroupItemSize.get(position);
            }
        });
//        LinearLayoutManager manager = new LinearLayoutManager(this);
        rv.setLayoutManager(manager);
        rv.setAdapter(mAdapter);

        rv.setTag("Normal");

        //设置滚动预加载
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(threadCanWork)
                        new Thread(UpdatePicture).start();
                }
            }
        });

        rv.addItemDecoration(new SectionDecoration(this, new SectionDecoration.DecorationCallback() {
            @Override
            public String getGroupUser(int position) {
                return pictureLists.get(position).getUserField();
            }

            @Override
            public String getGroupDate(int position) {
                return pictureLists.get(position).getDateInfo();
            }
        }));
//        rv.addItemDecoration(new GridDividerItemDecoration(this, 20, 15, false));

        //开启动画
        mAdapter.openLoadAnimation();

        //给adapter添加事件控制
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Toast.makeText(YunDiskActivity.this, "点击了第"+(position+1)+"张图片",
                    Toast.LENGTH_SHORT).show();
            if(multiSelectStatus){
                //单击反转checkbox勾选状态
                mAdapter.setBooleanList(position,
                        !mAdapter.getBooleanList(position));

                adapter.notifyItemChanged(position);
                //没有checkbox被勾选时退出多选状态
                if(!mAdapter.hasItemSelected())
                    multiSelectStatus = false;
            }
        });
        
        //长按控制
        mAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            //第一次长按激活勾选功能
            if(!multiSelectStatus){
                mAdapter.setBooleanList(position, true);

                adapter.notifyItemChanged(position);
                Toast.makeText(YunDiskActivity.this, "长按了第" + (position + 1) + "条条目", Toast.LENGTH_SHORT).show();
                //进入多选状态,单击可以勾选
                multiSelectStatus = true;
            }
            return false;
        });

        //启动图片加载
        startLoading();
    }
    
    private void init(){
        filCount = 0;

        filesId = new ArrayList<>();
        pictureLists = new ArrayList<>();
        client = new OkHttpClient();
        datesOrder = new HashMap<>();
        Id2Pic = new HashMap<>();
        faceGroup2Id = new HashMap<>();

        needRefresh = true;
        multiSelectStatus = false;

        token = getIntent().getStringExtra("token");
        Log.i("token", "...."+token);

        mSharedPreferences = getSharedPreferences("data", 0);
        editor = mSharedPreferences.edit();
        rv = findViewById(R.id.rv);

        faceGroupItemSize = new ArrayList<>();

        threadCanWork = true;

        sdf = new SimpleDateFormat("yyyy年MM月dd日");
        
        downloadUrl = "http://114.55.36.148:8000/download";
        filesUrl = "http://114.55.36.148:8000/files";
    }

    //控制ui更新
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //处理图片新加入的更新
                case 1:
                    Bundle data = msg.getData();
                    byte[] buf = Base64.decode(data.getString("value"), 0);
                    String faceUser = data.getString("user");
                    String id = data.getString("id");
                    String dateInfo = data.getString("date");
                    PictureList newPicture = new PictureList(buf);
                    newPicture.setUserField(faceUser);
                    newPicture.setId(id);
                    newPicture.setDateInfo(dateInfo);

                    pictureLists.add(newPicture);
                    mAdapter.notifyItemInserted(pictureLists.size());
//                Log.i("数据更新", "......");
                    Id2Pic.put(id, pictureLists.size()-1);
                    break;
                case 2:
                    mAdapter.notifyDataSetChanged();
                    Log.i("刷新", "..............-----");
                    break;
            }
        }
    };

    //启动图片加载
    private void startLoading() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!filesId.isEmpty()){
            for(int i = 0; i<filesId.size(); i++){
                mAdapter.addBooleanList();
            }
            Log.i("初始化bool值", "....."+mAdapter.getBooleanLenth());


            new Thread(UpdatePicture).start();
        }
    }

    //加载线程
    //获取图片base64与其他信息在这里处理
    Runnable UpdatePicture = new Runnable() {
        @Override
        public void run() {
//            OkHttpClient client = new OkHttpClient();
            if (threadCanWork){
                threadCanWork = false;
                int loadNum = 9;
                int t = filCount+loadNum;
                for(; filCount < t;filCount++){
                    if(filCount>=filesId.size()){
                        if(needRefresh) {
                            Message msg = new Message();
                            msg.what = 2;
                            handler.sendMessage(msg);
                            needRefresh = false;
                        }
                        break;
                    }
                    faceGroupItemSize.add(1);
//                Log.i("目前=", "...."+filCount);
                    String fileId = filesId.get(filCount);
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("id", fileId)
                            .addFormDataPart("token", token)
                            .build();
                    Request request = new Request.Builder()
                            .url(downloadUrl)
                            .post(requestBody)
                            .build();

                    try {
                        Response resp = client.newCall(request).execute();

                        JSONArray jsonArray = new JSONArray(resp.body().string());
                        JSONObject jsonObject = new JSONObject(jsonArray.get(0).toString());

                        Message msg = new Message();
                        Bundle data1 = new Bundle();
                        //获取数据库中的id信息
                        String id = jsonObject.getString("_id");
                        //获得日期信息
                        try {
                            Date dateInfo = sdf.parse(jsonObject.getString("dateinfo"));
                            datesOrder.put(id, dateInfo);
                            data1.putString("date", sdf.format(dateInfo));
//                            Log.i("插入一条", "...时间记录"+dateInfo+":"+id);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //获得人脸分组信息
                        String faceUser = jsonObject.getString("faceuser");
                        //添加进分组中
                        if(faceGroup2Id.containsKey(faceUser))
                            faceGroup2Id.get(faceUser).add(id);
                        else{
                            List<String> stringList = new ArrayList<>();
                            stringList.add(id);
                            faceGroup2Id.put(faceUser, stringList);
                        }
                        //获得base64
                        String data = jsonObject.getString("filedata");
//                    Log.i(".......", data);

                        data1.putString("value", data);
                        data1.putString("id", id);
                        data1.putString("user", faceUser);

                        msg.what = 1;
                        msg.setData(data1);
                        handler.sendMessage(msg);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }

                threadCanWork = true;
            }

        }
    };

    Runnable FetchFileName = new Runnable() {
        @Override
        public void run() {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("token", token)
                    .build();
            Request request = new Request.Builder()
                    .url(filesUrl)
                    .post(requestBody)
                    .build();
            try {
                Response resp = client.newCall(request).execute();
                JSONArray jsonArray = new JSONArray(resp.body().string());

                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject =new JSONObject(jsonArray.get(i).toString());
                    filesId.add(jsonObject.getString("_id"));
                }
//                filesName = Arrays.asList(data.substring(1, data.length()-1).split(","));
//                Log.i("filenames", "........"+filesName);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void resetLayout(View view) {
        rv.setTag("Normal");
        faceGroupItemSize.clear();
        for(int i = 0;i < pictureLists.size();i++)
            faceGroupItemSize.add(1);
        mAdapter.notifyDataSetChanged();

    }

    public void delete(View view) {
        int y = 0;
        int i = 0;
        while(i < mAdapter.getBooleanLenth()){
            if(mAdapter.getBooleanList(i)){
                mAdapter.removeBooleanList(i);
                //删除的时候映射中的数据也要全部删除
                PictureList tempP = pictureLists.get(i);
                List<String> belongGroup = faceGroup2Id.get(tempP.getUserField());
                belongGroup.remove(tempP.getId());
                faceGroup2Id.put(tempP.getUserField(), belongGroup);
                datesOrder.remove(tempP.getId());
                modifyId2Pic(tempP.getId());
                pictureLists.remove(i);
                y++;
                i--;
            }
            i++;
        }
        mAdapter.notifyDataSetChanged();
        String tag =(String) rv.getTag();
        if(tag.equals("Face"))
            sortByFace(rv);
        else if(tag.equals("Time"))
            sortByTime(rv);
        //删除会导致退出多选状态
        if(!mAdapter.hasItemSelected())
            multiSelectStatus = false;

        if(y == 0){
            Toast.makeText(YunDiskActivity.this, "没有要删除的元素", Toast.LENGTH_SHORT).show();
        }
    }

    public void modifyId2Pic(String id){
        int indexCutLine = Id2Pic.get(id);
        Id2Pic.remove(id);
        List<Map.Entry<String, Integer>> list = new ArrayList<>(Id2Pic.entrySet());
        for(Map.Entry<String, Integer> item: list){
            if(item.getValue()>indexCutLine){
                Id2Pic.put(item.getKey(), item.getValue()-1);
            }
        }

    }

    public void sortByTime(View view) {
        rv.setTag("Time");
        faceGroupItemSize.clear();
        //当前日期排序没有和checkbox完成同步
        //按日期排序
        List<Map.Entry<String, Date>> list = new ArrayList<>(datesOrder.entrySet());
//        Log.i("date与id", "....长"+list.size());
        Collections.sort(list, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));

        //根据对应的id顺序调整资源顺序
        int startIndex = pictureLists.size();
        int GroupNumCount = 0;
        int newIndex = 0;
        Date nowDate=null;
        for(Map.Entry<String, Date> item:list){
            if(nowDate==null){
                nowDate = item.getValue();
                GroupNumCount++;
                faceGroupItemSize.add(1);
            }else{
                if(item.getValue().compareTo(nowDate)!=0){
                    nowDate = item.getValue();
                    faceGroupItemSize.remove(faceGroupItemSize.size()-1);
                    if(GroupNumCount%4 != 0)
                        faceGroupItemSize.add(5 - (GroupNumCount % 4));
                    else
                        faceGroupItemSize.add(1);
                    faceGroupItemSize.add(1);
                    GroupNumCount = 1;
                }else{
                    faceGroupItemSize.add(1);
                    GroupNumCount++;
                }
            }
            //以id取出对应资源所在位置
            int OldIndex = Id2Pic.get(item.getKey());
            //按新的顺序修改id记录的资源位置
            Id2Pic.put(item.getKey(), newIndex++);
            pictureLists.add(pictureLists.get(OldIndex));
        }
        if (startIndex > 0) {
            pictureLists.subList(0, startIndex).clear();
        }
        Log.i("piclist...", "当前长度"+pictureLists.size());
        mAdapter.notifyDataSetChanged();
    }

    public void sortByFace(View view) {
        rv.setTag("Face");
        faceGroupItemSize.clear();
        List<Map.Entry<String, List<String>>> list = new ArrayList<>(faceGroup2Id.entrySet());
        int newIndex = 0;
        int startIndex = pictureLists.size();
        int listLenth;

        for(Map.Entry<String, List<String>> item: list){
            List<String> id_list = item.getValue();
            listLenth = id_list.size();
            if(listLenth==0) {
                //列表为空后删除
                faceGroup2Id.remove(item.getKey());
                continue;
            }
            for(String id: id_list){
                faceGroupItemSize.add(1);
                //以id取出对应资源所在位置
                int OldIndex = Id2Pic.get(id);
                //按新的顺序修改id记录的资源位置
                Id2Pic.put(id, newIndex++);
                pictureLists.add(pictureLists.get(OldIndex));
            }
            //计算当前组最后一个应占的spanSize
            int lastSpan;
            if(listLenth%4 != 0)
                lastSpan = 5 - (listLenth % 4);
            else
                lastSpan = 1;
            faceGroupItemSize.remove(faceGroupItemSize.size()-1);
            faceGroupItemSize.add(lastSpan);
        }
        if (startIndex > 0) {
            pictureLists.subList(0, startIndex).clear();
        }
        Log.i("piclist...", "当前长度"+pictureLists.size());
        mAdapter.notifyDataSetChanged();
    }

    public void download(View view) {

    }
}
