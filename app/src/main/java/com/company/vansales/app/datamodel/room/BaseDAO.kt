package com.company.vansales.app.datamodel.room

import androidx.room.*


@Dao
abstract class BaseDao<T> {
    /**
     * Insert an object in the database.
     *
     * @param obj the object to be inserted.
     * @return The SQLite row id
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(obj: T): Long


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertsd(objList: List<T>)

    /**
     * Insert an array of objects in the database.
     *
     * @param objList the objects to be inserted.
     * @return The SQLite row ids
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(objList: List<T>): List<Long>

    /**
     * Update an object from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    abstract fun update(obj: T)

    /**
     * Update an array of objects from the database.
     *
     * @param objList the object to be updated
     */
    @Update
    abstract fun update(objList: List<T>)

    /**
     * Delete an object from the database
     *
     * @param obj the object to be deleted
     */
    @Delete
    abstract fun delete(obj: T)

    /**
     * Delete an object from the database
     *
     * @param obj the object to be deleted
     */
    @Delete
    abstract fun delete(obj: List<T>)

    /**
     * Upsert for object
     *
     * @param obj
     */
    @Transaction
    open fun upsert(obj: T) {
        val id = insert(obj)
        if (id == -1L) {
            update(obj)
        }
    }

    /**
     * Upsert for object list
     *
     * @param objList
     */
    @Transaction
    open fun upsert(objList: List<T>) {
        val insertResult = insert(objList)
        val updateList = ArrayList<T>()
        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) {
                updateList.add(objList[i])
            }
        }
        if (updateList.isNotEmpty()) {
            update(updateList)
        }
    }

    /**
     * Delete old list and insert new list
     *
     * @param oldList
     */
    @Transaction
    open fun delsert(oldList: List<T>, newList: List<T>) {
        delete(oldList)
        insert(newList)
    }
}
