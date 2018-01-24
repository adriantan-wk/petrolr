package com.example.apptivitylab.demoapp.controllers

import com.example.apptivitylab.demoapp.models.Brand
import com.example.apptivitylab.demoapp.models.PetrolType
import com.example.apptivitylab.demoapp.models.User

/**
 * Created by ApptivityLab on 23/01/2018.
 */
object UserController {
    lateinit var user: User

    fun createMockUser() {
        val brand1 = Brand("SHEL", "Shell")
        val brand2 = Brand("PTNS", "Petronas")

        val preferredBrands: ArrayList<Brand> = ArrayList()
        preferredBrands.add(brand1)
        preferredBrands.add(brand2)

        val petrolType = PetrolType("P004", "EURO 5", 2.33)

        this.user = User("U001", "adrian", "123", petrolType, preferredBrands)
    }
}
