package com.example.apptivitylab.demoapp.controllers

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.VolleyError
import com.example.apptivitylab.demoapp.R
import com.example.apptivitylab.demoapp.api.RestAPIClient
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
    var brandList: ArrayList<Brand> = ArrayList()

//    fun loadMockBrands(context: Context) {
//        val brandList: ArrayList<Brand> = ArrayList()
//        val inputStream: InputStream = context.resources.openRawResource(R.raw.brands)
//        val reader = BufferedReader(InputStreamReader(inputStream))
//        var jsonObject: JSONObject
//        var brand: Brand
//
//        val fileContent = reader.readText()
//        jsonObject = JSONObject(fileContent.substring(fileContent.indexOf("{"), fileContent.lastIndexOf("}") + 1))
//        val jsonArray: JSONArray = jsonObject.optJSONArray("brands")
//
//        for (pt in 0 until jsonArray.length()) {
//            brand = Brand(jsonArray.getJSONObject(pt))
//
//            brand.brandLogo = when (brand.brandID) {
//                "SHEL" -> R.drawable.ic_shell
//                "BRPE" -> R.drawable.ic_bp
//                "PTNS" -> R.drawable.ic_petronas
//                "PTRN" -> R.drawable.ic_petron
//                else -> 0
//            }
//
//            brandList.add(brand)
//        }
//
//        this.brandList = brandList
//    }

    fun loadBrands(context: Context) {
        val path = "/data/companies"

        RestAPIClient.shared(context).getResources(path, null,
                object : RestAPIClient.OnGetResourceCompletedListener {
                    override fun onComplete(jsonObject: JSONObject?, error: VolleyError?) {
                        if (jsonObject != null) {
                            var brandList: ArrayList<Brand> = ArrayList()
                            var brand: Brand

                            val jsonArray: JSONArray = jsonObject.optJSONArray("resource")

                            for (item in 0 until jsonArray.length()) {
                                brand = Brand(jsonArray.getJSONObject(item))

                                brandList.add(brand)
                            }
                            this@BrandController.brandList = brandList

                            this@BrandController.brandList.forEach {
                                Log.i("OKIE", "${it.brandID}")
                                Log.i("OKIE", "${it.brandName}")
                                Log.i("OKIE", "${it.brandWebsite}")
                                Log.i("OKIE", "${it.brandCreatedAt}")
                                Log.i("OKIE", "${it.brandUpdatedAt}")
                                Log.i("OKIE", "${it.brandLogo}")
                            }

                        } else {
                            Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
    }

}