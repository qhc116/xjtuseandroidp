package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SectionDecoration extends RecyclerView.ItemDecoration {

    //外部需要实现的回调接口
    public interface DecorationCallback{
        String getGroupUser(int position);

        String getGroupDate(int position);


    }

    private Paint paint;
    private DecorationCallback callback;
    private TextPaint textPaint;
    private int topGap;

    private Paint.FontMetrics fontMetrics;

    private static final String TAG = "SectionDecoration";
    private String StatusTag;


    public SectionDecoration(Context context, DecorationCallback decorationCallback){
        Resources res = context.getResources();
        this.callback = decorationCallback;
        //设置paint
        paint = new Paint();
        paint.setColor(res.getColor(R.color.colorAccent));
        //设置textPaint
        textPaint = new TextPaint();
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(60);
        textPaint.setColor(Color.BLACK);
        textPaint.getFontMetrics(fontMetrics);
        textPaint.setTextAlign(Paint.Align.LEFT);
        fontMetrics = new Paint.FontMetrics();
        topGap = res.getDimensionPixelSize(R.dimen.dp_40);
        StatusTag = "";



    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //在装饰器绘制前获取当前的排序种类
        StatusTag = (String) parent.getTag();
        int pos = parent.getChildAdapterPosition(view);
        Log.i(TAG, "getItemOffsets：" + pos);
        String groupId = callback.getGroupUser(pos);
//        if (groupId < 0) return;
        if (pos == 0 || isFirstInGroup(pos)) {//同组的第一个才添加padding
            outRect.top = topGap;
        } else {
            outRect.top = topGap;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        //绘制分组标题的函数

        String textLine = "";
        int itemCount = state.getItemCount();
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        float lineHeight = textPaint.getTextSize() + fontMetrics.descent;

        String preGroupId, groupId = "";
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);

            //相邻两个分组一样时跳过
            preGroupId = groupId;
            //不同排序使用不同的组别判断
            if(StatusTag.equals("Face"))
                groupId = callback.getGroupUser(position);
            else if(StatusTag.equals("Time"))
                groupId = callback.getGroupDate(position);
            if (groupId.isEmpty() || groupId.equals(preGroupId)) continue;
            //不同排序使用不同的标题显示，刚进入时不绘制任何标题
            if(StatusTag.equals("Face"))
                textLine = callback.getGroupUser(position).toUpperCase();
            else if (StatusTag.equals("Time"))
                textLine = callback.getGroupDate(position).toUpperCase();
            else
                continue;
            if (TextUtils.isEmpty(textLine)) continue;

            int viewBottom = view.getBottom();
            float textY = Math.max(topGap, view.getTop());
            if (position + 1 < itemCount) { //下一个和当前不一样移动当前
                String  nextGroupId = callback.getGroupUser(position + 1);
                if (!nextGroupId.equals(groupId) && viewBottom < textY ) {//组内最后一个view进入了header
                    textY = viewBottom;
                }
            }
            c.drawRect(left, textY - topGap, right, textY, paint);
            c.drawText(textLine, left, textY, textPaint);
        }
    }


    private boolean isFirstInGroup(int pos) {
        if (pos == 0) {
            return true;
        } else {
            if(StatusTag.equals("Face")) {
                String prevGroupId = callback.getGroupUser(pos - 1);
                String groupId = callback.getGroupUser(pos);
                return prevGroupId.equals(groupId);
            }else if(StatusTag.equals("Time")){
                String prevGroupDate = callback.getGroupDate(pos - 1);
                String groupDate = callback.getGroupDate(pos);
                return prevGroupDate.equals(groupDate);
            }else
                return false;
        }
    }
}
