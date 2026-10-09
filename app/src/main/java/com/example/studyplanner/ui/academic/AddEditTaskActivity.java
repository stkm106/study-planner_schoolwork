package com.example.studyplanner.ui.academic;

import android.os.Bundle;
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

public class AddEditTaskActivity extends AppCompatActivity {

    private TextView tvDialogTitle;
    private Spinner spCategory, spPriority;
    private EditText etTaskTitle, etTaskDesc, etDeadline, etLocation;
    private Button btnSave, btnCancel;

    private TaskDao taskDao;
    private int currentTaskId = -1; // -1 là Thêm mới, khác -1 là Sửa
    private Task currentTask;

    private String[] categories = {"ASSIGNMENT", "EXAM", "HOMEWORK", "PROJECT"};
    private String[] priorities = {"Low (1)", "Medium (2)", "High (3)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_task); // Dùng lại layout pop-up của m

        taskDao = new TaskDao(this);
        initViews();
        setupSpinners();

        // Kiểm tra xem là THÊM MỚI hay SỬA
        if (getIntent().hasExtra("TASK_ID")) {
            currentTaskId = getIntent().getIntExtra("TASK_ID", -1);
        }

        if (currentTaskId != -1) {
            // MỐT SỬA: Lấy dữ liệu cũ đổ lên Pop-up
            tvDialogTitle.setText("Edit Task");
            btnSave.setText("Update Task");
            loadTaskData(currentTaskId);
        } else {
            // MỐT THÊM MỚI
            tvDialogTitle.setText("Create new task");
            btnSave.setText("Save Task");
        }

        btnCancel.setOnClickListener(v -> finish()); // Đóng pop-up

        btnSave.setOnClickListener(v -> saveOrUpdateTask());
    }

    private void initViews() {
        tvDialogTitle = findViewById(R.id.tvDialogTitle);
        spCategory = findViewById(R.id.spCategory);
        spPriority = findViewById(R.id.spPriority);
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDesc = findViewById(R.id.etTaskDesc);
        etDeadline = findViewById(R.id.etDeadline);
        etLocation = findViewById(R.id.etLocation);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupSpinners() {
        spCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories));
        spPriority.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, priorities));
    }

    // Đổ thông tin cũ vào Pop-up khi bấm EDIT
    private void loadTaskData(int taskId) {
        currentTask = taskDao.getTaskById(taskId);
        if (currentTask != null) {
            etTaskTitle.setText(currentTask.getTitle());
            etTaskDesc.setText(currentTask.getDescription());
            etDeadline.setText(currentTask.getDeadline());
            etLocation.setText(currentTask.getLocation());

            // Set vị trí cho Spinner Category
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equalsIgnoreCase(currentTask.getCategory())) {
                    spCategory.setSelection(i);
                    break;
                }
            }

            // Set vị trí cho Spinner Priority (Priority từ 1-3 nên trừ 1 để lấy index)
            int priorityIndex = currentTask.getPriority() - 1;
            if (priorityIndex >= 0 && priorityIndex < priorities.length) {
                spPriority.setSelection(priorityIndex);
            }
        }
    }

    private void saveOrUpdateTask() {
        String title = etTaskTitle.getText().toString().trim();
        String desc = etTaskDesc.getText().toString().trim();
        String deadline = etDeadline.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();
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
            newTask.setCategory(category);
            newTask.setPriority(priority);
            newTask.setDeadline(deadline);
            newTask.setLocation(location);
            newTask.setStatus(0);

            long result = taskDao.insertTask(newTask);
            if (result > 0) {
                Toast.makeText(this, "Đã thêm thành công!", Toast.LENGTH_SHORT).show();
                finish(); // Đóng pop-up sau khi lưu
            }
        } else {
            // CẬP NHẬT (SỬA)
            currentTask.setTitle(title);
            currentTask.setDescription(desc);
            currentTask.setCategory(category);
            currentTask.setPriority(priority);
            currentTask.setDeadline(deadline);
            currentTask.setLocation(location);

            int result = taskDao.updateTask(currentTask);
            if (result > 0) {
                Toast.makeText(this, "Đã cập nhật thành công!", Toast.LENGTH_SHORT).show();
                finish(); // Đóng pop-up sau khi cập nhật
            }
        }
    }
}