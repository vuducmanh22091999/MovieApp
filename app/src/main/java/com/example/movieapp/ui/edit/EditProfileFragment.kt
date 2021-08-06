package com.example.movieapp.ui.edit

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.view.View
import com.example.movieapp.R
import com.example.movieapp.base.BaseFragment
import com.example.movieapp.ui.main.MainActivity
import com.example.movieapp.utils.ACCOUNT
import com.example.movieapp.utils.ADMIN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import java.io.IOException
import kotlin.collections.HashMap
import android.util.Log
import com.example.movieapp.utils.PHONE_NUMBER
import com.example.movieapp.utils.USER_NAME
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.webkit.MimeTypeMap
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso


class EditProfileFragment : BaseFragment(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storage : StorageReference
    private val REQUEST_CODE_IMAGE = 1
    private var uri: Uri? = null
    private val infoUser = HashMap<String, Any>()
    private var userName = ""
    private var phoneNumber = ""

    override fun getLayoutID(): Int {
        return R.layout.fragment_edit_profile
    }

    override fun doViewCreated() {
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child(ACCOUNT).child(ADMIN)
        storage = FirebaseStorage.getInstance().getReference("Images")
        handleBottom()
        getInfoFromAccountScreen()
        initListener()
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
        (activity as MainActivity).hideBottom()
    }

    private fun getInfoFromAccountScreen() {
        userName = arguments?.getString(USER_NAME).toString()
        phoneNumber = arguments?.getString(PHONE_NUMBER).toString()
        frgEditProfile_etNameUser.setText(userName)
        frgEditProfile_etPhoneUser.setText(phoneNumber)
    }

    private fun saveProfile() {
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
                val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                    if (!it.isSuccessful) {
                        it.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation fileReference.downloadUrl
                })
             }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        infoUser["urlAvatar"] = task.result.toString()
                        val test = task.result?.storage?.downloadUrl.toString()
                        Log.d("test-aa", task.result.toString())
                        Log.d("test-aa", test)
                        auth.currentUser?.uid?.let {
                            databaseReference.child(it).updateChildren(infoUser)
                        }
                    }
                }
        }
        back()
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