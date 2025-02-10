package com.company.vansales.app.datamodel.models.mastermodels

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity

@Entity(tableName = "visits", primaryKeys = ["visitListID", "visitItemNo"])
data class Visits(
    var sequence: Int?,
    var customizeField: String?,
    var route: String?,
    var division: String?,
    var excutionDate: String?,
    var visitListID: String,
    var visitPlan: String?,
    var dist_channel: String?,
    var customerNo: String?,
    var visitItemNo: Int,
    var driver: String?,
    var salesOrg: String?,
    var visitType : String?,
    var visitStatus: String,
    var customerName: String?,
    var customerNameArabic: String?,
    var customerRegion: String?,
    var customerRegionArabic: String?,
    var customerCity: String?,
    var fieldValue: String?,
    var comment : String?,
    var startDayDate : String?,
    var startDayTime : String?,
    var endDayDate : String?,
    var endDayTime : String?,
    var startLatitude : Double?,
    var startLongitude : Double?,
    var endLatitude : Double?,
    var endLongitude : Double?,

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(sequence)
        parcel.writeString(customizeField)
        parcel.writeString(route)
        parcel.writeString(division)
        parcel.writeString(excutionDate)
        parcel.writeString(visitListID)
        parcel.writeString(visitPlan)
        parcel.writeString(dist_channel)
        parcel.writeString(customerNo)
        parcel.writeInt(visitItemNo)
        parcel.writeString(driver)
        parcel.writeString(salesOrg)
        parcel.writeString(visitType)
        parcel.writeString(visitStatus)
        parcel.writeString(customerName)
        parcel.writeString(customerNameArabic)
        parcel.writeString(customerRegion)
        parcel.writeString(customerRegionArabic)
        parcel.writeString(customerCity)
        parcel.writeString(fieldValue)
        parcel.writeString(comment)
        parcel.writeString(startDayDate)
        parcel.writeString(startDayTime)
        parcel.writeString(endDayDate)
        parcel.writeString(endDayTime)
        startLatitude?.let { parcel.writeDouble(it) }
        startLongitude?.let { parcel.writeDouble(it) }
        endLatitude?.let { parcel.writeDouble(it) }
        endLongitude?.let { parcel.writeDouble(it) }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Visits> {
        override fun createFromParcel(parcel: Parcel): Visits {
            return Visits(parcel)
        }

        override fun newArray(size: Int): Array<Visits?> {
            return arrayOfNulls(size)
        }
    }
}