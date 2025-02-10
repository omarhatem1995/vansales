package com.company.vansales.app.datamodel.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.company.vansales.app.datamodel.models.localmodels.*
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocHeader
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocItemBatch
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocItems
import com.company.vansales.app.datamodel.models.mastermodels.*
import com.company.vansales.app.datamodel.room.ApplicationSyncTimeStampDAO
import com.company.vansales.app.datamodel.room.CustomersDAO
import com.company.vansales.app.datamodel.room.InvoiceHeaderDAO
import com.company.vansales.app.datamodel.room.InvoiceNumberDAO
import com.company.vansales.app.datamodel.room.ItemPriceConditionDAO
import com.company.vansales.app.datamodel.room.MaterialsDAO
import com.company.vansales.app.datamodel.room.MaterialsUnitDAO
import com.company.vansales.app.datamodel.room.PricesDAO
import com.company.vansales.app.datamodel.room.SalesDocHeaderBatchDAO
import com.company.vansales.app.datamodel.room.SalesDocHeaderDAO
import com.company.vansales.app.datamodel.room.SalesDocHeaderItemsDAO
import com.company.vansales.app.datamodel.room.TaxesDAO
import com.company.vansales.app.datamodel.room.TransactionsHistoryDAO
import com.company.vansales.app.datamodel.room.TruckBatchDAO
import com.company.vansales.app.datamodel.room.TruckItemDAO
import com.company.vansales.app.datamodel.room.VisitsDAO
import com.company.vansales.app.datamodel.models.localmodels.ApplicationConfig
import com.company.vansales.app.datamodel.models.localmodels.ApplicationSettings
import com.company.vansales.app.datamodel.models.localmodels.InvoiceHeader
import com.company.vansales.app.datamodel.models.localmodels.InvoiceNumber
import com.company.vansales.app.datamodel.models.mastermodels.HelpView
import com.company.vansales.app.datamodel.models.mastermodels.ItemPriceCondition
import com.company.vansales.app.datamodel.models.mastermodels.Prices
import com.company.vansales.app.datamodel.models.mastermodels.Routes
import com.company.vansales.app.datamodel.models.mastermodels.Taxes

@Database(
    entities = [
        ApplicationConfig::class,
        ApplicationSyncTimeStamp::class,
        ApplicationSettings::class,
        TransactionsHistory::class,
        Customer::class,
        Materials::class,
        MaterialsUnit::class,
        TruckBatch::class,
        TruckItem::class,
        Routes::class,
        ItemPriceCondition::class,
        Prices::class,
        Visits::class,
        HelpView::class,
        InvoiceNumber::class,
        SalesDocHeader::class,
        SalesDocItemBatch::class,
        SalesDocItems::class,
        InvoiceHeader::class,
        Taxes::class,
    ],
    version = 2
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getApplicationSettings(): ApplicationSettingsDAO
    abstract fun getAppConfig(): ApplicationConfigDAO
    abstract fun getCustomers(): CustomersDAO
    abstract fun getTransactionsHistory(): TransactionsHistoryDAO
    abstract fun getMaterials(): MaterialsDAO
    abstract fun getMaterialsUnit(): MaterialsUnitDAO
    abstract fun getTruckBatch(): TruckBatchDAO
    abstract fun getTruckItem(): TruckItemDAO
    abstract fun getRoutes(): RoutesDAO
    abstract fun getItemPriceConditions(): ItemPriceConditionDAO
    abstract fun getPrices(): PricesDAO
    abstract fun getVisits(): VisitsDAO
    abstract fun getHelpView(): HelpViewDAO
    abstract fun getSalesDocHeader(): SalesDocHeaderDAO
    abstract fun getSalesDocItems(): SalesDocHeaderItemsDAO
    abstract fun getSalesDocBatches(): SalesDocHeaderBatchDAO
    abstract fun getInvoiceNumber() : InvoiceNumberDAO
    abstract fun getInvoiceHeader() : InvoiceHeaderDAO
    abstract fun getTaxes() : TaxesDAO
    abstract fun getApplicationSyncTimeStamp() : ApplicationSyncTimeStampDAO

    companion object {
        private const val DB_NAME = "VanSales"
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase? {
            if (INSTANCE == null) {
                synchronized(AppDataBase::class.java) {

                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java, DB_NAME
                    )
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }
    }


}