package com.company.vansales.app.datamodel.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.company.vansales.app.datamodel.models.mastermodels.Customer
import com.company.vansales.app.datamodel.models.mastermodels.Materials

@Dao
abstract class MaterialsDAO : BaseDao<Materials>() {

    @Query("SELECT * FROM materials")
    abstract fun getMaterials(): List<Materials>

    @Query("SELECT * FROM materials WHERE salesOrg = :salesOrg AND distChannel = :distChannel")
    abstract fun getAllMaterials(salesOrg: String, distChannel : String): List<Materials>

    @Query("SELECT * FROM materials WHERE materialNo = :materialNo")
    abstract fun getMaterialByMaterialNo(materialNo: String): Materials

    @Query("SELECT * FROM materials WHERE materialNo = :materialNo AND distChannel = :distChannel")
    abstract fun getMaterialByMaterialNumberAndDistChannel(materialNo: String , distChannel: String): Materials

    @Query("SELECT * FROM materials WHERE materialNo = :materialNo AND distChannel = :distChannel")
    abstract fun getTaxesForMaterial(materialNo: String , distChannel: String): Materials

    @Query("SELECT * FROM materials WHERE barcode = :barcode")
    abstract fun getMaterialByBarcode(barcode: String): Materials

    @Query("SELECT * FROM materials WHERE materialNo = :materialNo AND salesOrg = :salesOrg AND distChannel = :distChannel")
    abstract fun getMaterialsByPrimary(materialNo: String, salesOrg: String, distChannel : String?): List<Materials>

    @Query("SELECT * FROM materials WHERE materialNo = :materialNo AND salesOrg = :salesOrg AND distChannel = :distChannel")
    abstract fun getMaterialByPrimary(materialNo: String, salesOrg: String, distChannel : String?): Materials

    @Query("DELETE FROM materials")
    abstract fun deleteAllMaterials()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateMaterials(materials: List<Materials>)

}
