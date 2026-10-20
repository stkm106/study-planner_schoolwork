package com.example.studyplanner.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.studyplanner.R;
import com.example.studyplanner.data.dao.TaskDao;

public class CalendarFragment extends Fragment {
    private TaskDao taskDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        taskDao = new TaskDao(requireContext());
        taskDao.open();

        return inflater.inflate(R.layout.activity_calendar_planner, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (taskDao != null) {
            taskDao.close();
        }
    }
}