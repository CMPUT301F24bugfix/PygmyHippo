package com.example.pygmyhippo.user;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pygmyhippo.MainActivity;
import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.GenerateAvatar;
import com.example.pygmyhippo.databinding.UserFragmentNewUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.installations.FirebaseInstallations;
import java.net.URISyntaxException;


/**
 * For creating a new entrant
 *
 * Currently just a static page.
 * Allows the user to edit or view their current provided information.
 * @author Jennifer
 * @version 1.0
 * No returns and no parameters
 */
public class NewUserFragment extends Fragment {
    Uri imagePath;
    MainActivity activity = new MainActivity(); // to enable access to the internal file directory
    private UserFragmentNewUserBinding binding;

    /**
     * This is called when the fragment is created, this ensure a connection the onrole listener to the parent
     * @author Griffin
     * @param context not sure
     */

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

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
                    System.out.println("Pathname for Picker: " + imagePath.toString());
                    binding.ENewUserProfileImg.setImageURI(uri);
                }
            });



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

        binding = UserFragmentNewUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        final String[] deviceID = new String[1];

        FirebaseInstallations.getInstance().getId()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            deviceID[0] = task.getResult();
                            Log.d("Installations", "Installation ID: " + task.getResult());
                        } else {
                            Log.e("Installations", "Unable to get Installation ID");
                        }
                    }
                });

        Button createButton = view.findViewById(R.id.E_newUser_create);

        // All the text fields

        EditText nameField = view.findViewById(R.id.E_newUser_textName);
        EditText pronounField = view.findViewById(R.id.E_newUser_textPronouns);
        EditText phoneField = view.findViewById(R.id.E_newUser_textPhone);
        EditText emailField = view.findViewById(R.id.E_newUser_textEmail);

        // Functional Radio Groups
        RadioButton notifyRadio = view.findViewById(R.id.E_newUser_notifications_yes);
        RadioButton geolocationRadio = view.findViewById(R.id.E_newUser_geo_yes);

        // Image Buttons
        Button uploadIm_btn = view.findViewById(R.id.E_newUser_uploadImage);

        /*
         * Creates the user in the DB from the provided user info
         * @author Jennifer
         */


        View.OnClickListener createUser = new View.OnClickListener() {
            /**
             * will create a new user after checking conditions are met
             * @author Jennifer
             * @param view the fragment view
             */


            @Override
            public void onClick(View view) {
                String userName = nameField.getText().toString();
                String userPhone = phoneField.getText().toString();
                String userEmail = emailField.getText().toString();
                String userPronouns = pronounField.getText().toString();
                Boolean userNotify = notifyRadio.isChecked();
                Boolean userGeolocation = geolocationRadio.isChecked();

                Boolean readyToCreate = true;

                if (userName.isEmpty() || userPhone.isEmpty() || userEmail.isEmpty() || userPronouns.isEmpty()) {
                    readyToCreate = false;
                }

                if (imagePath == null) {
                    try {
                        GenerateAvatar generator = new GenerateAvatar();
                        imagePath = generator.generateAvatar(getContext(), userName);

                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    if (readyToCreate) {
                        Account newUser = new Account(
                                userEmail + deviceID[0],
                                userName,
                                userPronouns,
                                userPhone,
                                userEmail,
                                deviceID[0],
                                imagePath,
                                "Edmonton",
                                userNotify,
                                userGeolocation
                        );
                    }

                }


            }
        };

        /*
         * Listener for the uploadAvatar image button, it allows the user to select a photo from their photo gallery by launching the media picker
         * @author Jennifer
         */
        View.OnClickListener uploadAvatar = new View.OnClickListener() {
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


        uploadIm_btn.setOnClickListener(uploadAvatar);
        createButton.setOnClickListener(createUser);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}