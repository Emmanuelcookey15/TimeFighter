package com.example.routinecheck

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Routines::class], version = 1)
abstract class RoutineDatabase: RoomDatabase() {

    abstract val routinesDao: RoutineDao


    companion object {

        @Volatile
        private var INSTANCE: RoutineDatabase? = null


        fun getDatabase(context: Context): RoutineDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RoutineDatabase::class.java,
                        "routinecheck_database"
                    )

                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                }
                INSTANCE = instance
                return instance
            }
        }
    }
}