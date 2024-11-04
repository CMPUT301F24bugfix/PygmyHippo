package com.example.pygmyhippo.admin;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pygmyhippo.R;
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
        
        binding.aAlllistTitleText.setText(R.string.all_images_title);
        binding.aAlllistFilterByText.setVisibility(View.INVISIBLE);
        binding.aAlllistCategorySpinner.setVisibility(View.INVISIBLE);
        binding.aAlllistOrderSpinner.setVisibility(View.INVISIBLE);

        imageList = new ArrayList<>();
        imageList.add(new Image("gs://pygmyhippo-b7892.appspot.com/moodengpfp.jpg", "100", Image.ImageType.Account));
        imageList.add(new Image("gs://pygmyhippo-b7892.appspot.com/ea0wkspMQlK0Z_5tIwxomw/avatar/1000000020", "200", Image.ImageType.Account));
        imageList.add(new Image("gs://pygmyhippo-b7892.appspot.com/ea0wkspMQlK0Z_5tIwxomw/avatar/1000000049", "300", Image.ImageType.Account));
        adapter = new AllImagesAdapter(imageList, this);

        binding.aAlllistRecycler.setAdapter(adapter);
        binding.aAlllistRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));

        binding.aAlllistRecycler.setClipToPadding(false);
        binding.aAlllistRecycler.setClipChildren(false);

        final int itemSpacing = 20;
        binding.aAlllistRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(itemSpacing, itemSpacing, itemSpacing, itemSpacing);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
