package com.example.apptivitylab.demoapp.models

import org.json.JSONObject

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class User {
    var userID : String? = null
    var username : String? = null
    var password : String? = null
    var prefPetrol : Petrol? = null
    var prefBrands : ArrayList<Brand> = ArrayList()

    constructor(jsonObject: JSONObject) {
        userID = jsonObject.optString("userID")
        username = jsonObject.optString("Username")
        password = jsonObject.optString("Password")
    }
}