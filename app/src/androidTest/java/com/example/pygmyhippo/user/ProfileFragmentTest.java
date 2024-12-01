package com.example.pygmyhippo.user;

/*
UI tests for the user profile fragment
Issues:
    - Test spinner in other profile fragment with more than one role
    - To test a matching account with the ID must be present in the database
    - Should test geolocation permissions (look this up if time permits)
 */

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.pygmyhippo.MainActivity;
import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
public class ProfileFragmentTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(createIntent());

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    public static Intent createIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.setClassName("com.example.pygmyhippo", "com.example.pygmyhippo.MainActivity");
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("currentRole", "user");

        Account account = new Account();
        account.setAccountID("TEST_ACCOUNT_USER");
        account.setName("Testing user account");
        account.getRoles().add(Account.AccountRole.user);
        account.getRoles().add(Account.AccountRole.organiser);
        account.setCurrentRole(Account.AccountRole.organiser);
        intent.putExtra("signedInAccount", account);

        return intent;
    }

    @Before
    public void setup() {
        scenario.getScenario().onActivity(activity -> {
            NavController navcontroller = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
            Bundle navArgs = new Bundle();
            Account account = new Account();
            account.setAccountID("TEST_ACCOUNT_USER");
            account.setName("Testing user account");
            account.getRoles().add(Account.AccountRole.user);
            account.getRoles().add(Account.AccountRole.organiser);
            account.setCurrentRole(Account.AccountRole.organiser);
            navArgs.putParcelable("signedInAccount", account);
            navArgs.putBoolean("useFirebase", false);
            navArgs.putBoolean("useNavigation", false);

            navcontroller.navigate(R.id.u_profile_menu_item, navArgs);
        });
    }

    @Test
    public void aTestProfileDisplay() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withText("My Profile")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Name:")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Pronouns:")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Email:")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Phone:")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Allow Notifications")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Allow Geolocation")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Testing user account")).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void bTestUpdateProfile() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Click update button
        onView(withId(R.id.E_profile_editBtn)).perform(click());

        // Check if buttons were set visible
        onView(withId(R.id.E_profile_uploadImageBtn)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Delete Image")).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.E_profile_create)).perform(scrollTo());
        onView(withText("Update")).check(ViewAssertions.matches(isDisplayed()));

        // Update name
        onView(withId(R.id.E_profile_textName)).perform(ViewActions.clearText());
        onView(withId(R.id.E_profile_textName)).perform(ViewActions.typeText("Bob Hilbert"));
        onView(withId(R.id.E_profile_textName)).perform(ViewActions.closeSoftKeyboard());
        onView(withText("Bob Hilbert")).check(ViewAssertions.matches(isDisplayed()));

        // Update pronouns
        onView(withId(R.id.E_profile_textPronouns)).perform(ViewActions.typeText("she/her"));
        onView(withId(R.id.E_profile_textPronouns)).perform(ViewActions.closeSoftKeyboard());
        onView(withText("she/her")).check(ViewAssertions.matches(isDisplayed()));

        // Update email
        onView(withId(R.id.E_profile_textEmail)).perform(ViewActions.typeText("bob@ualberta.ca"));
        onView(withId(R.id.E_profile_textEmail)).perform(ViewActions.closeSoftKeyboard());
        onView(withText("bob@ualberta.ca")).check(ViewAssertions.matches(isDisplayed()));

        // Update phone number
        onView(withId(R.id.E_profile_textPhone)).perform(ViewActions.typeText("780 666 1452"));
        onView(withId(R.id.E_profile_textPhone)).perform(ViewActions.closeSoftKeyboard());
        onView(withText("780 666 1452")).check(ViewAssertions.matches(isDisplayed()));

        // Click on notifications
        onView(withId(R.id.E_profile_notification_dec)).perform(ViewActions.click());

        // Click the update button
        onView(withText("Update")).perform(click());

        // Check if all the fields still match
        onView(withText("Bob Hilbert")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("she/her")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("bob@ualberta.ca")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("780 666 1452")).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.E_profile_gps_dec)).check(ViewAssertions.matches(isNotChecked()));
        onView(withId(R.id.E_profile_notification_dec)).check(ViewAssertions.matches(isChecked()));
    }

    @Test
    public void testUserSpinner() {
        try {
            Thread.sleep(5500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // The beginning account has two roles so should show up
        onView(withId(R.id.u_roleSpinner)).check(matches(isDisplayed()));

        // Click on it and test navigation
        onView(withId(R.id.u_roleSpinner)).perform(click());
        onView(withText("organiser")).perform(click());

        // Wait and check if navigation works
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(withText("Event Details")).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void zTestClearFields() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Click edit button
        onView(withId(R.id.E_profile_editBtn)).perform(click());

        // Literally just using this to clear the test fields after the tests are run
        onView(withId(R.id.E_profile_textName)).perform(ViewActions.clearText());
        onView(withId(R.id.E_profile_textName)).perform(ViewActions.typeText("Testing user account"));
        onView(withId(R.id.E_profile_textName)).perform(ViewActions.closeSoftKeyboard());

        onView(withId(R.id.E_profile_textPronouns)).perform(ViewActions.clearText());
        onView(withId(R.id.E_profile_textPronouns)).perform(ViewActions.closeSoftKeyboard());

        onView(withId(R.id.E_profile_textEmail)).perform(ViewActions.clearText());
        onView(withId(R.id.E_profile_textEmail)).perform(ViewActions.closeSoftKeyboard());

        onView(withId(R.id.E_profile_textPhone)).perform(ViewActions.clearText());
        onView(withId(R.id.E_profile_textPhone)).perform(ViewActions.closeSoftKeyboard());

        onView(withId(R.id.E_profile_notification_dec)).perform(ViewActions.click());

        // Click update
        onView(withId(R.id.E_profile_create)).perform(scrollTo());
        onView(withText("Update")).perform(click());
    }
}
