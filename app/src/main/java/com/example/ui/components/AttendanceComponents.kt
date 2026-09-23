package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AttendanceRecord
import com.example.data.local.AttendanceStatus
import com.example.data.local.Student
import com.example.ui.DailyStats
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.AbsentRedBg
import com.example.ui.theme.CardBorder
import com.example.ui.theme.HalfDayBlue
import com.example.ui.theme.HalfDayBlueBg
import com.example.ui.theme.LeaveAmber
import com.example.ui.theme.LeaveAmberBg
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PresentGreenBg
import com.example.ui.theme.SchoolBlueDark
import com.example.ui.theme.SchoolBluePrimary
import com.example.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DateNavigationBar(
    currentDate: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onToday: () -> Unit,
    onOpenDatePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayDate = try {
        val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(currentDate) ?: Date()
        val gujaratiDay = SimpleDateFormat("EEEE", Locale("gu", "IN")).format(parsed)
        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(parsed)
        "$formattedDate, $gujaratiDay"
    } catch (_: Exception) {
        currentDate
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("date_navigation_bar"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onPreviousDay,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("prev_date_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous Day",
                    tint = SchoolBlueDark
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenDatePicker() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("date_picker_trigger")
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Select Date",
                    tint = SchoolBluePrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = displayDate,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "તારીખ બદલવા અહી ક્લિક કરો",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNextDay,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("next_date_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Day",
                        tint = SchoolBlueDark
                    )
                }
                OutlinedButton(
                    onClick = onToday,
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("today_button"),
                    contentPadding = ButtonDefaults.TextButtonContentPadding,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Today,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "આજે", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun AttendanceStatsOverview(
    stats: DailyStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("stats_overview_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "દૈનિક હાજરી વિશ્લેષણ",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = SchoolBlueDark
                )
                Text(
                    text = String.format(Locale.US, "હાજરી: %.0f%%", stats.percentage),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (stats.percentage >= 75f) PresentGreen else AbsentRed
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBadge(
                    label = "કુલ",
                    value = "${stats.total}",
                    bgColor = Color(0xFFEDE7F6),
                    textColor = Color(0xFF4527A0),
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    label = "હાજર",
                    value = "${stats.present}",
                    bgColor = PresentGreenBg,
                    textColor = PresentGreen,
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    label = "ગેરહાજર",
                    value = "${stats.absent}",
                    bgColor = AbsentRedBg,
                    textColor = AbsentRed,
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    label = "રજા",
                    value = "${stats.leave}",
                    bgColor = LeaveAmberBg,
                    textColor = LeaveAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatBadge(
    label: String,
    value: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = textColor.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
fun StudentAttendanceCard(
    student: Student,
    currentRecord: AttendanceRecord?,
    onStatusSelected: (AttendanceStatus) -> Unit,
    onEditRemarks: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val status = currentRecord?.let { AttendanceStatus.fromCode(it.status) } ?: AttendanceStatus.PRESENT

    val cardBorderColor = when (status) {
        AttendanceStatus.PRESENT -> PresentGreen.copy(alpha = 0.5f)
        AttendanceStatus.ABSENT -> AbsentRed.copy(alpha = 0.7f)
        AttendanceStatus.LEAVE -> LeaveAmber.copy(alpha = 0.6f)
        AttendanceStatus.HALF_DAY -> HalfDayBlue.copy(alpha = 0.6f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, cardBorderColor, RoundedCornerShape(14.dp))
            .testTag("student_card_${student.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Roll number badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            when (status) {
                                AttendanceStatus.PRESENT -> PresentGreenBg
                                AttendanceStatus.ABSENT -> AbsentRedBg
                                AttendanceStatus.LEAVE -> LeaveAmberBg
                                AttendanceStatus.HALF_DAY -> HalfDayBlueBg
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${student.rollNo}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = when (status) {
                            AttendanceStatus.PRESENT -> PresentGreen
                            AttendanceStatus.ABSENT -> AbsentRed
                            AttendanceStatus.LEAVE -> LeaveAmber
                            AttendanceStatus.HALF_DAY -> HalfDayBlue
                        }
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name and details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = student.gender,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (student.grNo.isNotBlank()) {
                            Text(
                                text = student.grNo,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                // If absent and phone available, show call button
                if (student.phone.isNotBlank()) {
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${student.phone}")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("call_student_${student.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Call Parent",
                            tint = if (status == AttendanceStatus.ABSENT) AbsentRed else SchoolBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Remarks button
                IconButton(
                    onClick = onEditRemarks,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Remarks",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Optional remark display
            if (!currentRecord?.remarks.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = LeaveAmberBg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                ) {
                    Text(
                        text = "નોંધ: ${currentRecord?.remarks}",
                        fontSize = 11.sp,
                        color = LeaveAmber,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Attendance selection pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatusPill(
                    label = "હાજર",
                    code = "P",
                    isSelected = status == AttendanceStatus.PRESENT,
                    activeColor = PresentGreen,
                    activeBgColor = PresentGreenBg,
                    onClick = { onStatusSelected(AttendanceStatus.PRESENT) },
                    modifier = Modifier.weight(1f),
                    testTag = "status_present_${student.id}"
                )

                StatusPill(
                    label = "ગેરહાજર",
                    code = "A",
                    isSelected = status == AttendanceStatus.ABSENT,
                    activeColor = AbsentRed,
                    activeBgColor = AbsentRedBg,
                    onClick = { onStatusSelected(AttendanceStatus.ABSENT) },
                    modifier = Modifier.weight(1f),
                    testTag = "status_absent_${student.id}"
                )

                StatusPill(
                    label = "રજા",
                    code = "L",
                    isSelected = status == AttendanceStatus.LEAVE,
                    activeColor = LeaveAmber,
                    activeBgColor = LeaveAmberBg,
                    onClick = { onStatusSelected(AttendanceStatus.LEAVE) },
                    modifier = Modifier.weight(1f),
                    testTag = "status_leave_${student.id}"
                )

                StatusPill(
                    label = "અડધો",
                    code = "HD",
                    isSelected = status == AttendanceStatus.HALF_DAY,
                    activeColor = HalfDayBlue,
                    activeBgColor = HalfDayBlueBg,
                    onClick = { onStatusSelected(AttendanceStatus.HALF_DAY) },
                    modifier = Modifier.weight(1f),
                    testTag = "status_halfday_${student.id}"
                )
            }
        }
    }
}

@Composable
fun StatusPill(
    label: String,
    code: String,
    isSelected: Boolean,
    activeColor: Color,
    activeBgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) activeBgColor else MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = if (isSelected) 1.5.dp else 0.5.dp,
                color = if (isSelected) activeColor else CardBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = activeColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
            Text(
                text = "$code $label",
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
