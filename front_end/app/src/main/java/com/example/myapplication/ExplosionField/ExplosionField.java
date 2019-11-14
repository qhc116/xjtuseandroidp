package com.example.myapplication.ExplosionField;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ExplosionField extends View {
    private static final Canvas mCanvas =new Canvas();
    private ArrayList<ExplosionAnimator> explosionAnimators;        //存储动画集
    private OnClickListener onClickListener;

    public ExplosionField(Context context) {
        super(context);
        init();
    }

    public ExplosionField(Context context, @Nullable AttributeSet attrs ) {
        super(context, attrs);
        init();
    }

    private void init() {
        explosionAnimators = new ArrayList<ExplosionAnimator>();

        attach2Activity((Activity) getContext());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (ExplosionAnimator animator : explosionAnimators) {
            animator.draw(canvas);
        }
    }


    //得到图的复制
    private Bitmap createBitmapFromView(View view) {
        //通过view的宽高创建出一个同样大小的空白图，用Bitmap的静态方法createBitmap()创建，最后一个参数表示图片质量。
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        if (bitmap != null) {
            synchronized (mCanvas) {
                mCanvas.setBitmap(bitmap);
                view.draw(mCanvas);
                mCanvas.setBitmap(null);        //清除引用
            }
        }
        return bitmap;
    }

    //建立方法方便调用
    public void explode(final View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);//获取view相对整个视图坐标
       rect.offset(0, -Utils.dp2px(25));
        final ExplosionAnimator animator = new ExplosionAnimator(this,createBitmapFromView(view), rect);
        explosionAnimators.add(animator);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.animate().alpha(0f).setDuration(150).start();
            }
           /* @Override
            public void onAnimationEnd(Animator animation) {
                view.animate().alpha(1f).setDuration(150).start();

                // 动画结束时从动画集中移除
                explosionAnimators.remove(animation);
                animation = null;
            }*/
        });
        animator.start();
    }


    private void attach2Activity(Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.addView(this, lp);
    }




    public void addListener(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0 ; i < count; i++) {
                addListener(viewGroup.getChildAt(i));
            }
        } else {
            view.setClickable(true);
            view.setOnClickListener(getOnClickListener());
        }
    }
    private OnClickListener getOnClickListener() {
        if (null == onClickListener) {

            onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExplosionField.this.explode(v);
                }
            };
        }

        return onClickListener;
    }

}

