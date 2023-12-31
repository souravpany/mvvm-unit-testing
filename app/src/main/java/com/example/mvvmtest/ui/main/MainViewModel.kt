package com.example.mvvmtest.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmtest.data.models.ProductListItem
import com.example.mvvmtest.data.repositories.ProductRepository
import com.example.mvvmtest.di.IODispatchers
import com.example.mvvmtest.di.MainDispatcher
import com.example.mvvmtest.utils.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ProductRepository,
    @IODispatchers private val ioDispatchers: CoroutineDispatcher,
    @MainDispatcher private val mainDispatchers: CoroutineDispatcher
) : ViewModel() {

    var sortedProductList = ArrayList<ProductListItem>()


    private val _uiState = MutableStateFlow<ApiState<ArrayList<ProductListItem>>>(ApiState.Loading)

    val uiState: StateFlow<ApiState<ArrayList<ProductListItem>>> = _uiState

    init {
        getProduct()
    }

    private fun getProduct() {
        viewModelScope.launch(mainDispatchers) {
            _uiState.value = ApiState.Loading
            repository.getProduct()
                .flowOn(ioDispatchers)
                .catch { exp ->
                    _uiState.value = ApiState.Error(exp.toString())
                }.collect {
                    it.sortBy { data -> data.id }
                    sortedProductList = it
                    _uiState.value = ApiState.Success(sortedProductList)
                }

        }
    }
}