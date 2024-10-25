package com.example.pygmyhippo.organizer;

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
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.OnRoleSelectedListener;
import com.example.pygmyhippo.databinding.OrganiserFragmentProfileBinding;
import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;
import java.util.Objects;


/**
 * This fragment holds most of the information about a user which is returned from a call to the database
 * To be Implemented:
 * - the edit button will change the interface and allow the user to edit all fields and send it to
 *   the database.
 * - Organiser profile page
 *
 * To be fixed:
 * - The framework for the communication between this can the main activity needs to be more robust
 *   (right now when this fragment is open the drop down is triggered sending roleSelectedListener,
 *   which would change the role to user since its he first role in the drop down)
 *
 * Currently just a static page.
 * Allows the organiser to edit or view their current provided information.
 * @author Jennifer, Griffin
 * @version 1.2
 * No returns and no parameters
 */
public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    Uri imagePath;
    String uploadType = "avatar";

    private OrganiserFragmentProfileBinding binding;

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


     // Registers a photo picker activity launcher in single-select mode and sets the profile image to the new URI
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    imagePath = uri;
                    if (Objects.equals(uploadType, "avatar")) binding.OProfileImage.setImageURI(uri);
                    else Picasso.get()
                            .load(imagePath)
                            .fit()
                            .into(binding.OProfileFacilityImg);
                }
            });

    /**
     * If a user hits the delete image button, this method will generate an avatar from the given link based upon the name field
     * and set the image view to the URI
     * @author Jennifer
     * @param name the name the user has entered
     */
    public void generateAvatar(String name) throws URISyntaxException {
        if (name.isEmpty()) name = "null";
        String url = "https://api.multiavatar.com/";
        imagePath = Uri.parse(url+name+"pot.png");

        Picasso.get()
                .load(imagePath)
                .into(binding.OProfileImage);

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


        binding = OrganiserFragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
/*
        Spinner role_dropdown = (Spinner) root.findViewById(R.id.organiser_spinner_role);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                root.getContext(),
                R.array.role,
                R.layout.e_p_role_dropdown
        );

        adapter.setDropDownViewResource(R.layout.e_p_role_dropdown);
        role_dropdown.setAdapter(adapter);

        // need to do this so the listener is connected
        role_dropdown.setOnItemSelectedListener(this);*/

        ImageView editButton = root.findViewById(R.id.O_profile_editBtn);
        Button updateButton = root.findViewById(R.id.O_profile_updateBtn);

        // All the text fields
        EditText name_f = root.findViewById(R.id.O_profile_textName);
        EditText pronoun_f = root.findViewById(R.id.O_profile_textPronouns);
        EditText phone_f = root.findViewById(R.id.O_profile_textPhone);
        EditText email_f = root.findViewById(R.id.O_profile_textEmail);
        EditText facilityName_f = root.findViewById(R.id.O_Profile_facilityNameText);
        EditText facilityLocation_f = root.findViewById(R.id.O_Profile_facilityLocationText);


        // Image Buttons
        Button uploadIm_btn = root.findViewById(R.id.O_profile_uploadImageBtn);
        Button deleteIm_btn = root.findViewById(R.id.O_profile_deleteImageBtn);
        Button facility_uploadIm_btn = root.findViewById(R.id.O_Profile_facilityUploadImagebtn);


        // Allows te page elements to be edited by the user if the edit button is clicked
        View.OnClickListener edit = new View.OnClickListener() {
            /**
             * Tell which elements to become focusable, to appear or disappear
             * @author Jennifer
             * @param view the fragment view
             */
            @Override
            public void onClick(View view) {
                // set focusable
                name_f.setFocusable(true);
                pronoun_f.setFocusable(true);
                phone_f.setFocusable(true);
                email_f.setFocusable(true);
                facilityName_f.setFocusable(true);
                facilityLocation_f.setFocusable(true);

                // set focusable in touch mode
                name_f.setFocusableInTouchMode(true);
                pronoun_f.setFocusableInTouchMode(true);
                phone_f.setFocusableInTouchMode(true);
                email_f.setFocusableInTouchMode(true);
                facilityName_f.setFocusableInTouchMode(true);
                facilityLocation_f.setFocusableInTouchMode(true);

                // set visibility
                updateButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);
                uploadIm_btn.setVisibility(View.VISIBLE);
                deleteIm_btn.setVisibility(View.VISIBLE);
                facility_uploadIm_btn.setVisibility(View.VISIBLE);
            }
        };

        View.OnClickListener update = new View.OnClickListener() {
            /**
             * Tell which elements to become unfocusable, to appear or disappear
             * @author Jennifer
             * @param view the fragment view
             */
            @Override
            public void onClick(View view) {
                // remove focus
                name_f.setFocusable(false);
                pronoun_f.setFocusable(false);
                phone_f.setFocusable(false);
                email_f.setFocusable(false);
                facilityName_f.setFocusable(false);
                facilityLocation_f.setFocusable(false);

                // remove focus in touch mode
                name_f.setFocusableInTouchMode(false);
                pronoun_f.setFocusableInTouchMode(false);
                phone_f.setFocusableInTouchMode(false);
                email_f.setFocusableInTouchMode(false);
                facilityName_f.setFocusableInTouchMode(false);
                facilityLocation_f.setFocusableInTouchMode(false);

                // set visibility
                updateButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
                uploadIm_btn.setVisibility(View.GONE);
                deleteIm_btn.setVisibility(View.GONE);
                facility_uploadIm_btn.setVisibility(View.GONE);

            }
        };


        // Listener for the uploadAvatar image button, it allows the user to select a photo from their photo gallery by launching the media picker
        View.OnClickListener uploadAvatar = new View.OnClickListener() {
            /**
             * Tells the media picker to launch when the button listener is triggered
             * @author Jennifer
             * @param view the fragment view
             */
            @Override
            public void onClick(View view) {
                uploadType = "avatar";
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        };

        View.OnClickListener uploadFacility = new View.OnClickListener() {
            /**
             * Tells the media picker to launch when the button listener is triggered
             * @author Jennifer
             * @param view the fragment view
             */
            @Override
            public void onClick(View view) {
                uploadType = "facility";
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        };


        // On click Listener for the delete image button
        View.OnClickListener deleteAvatar = new View.OnClickListener() {
            /**
             * Sends the users name to the method getAvatar
             * @author Jennifer
             * @param view the fragment view
             */
            @Override
            public void onClick(View view) {
                try {
                    generateAvatar(name_f.getText().toString());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        facility_uploadIm_btn.setOnClickListener(uploadFacility);
        uploadIm_btn.setOnClickListener(uploadAvatar);
        deleteIm_btn.setOnClickListener(deleteAvatar);
        editButton.setOnClickListener(edit);
        updateButton.setOnClickListener(update);
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