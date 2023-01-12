package com.example.helpme;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class EditProfileTest {

    @Rule
    public ActivityScenarioRule<HomeActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(HomeActivity.class);

    @Test
    public void editProfileTest() {
        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.text_email_login),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_email_login_wrapper),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText.perform(click());

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.text_email_login),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_email_login_wrapper),
                                        0),
                                0),
                        isDisplayed()));

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.text_email_login), withText("uo257"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_email_login_wrapper),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText3.perform(click());

        ViewInteraction textInputEditText4 = onView(
                allOf(withId(R.id.text_email_login), withText("uo257"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_email_login_wrapper),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText4.perform(replaceText("uo257239@uniovi.es"));

        ViewInteraction textInputEditText5 = onView(
                allOf(withId(R.id.text_email_login), withText("uo257239@uniovi.es"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_email_login_wrapper),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText5.perform(closeSoftKeyboard());

        ViewInteraction textInputEditText6 = onView(
                allOf(withId(R.id.text_password_login),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_password_login_wrapper),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText6.perform(replaceText("123456"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.button_login_login), withText("Iniciar Sesi�n"),
                        childAtPosition(
                                allOf(withId(R.id.layout_login_form),
                                        childAtPosition(
                                                withId(R.id.layout_login_general),
                                                1)),
                                3),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.nav_cuenta),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.tv_user_name), withText("Francisco"),
                        withParent(withParent(withId(R.id.ll_user_name))),
                        isDisplayed()));
        editText.check(matches(withText("Francisco")));

        ViewInteraction checkBox = onView(
                allOf(withText("Comunicaci�n Persona-M�quina"),
                        childAtPosition(
                                allOf(withId(R.id.ll_dentroscroll),
                                        childAtPosition(
                                                allOf(withId(R.id.scrollView2), withContentDescription("Titulo")),
                                                0)),
                                0)));
        checkBox.perform(scrollTo(), click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.tv_user_name), withText("Francisco"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ll_user_name),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Francisco Coya"));

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.tv_user_name), withText("Francisco Coya"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.ll_user_name),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(closeSoftKeyboard());

        ViewInteraction constraintLayout = onView(
                allOf(withId(R.id.bt_guardar_perfil),
                        childAtPosition(
                                allOf(withId(R.id.constraintLayout),
                                        childAtPosition(
                                                withId(R.id.containerView),
                                                4)),
                                4),
                        isDisplayed()));
        constraintLayout.perform(click());

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.nav_home),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.nav_cuenta),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                3),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.tv_user_name), withText("Francisco Coya"),
                        withParent(withParent(withId(R.id.ll_user_name))),
                        isDisplayed()));
        editText2.check(matches(withText("Francisco Coya")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
