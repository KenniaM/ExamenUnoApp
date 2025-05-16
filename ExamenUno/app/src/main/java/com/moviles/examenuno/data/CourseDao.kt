package com.moviles.examenuno.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moviles.examenuno.models.Course
import com.moviles.examenuno.models.CourseEntity

@Dao
interface CourseDao {

    @Query("SELECT * FROM Courses")
    suspend fun getAllCourse(): List<Course>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: List<Course>)

    @Query("DELETE FROM Courses")
    suspend fun deleteAllCourse()
}