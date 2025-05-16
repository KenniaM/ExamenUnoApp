package com.moviles.examenuno.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.examenuno.App
import com.moviles.examenuno.models.Student
import com.moviles.examenuno.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import com.moviles.examenuno.repository.StudentRepository

class StudentViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentViewModel::class.java)) {
            return StudentViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class StudentViewModel(private val context: Context) : ViewModel() {

    val successMessage = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    var isLoading = mutableStateOf(false)

    private val _student = MutableStateFlow<List<Student>>(emptyList())
    val student: StateFlow<List<Student>> get() = _student
    private val repository = StudentRepository(context)
    private val _dataSource = MutableStateFlow<String>("")
    val dataSource: StateFlow<String> get() = _dataSource
    // Agregar propiedad para el estado de carga
    private val _loadingState = MutableLiveData<String>()
    val loadingState: LiveData<String> = _loadingState
    var studentRomm: List<Student> = listOf()

    fun loadStudent() {
        viewModelScope.launch {
            _student.value = repository.getStudents()
        }
    }

    fun saveStudents(studentList: List<Student>) {
        viewModelScope.launch {
            repository.insertStudents(studentList)
        }
    }
    fun getAllStudent() {
        viewModelScope.launch {
            _loadingState.value = "Cargando desde la API..." // Muestra cuando cargamos desde la API
            try {
                val hasInternet = App.hasInternet()
                if (hasInternet) {
                    val apiStudent = RetrofitInstance.api.getStudent()
                    repository.clearStudents()
                    repository.insertStudents(apiStudent)
                    Log.i("ViewModelInfo", "Datos sincronizados con API")
                    _loadingState.value = "Datos desde la API" // Una vez sincronizado
                }else {
                    _loadingState.value = "Cargando desde la caché..." // Cuando no hay internet
                }
                val localStudent = repository.getStudents()
                _student.value = localStudent

            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
                _loadingState.value = "Error al cargar los Estudiantes"

                val localStudent = repository.getStudents()
                _student.value = localStudent
            }
        }
    }
    fun getAllStudentByCourseId(courseId: Int) {
        viewModelScope.launch {
            _loadingState.value = "Cargando desde la API..."
            isLoading.value = true
            try {
                val hasInternet = App.hasInternet()
                if (hasInternet) {
                    val apiStudent = RetrofitInstance.api.getStudentByCourseId(courseId)
                    repository.clearStudents()
                    repository.insertStudents(apiStudent)
                    Log.i("ViewModelInfo", "Datos sincronizados con API")
                    _loadingState.value = "Datos desde la API"
                } else {
                    _loadingState.value = "Cargando desde la caché..."
                }

                val localStudents = repository.getStudentsByCourseId(courseId)
                _student.value = localStudents

            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
                _loadingState.value = "Error al cargar los Estudiantes"

                val localStudents = repository.getStudentsByCourseId(courseId)
                _student.value = localStudents
            }
        }
    }

    fun addStudent(request: Student, courseId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val student = RetrofitInstance.api.addStudent(request)
                successMessage.value = "Estudiante creado con éxito"
                Log.d("StudentViewModel", "Estudiante creado: $student")
                getAllStudentByCourseId(courseId) // Refrescar la lista de estudiantes
            } catch (e: IOException) {
                errorMessage.value = "Error de red: ${e.message}"
                Log.e("StudentViewModel", "Error de red: ${e.message}")
            } catch (e: Exception) {
                errorMessage.value = "Error inesperado: ${e.message}"
                Log.e("StudentViewModel", "Error inesperado: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }
    fun updateStudent(id: Int, request: Student, courseId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val student = RetrofitInstance.api.updateStudent(id, request)
                successMessage.value = "Estudiante actualizado con éxito"
                Log.d("StudentViewModel", "Estudiante actualizado: $student")
                getAllStudentByCourseId(courseId) // Refrescar la lista de estudiantes
            } catch (e: IOException) {
                errorMessage.value = "Error de red: ${e.message}"
                Log.e("StudentViewModel", "Error de red: ${e.message}")
            } catch (e: Exception) {
                errorMessage.value = "Error inesperado: ${e.message}"
                Log.e("StudentViewModel", "Error inesperado: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }
    fun deleteStudent(id: Int?, courseId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.api.deleteStudent(id)
                if (response.isSuccessful) {
                    successMessage.value = "Estudiante eliminado con éxito"
                    Log.d("StudentViewModel", "Estudiante eliminado con ID: $id")
                    getAllStudentByCourseId(courseId) // Refrescar la lista
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("StudentViewModel", "Error al eliminar estudiante: $errorBody")
                    errorMessage.value = "Error al eliminar estudiante: $errorBody"
                }
            } catch (e: IOException) {
                errorMessage.value = "Error de red: ${e.message}"
                Log.e("StudentViewModel", "Error de red: ${e.message}")
            } catch (e: Exception) {
                errorMessage.value = "Error inesperado: ${e.message}"
                Log.e("StudentViewModel", "Error inesperado: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }


}