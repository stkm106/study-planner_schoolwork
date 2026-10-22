package com.example.studyplanner.ui.ocr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studyplanner.R;
import com.example.studyplanner.utils.FirebaseSyncHelper;
import com.example.studyplanner.utils.OCRHelper;

public class OCRScanActivity extends AppCompatActivity {
    private ImageView imageViewPreview;
    private TextView tvExtractedResult;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageViewPreview.setImageURI(uri);
                    processOCR(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_scan);

        imageViewPreview = findViewById(R.id.imageViewPreview);
        tvExtractedResult = findViewById(R.id.tvExtractedResult);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        Button btnUseText = findViewById(R.id.btnUseText);

        btnSelectImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnUseText.setOnClickListener(v -> {
            String text = tvExtractedResult.getText().toString().trim();

            if (!text.isEmpty() && !text.equals("Chưa có văn bản nào được quét...")) {
                try {
                    // Gọi hàm đồng bộ toàn bộ dữ liệu SQLite cục bộ lên Firebase Firestore
                    // Sử dụng ID mặc định "user_demo" (hoặc ID tài khoản thực tế của nhóm)
                    FirebaseSyncHelper.syncLocalToCloud("user_demo", OCRScanActivity.this);
                    Toast.makeText(OCRScanActivity.this, "Đã lưu & đồng bộ dữ liệu lên Firebase!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(OCRScanActivity.this, "Lỗi đồng bộ Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(OCRScanActivity.this, "Chưa có văn bản nào để sử dụng!", Toast.LENGTH_SHORT).show();
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("EXTRACTED_TEXT", text);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }

    private void processOCR(Uri uri) {
        Toast.makeText(this, "Đang quét văn bản...", Toast.LENGTH_SHORT).show();
        OCRHelper.recognizeText(this, uri, new OCRHelper.OCRCallback() {
            @Override
            public void onSuccess(String extractedText) {
                tvExtractedResult.setText(extractedText);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(OCRScanActivity.this, "Lỗi quét: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}