package com.example.movieapp.data.model.product

class ProductImage(
    val imageName: String = "${System.currentTimeMillis()}",
    var imagePath: String? = null, //imagePath is image from local
    var imageUrl: String? = null,  //imageUrl is link from firebase
    var isUploadFailed: Boolean = false,
    var isUploadSuccess: Boolean = false
)