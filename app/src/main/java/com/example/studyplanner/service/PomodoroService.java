package com.example.studyplanner.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.studyplanner.MainActivity;

import java.util.Locale;

public class PomodoroService extends Service {

    public static final String CHANNEL_ID = "PomodoroServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    private final IBinder binder = new LocalBinder();
    private CountDownTimer countDownTimer;

    private boolean isTimerRunning = false;
    private boolean isWorkMode = true; // Mặc định true = Work Mode (25m), false = Break Mode (5m)

    private static final long WORK_TIME_IN_MILLIS = 25 * 60 * 1000;
    private static final long BREAK_TIME_IN_MILLIS = 5 * 60 * 1000;

    private long timeLeftInMillis = WORK_TIME_IN_MILLIS;
    private long totalTimeInMillis = WORK_TIME_IN_MILLIS;
    private OnTimerListener timerListener;

    public void setOnTimerListener(OnTimerListener listener) {
        this.timerListener = listener;
    }

    public class LocalBinder extends Binder {
        public PomodoroService getService() {
            return PomodoroService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, buildNotification("00:00"));
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void startTimer(long durationMillis, OnTimerListener listener) {
        this.totalTimeInMillis = durationMillis;
        this.timerListener = listener;

        if (timeLeftInMillis <= 0 || timeLeftInMillis > totalTimeInMillis) {
            timeLeftInMillis = totalTimeInMillis;
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateNotification();
                if (timerListener != null) {
                    timerListener.onTick(timeLeftInMillis, totalTimeInMillis);
                }
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                int durationMins = (int) (totalTimeInMillis / (60 * 1000));

                if (durationMins > 0) {
                    String currentTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());
                    com.example.studyplanner.data.model.StudyLog log = new com.example.studyplanner.data.model.StudyLog(
                            null,
                            "POMODORO",
                            durationMins,
                            currentTime
                    );
                    com.example.studyplanner.data.dao.StudyLogDao dao = new com.example.studyplanner.data.dao.StudyLogDao(getApplicationContext());
                    dao.insertLog(log);
                }

                updateNotificationText(isWorkMode ? "Đã hết giờ học tập!" : "Đã hết giờ nghỉ ngơi!");
                if (timerListener != null) {
                    timerListener.onFinish();
                }
            }
        }.start();

        isTimerRunning = true;
    }

    public void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        updateNotificationText("Đã tạm dừng");
    }

    public void resetTimer(long newDurationMillis, boolean isWorkMode) {
        pauseTimer();
        this.isWorkMode = isWorkMode;
        this.totalTimeInMillis = newDurationMillis;
        this.timeLeftInMillis = newDurationMillis;
        updateNotification();
    }

    private void updateNotification() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeStr = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, buildNotification("Thời gian còn lại: " + timeStr));
        }
    }

    private void updateNotificationText(String text) {
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, buildNotification(text));
        }
    }

    private Notification buildNotification(String contentText) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Pomodoro Timer")
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pomodoro Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public boolean isWorkMode() {
        return isWorkMode;
    }

    public long getTimeLeftInMillis() {
        return timeLeftInMillis;
    }

    public long getTotalTimeInMillis() {
        return totalTimeInMillis;
    }

    public interface OnTimerListener {
        void onTick(long timeLeft, long totalTime);
        void onFinish();
    }
}