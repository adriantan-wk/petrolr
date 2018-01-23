package com.example.apptivitylab.demoapp.controllers

import com.example.apptivitylab.demoapp.models.User

/**
 * Created by ApptivityLab on 23/01/2018.
 */
object UserController {
    lateinit var user: User

    fun createMockUser() {
        val prefBrands: ArrayList<String> = ArrayList()
        prefBrands.add("SHEL")
        prefBrands.add("PTNS")

        this.user = User("U001", "adrian", "123", "P004", prefBrands)
    }
}
