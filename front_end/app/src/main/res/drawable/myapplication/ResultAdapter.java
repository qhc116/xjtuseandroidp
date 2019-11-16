package com.example.myapplication;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.utils.MarkFaces;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ReHolder> {

    public class ReHolder extends RecyclerView.ViewHolder{

        private ImageView iv;

        public ReHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
        }


    }

    private List<PictureList> pictureLists;

    public ResultAdapter(List<PictureList> pictureLists){
        this.pictureLists = pictureLists;
    }

    @NonNull
    @Override
    public ResultAdapter.ReHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_image_layout, parent, false);
        ReHolder viewHolder = new ReHolder(v);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReHolder holder, int position) {
        Bitmap bitmap = pictureLists.get(position).getBitmap();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(pictureLists.get(position).getResultJson());
            holder.iv.setImageBitmap(MarkFaces.markFaces(jsonObject, bitmap));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public int getItemCount() {
        return pictureLists.size();
    }
}
