package com.example.studyplanner.notification;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.studyplanner.data.TaskEntity;

public class NotificationScheduler {

    @SuppressLint("ScheduleExactAlarm") // Tắt cảnh báo Lint kiểm tra Exact Alarm
    public static void scheduleTaskReminder(Context context, TaskEntity task, long advanceMinutes) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("taskId", task.id);
        intent.putExtra("title", "GK Nhắc nhở: " + task.title);
        intent.putExtra("message", "Loại: " + task.type + " - Hạn chót/Bắt đầu lúc " + task.dateTimeMillis);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                task.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerAtMillis = task.dateTimeMillis - (advanceMinutes * 60 * 1000);

        if (triggerAtMillis > System.currentTimeMillis()) {
            // 1. Dành cho Android 12 (API 31) trở lên
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                } else {
                    // Nếu không có quyền báo thức chính xác, dùng phương thức thường để ứng dụng không bị Crash
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                }
            }
            // 2. Dành cho Android 6.0 (API 23) đến Android 11
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }
            // 3. Dành cho các phiên bản Android cũ hơn
            else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }
        }
    }
}