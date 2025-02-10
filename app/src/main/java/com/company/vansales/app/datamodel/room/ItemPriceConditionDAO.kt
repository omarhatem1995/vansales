package com.company.vansales.app.datamodel.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.company.vansales.app.datamodel.models.mastermodels.ItemPriceCondition

@Dao
abstract class ItemPriceConditionDAO : BaseDao<ItemPriceCondition>() {

    @Query("SELECT * FROM item_price_condition WHERE customer = :customer")
    abstract fun getItemPriceConditions(customer : String ): LiveData<List<ItemPriceCondition>>


    @Query("SELECT * FROM item_price_condition" +
            " WHERE materialNo = :materialNumber" +
            " AND customer = :customerId AND distChannel = :distChannel AND salesOrg =:salesOrg")
    abstract fun getItemPriceConditionsByPrimary(
        materialNumber: String,
        customerId: String?,
        distChannel: String?,
        salesOrg : String?
    ): List<ItemPriceCondition>

    @Query("SELECT * FROM item_price_condition" +
            " WHERE condition = :materialNumber" +
            " AND distChannel = :distChannel AND salesOrg =:salesOrg")
    abstract fun getItemPriceConditionByPrimary(
        materialNumber: String,
        distChannel: String?,
        salesOrg : String?
    ): List<ItemPriceCondition>

    @Query("SELECT * FROM item_price_condition WHERE customer = :customerId AND distChannel = :distChannel AND salesOrg =:salesOrg")
    abstract fun getItemPriceConditionsHeader(
        customerId: String?,
        distChannel: String?,
        salesOrg : String?
    ): List<ItemPriceCondition>

    @Query("DELETE FROM item_price_condition")
    abstract fun deleteAllConditions()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updatePriceConditions(itemPriceCondition: List<ItemPriceCondition>)

    @Delete
    abstract fun deletePriceConditions(priceConditions: List<ItemPriceCondition>)

    @Query("DELETE FROM item_price_condition WHERE customer =:customer AND distChannel =:distChannel AND salesOrg =:salesOrg AND materialNo =:materialNo " +
            "AND condition =:condition AND condCounter=:condCounter")
    abstract fun deleteItemCondition(customer:String,distChannel:String,
                                     salesOrg:String,
                                     materialNo:String,
                                     condition:String,
                                     condCounter:Int)

}