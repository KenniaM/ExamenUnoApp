

package com.moviles.examenuno.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Courses")
data class Course(
    @PrimaryKey val id: Int?,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val schedule: String,
    val professor:String
)
