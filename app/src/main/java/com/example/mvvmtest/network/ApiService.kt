package com.example.mvvmtest.network

import com.example.mvvmtest.data.models.ProductListItem
import retrofit2.http.GET

interface ApiService {

    @GET(EndPoints.PRODUCTS)
    suspend fun getProducts(): ArrayList<ProductListItem>

}