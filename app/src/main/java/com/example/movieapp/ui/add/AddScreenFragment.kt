package com.example.movieapp.ui.add

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.util.Log
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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add_product.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_search_movie.*
import java.io.IOException

class AddScreenFragment : BaseFragment(), View.OnClickListener {
    private val REQUEST_CODE_IMAGE = 1
    private var uri: Uri? = null
    private var idProduct = 0
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private var urlAvatar = ""

    override fun getLayoutID(): Int {
        return R.layout.fragment_add_product
    }

    override fun doViewCreated() {
        database = FirebaseDatabase.getInstance().reference.child(PRODUCT)
        storage = FirebaseStorage.getInstance().getReference("Images")
        initListener()
        handleBottom()
        setInfo()
    }

    private fun handleBottom() {
        (activity as MainActivity).hideBottom()
    }

    private fun initListener() {
        frgAdd_imgSave.setOnClickListener(this)
        frgAdd_imgProduct.setOnClickListener(this)
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
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            uri = data.data!!
            try {
                Picasso.get().load(uri).into(frgAdd_imgProduct)
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

    private fun addProduct() {
        if (frgAdd_etNameProduct.text.toString().isEmpty() || frgAdd_etAmountProduct.text.toString()
                .isEmpty()
        )
            Toast.makeText(context, getString(R.string.do_not_leave_blank), Toast.LENGTH_SHORT)
                .show()
        else {
            val name = frgAdd_etNameProduct.text.toString()
            val number = frgAdd_etAmountProduct.text.toString()

            val uploadTask: UploadTask
            if (uri != null) {
                val fileReference: StorageReference = storage.child(
                    System.currentTimeMillis()
                        .toString() + "." + getFileExtension(uri!!)
                )
                uploadTask = fileReference.putFile(uri!!)
                uploadTask.addOnSuccessListener {
                    val urlTask = uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            throw task.exception!!
                        }
                        fileReference.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            urlAvatar = task.result.toString()
                            val productModel =
                                ProductModel(name = name, urlAvatar = urlAvatar, number = number)
                            database.child(arguments?.getString(NAME_PRODUCT).toString())
                                .child(name).setValue(productModel)
                        }
                    }
                }
            }
            back()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgAdd_imgSave -> addProduct()
            R.id.frgAdd_imgProduct -> openGallery()
        }
    }
}