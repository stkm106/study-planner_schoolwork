# Smart Study Planner and Assignment Reminder
An application that helps students organize courses, assignments, examinations, and study schedules. The system supports reminders, calendar-based planning, progress tracking, and study-time management.

# 📁 Project Directory Structure (Android Java + SQLite)
```text
app/
 ├── src/
 │    └── main/
 │         ├── java/com/example/smartstudyplanner/
 │         │    ├── data/                      # 🗄️ LAYER 1: DATA BASE & MODELS
 │         │    │    ├── database/
 │         │    │    │    └── DatabaseHelper.java       # [Person 1] DB Helper (SQLiteOpenHelper, onCreate, onUpgrade)
 │         │    │    ├── dao/
 │         │    │    │    ├── CourseDao.java             # [Person 1] CRUD Courses
 │         │    │    │    ├── AssignmentDao.java         # [Person 1] CRUD Assignments
 │         │    │    │    ├── ExamDao.java               # [Person 1] CRUD Examinations
 │         │    │    │    ├── ScheduleDao.java           # [Person 2] Query Study Schedules
 │         │    │    │    └── StudyLogDao.java           # [Person 3] Query Pomodoro Logs & Statistics
 │         │    │    └── model/
 │         │    │         ├── Course.java
 │         │    │         ├── Assignment.java
 │         │    │         ├── Examination.java
 │         │    │         ├── Schedule.java
 │         │    │         └── StudyLog.java
 │         │    │
 │         │    ├── service/                   # ⚙️ LAYER 2: BACKGROUND SERVICES & RECEIVERS
 │         │    │    ├── PomodoroService.java        # [Person 3] Foreground Service đếm ngược Pomodoro
 │         │    │    ├── ReminderReceiver.java       # [Person 2] BroadcastReceiver nhận lịch báo thức/thông báo
 │         │    │    └── AlarmScheduler.java         # [Person 2] Helper đặt lịch AlarmManager
 │         │    │
 │         │    ├── utils/                     # 🛠️ LAYER 3: UTILITIES & HELPERS
 │         │    │    ├── DateUtils.java             # Class hỗ trợ format ngày tháng / tính toán tuần
 │         │    │    ├── WorkloadRecommender.java   # [Person 2] Thuật toán gợi ý lịch học tự động
 │         │    │    ├── OCRHelper.java             # [Person 4] Google ML Kit OCR Scanner
 │         │    │    └── FirebaseSyncHelper.java    # [Person 4] Đồng bộ SQLite <-> Firebase Cloud
 │         │    │
 │         │    └── ui/                        # 🎨 LAYER 4: USER INTERFACE (ACTIVITIES & FRAGMENTS)
 │         │         ├── main/
 │         │         │    └── MainActivity.java     # Host chính chứa BottomNavigationView
 │         │         ├── academic/                  # [Person 1] Core Academic Management
 │         │         │    ├── CourseListFragment.java
 │         │         │    ├── AssignmentAdapter.java
 │         │         │    └── AddEditAssignmentActivity.java
 │         │         ├── calendar/                  # [Person 2] Calendar & Schedule Planner
 │         │         │    ├── CalendarFragment.java
 │         │         │    └── SchedulePlannerActivity.java
 │         │         ├── productivity/              # [Person 3] Productivity Suite (Pomodoro & Stats)
 │         │         │    ├── PomodoroFragment.java
 │         │         │    └── StatisticsFragment.java
 │         │         └── ocr_sync/                  # [Person 4] OCR & Settings
 │         │              ├── OCRScanActivity.java
 │         │              └── SyncSettingsActivity.java
 │         │
 │         ├── res/                            # 🖼️ RESOURCE FILES (XML Layouts, Drawables, Values)
 │         │    ├── drawable/                  # Icons, custom rounded borders, background shapes
 │         │    ├── layout/                    # Layouts XML cho Activity, Fragment, Custom Adapter Items
 │         │    │    ├── activity_main.xml
 │         │    │    ├── fragment_pomodoro.xml  # [Person 3] UI Đồng hồ Pomodoro
 │         │    │    ├── fragment_statistics.xml# [Person 3] UI Biểu đồ MPAndroidChart
 │         │    │    ├── item_assignment.xml
 │         │    │    └── ...
 │         │    ├── menu/                      # Menu cho BottomNavigationView, Toolbar
 │         │    │    └── bottom_nav_menu.xml
 │         │    └── values/                    # Constants (Strings, Colors, Styles, Themes)
 │         │         ├── colors.xml
 │         │         ├── strings.xml
 │         │         └── themes.xml
 │         │
 │         └── AndroidManifest.xml             # 📜 File khai báo Permissions, Services, Activities của App
 └── build.gradle (Module :app)                # 📦 Khai báo thư viện (MPAndroidChart, ML Kit, Firebase...)
```
