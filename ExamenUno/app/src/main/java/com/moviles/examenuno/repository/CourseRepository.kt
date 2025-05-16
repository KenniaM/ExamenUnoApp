package com.moviles.examenuno.repository

import android.content.Context
import com.moviles.examenuno.data.DataBaseBuilder
import com.moviles.examenuno.models.Course

class CourseRepository(private val context: Context) {
    private val courseDao = DataBaseBuilder.getInstance(context).courseDao()

    // Obtener todos los cursos desde la base de datos
    suspend fun getCourses(): List<Course> {
        return courseDao.getAllCourse()
    }

    // Insertar una lista de cursos en la base de datos
    suspend fun insertCourses(courses: List<Course>) {
        courseDao.insertCourse(courses)
    }

    // Eliminar todos los cursos de la base de datos
    suspend fun clearCourses() {
        courseDao.deleteAllCourse()
    }
}
