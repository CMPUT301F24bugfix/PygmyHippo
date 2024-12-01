package com.example.pygmyhippo.organiser;

/*
UI tests for the organiser profile fragment
Issues:
    - Test spinner in other profile fragment with more than one role
 */

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
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
        intent.putExtra("currentRole", "organiser");

        Account account = new Account();
        account.setAccountID("TEST_ACCOUNT");
        account.setName("Testing account");
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
            account.setAccountID("TEST_ACCOUNT");
            account.setName("Testing account");
            account.setCurrentRole(Account.AccountRole.organiser);
            navArgs.putParcelable("signedInAccount", account);
            navArgs.putBoolean("useFirebase", false);
            navArgs.putBoolean("useNavigation", false);

            navcontroller.navigate(R.id.organiser_profile_page, navArgs);
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
        onView(withText("Create Facility")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Testing account")).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void bTestUpdateProfile() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Click update button
        onView(withId(R.id.O_profile_editBtn)).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

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
    public void cTestCreateFacility() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
    public void dTestUpdateFacility() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    @Test
    public void zTestClearFields() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Click edit button
        onView(withId(R.id.O_profile_editBtn)).perform(click());

        // Literally just using this to clear the test fields after the tests are run
        onView(withId(R.id.O_profile_textName)).perform(ViewActions.clearText());
        onView(withId(R.id.O_profile_textName)).perform(ViewActions.typeText("Testing account"));
        onView(withId(R.id.O_profile_textName)).perform(ViewActions.closeSoftKeyboard());

        onView(withId(R.id.O_profile_textPronouns)).perform(ViewActions.clearText());
        onView(withId(R.id.O_profile_textPronouns)).perform(ViewActions.closeSoftKeyboard());

        onView(withId(R.id.O_profile_textEmail)).perform(ViewActions.clearText());
        onView(withId(R.id.O_profile_textEmail)).perform(ViewActions.closeSoftKeyboard());

        onView(withId(R.id.O_profile_textPhone)).perform(ViewActions.clearText());
        onView(withId(R.id.O_profile_textPhone)).perform(ViewActions.closeSoftKeyboard());

        // Scroll down
        onView(withId(R.id.O_profile_updateBtn)).perform(scrollTo());

        // Clear facility
        onView(withId(R.id.O_Profile_facilityNameText)).perform(ViewActions.clearText());
        onView(withId(R.id.O_Profile_facilityNameText)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.O_Profile_facilityLocationText)).perform(ViewActions.clearText());
        onView(withId(R.id.O_Profile_facilityLocationText)).perform(ViewActions.closeSoftKeyboard());

        // Click update
        onView(withText("Update")).perform(click());
    }

    @Test
    public void testUserSpinner() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // The beginning account only has one role, so the spinner should not show up
        onView(withId(R.id.o_roleSpinner)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }
}
