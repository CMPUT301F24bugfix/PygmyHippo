package com.example.pygmyhippo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AllListAdapter extends RecyclerView.Adapter<AllListAdapter.DSViewHolder> {
    public enum AdapterType {
        Account,
        Event
    }

    public abstract static class DSViewHolder extends RecyclerView.ViewHolder {
        public DSViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void setViews(AllListFragment.DSWrapper dataclass);
    }

    public static class AccountViewHolder extends DSViewHolder {
        TextView usernameTextView, phoneTextView, emailTextView, locationTextView;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.a_alllist_event_name_text);
            phoneTextView = itemView.findViewById(R.id.a_alllist_event_occurence_text);
            emailTextView = itemView.findViewById(R.id.a_alllist_event_email_text);
            locationTextView = itemView.findViewById(R.id.a_alllist_event_waitlist_status_text);
        }

        @Override
        public void setViews(AllListFragment.DSWrapper dataclass) {
            Account account = dataclass.getAccount();
            usernameTextView.setText(account.getName());
            phoneTextView.setText(account.getPhoneNumber());
            emailTextView.setText(account.getEmailAddress());
            locationTextView.setText(account.getLocation());
        }
    }

    public static class EventViewHolder extends DSViewHolder {
        TextView usernameTextView, phoneTextView, emailTextView, locationTextView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void setViews(AllListFragment.DSWrapper dataclass) {

        }
    }

    ArrayList<AllListFragment.DSWrapper> listItems;
    AdapterType type;

    public AllListAdapter(ArrayList<AllListFragment.DSWrapper> listItems, AdapterType type) {
        this.setListItems(listItems);
        this.type = type;
    }


    @NonNull
    @Override
    public AllListAdapter.DSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AllListAdapter.DSViewHolder viewHolder;
        View view;
        switch (this.type) {
            case Account:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_list_user_item, parent, false);
                viewHolder = new AccountViewHolder(view);
                break;
            case Event:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_list_event_item, parent, false);
                viewHolder =  new EventViewHolder(view);
                break;
            default:
                throw new RuntimeException("Unknown Type for all list");
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AllListAdapter.DSViewHolder holder, int position) {
        holder.setViews(listItems.get(position));
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void setListItems(ArrayList<AllListFragment.DSWrapper> listItems) {
        this.listItems = listItems;
    }
}
