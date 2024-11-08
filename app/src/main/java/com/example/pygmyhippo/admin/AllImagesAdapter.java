package com.example.pygmyhippo.admin;
/*
 * AllImagesAdapter
 *
 * Purpose: RecyclerView adapter for RecyclerView in AllImagesFragment.
 *          - Contributes to the image browsing for admin
 * Issues: None
 */


import android.net.Uri;
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
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter for RecyclerView in AllImagesFragment.
 */
public class AllImagesAdapter extends BaseRecyclerAdapter<Image, AllImagesAdapter.ImageViewHolder> {
    /**
     * The child of the base adapter, this child will hold an image formatted for this list
     */
    public static class ImageViewHolder extends BaseViewHolder<Image> implements DBOnCompleteListener<Uri> {
        private final ImageView imageView;
        private final AllImagesDB handler;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.a_alllist_image);
            handler = new AllImagesDB();
        }

        @Override
        public void setViews(Image dataclass) {
            // Get the image from the database
            handler.getImageDownloadUrl(dataclass, this);
        }

        @Override
        public void OnComplete(@NonNull ArrayList<Uri> docs, int queryID, int flags) {
            if (queryID == 0) {
                if (flags == DBOnCompleteFlags.SUCCESS.value) {
                    // Get the image and format it
                    Uri downloadUri = docs.get(0);
                    int imageSideLength = imageView.getWidth() / 2;
                    Picasso.get()
                            .load(downloadUri)
                            .resize(imageSideLength, imageSideLength)
                            .centerCrop()
                            .into(imageView);
                }
            }
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

        // Initialize the image holder
        AllImagesAdapter.ImageViewHolder viewHolder = new ImageViewHolder(view);

        view.setOnClickListener(v -> {
            listener.onItemClick(viewHolder.getAdapterPosition());
        });

        return viewHolder;
    }
}
