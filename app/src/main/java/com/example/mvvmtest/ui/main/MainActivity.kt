package com.example.mvvmtest.ui.main

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmtest.R
import com.example.mvvmtest.data.models.ProductListItem
import com.example.mvvmtest.databinding.ActivityMainBinding
import com.example.mvvmtest.utils.ApiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private var sortedProductList = ArrayList<ProductListItem>()

    @Inject
    lateinit var productListingAdapter: ProductListingAdapter

    private fun setupUI() {
        binding.rvProduct.layoutManager = LinearLayoutManager(this)
        binding.rvProduct.addItemDecoration(
            DividerItemDecoration(
                binding.rvProduct.context,
                (binding.rvProduct.layoutManager as LinearLayoutManager).orientation
            )
        )

        binding.rvProduct.adapter = productListingAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun renderList(product: List<ProductListItem>) {
        productListingAdapter.updateAdapter(product as ArrayList<ProductListItem>)
        productListingAdapter.notifyDataSetChanged()
    }


    @Suppress("UNCHECKED_CAST")
    private fun collectFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is ApiState.Error -> {
                            binding.pbProduct.visibility = View.GONE
                            Toast.makeText(
                                this@MainActivity,
                                uiState.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        ApiState.Loading -> {
                            binding.pbProduct.visibility = View.VISIBLE
                            binding.rvProduct.visibility = View.GONE
                        }
                        is ApiState.Success -> {
                            binding.pbProduct.visibility = View.GONE
                            binding.rvProduct.visibility = View.VISIBLE
                            uiState.data.sortedBy { it.id }
                            renderList(uiState.data)
                            sortedProductList = uiState.data as ArrayList<ProductListItem>
                        }
                    }

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupUI()
        collectFlows()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.product_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.title -> {
                sortedProductList.sortBy { it.title }
                renderList(sortedProductList)
                true
            }
            R.id.price -> {
                sortedProductList.sortBy { it.price }
                renderList(sortedProductList)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}