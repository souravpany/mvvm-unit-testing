package com.example.mvvmtest.ui.main

import androidx.recyclerview.widget.DiffUtil
import com.example.mvvmtest.data.models.ProductListItem

class ItemDiffCallback(
    private val oldList: ArrayList<ProductListItem>,
    private val newList: ArrayList<ProductListItem>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}