package com.example.pygmyhippo.user;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.not;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {

    @Test
    public void testViewProfileDetails() {
        // Create a mock account to bypass the account creation dialog
        Account mockAccount = new Account(
                "1",  // accountID
                "Avreet",  // name
                "she/they",  // pronouns
                "7801234567",  // phoneNumber
                "test@gmail.com",  // emailAddress
                "1",  // deviceID
                "profilePic.png",  // profilePicture
                "Location",  // location
                true,  // receiveNotifications
                true,  // enableGeolocation
                new ArrayList<>(Arrays.asList(Account.AccountRole.user)),  // roles
                Account.AccountRole.user,  // currentRole
                null  // facilityProfile
        );
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.pygmyhippo", "com.example.pygmyhippo.MainActivity");
        intent.putExtra("signedInAccount", mockAccount);
        intent.putExtra("currentRole", "user");

        // Launch the MainActivity with the intent
        ActivityScenario.launch(intent);

        // Navigate to Profile tab
        onView(withId(R.id.u_profile_menu_item)).perform(click());

        // Verify initial profile details
        onView(withId(R.id.E_profile_textName)).check(matches(withText("Avreet")));
        onView(withId(R.id.E_profile_textEmail)).check(matches(withText("test@gmail.com")));
        onView(withId(R.id.E_profile_textPhone)).check(matches(withText("7801234567")));
        onView(withId(R.id.E_profile_textPronouns)).check(matches(withText("she/they")));
        onView(withId(R.id.E_profile_notification_dec)).check(matches(isChecked()));
        onView(withId(R.id.E_profile_gps_dec)).check(matches(isChecked()));

        // Verify profile picture is displayed
        onView(withId(R.id.E_profile_profileImg)).check(matches(isDisplayed()));
    }

    @Test
    public void testEditProfileDetails() {
        // Launch intent with mock account (as in previous test)
        Account mockAccount = new Account(
                "1", "Avreet", "she/they", "7801234567", "test@gmail.com", "1",
                "profilePic.png", "Location", true, true,
                new ArrayList<>(Arrays.asList(Account.AccountRole.user)),
                Account.AccountRole.user, null
        );
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.pygmyhippo", "com.example.pygmyhippo.MainActivity");
        intent.putExtra("signedInAccount", mockAccount);
        intent.putExtra("currentRole", "user");

        ActivityScenario.launch(intent);

        // Navigate to Profile tab
        onView(withId(R.id.u_profile_menu_item)).perform(click());

        // Click the edit button
        onView(withId(R.id.E_profile_editBtn)).perform(click());

        // Perform edits
        onView(withId(R.id.E_profile_textName)).perform(clearText(), typeText("Avreet Kaur"));
        onView(withId(R.id.E_profile_textEmail)).perform(clearText(), typeText("avreet.kaur@gmail.com"));
        onView(withId(R.id.E_profile_textPhone)).perform(clearText(), typeText("9999999999"));
        onView(withId(R.id.E_profile_textPronouns)).perform(clearText(), typeText("she/her"));

        // Scroll to checkboxes and toggle them
        onView(withId(R.id.E_profile_notification_dec)).perform(ViewActions.scrollTo(), click());
        onView(withId(R.id.E_profile_gps_dec)).perform(ViewActions.scrollTo(), click());

        // Click the update button
        onView(withId(R.id.E_profile_create)).perform(ViewActions.scrollTo(), click());

        // Verify updated profile details
        onView(withId(R.id.E_profile_textName)).check(matches(withText("Avreet Kaur")));
        onView(withId(R.id.E_profile_textEmail)).check(matches(withText("avreet.kaur@gmail.com")));
        onView(withId(R.id.E_profile_textPhone)).check(matches(withText("9999999999")));
        onView(withId(R.id.E_profile_textPronouns)).check(matches(withText("she/her")));
        onView(withId(R.id.E_profile_notification_dec)).check(matches(not(isChecked())));
        onView(withId(R.id.E_profile_gps_dec)).check(matches(not(isChecked())));
    }

    @Test
    public void testEditProfilePicture() {
        // Launch intent with mock account
        Account mockAccount = new Account(
                "1", "Avreet", "she/they", "7801234567", "test@gmail.com", "1",
                "profilePic.png", "Location", true, true,
                new ArrayList<>(Arrays.asList(Account.AccountRole.user)),
                Account.AccountRole.user, null
        );
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.pygmyhippo", "com.example.pygmyhippo.MainActivity");
        intent.putExtra("signedInAccount", mockAccount);
        intent.putExtra("currentRole", "user");

        ActivityScenario.launch(intent);

        // Navigate to Profile tab
        onView(withId(R.id.u_profile_menu_item)).perform(click());

        // Click the edit button
        onView(withId(R.id.E_profile_editBtn)).perform(click());

        // Delete the profile picture
        onView(withId(R.id.E_profile_deleteAvatarbtn)).perform(click());

        // Verify profile picture is reset to default
        onView(withId(R.id.E_profile_profileImg)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackNavigationFromProfileFragment() {
        // Launch intent with mock account
        Account mockAccount = new Account(
                "1", "Avreet", "she/they", "7801234567", "test@gmail.com", "1",
                "profilePic.png", "Location", true, true,
                new ArrayList<>(Arrays.asList(Account.AccountRole.user)),
                Account.AccountRole.user, null
        );
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.pygmyhippo", "com.example.pygmyhippo.MainActivity");
        intent.putExtra("signedInAccount", mockAccount);
        intent.putExtra("currentRole", "user");

        ActivityScenario.launch(intent);

        // Navigate to Profile tab
        onView(withId(R.id.u_profile_menu_item)).perform(click());

        // Press the back button and verify navigation
        onView(withContentDescription("Navigate up")).perform(click());
        onView(withId(R.id.nav_view)).check(matches(isDisplayed()));
    }
}