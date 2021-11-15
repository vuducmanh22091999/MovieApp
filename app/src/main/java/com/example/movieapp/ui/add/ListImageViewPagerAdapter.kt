package com.example.movieapp.ui.add

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.movieapp.data.model.product.ProductImage

class ListImageViewPagerAdapter(
    fragmentManager: FragmentManager,
    private val listImage: ArrayList<ProductImage>
): FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getCount(): Int {
        return listImage.size
    }

    override fun getItem(position: Int): Fragment {
        val productImage = listImage[position]
        var imagePath = ""

        productImage.urlLocal?.let {
            imagePath = it
        }

        productImage.urlFirebase?.let {
            imagePath = it
        }

        return ListImageFragment(imagePath)
    }
}