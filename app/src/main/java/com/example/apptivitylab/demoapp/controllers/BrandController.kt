package com.example.apptivitylab.demoapp.controllers

import android.content.Context
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.models.Brand
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by ApptivityLab on 25/01/2018.
 */

object BrandController {
    private lateinit var brandList: ArrayList<Brand>

    private fun loadMockBrands(context: Context) {
        val brandList: ArrayList<Brand> = ArrayList()
        val inputStream: InputStream = context.resources.openRawResource(R.raw.brands)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var jsonObject: JSONObject
        var brand: Brand

        val fileContent = reader.readText()
        jsonObject = JSONObject(fileContent.substring(fileContent.indexOf("{"), fileContent.lastIndexOf("}") + 1))
        val jsonArray: JSONArray = jsonObject.optJSONArray("brands")

        for (pt in 0 until jsonArray.length()) {
            brand = Brand(jsonArray.getJSONObject(pt))
            brandList.add(brand)
        }

        this.brandList = brandList
    }
}