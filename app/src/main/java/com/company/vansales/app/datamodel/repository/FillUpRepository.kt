package com.company.vansales.app.datamodel.repository

import android.app.Application
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.DeliveriesBody
import com.company.vansales.app.datamodel.models.mastermodels.Delivery
import com.company.vansales.app.datamodel.models.mastermodels.DeliveryCheckResponse
import com.company.vansales.app.datamodel.services.ClientService
import io.reactivex.Observable

class FillUpRepository(application: Application) {

    private var clientService = ClientService.getClient(application)
    fun getDeliveriesListRemote(baseBody: BaseBody): Observable<BaseResponse<Delivery>> {
        return clientService.getDeliveries(baseBody)
    }

    fun getDeliveriesCheckRemote(deliveriesBody: DeliveriesBody) : Observable<DeliveryCheckResponse>{
        return clientService.getDeliveryCheck(deliveriesBody)

    }

}