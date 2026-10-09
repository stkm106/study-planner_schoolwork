package com.example.studyplanner.ui.academic;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyplanner.R;
import com.example.studyplanner.data.dao.TaskDao;
import com.example.studyplanner.data.model.Task;

import java.util.ArrayList;
import java.util.List;

public class CourseListFragment extends Fragment {

    private Spinner spinnerMonthFilter, spinnerCategoryFilter;
    private Button btnAddNew;
    private RecyclerView rvTasks;

    private TaskDao taskDao;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_academic, container, false);

        spinnerMonthFilter = view.findViewById(R.id.spinnerMonthFilter);
        spinnerCategoryFilter = view.findViewById(R.id.spinnerCategoryFilter);
        btnAddNew = view.findViewById(R.id.btnAddNew);
        rvTasks = view.findViewById(R.id.rvTasks);

        taskDao = new TaskDao(getContext());

        setupSpinners();
        setupRecyclerView();

        // NÚT THÊM MỚI: Gọi thẳng AddEditTaskActivity (Không truyền TASK_ID)
        btnAddNew.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load lại danh sách mỗi khi đóng Pop-up thêm/sửa
        loadTasks();
    }

    private void setupSpinners() {
        String[] categories = {"Assignment", "Exam", "Homework", "Project"};
        spinnerCategoryFilter.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, categories));

        String[] months = {"Hôm nay", "Tháng 1", "Tháng 2", "Tháng 3","Tháng4","Tháng 5","Tháng 6","Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        spinnerMonthFilter.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, months));
    }

    private void setupRecyclerView() {
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter(getContext(), taskList);
        rvTasks.setAdapter(taskAdapter);
    }

    private void loadTasks() {
        taskList = taskDao.getTasksByCategory("ASSIGNMENT"); // Hoặc lấy toàn bộ tùy DAO của m
        if (taskAdapter != null) {
            taskAdapter.updateData(taskList);
        }
    }
}