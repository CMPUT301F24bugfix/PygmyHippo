package com.example.pygmyhippo.admin;

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

import java.util.ArrayList;

/**
 * Adapter for RecyclerView in AllUsersFragment.
 */
public class AllUsersAdapter extends BaseRecyclerAdapter<Account, AllUsersAdapter.UserViewHolder> {
    public static class UserViewHolder extends BaseViewHolder<Account> {
        private final TextView usernameTextView, phoneTextView, emailTextView, locationTextView;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.a_alllist_event_name_text);
            phoneTextView = itemView.findViewById(R.id.a_alllist_event_occurence_text);
            emailTextView = itemView.findViewById(R.id.a_alllist_event_email_text);
            locationTextView = itemView.findViewById(R.id.a_alllist_event_waitlist_status_text);
        }

        @Override
        public void setViews(Account account) {
            usernameTextView.setText(account.getName());
            phoneTextView.setText(account.getPhoneNumber());
            emailTextView.setText(account.getEmailAddress());
            locationTextView.setText(account.getLocation());
        }
    }

    public AllUsersAdapter(ArrayList<Account> dataList, RecyclerClickListener listener) {
        super(dataList, listener);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_list_user_item, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(view);

        view.setOnClickListener(v -> {
            listener.onItemClick(viewHolder.getAdapterPosition());
        });

        return viewHolder;
    }
}
