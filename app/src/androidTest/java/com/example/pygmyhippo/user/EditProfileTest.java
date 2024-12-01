package com.example.pygmyhippo.user;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
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
public class EditProfileTest {

    @Test
    public void testEditProfile() {
        // Create a mock account with initial values
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

        // Verify the initial state using hints (fields are initially non-editable)
        onView(withId(R.id.E_profile_textName)).check(matches(withHint("Moo Deng"))); // Mock hint for Name
        onView(withId(R.id.E_profile_textEmail)).check(matches(withHint("moodeng@ualberta.ca"))); // Mock hint for Email
        onView(withId(R.id.E_profile_textPhone)).check(matches(withHint("123 456 7890"))); // Mock hint for Phone
        onView(withId(R.id.E_profile_textPronouns)).check(matches(withHint("she/they"))); // Mock hint for Pronouns
        onView(withId(R.id.E_profile_notification_dec)).check(matches(isChecked())); // Notifications enabled
        onView(withId(R.id.E_profile_gps_dec)).check(matches(isChecked())); // Geolocation enabled

        // Click the edit button to make fields editable
        onView(withId(R.id.E_profile_editBtn)).perform(click());

        // Perform edits on the fields
        onView(withId(R.id.E_profile_textName)).perform(clearText(), typeText("Avreet Kaur"));
        onView(withId(R.id.E_profile_textEmail)).perform(clearText(), typeText("avreet.kaur@gmail.com"));
        onView(withId(R.id.E_profile_textPhone)).perform(clearText(), typeText("9999999999"));
        onView(withId(R.id.E_profile_textPronouns)).perform(clearText(), typeText("she/her"));

        // Scroll to checkboxes and toggle them
        onView(withId(R.id.E_profile_notification_dec)).perform(ViewActions.scrollTo(), click()); // Enable notifications
        onView(withId(R.id.E_profile_gps_dec)).perform(ViewActions.scrollTo(), click()); // Disable geolocation

        // Click the update button to save changes
        onView(withId(R.id.E_profile_create)).perform(ViewActions.scrollTo(), click());

        // Verify updated profile details after save
        onView(withId(R.id.E_profile_textName)).check(matches(withText("Avreet Kaur")));
        onView(withId(R.id.E_profile_textEmail)).check(matches(withText("avreet.kaur@gmail.com")));
        onView(withId(R.id.E_profile_textPhone)).check(matches(withText("9999999999")));
        onView(withId(R.id.E_profile_textPronouns)).check(matches(withText("she/her")));
        onView(withId(R.id.E_profile_notification_dec)).check(matches(not(isChecked()))); // Notifications enabled
        onView(withId(R.id.E_profile_gps_dec)).check(matches(not(isChecked()))); // Geolocation disabled
    }
}