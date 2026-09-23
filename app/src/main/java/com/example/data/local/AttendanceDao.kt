package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    // Students
    @Query("SELECT * FROM students WHERE active = 1 ORDER BY rollNo ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT COUNT(*) FROM students WHERE active = 1")
    suspend fun getStudentCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    // Attendance
    @Query("SELECT * FROM attendance_records WHERE date = :date")
    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE date LIKE :monthPrefix ORDER BY date ASC")
    fun getAttendanceForMonth(monthPrefix: String): Flow<List<AttendanceRecord>>

    @Upsert
    suspend fun upsertAttendanceRecord(record: AttendanceRecord)

    @Upsert
    suspend fun upsertAttendanceRecords(records: List<AttendanceRecord>)

    @Query("DELETE FROM attendance_records WHERE date = :date")
    suspend fun clearAttendanceForDate(date: String)

    // Settings
    @Query("SELECT * FROM class_settings WHERE id = 1")
    fun getClassSettings(): Flow<ClassSettings?>

    @Upsert
    suspend fun upsertClassSettings(settings: ClassSettings)
}
