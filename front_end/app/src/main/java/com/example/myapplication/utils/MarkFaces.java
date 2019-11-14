package com.example.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MarkFaces {
    public static Bitmap markFaces(JSONObject jsonObject,Bitmap bitmap){
        try {
            int face_num = (int) jsonObject.getJSONObject("result").get("face_num");
            JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("face_list");
            Log.d("TAG", "markFaces: "  + jsonArray);
            return mark(jsonArray,face_num, bitmap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    private static Bitmap mark(JSONArray jsonArray, int face_num, Bitmap bitmap) {
        try {
            int left, top, right, bottom,rotation;
            Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            for (int i =0; i < face_num; i++) {
                Canvas canvas = new Canvas(mutableBitmap);
                JSONObject location = ((JSONObject) jsonArray.get(i)).getJSONObject("location");
                String name = ((JSONObject) ((JSONObject) jsonArray.get(i)).getJSONArray("user_list").get(0)).getString("user_id");
                //JSONArray name = ((JSONArray) jsonArray.get(i)).getJSONObject("user_list");
                //Canvas canvas = new Canvas(imageBitmap);
                Paint paint = new Paint();
                left = ((Double) location.get("left")).intValue();
                top = ((Double) location.get("top")).intValue();
                right = left + (int)location.get("width");
                bottom = top + (int)location.get("height");
                rotation = (int)location.get("rotation");

                paint.setColor(Color.GREEN);
                paint.setDither(true);//防抖动
                paint.setFilterBitmap(true);//抗锯齿
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);//框框
                paint.setStrokeWidth(1);//框框宽度
                paint.setTextSize(16);

                canvas.rotate(rotation,left,top);
                canvas.drawRect(left, top, right, bottom, paint);
                canvas.drawText (name, left, top-1, paint);
            }
            return mutableBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
