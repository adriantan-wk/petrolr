package com.example.apptivitylab.demoapp.models

import org.json.JSONObject

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class User {
    val username : String
    val password : String

    constructor(jsonObject: JSONObject) {
        username = jsonObject.optString("Username")
        password = jsonObject.optString("Password")
    }
}