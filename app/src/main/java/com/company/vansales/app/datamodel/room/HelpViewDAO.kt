package com.company.vansales.app.datamodel.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.company.vansales.app.datamodel.models.mastermodels.HelpView

@Dao
abstract class HelpViewDAO :BaseDao<HelpView>() {

    @Query("SELECT * FROM help_view WHERE fieldName = :checkListValue AND fieldLanguage = :fieldLanguage  ")
    abstract fun getDamageCheckList(fieldLanguage : String ,checkListValue : String ): LiveData<List<HelpView>>

    @Query("SELECT * FROM help_view WHERE fieldName = :failedReasonsValue AND fieldLanguage = :fieldLanguage  ")
    abstract fun getFailedVisitReasons(fieldLanguage : String ,failedReasonsValue : String) : List<HelpView>

    @Query("SELECT * FROM help_view WHERE  fieldName = :focValue")
    abstract fun getReason(focValue : String ) : List<HelpView>

    @Query("DELETE FROM help_view")
    abstract fun deleteAllHelpView()

}