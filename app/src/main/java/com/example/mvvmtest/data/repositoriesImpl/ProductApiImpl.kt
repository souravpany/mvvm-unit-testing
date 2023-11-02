package com.example.mvvmtest.data.repositoriesImpl

import com.example.mvvmtest.data.models.ProductListItem
import com.example.mvvmtest.network.ApiService
import javax.inject.Inject

class ProductApiImpl @Inject constructor(
    private val apiService: ApiService
) {

    /**
     * Api call to get all product
     */
    suspend fun getProduct(): ArrayList<ProductListItem> {
        return apiService.getProducts()
    }
}