package com.company.vansales.app.di

import android.app.Application
import android.util.Log
import com.company.vansales.app.SAPWizardApplication.Companion.application
import com.company.vansales.app.datamodel.repository.*
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.services.api.*
import com.company.vansales.app.datamodel.services.core.networkresponsefactory.ApiResponseAdapterFactory
import com.company.vansales.app.datamodel.services.interceptor.DomainURLInterceptor
import com.company.vansales.app.utils.AppUtils
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiModule {



    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDataBase {
        return AppDataBase.getDatabase(application)!!
    }

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() =
        HttpLoggingInterceptor { message -> Log.e("http", message) }
            .apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        RetrofitUrlManager.getInstance().with(OkHttpClient.Builder())
            .addInterceptor(httpLoggingInterceptor)
            //.addInterceptor(getChangeURLInterceptor())
            .callTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun getChangeURLInterceptor(): DomainURLInterceptor {
        return DomainURLInterceptor()
    }



    private fun <T> provideService(
        okhttpClient: OkHttpClient,
        baseUrl: String,
        clazz: Class<T>
    ): T {
        return createRetrofit(okhttpClient, baseUrl).create(clazz)
    }

    private fun createRetrofit(
        okhttpClient: OkHttpClient,
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okhttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Singleton
    @Provides
    fun provideCreateCustomerApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        CreateCustomerGateway::class.java
    )

    @Singleton
    @Provides
    fun providesCreateCustomerRepository(apiService: CreateCustomerGateway) =
        CreateCustomerRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideGetDriverDataApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        DriverDataGateway::class.java
    )

    @Singleton
    @Provides
    fun providesGetDriverDataRepository(apiService: DriverDataGateway) =
        DriverDataRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideGetHelpApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        GetHelpGateway::class.java
    )

    @Singleton
    @Provides
    fun providesGetHelpRepository(apiService: GetHelpGateway) =
        GetHelpRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideGetTruckContentApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        GetTruckContentsGateway::class.java
    )

    @Singleton
    @Provides
    fun providesGetTruckContentRepository(apiService: GetTruckContentsGateway) =
        GetTruckContentRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideGetPricesApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        GetPricesGateway::class.java
    )

    @Singleton
    @Provides
    fun providesGetPricesRepository(apiService: GetPricesGateway) =
        GetPricesRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideGetPriceConditionsApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        GetPriceConditionsGateway::class.java
    )

    @Singleton
    @Provides
    fun providesGetPriceConditionsRepository(apiService: GetPriceConditionsGateway) =
        GetPriceConditionsRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideGetRoutesApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        GetRoutesGateway::class.java
    )

    @Provides
    @Singleton
    fun provideGetRoutesRepository(
        getRoutesGateway: GetRoutesGateway,
        appDB: AppDataBase
    ): GetRoutesRepositoryImpl {
        return GetRoutesRepositoryImpl(getRoutesGateway, appDB)
    }

    @Singleton
    @Provides
    fun provideLoginApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        LoginGateway::class.java
    )

    @Singleton
    @Provides
    fun providesLoginRepository(apiService: LoginGateway) =
        LoginRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideClerkLoginApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        LoginClerkGateway::class.java
    )

    @Singleton
    @Provides
    fun providesLoginClerkRepository(apiService: LoginClerkGateway) =
        LoginClerkRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideOpenInvoicesApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        GetOpenInvoicesGateway::class.java
    )

    @Singleton
    @Provides
    fun providesOpenInvoicesRepository(apiService: GetOpenInvoicesGateway) =
        OpenInvoicesRepositoryImpl(apiService)
    @Singleton
    @Provides
    fun provideCollectionsApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        CollectionsGateway::class.java
    )

    @Singleton
    @Provides
    fun providesCollectionsRepository(apiService: CollectionsGateway) =
        CollectionsRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideStorageLocApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        StorageLocGateway::class.java
    )

    @Singleton
    @Provides
    fun providesStorageLocRepository(apiService: StorageLocGateway) =
        StorageLocationRepositoryImpl(apiService)
    @Provides
    fun CreateDocumentApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        CreateDocumentGateway::class.java
    )

    @Singleton
    @Provides
    fun CreateDocumentRepository(apiService: CreateDocumentGateway) =
        CreateDocumentRepositoryImpl(apiService)

    @Provides
    fun CancelDocumentApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        CancelDocumentGateway::class.java
    )

    @Singleton
    @Provides
    fun CancelDocumentRepository(apiService: CancelDocumentGateway) =
        CancelDocumentRepositoryImpl(apiService)

    @Provides
    fun ApprovalsStatusApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        ApprovalsStatusGateway::class.java
    )

    @Singleton
    @Provides
    fun ApprovalsStatusRepository(apiService: ApprovalsStatusGateway) =
        ApprovalsStatusRepositoryImpl(apiService)

    @Provides
    fun BillToBillCheckApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        BillToBillCheckGateway::class.java
    )

    @Singleton
    @Provides
    fun BillToBillCheckRepository(apiService: BillToBillCheckGateway) =
        BillToBillCheckRepositoryImpl(apiService)

    @Provides
    fun RequestStatusApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        RequestStatusGateway::class.java
    )

    @Singleton
    @Provides
    fun RequestStatusRepository(apiService: RequestStatusGateway) =
        RequestStatusRepositoryImpl(apiService)

    @Provides
    fun VanSalesConfigApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        VanSalesConfigGateway::class.java
    )

    @Singleton
    @Provides
    fun VanSalesConfigRepository(apiService: VanSalesConfigGateway) =
        VanSalesConfigRepositoryImpl(apiService)

    @Provides
    fun GetPayersApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        PayersGateway::class.java
    )

    @Singleton
    @Provides
    fun GetPayersRepository(apiService: PayersGateway) =
        PayersRepositoryImpl(apiService)

    @Provides
    fun NumberRangeApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        NumberRangeGateway::class.java
    )

    @Singleton
    @Provides
    fun NumberRangeRepository(apiService: NumberRangeGateway) =
        NumberRangeRepositoryImpl(apiService)

    @Provides
    fun EndOfDayApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        EndOfDayGateway::class.java
    )

    @Singleton
    @Provides
    fun EndOfDayRepository(apiService: EndOfDayGateway) =
        EndOfDayRepositoryImpl(apiService)
    @Provides
    fun TaxesApiService(
        okhttpClient: OkHttpClient
    ) = provideService(
        okhttpClient,
        AppUtils.urlBuilder(application),
        TaxesGateway::class.java
    )

    @Singleton
    @Provides
    fun TaxesRepository(apiService: TaxesGateway) =
        TaxesRepositoryImpl(application,apiService)


}