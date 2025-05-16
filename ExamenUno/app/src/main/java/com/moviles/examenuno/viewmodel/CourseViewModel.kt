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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.moviles.examenuno.App
import com.moviles.examenuno.models.Student
import com.moviles.examenuno.repository.CourseRepository
import com.moviles.examenuno.repository.StudentRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class CourseViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseViewModel::class.java)) {
            return CourseViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CourseViewModel (private val context: Context) : ViewModel() {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> get() = _courses
    private val repository = CourseRepository(context)
    private val _dataSource = MutableStateFlow<String>("")
    val dataSource: StateFlow<String> get() = _dataSource
    // Agregar propiedad para el estado de carga
    private val _loadingState = MutableLiveData<String>()
    val loadingState: LiveData<String> = _loadingState
    var courseRomm: List<Course> = listOf()

    fun loadCourse() {
        viewModelScope.launch {
            _courses.value = repository.getCourses()
        }
    }

    fun saveCourses(courseList: List<Course>) {
        viewModelScope.launch {
            repository.insertCourses(courseList)
        }
    }

    fun fetchCourses() {
        viewModelScope.launch {
            _loadingState.value = "Cargando desde la API..." // Muestra cuando cargamos desde la API
            try {
                val hasInternet = App.hasInternet()
                if (hasInternet) {
                    val apiCourse = RetrofitInstance.api.getCourse()
                    repository.clearCourses()
                    repository.insertCourses(apiCourse)
                    Log.i("ViewModelInfo", "Datos sincronizados con API")
                    _loadingState.value = "Datos desde la API" // Una vez sincronizado
                }else {
                    _loadingState.value = "Cargando desde la caché..." // Cuando no hay internet
                }
                val localStudent = repository.getCourses()
                _courses.value = localStudent
            } catch (e: Exception) {
                Log.e("CourseViewModel", "Error obteniendo cursos: ${e.message}")
                // Aquí podrías cargar desde Room si falla el API
                _loadingState.value = "Error al cargar los cursos"

                val localCourse = repository.getCourses()
                _courses.value = localCourse
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
