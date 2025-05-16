package com.moviles.examenuno.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moviles.examenuno.models.Student


@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class StudentDataBase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
}
