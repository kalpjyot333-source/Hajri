package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.Student
import com.example.ui.AttendanceViewModel
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.SchoolBlueDark
import com.example.ui.theme.SchoolBluePrimary
import com.example.ui.theme.TextMuted

@Composable
fun StudentListScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val students by viewModel.students.collectAsState()
    val classSettings by viewModel.classSettings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var studentToEdit by remember { mutableStateOf<Student?>(null) }
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var studentToDelete by remember { mutableStateOf<Student?>(null) }

    val filteredList = remember(students, searchQuery) {
        if (searchQuery.isBlank()) students
        else students.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.rollNo.toString().contains(searchQuery) ||
            it.grNo.contains(searchQuery, ignoreCase = true)
        }
    }

    val boyCount = remember(students) { students.count { it.gender == "કુમાર" } }
    val girlCount = remember(students) { students.count { it.gender == "કન્યા" } }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Class and Enrollment Statistics Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("students_header_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${classSettings.standard} (વર્ગ: ${classSettings.section})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SchoolBlueDark
                            )
                            Text(
                                text = "શાળા: ${classSettings.schoolName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SchoolBluePrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "કુલ: ${students.size}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SchoolBluePrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "👦 કુમાર: $boyCount",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "👧 કન્યા: $girlCount",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "વર્ષ: ${classSettings.academicYear}",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("વિદ્યાર્થી શોધો (નામ, રોલ નં, GR નં)...", fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = SchoolBlueDark
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_student_roster_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Students List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("students_roster_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 88.dp)
            ) {
                items(
                    items = filteredList,
                    key = { it.id }
                ) { student ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("roster_item_${student.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SchoolBluePrimary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${student.rollNo}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = SchoolBlueDark
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = student.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "${student.gender} • ${student.grNo}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                    if (student.phone.isNotBlank()) {
                                        Text(
                                            text = "📱 ${student.phone}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SchoolBluePrimary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            if (student.phone.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${student.phone}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Call",
                                        tint = SchoolBluePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { studentToEdit = student },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = { studentToDelete = student },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = AbsentRed.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // FAB to add new student
        FloatingActionButton(
            onClick = { isAddDialogOpen = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 80.dp)
                .testTag("add_student_fab"),
            containerColor = SchoolBluePrimary,
            contentColor = androidx.compose.ui.graphics.Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Student"
            )
        }
    }

    // Add Student Dialog
    if (isAddDialogOpen) {
        val nextRoll = (students.maxOfOrNull { it.rollNo } ?: 0) + 1
        StudentEditDialog(
            initialStudent = Student(
                rollNo = nextRoll,
                grNo = "GR-${1400 + nextRoll}",
                name = "",
                gender = "કુમાર",
                phone = ""
            ),
            title = "નવો વિદ્યાર્થી ઉમેરો",
            onSave = { newStudent ->
                viewModel.saveStudent(newStudent)
                isAddDialogOpen = false
            },
            onDismiss = { isAddDialogOpen = false }
        )
    }

    // Edit Student Dialog
    if (studentToEdit != null) {
        StudentEditDialog(
            initialStudent = studentToEdit!!,
            title = "વિદ્યાર્થી વિગત સુધારો",
            onSave = { updatedStudent ->
                viewModel.saveStudent(updatedStudent)
                studentToEdit = null
            },
            onDismiss = { studentToEdit = null }
        )
    }

    // Delete Confirmation Dialog
    if (studentToDelete != null) {
        val s = studentToDelete!!
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            title = { Text("વિદ્યાર્થી કાઢી નાખવાની પુષ્ટિ") },
            text = {
                Text("શું તમે ખરેખર રોલ નં. ${s.rollNo} (${s.name}) ને યાદીમાંથી કાઢી નાખવા માંગો છો?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteStudent(s)
                        studentToDelete = null
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AbsentRed)
                ) {
                    Text("હા, કાઢી નાખો")
                }
            },
            dismissButton = {
                TextButton(onClick = { studentToDelete = null }) {
                    Text("રદ કરો")
                }
            }
        )
    }
}

@Composable
fun StudentEditDialog(
    initialStudent: Student,
    title: String,
    onSave: (Student) -> Unit,
    onDismiss: () -> Unit
) {
    var rollNoText by remember { mutableStateOf(initialStudent.rollNo.toString()) }
    var nameText by remember { mutableStateOf(initialStudent.name) }
    var grNoText by remember { mutableStateOf(initialStudent.grNo) }
    var gender by remember { mutableStateOf(initialStudent.gender) }
    var phoneText by remember { mutableStateOf(initialStudent.phone) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = title, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (errorMessage.isNotBlank()) {
                    Text(text = errorMessage, color = AbsentRed, fontSize = 12.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = rollNoText,
                        onValueChange = { rollNoText = it },
                        label = { Text("રોલ નં.*") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = grNoText,
                        onValueChange = { grNoText = it },
                        label = { Text("GR નં.") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("વિદ્યાર્થીનું નામ* (ગુજરાતી / English)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Gender Selection
                Text(text = "જાતિ:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { gender = "કુમાર" }
                    ) {
                        RadioButton(selected = gender == "કુમાર", onClick = { gender = "કુમાર" })
                        Text(text = "કુમાર (Boy)", fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { gender = "કન્યા" }
                    ) {
                        RadioButton(selected = gender == "કન્યા", onClick = { gender = "કન્યા" })
                        Text(text = "કન્યા (Girl)", fontSize = 13.sp)
                    }
                }

                OutlinedTextField(
                    value = phoneText,
                    onValueChange = { phoneText = it },
                    label = { Text("વાલીનો મોબાઇલ નંબર") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val roll = rollNoText.toIntOrNull()
                    if (roll == null || roll <= 0) {
                        errorMessage = "કૃપા કરીને માન્ય રોલ નંબર દાખલ કરો."
                        return@Button
                    }
                    if (nameText.trim().isBlank()) {
                        errorMessage = "કૃપા કરીને વિદ્યાર્થીનું નામ દાખલ કરો."
                        return@Button
                    }
                    onSave(
                        initialStudent.copy(
                            rollNo = roll,
                            name = nameText.trim(),
                            grNo = grNoText.trim(),
                            gender = gender,
                            phone = phoneText.trim()
                        )
                    )
                }
            ) {
                Text("સાચવો")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("રદ કરો")
            }
        }
    )
}
