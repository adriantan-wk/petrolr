package com.example.apptivitylab.demoapp.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.Volley
import org.json.JSONArray
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

    interface OnDataSetReceivedListener {
        fun onDataSetReceived(jsonObject: JSONObject?, error: VolleyError?)
    }

    interface OnFullDataReceivedListener {
        fun onFullDataReceived(dataReceived: Boolean, error: VolleyError?)
    }

    fun getResources(path: String, limit: Int?, completionListener: OnGetResourceCompletedListener) {
        if (limit == null) {
            val request = Euro5JsonObjectRequest(Request.Method.GET,
                    BASE_URL + path, null,
                    object : Response.Listener<JSONObject> {
                        override fun onResponse(response: JSONObject?) {
                            completionListener.onComplete(response, null)
                        }
                    }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    completionListener.onComplete(null, error)
                }
            })

            this.requestQueue.add(request)
        } else {
            var resultJSONArray = JSONArray()

            this.getNewData(path, limit, 0, resultJSONArray,
                    object : OnDataSetReceivedListener {
                        override fun onDataSetReceived(jsonObject: JSONObject?, error: VolleyError?) {
                            if (jsonObject != null) {
                                completionListener.onComplete(jsonObject, null)
                            } else {
                                completionListener.onComplete(null, error)
                            }
                        }
                    })
        }
    }

    fun getNewData(path: String, limit: Int, offset: Int, resultJSONArray: JSONArray, onDataSetReceivedListener: OnDataSetReceivedListener) {
        var limitParameter = "&limit=" + limit
        var offsetParameter = "&offset=" + offset

        val request = Euro5JsonObjectRequest(
                Request.Method.GET,
                BASE_URL + path + limitParameter + offsetParameter, null,
                object : Response.Listener<JSONObject> {
                    override fun onResponse(response: JSONObject?) {
                        response?.let {
                            val jsonArray = it.optJSONArray("resource")

                            (0 until jsonArray.length()).forEach { item ->
                                resultJSONArray.put(jsonArray.getJSONObject(item))
                            }

                            if (jsonArray.length() < limit || jsonArray.length() == 0) {
                                var resultJSONObject = JSONObject()
                                resultJSONObject.put("resource", resultJSONArray)

                                onDataSetReceivedListener.onDataSetReceived(resultJSONObject, null)
                            } else {
                                this@RestAPIClient.getNewData(path, limit, offset + limit, resultJSONArray, onDataSetReceivedListener)
                            }
                        }
                    }
                }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {
                onDataSetReceivedListener.onDataSetReceived(null, error)
            }
        })

        this.requestQueue.add(request)
    }
}