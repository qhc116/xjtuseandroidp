package com.example.myapplication.LoginRegisterPage;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String[] textContainer;

    private Button mBtnBaiDu;
    private ViewPager mloopPager;
    private LooperPagerAdapter mlooperPagerAdapter;
    private  List<Integer> sPics;
    private Handler mHandler;
    private boolean mIsTouch = true;
    private TextView tvTopView;
    private  LinearLayout mpointContainer;
    private int previousSelectedPosition = 0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

            mBtnBaiDu = findViewById(R.id.mbtn_login);
            mBtnBaiDu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, userLoginActivity.class);
                    startActivity(intent);
                }
            });


            //初始化轮播图视图
            initViews();
            //初始化标题数据
            initData();


            //后初始化数据,要加notify
            mlooperPagerAdapter.setData(sPics);
            mlooperPagerAdapter.notifyDataSetChanged();
            //小圆点的变化
            mloopPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {      //页面滑动结束时的位置
                    int realposition = position % sPics.size();
                    mpointContainer.getChildAt(previousSelectedPosition).setEnabled(false);
                    mpointContainer.getChildAt(realposition).setEnabled(true);
                    previousSelectedPosition = realposition;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            insertPoint();
            mpointContainer.getChildAt(0).setEnabled(true);
            mHandler = new Handler();
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        private void initViews() {
            mpointContainer = findViewById(R.id.points_container);
            tvTopView = findViewById(R.id.tv_top_view);

            //找到控件
            mloopPager = findViewById(R.id.looper_pager);
            //设置适配器
            mlooperPagerAdapter = new LooperPagerAdapter();
            mloopPager.setAdapter(mlooperPagerAdapter);
        }

        private void initData() {
            //图片资源Id数组
            sPics = new ArrayList<>();
            sPics.add(R.drawable.image1);
            sPics.add(R.drawable.image2);
            sPics.add(R.drawable.image3);
            sPics.add(R.drawable.image4);
            sPics.add(R.drawable.image5);
            //文本描述
            textContainer = new String[]{
                    "哈曼顿计划",
                    "谁敢反对哈曼曼",
                    "就打爆谁的狗头",
                    "碧蓝航线,天下第一",
                    "滑稽"
            };
        }


        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        private void insertPoint() {
            //按照图片资源数量添加小圆点
            for (int i = 0; i < sPics.size(); i++) {
                View point = new View(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20, 20);
                point.setBackground(getResources().getDrawable(R.drawable.shape_point));
                layoutParams.leftMargin = 10;
                point.setLayoutParams(layoutParams);
                point.setEnabled(false);
                mpointContainer.addView(point);
            }


        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            //当界面绑定到窗口时
            mHandler.post(mLooperTask);
        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            //当从界面离开时
            mHandler.removeCallbacks(mLooperTask);
        }

        private Runnable mLooperTask = new Runnable() {
            @Override
            public void run() {
                //获取当前的轮播的位置
                int mcurentItem = mloopPager.getCurrentItem();
                if (mIsTouch == true) {
                    mcurentItem--;
                    mIsTouch = false;
                }
                //切换图片

                mloopPager.setCurrentItem(++mcurentItem, true);
                tvTopView.setText(textContainer[mcurentItem % sPics.size()]);
                mHandler.postDelayed(this, 3000);

                touchStop(mloopPager);
            }
        };

        private void touchStop(ViewPager mloopPager) {
            mloopPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        //当屏幕被按下时
                        case MotionEvent.ACTION_DOWN:
                            mHandler.removeMessages(0);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            mHandler.removeCallbacks(mLooperTask);
                            mIsTouch = true;
                            break;
                        case MotionEvent.ACTION_UP:
                            mHandler.post(mLooperTask);
                            break;
                    }
                    return false;
                }

            });
        }


        public void policy1(View view) {
            //应该是跳转去文档的
            Toast.makeText(MainActivity.this, "隐私协议", Toast.LENGTH_SHORT).show();
        }

        public void policy2(View view) {
            Toast.makeText(MainActivity.this, "用户协议", Toast.LENGTH_SHORT).show();
        }
    }


