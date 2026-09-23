package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AttendanceStatus
import com.example.data.local.Student
import com.example.ui.AttendanceFilter
import com.example.ui.AttendanceViewModel
import com.example.ui.components.AttendanceStatsOverview
import com.example.ui.components.DateNavigationBar
import com.example.ui.components.StudentAttendanceCard
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.SchoolBlueDark
import com.example.ui.theme.SchoolBluePrimary
import com.example.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DailyAttendanceScreen(
    viewModel: AttendanceViewModel,
    onNavigateToReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedDate by viewModel.selectedDate.collectAsState()
    val students by viewModel.students.collectAsState()
    val dailyAttendanceMap by viewModel.dailyAttendanceMap.collectAsState()
    val dailyStats by viewModel.dailyStats.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeFilter by viewModel.activeFilter.collectAsState()

    var studentForRemarks by remember { mutableStateOf<Student?>(null) }
    var remarksText by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }

    // DatePicker Dialog
    val openDatePicker = {
        val cal = Calendar.getInstance()
        try {
            val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(selectedDate)
            if (d != null) cal.time = d
        } catch (_: Exception) {}

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newDate = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                viewModel.setSelectedDate(newDate)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Filter students
    val filteredStudents = remember(students, dailyAttendanceMap, searchQuery, activeFilter) {
        students.filter { student ->
            val matchesQuery = searchQuery.isBlank() ||
                    student.name.contains(searchQuery, ignoreCase = true) ||
                    student.rollNo.toString().contains(searchQuery) ||
                    student.grNo.contains(searchQuery, ignoreCase = true)

            if (!matchesQuery) return@filter false

            val record = dailyAttendanceMap[student.id]
            val status = record?.let { AttendanceStatus.fromCode(it.status) } ?: AttendanceStatus.PRESENT

            when (activeFilter) {
                AttendanceFilter.ALL -> true
                AttendanceFilter.ONLY_ABSENT -> status == AttendanceStatus.ABSENT
                AttendanceFilter.ONLY_PRESENT -> status == AttendanceStatus.PRESENT
                AttendanceFilter.ONLY_LEAVE -> status == AttendanceStatus.LEAVE
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Date selector
        DateNavigationBar(
            currentDate = selectedDate,
            onPreviousDay = { viewModel.goToPreviousDay() },
            onNextDay = { viewModel.goToNextDay() },
            onToday = { viewModel.goToToday() },
            onOpenDatePicker = { openDatePicker() }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Statistics bar
        AttendanceStatsOverview(stats = dailyStats)

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.markAll(AttendanceStatus.PRESENT) },
                colors = ButtonDefaults.buttonColors(containerColor = PresentGreen),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .testTag("mark_all_present_btn"),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "બધા હાજર",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = { viewModel.markAll(AttendanceStatus.ABSENT) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AbsentRed),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .testTag("mark_all_absent_btn"),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HighlightOff,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "બધા ગેરહાજર",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onNavigateToReport,
                colors = ButtonDefaults.buttonColors(containerColor = SchoolBlueDark),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .testTag("view_share_report_btn"),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "રિપોર્ટ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search and Filter Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSearchExpanded) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("વિદ્યાર્થી શોધો (નામ અથવા રોલ નં)...", fontSize = 12.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("search_students_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.setSearchQuery("")
                            isSearchExpanded = false
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                        }
                    }
                )
            } else {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        FilterChip(
                            selected = activeFilter == AttendanceFilter.ALL,
                            onClick = { viewModel.setActiveFilter(AttendanceFilter.ALL) },
                            label = { Text("બધા (${students.size})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SchoolBluePrimary.copy(alpha = 0.15f),
                                selectedLabelColor = SchoolBlueDark
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = activeFilter == AttendanceFilter.ONLY_ABSENT,
                            onClick = { viewModel.setActiveFilter(AttendanceFilter.ONLY_ABSENT) },
                            label = { Text("ગેરહાજર (${dailyStats.absent})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AbsentRed.copy(alpha = 0.15f),
                                selectedLabelColor = AbsentRed
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = activeFilter == AttendanceFilter.ONLY_PRESENT,
                            onClick = { viewModel.setActiveFilter(AttendanceFilter.ONLY_PRESENT) },
                            label = { Text("હાજર (${dailyStats.present})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PresentGreen.copy(alpha = 0.15f),
                                selectedLabelColor = PresentGreen
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = activeFilter == AttendanceFilter.ONLY_LEAVE,
                            onClick = { viewModel.setActiveFilter(AttendanceFilter.ONLY_LEAVE) },
                            label = { Text("રજા (${dailyStats.leave})", fontSize = 11.sp) }
                        )
                    }
                }
                IconButton(
                    onClick = { isSearchExpanded = true },
                    modifier = Modifier.testTag("expand_search_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = SchoolBlueDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Student attendance cards list
        if (filteredStudents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "કોઈ વિદ્યાર્થી મળ્યા નથી",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ફિલ્ટર બદલો અથવા વિદ્યાર્થી યાદી તપાસો",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("daily_students_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 6.dp, bottom = 80.dp)
            ) {
                items(
                    items = filteredStudents,
                    key = { it.id }
                ) { student ->
                    val record = dailyAttendanceMap[student.id]
                    StudentAttendanceCard(
                        student = student,
                        currentRecord = record,
                        onStatusSelected = { status ->
                            viewModel.setStudentAttendance(student.id, status, record?.remarks ?: "")
                        },
                        onEditRemarks = {
                            studentForRemarks = student
                            remarksText = record?.remarks ?: ""
                        }
                    )
                }
            }
        }
    }

    // Remarks editing dialog
    if (studentForRemarks != null) {
        val student = studentForRemarks!!
        AlertDialog(
            onDismissRequest = { studentForRemarks = null },
            title = {
                Text(
                    text = "${student.name} - વિશેષ નોંધ / રજા કારણ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "દા.ત. માંદગી, લગ્ન પ્રસંગ, પરવાનગી લીધેલ, વગેરે",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = remarksText,
                        onValueChange = { remarksText = it },
                        label = { Text("નોંધ દાખલ કરો") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("remarks_input"),
                        singleLine = false,
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val currentRecord = dailyAttendanceMap[student.id]
                        val currentStatus = currentRecord?.let { AttendanceStatus.fromCode(it.status) } ?: AttendanceStatus.PRESENT
                        viewModel.setStudentAttendance(student.id, currentStatus, remarksText.trim())
                        studentForRemarks = null
                    },
                    modifier = Modifier.testTag("save_remarks_btn")
                ) {
                    Text("સાચવો")
                }
            },
            dismissButton = {
                TextButton(onClick = { studentForRemarks = null }) {
                    Text("રદ કરો")
                }
            }
        )
    }
}
