package com.moviles.examenuno.data

import androidx.room.Database
import androidx.room.RoomDatabase

import com.moviles.examenuno.models.Course


@Database(entities = [Course::class], version = 2, exportSchema = false)
abstract class CourseDataBase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
}

