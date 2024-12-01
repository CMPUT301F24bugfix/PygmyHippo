package com.example.pygmyhippo.user;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.pygmyhippo.MainActivity;
import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
public class UserNavigationWithIntentTest {

    @Test
    public void testUserFlowWithIntent() {
        // Step 1: Create a mock Account object
        Account mockAccount = new Account(
                "1",  // accountID
                "Avreet",  // name
                "She/Her",  // pronouns
                "9999999999",  // phoneNumber
                "test@gmail.com",  // emailAddress
                "mockDeviceID",  // deviceID
                "profilePic.png",  // profilePicture
                "Edmonton, Alberta",  // location
                true,  // receiveNotifications
                true,  // enableGeolocation
                new ArrayList<>(Arrays.asList(Account.AccountRole.user)),  // roles
                Account.AccountRole.user,  // currentRole
                null  // facilityProfile
        );

        // Step 2: Create an intent to launch MainActivity
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.putExtra("signedInAccount", mockAccount);
        intent.putExtra("currentRole", "user");

        // Step 3: Launch MainActivity with the intent
        ActivityScenario.launch(intent);

        // Step 4: Verify navigation to bottom navigation screen (My Events)
        // Step 4: Verify the QR Scanner screen is displayed
        onView(withId(R.id.u_QRScanner)) // Check the presence of QR Scanner UI element
                .check(matches(isDisplayed()));

        // Step 5: Navigate to "My Events" using the bottom navigation
        onView(withId(R.id.u_my_events_menu_item)) // Click on "My Events" button
                .perform(click());
        onView(withText("My Events")) // Verify the "My Events" screen is displayed
                .check(matches(isDisplayed()));

        // Step 6: Interact with the Profile tab
        onView(withId(R.id.u_profile_menu_item))
                .perform(click());
        onView(withText("My Profile"))
                .check(matches(isDisplayed()));

        // Step 7: Verify Profile details
        onView(withText("Avreet"))
                .check(matches(isDisplayed()));
        onView(withText("test@gmail.com"))
                .check(matches(isDisplayed()));
        onView(withText("9999999999"))
                .check(matches(isDisplayed()));


        // Step 9: Navigate back to "My Events"
        onView(withId(R.id.u_my_events_menu_item))
                .perform(click());
        onView(withText("My Events"))
                .check(matches(isDisplayed()));
    }
}
