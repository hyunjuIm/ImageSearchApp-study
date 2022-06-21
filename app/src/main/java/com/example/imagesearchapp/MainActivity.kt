package com.example.imagesearchapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imagesearchapp.data.Repository
import com.example.imagesearchapp.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        bindViews()

        fetchRandomPhotos()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun initViews() = with(binding) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = PhotoAdapter()
        }
    }

    private fun bindViews() = with(binding) {
        searchEditText.setOnEditorActionListener { editeText, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentFocus?.let { view ->
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)

                    view.clearFocus()
                }

                fetchRandomPhotos(editeText.text.toString())
            }

            true
        }

        refreshLayout.setOnRefreshListener {
            fetchRandomPhotos(searchEditText.text.toString())
        }

    }

    private fun fetchRandomPhotos(query: String? = null) = scope.launch {
        Repository.getRandomPhotos(query)?.let { photos ->
            (binding.recyclerView.adapter as? PhotoAdapter)?.apply {
                this.photos = photos
                notifyDataSetChanged()
            }

            binding.refreshLayout.isRefreshing = false
        }
    }
}