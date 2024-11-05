package com.example.pygmyhippo;

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
import com.example.pygmyhippo.common.OnRoleSelectedListener;
import com.example.pygmyhippo.database.DBOnCompleteFlags;
import com.example.pygmyhippo.database.DBOnCompleteListener;
import com.example.pygmyhippo.databinding.AdminMainActivityNavigationBinding;
import com.example.pygmyhippo.databinding.OrganiserMainActivityNagivationBinding;
import com.example.pygmyhippo.databinding.UserMainActivityNagivationBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Main Activity for our android app
 *
 * FIXME: Double account creation when a new device tries to open the app.
 * @author Jennifer, Griffin
 */
public class MainActivity extends AppCompatActivity implements OnRoleSelectedListener, DBOnCompleteListener<Account> {
    final int PERMISSION_REQUEST_CODE =112;
    private OrganiserMainActivityNagivationBinding organiserBinder;
    private UserMainActivityNagivationBinding userBinder;
    private AdminMainActivityNavigationBinding adminBinding;
    private MainActivityDB dbHandler;
    private Account signedInAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean useDB = true; // Change to enable/disable DB use.

        if (useDB) {
            dbHandler = new MainActivityDB();
            dbHandler.getDeviceAccount(getDeviceID(), this);
        } else {
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

            setNavigation(signedInAccount);
        }

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
     * This is the call back function that listens for when the role is changed on the profile page.
     * Right now it is commented out because the navigation doesnt like to be switched.
     * TODO
     *  - need to check if the account has this permissions before changing
     *  - need to update the account with the new current role
     * @author Griffin
     * @param role: this is a string this is sent from the drop down for switching the roles
     */
    @Override
    public void onRoleSelected(String role) {
//        switch (role) {
//            case "User":
//                if (organiserBinder != null) {
//                    organiserBinder = null;
//                }
//                if (userBinding == null) {
//                    userBinding = UserMainActivityNagivationBinding.inflate(getLayoutInflater());
//                }
//                setContentView(userBinding.getRoot());  // Set layout for user
//                // Initialize NavController for user view
//                setupNavController(userBinding.navView);
//                break;
//            case "Organizer":
//                if (userBinding != null) {
//                    userBinding = null;
//                }
//                if (organiserBinder == null) {
//                    organiserBinder = OrganiserMainActivityNagivationBinding.inflate(getLayoutInflater());
//                }
//                setContentView(organiserBinder.getRoot());  // Set layout for organizer
//                // Initialize NavController for organizer view
//                setupNavController(organiserBinder.navView);
//                break;
//            default:
//                break;
//        }
    }

    /**
     * This function does the initialization on the BottomNavigationView object passed
     * @author Griffin
     * @param navView: not sure
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
        userBinder.navView.setOnItemSelectedListener(item -> {
            Log.d("Menu", String.format("Menu item (%s) selected", item.getTitle()));
            if (item.getItemId() == R.id.u_profile_menu_item) {
                Log.d("Menu", "Sending account to profile");
                navController.navigate(R.id.u_profile_menu_item, bundle);
            } else if (item.getItemId() == R.id.u_scanQR_menu_item) {
                navController.navigate(R.id.u_scanQR_menu_item, bundle);
            } else if (item.getItemId() == R.id.u_my_events_menu_item) {
                navController.navigate(R.id.u_my_events_menu_item, bundle);
            }
            return true;
        });
    }

    /**
     * This function does the initialization on the BottomNavigationView object passed
     * @author Griffin
     * @param navView: not sure
     * @param bundle: Arguments to pass to first fragment.
     */
    private void setupNavControllerOrganiser(BottomNavigationView navView, Bundle bundle) {
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.organiser_myEvents_page, R.id.organiser_postEvent_page, R.id.organiser_profile_page)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navController.setGraph(R.navigation.organiser_mobile_navigation, bundle);
        NavigationUI.setupWithNavController(navView, navController);
    }


    private void setupNavControllerAdmin(BottomNavigationView navView, Bundle bundle) {
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.admin_all_events, R.id.admin_all_images, R.id.admin_all_profiles)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        navController.setGraph(R.navigation.admin_mobile_navigation, bundle);
        NavigationUI.setupWithNavController(navView, navController);
    }

    /**
     * Callback called when DB queries complete.
     * @param docs - Documents retrieved from DB (if it was a get query).
     * @param queryID - ID of query completed.
     * @param flags - Flags to indicate query status/set how to process query result.
     */
    @Override
    public void OnComplete(@NonNull ArrayList<Account> docs, int queryID, int flags) {
        switch (queryID) {
            case 0: // getDeviceAccount()
                if (flags == DBOnCompleteFlags.SINGLE_DOCUMENT.value) {
                    signedInAccount = docs.get(0);
                    Toast toast = Toast.makeText(this,
                            String.format("Got account (%s)", signedInAccount.getAccountID()),
                            Toast.LENGTH_SHORT);
                    toast.show();
                    setNavigation(signedInAccount);
                } else if (flags == DBOnCompleteFlags.NO_DOCUMENTS.value) {
                    dbHandler.addNewDevice(getDeviceID(), this);
                } else {
                    handleDBError();
                }
                break;
            case 1: // addNewDevice
                if (flags == DBOnCompleteFlags.SUCCESS.value) {
                    Toast toast = Toast.makeText(this,
                            "Made new account for device",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    signedInAccount = docs.get(0);
                    setNavigation(signedInAccount);
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
     * @param account Account currently signed in.
     */
    private void setNavigation(Account account) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("account", account);

        // Use a switch to determine the nagivation based on the current role
        switch (account.getCurrentRole()) {
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
                Log.d("MainActivity", String.format("Unknown role (%s)", account.getCurrentRole().value));
                break;
        }
    }
}
