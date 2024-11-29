package com.example.pygmyhippo.user;

/*
UI Testing for the create user alert dialog
These tests check that the alert dialog only closes when the required fields are set
Issues:
    - Testing can only be done with an emulator whose device ID is not in the database
    - Test data is deleted by name, so could potentially delete an account with the same name
            Tried to handle this by only deleting if one account with such name exists
 */

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.pygmyhippo.MainActivity;
import com.example.pygmyhippo.R;
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
public class NewUserTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(createIntent());

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    public static Intent createIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.setClassName("com.example.pygmyhippo", "com.example.pygmyhippo.MainActivity");
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        return intent;
    }

    @Before
    public void setup() {
        scenario.getScenario().onActivity(activity -> {
            Bundle navArgs = new Bundle();
            navArgs.putBoolean("useFirebase", true);
            navArgs.putBoolean("useNavigation", false);
        });
    }

    @Test
    public void testDisplay() {
        onView(withId(R.id.new_name_txt))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_email_txt))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_phone_txt))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Select Permanent Role(s):"))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_role_user))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_role_organiser))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testNoFields() {
        onView(withText("Create"))
                .inRoot(isDialog())
                .perform(click());

        // Check that it didn't go through by seeing that the dialog box is still visible
        onView(withId(R.id.new_name_txt))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_email_txt))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_phone_txt))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Select Permanent Role(s):"))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_role_user))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_role_organiser))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testNameFields() {
        // Add name but no email, shouldn't go through
        // Update name
        onView(withId(R.id.new_name_txt)).inRoot(isDialog()).perform(ViewActions.typeText("Bobby Hill"));
        onView(withId(R.id.new_name_txt)).inRoot(isDialog()).perform(ViewActions.closeSoftKeyboard());
        onView(withText("Bobby Hill")).inRoot(isDialog()).check(ViewAssertions.matches(isDisplayed()));

        onView(withText("Create"))
                .inRoot(isDialog())
                .perform(click());

        // Check that it didn't go through by seeing that the dialog box is still visible
        onView(withId(R.id.new_name_txt))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_email_txt))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_phone_txt))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Select Permanent Role(s):"))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_role_user))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_role_organiser))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testEmailFields() {
        // Add email but no name, shouldn't go through
        // Update email
        onView(withId(R.id.new_email_txt)).inRoot(isDialog()).perform(ViewActions.typeText("bobby@yahoo.ca"));
        onView(withId(R.id.new_email_txt)).inRoot(isDialog()).perform(ViewActions.closeSoftKeyboard());
        onView(withText("bobby@yahoo.ca")).inRoot(isDialog()).check(ViewAssertions.matches(isDisplayed()));

        onView(withText("Create"))
                .inRoot(isDialog())
                .perform(click());

        // Check that it didn't go through by seeing that the dialog box is still visible
        onView(withId(R.id.new_name_txt))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_email_txt))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_phone_txt))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Select Permanent Role(s):"))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_role_user))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_role_organiser))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testNoRoles() {
        // Add email and name and phone, but no roles: It shouldn't go through
        // Update name
        onView(withId(R.id.new_name_txt)).inRoot(isDialog()).perform(ViewActions.typeText("Bobby Hill"));
        onView(withId(R.id.new_name_txt)).inRoot(isDialog()).perform(ViewActions.closeSoftKeyboard());
        onView(withText("Bobby Hill")).inRoot(isDialog()).check(ViewAssertions.matches(isDisplayed()));

        // Update email
        onView(withId(R.id.new_email_txt)).inRoot(isDialog()).perform(ViewActions.typeText("bobby@yahoo.ca"));
        onView(withId(R.id.new_email_txt)).inRoot(isDialog()).perform(ViewActions.closeSoftKeyboard());
        onView(withText("bobby@yahoo.ca")).inRoot(isDialog()).check(ViewAssertions.matches(isDisplayed()));

        // Update phone
        onView(withId(R.id.new_phone_txt)).inRoot(isDialog()).perform(ViewActions.typeText("780 111 2222"));
        onView(withId(R.id.new_phone_txt)).inRoot(isDialog()).perform(ViewActions.closeSoftKeyboard());
        onView(withText("780 111 2222")).inRoot(isDialog()).check(ViewAssertions.matches(isDisplayed()));


        onView(withText("Create"))
                .inRoot(isDialog())
                .perform(click());

        // Check that it didn't go through by seeing that the dialog box is still visible
        onView(withId(R.id.new_name_txt))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_email_txt))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_phone_txt))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Select Permanent Role(s):"))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_role_user))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.new_role_organiser))
                .inRoot(isDialog())
                .check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void ZTestCompleteCreation() throws InterruptedException {
        // Add email and name and phone, and a role, it should go through
        // Update name
        onView(withId(R.id.new_name_txt)).inRoot(isDialog()).perform(ViewActions.typeText("This is hopefully a very specific test name"));
        onView(withId(R.id.new_name_txt)).inRoot(isDialog()).perform(ViewActions.closeSoftKeyboard());
        onView(withText("This is hopefully a very specific test name")).inRoot(isDialog()).check(ViewAssertions.matches(isDisplayed()));

        // Update email
        onView(withId(R.id.new_email_txt)).inRoot(isDialog()).perform(ViewActions.typeText("bobby@yahoo.ca"));
        onView(withId(R.id.new_email_txt)).inRoot(isDialog()).perform(ViewActions.closeSoftKeyboard());
        onView(withText("bobby@yahoo.ca")).inRoot(isDialog()).check(ViewAssertions.matches(isDisplayed()));

        // Update phone
        onView(withId(R.id.new_phone_txt)).inRoot(isDialog()).perform(ViewActions.typeText("780 111 2222"));
        onView(withId(R.id.new_phone_txt)).inRoot(isDialog()).perform(ViewActions.closeSoftKeyboard());
        onView(withText("780 111 2222")).inRoot(isDialog()).check(ViewAssertions.matches(isDisplayed()));

        // Pick organiser role
        // Update phone
        onView(withId(R.id.new_role_organiser)).inRoot(isDialog()).perform(click());
        onView(withId(R.id.new_role_organiser)).inRoot(isDialog()).check(ViewAssertions.matches(isChecked()));

        onView(withText("Create"))
                .inRoot(isDialog())
                .perform(click());

        // Check that it did go through and text from the post event fragment is visible
        // Meaning it navigated to the right role
        Thread.sleep(5500);     // Wait for the layout to change
        onView(withText("Event Details")).check(ViewAssertions.matches(isDisplayed()));
    }

    @After
    public void cleanUp() {
        // Remove the account that got added by the class
        scenario.getScenario().onActivity(activity -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Find the account with the name in the database
            db.collection("Accounts")
                    .whereEqualTo("name", "This is hopefully a very specific test name")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot queryResult = task.getResult();

                                // Add results to a list, this gets passed to the listener
                                if (queryResult.size() == 1) {
                                    DocumentSnapshot doc = queryResult.getDocuments().get(0);
                                    db.collection("Accounts").document(doc.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("UI Testing", "Cleaned up test data");
                                            }
                                        }
                                    });
                                }

                                // Only delete the data if there is one reference. Or else we risk deleting actual data
                            }
                        }
                    });
        });
    }
}
