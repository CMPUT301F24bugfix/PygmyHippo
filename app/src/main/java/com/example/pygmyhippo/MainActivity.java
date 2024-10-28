package com.example.pygmyhippo;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.os.StrictMode;

import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.OnRoleSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pygmyhippo.databinding.UserMainActivityNagivationBinding;
import com.example.pygmyhippo.databinding.OrganiserMainActivityNagivationBinding;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Main Activity for our android app
 * @author Jennifer, Griffin
 */
public class MainActivity extends AppCompatActivity implements OnRoleSelectedListener {
    final int PERMISSION_REQUEST_CODE =112;
    private OrganiserMainActivityNagivationBinding organiserBinder;
    private UserMainActivityNagivationBinding userBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Account currentAccount = new Account(
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
                Account.AccountRole.organiser,  // currentRole (TODO: Change this if you want to test with user)
                null  // facilityProfile
        );

        // Use a switch to determine the nagivation based on the current role
        switch (currentAccount.getCurrentRole()) {
            case user:
                userBinder = UserMainActivityNagivationBinding.inflate(getLayoutInflater());
                setContentView(userBinder.getRoot());
                setupNavControllerUser(userBinder.navView);
                break;
            case organiser:
                organiserBinder = OrganiserMainActivityNagivationBinding.inflate(getLayoutInflater());
                setContentView(organiserBinder.getRoot());
                setupNavControllerOrganiser(organiserBinder.navView);
                break;
            default:
                // needs a case for admin
                break;
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
     */
    private void setupNavControllerUser(BottomNavigationView navView) {
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                // TODO: change the navigation names for user to be more descriptive
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);
    }

    /**
     * This function does the initialization on the BottomNavigationView object passed
     * @author Griffin
     * @param navView: not sure
     */
    private void setupNavControllerOrganiser(BottomNavigationView navView) {
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.organiser_myEvents_page, R.id.organiser_postEvent_page, R.id.organiser_profile_page)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
