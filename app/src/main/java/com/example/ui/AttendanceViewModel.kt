package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AttendanceRecord
import com.example.data.local.AttendanceStatus
import com.example.data.local.ClassSettings
import com.example.data.local.Student
import com.example.data.repository.AttendanceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AttendanceFilter {
    ALL,
    ONLY_ABSENT,
    ONLY_PRESENT,
    ONLY_LEAVE
}

data class DailyStats(
    val total: Int = 0,
    val present: Int = 0,
    val absent: Int = 0,
    val leave: Int = 0,
    val halfDay: Int = 0,
    val percentage: Float = 0f
)

data class StudentMonthlySummary(
    val student: Student,
    val totalWorkingDays: Int,
    val presentCount: Int,
    val absentCount: Int,
    val leaveCount: Int,
    val halfDayCount: Int,
    val percentage: Float
)

@OptIn(ExperimentalCoroutinesApi::class)
class AttendanceViewModel(private val repository: AttendanceRepository) : ViewModel() {

    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val monthFormat = SimpleDateFormat("yyyy-MM", Locale.US)

    private val _selectedDate = MutableStateFlow(isoDateFormat.format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedMonth = MutableStateFlow(monthFormat.format(Date()))
    val selectedMonth: StateFlow<String> = _selectedMonth.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeFilter = MutableStateFlow(AttendanceFilter.ALL)
    val activeFilter: StateFlow<AttendanceFilter> = _activeFilter.asStateFlow()

    val students: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val classSettings: StateFlow<ClassSettings> = repository.classSettings
        .map { it ?: ClassSettings() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ClassSettings())

    // Daily attendance map: studentId -> AttendanceRecord
    val dailyAttendanceMap: StateFlow<Map<Long, AttendanceRecord>> = _selectedDate
        .flatMapLatest { date ->
            repository.getAttendanceForDate(date).map { list ->
                list.associateBy { it.studentId }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Monthly attendance list
    val monthlyAttendanceList: StateFlow<List<AttendanceRecord>> = _selectedMonth
        .flatMapLatest { month ->
            repository.getAttendanceForMonth("$month%")
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Daily stats calculated reactively
    val dailyStats: StateFlow<DailyStats> = combine(
        students,
        dailyAttendanceMap
    ) { studentList, attMap ->
        val total = studentList.size
        var present = 0
        var absent = 0
        var leave = 0
        var halfDay = 0

        studentList.forEach { student ->
            val record = attMap[student.id]
            val status = record?.let { AttendanceStatus.fromCode(it.status) } ?: AttendanceStatus.PRESENT
            when (status) {
                AttendanceStatus.PRESENT -> present++
                AttendanceStatus.ABSENT -> absent++
                AttendanceStatus.LEAVE -> leave++
                AttendanceStatus.HALF_DAY -> halfDay++
            }
        }

        val pct = if (total > 0) ((present + (halfDay * 0.5f)) / total) * 100f else 0f
        DailyStats(
            total = total,
            present = present,
            absent = absent,
            leave = leave,
            halfDay = halfDay,
            percentage = pct
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyStats())

    // Monthly summary per student
    val monthlyStudentSummaries: StateFlow<List<StudentMonthlySummary>> = combine(
        students,
        monthlyAttendanceList
    ) { studentList, monthRecords ->
        val distinctDates = monthRecords.map { it.date }.distinct().size
        val totalWorkingDays = if (distinctDates == 0) 1 else distinctDates

        studentList.map { student ->
            val studentRecords = monthRecords.filter { it.studentId == student.id }
            var pCount = 0
            var aCount = 0
            var lCount = 0
            var hdCount = 0

            studentRecords.forEach { rec ->
                when (AttendanceStatus.fromCode(rec.status)) {
                    AttendanceStatus.PRESENT -> pCount++
                    AttendanceStatus.ABSENT -> aCount++
                    AttendanceStatus.LEAVE -> lCount++
                    AttendanceStatus.HALF_DAY -> hdCount++
                }
            }

            val pct = if (totalWorkingDays > 0) {
                ((pCount + (hdCount * 0.5f)) / totalWorkingDays) * 100f
            } else 0f

            StudentMonthlySummary(
                student = student,
                totalWorkingDays = totalWorkingDays,
                presentCount = pCount,
                absentCount = aCount,
                leaveCount = lCount,
                halfDayCount = hdCount,
                percentage = pct.coerceIn(0f, 100f)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializeDefaultDataIfEmpty()
        }
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
        // keep selectedMonth synced
        if (date.length >= 7) {
            _selectedMonth.value = date.substring(0, 7)
        }
    }

    fun setSelectedMonth(month: String) {
        _selectedMonth.value = month
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setActiveFilter(filter: AttendanceFilter) {
        _activeFilter.value = filter
    }

    fun goToPreviousDay() {
        val cal = Calendar.getInstance()
        cal.time = isoDateFormat.parse(_selectedDate.value) ?: Date()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        setSelectedDate(isoDateFormat.format(cal.time))
    }

    fun goToNextDay() {
        val cal = Calendar.getInstance()
        cal.time = isoDateFormat.parse(_selectedDate.value) ?: Date()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        setSelectedDate(isoDateFormat.format(cal.time))
    }

    fun goToToday() {
        setSelectedDate(isoDateFormat.format(Date()))
    }

    fun setStudentAttendance(studentId: Long, status: AttendanceStatus, remarks: String = "") {
        viewModelScope.launch {
            repository.setAttendanceStatus(
                studentId = studentId,
                date = _selectedDate.value,
                status = status,
                remarks = remarks
            )
        }
    }

    fun markAll(status: AttendanceStatus) {
        viewModelScope.launch {
            val studentList = students.value
            repository.markAllStudents(studentList, _selectedDate.value, status)
        }
    }

    fun saveStudent(student: Student) {
        viewModelScope.launch {
            if (student.id == 0L) {
                repository.addStudent(student)
            } else {
                repository.updateStudent(student)
            }
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.deleteStudent(student)
        }
    }

    fun updateSettings(settings: ClassSettings) {
        viewModelScope.launch {
            repository.updateClassSettings(settings)
        }
    }

    // Gujarati formatted report for WhatsApp / SMS
    fun generateDailyReportMessage(): String {
        val settings = classSettings.value
        val dateStr = _selectedDate.value
        val stats = dailyStats.value
        val studentList = students.value
        val attMap = dailyAttendanceMap.value

        val formattedDate = try {
            val date = isoDateFormat.parse(dateStr)
            val dayName = SimpleDateFormat("EEEE", Locale("gu", "IN")).format(date ?: Date())
            val dateDisplay = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(date ?: Date())
            "$dateDisplay ($dayName)"
        } catch (_: Exception) {
            dateStr
        }

        val absentStudents = studentList.filter { student ->
            val rec = attMap[student.id]
            val status = rec?.let { AttendanceStatus.fromCode(it.status) } ?: AttendanceStatus.PRESENT
            status == AttendanceStatus.ABSENT
        }

        val leaveStudents = studentList.filter { student ->
            val rec = attMap[student.id]
            val status = rec?.let { AttendanceStatus.fromCode(it.status) } ?: AttendanceStatus.PRESENT
            status == AttendanceStatus.LEAVE
        }

        val sb = StringBuilder()
        sb.append("🏫 ${settings.schoolName}\n")
        sb.append("📋 દૈનિક હાજરી પત્રક અહેવાલ\n")
        sb.append("📅 તારીખ: $formattedDate\n")
        sb.append("📖 ધોરણ: ${settings.standard} (વર્ગ: ${settings.section})\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("👥 કુલ વિદ્યાર્થી સંખ્યા: ${stats.total}\n")
        sb.append("✅ હાજર સંખ્યા: ${stats.present}\n")
        sb.append("❌ ગેરહાજર સંખ્યા: ${stats.absent}\n")
        if (stats.leave > 0) {
            sb.append("📝 રજા પર: ${stats.leave}\n")
        }
        if (stats.halfDay > 0) {
            sb.append("⏱️ અડધો દિવસ: ${stats.halfDay}\n")
        }
        sb.append(String.format(Locale.US, "📊 હાજરી ટકાવારી: %.1f%%\n", stats.percentage))
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")

        if (absentStudents.isNotEmpty()) {
            sb.append("🔴 ગેરહાજર વિદ્યાર્થીઓ (${absentStudents.size}):\n")
            absentStudents.forEach { s ->
                sb.append("• રોલ નં. ${s.rollNo} - ${s.name}\n")
            }
            sb.append("────────────────────\n")
        } else {
            sb.append("🌟 આજે તમામ વિદ્યાર્થીઓ હાજર છે!\n")
            sb.append("────────────────────\n")
        }

        if (leaveStudents.isNotEmpty()) {
            sb.append("🟡 રજા પર વિદ્યાર્થીઓ (${leaveStudents.size}):\n")
            leaveStudents.forEach { s ->
                val rec = attMap[s.id]
                val remark = if (!rec?.remarks.isNullOrBlank()) " (${rec?.remarks})" else ""
                sb.append("• રોલ નં. ${s.rollNo} - ${s.name}$remark\n")
            }
            sb.append("────────────────────\n")
        }

        sb.append("👨‍🏫 વર્ગ શિક્ષક: ${settings.teacherName}\n")
        sb.append("શૈક્ષણિક વર્ષ: ${settings.academicYear}")

        return sb.toString()
    }
}

class AttendanceViewModelFactory(private val repository: AttendanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttendanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttendanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
