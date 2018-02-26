package com.example.apptivitylab.demoapp.models

import android.os.Parcel
import android.os.Parcelable
import com.example.apptivitylab.demoapp.controllers.BrandController
import com.example.apptivitylab.demoapp.controllers.PetrolTypeController
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ApptivityLab on 12/01/2018.
 */

class User() : Parcelable {
    var userID: String? = null
    var username: String? = null
    var password: String? = null
    var email: String? = null
    var phoneNo: String? = null
    var userCreatedAt: Date? = null
    var preferredPetrolType: PetrolType? = null
    var preferredBrands: ArrayList<Brand> = ArrayList()

    constructor(parcel: Parcel) : this() {
        this.userID = parcel.readString()
        this.username = parcel.readString()
        this.password = parcel.readString()
        this.email = parcel.readString()
        this.phoneNo = parcel.readString()
        this.userCreatedAt = Date(parcel.readLong())
        this.preferredPetrolType = parcel.readParcelable(PetrolType::class.java.classLoader)
        this.preferredBrands = parcel.readArrayList(Brand::class.java.classLoader) as ArrayList<Brand>
    }

    constructor(jsonObject: JSONObject) : this() {
        this.userID = jsonObject.optString("uuid")
        this.username = jsonObject.optString("name")
        this.password = jsonObject.optString("password")
        this.email = jsonObject.optString("email")
        this.phoneNo = jsonObject.optString("phone")
        this.userCreatedAt = (SimpleDateFormat("yyyy-MM-dd kk:mm:ss").parse(jsonObject.optString("created_at")))
    }

    fun assignUserPreferences() {
        var preferredBrandIDs: ArrayList<String> = ArrayList()
        this.preferredBrands.forEach { brand ->
            brand.brandID?.let {
                preferredBrandIDs.add(it)
            }
        }

        this.preferredPetrolType = PetrolTypeController.petrolTypeList.firstOrNull { petrolType ->
            petrolType.petrolID == this.preferredPetrolType?.petrolID
        }

        this.preferredBrands.clear()

        preferredBrandIDs.forEach { brandID ->
            this.preferredBrands.add(BrandController.brandList.first { brand ->
                brand.brandID == brandID
            })
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(this.userID)
        parcel.writeString(this.username)
        parcel.writeString(this.password)
        parcel.writeString(this.email)
        parcel.writeString(this.phoneNo)

        this.userCreatedAt?.let {
            parcel.writeLong(it.time)
        }

        parcel.writeParcelable(this.preferredPetrolType, flags)
        parcel.writeList(this.preferredBrands)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}