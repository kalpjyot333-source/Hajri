package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AttendanceStatus
import com.example.ui.AttendanceViewModel
import com.example.ui.components.AttendanceStatsOverview
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.AbsentRedBg
import com.example.ui.theme.CardBorder
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PresentGreenBg
import com.example.ui.theme.SchoolBlueDark
import com.example.ui.theme.SchoolBluePrimary
import com.example.ui.theme.TextMuted

@Composable
fun DailyReportScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dailyStats by viewModel.dailyStats.collectAsState()
    val reportText = viewModel.generateDailyReportMessage()
    val students by viewModel.students.collectAsState()
    val dailyAttendanceMap by viewModel.dailyAttendanceMap.collectAsState()

    val absentStudents = students.filter { student ->
        val rec = dailyAttendanceMap[student.id]
        val status = rec?.let { AttendanceStatus.fromCode(it.status) } ?: AttendanceStatus.PRESENT
        status == AttendanceStatus.ABSENT
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Stats Overview
        AttendanceStatsOverview(stats = dailyStats)

        Spacer(modifier = Modifier.height(14.dp))

        // WhatsApp Share & Copy Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, reportText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "હાજરી અહેવાલ WhatsApp/મેસેજ પર મોકલો"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), // WhatsApp Green
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("whatsapp_share_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "WhatsApp શેર",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            OutlinedButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("હાજરી અહેવાલ", reportText)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "હાજરી રિપોર્ટ કોપી કર્યો!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("copy_report_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "કોપી કરો",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Report Preview Box (Styled like a school register slip)
        Text(
            text = "અહેવાલ પ્રીવ્યૂ (School Message Preview):",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = SchoolBlueDark
        )

        Spacer(modifier = Modifier.height(6.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                .testTag("report_preview_card"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF9F6)),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = reportText,
                    fontSize = 13.sp,
                    lineHeight = 22.sp,
                    color = Color(0xFF1E293B),
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Absent Students Spotlight (if any)
        if (absentStudents.isNotEmpty()) {
            Text(
                text = "ગેરહાજર વિદ્યાર્થીઓ યાદી (${absentStudents.size}):",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AbsentRed
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                absentStudents.forEach { s ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AbsentRedBg),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "રોલ ${s.rollNo} • ${s.name}",
                                fontWeight = FontWeight.Bold,
                                color = AbsentRed,
                                fontSize = 13.sp
                            )
                            if (s.phone.isNotBlank()) {
                                Text(
                                    text = "વાલી: ${s.phone}",
                                    fontSize = 11.sp,
                                    color = AbsentRed.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = PresentGreenBg
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎉 ૧૦૦% હાજરી! આજે તમામ વિદ્યાર્થીઓ શાળામાં ઉપસ્થિત છે.",
                        fontWeight = FontWeight.Bold,
                        color = PresentGreen,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
