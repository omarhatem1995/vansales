package com.company.vansales.app.datamodel.services

import android.app.Application
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Customer
import com.company.vansales.app.datamodel.models.mastermodels.DeliveriesBody
import com.company.vansales.app.datamodel.models.mastermodels.Delivery
import com.company.vansales.app.datamodel.models.mastermodels.DeliveryCheckResponse
import com.company.vansales.app.datamodel.models.mastermodels.HelpView
import com.company.vansales.app.datamodel.models.mastermodels.ItemPriceCondition
import com.company.vansales.app.datamodel.models.mastermodels.LoginRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.LoginResponseModel
import com.company.vansales.app.datamodel.models.mastermodels.Materials
import com.company.vansales.app.datamodel.models.mastermodels.MaterialsUnit
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.datamodel.models.mastermodels.Prices
import com.company.vansales.app.datamodel.models.mastermodels.Routes
import com.company.vansales.app.datamodel.models.mastermodels.TruckResponse
import com.company.vansales.app.datamodel.models.mastermodels.Visits
import com.company.vansales.app.datamodel.models.mastermodels.VisitsBody
import com.company.vansales.app.utils.AppUtils
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface ClientService {

    companion object {
        fun getClient(application: Application): ClientService {
            val retrofit = Retrofit.Builder()
                .baseUrl(AppUtils.urlBuilder(application))
                .client(HttpClient.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            return retrofit.create(ClientService::class.java)
        }

        fun createServiceFiveMinutesTimeOut(application: Application): ClientService {
            val retrofit = Retrofit.Builder()
                .baseUrl(AppUtils.urlBuilder(application))
                .client(HttpClient.getClientFiveMinutesTimeOut())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            return retrofit.create(ClientService::class.java)
        }


    }

    @POST("HelpView/find")
    fun getHelpView(@Body baseBody: BaseBody):
            Observable<BaseResponse<HelpView>?>

    @POST("Customers/find")
    fun getCustomers(@Body baseBody: BaseBody):
            Observable<BaseResponse<Customer>>

    @Streaming
    @POST("Materials/find")
    fun getMaterials(@Body baseBody: BaseBody):
            Observable<BaseResponse<Materials>>

    @POST("MaterialUnit/find")
    fun getMaterialsUnit(@Body baseBody: BaseBody):
            Observable<BaseResponse<MaterialsUnit>>

    @POST("Truck/find")
    fun getTruckContent(@Body baseBody: BaseBody):
            Observable<TruckResponse>

    @POST("Routes/find")
    fun getRoutes(@Body baseBody: BaseBody):
            Observable<BaseResponse<Routes>>

    @Streaming
    @POST("Conditions/find")
    fun getItemPriceCondition(@Body baseBody: BaseBody):
            Observable<BaseResponse<ItemPriceCondition>>

    @Streaming
    @POST("Prices/find")
    fun getPrices(@Body baseBody: BaseBody):
            Observable<BaseResponse<Prices>>

    @POST("Visits/findByRoute")
    fun getVisits(@Body visitsBody: VisitsBody):
            Observable<BaseResponse<Visits>>



    @POST("Delivery/find")
    fun getDeliveries(@Body baseBody: BaseBody):
            Observable<BaseResponse<Delivery>>

    @POST("Delivery/findItem")
    fun getDeliveryCheck(@Body deliveriesBody: DeliveriesBody):
            Observable<DeliveryCheckResponse>

    @POST("Document/create")
    fun createDocument(@Body salesDocBody: SalesDocBody):
            Observable<BaseResponse<PostDocumentResponse>>

    @POST("login/check")
    suspend fun login(@Body loginRequestModel: LoginRequestModel):
            ApiResponse<LoginResponseModel, Error>


}