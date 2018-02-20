package com.example.apptivitylab.demoapp.api

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import org.json.JSONObject

/**
 * Created by ApptivityLab on 14/02/2018.
 */

class RestAPIClient(val context: Context) {
    private var requestQueue: RequestQueue = Volley.newRequestQueue(this.context)

    companion object {
        val BASE_URL = "https://kong-gateway.apptivitylab.com/euro5-api-dev/v1"

        private var singleton: RestAPIClient? = null

        fun shared(context: Context): RestAPIClient {
            if (singleton == null) {
                singleton = RestAPIClient(context)
            }

            return singleton!!
        }
    }

    interface OnGetResourceCompletedListener {
        fun onComplete(jsonObject: JSONObject?, error: VolleyError?)
    }

    fun getResources(path: String, limit: Int?, completionListener: OnGetResourceCompletedListener) {
        var retrievedAllRecords = false
        var limitParameter = ""
        var offset = 0
        var offsetParameter = ""

        if (limit != null) {
            limitParameter = "&limit=" + limit
        }

//        while (!retrievedAllRecords) {
            val request = Euro5JsonObjectRequest(Request.Method.GET,
                    BASE_URL + path + limitParameter, null,
                    object : Response.Listener<JSONObject> {
                        override fun onResponse(response: JSONObject?) {
//                                    response?.let {
//                                        val jsonArray = it.optJSONArray("resource")
//
//                                        if (limit != null && jsonArray.length() < limit) {
//                                            retrievedAllRecords = true
//                                            offset += limit
//                                            offsetParameter = "&offset=" + offset
//                                        }
//                                    }

                            completionListener.onComplete(response, null)
                        }
                    }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    completionListener.onComplete(null, error)
                }
            })

            this.requestQueue.add(request)
//        }
    }
}