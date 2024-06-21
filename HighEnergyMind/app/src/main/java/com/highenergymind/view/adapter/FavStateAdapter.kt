package com.highenergymind.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.highenergymind.view.fragment.favoritePager.FavAffirmationFragment
import com.highenergymind.view.fragment.favoritePager.FavTrackFragment

class FavStateAdapter(fragmentManager: FragmentManager, lifecycle: androidx.lifecycle.Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                FavTrackFragment()
            }

            1 -> {
                FavAffirmationFragment()
            }
            else->{
                FavTrackFragment()
            }

        }
    }
}