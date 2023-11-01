package com.example.mvvmtest.data.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ProductListItem(
    val image: String? = null,
    val price: Double? = null,
    val rating: Rating? = null,
    val description: String? = null,
    val id: Int? = null,
    val title: String? = null,
    val category: String? = null
) : Parcelable

@Parcelize
data class Rating(
    val rate: Double? = null,
    val count: Int? = null
) : Parcelable
