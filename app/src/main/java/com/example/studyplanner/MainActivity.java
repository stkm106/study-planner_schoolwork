package com.example.studyplanner;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studyplanner.ui.calendar.SchedulePlannerActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Chuyển hướng chính xác sang Activity
        Intent intent = new Intent(MainActivity.this, SchedulePlannerActivity.class);
        startActivity(intent);
        finish(); // Đóng MainActivity
    }
}