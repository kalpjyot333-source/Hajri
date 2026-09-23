package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rollNo: Int,
    val grNo: String = "",
    val name: String,
    val gender: String = "કુમાર", // "કુમાર" (Boy) or "કન્યા" (Girl)
    val phone: String = "",
    val active: Boolean = true
)
