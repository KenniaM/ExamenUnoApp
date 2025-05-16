package com.moviles.examenuno

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.moviles.examenuno.models.Course
import com.moviles.examenuno.viewmodel.CourseViewModel
import com.moviles.examenuno.ui.theme.ExamenUnoTheme

class MainActivity : ComponentActivity() {
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamenUnoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamenUnoTheme {
                val viewModel: CourseViewModel = viewModel()
                CourseScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExamenUnoTheme {
        Greeting("Android")
    }
}



@Preview(showBackground = true)
@Composable
fun EventScreenPreview(){
    ExamenUnoTheme{
        var viewModel: CourseViewModel = viewModel()
        CourseScreen(viewModel)
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(viewModel: CourseViewModel) {
    val courses by viewModel.courses.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedCourse by remember { mutableStateOf<Course?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.i("Activity", "Coming here???")
        viewModel.fetchCourses()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cursos") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedCourse = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Curso")
            }
        }

    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Button with padding
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween // Asegura que los botones estén separados
            ) {
                Button(
                    modifier = Modifier.weight(1f), // Distribuye el espacio equitativamente
                    onClick = { viewModel.fetchCourses() }
                ) {
                    Text("Lista de cursos")
                }

                Spacer(modifier = Modifier.width(8.dp)) // Espacio entre los botones

                Button(
                    modifier = Modifier.weight(1f), // Distribuye el espacio equitativamente
                    onClick = {
                        // Navegar a StudentDetailActivity
                        val intent = Intent(context, StudentDetailActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Text("Ver Estudiantes")
                }
            }


            // Spacer to ensure some space between button and the list
            Spacer(modifier = Modifier.height(8.dp))

            // Event List with remaining space
            CourseList(courses ,
                onEdit = { course ->
                    selectedCourse = course
                    showDialog = true
                }, onDelete =
                    { course -> viewModel.deleteCourse(course.id)
                    },
                    onSelect = { course ->
                    // Navegar a StudentsActivity
                    val intent = Intent(context, StudentsActivity::class.java)
                    intent.putExtra("COURSE_ID", course.id)
                    context.startActivity(intent)
                })
        }
    }
    if (showDialog) {
        val context = LocalContext.current
        CourseDialog(
            course = selectedCourse,
            onDismiss = { showDialog = false },
            onSave = { course ->
                if (course.id == null) viewModel.addCourse(course, imageUri, context)
                else viewModel.updateCourse(course, imageUri, context)
                showDialog = false
            },
            imageUri = imageUri,  // Pasa imageUri aquí
            onImageSelected = { uri -> imageUri = uri } // Pasa la función para actualizar imageUri
        )
    }
}


@Composable
fun CourseList(
    courses: List<Course>,
    modifier: Modifier = Modifier,
    onEdit: (Course) -> Unit,
    onDelete: (Course) -> Unit,
    onSelect: (Course) -> Unit
) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(courses) { course ->
            CourseItem(course, onEdit, onDelete, onSelect)
        }
    }
}


@Composable
fun CourseItem(
    course: Course,
    onEdit: (Course) -> Unit,
    onDelete: (Course) -> Unit,
    onSelect: (Course) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { onSelect(course) }, // Navegar al seleccionar
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = course.name, style = MaterialTheme.typography.titleLarge)
            course.description?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
            course.professor?.let {
                Text(text = "👨‍🏫 $it", style = MaterialTheme.typography.bodySmall)
            }
            course.schedule?.let {
                Text(text = "📅 $it", style = MaterialTheme.typography.bodySmall)
            }
            course.image?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Course Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(top = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { onEdit(course) }) {
                    Text("Editar", color = MaterialTheme.colorScheme.primary)
                }
                TextButton(onClick = { onDelete(course) }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}




@Composable
fun CourseDialog(
    course: Course?,
    onDismiss: () -> Unit,
    onSave: (Course) -> Unit,
    imageUri: Uri?,
    onImageSelected: (Uri?) -> Unit
) {
    var name by remember { mutableStateOf(course?.name ?: "") }
    var description by remember { mutableStateOf(course?.description ?: "") }
    var schedule by remember { mutableStateOf(course?.schedule ?: "") }
    var professor by remember { mutableStateOf(course?.professor ?: "") }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        onImageSelected(it)
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (course == null) "Agregar" else "Editar Curso") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                OutlinedTextField(value = schedule, onValueChange = { schedule = it }, label = { Text("Schedule") })
                OutlinedTextField(value = professor, onValueChange = { professor = it }, label = { Text("Professor") })

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") }
                    ) {
                        Text("Select Image")
                    }

                    imageUri?.let {
                        Image(
                            painter = rememberImagePainter(it),
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .size(120.dp)
                                .align(Alignment.CenterHorizontally),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    Course(
                        id = course?.id,
                        name = name,
                        description = description,
                        image = imageUri?.toString(),
                        schedule = schedule,
                        professor = professor
                    )
                )
            }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.secondary)
            }
        }
    )
}
