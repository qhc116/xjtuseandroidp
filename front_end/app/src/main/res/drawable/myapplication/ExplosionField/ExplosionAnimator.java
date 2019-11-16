package com.example.myapplication.ExplosionField;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

public class ExplosionAnimator extends ValueAnimator {

    private static final int DEFAULT_DURATION = 1500;
    private Particle[][] mParticles;
    private Paint mPaint;
    private View mContainer;




    public ExplosionAnimator(View view, Bitmap bitmapFromView, Rect rect) {
        mPaint =new Paint();
        mContainer=view;

        setFloatValues(0.0f, 1.0f);
        setDuration(DEFAULT_DURATION);      //设置持续时间
        mParticles=generateParticles(bitmapFromView,rect);
        // 在一段时间内（大概1.5秒）让ValueAnimator里的值从0.0f变化到1.0f，然后根据系统生成的递增随机值（范围在0.0f~1.0f）改变Particle里的属性值。
    }

    //该方法计算横竖粒子的数量
    public Particle[][] generateParticles(Bitmap bitmap, Rect bound) {       //Rect  矩形类
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int partWidthcount = w / Particle.PART_WH;
        int partHeightcount = w / Particle.PART_WH;

        int bitmap_part_w = bitmap.getWidth() / partWidthcount;
        int bitmap_part_h = bitmap.getHeight() / partHeightcount;

        Particle[][] particles = new Particle[partHeightcount][partWidthcount];
        int color;
        Point point = null;
        for (int row = 0; row < partHeightcount; row++)
            for (int column = 0; column < partWidthcount; column++) {
                color = bitmap.getPixel(column * bitmap_part_w, row * bitmap_part_h);

                point = new Point(column, row);
                particles[row][column] = Particle.generateParticle(color, bound, point);
            }
        return particles;
    }

    public void draw(Canvas canvas) {
        if(!isStarted()) { //动画结束时停止
            return;
        }
        for (Particle[] particle : mParticles) {
            for (Particle p : particle) {

                p.advance((Float) getAnimatedValue());

                mPaint.setColor(p.color);
                mPaint.setAlpha((int) (Color.alpha(p.color) * p.alpha)); //这样透明颜色就不是黑色了

                canvas.drawCircle(p.cx, p.cy, p.radius, mPaint);
            }
        }
        mContainer.invalidate();
    }

    @Override
    public void start() {
        super.start();
      // 调用 ExplosionField中的invalidate()方法，就是调用ExplosionField的onDraw()方法。而ExplosionField的onDraw()里又调用了ExplosionAnimator的draw()方法。这样循环就出现了动画效果。
        mContainer.invalidate();
    }
}
