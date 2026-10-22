package com.example.studyplanner.ui.sync;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studyplanner.R;
import com.example.studyplanner.utils.FirebaseSyncHelper;

public class SyncSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_settings);

        Button btnSyncNow = findViewById(R.id.btnSyncNow);
        String userId = "user_nhom_17"; // Có thể thay bằng ID tài khoản thật sau

        btnSyncNow.setOnClickListener(v -> {
            FirebaseSyncHelper.syncLocalToCloud(userId, this);
            Toast.makeText(this, "Đồng bộ lên đám mây thành công!", Toast.LENGTH_SHORT).show();
        });
    }
}