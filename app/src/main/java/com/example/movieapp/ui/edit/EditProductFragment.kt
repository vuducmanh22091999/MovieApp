package com.example.movieapp.ui.edit

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.ProductImage
import com.example.movieapp.data.model.product.ProductModel
import com.example.movieapp.ui.add.ListImageViewPagerAdapter
import com.example.movieapp.ui.main.MainActivity
import com.example.movieapp.utils.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_add_product.*
import kotlinx.android.synthetic.main.fragment_edit_product.*
import java.io.IOException
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EditProductFragment : BaseFragment(), View.OnClickListener {
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private val REQUEST_CODE_IMAGE = 1
    private var uri: Uri? = null
    private val infoProduct = HashMap<String, Any>()
    private var productModel = ProductModel()
    private lateinit var progress: ProgressDialog
    private lateinit var listImageViewPagerAdapter: ListImageViewPagerAdapter
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
        initListener()
        getInfoFromHomeScreen()
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

    private fun getInfoFromHomeScreen() {
        productModel = arguments?.getSerializable(PRODUCT_MODEL) as ProductModel
        frgEditProduct_etNameProduct.setText(productModel.name)
        frgEditProduct_etAmountProduct.setText(productModel.amount.toString())
        frgEditProduct_etPriceProduct.setText(productModel.price.toString())
        getProductImage()
        listImageViewPagerAdapter = ListImageViewPagerAdapter(
            childFragmentManager,
            productImages
        )
        frgEditProduct_viewpager.adapter = listImageViewPagerAdapter
        listImageViewPagerAdapter.notifyDataSetChanged()
        frgEditProduct_circleIndicator.setViewPager(frgEditProduct_viewpager)
    }

    private fun getProductImage() {
        for (imageUrl in productModel.listImage) {
            val imageName = getImageName(imageUrl)
            productImages.add(ProductImage(imageName = imageName, imageUrl = imageUrl))
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
        val amount = frgEditProduct_etAmountProduct.text.toString().toInt()
        val price = frgEditProduct_etPriceProduct.text.toString().toInt()

        productModel.apply {
            this.type = productModel.type
            this.id = productModel.id
            this.name = name
            this.amount = amount
            this.price = price
        }

        if (productImages.size != 0) {
            productModel.listImage.clear()
            productImages.filter { it.imageUrl != null }.forEach { image ->
                image.imageUrl?.let { url ->
                    productModel.listImage.add(url)
                }
            }
        }

        Log.d("ImageURI", productModel.toString())

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

//        productModel.listImage.clear()
//        productImages.filter { it.imageUrl != null }.forEach { image ->
//            image.imageUrl?.let { url ->
//                productModel.listImage.add(url)
//            }
//        }
//
//        infoProduct["name"] = frgEditProduct_etNameProduct.text.toString()
//        infoProduct["number"] = frgEditProduct_etAmountProduct.text
//        infoProduct["price"] = frgEditProduct_etPriceProduct.text
//        infoProduct["urlAvatar"] = productModel.listImage.toList()[0]
//        infoProduct["listImage"] = productModel.listImage.toArray()
//        Log.d("TEST-UPDATE", infoProduct.toString())
//
//        productModel.let {
//            database.child(it.type!!)
//                .child(it.id.toString()).updateChildren(infoProduct).addOnCompleteListener {
//                    if ((activity is MainActivity)) {
//                        dismissProgress()
//                        updateCount = 0
//                    }
//                    back()
//                }
//        }
    }

    private fun updateProduct() {
        var numberOfRequest: Int
        productImages.filter { it.imagePath != null }.apply {
            showProgress()
            numberOfRequest = if (this.isEmpty()) {
                insertProduct()
                0
            }
            else
                this.size
        }.forEach { productImage ->
            val imageUri = Uri.parse(productImage.imagePath)
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
                                this.imageUrl = uri
                                this.imagePath = null
                                this.isUploadSuccess = true
                            }
                            Log.d("ImageURI", "ImageURI : $uri")
                            if (updateCount == numberOfRequest) {
                                insertProduct()
                            }
                        }
                    }.addOnFailureListener {
                        updateCount++
                        if (updateCount == numberOfRequest) {
                            insertProduct()
                        }
                        Log.d(this.tag, "addOnFailureListener : ${it.message}")
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
                            productImages.add(ProductImage(imagePath = uri.toString()))
                        }
                    }
                    intent.data?.let {
                        productImages.add(ProductImage(imagePath = it.toString()))
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