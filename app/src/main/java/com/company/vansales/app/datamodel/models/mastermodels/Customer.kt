package com.company.vansales.app.datamodel.models.mastermodels

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity


@Entity(tableName = "customers", primaryKeys = ["salesOrg", "distChannel","customer"])
data class Customer(
    var salesOrg: String,
    var distChannel: String,
    var customer: String,
    var address: String?,
    var region: String?,
    var currency: String?,
    var name1: String?,
    var customizeField:String?,
    var printOption : String?,
    var paymentTerm: String?,
    var latitude: Double?,
    var nameArabic:String?,
    var longitude: Double?,
    var city:String?,
    var credit: Double?,
    var regionArabic: String?,
    var addressArabic: String?,
    var mobile: String?,
    var telephone: String?,
    var yvatTax: String?,
    var yexcTax: String?,
    var operation: String?,

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
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
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(salesOrg)
        parcel.writeString(distChannel)
        parcel.writeString(customer)
        parcel.writeString(address)
        parcel.writeString(region)
        parcel.writeString(currency)
        parcel.writeString(name1)
        parcel.writeString(customizeField)
        parcel.writeString(printOption)
        parcel.writeString(paymentTerm)
        parcel.writeValue(latitude)
        parcel.writeString(nameArabic)
        parcel.writeValue(longitude)
        parcel.writeString(city)
        parcel.writeValue(credit)
        parcel.writeString(regionArabic)
        parcel.writeString(addressArabic)
        parcel.writeString(mobile)
        parcel.writeString(telephone)
        parcel.writeString(yvatTax)
        parcel.writeString(yexcTax)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Customer> {
        override fun createFromParcel(parcel: Parcel): Customer {
            return Customer(parcel)
        }

        override fun newArray(size: Int): Array<Customer?> {
            return arrayOfNulls(size)
        }
    }
}