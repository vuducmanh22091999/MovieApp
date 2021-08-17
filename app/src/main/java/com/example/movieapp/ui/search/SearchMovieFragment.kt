package com.example.movieapp.ui.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.movie.ListMovieModel
import com.example.movieapp.data.model.product.ProductModel
import com.example.movieapp.ui.detail.movie.DetailMovieFragment
import com.example.movieapp.ui.home.adapter.ListProductAdapter
import com.example.movieapp.ui.search.adapter.SearchMovieAdapter
import com.example.movieapp.utils.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_search_movie.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.HashMap
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.fragment_home.*


class SearchMovieFragment : BaseFragment(), View.OnClickListener {
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var searchMovieAdapter: SearchMovieAdapter
    private var listMovieModel = ListMovieModel()
    private lateinit var timer: Timer
    private var idProduct = 0
    private var listString = ArrayList<ProductModel>()
    private lateinit var listProductAdapter: ListProductAdapter

    private lateinit var database: DatabaseReference

    override fun getLayoutID(): Int {
        return R.layout.fragment_search_movie
    }

    override fun doViewCreated() {
//        searchMovie()
//        observerViewModel()
//        handleRcvWhenDelete()
        database = FirebaseDatabase.getInstance().reference.child(PRODUCT)

        initListener()
        setDataSpinner()
        setText()
        setListData()
    }

    private fun initListener() {
        buttonAdd.setOnClickListener(this)
        buttonUpdate.setOnClickListener(this)
        buttonDelete.setOnClickListener(this)
    }

    private fun setDataSpinner() {
        val product = resources.getStringArray(R.array.product)
        val adapter =
            context?.let { ArrayAdapter(it, android.R.layout.simple_dropdown_item_1line, product) }
        spinner.adapter = adapter
    }

    private fun setText() {
        database.child("Adidas")
            .child("Adidas1").child("name").get().addOnSuccessListener {
                test.text = it.value.toString()
            }

        database.child("Adidas").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val test = snapshot.child("Adidas1").child("name").value.toString()
                Log.d("test-log", test)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setListData() {
        database.child("Adidas").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        val string = value.getValue(ProductModel::class.java)
                        if (string != null) {
//                            listString.clear()
                            listString.add(string)
                        }
                        listProductAdapter = ListProductAdapter(listString.toList()) { index, _ ->
                            frgSearchMovie_etSearch.setText(listString[index].name.toString())
                            frgSearchMovie_etSearch1.setText(listString[index].number.toString())
                        }
                        val linearLayoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        rcvTest.setHasFixedSize(true)
                        rcvTest.layoutManager = linearLayoutManager
                        rcvTest.adapter = listProductAdapter
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun add() {
        val product = HashMap<String, Any>()
        idProduct += 1
        product["id"] = idProduct
        product["name"] = frgSearchMovie_etSearch.text.toString()
        product["number"] = frgSearchMovie_etSearch1.text.toString()
        database.child(spinner.selectedItem.toString())
            .child(spinner.selectedItem.toString() + idProduct.toString()).setValue(product)
//        database.child(spinner.selectedItem.toString()).setValue(product)
    }

    private fun update() {
        val product = HashMap<String, Any>()
        product["name"] = frgSearchMovie_etSearch.text.toString()
        product["number"] = frgSearchMovie_etSearch1.text.toString()
        database.child(spinner.selectedItem.toString()).child("Adidas1").updateChildren(product)

    }

    private fun delete() {
        val product = HashMap<String, Any>()
        product["name"] = frgSearchMovie_etSearch.text.toString()
        product["number"] = frgSearchMovie_etSearch1.text.toString()
        database.child(spinner.selectedItem.toString()).child("Adidas2").removeValue()
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
                                    viewModel.searchMovie(
                                        frgSearchMovie_etSearch.text.toString(),
                                        API_KEY
                                    )
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.buttonAdd -> add()
            R.id.buttonUpdate -> update()
            R.id.buttonDelete -> delete()
        }
    }
}