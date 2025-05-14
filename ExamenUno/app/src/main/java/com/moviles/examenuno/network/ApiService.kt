package com.moviles.examenuno.network

import com.moviles.examenuno.models.Course
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
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