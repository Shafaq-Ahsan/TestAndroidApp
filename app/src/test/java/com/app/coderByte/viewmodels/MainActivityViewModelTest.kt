package com.app.coderByte.viewmodels

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class MainActivityViewModelTest {
    lateinit var mViewModel: MainActivityViewModel
    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()
    @Before
    fun setup() {
        mViewModel = MainActivityViewModel()
    }

    @Test
    fun getResponseData() {
    }

    @Test
    fun isLoaderVisible() {
        coroutinesTestRule.testDispatcher.runBlockingTest {
            Mockito.verify(mViewModel, Mockito.times(1)).toggleLoader(true);
        }
    }

    @Test
    fun getData() {
    }
}