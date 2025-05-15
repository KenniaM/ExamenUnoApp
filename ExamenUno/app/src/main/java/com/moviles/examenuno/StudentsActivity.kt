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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.examenuno.models.Student
import com.moviles.examenuno.ui.theme.ExamenUnoTheme
import com.moviles.examenuno.viewmodel.StudentViewModel

class StudentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val courseId = intent.getIntExtra("COURSE_ID", -1)
            if (courseId != -1) {
                // Usa el ID para cargar los estudiantes inscritos
                Log.i("StudentsActivity", "Course ID recibido: $courseId")
            } else {
                Log.e("StudentsActivity", "No se recibió un Course ID válido.")
            }
            ExamenUnoTheme {
                val viewModel: StudentViewModel = viewModel()
                studentScreen(viewModel,courseId)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun studentScreen(viewModel: StudentViewModel,courseId: Int) {
    val students by viewModel.student.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }

    LaunchedEffect(Unit) {
        Log.i("Activity", "Coming here???")
        viewModel.getAllStudentByCourseId(courseId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Estudiantes") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedStudent = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar estudiante")
            }
        }

    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Button with padding
            Button(
                modifier = Modifier
                    .padding(16.dp) // Add padding around the button
                    .fillMaxWidth(),
                onClick = { viewModel.getAllStudent() }
            ) {
                Text("Lista de estudiantes")
            }

            // Spacer to ensure some space between button and the list
            Spacer(modifier = Modifier.height(8.dp))

            // Event List with remaining space
            StudentList(students,
                onEdit = { student ->
                    selectedStudent = student
                    showDialog = true
                }, onDelete =
                    {  student -> viewModel.deleteStudent(student.id, courseId)
                    })
        }
    }
    if (showDialog) {
        StudentDialog(
            student = selectedStudent,
            courseId = courseId,
            onDismiss = { showDialog = false },
            onSave = { student ->
                if (student.id == null) viewModel.addStudent(student, courseId)
                else viewModel.updateStudent(student.id, student, courseId)
                showDialog = false
            }
        )
    }
}
@Composable
fun StudentList(student: List<Student>, modifier: Modifier = Modifier, onEdit: (Student) -> Unit, onDelete: (Student) -> Unit) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(student) { student ->
            StudentItem(student, onEdit, onDelete)
        }
    }
}

@Composable
fun StudentItem(student: Student, onEdit: (Student) -> Unit, onDelete: (Student) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = student.name, style = MaterialTheme.typography.titleLarge)
            student.email.let {
                Text(text = "📧 $it", style = MaterialTheme.typography.bodyMedium)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { onEdit(student) }) {
                    Text("Editar", color = MaterialTheme.colorScheme.primary)
                }
                TextButton(onClick = { onDelete(student) }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun StudentDialog(student: Student?, onDismiss: () -> Unit, onSave: (Student) -> Unit,courseId:Int) {
    var name by remember { mutableStateOf(student?.name ?: "") }
    var email by remember { mutableStateOf(student?.email ?: "") }
    var phone by remember { mutableStateOf(student?.phone ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (student == null) "Agregar estudiante" else "Editar estudiante") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Telefono") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(Student(student?.id, name, email, phone, courseId.toString()))
            }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.secondary)
            }
        }
    )
}