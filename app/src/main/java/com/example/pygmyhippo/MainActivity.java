package com.example.pygmyhippo;

import android.os.Bundle;
import android.os.StrictMode;

import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.common.OnRoleSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
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
                new ArrayList<>(Arrays.asList(Account.AccountRole.user, Account.AccountRole.organizer)),  // roles
                Account.AccountRole.user,  // currentRole (TODO: Change this if you want to test with user)
                null  // facilityProfile
        );

        // Use a switch to determine the nagivation based on the current role
        switch (currentAccount.getCurrentRole()) {
            case user:
                userBinder = UserMainActivityNagivationBinding.inflate(getLayoutInflater());
                setContentView(userBinder.getRoot());
                setupNavControllerUser(userBinder.navView);
                break;
            case organizer:
                organiserBinder = OrganiserMainActivityNagivationBinding.inflate(getLayoutInflater());
                setContentView(organiserBinder.getRoot());
                setupNavControllerOrganiser(organiserBinder.navView);
                break;
            default:
                // needs a case for admin
                break;
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
                R.id.organiser_myEvents_page, R.id.organiser_calander_page, R.id.organiser_profile_page)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
