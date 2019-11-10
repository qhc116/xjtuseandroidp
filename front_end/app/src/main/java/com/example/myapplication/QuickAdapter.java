package com.example.myapplication;


import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

public class QuickAdapter extends BaseQuickAdapter<PictureList, BaseViewHolder> {
    private List<Boolean> booleanList = new ArrayList<>();

    public boolean hasItemSelected() {
        return booleanList.contains(true);
    }


    public Boolean getBooleanList(int position) {
        return booleanList.get(position);
    }

    public void removeBooleanList(int position){
        booleanList.remove(position);
        Log.i("移除了", ".....第"+(position+1)+"条bool");
    }

    public void addBooleanList() {
        this.booleanList.add(false);
    }

    public int getBooleanLenth(){
        return booleanList.size();
    }

    public void setBooleanList(int position, boolean status){
        booleanList.set(position, status);
    }


    public QuickAdapter(@LayoutRes int layoutResId, @Nullable List<PictureList> data){
        super(layoutResId, data);

    }



    @Override
    protected void convert(BaseViewHolder helper, PictureList item){
        //绑定数据前将checkbox设false

        //helper.func(A,B) = A.func(B)

        helper.setImageBitmap(R.id.iv, item.getBitmap());
        helper.setOnCheckedChangeListener(R.id.cb, new CompoundButton.OnCheckedChangeListener() {
            //holder只负责绑定监听器，下面的函数响应checkbox的变化
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                booleanList.set(helper.getLayoutPosition(), isChecked);
                if(booleanList.get(helper.getLayoutPosition())){
                    helper.setVisible(R.id.cb, true);

                }
                else{
                    helper.setVisible(R.id.cb, false);

                }

            }
        });
        Log.i("Position", "....."+helper.getLayoutPosition());

        helper.setChecked(R.id.cb,
                booleanList.get(helper.getLayoutPosition()));

    }



}
