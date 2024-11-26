package com.example.pygmyhippo.admin;

/*
 * DeleteImageFragment
 *
 * Purposes:
 * Displays selected image to Admin with the option to delete said image
 *
 * Issues:
 * NA (at the moment)
 */
import static androidx.navigation.Navigation.findNavController;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Image;
import com.example.pygmyhippo.database.AccountDB;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.database.EventDB;
import com.example.pygmyhippo.database.ImageStorage;
import com.example.pygmyhippo.database.StorageOnCompleteListener;
import com.example.pygmyhippo.databinding.AdminDeleteImageBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DeleteImageFragment extends Fragment implements StorageOnCompleteListener<Uri> {

    private AdminDeleteImageBinding binding;
    private NavController navController;
    private Account signedInAccount;
    private Image currentImage;
    private ImageStorage ImageHandler;
    private EventDB EventDBhandler;
    private AccountDB AccountDbHandler;

    private ImageView ImageViewer;
    private Button DeleteButton;

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
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        navController = findNavController(view);
        AccountDbHandler = new AccountDB();
        EventDBhandler = new EventDB();
        ImageHandler = new ImageStorage();

        // set the image
        ImageViewer = view.findViewById(R.id.a_deleteImage_imageView);
        ImageHandler.getImageDownloadUrl(currentImage.getUrl(), this);


        // set up the back button
        FloatingActionButton backButton = view.findViewById(R.id.a_deleteImage_backBtn);
        backButton.setOnClickListener(view1 -> {
            Log.d("EventFragment", "Back button pressed");
            navController.popBackStack();
        });

        // set up the delete button
        DeleteButton = view.findViewById(R.id.a_deleteImage_deleteBtn);
        DeleteButton.setOnClickListener(view1 -> {
            Log.d("EventFragment", "Delete image button pressed");

            if (currentImage.getType() == Image.ImageType.Event) {
                // delete the image reference
                EventDBhandler.deleteEventImageReference(currentImage.getID(), new DBOnCompleteListener<Object>() {

                    /**
                     * @param docs    - Documents retrieved from DB (if it was a get query).
                     * @param queryID - ID of query completed.
                     * @param flags   - Flags to indicate query status/set how to process query result.
                     */
                    @Override
                    public void OnCompleteDB(@NonNull ArrayList<Object> docs, int queryID, int flags) {
                        if (flags == DBOnCompleteFlags.SUCCESS.value) {
                            Log.d("FirebaseStorage", "Event image deleted successfully");
                        } else {
                            Log.e("FirebaseStorage", "Event image not deleted");
                        }
                    }
                });

                // delete the image in firebase
                ImageHandler.DeleteImageByURL(currentImage.getUrl(), new StorageOnCompleteListener<Image>() {
                    /**
                     * @param docs    - Documents retrieved from DB (if it was a get query).
                     * @param queryID - ID of query completed.
                     * @param flags   - Flags to indicate query status/set how to process query result.
                     */
                    @Override
                    public void OnCompleteStorage(@NonNull ArrayList<Image> docs, int queryID, int flags) {
                        if (flags == DBOnCompleteFlags.SUCCESS.value) {
                            Log.d("FirebaseStorage", "Event image deleted successfully");
                            Toast.makeText(getContext(), "Event Image Deleted", Toast.LENGTH_LONG).show();
                        } else {
                            Log.e("FirebaseStorage", "Event image not deleted");
                        }
                    }
                });
            } else if (currentImage.getType() == Image.ImageType.Account) {
                // delete the image reference
                AccountDbHandler.deleteAccountProfileImageReference(currentImage.getID(), new DBOnCompleteListener<Object>() {

                    /**
                     * @param docs    - Documents retrieved from DB (if it was a get query).
                     * @param queryID - ID of query completed.
                     * @param flags   - Flags to indicate query status/set how to process query result.
                     */
                    @Override
                    public void OnCompleteDB(@NonNull ArrayList<Object> docs, int queryID, int flags) {
                        if (flags == DBOnCompleteFlags.SUCCESS.value) {
                            Log.d("FirebaseStorage", "Profile image deleted successfully");
                        } else {
                            Log.e("FirebaseStorage", "Profile image not deleted");
                        }
                    }
                });

                // delete the image in firebase
                ImageHandler.DeleteImageByURL(currentImage.getUrl(), new StorageOnCompleteListener<Image>() {
                    /**
                     * @param docs    - Documents retrieved from DB (if it was a get query).
                     * @param queryID - ID of query completed.
                     * @param flags   - Flags to indicate query status/set how to process query result.
                     */
                    @Override
                    public void OnCompleteStorage(@NonNull ArrayList<Image> docs, int queryID, int flags) {
                        if (flags == DBOnCompleteFlags.SUCCESS.value) {
                            Log.d("FirebaseStorage", "Profile image deleted successfully");
                            Toast.makeText(getContext(), "Profile Image Deleted", Toast.LENGTH_LONG).show();
                        } else {
                            Log.e("FirebaseStorage", "Profile image not deleted");
                        }
                    }
                });
            }
            else if (currentImage.getType() == Image.ImageType.Facility){
                // delete the image reference
                AccountDbHandler.deleteFacilityProfileImageReference(currentImage.getID(), new DBOnCompleteListener<Object>() {

                    /**
                     * @param docs    - Documents retrieved from DB (if it was a get query).
                     * @param queryID - ID of query completed.
                     * @param flags   - Flags to indicate query status/set how to process query result.
                     */
                    @Override
                    public void OnCompleteDB(@NonNull ArrayList<Object> docs, int queryID, int flags) {
                        if (flags == DBOnCompleteFlags.SUCCESS.value) {
                            Log.d("FirebaseStorage", "Facility image deleted successfully");
                        } else {
                            Log.e("FirebaseStorage", "Facility image not deleted");
                        }
                    }
                });

                // delete the image in firebase
                ImageHandler.DeleteImageByURL(currentImage.getUrl(), new StorageOnCompleteListener<Image>() {
                    /**
                     * @param docs    - Documents retrieved from DB (if it was a get query).
                     * @param queryID - ID of query completed.
                     * @param flags   - Flags to indicate query status/set how to process query result.
                     */
                    @Override
                    public void OnCompleteStorage(@NonNull ArrayList<Image> docs, int queryID, int flags) {
                        if (flags == DBOnCompleteFlags.SUCCESS.value) {
                            Log.d("FirebaseStorage", "Facility image deleted successfully");
                            Toast.makeText(getContext(), "Facility Image Deleted", Toast.LENGTH_LONG).show();
                        } else {
                            Log.e("FirebaseStorage", "Facility image not deleted");
                        }
                    }
                });
            }
        });
    }

    /**
     * @param docs    - Documents retrieved from DB (if it was a get query).
     * @param queryID - ID of query completed.
     * @param flags   - Flags to indicate query status/set how to process query result.
     */
    @Override
    public void OnCompleteStorage(@NonNull ArrayList<Uri> docs, int queryID, int flags) {
        if (queryID == 1) { // updated the image in the fragment
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                Log.i("Post Event", "Image upload successful");
                Uri currentImageUri = docs.get(0);
                Picasso.get()
                        .load(currentImageUri)
                        .resize(ImageViewer.getWidth(), ImageViewer.getHeight())
                        .centerCrop()
                        .into(ImageViewer);
            } else {
                Log.e("Post Event", "Image upload unsuccessful");
            }
        }
    }
}