package com.company.vansales.app.datamodel.models.mastermodels

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity

@Entity(tableName = "truck_item", primaryKeys = ["salesOrg", "materialNo","mtype"])
data class TruckItem(
    var salesOrg: String,
    var isSellable: String?,
    var description: String?,
    var materialNo: String,
    var customizeField: String?,
    var barcode: String?,
    var unit: String?,
    var plant: String?,
    var mtype: String,
    var total: Double?,
    var materialGroup: String?,
    var groupDesc: String?,
    var available: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(salesOrg)
        parcel.writeString(isSellable)
        parcel.writeString(description)
        parcel.writeString(materialNo)
        parcel.writeString(customizeField)
        parcel.writeString(barcode)
        parcel.writeString(unit)
        parcel.writeString(plant)
        parcel.writeString(mtype)
        parcel.writeValue(total)
        parcel.writeString(materialGroup)
        parcel.writeString(groupDesc)
        parcel.writeString(available)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TruckItem> {
        override fun createFromParcel(parcel: Parcel): TruckItem {
            return TruckItem(parcel)
        }

        override fun newArray(size: Int): Array<TruckItem?> {
            return arrayOfNulls(size)
        }
    }
}