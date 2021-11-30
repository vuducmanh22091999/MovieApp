package com.example.movieapp.ui.detail.product

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.CartProductModel
import com.example.movieapp.data.model.product.ProductImage
import com.example.movieapp.data.model.product.ProductModel
import com.example.movieapp.data.model.product.SizeProductModel
import com.example.movieapp.ui.add.ListImageViewPagerAdapter
import com.example.movieapp.ui.detail.adapter.ListSizeUserAdapter
import com.example.movieapp.ui.main.UserActivity
import com.example.movieapp.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_detail_product.*

class DetailProductFragment : BaseFragment(), View.OnClickListener {
    private var detailProductModel = ProductModel()
    private lateinit var database: DatabaseReference
    private lateinit var listImageViewPagerAdapter: ListImageViewPagerAdapter
    private var productImages: ArrayList<ProductImage> = arrayListOf()
    private var idUser = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var progress: ProgressDialog
    private lateinit var listSizeUserAdapter: ListSizeUserAdapter
    private var listSize = ArrayList<SizeProductModel>()
    private var listSizePicked = ArrayList<SizeProductModel>()
    private val listCartProduct = ArrayList<CartProductModel>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_detail_product
    }

    override fun doViewCreated() {
        database = FirebaseDatabase.getInstance().reference.child(USER_CART)
        auth = FirebaseAuth.getInstance()
        idUser = auth.currentUser?.uid.toString()
        progress = ProgressDialog(context)
        initListener()
        hideKeyboardWhenClickOutside()
        handleBottom()
        getInfoFromUserHome()
        getListCart()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun hideKeyboardWhenClickOutside() {
        repeat(2) {
            frgDetailProduct_layout.setOnTouchListener { v, event ->
                val imm =
                    context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
                true
            }
        }
    }

    private fun initListener() {
        frgDetailProduct_tvAddToCart.setOnClickListener(this)
        frgDetailProduct_imgBack.setOnClickListener(this)
    }

    private fun handleBottom() {
        (activity as UserActivity).hideBottom()
    }

    private fun setDataForListSize() {
        for (size in 38..43) {
            detailProductModel.listSize.find {
                it.size == size
            }?.apply {
                if (this.size == size && this.isSelected)
                    listSize.add(
                        SizeProductModel(
                            size = size,
                            amountSize = this.amountSize,
                            isSelected = false
                        )
                    )
            }
        }
    }

    private fun setUpRecyclerView(listSize: List<SizeProductModel>) {
        listSizeUserAdapter = ListSizeUserAdapter(listSize.toList()) { index, _ ->
            checkPosition(index)
        }

        val gridLayoutManager = GridLayoutManager(context, 3)
        frgDetailProduct_rcvSize.setHasFixedSize(true)
        frgDetailProduct_rcvSize.layoutManager = gridLayoutManager
        frgDetailProduct_rcvSize.adapter = listSizeUserAdapter
    }

    private fun showProgress() {
        progress.setMessage("Waiting add product to cart...")
        progress.setCancelable(false)
        progress.show()
    }

    private fun dismissProgress() {
        progress.dismiss()
    }

    @SuppressLint("SetTextI18n")
    private fun getInfoFromUserHome() {
        detailProductModel = arguments?.getSerializable(DETAIL_PRODUCT) as ProductModel
        frgDetailProduct_tvTitleNameProduct.text = detailProductModel.name
        frgDetailProduct_tvPrice.text = "${formatStringLong(detailProductModel.price)}$"
        frgDetailProduct_tvContent.text = detailProductModel.contentProduct
        getProductImage()
        listImageViewPagerAdapter = ListImageViewPagerAdapter(
            childFragmentManager,
            productImages
        )
        frgDetailProduct_viewpager.adapter = listImageViewPagerAdapter
        listImageViewPagerAdapter.notifyDataSetChanged()
        frgDetailProduct_circleIndicator.setViewPager(frgDetailProduct_viewpager)

        setDataForListSize()
        setUpRecyclerView(listSize.toList())
    }

    @SuppressLint("SetTextI18n")
    private fun checkPosition(index: Int) {
        val indexSelected = listSize.indexOfFirst {
            it.isSelected
        }
        if (indexSelected != -1) {
            listSize[indexSelected].isSelected = false
            listSizeUserAdapter.notifyItemChanged(indexSelected)
        }
        listSize[index].isSelected = true
        frgDetailProduct_tvAmount.text = "Amount: ${listSize[index].amountSize}"
        listSizePicked.clear()
        listSizePicked.add(listSize[index])
        listSizeUserAdapter.notifyItemChanged(index)
    }

    private fun getProductImage() {
        for (imageUrl in detailProductModel.listImage) {
            val imageName = getImageName(imageUrl)
            productImages.add(ProductImage(imageName = imageName, urlFirebase = imageUrl))
        }
    }

    private fun getImageName(path: String): String {
        val stringArray = path.split("/").toTypedArray()
        return stringArray[stringArray.size - 1].substringBefore(".")
    }

    private fun getListCart() {
        database.child(idUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        val cartProductModel = value.getValue(CartProductModel::class.java)
                        if (cartProductModel != null)
                            listCartProduct.add(cartProductModel)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun checkValidate() {
        when {
            frgDetailProduct_etAmountOrder.text.toString().isEmpty() ->
                Toast.makeText(context, "Don't leave blank!!!", Toast.LENGTH_SHORT).show()
            frgDetailProduct_etAmountOrder.text.toString().trim().toLong() == 0L ->
                Toast.makeText(context, "Please type amount > 0", Toast.LENGTH_SHORT).show()
            listSizePicked.isEmpty() -> Toast.makeText(
                context,
                "Please pick size",
                Toast.LENGTH_SHORT
            ).show()
            frgDetailProduct_etAmountOrder.text.toString().trim().toLong() > listSizePicked[0].amountSize ->
                Toast.makeText(
                    context,
                    "Amount order is smaller than available current amount",
                    Toast.LENGTH_SHORT
                ).show()
            else -> {
                updateCart()
            }
        }
    }

    private fun updateCart() {
        if (listCartProduct.isNotEmpty()) {
            val modelUpdate = listCartProduct.firstOrNull {
                it.productModel?.id == detailProductModel.id &&
                        it.size == listSizePicked[0].size
            }
            if (modelUpdate != null) {
                modelUpdate.amountUserOrder += frgDetailProduct_etAmountOrder.text.toString()
                    .toInt()
                modelUpdate.totalPrice =
                    modelUpdate.productModel?.price!! *
                            modelUpdate.amountUserOrder
                setDatabase(modelUpdate.idCart!!, modelUpdate)
            } else
                addToCart()
        } else
            addToCart()
    }

    private fun insertCart(key: Long, amountUserOrder: Long) {
        showProgress()
        val totalCart = detailProductModel.price * amountUserOrder
        val cartProductModel = CartProductModel(
            key,
            idUser,
            amountUserOrder,
            listSizePicked[0].size,
            ProductModel(
                type = detailProductModel.type,
                id = detailProductModel.id,
                urlAvatar = detailProductModel.urlAvatar,
                name = detailProductModel.name,
                price = detailProductModel.price,
                listImage = detailProductModel.listImage,
                listSize = listSizePicked
            ),
            isAddSuccess = true,
            totalPrice = totalCart
        )
        setDatabase(key, cartProductModel)
    }

    private fun setDatabase(key: Long, cartProductModel: CartProductModel) {
        database.child(idUser).child(key.toString()).setValue(cartProductModel)
            .addOnSuccessListener {
                dismissProgress()
                if (activity is UserActivity)
                    (activity as UserActivity).onBackPressed()
            }
    }

    private fun addToCart() {
        val key = System.currentTimeMillis()
        val amountUserOrder = frgDetailProduct_etAmountOrder.text.toString().trim().toLong()
        insertCart(key, amountUserOrder)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgDetailProduct_tvAddToCart -> checkValidate()
            R.id.frgDetailProduct_imgBack -> (activity as UserActivity).onBackPressed()
        }
    }
}