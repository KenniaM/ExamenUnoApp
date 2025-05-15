package com.moviles.examenuno.network

import com.moviles.examenuno.models.Student
import com.moviles.examenuno.models.Course
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @GET("api/student")
    suspend fun getStudent(): Response<List<Student>>

    @POST("api/student")
    suspend fun addStudent(@Body request: Student): Response<Student>

    @PUT("api/student/{id}")
    suspend fun updateStudent(@Path("id") id: Int?, @Body request: Student): Response<Student>

    @DELETE("api/student/{id}")
    suspend fun deleteStudent(@Path("id") id: Int?): Response<Unit>

    @GET("api/student/course/{courseId}")
    suspend fun getStudentByCourseId(@Path("courseId") id: Int): Response<List<Student>>

    @GET("api/course")
    suspend fun getCourse(): List<Course>

    @POST("api/course")
    @Multipart
    suspend fun addCourse(
        @Part("Name") name: RequestBody,
        @Part("Description") description: RequestBody,
        @Part("Schedule") schedule: RequestBody,
        @Part("Professor") professor: RequestBody,
        @Part file: MultipartBody.Part? // Cambié "image" a "file" y eliminé el nombre del parámetro aquí
    ): Course

    @Multipart
    @PUT("api/course/{courseId}")
    suspend fun updateCourse(
        @Path("courseId") id: Int,
        @Part("Name") name: RequestBody,
        @Part("Description") description: RequestBody,
        @Part("Schedule") schedule: RequestBody,
        @Part("Professor") professor: RequestBody,
        @Part file: MultipartBody.Part?
    ): Course


    @DELETE("api/course/{courseId}")
    suspend fun deleteCourse(@Path("courseId") id: Int): Response<Unit>



    @GET("api/course/{courseId}")
    suspend fun getCourseById(@Path("courseId") id: Int): Course
}