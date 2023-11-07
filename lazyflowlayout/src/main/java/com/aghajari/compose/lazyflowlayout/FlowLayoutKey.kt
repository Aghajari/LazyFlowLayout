package com.aghajari.compose.lazyflowlayout

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

internal fun getDefaultFlowLayoutKey(index: Int): Any = DefaultFlowLayoutKey(index)

@SuppressLint("BanParcelableUsage")
private data class DefaultFlowLayoutKey(private val index: Int) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(index)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<DefaultFlowLayoutKey> =
            object : Parcelable.Creator<DefaultFlowLayoutKey> {
                override fun createFromParcel(parcel: Parcel) =
                    DefaultFlowLayoutKey(parcel.readInt())

                override fun newArray(size: Int) = arrayOfNulls<DefaultFlowLayoutKey?>(size)
            }
    }
}