package com.example.movieapp.ui.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.movie.ListMovieModel
import com.example.movieapp.ui.detail.movie.DetailMovieFragment
import com.example.movieapp.ui.search.adapter.SearchMovieAdapter
import com.example.movieapp.utils.API_KEY
import com.example.movieapp.utils.ID_MOVIE
import com.example.movieapp.utils.LOADING_LENGTH
import kotlinx.android.synthetic.main.fragment_search_movie.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SearchMovieFragment : BaseFragment() {
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var searchMovieAdapter: SearchMovieAdapter
    private var listMovieModel = ListMovieModel()
    private lateinit var timer: Timer

    override fun getLayoutID(): Int {
        return R.layout.fragment_search_movie
    }

    override fun doViewCreated() {
        searchMovie()
        observerViewModel()
        handleRcvWhenDelete()
    }

    private fun searchMovie() {
        frgSearchMovie_imgSearch.setOnClickListener {
            when {
                frgSearchMovie_etSearch.text.toString().isEmpty() -> Toast.makeText(
                    context,
                    getString(R.string.do_not_blank),
                    Toast.LENGTH_SHORT
                ).show()
                frgSearchMovie_etSearch.text.toString().length < 3 -> Toast.makeText(
                    context,
                    getString(R.string.enter_more_than_3_character),
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
//                    Handler().postDelayed({
                        showLoading()
//                    }, LOADING_LENGTH)
                    viewModel.searchMovie(frgSearchMovie_etSearch.text.toString(), API_KEY)
                    hideKeyboard()
                }
            }
        }
    }

    private fun handleRcvWhenDelete() {
        frgSearchMovie_etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (listMovieModel.results.size != 0) {
                    timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            Handler(Looper.getMainLooper()).post {
                                if (s.toString().isNotEmpty()) {
                                    viewModel.searchMovie(frgSearchMovie_etSearch.text.toString(), API_KEY)
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
        viewModel.resultSearchMovie.observe(this@SearchMovieFragment, {
            initRecyclerViewSearchMovie(it)
            hideLoading()
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
                val detailMovieFragment = DetailMovieFragment()
                val bundle = Bundle()
                bundle.putSerializable(ID_MOVIE, this.listMovieModel.results[index].id)
                detailMovieFragment.arguments = bundle
                addFragment(detailMovieFragment, R.id.frameLayout)
            }
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        frgSearchMovie_rcvResultSearch.setHasFixedSize(true)
        frgSearchMovie_rcvResultSearch.layoutManager = linearLayoutManager
        frgSearchMovie_rcvResultSearch.adapter = searchMovieAdapter
    }
}