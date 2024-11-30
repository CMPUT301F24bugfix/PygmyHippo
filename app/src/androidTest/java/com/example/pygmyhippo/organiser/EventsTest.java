package com.example.pygmyhippo.organiser;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.example.pygmyhippo.MainActivity;
import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Testing Class for Events Page
 * @author Ethan
 * @version 1.0
 */
@RunWith(JUnit4.class)
@LargeTest
public class EventsTest {
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
            navArgs.putString("eventID", "nYpVxrA8Aw0JhoGEr1T5");
            navcontroller.navigate(R.id.organiser_eventFragment, navArgs);
        });
    }

    @Test
    public void testFragment() {
        try {
            Thread.sleep(1000);
        }
        catch (Exception e) {
            System.err.println(e);
        }

        onView(withId(R.id.u_eventImageView)).check(matches(isDisplayed()));
    }

    @Test
    public void testQR() {
        try {
            Thread.sleep(1000);
        }
        catch (Exception e) {
            System.err.println(e);
        }

        onView(withId(R.id.o_button_view_QR)).perform(click());

        onView(withId(R.id.o_eventqr_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testEntrantList() {
        try {
            Thread.sleep(2000);
        }
        catch (Exception e) {
            System.err.println(e);
        }

        onView(withId(R.id.button_view_entrants)).perform(click());

        try {
            Thread.sleep(2000);
        }
        catch (Exception e) {
            System.err.println(e);
        }

        onView(withId(R.id.o_entrant_list_spinner)).check(matches(isDisplayed()));
    }

    @Test
    public void testEntrantSpinner() {
        final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        final String[] statusCategories = context.getResources().getStringArray(R.array.entrant_status);

        try {
            Thread.sleep(2000);
        }
        catch (Exception e) {
            System.err.println(e);
        }

        onView(withId(R.id.button_view_entrants)).perform(click());

        try {
            Thread.sleep(2000);
        }
        catch (Exception e) {
            System.err.println(e);
        }

        for (String category : statusCategories) {
            onView(withId(R.id.o_entrant_list_spinner)).perform(click());
            onData(allOf(is(instanceOf(CharSequence.class)), is(category))).perform(click());
            onView(withText(category)).check(ViewAssertions.matches(isDisplayed()));
        }
    }

    @Test
    public void testEdit() {
        try {
            Thread.sleep(2000);
        }
        catch (Exception e) {
            System.err.println(e);
        }

        onView(withId(R.id.u_edit_event_button)).perform(click());

        onView(withId(R.id.o_editEvent_name_edit)).check(matches(withText("Debug Event")));
        onView(withId(R.id.o_editEvent_dataTime_edit)).check(matches(withText("25/11/2024")));
        onView(withId(R.id.o_editEvent_price_edit)).check(matches(withText("1")));
        onView(withId(R.id.o_editEvent_location_edit)).check(matches(withText("1")));
        onView(withId(R.id.o_editEvent_description_edit)).check(matches(withText("1")));
    }
}
