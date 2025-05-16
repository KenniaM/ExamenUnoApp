package com.moviles.examenuno.data

import android.content.Context
import androidx.room.Room

object DataBaseBuilder {

    private var INSTANCE: CourseDataBase? = null

    fun getInstance(context: Context): CourseDataBase {
        if (INSTANCE == null) {
            synchronized(CourseDataBase::class) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    CourseDataBase::class.java,
                    "course_database"
                ).fallbackToDestructiveMigration().build()
            }
        }
        return INSTANCE!!
    }
}