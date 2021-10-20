package com.example.movieapp.ui.add

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.ProductImage
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
    private lateinit var progress: ProgressDialog
    private lateinit var listImageViewPagerAdapter: ListImageViewPagerAdapter
    private var productModel = ProductModel()
    private var updateCount = 0
    private var productImages: ArrayList<ProductImage> = arrayListOf()
    private val REQUEST_GALLERY_IMAGE = 1

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

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(context)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery")
        pictureDialog.setItems(
            pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> openGallery()
            }
        }
        pictureDialog.show()
    }

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startGallery()
        } else {
            requestGalleryPermission()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            REQUEST_GALLERY_IMAGE
        )
    }

    private val requestPermissionGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            }
        }

    private fun requestGalleryPermission() {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE))
            requestPermissionGalleryLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        else {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", context?.packageName, null)
            intent.data = uri
            startActivity(intent)
        }

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
                    frgAdd_viewpager.adapter = listImageViewPagerAdapter
                    frgAdd_circleIndicator.setViewPager(frgAdd_viewpager)
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
                .isEmpty() || productImages.isEmpty()
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
            uploadImages()
        }
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

    private fun insertProduct() {
//        getProductImage()
//        productModel.listImage.clear()
//        productImages.filter { it.imageUrl != null }.forEach { image ->
//            image.imageUrl?.let { url ->
//                productModel.listImage.add(url)
//            }
//        }
        productModel.apply {
            this.urlAvatar = listImage.toList()[0]
        }
//        Log.d("ImageURI", productModel.toString())
        database.child(arguments?.getString(NAME_PRODUCT).toString())
            .child(productModel.id.toString()).setValue(productModel).addOnCompleteListener {
                if ((activity is MainActivity)) {
                    dismissProgress()
                    updateCount = 0
                    (activity as MainActivity).hideLoading()
                    (activity as MainActivity).hideKeyboard()
                }
                back()
            }
    }

    private fun uploadImages() {
        var numberOfRequest: Int
        productImages.filter { it.imagePath != null }.apply {
            showProgress()
            numberOfRequest = this.size
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
//                            Log.d("ImageURI", "ImageURI : $uri")
                            productModel.listImage.add(uri)
                            if (updateCount == numberOfRequest) {
                                productImages.find { image ->
                                    image.imageName == getImageName(task.result?.path ?: "")
                                }?.apply {
                                    if (this.imagePath == null)
                                        insertProduct()
                                    else {
                                        dismissProgress()
                                        showRetryDialog()
                                    }
                                }
                            }
                        }
                    }.addOnFailureListener {
                        Log.d("ImageURI", "addOnFailureListener : ${it.message}")
                    }
                }
            }
        }
    }

    private fun showRetryDialog() {
        val pictureDialog = AlertDialog.Builder(context)
        pictureDialog.setTitle("Load image failed. Retry...")
        val pictureDialogItems = arrayOf("Ok")
        pictureDialog.setItems(
            pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> openGallery()
            }
        }
        pictureDialog.show()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgAdd_imgSave -> {
                createProduct()
            }
            R.id.frgAdd_imgAddImages -> showPictureDialog()
        }
    }
}