package com.example.helpme;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.core.AllOf.allOf;

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
public class SendChatMessage {

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void sendChatMessage() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
        textInputEditText2.perform(replaceText("uo257239@uniovi.es"), closeSoftKeyboard());

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.text_password_login),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_password_login_wrapper),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText3.perform(replaceText("123456"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.button_login_login), withText("Iniciar Sesión"),
                        childAtPosition(
                                allOf(withId(R.id.layout_login_form),
                                        childAtPosition(
                                                withId(R.id.layout_login_general),
                                                1)),
                                3),
                        isDisplayed()));
        materialButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.nav_chat),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomNavigationView),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.recycler_listado_chats),
                        childAtPosition(
                                withId(R.id.layout_listar_chats_wrapper),
                                1)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.text_mensaje_enviar_chat),
                        childAtPosition(
                                allOf(withId(R.id.layout_chat),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.text_mensaje_enviar_chat),
                        childAtPosition(
                                allOf(withId(R.id.layout_chat),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.text_mensaje_enviar_chat),
                        childAtPosition(
                                allOf(withId(R.id.layout_chat),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("Mensaje de prueba"), closeSoftKeyboard());

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.button_enviar_mensaje_chat), withContentDescription("Enviar el mensaje"),
                        childAtPosition(
                                allOf(withId(R.id.layout_chat),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatImageButton.perform(click());
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
