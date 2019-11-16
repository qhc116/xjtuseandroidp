package com.example.myapplication.ExplosionField;

import android.graphics.Point;
import android.graphics.Rect;

import java.util.Random;

//用来描述粒子，包括属性有颜色、透明度、圆心坐标、半径。
public class Particle {
    public static final int PART_WH = 8;     //设置小球宽高

    public float cx;
    public float cy;
    public float radius;
    public int color;
    float alpha;
    private Rect mBound;
    private Random random=new Random();

    public static Particle generateParticle(int color, Rect bound, Point point) {
        int row = point.y;
        int column = point.x;

        Particle particle = new Particle();
        particle.mBound = bound;
        particle.color = color;
        particle.alpha = 1f;

        particle.radius = PART_WH;
        particle.cx = PART_WH * column + bound.left;
        particle.cy = PART_WH * row + bound.top;
        return particle;
    }

    //cx左右移动都可以，cy向下移动且距离和view高度有关（不同高度图片，每次下降距离不同），radius变小，alpha变得越来越透明。
    public void advance(float factor) {
        cx = cx + factor * random.nextInt(mBound.width()) * (random.nextFloat() - 0.5f);
        cy = cy + factor * random.nextInt(mBound.height() / 2);

        radius = radius - factor * random.nextInt(2);

        alpha = (1f - factor) * (1 + random.nextFloat());
    }

}
