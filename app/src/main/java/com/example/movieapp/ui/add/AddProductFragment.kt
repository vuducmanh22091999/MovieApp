package com.example.movieapp.ui.add

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.data.model.product.ProductImage
import com.example.movieapp.data.model.product.ProductModel
import com.example.movieapp.data.model.product.SizeProductModel
import com.example.movieapp.ui.main.MainActivity
import com.example.movieapp.utils.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_add_product.*
import java.io.File
import java.io.IOException
import kotlin.collections.ArrayList

class AddProductFragment : BaseFragment(), View.OnClickListener {
    private val REQUEST_CODE_IMAGE = 1
    private var uri: Uri? = null
    private lateinit var database: DatabaseReference
    private lateinit var database1: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var progress: ProgressDialog
    private lateinit var listImageViewPagerAdapter: ListImageViewPagerAdapter
    private lateinit var listSizeAdapter: ListSizeAdapter
    private var listSize = ArrayList<SizeProductModel>()
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
        database1 = FirebaseDatabase.getInstance().reference.child("NhaCungCap")
        progress = ProgressDialog(context)
        initListener()
        hideKeyboardWhenClickOutside()
        handleBottom()
        setDataForListSize()
        setUpRecyclerView()
        setInfo()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun hideKeyboardWhenClickOutside() {
        repeat(2) {
            frgAdd_layout.setOnTouchListener { v, event ->
                val imm =
                    context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
                true
            }
        }
    }

    private fun showProgress() {
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
        frgAdd_tvAddProduct.setOnClickListener(this)
        frgAdd_imgAddImages.setOnClickListener(this)
        frgAdd_imgBack.setOnClickListener(this)
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

    private fun setUpRecyclerView() {
        listSizeAdapter = ListSizeAdapter(listSize.toList(), { index, amountSize ->
            listSize[index].isSelected = !listSize[index].isSelected
            if (listSize[index].isSelected) {
                listSize[index].amountSize = amountSize
                productModel.listSize.add(listSize[index])
                listSizeAdapter.notifyItemChanged(index)
            } else {
                listSize[index].amountSize = 0L
                productModel.listSize.remove(listSize[index])
                listSizeAdapter.notifyItemChanged(index)
            }
        }, { string, index ->
        })
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        frgAdd_rcvSize.setHasFixedSize(true)
        frgAdd_rcvSize.layoutManager = linearLayoutManager
        frgAdd_rcvSize.adapter = listSizeAdapter
    }

    private fun setDataForListSize() {
        for (size in 38..43)
            listSize.add(SizeProductModel(size = size, isSelected = false))
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
                var countImage = 0
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
//                        productImages.add(
//                            ProductImage(
//                                imageName = System.currentTimeMillis().toString(),
//                                urlLocal = it.toString()
//                            )
//                        )
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
        listSize.forEach {
            if (it.isSelected)
                if (it.amountSize == 0L)
                    Toast.makeText(context, "Please input amount size!!!", Toast.LENGTH_SHORT).show()
        }
        if (frgAdd_etNameProduct.text.toString().isEmpty() ||
            frgAdd_etPriceProduct.text.toString().isEmpty() ||
            frgAdd_etContentProduct.text.toString().isEmpty()
        )
            Toast.makeText(context, getString(R.string.do_not_leave_blank), Toast.LENGTH_SHORT)
                .show()
        else if (productImages.isEmpty())
            Toast.makeText(context, getString(R.string.add_image), Toast.LENGTH_SHORT)
                .show()
        else if (frgAdd_etPriceProduct.text.toString().trim().toLong() == 0L ||
            frgAdd_etPriceProduct.text.toString().trim().toLong() < 0L
        )
            Toast.makeText(context, getString(R.string.title_price_bigger_0), Toast.LENGTH_SHORT)
                .show()
        else {
            val name = frgAdd_etNameProduct.text.toString()
            val price = frgAdd_etPriceProduct.text.toString().trim().toLong()
            val content = frgAdd_etContentProduct.text.toString()

            val key = System.currentTimeMillis()
            uploadPhoto()
            productModel.apply {
                this.type = arguments?.getString(NAME_PRODUCT).toString()
                this.id = key
                this.name = name
                this.contentProduct = content
                this.price = price
                this.listImage = productModel.listImage
            }
        }
    }



    private fun insertProduct() {
        productImages.forEach { image ->
            image.urlFirebase?.let { url ->
                productModel.listImage.add(url)
            }
        }

        Log.d("test-add-image", productModel.listImage.toString())

        productModel.apply {
            this.urlAvatar = listImage[0]
        }
        database.child(arguments?.getString(NAME_PRODUCT).toString())
            .child(productModel.id.toString()).setValue(productModel).addOnCompleteListener {
                if ((activity is MainActivity)) {
                    dismissProgress()
                    updateCount = 0
                    (activity as MainActivity).hideKeyboard()
                }
                back()
            }
    }

    private fun uploadPhoto() {
        showProgress()
        val productImage = productImages[updateCount]
        val fileUri = Uri.parse(productImage.urlLocal)
        fileUri?.let { it ->
            android.os.Handler().postDelayed({
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
                            android.os.Handler().postDelayed({
                                insertProduct()
                            }, SPLASH_DISPLAY_LENGTH)
                        else
                            android.os.Handler().postDelayed({
                                uploadPhoto()
                            }, SPLASH_DISPLAY_LENGTH)

                    }
                }
            }, SPLASH_DISPLAY_LENGTH)
//            val uploadTask: UploadTask
//            val fileReference: StorageReference = storage.child(
//                "${productImage.imageName}.${getFileExtension(it)}"
//            )
//            uploadTask = fileReference.putFile(it)
//            uploadTask.addOnSuccessListener {
//                fileReference.downloadUrl.addOnSuccessListener {
//                    this.productImages[updateCount].apply {
//                        this.urlFirebase = it.toString()
//                        this.urlLocal = null
//                        this.isUploadSuccess = true
//                    }
//                    updateCount++
//                    if (updateCount == productImages.size)
//                        android.os.Handler().postDelayed({
//                            insertProduct()
//                        }, SPLASH_DISPLAY_LENGTH)
//                    else
//                        android.os.Handler().postDelayed({
//                            uploadPhoto()
//                        }, SPLASH_DISPLAY_LENGTH)
//
//                }
//            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgAdd_tvAddProduct -> createProduct()
            R.id.frgAdd_imgAddImages -> showPictureDialog()
            R.id.frgAdd_imgBack -> {
                (activity as MainActivity).onBackPressed()
//                (activity as MainActivity).hideKeyboard()
            }
        }
    }
}