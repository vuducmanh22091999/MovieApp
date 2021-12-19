package com.example.movieapp.data.model.product

class ProductImage(
    val imageName: String = "",
    var urlLocal: String? = null, //imagePath is image from local
    var urlFirebase: String? = null,  //imageUrl is link from firebase
    var isUploadSuccess: Boolean = false
)