package com.example.pygmyhippo.organiser;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.example.pygmyhippo.MainActivity;
import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;
import com.example.pygmyhippo.organizer.ViewEntrantsFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Testing class for ViewEntrantsFragment
 * @author Kori
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewEntrantsTest {
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
            account.setAccountID("0");
            account.setName("Testing account");
            account.setCurrentRole(Account.AccountRole.organiser);
            navArgs.putParcelable("signedInAccount", account);
            navArgs.putString("eventID", "nYpVxrA8Aw0JhoGEr1T5");
            navcontroller.navigate(R.id.view_entrants_fragment, navArgs);
        });
    }

    @Test
    public void testDisplay() {
        onView(withText("Entrants")).check(ViewAssertions.matches(isDisplayed()));
        onView(withText("Filter by:")).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testEntrantSpinner() {
        try {
            Thread.sleep(2000);
        }
        catch (Exception e) {
            System.err.println(e);
        }

        // Click the spinner
        onView(withId(R.id.o_entrant_list_spinner)).perform(click());

        // Check if the drop down is displayed
        String waitlist = "WaitList";
        String invited = "Invited";
        String canceled = "Cancelled";
        String accepted = "Accepted";
        onView(withText(waitlist)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(invited)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(canceled)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(accepted)).check(ViewAssertions.matches(isDisplayed()));

        // Click on invited
        onView(withText(invited)).perform(click());
        // Check if invited is displayed on spinner
        onView(withText(invited)).check(ViewAssertions.matches(isDisplayed()));

        // Do the same for canceled, waitlist, and accepted
        // Click the spinner
        onView(withId(R.id.o_entrant_list_spinner)).perform(click());
        onView(withText(canceled)).perform(click());
        onView(withText(canceled)).check(ViewAssertions.matches(isDisplayed()));

        // For accepted
        onView(withId(R.id.o_entrant_list_spinner)).perform(click());
        onView(withText(accepted)).perform(click());
        onView(withText(accepted)).check(ViewAssertions.matches(isDisplayed()));

        // For Waitlist
        onView(withId(R.id.o_entrant_list_spinner)).perform(click());
        onView(withText(waitlist)).perform(click());
        onView(withText(waitlist)).check(ViewAssertions.matches(isDisplayed()));

        // There is one entrant in this event that is waitlisted, so check if a list entry shows now
        // Indirectly check by using waitlist status text
        onView(withText("Status: waitlisted")).check(ViewAssertions.matches(isDisplayed()));
    }

}
