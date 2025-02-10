package com.cic.vansales.datamodel.services.api

import android.app.Application
import com.company.vansales.app.datamodel.services.HttpClient
import com.company.vansales.app.datamodel.services.api.CreateDocumentGateway
import com.company.vansales.app.datamodel.services.core.networkresponsefactory.ApiResponseAdapterFactory
import com.company.vansales.app.utils.AppUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CreateDocumentGatewayProvider {
    fun provideGateWay(application: Application) : CreateDocumentGateway {
        return Retrofit.Builder()
            .baseUrl(AppUtils.urlBuilder(application))
            .client(HttpClient.getClient())
            .addCallAdapterFactory(ApiResponseAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CreateDocumentGateway::class.java)
    }
}