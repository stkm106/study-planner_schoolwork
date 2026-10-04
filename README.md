# Smart Study Planner and Assignment Reminder
An application that helps students organize courses, assignments, examinations, and study schedules. The system supports reminders, calendar-based planning, progress tracking, and study-time management.

# Entities
1. Bảng courses (Quản lý Môn học)
```
id: INTEGER PRIMARY KEY AUTOINCREMENT
code: TEXT (Mã môn: e.g. "PRM392")
name: TEXT (Tên môn: e.g. "Lập trình Di động")
color_hex: TEXT (Mã màu hiển thị lên Calendar: e.g. "#FF5722")
```

2. Bảng assignments (Quản lý Bài tập)
```
id: INTEGER PRIMARY KEY AUTOINCREMENT
course_id: INTEGER (Khóa ngoại trỏ đến courses.id, cho phép NULL nếu bài tập chung)
title: TEXT (Tên bài tập/đồ án)
description: TEXT (Mô tả chi tiết)
deadline: TEXT (Định dạng ISO-8601: YYYY-MM-DD HH:MM:SS)
priority: INTEGER (Mức độ ưu tiên: 1 = Thấp, 2 = Trung bình, 3 = Cao)
status: INTEGER (0 = Chưa xong/Pending, 1 = Đã xong/Completed) — Phục vụ biểu đồ của bạn (Người 3)
estimated_hours: REAL (Thời gian ước tính để hoàn thành, dùng cho Auto Schedule của Người 2)
```

3. Bảng examinations (Lịch thi)
```
id: INTEGER PRIMARY KEY AUTOINCREMENT
course_id: INTEGER (Khóa ngoại trỏ đến courses.id)
title: TEXT (Tên kỳ thi: e.g. "Thi giữa kỳ", "Thi lý thuyết")
exam_date: TEXT (YYYY-MM-DD HH:MM:SS)
location: TEXT (Địa điểm/Phòng thi)
duration_minutes: INTEGER (Thời gian làm bài: e.g. 60, 90 phút)
```

4. Bảng study_schedules (Lịch học ngày/tuần)
```
id: INTEGER PRIMARY KEY AUTOINCREMENT
course_id: INTEGER (Khóa ngoại trỏ đến courses.id, nullable)
day_of_week: INTEGER (1 = Chủ Nhật, 2 = Thứ 2, ..., 7 = Thứ 7)
start_time: TEXT (HH:MM)
end_time: TEXT (HH:MM)
is_auto_generated: INTEGER (0 = Do sinh viên tự tạo, 1 = Do hệ thống gợi ý)
```

5. Bảng study_logs (Nhật ký Pomodoro)
```
id: INTEGER PRIMARY KEY AUTOINCREMENT
assignment_id: INTEGER (Khóa ngoại trỏ đến assignments.id, nullable nếu học môn chung mà không chọn bài tập cụ thể)
session_type: TEXT ("POMODORO" hoặc "MANUAL")
duration_minutes: INTEGER (Số phút đã học thực tế: e.g. 25, 50...)
completed_at: TEXT (Ngày giờ hoàn thành: YYYY-MM-DD HH:MM:SS) — Cực kỳ quan trọng để bạn query xuất biểu đồ tuần/tháng!
```

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
