package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "class_settings")
data class ClassSettings(
    @PrimaryKey
    val id: Int = 1,
    val standard: String = "ધોરણ ૭",
    val section: String = "અ",
    val schoolName: String = "શ્રી પ્રાથમિક શાળા",
    val teacherName: String = "શ્રી રમેશભાઈ પટેલ",
    val academicYear: String = "૨૦૨૬-૨૭"
)
