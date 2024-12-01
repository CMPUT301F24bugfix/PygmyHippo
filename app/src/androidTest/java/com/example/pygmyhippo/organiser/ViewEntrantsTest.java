package com.example.pygmyhippo.organiser;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
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
import androidx.test.ext.junit.runners.AndroidJUnit4;
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
 * Testing Class for View Entrants Page
 * @author Ethan
 * @version 1.0
 */
@RunWith(JUnit4.class)
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
            navcontroller.navigate(R.id.view_entrants_fragment, navArgs);
        });
    }

    @Test
    public void testFragment() {
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

        for (String category : statusCategories) {
            onView(withId(R.id.o_entrant_list_spinner)).perform(click());
            onData(allOf(is(instanceOf(CharSequence.class)), is(category))).perform(click());
            onView(withText(category)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testEntrantPage() {
        try {
            Thread.sleep(2000);
        }
        catch (Exception e) {
            System.err.println(e);
        }

        onData(anything()).inAdapterView(withId(R.id.o_entrant_listview)).atPosition(0).perform(click());

        try {
            Thread.sleep(2000);
        }
        catch (Exception e) {
            System.err.println(e);
        }

        onView(withId(R.id.E_profile_profileImg)).check(matches(isDisplayed()));
        onView(withId(R.id.e_username)).check(matches(withText("test")));
        onView(withId(R.id.entrant_prounouns)).check(matches(withText("test")));
        onView(withId(R.id.entrant_email)).check(matches(withText("test")));
        onView(withId(R.id.entrant_phone)).check(matches(withText("1")));
    }
}

