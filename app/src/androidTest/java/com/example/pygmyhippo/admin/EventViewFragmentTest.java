package com.example.pygmyhippo.admin;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
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
import androidx.test.espresso.contrib.RecyclerViewActions;
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

/**
 * Testing for admin single event view fragment.
 *
 * Currently only testing if the fragment appears.
 *
 * Issues:
 * - should be updated to Espresso Idle Resource instead of Thread.sleep()
 * ^^ i cant figure this out
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventViewFragmentTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<>(createIntent());

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    public static Intent createIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.setClassName("com.example.pygmyhippo", "com.example.pygmyhippo.MainActivity");
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("currentRole", "admin");

        Account account = new Account();
        account.setAccountID("0");
        account.setName("Testing account");
        account.setCurrentRole(Account.AccountRole.admin);
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
            navArgs.putBoolean("isAdmin", true);
            navArgs.putString("eventID", "TEST_EVENT"); // no event with this id exist therefor the default event is displayed
            navArgs.putBoolean("useFirebase", false);
            navArgs.putBoolean("useNavigation", false);
            navcontroller.navigate(R.id.admin_navigation_event_page, navArgs);

        });
    }

    @Test
    public void checkFragmentAppears() throws InterruptedException {
        Thread.sleep(20); // i know this is not best practice
        onView(withId(R.id.u_eventNameView)).check(matches(withText("Debug Event")));
    }

    @Test
    public void checkAdminButtonsAppear() throws InterruptedException{
        Thread.sleep(20); // i know this is not best practice
        onView(withId(R.id.a_deleteQRCodeButton)).check(matches(withText("Delete Hash QR Data")));
        onView(withId(R.id.a_deleteEventButton)).check(matches(withText("Delete Event")));
    }
}
