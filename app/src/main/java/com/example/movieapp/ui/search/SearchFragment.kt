package com.example.movieapp.ui.search

import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.movie.ListMovieModel
import com.example.movieapp.ui.search.adapter.SearchMovieAdapter
import com.example.movieapp.utils.API_KEY
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SearchFragment : BaseFragment() {
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var searchMovieAdapter: SearchMovieAdapter
    private var listMovieModel = ListMovieModel()
    private lateinit var timer: Timer

    override fun getLayoutID(): Int {
        return R.layout.fragment_search
    }

    override fun doViewCreated() {
        searchMovie()
        observerViewModel()
        handleRcvWhenDelete()
    }

    private fun searchMovie() {
        frgSearch_imgSearch.setOnClickListener {
            when {
                frgSearch_etSearch.text.toString().isEmpty() -> Toast.makeText(
                    context,
                    getString(R.string.do_not_blank),
                    Toast.LENGTH_SHORT
                ).show()
                frgSearch_etSearch.text.toString().length < 3 -> Toast.makeText(
                    context,
                    getString(R.string.enter_more_than_3_character),
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                    viewModel.searchMovie(frgSearch_etSearch.text.toString(), API_KEY)
                    hideKeyboard()
                }
            }
        }
    }

    private fun handleRcvWhenDelete() {
        frgSearch_etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (listMovieModel.results.size != 0) {
                    timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            Handler(Looper.getMainLooper()).post {
                                if (s.toString().isNotEmpty()) {
                                    viewModel.searchMovie(frgSearch_etSearch.text.toString(), API_KEY)
                                    searchMovieAdapter.filterMovie(listMovieModel.results)
                                }
                            }
                        }

                    }, 10)
                }
                if (s.toString().isEmpty()) {
                    listMovieModel.results.clear()
                    listMovieModel.results.addAll(listMovieModel.results)
                    searchMovieAdapter.filterMovie(listMovieModel.results)
                }
            }
        })
    }

    private fun observerViewModel() {
        viewModel.resultSearch.observe(this@SearchFragment, {
            initRecyclerViewSearchMovie(it)
        })
    }

    private fun initRecyclerViewSearchMovie(listMoviePopularModel: ListMovieModel) {
        if (this.listMovieModel.results.size > 0) {
            this.listMovieModel.results.clear()
            this.listMovieModel.results.addAll(listMoviePopularModel.results)
            searchMovieAdapter.notifyDataSetChanged()
        } else {
            this.listMovieModel.results.addAll(listMoviePopularModel.results)
        }
        searchMovieAdapter =
            SearchMovieAdapter(this.listMovieModel.results.toList()) { index, _ ->
                Toast.makeText(
                    context,
                    listMoviePopularModel.results[index].title,
                    Toast.LENGTH_SHORT
                ).show()
            }
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        frgSearch_rcvResultSearch.setHasFixedSize(true)
        frgSearch_rcvResultSearch.layoutManager = linearLayoutManager
        frgSearch_rcvResultSearch.adapter = searchMovieAdapter
    }
}