package com.example.pygmyhippo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pygmyhippo.databinding.FragmentAllListBinding;

import java.util.ArrayList;
import java.util.Objects;

public class AllListFragment extends Fragment {
    public class DSWrapper {
        private Account account;
        private Event event;

        public DSWrapper(Account account) {
            this.account = account;
        }

        public DSWrapper(Event event) {
            this.event = event;
        }

        public Account getAccount() {
            return account;
        }

        public Event getEvent() {
            return event;
        }
    }

    FragmentAllListBinding binding;
    ArrayList<DSWrapper> allListData;
    AllListAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAllListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        allListData = new ArrayList<>();
        allListData.add(new DSWrapper(new Account("12312", "James", "1284182", "James@gmail.com", "Edmonton")));
        allListData.add(new DSWrapper(new Account("2444", "Griffin", "145562355", "griffin@gmail.com", "Vancouver")));
        allListData.add(new DSWrapper(new Account("1212", "Katharine", "039685", "katharine@gmail.com", "Toronto")));

        adapter = new AllListAdapter(allListData, AllListAdapter.AdapterType.Account);
        binding.allListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.allListRecycler.setAdapter(adapter);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                Objects.requireNonNull(this.getContext()),
                R.array.user_category_spinner,
                android.R.layout.simple_spinner_item
        );

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        binding.categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> orderAdapter = ArrayAdapter.createFromResource(
                Objects.requireNonNull(this.getContext()),
                R.array.order_spinner,
                android.R.layout.simple_spinner_item
        );

        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        binding.orderSpinner.setAdapter(orderAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
