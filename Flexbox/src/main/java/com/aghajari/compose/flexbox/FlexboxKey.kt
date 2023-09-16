package com.aghajari.compose.flexbox

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

fun getDefaultFlexboxKey(index: Int): Any = DefaultFlexboxKey(index)

@SuppressLint("BanParcelableUsage")
private data class DefaultFlexboxKey(private val index: Int) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(index)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<DefaultFlexboxKey> =
            object : Parcelable.Creator<DefaultFlexboxKey> {
                override fun createFromParcel(parcel: Parcel) =
                    DefaultFlexboxKey(parcel.readInt())

                override fun newArray(size: Int) = arrayOfNulls<DefaultFlexboxKey?>(size)
            }
    }
}