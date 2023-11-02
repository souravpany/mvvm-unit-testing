package com.example.mvvmtest.data.repositories
import com.example.mvvmtest.data.models.ProductListItem
import com.example.mvvmtest.data.repositoriesImpl.ProductApiImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productApiImpl: ProductApiImpl
) {

    /**
     * Get all product from Api
     */
    fun getProduct(): Flow<ArrayList<ProductListItem>> = flow { emit(productApiImpl.getProduct()) }
}