package com.example.studyplanner;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.studyplanner.ui.academic.CourseListFragment;
import com.example.studyplanner.ui.calendar.CalendarFragment;
import com.example.studyplanner.ui.productivity.PomodoroFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import android.content.Intent;
import com.example.studyplanner.ui.ocr.OCRScanActivity;
import com.example.studyplanner.ui.sync.SyncSettingsActivity;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Mặc định load CourseListFragment khi vừa mở app
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CourseListFragment())
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_academic);
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_academic) {
                    selectedFragment = new CourseListFragment();
                } else if (itemId == R.id.nav_productivity) {
                    selectedFragment = new PomodoroFragment();
                } else if (itemId == R.id.nav_calendar) {
                    // selectedFragment = new CalendarFragment();
                } else if (itemId == R.id.nav_ocr) {
                    // Mở màn hình quét OCR độc lập
                    Intent intent = new Intent(MainActivity.this, OCRScanActivity.class);
                    startActivity(intent);
                    return false; // Trả về false để không bị đổi highlight tab OCR vĩnh viễn
                } else if (itemId == R.id.nav_sync) {
                    // Mở màn hình quản lý Sync (SyncSettingsActivity)
                    Intent intent = new Intent(MainActivity.this, SyncSettingsActivity.class);
                    startActivity(intent);
                    return false;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true;
                } else if (itemId == R.id.nav_calendar) {
                    selectedFragment = new CalendarFragment();
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }
}