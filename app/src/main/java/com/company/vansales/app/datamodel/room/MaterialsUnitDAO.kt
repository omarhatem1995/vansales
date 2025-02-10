package com.company.vansales.app.datamodel.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.company.vansales.app.datamodel.models.mastermodels.Materials
import com.company.vansales.app.datamodel.models.mastermodels.MaterialsUnit
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MaterialsUnitDAO : BaseDao<MaterialsUnit>() {

    @Query("SELECT * FROM materials_unit")
    abstract fun getMaterialsUnit(): Flow<List<MaterialsUnit>>

    @Query("SELECT * FROM materials_unit WHERE materialNo = :materialNumber AND unit = :unit")
    abstract fun getMaterialsUnitByPrimary(materialNumber:String, unit: String):List<MaterialsUnit>

    @Query("SELECT * FROM materials_unit WHERE materialNo = :materialNumber AND unit = :unit")
    abstract fun getMaterialUnitByPrimary(materialNumber:String, unit: String):MaterialsUnit

    @Query("SELECT * FROM materials_unit WHERE materialNo = :materialNumber ")
    abstract fun getMaterialsUnit(materialNumber:String):List<MaterialsUnit>

    @Query("SELECT * FROM materials_unit WHERE materialNo = :materialNumber")
    abstract fun getMaterialsUnitByMaterialNumber(materialNumber:String):List<MaterialsUnit>

    @Query("DELETE FROM materials_unit")
    abstract fun deleteAllMaterialsUnit()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateMaterialUnits(materials: List<MaterialsUnit>)

}
