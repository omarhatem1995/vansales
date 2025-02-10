package com.company.vansales.app.datamodel.room

import androidx.room.Dao
import androidx.room.Query
import com.company.vansales.app.datamodel.models.mastermodels.TruckBatch
import com.company.vansales.app.datamodel.models.mastermodels.TruckItem

@Dao
abstract class TruckBatchDAO : BaseDao<TruckBatch>() {

    @Query("SELECT Count(1) FROM truck_batch WHERE materialNo = :materialNo AND salesOrg = :salesOrg  AND batch = :batch AND mtype =:type")
    abstract fun isBatchAlreadyExists(
        materialNo: String,
        salesOrg: String,
        batch: String,
        type: String
    ): Boolean

    @Query("UPDATE truck_batch SET available = available + :addedQuantity WHERE materialNo = :materialNo AND salesOrg = :salesOrg AND batch = :batch AND mtype = :mType ")
    abstract fun addToTruckBatches(
        addedQuantity: Double,
        materialNo: String,
        salesOrg: String,
        batch: String,
        mType: String
    )

    @Query("SELECT * FROM truck_batch WHERE materialNo = :materialNo AND mtype = :status AND salesOrg =:salesOrg ORDER BY expiryDate ASC ")
    abstract fun getBatchesByMaterialNo(
        materialNo: String,
        status: String,
        salesOrg: String
    ): List<TruckBatch>

    @Query("SELECT * FROM truck_batch WHERE materialNo = :materialNo AND mtype = :status OR materialNo = :materialNo  AND mtype = :status2 AND salesOrg =:salesOrg ORDER BY expiryDate ASC ")
    abstract fun getBatchesByMaterialNo(
        materialNo: String,
        status: String,
        status2 : String,
        salesOrg: String
    ): List<TruckBatch>

    @Query("SELECT * FROM truck_batch WHERE materialNo = :materialNo ORDER BY expiryDate ASC ")
    abstract fun getBatchesStockCount(
        materialNo: String
    ): List<TruckBatch>

    @Query("DELETE FROM truck_batch")
    abstract fun deleteAllBatches()

    @Query("SELECT * FROM truck_batch WHERE materialNo= :materialNo")
    abstract fun getMaterialsTruckBatchesOfSpecificItem(materialNo: String): TruckBatch?

    @Query("UPDATE truck_batch SET available = available - :subtractedQuantity WHERE materialNo = :materialNo AND salesOrg = :salesOrg AND batch = :batch AND mtype = :mType ")
    abstract fun subtractFromTruckBatches(
        subtractedQuantity: Double,
        materialNo: String,
        salesOrg: String,
        batch: String,
        mType: String
    )

    @Query("DELETE FROM truck_batch WHERE mtype = :batchType ")
    abstract fun deleteDamagedBatches(batchType : String)


}
