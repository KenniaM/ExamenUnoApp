package com.moviles.examenuno.network

import com.moviles.examenuno.models.Student
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("api/student")
    suspend fun getStudent(): Response<List<Student>>

    @POST("api/student")
    @Multipart
    suspend fun addStudent(student: Student): Student

    @PUT("api/student/{id}")
    suspend fun updateStudent(@Path("id") id: Int?, @Body studentDto: Student): Student

    @DELETE("api/student/{id}")
    suspend fun deleteStudent(@Path("id") id: Int?): Response<Unit>
}