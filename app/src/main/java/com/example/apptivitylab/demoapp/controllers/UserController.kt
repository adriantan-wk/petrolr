package com.example.apptivitylab.demoapp.controllers

import com.example.apptivitylab.demoapp.models.User

/**
 * Created by ApptivityLab on 23/01/2018.
 */
object UserController {
    lateinit var user: User

    fun createMockUser() {
        val preferredBrands: ArrayList<String> = ArrayList()
        preferredBrands.add("SHEL")
        preferredBrands.add("PTNS")

        this.user = User("U001", "adrian", "123", "P004", preferredBrands)
    }
}
