package com.example.apptivitylab.demoapp.models

import org.json.JSONObject

/**
 * Created by ApptivityLab on 19/01/2018.
 */

class Brand() {
    var brandID: String? = null
    var brandName: String? = null
    var stations: ArrayList<Station> = ArrayList()

    constructor(jsonObject: JSONObject): this() {
        brandID = jsonObject.optString("brand_id")
        brandName = jsonObject.optString("brand_name")
    }

    constructor(brandID: String, brandName: String): this() {
        this.brandID = brandID
        this.brandName = brandName
    }
}