package com.example.pygmyhippo.admin;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.pygmyhippo.common.RecyclerClickListener;

/*
This class works as the adapter for the AllUsers recycler list.
Purposes:
    - Formats the user list entries
    - To allow the Admin to browse through profiles
Issues:
    - None
*/

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.pygmyhippo.common.BaseRecyclerAdapter;
import com.example.pygmyhippo.common.BaseViewHolder;
import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.RecyclerClickListener;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.ImageStorage;
import com.example.pygmyhippo.database.StorageOnCompleteListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter for RecyclerView in AllUsersFragment.
 */
public class AllUsersAdapter extends BaseRecyclerAdapter<Account, AllUsersAdapter.UserViewHolder> {
    /**
     * Child of the base adapter. This will hold the text views to display the account info
     */
    public static class UserViewHolder extends BaseViewHolder<Account> {
        private ImageStorage imageHandler = new ImageStorage();
        private final TextView usernameTextView, phoneTextView, emailTextView, locationTextView;
        private ShapeableImageView profileImage;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.a_allList_event_image);
            usernameTextView = itemView.findViewById(R.id.a_alllist_event_name_text);
            phoneTextView = itemView.findViewById(R.id.a_alllist_event_occurence_text);
            emailTextView = itemView.findViewById(R.id.a_alllist_event_email_text);
            locationTextView = itemView.findViewById(R.id.a_alllist_event_waitlist_status_text);
        }

        @Override
        public void setViews(Account account) {
            // Initialize the texts based on the account attributes
            usernameTextView.setText(account.getName());
            phoneTextView.setText(account.getPhoneNumber());
            emailTextView.setText(account.getEmailAddress());
            locationTextView.setText(account.getLocation());

            if (!account.getProfilePicture().isEmpty()) {
                // Has an image so load that in from the database
                imageHandler.getImageDownloadUrl(account.getProfilePicture(), new StorageOnCompleteListener<Uri>() {
                    @Override
                    public void OnCompleteStorage(@NonNull ArrayList<Uri> docs, int queryID, int flags) {
                        // Author of this code segment is James
                        if (flags == DBOnCompleteFlags.SUCCESS.value) {
                            // Get the image and format it
                            Uri downloadUri = docs.get(0);
                            int imageSideLength = profileImage.getWidth() / 2;
                            Picasso.get()
                                    .load(downloadUri)
                                    .resize(imageSideLength, imageSideLength)
                                    .centerCrop()
                                    .into(profileImage);
                        }
                    }
                });
            } else {
                // The account doesn't have an image, so generate their avatar
                // Author of this code is Jen
                String name = account.getName();
                if (name.isEmpty()) name = "null";
                String url = "https://api.multiavatar.com/";
                Uri avatarURI = Uri.parse(url+name+".png");

                // Retrieve the generated profile picture
                Picasso.get()
                        .load(avatarURI)
                        .into(profileImage);
            }
        }
    }

    public AllUsersAdapter(ArrayList<Account> dataList, RecyclerClickListener listener) {
        super(dataList, listener);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_all_list_user_item, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(view);

        view.setOnClickListener(v -> {
            listener.onItemClick(viewHolder.getAdapterPosition());
        });

        return viewHolder;
    }
}
