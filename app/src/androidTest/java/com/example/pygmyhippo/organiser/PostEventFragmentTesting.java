package com.example.pygmyhippo.organiser;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.TypeTextAction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.pygmyhippo.MainActivity;
import com.example.pygmyhippo.R;
import com.example.pygmyhippo.common.Account;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Testing class for my postEventFragment
 * TODO:
 *  - More verbose testing
 *  - Test if added to database
 *  - could read the toast (although using the toast should only be a temporary solution)
 *  - once database is connected check that qr code links
 * @author Griffin, Ethan
 * @version 1.2
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PostEventFragmentTesting {
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
            navArgs.putBoolean("useFirebase", true);
            navArgs.putBoolean("useNavigation", false);
            navcontroller.navigate(R.id.organiser_postEvent_page, navArgs);
        });
    }

//    @Before
//    public void registerIdlingResource() {
//        scenario.getScenario().onActivity(new ActivityScenario.ActivityAction<MainActivity>() {
//            @Override
//            public void perform(MainActivity activity) {
//                eventIdlingResource = new EventIdlingResource(activity);
//                IdlingRegistry.getInstance().register(eventIdlingResource);
//            }
//        });
//    }

    @Test
    public void testCreateFragment() {
        // Test if we are taken to the correct fragment
        onView(withId(R.id.organiser_postEvent_page)).check(matches(isDisplayed()));
    }

    @Test
    public void testNoData() {
        // Emulator animations need to be disabled to run this
        onView(withId(R.id.o_postEvent_bottom_scroll)).perform(scrollTo());
        onView(withId(R.id.o_postEvent_post_button)).perform(click());

        // Espresso doesn't test for Toasts that get sent but we can test to see if the fragment
        // doesn't change to confirm that the event post did not work
        onView(withId(R.id.organiser_postEvent_page)).check(matches(isDisplayed()));
    }

    @Test
    public void testWithData() {
        onView(withId(R.id.o_postEvent_addImage)).check(matches(isDisplayed()));

        String testName = "Test Event";
        onView(withId(R.id.o_postEvent_name_edit)).perform(typeText(testName));

        int year = 2025;
        int month = 1;
        int day = 30;
        onView(withId(R.id.o_postEvent_dataTime_edit)).perform(click());

        // Check if the DatePicker is displayed
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).check(matches(isDisplayed()));
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(year, month, day));
        onView(withId(android.R.id.button1)).perform(click());

        String testPrice = "FREE";
        onView(withId(R.id.o_postEvent_price_edit)).perform(typeText(testPrice));

        String testLocation = "123 Park Street";
        onView(withId(R.id.o_postEvent_location_edit)).perform(typeText(testLocation));

        // Need to scroll so it is visible on the UI
        onView(withId(R.id.o_postEvent_bottom_scroll)).perform(scrollTo());

        String testDesc = "Test";
        onView(withId(R.id.o_postEvent_description_edit)).perform(typeText(testDesc));

        String testEntLim = "10";
        onView(withId(R.id.o_postEvent_limit_edit)).perform(typeText(testEntLim));

        String testWin = "5";
        onView(withId(R.id.o_postEvent_winners_edit)).perform(typeText(testWin));

        onView(withId(R.id.o_postEvent_geolocation_check)).perform(click());

        // Test if editTexts are properly saved
        onView(withId(R.id.o_postEvent_name_edit)).check(matches(withText(testName)));
        onView(withId(R.id.o_postEvent_dataTime_edit)).check(matches(withText(day + "/" + month + "/" + year)));
        onView(withId(R.id.o_postEvent_price_edit)).check(matches(withText(testPrice)));
        onView(withId(R.id.o_postEvent_location_edit)).check(matches(withText(testLocation)));
        onView(withId(R.id.o_postEvent_description_edit)).check(matches(withText(testDesc)));
        onView(withId(R.id.o_postEvent_limit_edit)).check(matches(withText(testEntLim)));
        onView(withId(R.id.o_postEvent_winners_edit)).check(matches(withText(testWin)));
        onView(withId(R.id.o_postEvent_geolocation_check)).check(matches(isChecked()));

        // Click Post Button
        onView(withId(R.id.o_postEvent_post_button)).perform(click());

        // Check if fragment switched
        // Commented because this doesn't work right now.
        // onView(withId(R.id.view_eventqr_fragment)).check(matches(isDisplayed()));
    }
}
