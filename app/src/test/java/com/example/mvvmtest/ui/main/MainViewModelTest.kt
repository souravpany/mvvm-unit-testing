package com.example.mvvmtest.ui.main

import app.cash.turbine.test
import com.example.mvvmtest.data.models.ProductListItem
import com.example.mvvmtest.data.repositories.ProductRepository
import com.example.mvvmtest.utils.ApiState
import com.example.mvvmtest.utils.DispatcherProvider
import com.example.mvvmtest.utils.TestDispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.*

import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    private lateinit var dispatcherProvider: DispatcherProvider

    @Mock
    lateinit var repo: ProductRepository

    @Before
    fun setUp() {
        dispatcherProvider = TestDispatcherProvider()
    }


    @Test
    fun givenServerResponse200_whenFetch_shouldReturnSuccess() {
        runTest {
            Mockito.doReturn(flowOf(emptyList<ProductListItem>())).`when`(repo).getProduct()
            val viewModel =
                MainViewModel(repository = repo, dispatcherProvider.io, dispatcherProvider.main)
            viewModel.uiState.test {
                assertEquals(ApiState.Success(emptyList<List<ProductListItem>>()), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            Mockito.verify(repo).getProduct()
        }
    }

    @Test
    fun givenServerResponseError_whenFetch_shouldReturnError() {
        runTest {
            val errorMessage = "Error Message For You"
            Mockito.doReturn(flow<List<ProductListItem>> {
                throw IllegalStateException(errorMessage)
            }).`when`(repo).getProduct()

            val viewModel =
                MainViewModel(repository = repo, dispatcherProvider.io, dispatcherProvider.main)
            viewModel.uiState.test {
                assertEquals(
                    ApiState.Error(IllegalStateException(errorMessage).toString()),
                    awaitItem()
                )
                cancelAndIgnoreRemainingEvents()
            }
            Mockito.verify(repo).getProduct()
        }
    }
}