package com.example.apptivitylab.demoapp.models

import org.json.JSONObject

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class User {
    var userID: String? = null
    var username: String? = null
    var password: String? = null
    var preferredPetrolType: PetrolType? = null
    var preferredBrands: ArrayList<Brand> = ArrayList()

    constructor(jsonObject: JSONObject) {
        userID = jsonObject.optString("userID")
        username = jsonObject.optString("Username")
        password = jsonObject.optString("Password")
    }

    constructor(userID: String, username: String, password: String,
                petrolType: PetrolType, prefBrands : ArrayList<Brand>) {
        this.userID = userID
        this.username = username
        this.password = password
        this.preferredPetrolType = petrolType
        this.preferredBrands = prefBrands
    }
}