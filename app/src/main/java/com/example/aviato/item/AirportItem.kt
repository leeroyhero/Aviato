package com.example.aviato.item

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class AirportItem (val fullname: String, val country: String, val iata:String, val latLng:LatLng):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(LatLng::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fullname)
        parcel.writeString(country)
        parcel.writeString(iata)
        parcel.writeParcelable(latLng, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AirportItem> {
        override fun createFromParcel(parcel: Parcel): AirportItem {
            return AirportItem(parcel)
        }

        override fun newArray(size: Int): Array<AirportItem?> {
            return arrayOfNulls(size)
        }
    }
}