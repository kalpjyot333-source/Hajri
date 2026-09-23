package com.example.data.repository

import com.example.data.local.AttendanceDao
import com.example.data.local.AttendanceRecord
import com.example.data.local.AttendanceStatus
import com.example.data.local.ClassSettings
import com.example.data.local.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AttendanceRepository(private val dao: AttendanceDao) {

    val allStudents: Flow<List<Student>> = dao.getAllStudents()
    val classSettings: Flow<ClassSettings?> = dao.getClassSettings()

    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecord>> {
        return dao.getAttendanceForDate(date)
    }

    fun getAttendanceForMonth(monthPrefix: String): Flow<List<AttendanceRecord>> {
        return dao.getAttendanceForMonth(monthPrefix)
    }

    suspend fun initializeDefaultDataIfEmpty() = withContext(Dispatchers.IO) {
        val count = dao.getStudentCount()
        if (count == 0) {
            val defaultStudents = listOf(
                Student(rollNo = 1, grNo = "GR-1401", name = "પટેલ આરવ કે.", gender = "કુમાર", phone = "9825012345"),
                Student(rollNo = 2, grNo = "GR-1402", name = "શાહ દિયા એમ.", gender = "કન્યા", phone = "9825012346"),
                Student(rollNo = 3, grNo = "GR-1403", name = "પરમાર ખુશી એસ.", gender = "કન્યા", phone = "9825012347"),
                Student(rollNo = 4, grNo = "GR-1404", name = "વ્યાસ મનન પી.", gender = "કુમાર", phone = "9825012348"),
                Student(rollNo = 5, grNo = "GR-1405", name = "સોલંકી રોહન બી.", gender = "કુમાર", phone = "9825012349"),
                Student(rollNo = 6, grNo = "GR-1406", name = "ચૌહાણ પ્રિયા જે.", gender = "કન્યા", phone = "9825012350"),
                Student(rollNo = 7, grNo = "GR-1407", name = "દેસાઈ મિત વી.", gender = "કુમાર", phone = "9825012351"),
                Student(rollNo = 8, grNo = "GR-1408", name = "જોશી નેહા આર.", gender = "કન્યા", phone = "9825012352"),
                Student(rollNo = 9, grNo = "GR-1409", name = "ઠાકોર કરણ ડી.", gender = "કુમાર", phone = "9825012353"),
                Student(rollNo = 10, grNo = "GR-1410", name = "મેવાડા રુદ્ર એચ.", gender = "કુમાર", phone = "9825012354"),
                Student(rollNo = 11, grNo = "GR-1411", name = "પંચાલ તનિષ્કા એન.", gender = "કન્યા", phone = "9825012355"),
                Student(rollNo = 12, grNo = "GR-1412", name = "રાઠોડ હર્ષ જી.", gender = "કુમાર", phone = "9825012356"),
                Student(rollNo = 13, grNo = "GR-1413", name = "વાઘેલા અંજલિ ટી.", gender = "કન્યા", phone = "9825012357"),
                Student(rollNo = 14, grNo = "GR-1414", name = "દરજી પ્રિન્સ એમ.", gender = "કુમાર", phone = "9825012358"),
                Student(rollNo = 15, grNo = "GR-1415", name = "ચાવડા પૂજા એલ.", gender = "કન્યા", phone = "9825012359"),
                Student(rollNo = 16, grNo = "GR-1416", name = "પ્રજાપતિ વિવેક કે.", gender = "કુમાર", phone = "9825012360"),
                Student(rollNo = 17, grNo = "GR-1417", name = "બારૈયા સ્નેહા આર.", gender = "કન્યા", phone = "9825012361"),
                Student(rollNo = 18, grNo = "GR-1418", name = "ગોહિલ ધ્રુવ એસ.", gender = "કુમાર", phone = "9825012362"),
                Student(rollNo = 19, grNo = "GR-1419", name = "રાવળ દિવ્યા બી.", gender = "કન્યા", phone = "9825012363"),
                Student(rollNo = 20, grNo = "GR-1420", name = "મકવાણા ઓમ પી.", gender = "કુમાર", phone = "9825012364")
            )
            dao.insertStudents(defaultStudents)
            dao.upsertClassSettings(
                ClassSettings(
                    id = 1,
                    standard = "ધોરણ ૭",
                    section = "અ",
                    schoolName = "શ્રી પ્રાથમિક શાળા",
                    teacherName = "શ્રી રમેશભાઈ પટેલ",
                    academicYear = "૨૦૨૬-૨૭"
                )
            )
        }
    }

    suspend fun setAttendanceStatus(studentId: Long, date: String, status: AttendanceStatus, remarks: String = "") {
        withContext(Dispatchers.IO) {
            dao.upsertAttendanceRecord(
                AttendanceRecord(
                    studentId = studentId,
                    date = date,
                    status = status.code,
                    remarks = remarks
                )
            )
        }
    }

    suspend fun markAllStudents(students: List<Student>, date: String, status: AttendanceStatus) {
        withContext(Dispatchers.IO) {
            val records = students.map { student ->
                AttendanceRecord(
                    studentId = student.id,
                    date = date,
                    status = status.code,
                    remarks = ""
                )
            }
            dao.upsertAttendanceRecords(records)
        }
    }

    suspend fun addStudent(student: Student): Long = withContext(Dispatchers.IO) {
        dao.insertStudent(student)
    }

    suspend fun updateStudent(student: Student) = withContext(Dispatchers.IO) {
        dao.updateStudent(student)
    }

    suspend fun deleteStudent(student: Student) = withContext(Dispatchers.IO) {
        dao.deleteStudent(student)
    }

    suspend fun updateClassSettings(settings: ClassSettings) = withContext(Dispatchers.IO) {
        dao.upsertClassSettings(settings)
    }
}
