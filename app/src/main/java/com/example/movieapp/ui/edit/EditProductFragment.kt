package com.example.movieapp.ui.edit

import android.app.Activity
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

class EditProductFragment: BaseFragment(), View.OnClickListener {
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private val REQUEST_CODE_IMAGE = 1
    private var uri: Uri? = null
    private var nameProduct = ""
    private var detailNameProduct = ""
    private var amountProduct = ""
    private var urlAvatarProduct = ""
    private val infoProduct = HashMap<String, Any>()

    override fun getLayoutID(): Int {
        return R.layout.fragment_edit_product
    }

    override fun doViewCreated() {
        database = FirebaseDatabase.getInstance().reference.child(PRODUCT)
        storage = FirebaseStorage.getInstance().getReference("Images")
        handleBottom()
        initListener()
        getInfoFromHomeScreen()
    }

    private fun handleBottom() {
        (activity as MainActivity).hideBottom()
    }

    private fun getInfoFromHomeScreen() {
        detailNameProduct = arguments?.getString(DETAIL_NAME_PRODUCT).toString()
        amountProduct = arguments?.getString(AMOUNT_PRODUCT).toString()
        urlAvatarProduct = arguments?.getString(URL_AVATAR).toString()
        nameProduct = arguments?.getString(NAME_PRODUCT).toString()
        frgEditProduct_etNameProduct.setText(detailNameProduct)
        frgEditProduct_etAmountProduct.setText(amountProduct)
        Picasso.get().load(urlAvatarProduct).into(frgEditProduct_imgAvatar)

    }

    private fun initListener() {
        frgEditProduct_imgSave.setOnClickListener(this)
        frgEditProduct_imgAvatar.setOnClickListener(this)
    }

    private fun updateProduct() {
        val uploadTask: UploadTask
        infoProduct["name"] = frgEditProduct_etNameProduct.text.toString()
        infoProduct["number"] = frgEditProduct_etAmountProduct.text.toString()

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
                        infoProduct["urlAvatar"] = task.result.toString()
                        database.child(nameProduct)
                            .child(detailNameProduct).updateChildren(infoProduct)
                        back()
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed!!", Toast.LENGTH_SHORT).show()
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
        when(v.id) {
            R.id.frgEditProduct_imgSave -> updateProduct()
            R.id.frgEditProduct_imgAvatar -> openGallery()
        }
    }
}