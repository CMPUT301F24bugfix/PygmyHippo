package com.example.pygmyhippo.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pygmyhippo.common.RecyclerClickListener;
import com.example.pygmyhippo.databinding.AdminFragmentAllListBinding;

public class AllImagesFragment extends Fragment implements RecyclerClickListener {
    AdminFragmentAllListBinding binding;


    @Override
    public void onItemClick(int position) {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding =  AdminFragmentAllListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
