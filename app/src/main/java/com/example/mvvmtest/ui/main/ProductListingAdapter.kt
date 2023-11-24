package com.example.mvvmtest.ui.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmtest.R
import com.example.mvvmtest.data.models.ProductListItem
import com.example.mvvmtest.databinding.ProductItemBinding
import javax.inject.Inject

class ProductListingAdapter @Inject constructor() :
    RecyclerView.Adapter<ProductListingAdapter.ProductViewHolder>() {

    private var productList = ArrayList<ProductListItem>()

    fun updateAdapter(newProductLists: ArrayList<ProductListItem>) {

        val itemDiffCallback = ItemDiffCallback(oldList = productList, newList = newProductLists)
        val diffResult = DiffUtil.calculateDiff(itemDiffCallback)
        this.productList.clear()
        this.productList.addAll(newProductLists)
        diffResult.dispatchUpdatesTo(this)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProductItemBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val productItem = productList[position]
        holder.setProduct(productItem)
    }

    override fun getItemCount(): Int {
        return productList.size
    }


    inner class ProductViewHolder(private val binding: ProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun setProduct(productItem: ProductListItem) {
            binding.textViewTitle.text = truncateTextLength(productItem.title)
            binding.textViewCategory.text = productItem.category
            binding.textViewDescription.text = truncateTextLength(productItem.description)
            binding.textViewPrice.text = "Rs. ${productItem.price.toString()}"
            Glide.with(binding.imageViewAvatar.context)
                .load(productItem.image)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.imageViewAvatar)
        }

    }

    /**
     * limiting the product title length
     * */
    fun truncateTextLength(str: String?): String {
        return if (str?.length!! > 22) {
            str.substring(0, 20) + "..."
        } else {
            str
        }
    }


}