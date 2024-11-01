package com.example.pygmyhippo.common;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Generic ViewHolder abstract class for implementing a custom RecyclerView adapter.
 *
 * These ViewHolders have the same type as the adapter they are served in. This ViewHolder abstract
 * class was designed to display singular dataclasses. Multiple dataclasses will require a custom
 * implementation. setViews is the important method to implement as it sets the item layout's
 * TextViews/EditTexts with values for rendering. These ViewHolders are generally tied to the
 * adapter, thus it is advisable to implement it as a static inner class of the adapter it is use
 * in.
 *
 * @param <T> Dataclass being displayed.
 * @see com.example.pygmyhippo.admin.AllEventsAdapter.EventViewHolder
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void setViews(T dataclass);
}
