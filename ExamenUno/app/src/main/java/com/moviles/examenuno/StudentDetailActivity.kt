package com.moviles.examenuno

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.examenuno.models.Student
import com.moviles.examenuno.ui.theme.ExamenUnoTheme
import com.moviles.examenuno.viewmodel.StudentViewModel
import com.moviles.examenuno.viewmodel.StudentViewModelFactory
import kotlinx.coroutines.launch

class StudentDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamenUnoTheme {
                val context = LocalContext.current
                val viewModel: StudentViewModel = viewModel(
                    factory = StudentViewModelFactory(context)
                )
                StudentListScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(viewModel: StudentViewModel) {
    val students by viewModel.student.collectAsState()
    val loadingState by viewModel.loadingState.observeAsState(initial = "Verificando conexión...")
    val coroutineScope = rememberCoroutineScope()
    // Snackbar host state to manage Snackbar visibility
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.getAllStudent()
    }
// Show Snackbar when loading state changes
    LaunchedEffect(loadingState) {
        if (loadingState.isNotEmpty() && loadingState != "Verificando conexión...") {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = when (loadingState) {
                        "Cargando desde la API..." -> "Sincronizando datos desde la API..."
                        "Datos desde la API" -> "Datos cargados desde la API."
                        "Cargando desde la caché..." -> "Mostrando datos locales desde la caché."
                        "Error al cargar los eventos" -> "Error al cargar eventos. Reintentando..."
                        else -> "Estado desconocido"
                    },
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Estudiantes") }
            )
        },snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.getAllStudent() },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Lista de estudiantes")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(students) { student ->
                StudentItem(student)
            }
        }
    }
}

@Composable
fun StudentItem(student: Student) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = student.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "📧 ${student.email}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "📞 ${student.phone}", style = MaterialTheme.typography.bodySmall)
            Text(text = "📘 Curso: ${student.courseName}", style = MaterialTheme.typography.bodySmall)
        }
    }
}