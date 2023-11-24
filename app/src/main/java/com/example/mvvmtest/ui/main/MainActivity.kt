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
import com.example.mvvmtest.di.DefaultDispatchers
import com.example.mvvmtest.di.IODispatchers
import com.example.mvvmtest.di.MainDispatcher
import com.example.mvvmtest.utils.ApiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

const val SORT_BY_PRICE: String = "SORT_BY_PRICE"
const val SORT_BY_TITLE: String = "SORT_BY_TITLE"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    // for any back ground task - like network and disk-related work
    @IODispatchers
    @Inject
    lateinit var ioDispatchers: CoroutineDispatcher

    // It the UI thread of Android
    @MainDispatcher
    @Inject
    lateinit var mainDispatchers: CoroutineDispatcher

    // Is used to do CPU-intensive work
    @DefaultDispatchers
    @Inject
    lateinit var defaultDispatchers: CoroutineDispatcher

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

    private fun renderList(product: ArrayList<ProductListItem>) {
        productListingAdapter.updateAdapter(product)
        binding.rvProduct.layoutManager?.scrollToPosition(0)
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
                            renderList(uiState.data)
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

    private suspend fun sortByProduct(sortType: String) {
        withContext(mainDispatchers) {
            binding.pbProduct.visibility = View.VISIBLE
            binding.rvProduct.visibility = View.GONE
            withContext(defaultDispatchers) {
                when (sortType) {
                    SORT_BY_TITLE -> {
                        mainViewModel.sortedProductList.sortBy { data -> data.title }
                    }
                    SORT_BY_PRICE -> {
                        mainViewModel.sortedProductList.sortBy { data -> data.price }
                    }
                }
                delay(1000)
            }
            binding.pbProduct.visibility = View.GONE
            binding.rvProduct.visibility = View.VISIBLE
            renderList(mainViewModel.sortedProductList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.product_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.title -> {
                lifecycleScope.launch {
                    sortByProduct(SORT_BY_TITLE)
                }
                true
            }
            R.id.price -> {
                lifecycleScope.launch {
                    sortByProduct(SORT_BY_PRICE)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}