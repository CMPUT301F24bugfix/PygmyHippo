package com.example.pygmyhippo;

/*
Starting point of the app
Purposes:
    - Sets up the navbar for navigating between the three fragments (for any role)
    - Signs an account in based on device ID
Issues:
    - Decide where to put notification permission handler
 */


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.database.MainActivityDB;
import com.example.pygmyhippo.databinding.AdminMainActivityNavigationBinding;
import com.example.pygmyhippo.databinding.OrganiserMainActivityNagivationBinding;
import com.example.pygmyhippo.databinding.UserMainActivityNagivationBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Main Activity for our android app
 *
 * @author Jennifer, Griffin
 */
public class MainActivity extends AppCompatActivity implements DBOnCompleteListener<Account> {
    final int PERMISSION_REQUEST_CODE =112;
    final boolean useDB = true;

    // Get the nav bars for each role
    private OrganiserMainActivityNagivationBinding organiserBinder;
    private UserMainActivityNagivationBinding userBinder;
    private AdminMainActivityNavigationBinding adminBinding;
    private MainActivityDB dbHandler;

    // Views used in create user builder
    private LinearLayout builderLayout;
    private EditText newNameView, newEmailView, newPhoneView;
    private CheckBox userCheck, organiserCheck;

    private Account signedInAccount;
    private Account.AccountRole currentRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getNavArguments();
        /* This code is from the stack overflow to fix an error I was having when trying to commit to github.
        It enables the app the app to prompt for user notifications... I don't know what was triggering the error.
        Author: Babbo Natale
        Posted: Feb 5, 2023 [ Accessed October 25, 2024 ]
        https://stackoverflow.com/questions/73940694/android-13-not-asking-for-post-notifications-permission
        */
        if (Build.VERSION.SDK_INT > 32) {
            if (!shouldShowRequestPermissionRationale("112")){
                getNotificationPermission();
            }
        }
    }


    /**
     * Dialog pop up asking for notification preference
     * TODO: Decide to use this or the radio buttons in the profile
     */
    public void getNotificationPermission(){
        try {
            if (Build.VERSION.SDK_INT > 32) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // allow

                }  else {
                    //deny
                }
                return;
        }

    }

    /**
     * This function does the initialization on the BottomNavigationView object passed
     * For user navigation
     * @author Griffin
     * @param navView: The navbar
     * @param bundle: Arguments to pass to first fragment.
     */
    private void setupNavControllerUser(BottomNavigationView navView, Bundle bundle) {
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                // TODO: change the navigation names for user to be more descriptive
                R.id.u_my_events_menu_item, R.id.u_scanQR_menu_item, R.id.u_profile_menu_item)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navController.setGraph(R.navigation.user_mobile_navigation, bundle);
        NavigationUI.setupWithNavController(navView, navController);

        navController.navigate(R.id.u_scanQR_menu_item, bundle);
        userBinder.navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.u_profile_menu_item) {
                Log.d("Menu", "Sending account to profile");
                navController.navigate(R.id.u_profile_menu_item, bundle);
            } else if (item.getItemId() == R.id.u_scanQR_menu_item) {
                navController.navigate(R.id.u_scanQR_menu_item, bundle);
            } else if (item.getItemId() == R.id.u_my_events_menu_item) {
                navController.navigate(R.id.u_my_events_menu_item, bundle);
            } else {
                Toast.makeText(this, "Unknown menu item selected for navigation", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    /**
     * This function does the initialization on the BottomNavigationView object passed
     * For organiser navigation
     * @author Griffin
     * @param navView: The nav bar
     * @param bundle: Arguments to pass to first fragment.
     */
    private void setupNavControllerOrganiser(BottomNavigationView navView, Bundle bundle) {
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.organiser_myEvents_page, R.id.organiser_postEvent_page, R.id.organiser_profile_page)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navController.setGraph(R.navigation.organiser_mobile_navigation, bundle);
        NavigationUI.setupWithNavController(navView, navController);
        navController.navigate(R.id.organiser_postEvent_page, bundle);
        organiserBinder.navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.organiser_myEvents_page) {
                navController.navigate(R.id.organiser_myEvents_page, bundle);
            } else if (item.getItemId() == R.id.organiser_postEvent_page) {
                navController.navigate(R.id.organiser_postEvent_page, bundle);
            } else if (item.getItemId() == R.id.organiser_profile_page) {
                navController.navigate(R.id.organiser_profile_page, bundle);
            } else {
                Toast.makeText(this, "Unknown menu item selected for navigation", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }


    /**
     * This function does the initialization on the BottomNavigationView object passed
     * For Admin navigation
     * @author Griffin
     * @param navView: The nav bar
     * @param bundle: Arguments to pass to first fragment.
     */
    public void setupNavControllerAdmin(BottomNavigationView navView, Bundle bundle) {
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.admin_all_events_menu_item, R.id.admin_all_images_menu_item, R.id.admin_all_profiles_menu_item)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navController.setGraph(R.navigation.admin_mobile_navigation, bundle);
        NavigationUI.setupWithNavController(navView, navController);
        navController.navigate(R.id.admin_navigation_all_images, bundle);
        adminBinding.navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.admin_all_events_menu_item) {
                navController.navigate(R.id.admin_navigation_all_events, bundle);
            } else if (item.getItemId() == R.id.admin_all_images_menu_item) {
                navController.navigate(R.id.admin_navigation_all_images, bundle);
            } else if (item.getItemId() == R.id.admin_all_profiles_menu_item) {
                navController.navigate(R.id.admin_navigation_all_users, bundle);
            } else {
                Toast.makeText(this, "Unknown menu item selected for navigation", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    /**
     * Callback called when DB queries complete.
     * @param docs - Documents retrieved from DB (if it was a get query).
     * @param queryID - ID of query completed.
     * @param flags - Flags to indicate query status/set how to process query result.
     */
    @Override
    public void OnCompleteDB(@NonNull ArrayList<Account> docs, int queryID, int flags) {
        switch (queryID) {
            case 0: // getDeviceAccount()
                if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                    signedInAccount = docs.get(0);
                    Toast toast = Toast.makeText(this,
                            String.format("Got account (%s)", signedInAccount.getAccountID()),
                            Toast.LENGTH_SHORT);
                    toast.show();

                    // Set up the current role navigation
                    currentRole = signedInAccount.getCurrentRole();
                    setupNavController();
                } else if (flags == DBOnCompleteFlags.NO_DOCUMENTS.value) {
                    // Make the new signed in account with the recorded device ID
                    signedInAccount = new Account();
                    signedInAccount.setDeviceID(getDeviceID());

                    // Make a dialog builder that the user must to fill out important initial details
                    AlertDialog.Builder builder = formatBuilder();

                    // Show the builder
                    AlertDialog createAccount = builder.create();
                    createAccount.show();

                    // Set the onclick listener for the positive button here so it won't always close
                    // Got this idea from https://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked
                    // Accessed on 2024-11-24
                    createAccount.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            // Get all the entered values
                            String newName = newNameView.getText().toString();
                            String newEmail = newEmailView.getText().toString();
                            String newPhone = newPhoneView.getText().toString();
                            boolean isUser = userCheck.isChecked();
                            boolean isOrganiser = organiserCheck.isChecked();

                            if (newName.isEmpty() || newEmail.isEmpty()) {
                                // One of the required fields weren't filled so don't continue
                                Toast toast = Toast.makeText(getBaseContext(),
                                        "Error: You must enter both name and email to continue. Try again",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            } else if (!isUser && !isOrganiser) {
                                // No roles were selected so don't continue
                                Toast toast = Toast.makeText(getBaseContext(),
                                        "Error: No role selected. Try again",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                // All required fields are entered, so add that to the new account class
                                signedInAccount.setName(newName);
                                signedInAccount.setEmailAddress(newEmail);
                                signedInAccount.setPhoneNumber(newPhone);
                                if (isUser) {
                                    signedInAccount.getRoles().add(Account.AccountRole.user);

                                    // Set the current account role
                                    signedInAccount.setCurrentRole(Account.AccountRole.user);
                                }
                                if (isOrganiser) {
                                    signedInAccount.getRoles().add(Account.AccountRole.organiser);
                                    if (!isUser) {
                                        // If account isn't also a user, then set this as current role
                                        signedInAccount.setCurrentRole(Account.AccountRole.organiser);
                                    }
                                }
                                // Add the new account to the database with these new fields
                                dbHandler.addNewDevice(signedInAccount, MainActivity.this::OnCompleteDB);

                                // Finally close the dialog
                                createAccount.dismiss();
                            }
                        }
                    });
                } else {
                    handleDBError();
                }
                break;
            case 1: // addNewDevice
                if (flags == DBOnCompleteFlags.SUCCESS.value) {
                    // Account did not exist based on device ID so a new one is automatically made
                    Toast toast = Toast.makeText(this,
                            "Made new account for device",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    signedInAccount = docs.get(0);

                    // Set up the current role navigation
                    currentRole = signedInAccount.getCurrentRole();
                    setupNavController();
                } else {
                    Toast toast = Toast.makeText(this,
                            "Could not make new account for device",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            default:
                Log.i("DB", String.format("Unknown query ID (%d)", queryID));
                handleDBError();
        }
    }

    /**
     * This method will format the builder used for the user to enter in their profile details
     * @author Kori
     * @return The builder once its formatted
     */
    public AlertDialog.Builder formatBuilder() {
        // Set the title and message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Account");
        builder.setMessage("Fill in the required details to continue:");
        builder.setCancelable(false);

        // Get the layout and set that to the builder
        // This code snippet is from https://stackoverflow.com/questions/10226740/get-linearlayout-from-another-xml-android
        // Accessed on 2024-11-24
        LayoutInflater inflater;
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        builderLayout = (LinearLayout) inflater.inflate(R.layout.create_user_dialog_content , null);

        // Set the retrieved layout view
        builder.setView(builderLayout);

        // Initialize our editable views from the layout
        newNameView = builderLayout.findViewById(R.id.new_name_txt);
        newEmailView = builderLayout.findViewById(R.id.new_email_txt);
        newPhoneView = builderLayout.findViewById(R.id.new_phone_txt);
        userCheck = builderLayout.findViewById(R.id.new_role_user);
        organiserCheck = builderLayout.findViewById(R.id.new_role_organiser);

        // Set the positive button
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // For now nothing is set. Or else clicking it will instantly close the dialog
                // So we want to set it after the dialog is shown so that it won't close unless the condition is matched
            }
        });

        return builder;
    }

    /**
     * Displays DB errors
     */
    private void handleDBError() {
        Toast toast = Toast.makeText(this, "DB Error!", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * ID used for identifying each user.
     *
     * TODO: replace with firebase installation IDs later.
     * @return Device ID.
     */
    private String getDeviceID() {
        // TODO: Move to Firebase Installation IDs.
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Setup navigation for user with a specific role.
     */
    private void setupNavController() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("signedInAccount", signedInAccount);

        // Use a switch to determine the nagivation based on the current role
        switch (currentRole) {
            case user:
                Log.d("MainActivity", "Setting navigation for user.");
                userBinder = UserMainActivityNagivationBinding.inflate(getLayoutInflater());
                setContentView(userBinder.getRoot());
                setupNavControllerUser(userBinder.navView, bundle);
                break;
            case organiser:
                Log.d("MainActivity", "Setting navigation for organiser.");
                organiserBinder = OrganiserMainActivityNagivationBinding.inflate(getLayoutInflater());
                setContentView(organiserBinder.getRoot());
                setupNavControllerOrganiser(organiserBinder.navView, bundle);
                break;
            case admin:
                Log.d("MainActivity", "Setting navigation for admin.");
                adminBinding = AdminMainActivityNavigationBinding.inflate(getLayoutInflater());
                setContentView(adminBinding.getRoot());
                setupNavControllerAdmin(adminBinding.navView, bundle);
                break;
            default:
                Log.d("MainActivity", String.format("Unknown role (%s)", currentRole.value));
                break;
        }
    }

    /**
     * Calculates the current role based on what the main activity receives
     */
    private void getNavArguments() {
        String receivedRole = getIntent().getStringExtra("currentRole") ;
        signedInAccount = getIntent().getParcelableExtra("signedInAccount");

        Log.d("MainActivity", String.format("Received %s as current role.", receivedRole));
        if (receivedRole == null) {
            // If the account didn't exist, they get automatically set as a user for now
            currentRole = Account.AccountRole.user;
        } else {
            // Actually set the role of the incoming account
            switch (receivedRole) {
                case "organiser":
                    currentRole = Account.AccountRole.organiser;
                    break;
                case "admin":
                    currentRole = Account.AccountRole.admin;
                    break;
                default:
                    currentRole = Account.AccountRole.user;
            }
        }

        if (signedInAccount == null) {
            if (useDB) {
                // Makes a new account in the database if there wasn't one matching the device ID
                Log.d("MainActivity", "Fetching account from DB based on DeviceID");
                dbHandler = new MainActivityDB();
                dbHandler.getDeviceAccount(getDeviceID(), this);
            } else {
                // FIXME: For initial testing
                Log.d("MainActivity", "Using mock data");
                signedInAccount = new Account(
                        "1",  // accountID
                        "Moo Deng",  // name
                        "She/Her",  // pronouns
                        "7801234567",  // phoneNumber
                        "MooDeng@ualberta.ca",  // emailAddress
                        "1",  // deviceID
                        "profilePic.png",  // profilePicture
                        "Edmonton, Alberta",  // location
                        true,  // receiveNotifications
                        true,  // enableGeolocation
                        new ArrayList<>(Arrays.asList(Account.AccountRole.user, Account.AccountRole.organiser)),  // roles
                        Account.AccountRole.organiser,  // CHANGE EITHER ORANIZER OR USER FOR ROLE currentRole (TODO: Change this if you want to test with user)
                        null  // facilityProfile
                );
                setupNavController();
            }
        } else {
            // TODO: Keep integrity of signedInAccount with Firestore (make sure fields match).
            setupNavController();
        }
    }
}
