package com.example.pygmyhippo.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.pygmyhippo.common.Image;
import com.example.pygmyhippo.common.RecyclerClickListener;
import com.example.pygmyhippo.databinding.AdminFragmentAllListBinding;

import java.util.ArrayList;

public class AllImagesFragment extends Fragment implements RecyclerClickListener {
    AdminFragmentAllListBinding binding;
    AllImagesAdapter adapter;
    ArrayList<Image> imageList;

    @Override
    public void onItemClick(int position) {
        Log.d("Admin", String.format("Image at position (%d) clicked.", position));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding =  AdminFragmentAllListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imageList = new ArrayList<>();
        imageList.add(new Image("gs://pygmyhippo-b7892.appspot.com/moodengpfp.jpg", "100", Image.ImageType.Account));
        adapter = new AllImagesAdapter(imageList, this);

        binding.aAlllistRecycler.setAdapter(adapter);
        binding.aAlllistRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
