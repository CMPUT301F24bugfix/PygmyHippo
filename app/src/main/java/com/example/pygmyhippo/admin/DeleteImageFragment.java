package com.example.pygmyhippo.admin;
import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Image;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.databinding.AdminDeleteImageBinding;

import java.util.ArrayList;

public class DeleteImageFragment<Event> extends Fragment implements DBOnCompleteListener<Event> {

    private AdminDeleteImageBinding binding;
    private Account signedInAccount;
    private Image currentImage;

    /**
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = AdminDeleteImageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        if (getArguments() != null) {
            signedInAccount = DeleteImageFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
            currentImage = DeleteImageFragmentArgs.fromBundle(getArguments()).getImage();
        }
        return root;
    }


    /**
     * @param docs    - Documents retrieved from DB (if it was a get query).
     * @param queryID - ID of query completed.
     * @param flags   - Flags to indicate query status/set how to process query result.
     */
    @Override
    public void OnCompleteDB(@NonNull ArrayList<Event> docs, int queryID, int flags) {
    }
}
