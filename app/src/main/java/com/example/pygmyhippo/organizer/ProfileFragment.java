package com.example.pygmyhippo.organizer;

/*
Profile fragment of an organiser
Purposes:
    - Allows organiser to see their profile details
    - Allows organiser to update their details
    - Allows organiser to make a facility profile
    - Spinner allows for user to change their account role
Issues:
    - NA
 */

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.Facility;
import com.example.pygmyhippo.common.Image;
import com.example.pygmyhippo.database.AccountDB;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.database.ImageStorage;
import com.example.pygmyhippo.database.StorageOnCompleteListener;
import com.example.pygmyhippo.databinding.OrganiserFragmentProfileBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


/**
 * This fragment holds most of the information about a user which is returned from a call to the database
 * To be Implemented:
 * - Facility profile page
 *
 * Allows the organiser to edit or view their current provided information.
 * @author Jennifer, Griffin, Kori
 * @version 1.3
 * No returns and no parameters
 */
public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener, DBOnCompleteListener<Account>, StorageOnCompleteListener<Image> {
    private String imagePath = null;
    private String uploadType = "avatar";
    private Account signedInAccount;
    private String currentRole;

    private OrganiserFragmentProfileBinding binding;
    private NavController navController;
    private AccountDB handler;
    private ImageStorage imageHandler;

    private EditText name_f, pronoun_f, phone_f, email_f, facilityName_f, facilityLocation_f;
    private ImageView profileImage, facilityImage;
    private ConstraintLayout facilityLayout;
    private Button createFacilityButton;


     // Registers a photo picker activity launcher in single-select mode and sets the profile image to the new URI
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    // Check if avatar or facility is being updated
                    if (uploadType.equals("avatar")) {
                        // Set the profile image
                        binding.OProfileImage.setImageURI(uri);

                        // If the user already had a profile picture, make sure to delete the old one first
                        if (!signedInAccount.getProfilePicture().isEmpty()) {
                            imageHandler.DeleteImageByURL(signedInAccount.getProfilePicture(), this);
                        }

                        // Save the image in the database
                        imageHandler.uploadProfileImageToFirebase(uri, this);
                    } else if (uploadType.equals("facility")) {
                        // Set the facility image
                        binding.OProfileFacilityImg.setImageURI(uri);

                        // If the user already had a profile picture, make sure to delete the old one first
                        if (!signedInAccount.getFacilityProfile().getFacilityPicture().isEmpty()) {
                            imageHandler.DeleteImageByURL(signedInAccount.getFacilityProfile().getFacilityPicture(), this);
                        }

                        // Save the image in the database
                        imageHandler.uploadFacilityImageToFirebase(uri, this);
                    }
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
        Uri avatarURI = Uri.parse(url+name+".png");

        Picasso.get()
                .load(avatarURI)
                .into(binding.OProfileImage);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    /**
     * @author Jennifer
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The profile view
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = OrganiserFragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get buttons
        ImageView editButton = root.findViewById(R.id.O_profile_editBtn);
        Button updateButton = root.findViewById(R.id.O_profile_updateBtn);

        // All the text fields
        name_f = root.findViewById(R.id.O_profile_textName);
        pronoun_f = root.findViewById(R.id.O_profile_textPronouns);
        phone_f = root.findViewById(R.id.O_profile_textPhone);
        email_f = root.findViewById(R.id.O_profile_textEmail);
        facilityName_f = root.findViewById(R.id.O_Profile_facilityNameText);
        facilityLocation_f = root.findViewById(R.id.O_Profile_facilityLocationText);


        // Image Buttons
        Button uploadIm_btn = root.findViewById(R.id.O_profile_uploadImageBtn);
        Button deleteIm_btn = root.findViewById(R.id.O_profile_deleteImageBtn);
        Button facility_uploadIm_btn = root.findViewById(R.id.O_Profile_facilityUploadImagebtn);
        createFacilityButton = root.findViewById(R.id.O_create_facility_button);

        // Images
        profileImage = root.findViewById(R.id.O_profile_image);
        facilityImage = root.findViewById(R.id.O_profile_facilityImg);

        // Get the layout
        facilityLayout = root.findViewById(R.id.facility_layout);

        // Get the account and initialize the db and storage handlers
        handler = new AccountDB();
        imageHandler = new ImageStorage();
        setProfile();

        Spinner roleSpinner = binding.oRoleSpinner;

        // Set up the spinner for assigned roles (only if the account has more than one role)
        if (signedInAccount.getRoles().size() > 1) {
            // Make the string array depending on the roles of the account
            ArrayList<String> rolesList = new ArrayList<String>();
            rolesList.add("-- select role --");         // For display

            // Add each role to the array
            for (int index = 0; index < signedInAccount.getRoles().size(); index++) {
                rolesList.add(signedInAccount.getRoles().get(index).value);
            }

            // Convert the ArrayList to a String[]
            // From https://www.geeksforgeeks.org/convert-an-arraylist-of-string-to-a-string-array-in-java/
            // Accessed on 2024-11-24
            String[] roles = Arrays.copyOf(rolesList.toArray(),
                    rolesList.size(), String[].class);

            // https://stackoverflow.com/questions/2505207/how-to-add-item-to-spinners-arrayadapter
            // Helped understand this. Accessed on 2024-11-24
            ArrayAdapter<CharSequence> roleAdapter = new ArrayAdapter<>(getContext(),
                    R.layout.e_p_role_dropdown,
                    roles);

            roleSpinner.setAdapter(roleAdapter);

            roleSpinner.setOnItemSelectedListener(this);
        } else {
            // Make the spinner gone since the user can't change roles
            roleSpinner.setVisibility(View.GONE);
        }


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
                createFacilityButton.setVisibility(View.GONE);
                uploadIm_btn.setVisibility(View.VISIBLE);
                deleteIm_btn.setVisibility(View.VISIBLE);
                facility_uploadIm_btn.setVisibility(View.VISIBLE);
            }
        };

        View.OnClickListener update = new View.OnClickListener() {
            /**
             * Tell which elements to become unfocusable, and stores the new user info in the database
             * @author Jennifer, Kori
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

                // Update the corresponding fields of the account
                signedInAccount.setName(name_f.getText().toString());
                signedInAccount.setPronouns(pronoun_f.getText().toString());
                signedInAccount.setPhoneNumber(phone_f.getText().toString());
                signedInAccount.setEmailAddress(email_f.getText().toString());

                String facilityName = facilityName_f.getText().toString();
                String facilityLocation = facilityLocation_f.getText().toString();
                signedInAccount.getFacilityProfile().setName(facilityName);
                signedInAccount.getFacilityProfile().setLocation(facilityLocation);

                if (facilityName.isEmpty()
                        && facilityLocation.isEmpty()
                        && signedInAccount.getFacilityProfile().getFacilityPicture().isEmpty()) {
                    createFacilityButton.setVisibility(View.VISIBLE);
                } else {
                    createFacilityButton.setVisibility(View.GONE);
                }

                // Update to reflect in the database
                handler.updateProfile(signedInAccount, new DBOnCompleteListener<Account>() {
                    @Override
                    public void OnCompleteDB(@NonNull ArrayList<Account> docs, int queryID, int flags) {
                        // Log when the data is updated or catch if there was an error
                        if (flags == DBOnCompleteFlags.SUCCESS.value) {
                            Log.d("DB", "Successfully finished updating account");
                        } else {
                            // If not the success flag, then there was an error
                            Log.d("ProfileFragment", "Error in updating account.");
                        }
                    }
                });
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
             * @author Jennifer, Kori
             * @param view the fragment view
             */
            @Override
            public void onClick(View view) {
                // First delete the image from the user, and the firebase storage
                if (!signedInAccount.getProfilePicture().isEmpty()) {
                    imageHandler.DeleteImageByURL(signedInAccount.getProfilePicture(), ProfileFragment.this::OnCompleteStorage);
                }

                // Update the profile
                signedInAccount.setProfilePicture("");
                handler.updateProfile(signedInAccount, ProfileFragment.this::OnCompleteDB);

                // Next, replace the old image with a generated avatar
                try {
                    generateAvatar(name_f.getText().toString());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        View.OnClickListener createFacility = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make the Facility text views visible and the create button invisible
                // Note: The layout won't stay visible unless the user enters in at least one value
                createFacilityButton.setVisibility(View.GONE);
                facilityLayout.setVisibility(View.VISIBLE);
            }
        };

        facility_uploadIm_btn.setOnClickListener(uploadFacility);
        uploadIm_btn.setOnClickListener(uploadAvatar);
        deleteIm_btn.setOnClickListener(deleteAvatar);
        editButton.setOnClickListener(edit);
        updateButton.setOnClickListener(update);
        createFacilityButton.setOnClickListener(createFacility);
        return root;
    }

    /**
     * This method just populates the profile details with the current account
     * @param currentAccount The account of the current user
     */
    private void populateAllViews(Account currentAccount){
        handler.getAccountByID(currentAccount.getAccountID(), new DBOnCompleteListener<Account>() {
            @Override
            public void OnCompleteDB(@NonNull ArrayList<Account> docs, int queryID, int flags) {
                if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value){
                    Account account = docs.get(0);
                    Log.d("Organizer Profile", "DB complete successful");
                    populateAllViewsAfterDB(account);
                }
                else{
                    Log.e("Organizer Profile", "DB complete NOT successful");
                }
            }
        });
    }

    /**
     * This method just populates the profile details after the database is queried
     * @param account The account of the current user
     */
    private void populateAllViewsAfterDB(Account account) {
        name_f.setText(account.getName());
        pronoun_f.setText(account.getPronouns());
        if (account.getPhoneNumber() != null) {
            // Check first because this field is optional
            phone_f.setText(account.getPhoneNumber());
        }
        email_f.setText(account.getEmailAddress());

        // Get the profile picture if it has already been set
        if (!account.getProfilePicture().isEmpty()) {
            // Get the profile picture
            imageHandler.getImageDownloadUrl(account.getProfilePicture(), new StorageOnCompleteListener<Uri>() {
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
                generateAvatar(account.getName());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        // Check if the account has a facility
        if (account.getFacilityProfile().facilityExists()) {
            // Change and populate the views to display the facility
            populateFacilityViews(account);
        }
        else{
            facilityLayout.setVisibility(View.GONE);
        }
    }

    /**
     * This method will set the fields for facility visible with their respective existing values
     * @author Kori
     * @param account The account with the facility we want to display
     */
    public void populateFacilityViews(Account account) {
        // Make the layout visible
        facilityLayout.setVisibility(View.VISIBLE);
        createFacilityButton.setVisibility(View.GONE);

        if (signedInAccount.getFacilityProfile().getName() != null) {
            // Set the name
            facilityName_f.setText(account.getFacilityProfile().getName());
        }
        if (signedInAccount.getFacilityProfile().getLocation() != null) {
            // Set the location
            facilityLocation_f.setText(account.getFacilityProfile().getLocation());
        }

        // Get the facility picture if it has already been set
        if (!signedInAccount.getFacilityProfile().getFacilityPicture().isEmpty()) {
            // Get the profile picture
            imageHandler.getImageDownloadUrl(signedInAccount.getFacilityProfile().getFacilityPicture(), new StorageOnCompleteListener<Uri>() {
                @Override
                public void OnCompleteStorage(@NonNull ArrayList<Uri> docs, int queryID, int flags) {
                    if (flags == DBOnCompleteFlags.SUCCESS.value) {
                        // Get the image and format it
                        Uri downloadUri = docs.get(0);
                        int imageWidth = facilityImage.getWidth();
                        int imageHeight = facilityImage.getHeight();
                        Picasso.get()
                                .load(downloadUri)
                                .resize(imageWidth, imageHeight)
                                .centerCrop()
                                .into(facilityImage);
                    }
                }
            });
        }
    }

    /**
     * This method will retrieve the signed in account and call for the details to be populated
     */
    private void setProfile() {
        signedInAccount = ProfileFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
        currentRole = ProfileFragmentArgs.fromBundle(getArguments()).getCurrentRole();

        Log.d("ProfileFragment", String.format("signedInAccount %s", signedInAccount));
        if (signedInAccount != null) {
            // An account was
            Log.d("ProfileFragment", String.format("Populating text views with %s %s", signedInAccount.getAccountID(), signedInAccount.getName()));
            populateAllViews(signedInAccount);
        } else {
            Log.d("ProfileFragment", "ERROR!");
        }
    }

    /**
     * since this implements the OnTimeSelectedLister we need to override these two methods to get
     * the communication working
     * @author Griffin
     * @param adapterView: The adapter view of the selectable options
     * @param view current view
     * @param i position of item clicked
     * @param l long
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedRole = adapterView.getItemAtPosition(i).toString().toLowerCase();
        Log.d("ProfileFragment", String.format("Role Spinner selected %s", selectedRole));

        // TODO: Verify signedInAccount has selected role in roles before navigating to new Main Activity.
        // ACCOUNT MUST ALWAYS HAVE USER ROLE!!!
        Bundle navArgs = new Bundle();
        navArgs.putParcelable("signedInAccount", signedInAccount);
        if (selectedRole.equals("user")) {
            // Update current account and navigate to the user stuff
            navArgs.putString("currentRole", "user");
            navController.navigate(R.id.u_mainActivity, navArgs);
        } else if (selectedRole.equals("organiser")) {
            Log.d("ProfileFragment", "User was selected, not doing anything :)");
        } else if (selectedRole.equals("admin")) {
            // Update current account and navigate to the admin stuff
            navArgs.putString("currentRole", "admin");
            navController.navigate(R.id.a_mainActivity, navArgs);
        } else {
            Log.d("ProfileFragment", String.format("Unknown role selected %s", selectedRole));
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

    /**
     * Data callback for account data
     * @param docs - Documents retrieved from DB (if it was a get query).
     * @param queryID - ID of query completed.
     * @param flags - Flags to indicate query status/set how to process query result.
     */
    @Override
    public void OnCompleteDB(@NonNull ArrayList<Account> docs, int queryID, int flags) {
        if (queryID == 3) {      // Update account
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
     * Callback for image handling
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
            } else {
                Log.d("FirebaseStorage", "Profile image deletion unsuccessful");
            }
        } else if (queryID == 6) {      // uploadFacilityImage
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                Log.d("FirebaseStorage", "Facility image uploaded successfully");

                // Get the image and get its url
                Image facilityImage = docs.get(0);
                imagePath = facilityImage.getUrl();

                // Update the user profile
                signedInAccount.getFacilityProfile().setFacilityPicture(imagePath);
                handler.updateProfile(signedInAccount, this);
            } else {
                Log.d("FirebaseStorage", "Profile image upload unsuccessful");
            }
        }
    }
}