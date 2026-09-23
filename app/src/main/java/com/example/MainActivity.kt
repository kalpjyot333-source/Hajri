package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.AttendanceRepository
import com.example.ui.AttendanceViewModel
import com.example.ui.AttendanceViewModelFactory
import com.example.ui.screens.DailyAttendanceScreen
import com.example.ui.screens.DailyReportScreen
import com.example.ui.screens.MonthlyRegisterScreen
import com.example.ui.screens.SettingsDialog
import com.example.ui.screens.StudentListScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SchoolBlueDark
import com.example.ui.theme.SchoolBluePrimary
import com.example.ui.theme.TextMuted

enum class AppTab(val title: String, val testTag: String) {
    DAILY("દૈનિક હાજરી", "tab_daily_attendance"),
    MONTHLY("માસિક પત્રક", "tab_monthly_register"),
    STUDENTS("વિદ્યાર્થી યાદી", "tab_student_list"),
    REPORT("દૈનિક રિપોર્ટ", "tab_daily_report")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AttendanceApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceApp() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { AttendanceRepository(database.attendanceDao()) }
    val viewModel: AttendanceViewModel = viewModel(factory = AttendanceViewModelFactory(repository))

    var currentTab by remember { mutableStateOf(AppTab.DAILY) }
    var isSettingsOpen by remember { mutableStateOf(false) }
    val classSettings by viewModel.classSettings.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${classSettings.standard} હાજરી પત્રક",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${classSettings.schoolName} • વર્ગ-${classSettings.section}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SchoolBluePrimary
                ),
                actions = {
                    IconButton(
                        onClick = { isSettingsOpen = true },
                        modifier = Modifier.testTag("open_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.DAILY,
                    onClick = { currentTab = AppTab.DAILY },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = "દૈનિક હાજરી"
                        )
                    },
                    label = { Text("દૈનિક હાજરી", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SchoolBluePrimary,
                        selectedTextColor = SchoolBluePrimary,
                        indicatorColor = SchoolBluePrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag(AppTab.DAILY.testTag)
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.MONTHLY,
                    onClick = { currentTab = AppTab.MONTHLY },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "માસિક પત્રક"
                        )
                    },
                    label = { Text("માસિક પત્રક", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SchoolBluePrimary,
                        selectedTextColor = SchoolBluePrimary,
                        indicatorColor = SchoolBluePrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag(AppTab.MONTHLY.testTag)
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.STUDENTS,
                    onClick = { currentTab = AppTab.STUDENTS },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "વિદ્યાર્થી યાદી"
                        )
                    },
                    label = { Text("વિદ્યાર્થી યાદી", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SchoolBluePrimary,
                        selectedTextColor = SchoolBluePrimary,
                        indicatorColor = SchoolBluePrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag(AppTab.STUDENTS.testTag)
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.REPORT,
                    onClick = { currentTab = AppTab.REPORT },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = "દૈનિક રિપોર્ટ"
                        )
                    },
                    label = { Text("અહેવાલ", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SchoolBluePrimary,
                        selectedTextColor = SchoolBluePrimary,
                        indicatorColor = SchoolBluePrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag(AppTab.REPORT.testTag)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.DAILY -> DailyAttendanceScreen(
                    viewModel = viewModel,
                    onNavigateToReport = { currentTab = AppTab.REPORT }
                )
                AppTab.MONTHLY -> MonthlyRegisterScreen(viewModel = viewModel)
                AppTab.STUDENTS -> StudentListScreen(viewModel = viewModel)
                AppTab.REPORT -> DailyReportScreen(viewModel = viewModel)
            }
        }
    }

    if (isSettingsOpen) {
        SettingsDialog(
            currentSettings = classSettings,
            onSave = { updatedSettings ->
                viewModel.updateSettings(updatedSettings)
                isSettingsOpen = false
            },
            onDismiss = { isSettingsOpen = false }
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
