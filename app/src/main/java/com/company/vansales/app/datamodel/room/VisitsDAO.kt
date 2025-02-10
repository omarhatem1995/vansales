package com.company.vansales.app.datamodel.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.company.vansales.app.datamodel.models.mastermodels.Visits
import com.company.vansales.app.utils.Constants.IN_PROGRESS_VISIT


@Dao
abstract class VisitsDAO : BaseDao<Visits>() {

    @Query("SELECT * FROM visits ")
    abstract fun getAllVisits(): List<Visits>
    @Query("DELETE FROM visits")
    abstract fun deleteDataInTable()

//    @Query("SELECT * FROM visits WHERE visitType != 'HEADER_VISIT' AND route = :route AND visitListID IN (SELECT MIN(visitListID) FROM visits WHERE visitType != 'HEADER_VISIT' AND route = :route GROUP BY customerName HAVING COUNT(*) > 1) ORDER BY customerName ASC, visitListID ASC")
    @Query("SELECT * FROM visits WHERE visitType != 'HEADER_VISIT' AND route = :route ORDER BY customerName ASC ")
    abstract fun getAllVisitsByRouteAscending(route : String): LiveData<List<Visits>>

//    @Query("SELECT * FROM visits WHERE visitType != 'HEADER_VISIT' AND route = :route AND visitListID IN (SELECT MIN(visitListID) FROM visits WHERE visitType != 'HEADER_VISIT' AND route = :route GROUP BY customerName HAVING COUNT(*) > 1) ORDER BY customerName DESC, visitListID DESC")
    @Query("SELECT * FROM visits WHERE visitType != 'HEADER_VISIT' AND route = :route ORDER BY customerName DESC")
    abstract fun getAllVisitsByRouteDescending(route : String): LiveData<List<Visits>>

    @Query("SELECT * FROM visits WHERE customerNo = :customerId")
    abstract fun getVisitByCustomerId(customerId : String): Visits

    @Query("UPDATE visits SET visitStatus = :newStatus WHERE visitStatus = :oldStatus  ")
    abstract fun updateCurrentActiveVisit(oldStatus: String, newStatus: String)

    @Query("UPDATE visits SET visitStatus = :status WHERE visitListID = :visitListID AND visitItemNo = :visitItemNo  ")
    abstract fun startVisit(visitListID: String, visitItemNo: Int, status: String)

    @Query("UPDATE visits SET visitStatus = :status WHERE visitListID = :visitListID AND visitItemNo = :visitItemNo ")
    abstract fun finishVisit(visitListID: String, visitItemNo: Int, status: String)

    @Query("UPDATE visits SET visitStatus = :status WHERE visitStatus = :inProgressStatus")
    abstract fun finishCurrentVisit(status: String, inProgressStatus: String = IN_PROGRESS_VISIT)

    @Query("UPDATE visits SET visitStatus = :status , comment = :failingReason, fieldValue = :failingReasonID WHERE visitListID = :visitListID AND visitItemNo = :visitItemNo ")
    abstract fun finishFailedVisit(
        visitListID: String,
        visitItemNo: Int,
        status: String,
        failingReason: String,
        failingReasonID: String
    )

    @Query("SELECT COUNT(1) FROM  visits WHERE visitStatus = :status")
    abstract fun isVisitCurrentlyInProgress(status: String): Boolean

    @Query("SELECT * FROM  visits WHERE visitStatus = :status")
    abstract fun getCurrentActiveVisit(status: String): Visits?

    @Query("SELECT * FROM  visits WHERE visitStatus = :status")
    abstract fun getCurrentActiveVisitLiveData(status: String): LiveData<Visits>

    @Query("SELECT * FROM  visits WHERE visitStatus = :finishedStatus OR  visitStatus = :failedStatus")
    abstract fun getFinishedVisits(finishedStatus: String, failedStatus: String): List<Visits>

    @Query("SELECT * FROM  visits ")
    abstract fun getAllFinishedVisits(): List<Visits>

    @Query("SELECT MAX(visitItemNo) FROM  visits")
    abstract fun getMaxvisitItemNoValue(): Int

    @Query("SELECT MAX(sequence) FROM  visits")
    abstract fun getMaxSequenceValue(): Int
    @Query("SELECT MAX(sequence) FROM visits WHERE sequence < (SELECT MAX(sequence) FROM visits)")
    abstract fun getSecondMaxSequenceValue(): Int
    @Query("SELECT visitListID FROM visits Limit 1")
    abstract fun getVisitListId(): String

    @Query("SELECT visitPlan FROM visits Limit 1")
    abstract fun getVisitPlan(): String

    @Query("SELECT COUNT(1) FROM  visits WHERE customerNo = :customerNumber")
    abstract fun isACustomerAssociatedWithAVisit(customerNumber: String): Boolean

    @Query("SELECT * FROM  visits WHERE customerNo = :customerNumber AND route = :route")
    abstract fun isACustomerWithSpecificRouteAssociatedWithAvisit(route : String, customerNumber: String): Boolean

    @Query("SELECT * FROM  visits WHERE customerNo = :customerNumber AND route = :route")
    abstract fun getVisitByRouteAndCustomerNumber(route : String,customerNumber: String): Visits

    @Query("UPDATE visits SET startDayDate =:startDate , startDayTime= :startTime , startLatitude =:startLatitude , startLongitude =:startLongitude WHERE visitListId =:visitListId AND visitItemNo =:visitItemNo")
    abstract fun updateStartVisitData(visitListId : String , visitItemNo : Int,
                                      startDate : String , startTime : String,startLatitude:Double , startLongitude:Double)

    @Query("UPDATE visits SET endDayDate =:endDate , endDayTime= :endTime , endLatitude =:endLatitude , endLongitude =:endLongitude,comment=:failedReason WHERE visitListId =:visitListId AND visitItemNo =:visitItemNo")
    abstract fun updateEndVisitData(visitListId : String , visitItemNo : Int,
                                    endDate : String , endTime : String,endLatitude:Double , endLongitude:Double,failedReason:String?)

    @Query("SELECT * FROM visits WHERE visitType = 'HEADER_VISIT'")
    abstract fun getVisitHeaderData(): Visits

    @Query("DELETE FROM visits")
    abstract fun deleteAllVisits()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updateVisits(visits: Visits)

}
