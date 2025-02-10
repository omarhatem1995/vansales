package com.company.vansales.app.datamodel.models.localmodels.salesdocmodels

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity


@Entity(tableName = "sales_doc_item", primaryKeys = ["exInvoice","itemNum"])
data class SalesDocItems(
    var exInvoice: String,
    var itemNum: Int,
    var material: String?,
    var description: String?,
    var descriptionArabic: String?,
    var quantity: Double?,
    var price: Double?,
    var discount: Double?,
    var unit: String?,
    var itemCategory: String?,
    var totalPrice: Double?,
    var currency: String?,
    var mType : String?,
    var storageLocation : String? = "",
    var plant : String? = ""

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(exInvoice)
        parcel.writeInt(itemNum)
        parcel.writeString(material)
        parcel.writeString(description)
        parcel.writeString(descriptionArabic)
        parcel.writeValue(quantity)
        parcel.writeValue(price)
        parcel.writeValue(discount)
        parcel.writeString(unit)
        parcel.writeString(itemCategory)
        parcel.writeValue(totalPrice)
        parcel.writeString(currency)
        parcel.writeString(mType)
        parcel.writeString(storageLocation)
        parcel.writeString(plant)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SalesDocItems> {
        override fun createFromParcel(parcel: Parcel): SalesDocItems {
            return SalesDocItems(parcel)
        }

        override fun newArray(size: Int): Array<SalesDocItems?> {
            return arrayOfNulls(size)
        }
    }
}