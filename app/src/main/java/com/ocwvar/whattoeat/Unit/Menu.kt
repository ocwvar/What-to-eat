package com.ocwvar.whattoeat.Unit

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Project Whattoeat
 * Created by OCWVAR
 * On 17-6-25 下午11:15
 * File Location com.ocwvar.whattoeat.Unit
 * This file use to :   菜单对象
 */
data class Menu(val foods: ArrayList<Food>, val title: String, val message: String) : Parcelable {

    constructor(parcel: Parcel) : this(
            TODO("foods"),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Menu> {
        override fun createFromParcel(parcel: Parcel): Menu {
            return Menu(parcel)
        }

        override fun newArray(size: Int): Array<Menu?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        other ?: return false
        if (other is Menu) {
            return other.toString().equals(title)
        } else {
            return false
        }
    }

    override fun hashCode(): Int = title.hashCode()

    override fun toString(): String = title
}