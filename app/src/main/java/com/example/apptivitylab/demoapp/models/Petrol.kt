package com.example.apptivitylab.demoapp.models

import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ApptivityLab on 19/01/2018.
 */

class Petrol {
    var petrolID : String? = null
    var petrolName : String? = null
    var curPrice : Double? = null
    var prevPrices : ArrayList<Double> = ArrayList()
    var priceChangeDates : ArrayList<Date> = ArrayList()

}