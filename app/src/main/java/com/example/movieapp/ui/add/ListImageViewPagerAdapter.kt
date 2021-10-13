package com.example.movieapp.ui.add

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ListImageViewPagerAdapter(
    fragmentManager: FragmentManager,
    private val listImage: ArrayList<String>
): FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getCount(): Int {
        return listImage.size
    }

    override fun getItem(position: Int): Fragment {
        return ListImageFragment(listImage[position])
    }
}