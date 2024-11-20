package com.example.pygmyhippo.user;

/*
This class will display the account of a user
It also provides functionality for the Admin wanting to delete this user
Purposes:
    - Allow user to see their account
    - Allow user to update their account
    - Spinner gives ability to change roles
    - Allows admin functionality to delete the profile (given the permissions)
    - Let's user delete profile pic, which generates a new one
Issues:
    - Decide to keep notifications and geolocation checkboxes when permissions do that anyways
 */

import android.Manifest;
import android.widget.AdapterView;
import android.widget.CheckBox;

import com.example.pygmyhippo.common.Image;
import com.example.pygmyhippo.database.AccountDB;
import com.example.pygmyhippo.database.ImageStorage;
import com.example.pygmyhippo.database.StorageOnCompleteListener;
import com.example.pygmyhippo.databinding.UserFragmentProfileBinding;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.organizer.EventFragment;
import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This fragment holds most of the information about a user which is returned from a call to the database
 * Purpose: To allow the user to update their profile and change their role
 *
 * Allows the user to edit or view their current provided information.
 * @author Jennifer, Griffin, Kori
 * @version 1.3
 * No returns and no parameters
 */
public class ProfileFragment extends Fragment  implements AdapterView.OnItemSelectedListener, DBOnCompleteListener<Account>, StorageOnCompleteListener<Image> {
    private String imagePath = null;
    private ImageStorage imageHandler;
    private UserFragmentProfileBinding binding;
    private NavController navController;
    private AccountDB handler;

    private Account signedInAccount;
    private String adminViewAccountID;
    private String currentRole;

    private EditText nameField, pronounField, phoneField, emailField;
    private CheckBox decGeo, decNotify;
    private Button uploadImgBtn, deleteImgBtn;
    private ImageView editButton;
    private Button updateButton, deleteUserButton;
    private ConstraintLayout adminConstraintLayout;
    private Spinner roleSpinner;
    private CircleImageView profileImage;

    // initialized the listener
    public void setOnRoleSelectedListener() {
        Log.d("ProfileFragment", "Role selected");
    }

    /**
     * Registers a photo picker activity launcher in single-select mode and sets the profile image to the new URI
     * @author Jennifer, Kori
     * @version 2.0
     */
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    // Set the profile image
                    binding.EProfileProfileImg.setImageURI(uri);

                    // If the user already had a profile picture, make sure to delete the old one first
                    if (!signedInAccount.getProfilePicture().isEmpty()) {
                        imageHandler.DeleteImageByURL(signedInAccount.getProfilePicture(), this);
                    }

                    // Save the new image in the database
                    imageHandler.uploadProfileImageToFirebase(uri, this);
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
        Uri avatarURI = Uri.parse(url+name+".png");

