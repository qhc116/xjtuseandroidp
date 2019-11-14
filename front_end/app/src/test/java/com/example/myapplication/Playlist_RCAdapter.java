package com.example.myapplication;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Playlist_RCAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    class MyHolder extends RecyclerView.ViewHolder{
        TextView noTv;
        TextView nameTv;
        TextView albumTv;

        private MyHolder(@NonNull View itemView) {
            super(itemView);
            noTv=itemView.findViewById(R.id.noTv);
            nameTv=itemView.findViewById(R.id.nameTv);
            albumTv=itemView.findViewById(R.id.albumTv);
        }
    }

    //数据
    private List<Plist> plists;

    //数据在创建Adapter时传入, 由Adapter分配给Holder
    public Playlist_RCAdapter(List<Plist> plists) {
        this.plists = plists;
    }

    //以下三个方法是ViewHolder必须实现的创建，绑定， 方法
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyHolder holder=null;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_layout, parent, false);
        holder = new MyHolder(itemView);
        Log.i("创建", "holder");
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolder holder1 = (MyHolder) holder;
        Plist plist = plists.get(position);
        //接下来将plist中值通过holder设置
        holder1.noTv.setText(((Integer)plist.getSong_no()).toString());
        holder1.nameTv.setText(plist.getSname());
        holder1.albumTv.setText(plist.getAlbum());
        Log.i("绑定", "View->Holder");
    }

    @Override
    public int getItemCount() {
        return plists.size();
    }
}
