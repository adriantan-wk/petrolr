package com.example.apptivitylab.demoapp.controllers

import android.content.Context
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.User
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by ApptivityLab on 23/01/2018.
 */
object UserController {
    lateinit var user: User

    fun setCurrentUser(user: User) {
        this.user = user
    }

    fun loadMockUsers(context: Context): ArrayList<User> {
        val userList: ArrayList<User> = ArrayList()
        val inputStream: InputStream = context.resources.openRawResource(R.raw.users)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var jsonObject: JSONObject
        var user: User

        val fileContent = reader.readText()
        jsonObject = JSONObject(fileContent.substring(fileContent.indexOf("{"), fileContent.lastIndexOf("}") + 1))
        val jsonArray: JSONArray = jsonObject.optJSONArray(("users"))

        for (s in 0 until jsonArray.length()) {
            user = User(jsonArray.getJSONObject(s))
            userList.add(user)
        }
        return userList
    }

    fun logOutUser() {
        this.user = User()
    }
}
