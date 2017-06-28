package com.ocwvar.whattoeat.Unit

import android.os.Parcel
import android.os.Parcelable

/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-6-25 下午11:13
 * File Location com.ocwvar.whattoeat.Unit
 * This file use to :   食物对象
 */
data class Food(val title: String, val message: String?, val icon: String?) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeString(icon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Food> {
        override fun createFromParcel(parcel: Parcel): Food {
            return Food(parcel)
        }

        override fun newArray(size: Int): Array<Food?> {
            return arrayOfNulls(size)
        }
    }

}