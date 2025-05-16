package com.moviles.examenuno.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Students")
data class Student(
    @PrimaryKey val id: Int?, //Optional attribute
    val name: String,
    val email: String,
    val phone: String,
    val courseId: String,
    val courseName: String? = null // New optional attribute
)
