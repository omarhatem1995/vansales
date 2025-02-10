package com.company.vansales.app.datamodel.repository

import androidx.lifecycle.LiveData
import com.company.vansales.R
import com.company.vansales.app.SAPWizardApplication.Companion.application
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.HelpView
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.HelpViewDAO
import com.company.vansales.app.datamodel.services.ClientService
import com.company.vansales.app.datamodel.sharedpref.GetSharedPreferences
import com.company.vansales.app.utils.Constants
import com.company.vansales.app.utils.Constants.ARABIC_LANGUAGE
import com.company.vansales.app.utils.Constants.CHECK_LIST
import com.company.vansales.app.utils.Constants.ENGLISH_LANGUAGE
import com.company.vansales.app.utils.Constants.FAILED_REASON
import com.company.vansales.app.utils.Constants.FOC_REASON
import com.company.vansales.app.utils.Constants.RET_REASON
import io.reactivex.Observable

class HelpViewRepository() {

    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    private val helpViewDAO: HelpViewDAO
    private var clientService = ClientService.getClient(application)
    val getDamageCheckListEnglish: LiveData<List<HelpView>>
    val getDamageCheckListArabic: LiveData<List<HelpView>>

    init {
        helpViewDAO = appDB.getHelpView()
        getDamageCheckListEnglish =
            helpViewDAO.getDamageCheckList(ENGLISH_LANGUAGE, CHECK_LIST)
        getDamageCheckListArabic =
            helpViewDAO.getDamageCheckList(ARABIC_LANGUAGE, CHECK_LIST)
    }

    fun getFocReason() : List<HelpView>{
        return helpViewDAO.getReason(FOC_REASON)
    }
    fun getReturnReason() : List<HelpView>{
        return helpViewDAO.getReason(RET_REASON)
    }

    fun getHelpViewRemote(baseBody: BaseBody): Observable<BaseResponse<HelpView>?> {
        return clientService.getHelpView(baseBody)
    }

    fun upsert(helpViewList: List<HelpView>?) {
        helpViewDAO.deleteAllHelpView()
        helpViewList?.let { helpViewDAO.upsert(it) }
    }

    fun getFailedVisitReasons(): HashMap<String, String> {
        var sharedPreferences = GetSharedPreferences(application)
        val helpViewList: List<HelpView> =
            if (sharedPreferences.getLanguage()?.toUpperCase() == Constants.ENGLISH_LANGUAGE) {
                helpViewDAO.getFailedVisitReasons(
                    ENGLISH_LANGUAGE,
                    FAILED_REASON
                )
            } else {
                helpViewDAO.getFailedVisitReasons(
                    ARABIC_LANGUAGE,
                    FAILED_REASON
                )
            }
        val failingReasons = HashMap<String, String>()
        for (i in helpViewList.indices) {
            failingReasons[helpViewList[i].filedDescrption] = helpViewList[i].fieldValue
        }
        failingReasons[application.resources.getString(R.string.other)] = "ZOT"
        return failingReasons
    }

}