package com.moviles.examenuno.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moviles.examenuno.models.Course

@Dao
interface CourseDao {

    @Query("SELECT * FROM course")
    suspend fun getAllCourse(): List<Course>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(events: List<Course>)

    @Query("DELETE FROM course")
    suspend fun deleteAllCourse()
}