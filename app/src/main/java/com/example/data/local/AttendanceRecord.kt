package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance_records",
    indices = [Index(value = ["studentId", "date"], unique = true)]
)
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val date: String, // Format "YYYY-MM-DD"
    val status: String, // "PRESENT", "ABSENT", "LEAVE", "HALF_DAY"
    val remarks: String = ""
)

enum class AttendanceStatus(val code: String, val gujaratiLabel: String, val shortCode: String) {
    PRESENT("PRESENT", "હાજર", "P"),
    ABSENT("ABSENT", "ગેરહાજર", "A"),
    LEAVE("LEAVE", "રજા", "L"),
    HALF_DAY("HALF_DAY", "અડધો દિવસ", "HD");

    companion object {
        fun fromCode(code: String?): AttendanceStatus {
            return entries.firstOrNull { it.code == code } ?: PRESENT
        }
    }
}
