package com.moviles.examenuno.models

data class Student(
    val id: Int?, //Optional attribute
    val name: String,
    val email: String,
    val phone: String,
    val courseId: String,
    val courseName: String? = null // New optional attribute
)
