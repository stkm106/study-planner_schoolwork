package com.example.studyplanner.ui.productivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.studyplanner.R;
import com.example.studyplanner.service.PomodoroService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PomodoroFragment extends Fragment {

    private TextView tvTimer;
    private ProgressBar pomodoroProgressBar;
    private MaterialButton btnStartPause;
    private View btnReset;
    private MaterialButtonToggleGroup toggleGroupMode;
    private View rootView;

    private static final long WORK_TIME_IN_MILLIS = 25 * 60 * 1000; // 25 phút
    private static final long BREAK_TIME_IN_MILLIS = 5 * 60 * 1000;  // 5 phút
    private long selectedTimeInMillis = WORK_TIME_IN_MILLIS;

    private PomodoroService pomodoroService;
    private boolean isBound = false;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PomodoroService.LocalBinder binder = (PomodoroService.LocalBinder) service;
            pomodoroService = binder.getService();
            isBound = true;
            registerServiceListener();
            syncUIWithServiceState();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            pomodoroService = null;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pomodoro, container, false);
        rootView = view;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() != null) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        tvTimer = view.findViewById(R.id.tvTimer);
        pomodoroProgressBar = view.findViewById(R.id.pomodoroProgressBar);
        btnStartPause = view.findViewById(R.id.btnStartPause);
        btnReset = view.findViewById(R.id.btnReset);
        toggleGroupMode = view.findViewById(R.id.toggleGroupMode);
        Spinner spinnerAssignments = view.findViewById(R.id.spinnerAssignments);
        View btnOpenStats = view.findViewById(R.id.btnOpenStats);

        // Nút Start / Pause
        btnStartPause.setOnClickListener(v -> {
            if (!isBound || pomodoroService == null) {
                Toast.makeText(getContext(), "Đang kết nối Service, vui lòng thử lại...", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pomodoroService.isTimerRunning()) {
                pomodoroService.pauseTimer();
                btnStartPause.setText("Tiếp tục");
            } else {
                startPomodoroService();
            }
        });

        // Nút Reset thủ công
        btnReset.setOnClickListener(v -> {
            if (isBound && pomodoroService != null) {
                boolean isWork = (toggleGroupMode.getCheckedButtonId() == R.id.btnWorkMode);
                pomodoroService.resetTimer(selectedTimeInMillis, isWork);
                btnStartPause.setText("Bắt đầu");
                updateUI(selectedTimeInMillis, selectedTimeInMillis);
            }
        });

        // 1. Sửa lại Toggle Group Listener để tránh tự reset khi Fragment vừa bind Service
        toggleGroupMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            // CHỈ xử lý khi nút đó được CHỌN (isChecked = true) VÀ không trong quá trình sync UI
            if (isChecked && isBound && pomodoroService != null) {
                boolean isWorkMode = (checkedId == R.id.btnWorkMode);
                long newDuration = isWorkMode ? WORK_TIME_IN_MILLIS : BREAK_TIME_IN_MILLIS;

                // Chỉ Reset nếu Mode thực sự bị thay đổi so với Service hiện tại
                if (pomodoroService.isWorkMode() != isWorkMode || !pomodoroService.isTimerRunning()) {
                    selectedTimeInMillis = newDuration;
                    applyThemeColor(isWorkMode ? "#BA4949" : "#4C8A96", isWorkMode ? "#BA4949" : "#4C8A96");

                    pomodoroService.resetTimer(selectedTimeInMillis, isWorkMode);
                    btnStartPause.setText("Bắt đầu");
                    updateUI(selectedTimeInMillis, selectedTimeInMillis);
                }
            }
        });

        // Load bài tập từ DB
        List<String> taskTitles = new ArrayList<>();
        taskTitles.add("-- Học tự do (Không chọn bài tập) --");

        try {
            com.example.studyplanner.data.database.DatabaseHelper dbHelper =
                    new com.example.studyplanner.data.database.DatabaseHelper(requireContext());
            android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();

            android.database.Cursor cursor = db.rawQuery("SELECT title FROM tasks WHERE status = 0", null);

            if (cursor.moveToFirst()) {
                do {
                    taskTitles.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                taskTitles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssignments.setAdapter(adapter);

        // Thống kê BottomSheet
        btnOpenStats.setOnClickListener(v -> {
            StatisticsFragment statsModal = new StatisticsFragment();
            statsModal.show(getParentFragmentManager(), "StatisticsBottomSheet");
        });

        return view;
    }

    private void startPomodoroService() {
        if (getContext() == null || pomodoroService == null) return;

        Intent intent = new Intent(getContext(), PomodoroService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent);
        } else {
            requireContext().startService(intent);
        }

        pomodoroService.startTimer(selectedTimeInMillis, new PomodoroService.OnTimerListener() {
            @Override
            public void onTick(long timeLeft, long totalTime) {
                if (isAdded()) {
                    updateUI(timeLeft, totalTime);
                }
            }

            @Override
            public void onFinish() {
                if (isAdded()) {
                    btnStartPause.setText("Bắt đầu");
                    Toast.makeText(getContext(), "Hoàn tất Pomodoro!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnStartPause.setText("Tạm dừng");
    }

    private void registerServiceListener() {
        if (pomodoroService != null) {
            pomodoroService.setOnTimerListener(new PomodoroService.OnTimerListener() {
                @Override
                public void onTick(long timeLeft, long totalTime) {
                    if (isAdded()) {
                        updateUI(timeLeft, totalTime);
                    }
                }

                @Override
                public void onFinish() {
                    if (isAdded()) {
                        btnStartPause.setText("Bắt đầu");
                        Toast.makeText(getContext(), "Hoàn tất Pomodoro!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // ĐỒNG BỘ TRẠNG THÁI HOÀN HẢO KHI MỞ LẠI FRAGMENT (Fix lỗi ô màu trắng)
    // 2. Sửa lại hàm syncUIWithServiceState chuẩn chỉnh
    private void syncUIWithServiceState() {
        if (pomodoroService != null) {
            boolean isWork = pomodoroService.isWorkMode();

            // Cập nhật biến thời gian chuẩn trước
            selectedTimeInMillis = isWork ? WORK_TIME_IN_MILLIS : BREAK_TIME_IN_MILLIS;

            // Đổi màu theme chuẩn
            applyThemeColor(isWork ? "#BA4949" : "#4C8A96", isWork ? "#BA4949" : "#4C8A96");

            // Đồng bộ Toggle Button (tránh trigger lại listener thừa)
            int targetCheckId = isWork ? R.id.btnWorkMode : R.id.btnBreakMode;
            if (toggleGroupMode.getCheckedButtonId() != targetCheckId) {
                toggleGroupMode.check(targetCheckId);
            }

            // Lấy thời gian thực tế đang đếm dở từ Service
            long timeLeft = pomodoroService.getTimeLeftInMillis();
            long totalTime = pomodoroService.getTotalTimeInMillis();

            updateUI(timeLeft, totalTime);
            btnStartPause.setText(pomodoroService.isTimerRunning() ? "Tạm dừng" : "Bắt đầu");
        }
    }

    private void updateUI(long timeLeft, long totalTime) {
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;
        tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));

        int progress = (int) (((double) timeLeft / totalTime) * 1000);
        pomodoroProgressBar.setMax(1000);
        pomodoroProgressBar.setProgress(progress);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getContext() != null) {
            Intent intent = new Intent(getContext(), PomodoroService.class);
            getContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isBound) {
            if (pomodoroService != null) {
                pomodoroService.setOnTimerListener(null);
            }
            if (getContext() != null) {
                getContext().unbindService(connection);
            }
            isBound = false;
        }
    }

    private void applyThemeColor(String mainHex, String textHex) {
        int mainColor = android.graphics.Color.parseColor(mainHex);
        int textColor = android.graphics.Color.parseColor(textHex);

        if (rootView != null) rootView.setBackgroundColor(mainColor);
        if (tvTimer != null) tvTimer.setTextColor(textColor);
        if (btnStartPause != null) btnStartPause.setTextColor(textColor);
        if (btnReset != null && btnReset.getBackground() != null) {
            btnReset.getBackground().setTint(mainColor);
        }
    }
}