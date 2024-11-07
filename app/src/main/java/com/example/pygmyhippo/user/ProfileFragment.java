package com.example.pygmyhippo.user;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.example.pygmyhippo.databinding.UserFragmentProfileBinding;
import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

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
public class ProfileFragment extends Fragment  implements AdapterView.OnItemSelectedListener, DBOnCompleteListener<Account> {
    private Uri imagePath;
    private UserFragmentProfileBinding binding;
    private NavController navController;
    private ProfileDB handler;

    private Account signedInAccount;
    private String adminViewAccountID;
    private String currentRole;

    private EditText nameField, pronounField, phoneField, emailField;
    private RadioButton decGeo, decNotify;
    private RadioGroup notifyRGroup, geolocationRGroup;
    private Button uploadImgBtn, deleteImgBtn;
    private ImageView editButton;
    private Button updateButton, deleteUserButton;
    private ConstraintLayout adminConstraintLayout;
    private Spinner roleSpinner;

    // initialized the listener
    public void setOnRoleSelectedListener() {
        Log.d("ProfileFragment", "Role selected");
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
                    binding.EProfileProfileImg.setImageURI(uri);
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
                .into(binding.EProfileProfileImg);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

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

        handler = new ProfileDB();

        editButton = root.findViewById(R.id.E_profile_editBtn);
        updateButton = root.findViewById(R.id.E_profile_create);
        deleteUserButton = binding.aDeleteUserButton;

        // All the text fields
        nameField = root.findViewById(R.id.E_profile_textName);
        pronounField = root.findViewById(R.id.E_profile_textPronouns);
        phoneField = root.findViewById(R.id.E_profile_textPhone);
        emailField = root.findViewById(R.id.E_profile_textEmail);

        // Decorator radio buttons
        decGeo = root.findViewById(R.id.E_profile_gps_dec);
        decNotify = root.findViewById(R.id.E_profile_notification_dec);

        // Functional Radio Groups
        notifyRGroup = root.findViewById(R.id.E_profile_notify_setting);
        geolocationRGroup = root.findViewById(R.id.E_profile_geo_setting);

        // Image Buttons
        uploadImgBtn = root.findViewById(R.id.E_profile_uploadImageBtn);
        deleteImgBtn = root.findViewById(R.id.E_profile_deleteAvatarbtn);

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

        /*
         * Exit edit mode the submit
         * To Do: this needs to call whatever method will submit to the database
         * author Jennifer
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
        deleteUserButton.setOnClickListener(view -> {
            handler.deleteAccountByID(adminViewAccountID, this);
        });

        return root;
    }

    private void populateTextViews(Account account) {
        nameField.setText(account.getName());
        pronounField.setText(account.getPronouns());
        phoneField.setText(account.getPhoneNumber());
        emailField.setText(account.getEmailAddress());
        decGeo.setActivated(account.isEnableGeolocation());
        decNotify.setActivated(account.isReceiveNotifications());
    }

    private void setProfile() {
        Log.d("ProfileFragment", String.format("adminViewAccountID %s was received", adminViewAccountID));
        signedInAccount = ProfileFragmentArgs.fromBundle(getArguments()).getSignedInAccount();
        adminViewAccountID = ProfileFragmentArgs.fromBundle(getArguments()).getAdminViewAccountID();
        currentRole = ProfileFragmentArgs.fromBundle(getArguments()).getCurrentRole();

        Log.d("ProfileFragment", String.format("signedInAccount %s", signedInAccount));
        if (adminViewAccountID != null && !Objects.equals(adminViewAccountID, "null") && !Objects.equals(adminViewAccountID, signedInAccount.getAccountID())) {
            Log.d("ProfileFragment", String.format("Getting Account %s for admin view", adminViewAccountID));
            handler.getAccountByID(adminViewAccountID, this);
            adminConstraintLayout.setVisibility(View.VISIBLE);
            roleSpinner.setVisibility(View.INVISIBLE);
        } else if (signedInAccount != null) {
            Log.d("ProfileFragment", String.format("Populating text views with %s %s", signedInAccount.getAccountID(), signedInAccount.getName()));
            populateTextViews(signedInAccount);
        } else {
            Log.d("ProfileFragment", "ERROR!");
        }
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
    public void OnComplete(@NonNull ArrayList<Account> docs, int queryID, int flags) {
        if (queryID == 0) {
            if (flags == DBOnCompleteFlags.SUCCESS.value) {
                Account retrievedAccount = docs.get(0);
                populateTextViews(retrievedAccount);
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
        }
    }
}