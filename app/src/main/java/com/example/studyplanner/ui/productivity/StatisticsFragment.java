package com.example.studyplanner.ui.productivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.example.studyplanner.R;
import com.example.studyplanner.data.dao.StudyLogDao;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsFragment extends BottomSheetDialogFragment {
    private TextView tvTodayTotal;
    private BarChart barChart;
    private StudyLogDao studyLogDao;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        tvTodayTotal = view.findViewById(R.id.tvTodayTotal);
        barChart = view.findViewById(R.id.barChart);

        studyLogDao = new StudyLogDao(requireContext());

        loadStatisticsData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật lại số liệu mỗi khi chuyển sang Tab này
        loadStatisticsData();
    }

    private void loadStatisticsData() {
        // 1. Cập nhật tổng số phút hôm nay
        int todayMins = studyLogDao.getTotalMinutesToday();
        tvTodayTotal.setText(todayMins + " phút");

        // 2. Lấy dữ liệu 7 ngày gần nhất và đẩy lên Biểu đồ
        setupBarChart();
    }

    private void setupBarChart() {
        Map<String, Integer> weeklyData = studyLogDao.getWeeklyStudyMinutes();

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat labelFormat = new SimpleDateFormat("EEE", Locale.getDefault()); // Thứ (Thu, Fri,...)

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -6); // Lùi lại 6 ngày trước

        for (int i = 0; i < 7; i++) {
            String dateStr = dateFormat.format(calendar.getTime());
            String dayLabel = labelFormat.format(calendar.getTime());

            int mins = weeklyData.containsKey(dateStr) ? weeklyData.get(dateStr) : 0;

            entries.add(new BarEntry(i, mins));
            labels.add(dayLabel);

            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Số phút học");
        dataSet.setColor(Color.parseColor("#BA4949")); // Tông màu Đỏ gạch Pomodoro
        dataSet.setValueTextColor(Color.parseColor("#212121"));
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.animateY(1000); // Hiệu ứng cột mọc từ dưới lên

        // Cấu hình Trục X (Đáy)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        // Cấu hình Trục Y
        barChart.getAxisRight().setEnabled(false); // Ẩn trục Y bên phải
        barChart.getAxisLeft().setAxisMinimum(0f);

        barChart.invalidate(); // Refresh biểu đồ
    }
}
