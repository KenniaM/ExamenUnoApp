package com.moviles.examenuno.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.examenuno.models.Course
import com.moviles.examenuno.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

import android.content.Context
import android.net.Uri
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException


class CourseViewModel  : ViewModel() {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> get() = _courses

    fun fetchCourses() {
        viewModelScope.launch {
            try {
                _courses.value = RetrofitInstance.api.getCourse()
                Log.i("CourseViewModel", "Cursos obtenidos de API: ${_courses.value}")
            } catch (e: Exception) {
                Log.e("CourseViewModel", "Error obteniendo cursos: ${e.message}")
                // Aquí podrías cargar desde Room si falla el API
            }
        }
    }


    fun addCourse(course: Course, imageUri: Uri?, context: Context) {
        viewModelScope.launch {
            try {
                // Convertir los campos a RequestBody
                val namePart = course.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionPart = (course.description ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val schedulePart = (course.schedule ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val professorPart = (course.professor ?: "").toRequestBody("text/plain".toMediaTypeOrNull())

                // Convertir Uri a MultipartBody.Part
                val filePart = imageUri?.let {
                    val file = FileUtils.getFileFromUri(context, it)
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("File", file.name, requestFile)
                }

                // Llamar a la API
                val response = RetrofitInstance.api.addCourse(
                    namePart,
                    descriptionPart,
                    schedulePart,
                    professorPart,
                    filePart
                )
                _courses.value += response
                Log.i("ViewModelInfo", "Curso creado exitosamente: $response")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }


    fun updateCourse(course: Course, imageUri: Uri?, context: Context) {
        viewModelScope.launch {
            try {
                val namePart = course.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionPart = (course.description ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val schedulePart = (course.schedule ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val professorPart = (course.professor ?: "").toRequestBody("text/plain".toMediaTypeOrNull())

                val filePart = imageUri?.let {
                    val file = FileUtils.getFileFromUri(context, it)
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", file.name, requestFile)
                }

                val response = RetrofitInstance.api.updateCourse(
                    course.id!!,
                    namePart,
                    descriptionPart,
                    schedulePart,
                    professorPart,
                    filePart
                )

                _courses.value = _courses.value.map {
                    if (it.id == course.id) response else it
                }

                Log.i("CourseViewModel", "Curso actualizado: $response")

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }




    fun deleteCourse(courseId: Int?) {
        if (courseId == null) {
            Log.e("CourseViewModel", "ID del curso es nulo. No se puede eliminar.")
            return
        }

        viewModelScope.launch {
            try {
                RetrofitInstance.api.deleteCourse(courseId)
                _courses.value = _courses.value.filter { it.id != courseId }
                Log.i("CourseViewModel", "Curso eliminado con ID: $courseId")
            } catch (e: Exception) {
                Log.e("CourseViewModel", "Error al eliminar curso: ${e.message}")
            }
        }
    }


    object FileUtils {
        fun getFileFromUri(context: Context, uri: Uri): File {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("temp", ".jpg", context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            return tempFile
        }
    }
}
