package com.example.studyplanner.notification;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.studyplanner.R;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "GK_REMINDER_CHANNEL";

    @Override
    @SuppressLint("MissingPermission") // Bỏ qua cảnh báo Lint của Android Studio tại hàm notify()
    public void onReceive(Context context, Intent intent) {
        // 1. Kiểm tra xem người dùng đã bật quyền thông báo cho App chưa
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (!notificationManagerCompat.areNotificationsEnabled()) {
            return;
        }

        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        int notificationId = intent.getIntExtra("taskId", 0);

        // 2. Tạo NotificationChannel cho Android 8.0 (API 26) trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "GK - Lịch học & Kỳ thi",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // 3. Xây dựng nội dung thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title != null ? title : "GK Nhắc Nhở")
                .setContentText(message != null ? message : "Bạn có lịch học / lịch thi sắp diễn ra!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // 4. Hiển thị thông báo bằng NotificationManagerCompat
        notificationManagerCompat.notify(notificationId, builder.build());
    }
}