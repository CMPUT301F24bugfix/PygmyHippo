package com.example.pygmyhippo.user;

/*
UI testing for joining an event
Author: Kori/Ethan
Issues:
    - Event used must be present in database
    - Should test geolocation
    - Consider testing to see if this updates in the event list
 */

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.espresso.assertion.ViewAssertions;
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
public class JoinEventTest {
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
            navArgs.putString("eventID", "nYpVxrA8Aw0JhoGEr1T5");
            navcontroller.navigate(R.id.u_eventFragment, navArgs);
        });
    }

    @Test
    public void aTestDisplay() {
        try {
            Thread.sleep(2000);
        }
        catch (Exception e) {
            System.err.println(e);
        }

        onView(withId(R.id.u_eventNameView)).check(matches(withText("Debug Event")));
        onView(withId(R.id.u_eventDateView)).check(matches(withText("25/11/2024")));
        onView(withId(R.id.u_eventCostView)).check(matches(withText("1")));
        onView(withId(R.id.u_eventLocationView)).check(matches(withText("1")));
        onView(withText("About This Event:")).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.u_aboutEventDescriptionView)).check(matches(withText("1")));
        onView(withText("Register")).check(ViewAssertions.matches(isDisplayed()));

    }

    @Test
    public void bTestJoinWaitlist() {
        try {
            Thread.sleep(2000);
        }
        catch (Exception e) {
            System.err.println(e);
        }

        onView(withText("Register")).perform(click());

        // Check that register button changes
        onView(withText("✔")).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void cTestLeaveWaitlist() {
        try {
            Thread.sleep(2000);
        }
        catch (Exception e) {
            System.err.println(e);
        }
        // Check that register is still checked (because should reflect from database)
        onView(withText("✔")).check(ViewAssertions.matches(isDisplayed()));

        onView(withText("✔")).perform(click());

        // Check that it goes back to register display
        onView(withText("Register")).check(ViewAssertions.matches(isDisplayed()));
    }
}
