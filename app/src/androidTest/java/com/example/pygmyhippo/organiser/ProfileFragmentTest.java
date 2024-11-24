package com.example.pygmyhippo.organiser;

/*
UI tests for the organiser profile fragment
Issues:
    - Needs spinner testing when navigation gets updated
 */

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.pygmyhippo.MainActivity;
import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
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
        intent.putExtra("currentRole", "organiser");

        Account account = new Account();
        account.setAccountID("0");
        account.setName("Testing account");
        account.setCurrentRole(Account.AccountRole.user);
        intent.putExtra("signedInAccount", account);

        return intent;
    }

    @Before
    public void setup() {
        scenario.getScenario().onActivity(activity -> {
            NavController navcontroller = Navigation.findNavController(activity, R.id.nav_host_fragment_activity_main);
            Bundle navArgs = new Bundle();
            Account account = new Account();
            account.setAccountID("0");
            account.setName("Testing account");
            account.setCurrentRole(Account.AccountRole.user);
            navArgs.putParcelable("signedInAccount", account);
            navArgs.putBoolean("useFirebase", false);
            navArgs.putBoolean("useNavigation", false);
            navcontroller.navigate(R.id.organiser_profile_page, navArgs);
        });
    }

    @Test
    public void testProfileDisplay() {
        onView(withText("My Profile")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("⭐Organiser")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Name:")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Pronouns:")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Email:")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Phone:")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Create Facility")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Testing account")).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testUpdateProfile() {
        // Click update button
        onView(withId(R.id.O_profile_editBtn)).perform(click());

        // Check if buttons were set visible
        onView(withId(R.id.O_profile_uploadImageBtn)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Delete Image")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Update")).check(ViewAssertions.matches(isDisplayed()));

        // Update name
        onView(withId(R.id.O_profile_textName)).perform(ViewActions.clearText());
        onView(withId(R.id.O_profile_textName)).perform(ViewActions.typeText("Bob Hilbert"));
        onView(withId(R.id.O_profile_textName)).perform(ViewActions.closeSoftKeyboard());
        onView(withText("Bob Hilbert")).check(ViewAssertions.matches(isDisplayed()));

        // Update pronouns
        onView(withId(R.id.O_profile_textPronouns)).perform(ViewActions.typeText("she/her"));
        onView(withId(R.id.O_profile_textPronouns)).perform(ViewActions.closeSoftKeyboard());
        onView(withText("she/her")).check(ViewAssertions.matches(isDisplayed()));

        // Update email
        onView(withId(R.id.O_profile_textEmail)).perform(ViewActions.typeText("bob@ualberta.ca"));
        onView(withId(R.id.O_profile_textEmail)).perform(ViewActions.closeSoftKeyboard());
        onView(withText("bob@ualberta.ca")).check(ViewAssertions.matches(isDisplayed()));

        // Update phone number
        onView(withId(R.id.O_profile_textPhone)).perform(ViewActions.typeText("780 666 1452"));
        onView(withId(R.id.O_profile_textPhone)).perform(ViewActions.closeSoftKeyboard());
        onView(withText("780 666 1452")).check(ViewAssertions.matches(isDisplayed()));

        // Click the update button
        onView(withText("Update")).perform(click());

        // Check if all the fields still match
        onView(withText("Bob Hilbert")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("she/her")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("bob@ualberta.ca")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("780 666 1452")).check(ViewAssertions.matches(isDisplayed()));

        // Check that create facility button comes back
        onView(withText("Create Facility")).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testCreateFacility() {
        // Click create facility button
        onView(withText("Create Facility")).perform(click());

        // Check if everything pops up
        onView(withText("Facility")).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.O_profile_facilityImg)).check(ViewAssertions.matches(isDisplayed()));

        // Scroll down
        onView(withId(R.id.O_Profile_facilityLocationText)).perform(scrollTo());

        // Continue checking
        onView(withText("Facility Name:")).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.O_Profile_facilityNameText)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Facility Location:")).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.O_Profile_facilityLocationText)).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testUpdateFacility() {
        // Click create facility button
        onView(withText("Create Facility")).perform(click());
        // Click update button
        onView(withId(R.id.O_profile_editBtn)).perform(click());

        // Scroll down and check if buttons become visible
        onView(withId(R.id.O_profile_updateBtn)).perform(scrollTo());
        onView(withId(R.id.O_Profile_facilityUploadImagebtn)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.O_profile_updateBtn)).check(ViewAssertions.matches(isDisplayed()));

        // Update facility name
        onView(withId(R.id.O_Profile_facilityNameText)).perform(ViewActions.typeText("University of Alberta"));
        onView(withId(R.id.O_Profile_facilityNameText)).perform(ViewActions.closeSoftKeyboard());
        onView(withText("University of Alberta")).check(ViewAssertions.matches(isDisplayed()));

        // Update facility location
        onView(withId(R.id.O_Profile_facilityLocationText)).perform(ViewActions.typeText("Whyte Ave"));
        onView(withId(R.id.O_Profile_facilityLocationText)).perform(ViewActions.closeSoftKeyboard());
        onView(withText("Whyte Ave")).check(ViewAssertions.matches(isDisplayed()));

        // Click update
        onView(withText("Update")).perform(click());

        // Scroll back down and check everything is updated
        onView(withId(R.id.O_Profile_facilityLocationText)).perform(scrollTo());
        onView(withId(R.id.O_profile_facilityImg)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("University of Alberta")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Whyte Ave")).check(ViewAssertions.matches(isDisplayed()));
    }
}
