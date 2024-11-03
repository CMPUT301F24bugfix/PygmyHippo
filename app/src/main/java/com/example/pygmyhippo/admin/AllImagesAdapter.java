package com.example.pygmyhippo.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.BaseRecyclerAdapter;
import com.example.pygmyhippo.common.BaseViewHolder;
import com.example.pygmyhippo.common.Image;
import com.example.pygmyhippo.common.RecyclerClickListener;

import java.util.ArrayList;

public class AllImagesAdapter extends BaseRecyclerAdapter<Image, AllImagesAdapter.ImageViewHolder> {
    public static class ImageViewHolder extends BaseViewHolder<Image> {
        private ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.a_alllist_image);
        }

        @Override
        public void setViews(Image dataclass) {
            // TODO: Get image from firebase storage.
        }
    }

    public AllImagesAdapter(ArrayList<Image> dataList, RecyclerClickListener listener) {
        super(dataList, listener);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_all_list_image_item, parent, false);
        AllImagesAdapter.ImageViewHolder viewHolder = new ImageViewHolder(view);

        view.setOnClickListener(v -> {
            listener.onItemClick(viewHolder.getAdapterPosition());
        });

        return viewHolder;
    }
}
