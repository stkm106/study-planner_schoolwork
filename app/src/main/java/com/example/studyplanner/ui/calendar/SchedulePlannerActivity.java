package com.example.studyplanner.ui.calendar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studyplanner.R;
import com.example.studyplanner.data.dao.ScheduleDao;
import com.example.studyplanner.data.dao.TaskDao;
import com.example.studyplanner.data.model.Schedule;
import com.example.studyplanner.data.model.Task;
import com.example.studyplanner.utils.WorkloadRecommender;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.List;

public class SchedulePlannerActivity extends AppCompatActivity {
    private ScheduleDao scheduleDao;
    private TaskDao taskDao;

    private MaterialButton btnAutoSchedule, btnAddTask;
    private CalendarView calendarView;
    private LinearLayout llTaskContainer;
    private int selectedDayOfWeek = 2; // Mặc định Thứ 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_planner);

        // Khởi tạo các DAO
        scheduleDao = new ScheduleDao(this);
        scheduleDao.open();

        taskDao = new TaskDao(this);
        taskDao.open();

        // Ánh xạ View
        calendarView = findViewById(R.id.calendarView);
        btnAutoSchedule = findViewById(R.id.btnAutoSchedule);
        btnAddTask = findViewById(R.id.btnAddTask);
        llTaskContainer = findViewById(R.id.llTaskContainer);

        // Sự kiện chọn ngày trên CalendarView
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            loadTasks();
        });

        btnAutoSchedule.setOnClickListener(v -> generateAutoSchedules());
        btnAddTask.setOnClickListener(v -> showAddTaskDialog());

        loadTasks();
    }

    private void loadTasks() {
        List<Task> tasks = taskDao.getAllTasks();
        renderTaskList(tasks);
    }

    private void renderTaskList(List<Task> tasks) {
        llTaskContainer.removeAllViews();

        if (tasks == null || tasks.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Chưa có nhiệm vụ hoặc lịch thi nào.");
            tvEmpty.setPadding(20, 20, 20, 20);
            tvEmpty.setTextColor(Color.GRAY);
            llTaskContainer.addView(tvEmpty);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Task task : tasks) {
            View itemView = inflater.inflate(R.layout.item_task, llTaskContainer, false);

            TextView tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            TextView tvDesc = itemView.findViewById(R.id.tvTaskDescription);
            TextView tvType = itemView.findViewById(R.id.tvTaskType);
            TextView tvTime = itemView.findViewById(R.id.tvTaskTime);
            TextView tvDuration = itemView.findViewById(R.id.tvTaskDuration);
            CheckBox cbCompleted = itemView.findViewById(R.id.cbCompleted);
            View viewPriority = itemView.findViewById(R.id.viewPriorityIndicator);

            // Set dữ liệu tiêu đề và nội dung
            tvTitle.setText(task.getTitle());
            tvDesc.setText(task.getDescription());
            tvTime.setText("🕒 " + task.getDeadline());
            tvDuration.setText("⏳ " + task.getDurationMinutes() + " phút");

            // Xử lý CheckBox trạng thái
            cbCompleted.setChecked(task.getStatus() == 1);
            cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                taskDao.updateTaskStatus(task.getId(), isChecked);
            });

            // Set Badge loại công việc
            String cat = task.getCategory() != null ? task.getCategory() : "STUDY";
            switch (cat) {
                case "EXAM":
                    tvType.setText("LỊCH THI");
                    tvType.setBackgroundResource(R.drawable.bg_type_exam);
                    break;
                case "ASSIGNMENT":
                    tvType.setText("BÀI TẬP");
                    tvType.setBackgroundResource(R.drawable.bg_type_assignment);
                    break;
                case "AUTO":
                    tvType.setText("TỰ ĐỘNG");
                    tvType.setBackgroundResource(R.drawable.bg_type_study);
                    break;
                default:
                    tvType.setText("ÔN TẬP");
                    tvType.setBackgroundResource(R.drawable.bg_type_study);
                    break;
            }

            // Xử lý đổi màu vạch độ quan trọng: 1 - Xanh dương, 2 - Cam, 3 - Đỏ
            int p = task.getPriority();
            if (p == 1) {
                viewPriority.setBackgroundColor(Color.parseColor("#1E88E5")); // Xanh dương
            } else if (p == 3) {
                viewPriority.setBackgroundColor(Color.parseColor("#E53935")); // Đỏ
            } else {
                viewPriority.setBackgroundColor(Color.parseColor("#FB8C00")); // Cam (Mặc định 2)
            }

            llTaskContainer.addView(itemView);
        }
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etDuration = dialogView.findViewById(R.id.etDuration);
        EditText etPriority = dialogView.findViewById(R.id.etPriority);
        RadioGroup rgType = dialogView.findViewById(R.id.rgType);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();
            String durationStr = etDuration.getText().toString().trim();
            String priorityStr = etPriority.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên môn thi / bài tập!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Xác định Category
            String category = "EXAM";
            int checkedId = rgType.getCheckedRadioButtonId();
            if (checkedId == R.id.rbAssignment) {
                category = "ASSIGNMENT";
            } else if (checkedId == R.id.rbStudy) {
                category = "STUDY";
            }

            int duration = durationStr.isEmpty() ? 60 : Integer.parseInt(durationStr);
            int priority = priorityStr.isEmpty() ? 2 : Integer.parseInt(priorityStr);

            Task newTask = new Task(title, desc, category, priority, "Thứ " + selectedDayOfWeek, duration);
            long id = taskDao.insertTask(newTask);

            if (id > 0) {
                Toast.makeText(this, "Đã thêm nhiệm vụ mới!", Toast.LENGTH_SHORT).show();
                loadTasks();
            } else {
                Toast.makeText(this, "Thêm thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void generateAutoSchedules() {
        List<Schedule> currentSchedules = scheduleDao.getAllSchedules();
        List<Schedule> suggested = WorkloadRecommender.generateSuggestedSchedules(currentSchedules);

        if (suggested.isEmpty()) {
            Toast.makeText(this, "Không có lịch gợi ý mới!", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Schedule s : suggested) {
            scheduleDao.insertSchedule(s);
            // Đồng thời tạo Task tự động lưu vào bảng tasks
            Task autoTask = new Task(
                    "[Gợi ý] Ôn tập tự động",
                    "Khung giờ hệ thống đề xuất: " + s.getStartTime() + " - " + s.getEndTime(),
                    "AUTO",
                    1,
                    "Thứ " + s.getDayOfWeek(),
                    120
            );
            taskDao.insertTask(autoTask);
        }

        Toast.makeText(this, "Đã tạo " + suggested.size() + " lịch gợi ý tự động!", Toast.LENGTH_SHORT).show();
        loadTasks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scheduleDao != null) scheduleDao.close();
        if (taskDao != null) taskDao.close();
    }
}