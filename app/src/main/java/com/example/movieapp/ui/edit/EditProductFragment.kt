package com.example.movieapp.ui.edit

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.ProductImage
import com.example.movieapp.data.model.product.ProductModel
import com.example.movieapp.data.model.product.SizeProductModel
import com.example.movieapp.ui.add.ListImageViewPagerAdapter
import com.example.movieapp.ui.add.ListSizeAdapter
import com.example.movieapp.ui.main.MainActivity
import com.example.movieapp.utils.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_edit_product.*
import java.io.File
import java.io.IOException
import kotlin.collections.ArrayList

class EditProductFragment : BaseFragment(), View.OnClickListener {
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private val REQUEST_CODE_IMAGE = 1
    private var uri: Uri? = null
    private var productModel = ProductModel()
    private var listSize = ArrayList<SizeProductModel>()
    private lateinit var progress: ProgressDialog
    private lateinit var listImageViewPagerAdapter: ListImageViewPagerAdapter
    private lateinit var listSizeAdapter: ListSizeAdapter
    private var productImages: ArrayList<ProductImage> = arrayListOf()
    private var updateCount = 0

    override fun getLayoutID(): Int {
        return R.layout.fragment_edit_product
    }

    override fun doViewCreated() {
        database = FirebaseDatabase.getInstance().reference.child(PRODUCT)
        storage = FirebaseStorage.getInstance().getReference("Images")
        progress = ProgressDialog(context)
        handleBottom()
        hideKeyboardWhenClickOutside()
        initListener()
        getInfoFromHomeScreen()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun hideKeyboardWhenClickOutside() {
        repeat(2) {
            frgEditProduct_layout.setOnTouchListener { v, event ->
                val imm =
                    context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
                true
            }
        }
    }

    private fun showProgress() {
        progress.setMessage("Waiting update data...")
        progress.setCancelable(false)
        progress.show()
    }

    private fun dismissProgress() {
        progress.dismiss()
    }

    private fun handleBottom() {
        (activity as MainActivity).hideBottom()
    }

    private fun setDataForListSize() {
        for (size in 38..43)
            listSize.add(SizeProductModel(size = size, isSelected = false))
        for (i in listSize.indices) {
            productModel.listSize.forEach {
                if (it.size == listSize[i].size) {
                    listSize[i].isSelected = true
                    listSize[i].amountSize = it.amountSize
                }
            }
        }
    }

    private fun getInfoFromHomeScreen() {
        productModel = arguments?.getSerializable(PRODUCT_MODEL) as ProductModel
        frgEditProduct_etNameProduct.setText(productModel.name)
        frgEditProduct_etPriceProduct.setText(productModel.price.toString())
        getProductImage()
        listImageViewPagerAdapter = ListImageViewPagerAdapter(
            childFragmentManager,
            productImages
        )
        frgEditProduct_viewpager.adapter = listImageViewPagerAdapter
        listImageViewPagerAdapter.notifyDataSetChanged()
        frgEditProduct_circleIndicator.setViewPager(frgEditProduct_viewpager)

        setDataForListSize()
        listSizeAdapter = ListSizeAdapter(listSize.toList()) { index, amountSize ->
            listSize[index].isSelected = !listSize[index].isSelected
            listSize[index].amountSize = amountSize
            if (listSize[index].isSelected) {
                productModel.listSize.add(listSize[index])
                listSizeAdapter.notifyItemChanged(index)
            } else {
                val indexRemove = productModel.listSize.firstOrNull {
                    it.size == listSize[index].size
                }
                if (indexRemove != null) {
                    productModel.listSize.remove(indexRemove)
                    checkPosition(index)
                }
            }
        }

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        frgEditProduct_rcvSize.setHasFixedSize(true)
        frgEditProduct_rcvSize.layoutManager = linearLayoutManager
        frgEditProduct_rcvSize.adapter = listSizeAdapter
    }

    private fun checkPosition(index: Int) {
        val indexSelected = listSize.indexOfFirst {
            it.isSelected
        }
        if (indexSelected != -1) {
            listSize[indexSelected].isSelected = true
            listSizeAdapter.notifyItemChanged(indexSelected)
        }
        listSize[index].isSelected = false
        listSizeAdapter.notifyItemChanged(index)
    }

    private fun getProductImage() {
        for (imageUrl in productModel.listImage) {
            val imageName = getImageName(imageUrl)
            productImages.add(ProductImage(imageName = imageName, urlFirebase = imageUrl))
        }
    }

    private fun getImageName(path: String): String {
        val stringArray = path.split("/").toTypedArray()
        return stringArray[stringArray.size - 1].substringBefore(".")
    }

    private fun initListener() {
        frgEditProduct_tvUpdate.setOnClickListener(this)
        frgEditProduct_imgAddImages.setOnClickListener(this)
    }

    private fun insertProduct() {
        val name = frgEditProduct_etNameProduct.text.toString()
        val price = frgEditProduct_etPriceProduct.text.toString().toInt()

        productModel.apply {
            this.type = productModel.type
            this.id = productModel.id
            this.name = name
            this.price = price
            this.listSize = productModel.listSize
        }

        if (productImages.size != 0) {
            productModel.listImage.clear()
            productImages.filter { it.urlFirebase != null }.forEach { image ->
                image.urlFirebase?.let { url ->
                    productModel.listImage.add(url)
                }
            }
        }

        productModel.let {
            database.child(it.type!!)
                .child(it.id.toString()).setValue(productModel).addOnCompleteListener {
                    if ((activity is MainActivity)) {
                        dismissProgress()
                        updateCount = 0
                    }
                    back()
                }
        }
    }

    private fun uploadPhoto() {
        showProgress()
        val productImage = productImages[updateCount]
        val fileUri = Uri.parse(productImage.urlLocal)
        fileUri?.let { it ->
            val uploadTask: UploadTask
            val fileReference: StorageReference = storage.child(
                "${productImage.imageName}.${getFileExtension(it)}"
            )
            uploadTask = fileReference.putFile(it)
            uploadTask.addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener {
                    this.productImages[updateCount].apply {
                        this.urlFirebase = it.toString()
                        this.urlLocal = null
                        this.isUploadSuccess = true
                    }
                    updateCount++
                    if (updateCount == productImages.size)
                        insertProduct()
                    else
                        uploadPhoto()
                }
            }
        }
    }

