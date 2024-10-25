package com.example.pygmyhippo.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.databinding.UserFragmentProfileBinding;
import com.example.pygmyhippo.common.OnRoleSelectedListener;
import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;

/**
 * This fragment holds most of the information about a user which is returned from a call to the database
 * To be Implemented: the edit button will change the interface and allow the user to edit all fields and send it to
 * the database.
 *
 * To be fixed:
 * - The framework for the communication between this can the main activity needs to be more robust
 *   (right now when this fragment is open the drop down is triggered sending roleSelectedListener,
 *   which would change the role to user since its he first role in the drop down)
 *
 * Currently just a static page.
 * Allows the user to edit or view their current provided information.
 * @author Jennifer, Griffin
 * @version 1.1
 * No returns and no parameters
 */
public class ProfileFragment extends Fragment  implements AdapterView.OnItemSelectedListener{
    Uri imagePath;

    private UserFragmentProfileBinding binding;

    // Listener interface to communicate with the main activity
    private OnRoleSelectedListener roleSelectedListener;

    // initialized the listener
    public void setOnRoleSelectedListener(OnRoleSelectedListener listener) {
        this.roleSelectedListener = listener;
    }

    /**
     * This is called when the fragment is created, this ensure a connection the onrole listener to the parent
     * @author Griffin
     * @param context not sure
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            roleSelectedListener = (OnRoleSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnRoleSelectedListener");
        }
    }

    /**
     * Registers a photo picker activity launcher in single-select mode and sets the profile image to the new URI
     * @author Jennifer
     * @version 1.0
     */
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    imagePath = uri;
                    binding.ENewUserProfileImg.setImageURI(uri);
                }
            });

    /**
     * If a user hits the delete image button, this method will generate an avatar from the given link based upon the name field
     * and set the image view to the URI
     * @author Jennifer
     * @param name the name the user has entered
     * @return void
     * @version 1.0
     */
    public void generateAvatar (String name) throws URISyntaxException {
        if (name.isEmpty()) name = "null";
        String url = "https://api.multiavatar.com/";
        imagePath = Uri.parse(url+name+".png");

        Picasso.get()
                .load(imagePath)
                .into(binding.ENewUserProfileImg);

    }
    /**
     * Creates the view
     * @author Jennifer
     * @param inflater not sure
     * @param container not sure
     * @param savedInstanceState not sure
     * @return root not sure
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = UserFragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Spinner role_dropdown = (Spinner) root.findViewById(R.id.user_spinner_role);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                root.getContext(),
                R.array.role,
                R.layout.e_p_role_dropdown
        );

        adapter.setDropDownViewResource(R.layout.e_p_role_dropdown);
        role_dropdown.setAdapter(adapter);

        // need to do this so the listener is connected
        role_dropdown.setOnItemSelectedListener(this);


        ImageView editButton = root.findViewById(R.id.E_profile_editBtn);
        Button updateButton = root.findViewById(R.id.E_profile_create);

        // All the text fields
        EditText nameField = root.findViewById(R.id.E_profile_textName);
        EditText pronounField = root.findViewById(R.id.E_profile_textPronouns);
        EditText phoneField = root.findViewById(R.id.E_profile_textPhone);
        EditText emailField = root.findViewById(R.id.E_profile_textEmail);

        // Decorator radio buttons
        RadioButton decGeo = root.findViewById(R.id.E_profile_gps_dec);
        RadioButton decNotify = root.findViewById(R.id.E_profile_notification_dec);

        // Functional Radio Groups
        RadioGroup notifyRGroup = root.findViewById(R.id.E_profile_notify_setting);
        RadioGroup geolocationRGroup = root.findViewById(R.id.E_profile_geo_setting);

        // Image Buttons
        Button uploadImgBtn = root.findViewById(R.id.E_profile_uploadImageBtn);
        Button deleteImgBtn = root.findViewById(R.id.E_profile_deleteAvatarbtn);

        /**
         * Allows te page elements to be edited by the user if the edit button is clicked
         * @author Jennifer
         */
        View.OnClickListener editUser = new View.OnClickListener() {
            /**
             * Tell whichs elements to become focusable, to appear or disappear
             * @author Jennifer
             * @param view the fragment view
             */
            @Override
            public void onClick(View view) {
                nameField.setFocusable(true);
                pronounField.setFocusable(true);
                phoneField.setFocusable(true);
                emailField.setFocusable(true);
                nameField.setFocusableInTouchMode(true);
                pronounField.setFocusableInTouchMode(true);
                phoneField.setFocusableInTouchMode(true);
                emailField.setFocusableInTouchMode(true);
                updateButton.setVisibility(View.VISIBLE);
                decNotify.setVisibility(View.GONE);
                decGeo.setVisibility(View.GONE);
                notifyRGroup.setVisibility(View.VISIBLE);
                geolocationRGroup.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);
                uploadImgBtn.setVisibility(View.VISIBLE);
                deleteImgBtn.setVisibility(View.VISIBLE);

            }
        };

        /**
         * Exit edit mode the submit
         * TODO: this needs to call whatever method will submit to the database
         * @author Jennifer
         */
        View.OnClickListener updateUser = new View.OnClickListener() {
            /**
             * Tell whichs elements to become unfocusable, to appear or disappear
             * @author Jennifer
             * @param view the fragment view
             */
            @Override
            public void onClick(View view) {
                nameField.setFocusable(false);
                pronounField.setFocusable(false);
                phoneField.setFocusable(false);
                emailField.setFocusable(false);
                nameField.setFocusableInTouchMode(false);
                pronounField.setFocusableInTouchMode(false);
                phoneField.setFocusableInTouchMode(false);
                emailField.setFocusableInTouchMode(false);
                updateButton.setVisibility(View.GONE);
                decNotify.setVisibility(View.VISIBLE);
                decGeo.setVisibility(View.VISIBLE);
                notifyRGroup.setVisibility(View.GONE);
                geolocationRGroup.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
                uploadImgBtn.setVisibility(View.GONE);
                deleteImgBtn.setVisibility(View.GONE);

            }
        };

        /**
         * Listener for the uploadImage image button, it allows the user to select a photo from their photo gallery by launching the media picker
         * @author Jennifer
         */
        View.OnClickListener uploadImage = new View.OnClickListener() {
            /**
             * Tells the media picker to launch when the button listener is triggered
             * @author Jennifer
             * @param view the fragment view
             */
            @Override
            public void onClick(View view) {

                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());

            }

        };

        /**
         * On click Listener for the deleteImage image button
         * @author Jennifer
         */
        View.OnClickListener deleteImage = new View.OnClickListener() {
            /**
             * Sends the users name to the method getAvatar
             * @author Jennifer
             * @param view the fragment view
             */
            @Override
            public void onClick(View view) {
                try {
                    generateAvatar(nameField.getText().toString());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        uploadImgBtn.setOnClickListener(uploadImage);
        deleteImgBtn.setOnClickListener(deleteImage);
        editButton.setOnClickListener(editUser);
        updateButton.setOnClickListener(updateUser);

        return root;
    }

    /**
     * since this implements the OnTimeSelectedLister we need to override these two methods to get
     * the communication working
     * @author Griffin
     * @param adapterView: The adapter view of the selectable options
     * @param view: not sure
     * @param i: position of item clicked
     * @param l: not sure
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedRole = adapterView.getItemAtPosition(i).toString();
        if (roleSelectedListener != null) {
            roleSelectedListener.onRoleSelected(selectedRole);
        }
    }

    /**
     * This is for the case when nothing is selected
     * @author Griffin
     * @param adapterView: The adapter view of the selectable options
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}