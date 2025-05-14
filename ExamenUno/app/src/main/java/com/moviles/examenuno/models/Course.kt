package com.moviles.examenuno.models

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "course")
data class Course(
    @PrimaryKey val id: Int?,
    val name: String,
    val description: String,
    val image: String?,
    val schedule: String,
    val professor: String
)
