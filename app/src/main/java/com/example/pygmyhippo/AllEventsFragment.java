package com.example.pygmyhippo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pygmyhippo.databinding.FragmentAllListBinding;

import java.util.ArrayList;

public class AllEventsFragment extends Fragment implements RecyclerClickListener {
    FragmentAllListBinding binding;
    ArrayList<Event> allListData;
    AllEventsAdapter adapter;

    private void getData() {
        allListData = new ArrayList<>();
        allListData.add(new Event("LeetCode Meetup", "SUB", "Oct. 30th", "09:30", Event.EventStatus.ongoing));
        allListData.add(new Event("CheriCon", "VVC", "Nov. 1st", "23:30", Event.EventStatus.ongoing));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        getData();

        binding = FragmentAllListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new AllEventsAdapter(allListData, this);
        binding.aAlllistRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.aAlllistRecycler.setAdapter(adapter);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this.requireContext(),
                R.array.all_events_category_spinner,
                android.R.layout.simple_spinner_item
        );

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        binding.aAlllistCategorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> orderAdapter = ArrayAdapter.createFromResource(
                this.requireContext(),
                R.array.order_spinner,
                android.R.layout.simple_spinner_item
        );

        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        binding.aAlllistOrderSpinner.setAdapter(orderAdapter);

        binding.aAlllistTitleText.setText(R.string.all_events_title);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(int position) {
        // TODO: Add navigation.
        Log.i("User", String.format("Admin clicked item at position %d", position));
    }
}