package com.example.apptivitylab.demoapp.ui


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.apptivitylab.demoapp.models.PetrolType

/**
 * Created by ApptivityLab on 27/02/2018.
 */

class PetrolPricePageAdapter(fragmentManager: FragmentManager, private val petrolTypeList: ArrayList<PetrolType>) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return PetrolPriceHistoryFragment.newInstance(petrolTypeList[position])
    }

    override fun getCount(): Int {
        return petrolTypeList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return petrolTypeList[position].petrolName
    }

    fun findPreferredPetrolTypePosition(preferredPetrolType: PetrolType): Int {
        return petrolTypeList.indexOf(petrolTypeList.first { petrolType ->
            petrolType.petrolID == preferredPetrolType.petrolID
        })
    }
}