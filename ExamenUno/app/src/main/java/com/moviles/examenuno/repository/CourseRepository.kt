package com.moviles.examenuno.repository



/*class CourseRepository(private val context: Context) {

    private val courseDao = DatabaseBuilder.getInstance(context).courseDao()

    suspend fun getCourses(): List<Course> {
        return courseDao.getAllCourses().map { entity ->
            entity.toDomain()
        }
    }

    suspend fun insertCourses(courses: List<Course>) {
        courseDao.insertCourses(courses.map { course ->
            course.toEntity()
        })
    }

    suspend fun clearCourses() {
        courseDao.deleteAllCourses()
    }

    private fun CourseEntity.toDomain(): Course {
        return Course(
            id = this.id,
            name = this.name,
            description = this.description,
            image = this.image,
            schedule = this.schedule,
            professor = this.professor
        )
    }

    private fun Course.toEntity(): CourseEntity {
        return CourseEntity(
            id = this.id ?: 0, // Usa 0 si el ID es nulo
            name = this.name,
            description = this.description,
            image = this.image,
            schedule = this.schedule,
            professor = this.professor
        )
    }
}*/
