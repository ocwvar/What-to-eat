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
/**
 * 食物对象
 * @param   title           名称
 * @param   message         信息
 * @param   icon            图像网址
 * @param   extraMessage    额外信息，此属性用于Record中使用，并不需要手动设置会自动生成，默认为NULL
 */
data class Food(val title: String, val message: String? = null, val icon: String? = null, val extraMessage: String? = null) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeString(icon)
        parcel.writeString(extraMessage)
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