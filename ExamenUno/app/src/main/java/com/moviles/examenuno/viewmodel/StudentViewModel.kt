package com.moviles.examenuno.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.moviles.examenuno.models.Student
import com.moviles.examenuno.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class StudentViewModel(application: Application) : AndroidViewModel(application)  {
    private val context = application.applicationContext

    val student = MutableStateFlow<List<Student>>(emptyList())
    val successMessage = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    var isLoading = mutableStateOf(false)
    private val _selectedStudent = MutableStateFlow<Student?>(null)
    val selectedStudent: StateFlow<Student?> = _selectedStudent

    fun getAllStudent() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = RetrofitInstance.api.getStudent()
                if (response.isSuccessful) {
                    student.value = response.body() ?: emptyList()
                    Log.d("StudentViewModel", "Estudiantes obtenidos: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("StudentViewModel", "Error al obtener estudiantes: $errorBody")
                    errorMessage.value = "Error al obtener estudiantes: $errorBody"
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