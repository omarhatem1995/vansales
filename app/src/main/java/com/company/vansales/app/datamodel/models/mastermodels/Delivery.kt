package com.company.vansales.app.datamodel.models.mastermodels

import android.os.Parcel
import android.os.Parcelable

data class Delivery(
    var salesOrg: String?,
    var creationDate: String?,
    var salesOrder: String?,
    var createBy: String?,
    var delivery: String?,
    var deliveryDate: String?,
    var deliveryType: String?,
    var totalAmount: Double?,
    var pickingStatus: String?,
    var totalRequested: Double?,
    var driver: String?,
    var customizeField: String?,
/*    var storageLocation : String?,
    var plant : String?*/
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        /*parcel.readString(),
        parcel.readString()*/
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(salesOrg)
        parcel.writeString(creationDate)
        parcel.writeString(salesOrder)
        parcel.writeString(createBy)
        parcel.writeString(delivery)
        parcel.writeString(deliveryDate)
        parcel.writeString(deliveryType)
        parcel.writeValue(totalAmount)
        parcel.writeString(pickingStatus)
        parcel.writeValue(totalRequested)
        parcel.writeString(driver)
        parcel.writeString(customizeField)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Delivery> {
        override fun createFromParcel(parcel: Parcel): Delivery {
            return Delivery(parcel)
        }

        override fun newArray(size: Int): Array<Delivery?> {
            return arrayOfNulls(size)
        }
    }
}