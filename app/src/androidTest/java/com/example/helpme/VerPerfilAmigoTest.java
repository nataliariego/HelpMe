package com.example.helpme;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

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
public class VerPerfilAmigoTest {

    @Rule
    public ActivityScenarioRule<HomeActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(HomeActivity.class);

    @Test
    public void verPerfilAmigoTest() {
        ViewInteraction editText = onView(
                allOf(withId(R.id.text_email_login), withText("Correo electr�nico"),
                        withParent(withParent(withId(R.id.text_email_login_wrapper))),
                        isDisplayed()));
        editText.check(matches(withText("Correo electr�nico")));

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
        textInputEditText2.perform(click());

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.text_email_login),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_email_login_wrapper),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText3.perform(click());

        ViewInteraction textInputEditText4 = onView(
                allOf(withId(R.id.text_email_login),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_email_login_wrapper),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText4.perform(click());

        ViewInteraction textInputEditText5 = onView(
                allOf(withId(R.id.text_email_login),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_email_login_wrapper),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText5.perform(click());

        ViewInteraction textInputEditText6 = onView(
                allOf(withId(R.id.text_email_login),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_email_login_wrapper),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText6.perform(click());

        ViewInteraction textInputEditText7 = onView(
                allOf(withId(R.id.text_email_login),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_email_login_wrapper),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText7.perform(replaceText("uo"), closeSoftKeyboard());

        ViewInteraction textInputEditText8 = onView(
                allOf(withId(R.id.text_email_login), withText("uo"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_email_login_wrapper),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText8.perform(click());

        ViewInteraction textInputEditText9 = onView(
                allOf(withId(R.id.text_email_login), withText("uo"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_email_login_wrapper),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText9.perform(replaceText("uo257239@uniovi.es"));

        ViewInteraction textInputEditText10 = onView(
                allOf(withId(R.id.text_email_login), withText("uo257239@uniovi.es"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_email_login_wrapper),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText10.perform(closeSoftKeyboard());

        ViewInteraction textInputEditText11 = onView(
                allOf(withId(R.id.text_password_login),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_password_login_wrapper),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText11.perform(replaceText("123456"), closeSoftKeyboard());

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

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.buttonVerAmigos), withText("Amigos"),
                        childAtPosition(
                                allOf(withId(R.id.containerView),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                5),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.buttonPerfil), withText("Ver perfil"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                3),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction circleImageView = onView(
                allOf(withId(R.id.img_persona_duda), withContentDescription("HelpMe"),
                        childAtPosition(
                                allOf(withId(R.id.containerView),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        circleImageView.perform(click());

        ViewInteraction constraintLayout = onView(
                allOf(withId(R.id.containerView),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        constraintLayout.perform(click());
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
