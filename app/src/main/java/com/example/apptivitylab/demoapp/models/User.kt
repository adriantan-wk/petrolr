package com.example.apptivitylab.demoapp.models

import org.json.JSONObject

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class User {
    var userID : String? = null
    var username : String? = null
    var password : String? = null
    var preferredPetrol: String? = null
    var preferredBrands: ArrayList<String> = ArrayList()

    constructor(jsonObject: JSONObject) {
        userID = jsonObject.optString("userID")
        username = jsonObject.optString("Username")
        password = jsonObject.optString("Password")
    }

    constructor(userID: String, username: String, password: String,
                petrolID: String, prefBrands : ArrayList<String>) {
        this.userID = userID
        this.username = username
        this.password = password
        this.preferredPetrol = petrolID
        this.preferredBrands = prefBrands
    }
}