package com.example.movieapp.ui.edit

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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_edit_product.*
import java.io.IOException

class EditProductFragment : BaseFragment(), View.OnClickListener {
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private val REQUEST_CODE_IMAGE = 1
    private var uri: Uri? = null
    private val infoProduct = HashMap<String, Any>()
    private var productModel = ProductModel()
    private lateinit var progress: ProgressDialog

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
        Picasso.get().load(productModel.urlAvatar).into(frgEditProduct_imgAvatar)

    }

    private fun initListener() {
        frgEditProduct_imgSave.setOnClickListener(this)
        frgEditProduct_imgAvatar.setOnClickListener(this)
    }

    private fun updateProduct() {
        val uploadTask: UploadTask
        infoProduct["name"] = frgEditProduct_etNameProduct.text.toString()
        infoProduct["number"] = frgEditProduct_etAmountProduct.text
        infoProduct["price"] = frgEditProduct_etPriceProduct.text

        if (uri != null || !productModel.urlAvatar.isNullOrEmpty()) {
            if (uri != null) {
                val fileReference: StorageReference = storage.child(
                    System.currentTimeMillis()
                        .toString() + "." + getFileExtension(uri!!)
                )
                uploadTask = fileReference.putFile(uri!!)
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
                            productModel.run {
                                infoProduct["urlAvatar"] = task.result.toString()
                                database.child(type!!)
                                    .child(id!!).updateChildren(infoProduct).addOnCompleteListener {
                                        if ((activity is MainActivity)) {
                                            dismissProgress()
                                            (activity as MainActivity).hideKeyboard()
                                        }
                                        back()
                                    }
                            }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, "Failed!!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                productModel.run {
                    infoProduct["urlAvatar"] = this.urlAvatar ?: ""
                    database.child(type!!)
                        .child(id!!).updateChildren(infoProduct).addOnCompleteListener {
                            if ((activity is MainActivity)) {
                                dismissProgress()
                                (activity as MainActivity).hideKeyboard()
                            }
                            back()
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
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            uri = data.data!!
            try {
                Picasso.get().load(uri).into(frgEditProduct_imgAvatar)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgEditProduct_imgSave -> updateProduct()
            R.id.frgEditProduct_imgAvatar -> openGallery()
        }
    }
}