        // Retrieve the generated profile picture
        Picasso.get()
                .load(avatarURI)
                .into(binding.EProfileProfileImg);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

    }


    /**
     * Jennifer
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The view of the profile fragment
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = UserFragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize database handlers
        handler = new AccountDB();
        imageHandler = new ImageStorage();

        editButton = root.findViewById(R.id.E_profile_editBtn);
        updateButton = root.findViewById(R.id.E_profile_create);
        deleteUserButton = binding.aDeleteUserButton;

        // All the text fields
        nameField = root.findViewById(R.id.E_profile_textName);
        pronounField = root.findViewById(R.id.E_profile_textPronouns);
        phoneField = root.findViewById(R.id.E_profile_textPhone);
        emailField = root.findViewById(R.id.E_profile_textEmail);

        // Decorator checkboxes
        decGeo = root.findViewById(R.id.E_profile_gps_dec);
        decNotify = root.findViewById(R.id.E_profile_notification_dec);
        decGeo.setEnabled(false);
        decNotify.setEnabled(false);

        // Image Buttons
        uploadImgBtn = root.findViewById(R.id.E_profile_uploadImageBtn);
        deleteImgBtn = root.findViewById(R.id.E_profile_deleteAvatarbtn);
        profileImage = root.findViewById(R.id.E_profile_profileImg);

        // Constraint Layout
        adminConstraintLayout = binding.aDeleteUserConstraint;

        roleSpinner = binding.uRoleSpinner;

        ArrayAdapter<CharSequence> roleAdapter = ArrayAdapter.createFromResource(
                root.getContext(),
                R.array.role,
                R.layout.e_p_role_dropdown
        );
        roleSpinner.setAdapter(roleAdapter);

        roleSpinner.setOnItemSelectedListener(this);

        setProfile();


        /*
         * Allows te page elements to be edited by the user if the edit button is clicked
         * author Jennifer
         */
        View.OnClickListener editUser = new View.OnClickListener() {
            /**
             * Tell whichs elements to become focusable, to appear or disappear
             * @author Jennifer
             * @param view the fragment view
             */
            @Override
            public void onClick(View view) {
                // enable checkboxes
                decGeo.setEnabled(true);
                decNotify.setEnabled(true);

                // Change the text visibilities
                nameField.setFocusable(true);
                pronounField.setFocusable(true);
                phoneField.setFocusable(true);
                emailField.setFocusable(true);
                nameField.setFocusableInTouchMode(true);
                pronounField.setFocusableInTouchMode(true);
                phoneField.setFocusableInTouchMode(true);
                emailField.setFocusableInTouchMode(true);
                updateButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);
                uploadImgBtn.setVisibility(View.VISIBLE);
                deleteImgBtn.setVisibility(View.VISIBLE);

                // edit margins
                // https://stackoverflow.com/questions/34266057/android-viewgroup-marginlayoutparams-not-working
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) decGeo.getLayoutParams();
                params.bottomMargin = 550;
                decGeo.setLayoutParams(params);

            }
        };

        /*
         * Exit edit mode the submit
         * TODO: Need to store the profile picture in the database
         *      Add restrictions on certain fields
         * author Jennifer
         */
        View.OnClickListener updateUser = new View.OnClickListener() {
            /**
             * Tell whichs elements to become unfocusable, to appear or disappear
             * Will also update the attributes of the account and store that in the database
             * @author Jennifer, Kori
             * @param view the fragment view
             */
            @Override
            public void onClick(View view) {
                // disable checkboxes
                decGeo.setEnabled(false);
                decNotify.setEnabled(false);

                // Change the text visibilities
                nameField.setFocusable(false);
                pronounField.setFocusable(false);
                phoneField.setFocusable(false);
                emailField.setFocusable(false);
                nameField.setFocusableInTouchMode(false);
                pronounField.setFocusableInTouchMode(false);
                phoneField.setFocusableInTouchMode(false);
                emailField.setFocusableInTouchMode(false);
                updateButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
                uploadImgBtn.setVisibility(View.GONE);
                deleteImgBtn.setVisibility(View.GONE);

                // Update the corresponding fields of the account
                signedInAccount.setName(nameField.getText().toString());
                signedInAccount.setPronouns(pronounField.getText().toString());
                signedInAccount.setPhoneNumber(phoneField.getText().toString());
                signedInAccount.setEmailAddress(emailField.getText().toString());
                signedInAccount.setReceiveNotifications(decNotify.isChecked());
                signedInAccount.setEnableGeolocation(decGeo.isChecked());

                // Update to reflect in the database
                handler.updateProfile(signedInAccount, ProfileFragment.this::OnCompleteDB);

                // edit margins
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) decGeo.getLayoutParams();
                params.bottomMargin = 350;
                decGeo.setLayoutParams(params);
            }
        };

        /*
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

        /*
         * On click Listener for the deleteImage image button
         * @author Jennifer, Kori
         */
        View.OnClickListener deleteImage = new View.OnClickListener() {
            /**
             * Sends the users name to the method getAvatar and deletes the old image
             * @author Jennifer, Kori
             * @param view the fragment view
             */
            @Override
            public void onClick(View view) {
                // First delete the image from the user, and the firebase storage
                if (!signedInAccount.getProfilePicture().isEmpty()) {
                    imageHandler.DeleteImageByURL(signedInAccount.getProfilePicture(), ProfileFragment.this::OnCompleteStorage);
                }

                // Next, replace the old image with a generated avatar
                try {
                    generateAvatar(nameField.getText().toString());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // Set the listener for each button
        uploadImgBtn.setOnClickListener(uploadImage);
        deleteImgBtn.setOnClickListener(deleteImage);
        editButton.setOnClickListener(editUser);
        updateButton.setOnClickListener(updateUser);
        deleteUserButton.setOnClickListener(view -> {
            handler.deleteAccountByID(adminViewAccountID, this);
        });

        return root;
    }

    /**
     * Populate the profile fields
     * @param account The current account whose data gets displayed
     */
    private void populateAllViews(Account account) {
        nameField.setText(account.getName());
        pronounField.setText(account.getPronouns());
        if (account.getPhoneNumber() != null) {
            // Check first because this field is optional
            phoneField.setText(account.getPhoneNumber());
        }
        emailField.setText(account.getEmailAddress());
        decGeo.setChecked(account.isEnableGeolocation());
        decNotify.setChecked(account.isReceiveNotifications());

        // Get the profile picture if it has already been set
        if (!signedInAccount.getProfilePicture().isEmpty()) {
            // Get the profile picture
            imageHandler.getImageDownloadUrl(signedInAccount.getProfilePicture(), new StorageOnCompleteListener<Uri>() {
                @Override
                public void OnCompleteStorage(@NonNull ArrayList<Uri> docs, int queryID, int flags) {
                    // Author of this code segment is James
                    if (flags == DBOnCompleteFlags.SUCCESS.value) {
                        // Get the image and format it
                        Uri downloadUri = docs.get(0);
                        int imageSideLength = profileImage.getWidth() / 2;
                        Picasso.get()
                                .load(downloadUri)
                                .resize(imageSideLength, imageSideLength)
                                .centerCrop()
                                .into(profileImage);
                    }
                }
            });
        } else {
            // There is no image, so generate the avatar
            try {
                generateAvatar(signedInAccount.getName());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method will get the current signed in account, and change the views
     * of the fragment depending on if their role is admin or user
     */
    private void setProfile() {
        Log.d("ProfileFragment", String.format("adminViewAccountID %s was received", adminViewAccountID));
        signedInAccount = ProfileFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
        adminViewAccountID = ProfileFragmentArgs.fromBundle(getArguments()).getAdminViewAccountID();
        currentRole = ProfileFragmentArgs.fromBundle(getArguments()).getCurrentRole();

        Log.d("ProfileFragment", String.format("signedInAccount %s", signedInAccount));
        if (adminViewAccountID != null && !Objects.equals(adminViewAccountID, "null") && !Objects.equals(adminViewAccountID, signedInAccount.getAccountID())) {
            Log.d("ProfileFragment", String.format("Getting Account %s for admin view", adminViewAccountID));
            handler.getAccountByID(adminViewAccountID, this);

            // Set the views for admin
            adminConstraintLayout.setVisibility(View.VISIBLE);
            roleSpinner.setVisibility(View.INVISIBLE);
        } else if (signedInAccount != null) {
            // Setting the views for users
            Log.d("ProfileFragment", String.format("Populating text views with %s %s", signedInAccount.getAccountID(), signedInAccount.getName()));
            populateAllViews(signedInAccount);
        } else {
            Log.d("ProfileFragment", "ERROR!");
        }
    }

    /**
     * since this implements the OnTimeSelectedLister we need to override these two methods to get
     * the communication working (provides functionality for role changing)
     * @author Griffin
     * @param adapterView: The adapter view of the selectable options
     * @param view: current view
     * @param i: position of item clicked
     * @param l: long
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedRole = adapterView.getItemAtPosition(i).toString().toLowerCase();
        Log.d("ProfileFragment", String.format("Role Spinner selected %s", selectedRole));

        // TODO: Verify signedInAccount has selected role in roles before navigating to new Main Activity.
        // ACCOUNT MUST ALWAYS HAVE USER ROLE!!!
        Bundle navArgs = new Bundle();
        navArgs.putParcelable("signedInAccount", signedInAccount);
        if (selectedRole.equals("user") && !Objects.equals(currentRole, "user")) {
            Log.d("ProfileFragment", "User was selected, not doign anything :)");
            navArgs.putString("currentRole", "user");
            navController.navigate(R.id.u_mainActivity, navArgs);
        } else if (selectedRole.equals("organiser") && !Objects.equals(currentRole, "organiser")) {
            navArgs.putString("currentRole", "organiser");
            navController.navigate(R.id.o_mainActivity, navArgs);
        } else if (selectedRole.equals("admin") && !Objects.equals(currentRole, "admin")) {
            navArgs.putString("currentRole", "admin");
            navController.navigate(R.id.a_mainActivity, navArgs);
        } else {
            Log.d("ProfileFragment", String.format("Did not navigate to %s", selectedRole));
        }
    }

    /**
     * This is for the case when nothing is selected
     * @author Griffin
     * @param adapterView: The adapter view of the selectable options
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d("ProfileFragment", "No role selected using role spinner.");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void OnCompleteDB(@NonNull ArrayList<Account> docs, int queryID, int flags) {
        if (queryID == 0) {
            if (flags != DBOnCompleteFlags.ERROR.value) {
                Account retrievedAccount = docs.get(0);
                populateAllViews(retrievedAccount);
            } else {
                Toast.makeText(getContext(), "Could not get account from Firestore.", Toast.LENGTH_LONG).show();
            }
        } else if (queryID == 1) {
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                Bundle navArgs = new Bundle();
                navArgs.putParcelable("signedInAccount", signedInAccount);
                navController.navigate(R.id.admin_navigation_all_users, navArgs);
            } else {
                Toast.makeText(getContext(), "Error when deleting account from Firestore.", Toast.LENGTH_LONG).show();
            }
        } else if (queryID == 2) {
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                Account account = docs.get(0);
                if (account.getCurrentRole() == Account.AccountRole.organiser) {
                    navController.navigate(R.id.o_mainActivity);
                }
            } else {
                Toast.makeText(getContext(), "Could not change current role", Toast.LENGTH_LONG).show();
            }
        } else if (queryID == 3) {      // Update account
            // Log when the data is updated or catch if there was an error
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                Log.d("DB", "Successfully finished updating account");
            } else {
                // If not the success flag, then there was an error
                Log.d("Profile", "Error in updating account.");
            }
        }
    }

    /**
     * The data callback once image handling is done
     * @param docs - Documents retrieved from DB (if it was a get query).
     * @param queryID - ID of query completed.
     * @param flags - Flags to indicate query status/set how to process query result.
     */
    @Override
    public void OnCompleteStorage(@NonNull ArrayList<Image> docs, int queryID, int flags) {
        if (queryID == 4) {      // uploadProfileImageToFirebase
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                Log.d("FirebaseStorage", "Profile image uploaded successfully");

                // Get the image and get its url
                Image profileImage = docs.get(0);
                imagePath = profileImage.getUrl();

                // Update the user profile
                signedInAccount.setProfilePicture(imagePath);
                handler.updateProfile(signedInAccount, this);
            } else {
                Log.d("FirebaseStorage", "Profile image upload unsuccessful");
            }
        } else if (queryID == 5) {      // Delete image
            // Log the results of the deletion
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                Log.d("FirebaseStorage", "Profile image deleted successfully");

                // Update this result to the account database
                signedInAccount.setProfilePicture("");
                handler.updateProfile(signedInAccount, this);
            } else {
                Log.d("FirebaseStorage", "Profile image deletion unsuccessful");
            }
        }
    }
}