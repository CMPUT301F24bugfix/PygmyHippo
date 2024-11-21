package com.example.pygmyhippo;

/*
Starting point of the app
Purposes:
    - Sets up the navbar for navigating between the three fragments (for any role)
    - Signs an account in based on device ID
Issues:
    - Double account creation when a new device tries to open the app.
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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

/**
 * Main Activity for our android app
 *
 * FIXME: Double account creation when a new device tries to open the app.
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
                    setupNavController();
                } else if (flags == DBOnCompleteFlags.NO_DOCUMENTS.value) {
                    dbHandler.addNewDevice(getDeviceID(), this);
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
        currentRole = Account.AccountRole.fromString(receivedRole);

        if (signedInAccount == null) {
            // Makes a new account in the database if there wasn't one matching the device ID
            Log.d("MainActivity", "Fetching account from DB based on DeviceID");
            dbHandler = new MainActivityDB();
            dbHandler.getDeviceAccount(getDeviceID(), this);
        } else {
            // TODO: Check integrity of signedInAccount with Firestore? (make sure fields match).
            //  ^ may break tests if firebase cannot be disabled.
            setupNavController();
        }
    }
}
