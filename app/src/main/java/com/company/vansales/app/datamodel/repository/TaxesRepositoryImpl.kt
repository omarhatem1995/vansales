package com.company.vansales.app.datamodel.repository

import android.app.Application
import android.util.Log
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Taxes
import com.company.vansales.app.datamodel.models.mastermodels.TaxesRequestModel
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.TaxesDAO
import com.company.vansales.app.datamodel.services.api.TaxesGateway
import com.company.vansales.app.domain.repos.TaxesRepo

class TaxesRepositoryImpl (
    val application: Application,
    private val taxesGateway: TaxesGateway,
) : TaxesRepo {
    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    private val taxesDAO: TaxesDAO =
         appDB.getTaxes()

    override suspend fun getTaxes(taxesRequest: TaxesRequestModel):
            ApiResponse<BaseResponse<Taxes>, Error> {
        Log.d("upsertTaxes" , " ${taxesRequest}")
        return taxesGateway.getTaxes(taxesRequest)
    }

    override suspend fun upsertTaxes(taxes: List<Taxes>) {
        Log.d("upsertTaxes" , " ${taxes.size}")
        taxesDAO.upsert(taxes)
    }


}