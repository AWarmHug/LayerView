package com.warm.layerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 作者: 51hs_android
 * 时间: 2017/6/6
 * 简介:
 */

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv.setText("这是"+position+"个");
    }

    @Override
    public int getItemCount() {
        return 30;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        public ViewHolder(View itemView) {
            super(itemView);
            tv= (TextView) itemView.findViewById(R.id.tv);
        }
    }
}
