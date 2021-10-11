package com.example.movieapp.ui.edit

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.view.View
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import java.io.IOException
import kotlin.collections.HashMap
import android.webkit.MimeTypeMap
import com.example.movieapp.ui.main.UserActivity
import com.example.movieapp.utils.*
import com.google.firebase.storage.*
import com.squareup.picasso.Picasso


class EditProfileFragment : BaseFragment(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storage: StorageReference
    private val REQUEST_CODE_IMAGE = 1
    private var uri: Uri? = null
    private val infoUser = HashMap<String, Any>()
    private var userName = ""
    private var phoneNumber = ""
    private var urlAvatar = ""
    private var typeAccount = ""
    private lateinit var progress: ProgressDialog

    override fun getLayoutID(): Int {
        return R.layout.fragment_edit_profile
    }

    override fun doViewCreated() {
        typeAccount = arguments?.getString(TYPE_ACCOUNT).toString()
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(typeAccount)
        storage = FirebaseStorage.getInstance().getReference("Images")
        progress = ProgressDialog(context)
        handleBottom()
        getInfoFromAccountScreen()
        initListener()
    }

    private fun showProgress() {
        progress.setMessage("Waiting update profile...")
        progress.setCancelable(false)
        progress.show()
    }

    private fun dismissProgress() {
        progress.dismiss()
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR: ContentResolver = context?.contentResolver!!
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun initListener() {
        frgEditProfile_imgSave.setOnClickListener(this)
        frgEditProfile_tvChangeYourAvatar.setOnClickListener(this)
    }

    private fun handleBottom() {
        (activity as UserActivity).hideBottom()
    }

    private fun getInfoFromAccountScreen() {
        userName = arguments?.getString(USER_NAME).toString()
        phoneNumber = arguments?.getString(PHONE_NUMBER).toString()
        urlAvatar = arguments?.getString(URL_AVATAR).toString()
        frgEditProfile_etNameUser.setText(userName)
        frgEditProfile_etPhoneUser.setText(phoneNumber)
        if (urlAvatar == "null")
            Picasso.get().load(R.drawable.ic_account).into(frgEditProfile_imgAvatar)
        else
            Picasso.get().load(urlAvatar).into(frgEditProfile_imgAvatar)
    }

    private fun saveProfile() {
        showProgress()
        val uploadTask: UploadTask
        infoUser["userName"] = frgEditProfile_etNameUser.text.toString()
        infoUser["phoneNumber"] = frgEditProfile_etPhoneUser.text.toString()
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
                        infoUser["urlAvatar"] = task.result.toString()
                        auth.currentUser?.uid?.let {
                            databaseReference.child(it).updateChildren(infoUser)
                                .addOnCompleteListener {
                                    if ((activity is MainActivity)) {
                                        dismissProgress()
                                    }
                                    back()
                                }
                        }
                    }
                }
            }
        } else {
            infoUser["urlAvatar"] = urlAvatar
            auth.currentUser?.uid?.let {
                databaseReference.child(it).updateChildren(infoUser)
                    .addOnCompleteListener {
                        if ((activity is MainActivity)) {
                            dismissProgress()
                            (activity as MainActivity).hideKeyboard()
                        }
                        back()
                    }
            }
        }
    }

    private fun openGallery() {
        //mở camera để chụp hình
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(intent, REQUEST_CODE_IMAGE)

        //mở collection để chọn hình ảnh
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null) {
            // set image đã chụp
//            val bitmap = data.extras?.get("data") as Bitmap
//            imgView.setImageBitmap(bitmap)

            // set image đã lấy từ device
            uri = data.data!!
            try {
                Picasso.get().load(uri).into(frgEditProfile_imgAvatar)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frgEditProfile_imgSave -> saveProfile()
            R.id.frgEditProfile_tvChangeYourAvatar -> openGallery()
        }
    }
}