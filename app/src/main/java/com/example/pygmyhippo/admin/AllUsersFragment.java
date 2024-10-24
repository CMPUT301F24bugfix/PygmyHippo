package com.example.pygmyhippo.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.RecyclerClickListener;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.databinding.FragmentAllListBinding;

import java.util.ArrayList;

/**
 * Admin fragment for displaying all users.
 *
 * Users are displayed in a list (RecyclerView). Users can be sorted by various attributes in
 * different orders. When users are clicked, the fragment should navigate to that particular
 * user's profile page with admin permissions.
 */
public class AllUsersFragment extends Fragment implements RecyclerClickListener {

    FragmentAllListBinding binding;
    ArrayList<Account> allListData;
    AllUsersAdapter adapter;

    private void getData() {
        // TODO: Add DB integration.
        allListData = new ArrayList<>();
        allListData.add(new Account("12312", "James", "1284182", "James@gmail.com", "Edmonton"));
        allListData.add(new Account("2444", "Griffin", "145562355", "griffin@gmail.com", "Vancouver"));
        allListData.add(new Account("1212", "Katharine", "039685", "katharine@gmail.com", "Toronto"));
    }

    @Override
    public void onItemClick(int position) {
        // TODO: Add navigation to user page as Admin.
        Log.i("User", String.format("Admin clicked item at position %d", position));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        getData();

        binding = FragmentAllListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new AllUsersAdapter(allListData, this);
        binding.aAlllistRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.aAlllistRecycler.setAdapter(adapter);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this.requireContext(),
                R.array.all_users_category_spinner,
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

        binding.aAlllistTitleText.setText(R.string.all_users_title);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
