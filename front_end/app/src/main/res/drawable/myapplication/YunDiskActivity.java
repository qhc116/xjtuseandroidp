package com.example.myapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.myapplication.ExplosionField.ExplosionField;
import com.example.myapplication.utils.Base64Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class YunDiskActivity extends AppCompatActivity {
    private static final int DOWNLOAD_SUCCESS = 1;
    private static final int DOWNLOAD_FINISH = 2;

    //从网络请求资源的id
    private List<String> filesId;
    //图片的资源列表
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
    private OkHttpClient client;
    private int filCount;
    private EditText editText;
    private String username;

    //是否需要刷新
    private boolean needRefresh;
    //控制同一时间只有一个线程在网络请求
    private boolean threadCanWork;
    //图片的多选状态
    private boolean multiSelectStatus;
    SharedPreferences mSharedPreferences;

    //用户登录的token信息
    private String token;

    //排序网格布局中item的尺寸
    private List<Integer> faceGroupItemSize;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private ExplosionField explosionField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_container);
        setContentView(R.layout.new_yun_disk_activity);

        //成员初始化
        init();
        initMenu();

        //设置键盘响应
        setKeyListener();

        //进入时先从服务器请求文件名
        new Thread(FetchFileName).start();

        //设置适配器
        initAdapter();

        //启动图片加载
        startLoading();

        //设置元素删除动画
       explosionField = new ExplosionField(this);
        explosionField.addListener(findViewById(R.id.rv));
    }
    /*  *//**
     * 餐单结构的创建
     * @param menu
     * @return
     *//*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    */

    /**
     * 处理菜单单击事件
     *
     * @param item
     * @return
     *//*
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            //ListView部分
            case R.id.sortByface:
                Log.d("abc", "点击了ListView中的垂直标准");
                sortByFace(rv);
                break;
            case R.id.sortBytime:
                sortByTime(rv);
                break;
            case R.id.normalOrder:
                resetLayout(rv);
                break;
            case R.id.allStar:

                break;
        }
        return super.onOptionsItemSelected(item);
    }
*/
    private void initMenu() {
        toolbar = findViewById(R.id.yun_toolbar_tb);
        drawerLayout = findViewById(R.id.yun_drawer_layout);
        bottomNavigationView = findViewById(R.id.yun_nav_view);
        navigationView = findViewById(R.id.yun_nav_left);

        //点击开启侧滑菜单
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        //底部导航条的点击事件
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.sortByface:
                        sortByFace(rv);
                        break;
                    case R.id.delete:
                        delete(rv);
                        break;
                }
                return true;
            }
        });

        //侧滑菜单的点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.sortBytime:
                        sortByTime(rv);
                        break;
                    case R.id.sortByface:
                        sortByFace(rv);
                        break;
                    case R.id.normalOrder:
                        resetLayout(rv);
                        break;
                        case R.id.menu_return:
                        Intent intent = new Intent(YunDiskActivity.this, HomeActivity.class);
                        startActivity(intent);
                        break;
                } return true;
            }
        });
    }


    private void initAdapter() {
        //创建适配器
        mAdapter = new QuickAdapter(R.layout.picture_layout, pictureLists);

        //创建布局管理
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        mAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                if (faceGroupItemSize.isEmpty()) {
                    return 1;
                } else
                    return faceGroupItemSize.get(position);
            }
        });
        rv.setLayoutManager(manager);
        rv.setAdapter(mAdapter);
        rv.setTag("Normal");

        //设置滚动加载
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (threadCanWork)
                        new Thread(UpdatePicture).start();
                }
            }
        });

        //添加装饰器，绘制排序的组别信息
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

        //开启动画
        mAdapter.openLoadAnimation();

        //给adapter添加事件控制
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(YunDiskActivity.this, "点击了第" + (position + 1) + "张图片",
                        Toast.LENGTH_SHORT).show();
                if (multiSelectStatus) {
                    //单击反转checkbox勾选状态
                    mAdapter.setBooleanList(position,
                            !mAdapter.getBooleanList(position));

                    adapter.notifyItemChanged(position);
                    //没有checkbox被勾选时退出多选状态
                    if (!mAdapter.hasItemSelected()) {
                        multiSelectStatus = false;
                    }

                } else {
                    //非多选状态下点击放大图片
                    Intent intent = new Intent(YunDiskActivity.this, ImageShower.class);

                    Bitmap bitmap = pictureLists.get(position).getBitmap();

                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 20, output);
                    byte[] buf = output.toByteArray();

                    ContentValues values = new ContentValues();
                    values.put("data", Base64.encodeToString(buf, 0));
                    //通过resolver插入数据
                    ContentResolver resolver = YunDiskActivity.this.getContentResolver();
                    Uri uri = Uri.parse("content://cn.drake.imageprovider/single");
                    //插入前先清除上一次的数据
                    resolver.delete(uri, "", new String[]{});
                    resolver.insert(uri, values);

                    YunDiskActivity.this.startActivity(intent);
                }
            }
        });

        //长按控制
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                //第一次长按激活勾选功能
                if (!multiSelectStatus) {
                    mAdapter.setBooleanList(position, true);

                    adapter.notifyItemChanged(position);
//                Toast.makeText(YunDiskActivity.this, "长按了第" + (position + 1) + "条条目", Toast.LENGTH_SHORT).show();
                    //进入多选状态,单击可以勾选
                    multiSelectStatus = true;
                }
                return false;
            }
        });
    }

    private void init() {
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
        Log.i("Get token", "...." + token);

        mSharedPreferences = getSharedPreferences("data", 0);
        rv = (RecyclerView) findViewById(R.id.rv);

        faceGroupItemSize = new ArrayList<>();

        threadCanWork = true;

        sdf = new SimpleDateFormat("yyyy年MM月dd日");

        editText = (EditText) findViewById(R.id.etx);



        username = getIntent().getStringExtra("username");

        downloadUrl = "http://114.55.36.148:8000/download";
        filesUrl = "http://114.55.36.148:8000/files";
    }

    /**
     * 线程从服务器获得请求结果时更新UI
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNLOAD_SUCCESS:
                    Bundle data = msg.getData();
                    byte[] buf = Base64.decode(data.getString("value"), 0);
                    String faceUser = data.getString("user");
                    String id = data.getString("id");
                    String dateInfo = data.getString("date");
                    String resultJson = data.getString("resultJson");
                    PictureList newPicture = new PictureList(buf);
                    newPicture.setUserField(faceUser);
                    newPicture.setId(id);
                    newPicture.setDateInfo(dateInfo);
                    newPicture.setResultJson(resultJson);
//                    Log.i("resultJson", "...."+resultJson);

                    pictureLists.add(newPicture);
                    mAdapter.notifyItemInserted(pictureLists.size());
                    Id2Pic.put(id, pictureLists.size() - 1);
                    break;
                case DOWNLOAD_FINISH:
                    mAdapter.notifyDataSetChanged();
                    Log.i("刷新", "..............-----");
                    break;
            }
        }
    };

    /**
     * 进入云相册时启动加载图片功能
     */
    private void startLoading() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!filesId.isEmpty()) {
            for (int i = 0; i < filesId.size(); i++) {
                mAdapter.addBooleanList();
            }
//            Log.i("初始化bool值", "....."+mAdapter.getBooleanLenth());


            new Thread(UpdatePicture).start();
        }
    }

    /**
     * 云相册中根据单个资源id，向服务器请求图片资源的Runnable对象
     */
    Runnable UpdatePicture = new Runnable() {
        @Override
        public void run() {
            if (threadCanWork) {
                threadCanWork = false;
                int loadNum = 9;
                int t = filCount + loadNum;
                for (; filCount < t; filCount++) {
                    if (filCount >= filesId.size()) {
                        if (needRefresh) {
                            Message msg = new Message();
                            msg.what = DOWNLOAD_FINISH;
                            handler.sendMessage(msg);
                            needRefresh = false;
                        }
                        break;
                    }
                    faceGroupItemSize.add(1);
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
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        //获得人脸分组信息
                        String faceUser = jsonObject.getString("faceuser");
                        //添加进分组中
                        if (faceGroup2Id.containsKey(faceUser))
                            faceGroup2Id.get(faceUser).add(id);
                        else {
                            List<String> stringList = new ArrayList<>();
                            stringList.add(id);
                            faceGroup2Id.put(faceUser, stringList);
                        }
                        //获得base64
                        String data = jsonObject.getString("filedata");
                        //获取resultJson
                        String resultJson = jsonObject.getString("resultJson");

                        data1.putString("resultJson", resultJson);
                        data1.putString("value", data);
                        data1.putString("id", id);
                        data1.putString("user", faceUser);

                        msg.what = DOWNLOAD_SUCCESS;
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

    /**
     * 进入云相册时用于获取图片资源id的Runnable对象
     */
    Runnable FetchFileName = new Runnable() {
        @Override
        public void run() {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", username)
                    .addFormDataPart("token", token)
                    .build();
            Request request = new Request.Builder()
                    .url(filesUrl)
                    .post(requestBody)
                    .build();
            try {
                Response resp = client.newCall(request).execute();
                JSONArray jsonArray = new JSONArray(resp.body().string());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    filesId.add(jsonObject.getString("_id"));
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 通过重设每个图片在网格布局中所占格数来恢复普通布局
     *
     * @param view
     */
    public void resetLayout(View view) {
        rv.setTag("Normal");
        faceGroupItemSize.clear();
        for (int i = 0; i < pictureLists.size(); i++)
            faceGroupItemSize.add(1);
        mAdapter.notifyDataSetChanged();

    }

    public void delete(View view) {
        int y = 0;
        int i = 0;
        while (i < mAdapter.getBooleanLenth()) {

            if (mAdapter.getBooleanList(i)) {
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
        String tag = (String) rv.getTag();
        if (tag.equals("Face"))
            sortByFace(rv);
        else if (tag.equals("Time"))
            sortByTime(rv);
        //删除会导致退出多选状态
        if (!mAdapter.hasItemSelected())
            multiSelectStatus = false;


        if (y == 0) {
            Toast.makeText(YunDiskActivity.this, "没有要删除的元素", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 删除图片时同步修改资源顺序
     *
     * @param id
     */
    public void modifyId2Pic(String id) {
        int indexCutLine = Id2Pic.get(id);
        Id2Pic.remove(id);
        List<Map.Entry<String, Integer>> list = new ArrayList<>(Id2Pic.entrySet());
        for (Map.Entry<String, Integer> item : list) {
            if (item.getValue() > indexCutLine) {
                Id2Pic.put(item.getKey(), item.getValue() - 1);
            }
        }

    }

    /**
     * 依据图片的时间排序，并根据 (id, Date) 及(id, index)的映射重排资源顺序
     * 再通过Adapter重新显示图片
     *
     * @param view
     */
    public void sortByTime(View view) {
        rv.setTag("Time");
        faceGroupItemSize.clear();
        //当前日期排序没有和checkbox完成同步
        //按日期排序
        List<Map.Entry<String, Date>> list = new ArrayList<>(datesOrder.entrySet());
//        Log.i("date与id", "....长"+list.size());
        Collections.sort(list, new Comparator<Map.Entry<String, Date>>() {
            @Override
            public int compare(Map.Entry<String, Date> o1, Map.Entry<String, Date> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        //根据对应的id顺序调整资源顺序
        int startIndex = pictureLists.size();
        int GroupNumCount = 0;
        int newIndex = 0;
        Date nowDate = null;
        for (Map.Entry<String, Date> item : list) {
            if (nowDate == null) {
                nowDate = item.getValue();
                GroupNumCount++;
                faceGroupItemSize.add(1);
            } else {
                if (item.getValue().compareTo(nowDate) != 0) {
                    nowDate = item.getValue();
                    faceGroupItemSize.remove(faceGroupItemSize.size() - 1);
                    if (GroupNumCount % 4 != 0)
                        faceGroupItemSize.add(5 - (GroupNumCount % 4));
                    else
                        faceGroupItemSize.add(1);
                    faceGroupItemSize.add(1);
                    GroupNumCount = 1;
                } else {
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
        Log.i("piclist...", "当前长度" + pictureLists.size());
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 依据图片的人脸分组，并根据 (faceUser, id_list)的映射重排资源顺序
     * 再通过Adapter重新显示图片
     *
     * @param view
     */
    public void sortByFace(View view) {
        rv.setTag("Face");
        faceGroupItemSize.clear();
        List<Map.Entry<String, List<String>>> list = new ArrayList<>(faceGroup2Id.entrySet());
        int newIndex = 0;
        int startIndex = pictureLists.size();
        int listLenth;

        for (Map.Entry<String, List<String>> item : list) {
            List<String> id_list = item.getValue();
            listLenth = id_list.size();
            if (listLenth == 0) {
                //列表为空后删除
                faceGroup2Id.remove(item.getKey());
                continue;
            }
            for (String id : id_list) {
                faceGroupItemSize.add(1);
                //以id取出对应资源所在位置
                int OldIndex = Id2Pic.get(id);
                //按新的顺序修改id记录的资源位置
                Id2Pic.put(id, newIndex++);
                pictureLists.add(pictureLists.get(OldIndex));
            }
            //计算当前组最后一个应占的spanSize
            int lastSpan;
            if (listLenth % 4 != 0)
                lastSpan = 5 - (listLenth % 4);
            else
                lastSpan = 1;
            faceGroupItemSize.remove(faceGroupItemSize.size() - 1);
            faceGroupItemSize.add(lastSpan);
        }
        if (startIndex > 0) {
            pictureLists.subList(0, startIndex).clear();
        }
        Log.i("piclist...", "当前长度" + pictureLists.size());
        mAdapter.notifyDataSetChanged();
    }

    public void download(View view) {

    }

    /**
     * 监听搜索栏的输入响应
     */
    private void setKeyListener() {

        View.OnKeyListener OnKey = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    toSearchResult(v);
                    return true;
                } else
                    return false;
            }
        };
        editText.setOnKeyListener(OnKey);
    }

    /**
     * 搜索成功时跳转到结果页面
     *
     * @param view
     */
    public void toSearchResult(View view) {
        EditText etx = (EditText) findViewById(R.id.etx);
        String query = etx.getEditableText().toString();
        List<String> id_list = faceGroup2Id.get(query);
        if (id_list == null) {
            Toast.makeText(YunDiskActivity.this, "您查询的人物不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        //通过resolver插入数据
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://cn.drake.imageprovider/multi");
        //插入前先清除上一次的数据
        resolver.delete(uri, "", new String[]{});
        for (String id : id_list) {
            //获取临时图片资源
            PictureList temp = pictureLists.get(Id2Pic.get(id));
            ContentValues values = new ContentValues();

            values.put("data", Base64Util.bitmapToBase64(temp.getBitmap()));
            values.put("resultJson", temp.getResultJson());

            resolver.insert(uri, values);
        }
        Intent intent = new Intent(YunDiskActivity.this, ResultActivity.class);
        intent.putExtra("query", query);
        startActivity(intent);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

}