    private fun updateProduct() {
        var numberOfRequest: Int
        productImages.filter { it.urlLocal != null }.apply {
            showProgress()
            numberOfRequest = if (this.isEmpty()) {
                insertProduct()
                0
            } else
                this.size
        }.forEach { productImage ->
            val imageUri = Uri.parse(productImage.urlLocal)
            imageUri?.let { it ->
                val uploadTask: UploadTask
                val fileReference: StorageReference = storage.child(
                    "${productImage.imageName}.${getFileExtension(it)}"
                )
                uploadTask = fileReference.putFile(it)
                uploadTask.addOnSuccessListener {
                    val urlTask = uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            throw task.exception!!
                        }
                        fileReference.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uri = task.result.toString()
                            updateCount++
                            productImages.find { image ->
                                image.imageName == getImageName(task.result?.path ?: "")
                            }?.apply {
                                this.urlFirebase = uri
                                this.urlLocal = null
                                this.isUploadSuccess = true
                            }
                            Log.d("ImageURI", "ImageURI : $uri")
                            if (updateCount == numberOfRequest) {
                                insertProduct()
                            }
                        }
                    }.addOnFailureListener {
                        updateCount++
                        if (updateCount == productImages.size)
                            insertProduct()
                        else
                            updateProduct()
                    }
                }
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR: ContentResolver = context?.contentResolver!!
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                data?.let { intent ->
                    intent.clipData?.let {
                        val count = it.itemCount
                        for (i in 0 until count) {
                            uri = it.getItemAt(i).uri
                            val f = File(it.getItemAt(i).uri.path)
                            val imageName = f.name
                            productImages.add(
                                ProductImage(
                                    imageName = imageName,
                                    urlLocal = uri.toString()
                                )
                            )
                        }
                    }
                    intent.data?.let {
                        val f = File(it.path)
                        val imageName = f.name
                        productImages.add(
                            ProductImage(
                                imageName = imageName,
                                urlLocal = it.toString()
                            )
                        )
                    }
                    listImageViewPagerAdapter = ListImageViewPagerAdapter(
                        childFragmentManager,
                        productImages
                    )
                    frgEditProduct_viewpager.adapter = listImageViewPagerAdapter
                    listImageViewPagerAdapter.notifyDataSetChanged()
                    frgEditProduct_circleIndicator.setViewPager(frgEditProduct_viewpager)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgEditProduct_tvUpdate -> updateProduct()
            R.id.frgEditProduct_imgAddImages -> openGallery()
        }
    }
}