package com.example.apptivitylab.demoapp.controllers

import com.example.apptivitylab.demoapp.models.User

/**
 * Created by ApptivityLab on 23/01/2018.
 */
object UserController {
    lateinit var user: User

    fun setCurrentUser(user: User) {
        this.user = user
    }

    fun logOutUser() {
        this.user = User()
    }
}
