package com.example.myapplication.LoginRegisterPage;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class LooperPagerAdapter extends PagerAdapter {
    private List<Integer> mPics=null;

    //控制滑动的次数,
    @Override
    public int getCount() {
        if(mPics !=null){
            //return mPics.size();
            return mPics.size()+1000;//滑动到最后一个时会崩溃,因为在imageView.setImageResource(mPics.get(position));中会出现数组越界异常
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int realPosition=position%mPics.size();
        ImageView imageView=new ImageView(container.getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//拉伸图片
        //imageView.setImageResource(mPics.get(position));
        imageView.setImageResource(mPics.get(realPosition));
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);

    }

    public void setData( List<Integer> pics) {
        this.mPics=pics;
    }
}
