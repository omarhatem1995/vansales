package com.company.vansales.app.datamodel.room

import androidx.room.Dao
import androidx.room.Query
import com.company.vansales.app.datamodel.models.mastermodels.Taxes
import com.company.vansales.app.datamodel.room.BaseDao

@Dao
abstract class TaxesDAO : BaseDao<Taxes>() {


    @Query("SELECT * FROM taxes")
    abstract fun getAllTaxes(): List<Taxes>

    @Query("SELECT * FROM taxes WHERE customerCode =:customerCode AND materialCode =:materialCode AND " +
            "condition =:condition")
    abstract fun getTaxesForCustomerAndMaterial(customerCode : String,
                                                materialCode : String,
                                                condition: String): Taxes

    @Query("UPDATE taxes SET condition = :condition AND condValue =:condValue AND " +
            "condUnit =:condUnit WHERE materialCode = :materialCode AND " +
            "customerCode =:customerCode ")
    abstract fun updateTaxes(
        condition: String,
        condValue: String,
        condUnit: String,
        materialCode: String,
        customerCode: String
    )

    @Query("DELETE FROM taxes")
    abstract fun deleteAllTaxes()
}