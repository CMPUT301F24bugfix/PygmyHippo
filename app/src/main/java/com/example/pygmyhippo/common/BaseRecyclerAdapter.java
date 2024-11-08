package com.example.pygmyhippo.common;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/*
Purposes:
    - To display single dataclasses in the admin browsing
Issues:
    - None
 */

/**
 * Abstract base class for RecyclerView Adapters for displaying single dataclasses (e.g. Event,
 * Account).
 *
 * In order to effectively to this adapter, you have to extend it and provide an accompanying
 * ViewHolder. A custom click listener is optional but required if you need to process list item
 * clicks.
 *
 * @see RecyclerClickListener
 * @see com.example.pygmyhippo.admin.AllEventsAdapter
 * @param <T> Dataclass being displayed.
 * @param <VH> ViewHolder for the same dataclass.
 */
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
