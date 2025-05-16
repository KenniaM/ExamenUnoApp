package com.moviles.examenuno.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moviles.examenuno.models.Student

@Dao
interface StudentDao {
    @Query("SELECT * FROM Students WHERE courseId = :courseId")
    suspend fun getStudentsByCourseId(courseId: Int): List<Student>

    @Query("SELECT * FROM Students")
    suspend fun getAllStudent(): List<Student>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: List<Student>)

    @Query("DELETE FROM Students")
    suspend fun deleteAllStudent()
}