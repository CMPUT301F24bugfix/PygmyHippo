package com.example.pygmyhippo.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.RecyclerClickListener;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.databinding.AdminFragmentAllListBinding;

import java.util.ArrayList;

/**
 * Admin fragment for displaying all users.
 *
 * Users are displayed in a list (RecyclerView). Users can be sorted by various attributes in
 * different orders. When users are clicked, the fragment should navigate to that particular
 * user's profile page with admin permissions.
 */
public class AllUsersFragment extends Fragment implements RecyclerClickListener, DBOnCompleteListener<Account> {

    AdminFragmentAllListBinding binding;
    NavController navController;

    AllUsersAdapter adapter;
    AllUsersDB handler;

    ArrayList<Account> allListData;
    Account signedInAccount;

    @Override
    public void onItemClick(int position) {
        // TODO: Add navigation to user page as Admin.
        Log.i("User", String.format("Admin clicked item at position %d", position));

        Bundle navArgs = new Bundle();
        navArgs.putString("adminViewAccountID", allListData.get(position).getAccountID());
        navArgs.putParcelable("signedInAccount", signedInAccount);
        navController.navigate(R.id.admin_navigation_profile_page, navArgs);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = AdminFragmentAllListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        handler = new AllUsersDB();
        allListData = new ArrayList<>();

        signedInAccount = AllUsersFragmentArgs.fromBundle(getArguments()).getSignedInAccount();

        // FIXME: Redesign so it works better with REcyclerView pagination.
        handler.getUsers(1000, this);

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

    @Override
    public void OnComplete(@NonNull ArrayList<Account> docs, int queryID, int flags) {
        if (queryID == 0) {
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                docs.forEach(doc -> {
                    allListData.add(doc);
                    adapter.notifyItemInserted(allListData.size() - 1);
                });
            } else {
                Toast.makeText(getContext(), "Could not get accounts from Firestore", Toast.LENGTH_LONG).show();
            }
        }
    }
}
