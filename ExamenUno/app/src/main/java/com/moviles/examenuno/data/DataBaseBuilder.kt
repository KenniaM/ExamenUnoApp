package com.moviles.examenuno.data

import android.content.Context
import androidx.room.Room
import com.moviles.examenuno.models.Student

object DataBaseBuilder {

    private var INSTANCE: CourseDataBase? = null
    private var INSTANCESTUDENT: StudentDataBase? = null

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
    fun getInstanceStudent(context: Context): StudentDataBase {
        if (INSTANCESTUDENT == null) {
            synchronized(StudentDataBase::class) {
                INSTANCESTUDENT = Room.databaseBuilder(
                    context.applicationContext,
                    StudentDataBase::class.java,
                    "student_database"
                ).fallbackToDestructiveMigration().build()
            }
        }
        return INSTANCESTUDENT!!
    }
}