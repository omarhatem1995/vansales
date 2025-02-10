package com.company.vansales.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderBatches
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderItems
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ISalesDocHeader
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.Delivery
import com.company.vansales.app.datamodel.models.mastermodels.Materials
import com.company.vansales.app.datamodel.models.mastermodels.TruckItem
import com.company.vansales.app.datamodel.repository.MaterialsRepository
import com.company.vansales.app.datamodel.repository.TruckManagementRepository
import com.company.vansales.app.datamodel.sharedpref.GetSharedPreferences
import com.company.vansales.app.utils.Constants.NUMBER_OF_RETRIES
import com.company.vansales.app.utils.Constants.STATUS_ERROR
import com.company.vansales.app.utils.Constants.STATUS_LOADED
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TruckManagementViewModel(application: Application) :
    AndroidViewModel(application) {

    private val mRepository: TruckManagementRepository = TruckManagementRepository(application)
    val getTruckItems: LiveData<List<TruckItem>> by lazy {
        mRepository.getTruckItems
    }
    private val materialsRepository : MaterialsRepository = MaterialsRepository(application)
    var compositeDisposable = CompositeDisposable()
    var sharedPref = GetSharedPreferences(application)

    //Truck Content Error Handling
    private val truckContentStatusMutableLiveData = MutableLiveData<String>()
    val truckContentStatusLiveData: LiveData<String>
        get() = truckContentStatusMutableLiveData
    val myExecutor: ExecutorService = Executors.newSingleThreadExecutor()


    fun getAllMaterialTruckItems(): List<TruckItem> {
        return  mRepository.getMaterialTruckItems()
    }

    fun getSellable(): ArrayList<TruckItem> {
        var materialList = ArrayList<TruckItem>()
        mRepository.getSellable().forEach { truckItem ->
            Log.d("getTruckMaterialsList", "${mRepository.getSellable().size}")
            val material = mRepository.getMaterialByMaterialNo(truckItem.materialNo)

            // Check if the material is not null before accessing its properties
            if (material != null) {
                if(truckItem.materialGroup != null && material.materialGrp != null) {
                    try {
                        truckItem.materialGroup = material.materialGrp
                        materialList.add(truckItem)
                    }catch (exception : Exception){
                        Log.d("getException" , "$exception")
                    }
                }
            } else {
                // Handle the case when material is null, if needed
                // For example, you might want to log a message or take some other action
            }
        }
        return materialList
    }
    fun getReturns(): ArrayList<TruckItem> {
        var materialList = ArrayList<TruckItem>()
        mRepository.getDeliveries().forEach {
            it.materialGroup = mRepository.getMaterialByMaterialNo(it.materialNo).materialGrp
            materialList.add(it)
        }
        return materialList
    }

    fun getDamages(): ArrayList<TruckItem> {
        val materialList = ArrayList<TruckItem>()
        mRepository.getDamaged().forEach {
            it.materialGroup = mRepository.getMaterialByMaterialNo(it.materialNo).materialGrp
            materialList.add(it)
        }
        return materialList
    }


    fun mapDeliveriesIntoTruck(
        headerItems: List<HeaderItems>,
        headerBatches: List<HeaderBatches>,
        delivery: Delivery
    ) {
        mRepository.mapDeliveriesIntoTruckItemsAndBatches(headerItems, headerBatches, delivery)
    }

    fun mapSelectedMaterialsIntoTruckItemsAndBatches(
        headerItems: List<HeaderItems>,
        headerBatches: List<HeaderBatches>,
        iSalesDocHeader: ISalesDocHeader
    ) {
        mRepository.mapSelectedMaterialsIntoTruckItemsAndBatches(headerItems, headerBatches,iSalesDocHeader)
    }


    fun getTruckContent(baseBody: BaseBody) {
        val truckContentDisposable =
            mRepository.getTruckContentRemote(baseBody)
                .subscribeOn(Schedulers.io())
                .retry(NUMBER_OF_RETRIES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { truckContent ->
                        mRepository.deleteAllTruckContent()
                        myExecutor.execute {
                            mRepository.upsertTruckItems(truckContent.data.truckItem,false)
                            mRepository.upsertTruckBatches(truckContent.data.truckBatch)
                        }
                        truckContentStatusMutableLiveData.value = STATUS_LOADED
                    },
                    {
                        truckContentStatusMutableLiveData.value = STATUS_ERROR
                    })
        compositeDisposable.add(truckContentDisposable)
    }

    fun getMaterialByMaterialNo(materialNo: String): Materials {
        return mRepository.getMaterialByMaterialNo(materialNo)
    }

}