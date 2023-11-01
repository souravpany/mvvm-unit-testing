package com.example.mvvmtest.data.repositories

import com.example.mvvmtest.data.models.ProductListItem
import com.example.mvvmtest.data.repositoriesImpl.ProductApiImpl
import com.example.mvvmtest.di.IODispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productApiImpl: ProductApiImpl,
    @IODispatchers private val ioDispatchers: CoroutineDispatcher
) {

    /**
     * Get all product from Api
     */
    fun getProduct(): Flow<List<ProductListItem>> {
        return flow {
            emit(
                productApiImpl.getProduct()
            )
        }.flowOn(ioDispatchers)
    }
}