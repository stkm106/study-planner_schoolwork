package com.example.studyplanner.ui.academic;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studyplanner.R;
import com.example.studyplanner.data.dao.TaskDao;
import com.example.studyplanner.data.model.Task;

import java.util.Calendar;
import java.util.Locale;

public class AddEditTaskActivity extends AppCompatActivity {

    private TextView tvDialogTitle;
    private Spinner spCategory, spPriority;
    private EditText etTaskTitle, etTaskDesc, etCourse, etDeadline, etLocation;
    private Button btnSave, btnCancel;

    private TaskDao taskDao;
    private int currentTaskId = -1; // -1 là Thêm mới, khác -1 là Sửa
    private Task currentTask;

    private String[] categories = {"Assignment", "Exam", "Homework", "Project"};
    private String[] priorities = {"Thấp (1)", "Trung bình (2)", "Khẩn cấp (3)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Ẩn tiêu đề Dialog
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_task);

        // 2. Ẩn Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 3. Kéo rộng Popup ra 90% màn hình
        getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );

        taskDao = new TaskDao(this);
        initViews();
        setupSpinners();
        setupDateTimePicker();

        if (getIntent().hasExtra("TASK_ID")) {
            currentTaskId = getIntent().getIntExtra("TASK_ID", -1);
        }

        if (currentTaskId != -1) {
            tvDialogTitle.setText("Edit Task");
            btnSave.setText("Update Task");
            loadTaskData(currentTaskId);
        } else {
            tvDialogTitle.setText("Create new task");
            btnSave.setText("Save Task");
        }

        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveOrUpdateTask());
    }

    private void initViews() {
        tvDialogTitle = findViewById(R.id.tvDialogTitle);
        spCategory = findViewById(R.id.spCategory);
        spPriority = findViewById(R.id.spPriority);
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDesc = findViewById(R.id.etTaskDesc);
        etCourse = findViewById(R.id.etCourse);
        etDeadline = findViewById(R.id.etDeadline);
        etLocation = findViewById(R.id.etLocation);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupSpinners() {
        spCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories));
        spPriority.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, priorities));
    }

    private void setupDateTimePicker() {
        etDeadline.setFocusable(false);
        etDeadline.setClickable(true);

        etDeadline.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddEditTaskActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        int mHour = c.get(Calendar.HOUR_OF_DAY);
                        int mMinute = c.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                AddEditTaskActivity.this,
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                (timeView, hourOfDay, minute) -> {
                                    String amPm = (hourOfDay >= 12) ? "PM" : "AM";
                                    int hour12 = hourOfDay % 12;
                                    if (hour12 == 0) hour12 = 12;

                                    String formattedDateTime = String.format(Locale.getDefault(),
                                            "%d-%02d-%02d %d:%02d %s",
                                            year, (monthOfYear + 1), dayOfMonth, hour12, minute, amPm);
                                    etDeadline.setText(formattedDateTime);
                                }, mHour, mMinute, false);

                        if (timePickerDialog.getWindow() != null) {
                            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        }
                        timePickerDialog.show();
                    }, mYear, mMonth, mDay);

            if (datePickerDialog.getWindow() != null) {
                datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
            datePickerDialog.show();
        });
    }

    // Đổ thông tin cũ vào Pop-up khi bấm EDIT
    private void loadTaskData(int taskId) {
        currentTask = taskDao.getTaskById(taskId);
        if (currentTask != null) {
            etTaskTitle.setText(currentTask.getTitle());
            etTaskDesc.setText(currentTask.getDescription());
            etCourse.setText(currentTask.getCourse()); // Lấy tên môn học đổ lên ô EditText
            etDeadline.setText(currentTask.getDeadline());
            etLocation.setText(currentTask.getLocation());

            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equalsIgnoreCase(currentTask.getCategory())) {
                    spCategory.setSelection(i);
                    break;
                }
            }

            int priorityIndex = currentTask.getPriority() - 1;
            if (priorityIndex >= 0 && priorityIndex < priorities.length) {
                spPriority.setSelection(priorityIndex);
            }
        }
    }

    private void saveOrUpdateTask() {
        String title = etTaskTitle.getText().toString().trim();
        String desc = etTaskDesc.getText().toString().trim();
        String course = etCourse.getText().toString().trim(); // Lấy chữ trong ô Course
        String deadline = etDeadline.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString().toUpperCase();
        int priority = spPriority.getSelectedItemPosition() + 1;

        if (title.isEmpty()) {
            etTaskTitle.setError("Không được để trống tên");
            return;
        }

        if (currentTaskId == -1) {
            // THÊM MỚI
            Task newTask = new Task();
            newTask.setTitle(title);
            newTask.setDescription(desc);
            newTask.setCourse(course); // Lưu tên môn học vào Task
            newTask.setCategory(category);
            newTask.setPriority(priority);
            newTask.setDeadline(deadline);
            newTask.setLocation(location);
            newTask.setStatus(0);

            long result = taskDao.insertTask(newTask);
            if (result > 0) {
                Toast.makeText(this, "Đã thêm thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            // CẬP NHẬT (SỬA)
            currentTask.setTitle(title);
            currentTask.setDescription(desc);
            currentTask.setCourse(course); // Cập nhật tên môn học vào Task
            currentTask.setCategory(category);
            currentTask.setPriority(priority);
            currentTask.setDeadline(deadline);
            currentTask.setLocation(location);

            int result = taskDao.updateTask(currentTask);
            if (result > 0) {
                Toast.makeText(this, "Đã cập nhật thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}