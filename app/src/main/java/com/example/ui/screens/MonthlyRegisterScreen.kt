package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AttendanceRecord
import com.example.data.local.AttendanceStatus
import com.example.ui.AttendanceViewModel
import com.example.ui.StudentMonthlySummary
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.AbsentRedBg
import com.example.ui.theme.CardBorder
import com.example.ui.theme.LeaveAmber
import com.example.ui.theme.LeaveAmberBg
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PresentGreenBg
import com.example.ui.theme.SchoolBlueDark
import com.example.ui.theme.SchoolBluePrimary
import com.example.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MonthlyRegisterScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val monthlySummaries by viewModel.monthlyStudentSummaries.collectAsState()
    val monthlyAttendanceList by viewModel.monthlyAttendanceList.collectAsState()
    val classSettings by viewModel.classSettings.collectAsState()

    var selectedStudentSummary by remember { mutableStateOf<StudentMonthlySummary?>(null) }

    // Parse month for display in Gujarati
    val displayMonth = remember(selectedMonth) {
        try {
            val parts = selectedMonth.split("-")
            val year = parts[0]
            val monthIndex = parts[1].toInt() - 1
            val gujaratiMonths = listOf(
                "જાન્યુઆરી", "ફેબ્રુઆરી", "માર્ચ", "એપ્રિલ", "મે", "જૂન",
                "જુલાઈ", "ઓગસ્ટ", "સપ્ટેમ્બર", "ઓક્ટોબર", "નવેમ્બર", "ડિસેમ્બર"
            )
            "${gujaratiMonths.getOrElse(monthIndex) { "" }} $year"
        } catch (_: Exception) {
            selectedMonth
        }
    }

    val totalDaysRecorded = remember(monthlyAttendanceList) {
        monthlyAttendanceList.map { it.date }.distinct().size
    }

    val averageClassPercentage = remember(monthlySummaries) {
        if (monthlySummaries.isEmpty()) 0f
        else monthlySummaries.map { it.percentage }.average().toFloat()
    }

    val lowAttendanceCount = remember(monthlySummaries) {
        monthlySummaries.count { it.percentage < 75f }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Month Selector Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("month_selector_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        val parts = selectedMonth.split("-")
                        var year = parts[0].toInt()
                        var month = parts[1].toInt() - 1
                        if (month < 1) {
                            month = 12
                            year -= 1
                        }
                        viewModel.setSelectedMonth(String.format(Locale.US, "%04d-%02d", year, month))
                    },
                    modifier = Modifier.testTag("prev_month_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Month",
                        tint = SchoolBlueDark
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "માસિક પત્રક - $displayMonth",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SchoolBlueDark
                    )
                    Text(
                        text = "ધોરણ: ${classSettings.standard} (વર્ગ: ${classSettings.section})",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                IconButton(
                    onClick = {
                        val parts = selectedMonth.split("-")
                        var year = parts[0].toInt()
                        var month = parts[1].toInt() + 1
                        if (month > 12) {
                            month = 1
                            year += 1
                        }
                        viewModel.setSelectedMonth(String.format(Locale.US, "%04d-%02d", year, month))
                    },
                    modifier = Modifier.testTag("next_month_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Month",
                        tint = SchoolBlueDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Month Summary KPI Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalDaysRecorded",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SchoolBlueDark
                    )
                    Text(
                        text = "નોંધાયેલ દિવસો",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format(Locale.US, "%.1f%%", averageClassPercentage),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (averageClassPercentage >= 75f) PresentGreen else AbsentRed
                    )
                    Text(
                        text = "સરેરાશ હાજરી",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$lowAttendanceCount",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (lowAttendanceCount > 0) AbsentRed else PresentGreen
                    )
                    Text(
                        text = "<75% હાજરી",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Share button row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "વિદ્યાર્થીવાર માસિક વિગત",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedButton(
                onClick = {
                    val sb = StringBuilder()
                    sb.append("🏫 ${classSettings.schoolName}\n")
                    sb.append("📊 માસિક હાજરી પત્રક સારાંશ ($displayMonth)\n")
                    sb.append("📖 ધોરણ: ${classSettings.standard} (વર્ગ: ${classSettings.section})\n")
                    sb.append("કુલ નોંધાયેલ દિવસો: $totalDaysRecorded\n")
                    sb.append("સરેરાશ વર્ગ હાજરી: ${String.format(Locale.US, "%.1f%%", averageClassPercentage)}\n")
                    sb.append("━━━━━━━━━━━━━━━━━━━━\n")
                    monthlySummaries.forEach { s ->
                        sb.append("રોલ ${s.student.rollNo}. ${s.student.name}: હાજર: ${s.presentCount} | ગેરહાજર: ${s.absentCount} | ${String.format(Locale.US, "%.0f%%", s.percentage)}\n")
                    }
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, sb.toString())
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "માસિક સારાંશ શેર કરો"))
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("સારાંશ શેર", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Student monthly list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("monthly_students_list"),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
        ) {
            items(
                items = monthlySummaries,
                key = { it.student.id }
            ) { summary ->
                StudentMonthlyCard(
                    summary = summary,
                    onClick = { selectedStudentSummary = summary }
                )
            }
        }
    }

    // Individual Student Monthly Detail Dialog
    if (selectedStudentSummary != null) {
        val s = selectedStudentSummary!!
        val studentRecords = monthlyAttendanceList.filter { it.studentId == s.student.id }

        AlertDialog(
            onDismissRequest = { selectedStudentSummary = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SchoolBluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${s.student.rollNo}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = s.student.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "GR: ${s.student.grNo} • ${s.student.gender}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "હાજર દિવસો: ${s.presentCount}", color = PresentGreen, fontWeight = FontWeight.Bold)
                        Text(text = "ગેરહાજર: ${s.absentCount}", color = AbsentRed, fontWeight = FontWeight.Bold)
                        Text(text = "રજા: ${s.leaveCount}", color = LeaveAmber, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = String.format(Locale.US, "માસિક હાજરી ટકાવારી: %.1f%%", s.percentage),
                        fontWeight = FontWeight.ExtraBold,
                        color = if (s.percentage >= 75f) PresentGreen else AbsentRed
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "તારીખવાર વિગત ($displayMonth):",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (studentRecords.isEmpty()) {
                        Text(
                            text = "આ મહિનામાં કોઈ હાજરી નોંધાયેલી નથી.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    } else {
                        LazyColumn(modifier = Modifier.height(200.dp)) {
                            items(studentRecords.sortedBy { it.date }) { rec ->
                                val status = AttendanceStatus.fromCode(rec.status)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = rec.date, fontSize = 12.sp)
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = when (status) {
                                            AttendanceStatus.PRESENT -> PresentGreenBg
                                            AttendanceStatus.ABSENT -> AbsentRedBg
                                            AttendanceStatus.LEAVE -> LeaveAmberBg
                                            AttendanceStatus.HALF_DAY -> PresentGreenBg
                                        }
                                    ) {
                                        Text(
                                            text = status.gujaratiLabel + if (rec.remarks.isNotBlank()) " (${rec.remarks})" else "",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (status) {
                                                AttendanceStatus.PRESENT -> PresentGreen
                                                AttendanceStatus.ABSENT -> AbsentRed
                                                AttendanceStatus.LEAVE -> LeaveAmber
                                                AttendanceStatus.HALF_DAY -> PresentGreen
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedStudentSummary = null }) {
                    Text("બંધ કરો")
                }
            }
        )
    }
}

@Composable
fun StudentMonthlyCard(
    summary: StudentMonthlySummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressColor = when {
        summary.percentage >= 80f -> PresentGreen
        summary.percentage >= 60f -> LeaveAmber
        else -> AbsentRed
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("student_monthly_card_${summary.student.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${summary.student.rollNo}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SchoolBlueDark
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = summary.student.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "GR: ${summary.student.grNo} • ${summary.student.gender}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format(Locale.US, "%.0f%%", summary.percentage),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = progressColor
                    )
                    Text(
                        text = "${summary.presentCount}/${summary.totalWorkingDays} દિવસ",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Linear Progress Indicator
            LinearProgressIndicator(
                progress = { (summary.percentage / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "હાજર: ${summary.presentCount}",
                    fontSize = 11.sp,
                    color = PresentGreen,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "ગેરહાજર: ${summary.absentCount}",
                    fontSize = 11.sp,
                    color = AbsentRed,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "રજા: ${summary.leaveCount}",
                    fontSize = 11.sp,
                    color = LeaveAmber,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "વિગત જુઓ >",
                    fontSize = 11.sp,
                    color = SchoolBluePrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
