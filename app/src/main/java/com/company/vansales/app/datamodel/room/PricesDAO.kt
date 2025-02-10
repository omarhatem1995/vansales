package com.company.vansales.app.datamodel.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.company.vansales.app.datamodel.models.mastermodels.ItemPriceCondition
import com.company.vansales.app.datamodel.models.mastermodels.Prices

@Dao
abstract class PricesDAO : BaseDao<Prices>() {

    @Query("SELECT * FROM prices")
    abstract fun getPrices(): LiveData<List<Prices>>

    @Query("SELECT * FROM prices WHERE materialNo = :materialNumber AND customer = :customerId AND distChannel = :distChannel")
    abstract fun getPricesByPrimary(materialNumber: String,customerId: String?,distChannel: String): List<Prices>

    @Query("SELECT * FROM prices WHERE materialNo = :materialNumber AND customer = :customerId AND distChannel = :distChannel")
    abstract fun getMaterialPriceByPrimary(materialNumber: String,customerId: String?,distChannel: String): Prices

    @Query("DELETE FROM prices")
    abstract fun deleteAllPrices()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updatePrices(materials: List<Prices>)

    @Delete
    abstract fun deletePrices(priceConditions: List<Prices>)

    @Query("SELECT * FROM prices WHERE materialNo =:materialNumber")
    abstract fun get1SEP(materialNumber : String): List<Prices>

}