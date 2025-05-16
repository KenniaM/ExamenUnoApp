package com.moviles.examenuno.repository

import android.content.Context
import com.moviles.examenuno.data.DataBaseBuilder
import com.moviles.examenuno.models.Student

class StudentRepository(private val context: Context) {
    private val studentDao = DataBaseBuilder.getInstanceStudent(context).studentDao()

    suspend fun getStudents(): List<Student> {
        return studentDao.getAllStudent()
    }
    suspend fun getStudentsByCourseId(courseId: Int): List<Student> {
        return studentDao.getStudentsByCourseId(courseId)
    }

    suspend fun insertStudents(students: List<Student>) {
        studentDao.insertStudent(students)
    }

    suspend fun insertStudent(student: Student) {
        studentDao.insertStudent(listOf(student)) // Reutiliza la lógica de lista
    }

    suspend fun clearStudents() {
        studentDao.deleteAllStudent()
    }
}
