package com.example.studyplanner.ui.calendar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyplanner.R;
import com.example.studyplanner.data.dao.ScheduleDao;
import com.example.studyplanner.data.dao.TaskDao;
import com.example.studyplanner.data.model.Task;
import com.example.studyplanner.ui.academic.AddEditTaskActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SchedulePlannerActivity extends AppCompatActivity {

    private ScheduleDao scheduleDao;
    private TaskDao taskDao;

    private CalendarView calendarView;
    private MaterialButton btnAutoSchedule, btnAddTask;
    private TextView tvSectionTitle;
    private LinearLayout llTaskContainer;

    private String selectedDateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_planner);

        scheduleDao = new ScheduleDao(this);
        scheduleDao.open();

        taskDao = new TaskDao(this);
        taskDao.open();

        calendarView = findViewById(R.id.calendarView);
        btnAutoSchedule = findViewById(R.id.btnAutoSchedule);
        btnAddTask = findViewById(R.id.btnAddTask);
        tvSectionTitle = findViewById(R.id.tvSectionTitle);
        llTaskContainer = findViewById(R.id.llTaskContainer);

        // Lấy ngày hôm nay mặc định
        Calendar today = Calendar.getInstance();
        selectedDateStr = formatDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        if (tvSectionTitle != null) {
            tvSectionTitle.setText("Nhiệm vụ & Lịch học ngày " + today.get(Calendar.DAY_OF_MONTH) + "/" + (today.get(Calendar.MONTH) + 1));
        }

        // Bắt sự kiện click chọn ngày trên Calendar
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDateStr = formatDate(year, month, dayOfMonth);
            if (tvSectionTitle != null) {
                tvSectionTitle.setText("Nhiệm vụ & Lịch học ngày " + dayOfMonth + "/" + (month + 1));
            }
            loadTasksForSelectedDate();
        });

        // 1. NÚT GỢI Ý LỊCH AI: Sinh lịch ôn tập tự động ngay tức thì
        btnAutoSchedule.setOnClickListener(v -> generateSmartAutoSchedule());

        // 2. NÚT BÁO THỨC: Mở form thêm Task/Lịch nhắc nhở
        btnAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(SchedulePlannerActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });

        loadTasksForSelectedDate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasksForSelectedDate();
    }

    private String formatDate(int year, int month, int day) {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
    }

    private void loadTasksForSelectedDate() {
        List<Task> allTasks = taskDao.getAllTasks();
        List<Task> filteredTasks = new ArrayList<>();

        for (Task task : allTasks) {
            if (task.getDeadline() != null && task.getDeadline().contains(selectedDateStr)) {
                filteredTasks.add(task);
            }
        }

        // Nếu ngày được chọn chưa có Task riêng, hiển thị tất cả Task hiện có để không bị trống màn hình
        if (filteredTasks.isEmpty()) {
            filteredTasks = allTasks;
        }

        renderTaskList(filteredTasks);
    }

    private void renderTaskList(List<Task> tasks) {
        llTaskContainer.removeAllViews();

        if (tasks == null || tasks.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("🎉 Chưa có lịch học nào. Bấm 'Gợi ý lịch AI' để tạo tự động!");
            tvEmpty.setPadding(30, 40, 30, 40);
            tvEmpty.setTextColor(Color.GRAY);
            tvEmpty.setTextSize(14);
            llTaskContainer.addView(tvEmpty);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Task task : tasks) {
            View itemView = inflater.inflate(R.layout.item_task, llTaskContainer, false);

            TextView tvPriority = itemView.findViewById(R.id.tvPriority);
            TextView tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            TextView tvDesc = itemView.findViewById(R.id.tvTaskDesc);
            TextView tvDeadline = itemView.findViewById(R.id.tvDeadline);
            TextView tvLocation = itemView.findViewById(R.id.tvLocation);

            if (tvTitle != null) tvTitle.setText(task.getTitle());
            if (tvDesc != null) tvDesc.setText(task.getDescription());
            if (tvDeadline != null) tvDeadline.setText("🕒 " + (task.getDeadline() != null ? task.getDeadline() : "Không có hạn"));
            if (tvLocation != null) tvLocation.setText("📍 " + (task.getLocation() != null ? task.getLocation() : "Chưa có địa điểm"));

            if (tvPriority != null) {
                String cat = task.getCategory() != null ? task.getCategory() : "STUDY";
                if (cat.equalsIgnoreCase("EXAM")) {
                    tvPriority.setText("🔴 Lịch thi");
                    tvPriority.setBackgroundResource(R.drawable.bg_type_exam);
                } else if (cat.equalsIgnoreCase("ASSIGNMENT")) {
                    tvPriority.setText("🟠 Bài tập");
                    tvPriority.setBackgroundResource(R.drawable.bg_type_assignment);
                } else {
                    tvPriority.setText("🔵 AI Gợi ý");
                    tvPriority.setBackgroundResource(R.drawable.bg_type_study);
                }
            }

            llTaskContainer.addView(itemView);
        }
    }

    // TỰ ĐỘNG TẠO LỊCH ÔN TẬP GỢI Ý VÀO NGÀY ĐANG CHỌN
    private void generateSmartAutoSchedule() {
        Task autoSession1 = new Task();
        autoSession1.setTitle("[AI Auto] Ôn tập Lý thuyết trọng tâm");
        autoSession1.setDescription("Hệ thống phân bổ 45 phút ôn tập dựa trên deadline sắp tới.");
        autoSession1.setCategory("AUTO");
        autoSession1.setStatus(0);
        autoSession1.setPriority(1);
        autoSession1.setDeadline(selectedDateStr + " 08:00 AM");
        autoSession1.setLocation("Phòng tự học / Nhà");

        Task autoSession2 = new Task();
        autoSession2.setTitle("[AI Auto] Làm bài tập tự luyện");
        autoSession2.setDescription("Khung giờ vàng tập trung cao độ (Phương pháp Pomodoro).");
        autoSession2.setCategory("AUTO");
        autoSession2.setStatus(0);
        autoSession2.setPriority(1);
        autoSession2.setDeadline(selectedDateStr + " 02:00 PM");
        autoSession2.setLocation("Thư viện");

        taskDao.insertTask(autoSession1);
        taskDao.insertTask(autoSession2);

        Toast.makeText(this, "🤖 Đã phân bổ 2 buổi ôn tập AI vào ngày " + selectedDateStr + "!", Toast.LENGTH_LONG).show();
        loadTasksForSelectedDate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scheduleDao != null) scheduleDao.close();
        if (taskDao != null) taskDao.close();
    }
}