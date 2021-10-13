package com.example.movieapp.ui.add

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.ProductModel
import com.example.movieapp.ui.main.MainActivity
import com.example.movieapp.utils.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_add_product.*
import java.io.IOException

class AddScreenFragment : BaseFragment(), View.OnClickListener {
    private val REQUEST_CODE_IMAGE = 1
    private var uri: Uri? = null
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private var localImageUrls = arrayListOf<String>()
    private var imageUrls = arrayListOf<String>()
    private lateinit var progress: ProgressDialog
    private lateinit var listImageViewPagerAdapter: ListImageViewPagerAdapter
    private var productModel = ProductModel()
    private var updateCount = 0

    override fun getLayoutID(): Int {
        return R.layout.fragment_add_product
    }

    override fun doViewCreated() {
        database = FirebaseDatabase.getInstance().reference.child(PRODUCT)
        storage = FirebaseStorage.getInstance().getReference("Images")
        progress = ProgressDialog(context)
        initListener()
        handleBottom()
        setInfo()
    }

    private fun showProgress() {
        progress.setTitle("Loading")
        progress.setMessage("Waiting add data...")
        progress.setCancelable(false)
        progress.show()
    }

    private fun dismissProgress() {
        progress.dismiss()
    }

    private fun handleBottom() {
        (activity as MainActivity).hideBottom()
    }

    private fun initListener() {
        frgAdd_imgSave.setOnClickListener(this)
        frgAdd_imgProduct.setOnClickListener(this)
        frgAdd_tvAdd.setOnClickListener(this)
        frgAdd_imgAddImages.setOnClickListener(this)
    }

    private fun setInfo() {
        when (arguments?.getString(NAME_PRODUCT)) {
            ADIDAS -> frgAdd_tvNameProduct.text = ADIDAS
            NIKE -> frgAdd_tvNameProduct.text = NIKE
            CONVERSE -> frgAdd_tvNameProduct.text = CONVERSE
            PUMA -> frgAdd_tvNameProduct.text = PUMA
            JORDAN -> frgAdd_tvNameProduct.text = JORDAN
        }
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
                            localImageUrls.add(uri.toString())
                        }
                    }
                    intent.data?.let {
                        localImageUrls.add(it.toString())
                    }
                    listImageViewPagerAdapter = ListImageViewPagerAdapter(
                        childFragmentManager,
                        localImageUrls
                    )
                    frgAdd_viewpager.adapter = listImageViewPagerAdapter
                    frgAdd_imgProduct.visibility = View.GONE
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR: ContentResolver = context?.contentResolver!!
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun createProduct() {
        if (frgAdd_etNameProduct.text.toString().isEmpty() || frgAdd_etAmountProduct.text.toString()
                .isEmpty()
        )
            Toast.makeText(context, getString(R.string.do_not_leave_blank), Toast.LENGTH_SHORT)
                .show()
        else {
            val name = frgAdd_etNameProduct.text.toString()
            val amount = frgAdd_etAmountProduct.text.toString().toInt()
            val price = frgAdd_etPriceProduct.text.toString().toInt()

            val key = System.currentTimeMillis().toString()
            productModel.apply {
                this.type = arguments?.getString(NAME_PRODUCT).toString()
                this.id = key
                this.name = name
                this.amount = amount
                this.price = price
            }
        }
    }

    private fun insertProduct() {
        productModel.apply {
            this.listImage = imageUrls
            this.urlAvatar = imageUrls[0]
        }

        database.child(arguments?.getString(NAME_PRODUCT).toString())
            .child(productModel.id.toString()).setValue(productModel).addOnCompleteListener {
                if ((activity is MainActivity)) {
                    dismissProgress()
                    (activity as MainActivity).hideLoading()
                    (activity as MainActivity).hideKeyboard()
                }
                back()
            }
    }

    private fun uploadImages() {
        for (url in localImageUrls) {
            val imageUri = Uri.parse(url)
            imageUri?.let {
                val uploadTask: UploadTask
                val fileReference: StorageReference = storage.child(
                    System.currentTimeMillis()
                        .toString() + "." + getFileExtension(it)
                )
                uploadTask = fileReference.putFile(it)
                uploadTask.addOnProgressListener {
                    showProgress()
                }.addOnSuccessListener {
                    val urlTask = uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            throw task.exception!!
                        }
                        fileReference.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            imageUrls.add(task.result.toString())
                            updateCount++
                            if (updateCount == localImageUrls.size) {
                                insertProduct()
                            }
                        }
                    }.addOnFailureListener {
                        updateCount++
                        if (updateCount == localImageUrls.size) {
                            insertProduct()
                        }
                    }
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgAdd_imgSave -> {
                createProduct()
                uploadImages()
            }
            R.id.frgAdd_imgProduct -> openGallery()
            R.id.frgAdd_imgAddImages -> openGallery()
        }
    }
}