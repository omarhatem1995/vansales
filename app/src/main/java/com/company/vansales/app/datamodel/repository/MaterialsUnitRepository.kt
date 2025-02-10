package com.company.vansales.app.datamodel.repository

import android.app.Application
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.MaterialsUnit
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.MaterialsUnitDAO
import com.company.vansales.app.datamodel.services.ClientService
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

class MaterialsUnitRepository(application: Application) {
    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    val materialsUnitDAO: MaterialsUnitDAO
    private var clientService = ClientService.getClient(application)
    val getMaterialsUnitLocale: Flow<List<MaterialsUnit>>

    init {
        materialsUnitDAO = appDB.getMaterialsUnit()
        getMaterialsUnitLocale = materialsUnitDAO.getMaterialsUnit()
    }

    fun upsert(materialsUnit: List<MaterialsUnit>) {
        materialsUnitDAO.upsert(materialsUnit)
    }
    fun update(materialsUnit: List<MaterialsUnit>) {
        materialsUnitDAO.updateMaterialUnits(materialsUnit)
    }

    fun getMaterialsUnitByPrimary(materialNumber: String, unit: String): List<MaterialsUnit> {
        return materialsUnitDAO.getMaterialsUnitByPrimary(materialNumber, unit)
    }

    fun getMaterialsUnitByMaterialNumber(materialNumber: String): List<MaterialsUnit> {
        return materialsUnitDAO.getMaterialsUnitByMaterialNumber(materialNumber)
    }

    fun getMaterialsUnitRemote(baseBody: BaseBody): Observable<BaseResponse<MaterialsUnit>> {
        return clientService.getMaterialsUnit(baseBody)
    }
}