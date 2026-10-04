package com.example.studyplanner.ui.productivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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

import java.util.Locale;

public class PomodoroFragment extends Fragment {

    private TextView tvTimer;
    private ProgressBar pomodoroProgressBar;
    private MaterialButton btnStartPause;
    private View btnReset;
    private MaterialButtonToggleGroup toggleGroupMode;

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
            registerServiceListener();
            updateUIFromService();
            isBound = true;
            updateUIFromService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pomodoro, container, false);

        // Xin quyền thông báo cho Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }

        tvTimer = view.findViewById(R.id.tvTimer);
        pomodoroProgressBar = view.findViewById(R.id.pomodoroProgressBar);
        btnStartPause = view.findViewById(R.id.btnStartPause);
        btnReset = view.findViewById(R.id.btnReset);
        toggleGroupMode = view.findViewById(R.id.toggleGroupMode);

        // Start & Pause
        btnStartPause.setOnClickListener(v -> {
            if (!isBound) return;
            if (pomodoroService.isTimerRunning()) {
                pomodoroService.pauseTimer();
                btnStartPause.setText("Tiếp tục");
            } else {
                startPomodoroService();
            }
        });

        // Reset
        btnReset.setOnClickListener(v -> {
            if (isBound) {
                pomodoroService.resetTimer(selectedTimeInMillis);
                btnStartPause.setText("Bắt đầu");
                updateUI(selectedTimeInMillis, selectedTimeInMillis);
            }
        });

        toggleGroupMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnWorkMode) {
                    selectedTimeInMillis = WORK_TIME_IN_MILLIS;
                } else if (checkedId == R.id.btnBreakMode) {
                    selectedTimeInMillis = BREAK_TIME_IN_MILLIS;
                }
                if (isBound) {
                    pomodoroService.resetTimer(selectedTimeInMillis);
                    btnStartPause.setText("Bắt đầu");
                    updateUI(selectedTimeInMillis, selectedTimeInMillis);
                }
            }
        });

        return view;
    }

    private void startPomodoroService() {
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
                    Toast.makeText(getContext(), "Hoàn tất đếm ngược Pomodoro!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Hoàn tất đếm ngược Pomodoro!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updateUIFromService() {
        if (pomodoroService != null) {
            long timeLeft = pomodoroService.getTimeLeftInMillis();
            updateUI(timeLeft, selectedTimeInMillis);
            btnStartPause.setText(pomodoroService.isTimerRunning() ? "Tạm dừng" : "Bắt đầu");
        }
    }

    // Progress bar
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
        Intent intent = new Intent(getContext(), PomodoroService.class);
        requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isBound) {
            if (pomodoroService != null) {
                pomodoroService.setOnTimerListener(null);
            }
            requireContext().unbindService(connection);
            isBound = false;
        }
    }
}