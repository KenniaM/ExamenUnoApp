package com.moviles.examenuno.repository

import android.content.Context
import com.moviles.examenuno.data.DataBaseBuilder
import com.moviles.examenuno.models.Student

class StudentRepository (private val context: Context) {
    private val studentDao = DataBaseBuilder.getInstanceStudent(context).studentDao()

    suspend fun getStudents(): List<Student> {
        return studentDao.getAllStudent().map { entity ->
            entity.toDomain()
        }
    }

    suspend fun insertStudent(events: List<Student>) {
        studentDao.insertStudent(events.map { event ->
            event.toEntity()
        })
    }

    suspend fun clearStudents() {
        studentDao.deleteAllStudent()
    }

    private fun Student.toDomain(): Student {
        return Student(
            id = this.id,
            name = this.name,
            email = this.email,
            phone = this.phone,
            courseId = this.courseId,
            courseName = this.courseName
        )
    }

    private fun Student.toEntity(): Student {
        return Student(
            id = this.id ?: 0,
            name = this.name,
            email = this.email,
            phone = this.phone,
            courseId = this.courseId,
            courseName = this.courseName
        )
    }

}