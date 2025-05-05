package com.moviles.examenuno.models

data class Course(
    val id: Int?, //Optional attribute
    val name: String,
    val description: String?,
    val image: String?,
    val schedule: String?,
    val professor: String?
)
