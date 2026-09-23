package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.ClassSettings

@Composable
fun SettingsDialog(
    currentSettings: ClassSettings,
    onSave: (ClassSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var schoolName by remember { mutableStateOf(currentSettings.schoolName) }
    var standard by remember { mutableStateOf(currentSettings.standard) }
    var section by remember { mutableStateOf(currentSettings.section) }
    var teacherName by remember { mutableStateOf(currentSettings.teacherName) }
    var academicYear by remember { mutableStateOf(currentSettings.academicYear) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("શાળા અને વર્ગ વિગત (Settings)", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = schoolName,
                    onValueChange = { schoolName = it },
                    label = { Text("શાળાનું નામ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = standard,
                    onValueChange = { standard = it },
                    label = { Text("ધોરણ (દા.ત. ધોરણ ૭)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = section,
                    onValueChange = { section = it },
                    label = { Text("વર્ગ / વિભાગ (દા.ત. અ, બ)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = teacherName,
                    onValueChange = { teacherName = it },
                    label = { Text("વર્ગ શિક્ષકનું નામ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = academicYear,
                    onValueChange = { academicYear = it },
                    label = { Text("શૈક્ષણિક વર્ષ (દા.ત. ૨૦૨૬-૨૭)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        currentSettings.copy(
                            schoolName = schoolName.trim(),
                            standard = standard.trim(),
                            section = section.trim(),
                            teacherName = teacherName.trim(),
                            academicYear = academicYear.trim()
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
