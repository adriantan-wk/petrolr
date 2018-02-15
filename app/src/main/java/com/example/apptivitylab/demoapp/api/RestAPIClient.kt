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

    fun getResources(path: String, completionListener: OnGetResourceCompletedListener) {
        val request = Euro5JsonObjectRequest(Request.Method.GET, BASE_URL + path, null,
                object : Response.Listener<JSONObject> {
                    override fun onResponse(response: JSONObject?) {
                        Log.i("CHECK", "$response")

                        completionListener.onComplete(response, null)
                    }
                }, object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {
                        completionListener.onComplete(null, error)
                    }
                })

        this.requestQueue.add(request)
    }
}