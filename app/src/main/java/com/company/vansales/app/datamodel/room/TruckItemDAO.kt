package com.company.vansales.app.datamodel.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.company.vansales.app.datamodel.models.mastermodels.TruckItem
import com.company.vansales.app.utils.Constants

@Dao
abstract class TruckItemDAO : BaseDao<TruckItem>() {

    @Query("SELECT * FROM truck_item")
    abstract fun getTruckItemsLiveData(): LiveData<List<TruckItem>>

    @Query("SELECT * FROM truck_item")
    abstract fun getAllTruckItems(): List<TruckItem>

    @Query("SELECT * FROM truck_item WHERE mtype = :type")
    abstract fun getMaterialsOfDamaged(type :String = Constants.MATERIAL_DAMAGED): List<TruckItem>

    @Query("SELECT * FROM truck_item WHERE materialNo= :materialNo AND mtype = :type")
    abstract fun getMaterialsTruckItemsSellableOfSpecificItem(materialNo: String,type :String = Constants.MATERIAL_CATEGORY_SELLABLE): TruckItem

    @Query("SELECT * FROM truck_item WHERE mtype = :type")
    abstract fun getMaterialsSellable(type :String = Constants.MATERIAL_CATEGORY_SELLABLE): List<TruckItem>

    @Query("SELECT * FROM truck_item WHERE mtype = :type ")
    abstract fun getMaterialsByType(type: String): List<TruckItem>

    @Query("SELECT * FROM truck_item WHERE salesOrg = :salesOrg AND mtype = :type ")
    abstract fun getTruckItemsBySalesOrg(salesOrg: String?, type: String?): List<TruckItem>

    @Query("SELECT * FROM truck_item WHERE  materialNo = :materialNo ")
    abstract fun getTruckItemByMaterialNumber(materialNo: String): List<TruckItem>

    @Query("SELECT * FROM truck_item WHERE  barcode = :barcode ")
    abstract fun getTruckItemByBarCode(barcode: String): TruckItem

    @Query("SELECT Count(1) FROM truck_item WHERE materialNo = :materialNo AND salesOrg = :salesOrg AND mtype = :type ")
    abstract fun isItemAlreadyExists(materialNo: String, salesOrg: String, type: String): Boolean

    @Query("DELETE FROM truck_item")
    abstract fun deleteAllItems()

    @Query("UPDATE truck_item SET available = available - :subtractedQuantity WHERE materialNo = :materialNo AND salesOrg = :salesOrg AND mtype = :mType")
    abstract fun subtractFromTruckMaterials(
        subtractedQuantity: Double,
        materialNo: String,
        salesOrg: String,
        mType: String
    )

    @Query("UPDATE truck_item SET available = available + :addedQuantity WHERE materialNo = :materialNo AND salesOrg = :salesOrg AND mtype = :mType")
    abstract fun addToTruckMaterials(
        addedQuantity: Double,
        materialNo: String,
        salesOrg: String,
        mType: String
    )


    @Query("DELETE FROM truck_item WHERE mtype = :itemType ")
    abstract fun deleteDamagedItems(itemType: String)


}
