package com.example.pygmyhippo;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public abstract class BaseRecyclerAdapter<T, VH extends BaseViewHolder<T>> extends RecyclerView.Adapter<VH> {
    protected RecyclerClickListener listener;
    protected ArrayList<T> dataList;

    public BaseRecyclerAdapter(ArrayList<T> dataList) {
        this.dataList = dataList;
    }

    public BaseRecyclerAdapter(ArrayList<T> dataList, RecyclerClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.setViews(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
