package com.example.routinecheck

import androidx.room.*

@Dao
interface RoutineDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(routine: Routines)


    @Query("SELECT * from routines_table" )
    fun getRoutine(): List<Routines>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateRoutine(routines: Routines)

    @Delete
    fun deleteRoutine(routines: Routines)


}