package com.company.vansales.app.datamodel.room

import androidx.room.Dao
import androidx.room.Query
import com.company.vansales.app.datamodel.models.mastermodels.Routes
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RoutesDAO : BaseDao<Routes>() {

    @Query("SELECT * FROM routes")
    abstract fun getRoutes(): Flow<List<Routes>>

    @Query("SELECT * FROM routes WHERE route = :route")
    abstract fun getRouteDetailsById(route : String) : Routes

    @Query("DELETE FROM routes")
    abstract fun deleteAllRoutes()

    @Query("SELECT * FROM routes")
    abstract fun getRoutesList(): List<Routes>

    @Query("DELETE FROM routes")
    abstract fun deleteAllSelectedRoutes()


}
