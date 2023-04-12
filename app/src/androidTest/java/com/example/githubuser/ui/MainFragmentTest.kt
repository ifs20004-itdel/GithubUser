package com.example.githubuser.ui

import org.junit.Assert.*

import androidx.fragment.app.testing.FragmentScenario
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.githubuser.R

@RunWith(AndroidJUnit4ClassRunner::class)
class MainFragmentTest {

    @Before
    fun setup() {
        FragmentScenario.launchInContainer(MainFragment::class.java)
    }

    @Test
    fun asserShowFavoriteUser() {
        onView(withId(R.id.rvUsers)).check(matches(isDisplayed()))
    }

}