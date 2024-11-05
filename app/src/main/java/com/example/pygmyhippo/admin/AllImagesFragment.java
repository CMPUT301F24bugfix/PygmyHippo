/**
 * AllImagesFragment
 *
 * Displays all available in the system for admins to see. Displays users' profile pictures, events'
 * posters, and facilities' pictures. Clicking on an image should navigate the admin to the relevant
 * page with admin permissions so they may or may not delete the image.
 *
 * TODO: Add images for facilities
 * TODO: Add navigation to profile, events, and (eventually) facilties.
 */

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
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Event;
import com.example.pygmyhippo.common.Image;
import com.example.pygmyhippo.common.RecyclerClickListener;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.databinding.AdminFragmentAllListBinding;

import java.util.ArrayList;

/**
 * Fragment for displaying all images for admins.
 */
public class AllImagesFragment extends Fragment implements RecyclerClickListener, DBOnCompleteListener<Object> {
    AdminFragmentAllListBinding binding;
    AllImagesAdapter adapter;
    ArrayList<Image> imageList;
    AllImagesDB handler;

    @Override
    public void onItemClick(int position) {
        Log.d("Admin", String.format("Image at position (%d) clicked.", position));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding =  AdminFragmentAllListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        handler = new AllImagesDB();
        
        binding.aAlllistTitleText.setText(R.string.all_images_title);
        binding.aAlllistFilterByText.setVisibility(View.INVISIBLE);
        binding.aAlllistCategorySpinner.setVisibility(View.INVISIBLE);
        binding.aAlllistOrderSpinner.setVisibility(View.INVISIBLE);

        imageList = new ArrayList<>();
        adapter = new AllImagesAdapter(imageList, this);

        handler.getAccounts(1000, this);
        handler.getEvents(1000, this);

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

    @Override
    public void OnComplete(@NonNull ArrayList<Object> docs, int queryID, int flags) {
        switch (queryID) {
            case 1:
                docs.forEach(obj -> {
                    Account account = (Account) obj;
                    Image image = new Image(account.getProfilePicture(), account.getAccountID(), Image.ImageType.Account);
                    imageList.add(image);
                    adapter.notifyItemInserted(imageList.size() - 1);
                });
                break;
            case 2:
                docs.forEach(obj -> {
                    Event event = (Event) obj;
                    Image image = new Image(event.getEventPoster(), event.getEventID(), Image.ImageType.Event);
                    imageList.add(image);
                    adapter.notifyItemInserted(imageList.size() - 1);
                });
                break;
            default:
                Log.d("AllImagesFragment", String.format("Received OnComplete call from unknown queryID %d", queryID));
        }
    }
}
