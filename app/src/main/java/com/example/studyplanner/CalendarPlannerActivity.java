package com.example.studyplanner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.studyplanner.R;
import com.example.studyplanner.data.TaskEntity;
import com.example.studyplanner.engine.WorkloadScheduler;
import com.example.studyplanner.notification.NotificationScheduler;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarPlannerActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_CODE = 101;

    private CalendarView calendarView;
    private LinearLayout llTaskContainer;
    private MaterialButton btnAddTask, btnAutoSchedule;

    private final List<TaskEntity> allTasks = new ArrayList<>();
    private final List<TaskEntity> filteredTasks = new ArrayList<>();
    private long selectedDateMillis = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_planner);

        // 1. Kiểm tra quyền gửi thông báo (Android 13+)
        checkNotificationPermission();

        // 2. Ánh xạ các View từ Layout XML
        calendarView = findViewById(R.id.calendarView);
        llTaskContainer = findViewById(R.id.llTaskContainer);
        btnAddTask = findViewById(R.id.btnAddTask);
        btnAutoSchedule = findViewById(R.id.btnAutoSchedule);

        // 3. Lắng nghe sự kiện chọn ngày trên Lịch
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(year, month, dayOfMonth, 0, 0, 0);
            selectedCal.set(Calendar.MILLISECOND, 0);

            selectedDateMillis = selectedCal.getTimeInMillis();
            filterTasksByDate(selectedDateMillis);
        });

        // 4. Gán sự kiện Click cho nút "+ Thêm mới"
        btnAddTask.setOnClickListener(v -> showAddTaskDialog());

        // 5. Gán sự kiện Click cho nút "Gợi ý lịch học tự động"
        btnAutoSchedule.setOnClickListener(v -> generateAutoSchedule());

        // Hiển thị danh sách ban đầu cho ngày hiện tại
        filterTasksByDate(selectedDateMillis);
    }

    // Hộp thoại (Dialog) để thêm Nhiệm vụ / Lịch thi mới
    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        RadioGroup rgType = dialogView.findViewById(R.id.rgType);
        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        EditText etDuration = dialogView.findViewById(R.id.etDuration);
        EditText etPriority = dialogView.findViewById(R.id.etPriority);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String durationStr = etDuration.getText().toString().trim();
            String priorityStr = etPriority.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên môn thi / bài tập!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Đọc giá trị Loại từ RadioGroup
            int checkedId = rgType.getCheckedRadioButtonId();
            String type = "EXAM";
            if (checkedId == R.id.rbAssignment) {
                type = "ASSIGNMENT";
            } else if (checkedId == R.id.rbStudy) {
                type = "STUDY";
            }

            int duration = durationStr.isEmpty() ? 90 : Integer.parseInt(durationStr);
            int priority = priorityStr.isEmpty() ? 3 : Integer.parseInt(priorityStr);

            // Tạo TaskEntity theo đúng loại đã chọn
            TaskEntity newTask = new TaskEntity(
                    title,
                    description,
                    selectedDateMillis,
                    type,
                    duration,
                    priority
            );

            allTasks.add(newTask);

            // Đặt lịch thông báo nhắc nhở
            long advanceMinutes = "EXAM".equals(type) ? 30 : 15;
            NotificationScheduler.scheduleTaskReminder(this, newTask, advanceMinutes);

            // Cập nhật lại danh sách hiển thị
            filterTasksByDate(selectedDateMillis);

            Toast.makeText(this, "Đã lưu thành công!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // Tự động sinh lịch học dựa trên thuật toán khối lượng công việc
    private void generateAutoSchedule() {
        if (allTasks.isEmpty()) {
            Toast.makeText(this, "Hãy thêm ít nhất 1 bài tập/lịch thi để hệ thống gợi ý!", Toast.LENGTH_LONG).show();
            return;
        }

        List<TaskEntity> autoSessions = WorkloadScheduler.generateAutoSchedule(new ArrayList<>(allTasks));
        if (autoSessions.isEmpty()) {
            Toast.makeText(this, "Không có lịch học mới nào được tạo thêm.", Toast.LENGTH_SHORT).show();
            return;
        }

        allTasks.addAll(autoSessions);
        filterTasksByDate(selectedDateMillis);

        Toast.makeText(this, "Đã tự động tạo " + autoSessions.size() + " buổi học hợp lý!", Toast.LENGTH_LONG).show();
    }

    // Lọc danh sách công việc hiển thị theo ngày được chọn và đổ trực tiếp vào LinearLayout
    private void filterTasksByDate(long selectedDayStartMs) {
        long selectedDayEndMs = selectedDayStartMs + (24 * 60 * 60 * 1000) - 1;
        filteredTasks.clear();

        for (TaskEntity task : allTasks) {
            if (task.dateTimeMillis >= selectedDayStartMs && task.dateTimeMillis <= selectedDayEndMs) {
                filteredTasks.add(task);
            }
        }

        // Xóa sạch các view cũ trong LinearLayout trước khi render lại
        llTaskContainer.removeAllViews();

        // Duyệt qua từng task và tạo view thủ công gieo vào LinearLayout
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());

        for (TaskEntity task : filteredTasks) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_task, llTaskContainer, false);

            TextView tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            TextView tvDescription = itemView.findViewById(R.id.tvTaskDescription);
            TextView tvType = itemView.findViewById(R.id.tvTaskType);
            TextView tvTime = itemView.findViewById(R.id.tvTaskTime);
            TextView tvDuration = itemView.findViewById(R.id.tvTaskDuration);
            CheckBox cbCompleted = itemView.findViewById(R.id.cbCompleted);
            View viewPriority = itemView.findViewById(R.id.viewPriorityIndicator);

            tvTitle.setText(task.title);
            tvDescription.setText(task.description);
            tvType.setText(task.type);
            cbCompleted.setChecked(task.isCompleted);

            tvTime.setText("🕒 " + sdf.format(new Date(task.dateTimeMillis)));
            tvDuration.setText("⏳ " + task.durationMinutes + " phút");

            // Đổi màu Badge theo loại công việc
            if ("EXAM".equalsIgnoreCase(task.type)) {
                tvType.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_type_exam));
            } else if ("ASSIGNMENT".equalsIgnoreCase(task.type)) {
                tvType.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_type_assignment));
            } else {
                tvType.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_type_study));
            }

            // Đổi màu vạch chỉ số ưu tiên
            if (task.priority == 3) {
                viewPriority.setBackgroundColor(Color.RED);
            } else if (task.priority == 2) {
                viewPriority.setBackgroundColor(Color.parseColor("#FF9800"));
            } else {
                viewPriority.setBackgroundColor(Color.GREEN);
            }

            cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> task.isCompleted = isChecked);

            // Thêm item view vào container chính
            llTaskContainer.addView(itemView);
        }
    }

    // Kiểm tra quyền thông báo cho Android 13+
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cấp quyền thông báo!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}