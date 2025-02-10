package com.company.vansales.app.datamodel.models.localmodels.salesdocmodels

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity

@Entity(tableName = "sales_doc_header", primaryKeys = ["exInvoice"])
data class SalesDocHeader(
    var distChannel : String?,
    var salesOrg : String?,
    var exInvoice: String,
    var erpInvoice: String?,
    var customer: String?,
    var docType: String?,
    var docStatus: String?,
    var totalValue: String?,
    var totalDiscount: String?,
    var plant: String?=null,
    var location: String?=null,
    var visitListId: String?,
    var visitItemNumber: String?,
    var latitude: Double?,
    var longitude: Double?,
    var driver: String?,
    var costumer : String?,
    var paymentTerm: String?,
    var visitId:String?,
    var visitItem : String?,
    var creationDate : String?,
    var creationTime : String?,
    var secondExInvoice : String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
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
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(distChannel)
        parcel.writeString(salesOrg)
        parcel.writeString(exInvoice)
        parcel.writeString(erpInvoice)
        parcel.writeString(customer)
        parcel.writeString(docType)
        parcel.writeString(docStatus)
        parcel.writeString(totalValue)
        parcel.writeString(totalDiscount)
        parcel.writeString(visitListId)
        parcel.writeString(visitItemNumber)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
        parcel.writeString(driver)
        parcel.writeString(costumer)
        parcel.writeString(plant)
        parcel.writeString(location)
        parcel.writeString(paymentTerm)
        parcel.writeString(visitId)
        parcel.writeString(visitItem)
        parcel.writeString(creationDate)
        parcel.writeString(creationTime)
        parcel.writeString(secondExInvoice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SalesDocHeader> {
        override fun createFromParcel(parcel: Parcel): SalesDocHeader {
            return SalesDocHeader(parcel)
        }

        override fun newArray(size: Int): Array<SalesDocHeader?> {
            return arrayOfNulls(size)
        }
    }
}